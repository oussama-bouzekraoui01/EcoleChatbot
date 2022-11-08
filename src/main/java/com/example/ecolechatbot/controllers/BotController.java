package com.example.ecolechatbot.controllers;

import com.example.ecolechatbot.models.Message;
import com.example.ecolechatbot.models.Langue;
import com.example.ecolechatbot.models.Response;
import com.example.ecolechatbot.services.EnglishService;
import com.example.ecolechatbot.services.FrenchService;
import com.example.ecolechatbot.services.LanguageService;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.langdetect.Language;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
public class BotController {

    @Autowired
    private EnglishService englishService;

    @Autowired
    private FrenchService frenchService;

    @Autowired
    private LanguageService languageService;

    @GetMapping(value = "/chat")
    public Response chat(@RequestParam(name = "message") String input) throws FileNotFoundException, IOException
    {
        Langue langue = languageService.getLanguage(input);


        Map<String, String> questionAnswer = englishService.initializeDataEn();
        Map<String, String> questionAnswerFr = frenchService.initializeDataFr();


        LocalDateTime  dateTime = LocalDateTime.now();
        Response response = new Response();
        response.setMessage("");

        if(langue.getLangue().equals("English")) {
            DoccatModel model = englishService.trainCategorizerModelEn();
            String[] sentences = englishService.decomposerPhrases(input);
            for (String sentence : sentences) {
                String[] tokens = englishService.decomposeMot(sentence);
                String category = englishService.detectCategory(model, tokens);
                response.setMessage(response.getMessage() + questionAnswer.get(category));
                if(sentences.length > 1) {
                    response.setMessage(response.getMessage() + ", ");
                }
            }
        }

        if(langue.getLangue().equals("French")) {
            DoccatModel model = frenchService.trainCategorizerModelFr();
            String[] sentences = frenchService.decomposerPhrases(input);
            for (String sentence : sentences) {
                String[] tokens = frenchService.decomposeMot(sentence);
                String category = frenchService.detectCategory(model, tokens);
                response.setMessage(response.getMessage() + questionAnswerFr.get(category));
                if(sentences.length > 1) {
                    response.setMessage(response.getMessage() + ", ");
                }
            }
        }


        response.setDateTime(dateTime);
        return response;
    }

    @PostMapping(path = "/getLangues")
    public Language[] getLanguages(@RequestBody Message language) throws IOException {
        Language[] languages = this.languageService.getLanguages(language.getMessage());
        return languages;
    }

    @PostMapping(path = "/getLangue")
    public Langue getLanguage(@RequestBody Message language) throws IOException {
        Langue lang = this.languageService.getLanguage(language.getMessage());
        return lang;
    }

    @PostMapping(path = "/getPhrases")
    public String[] getPhrases(@RequestBody Message langue) throws IOException {
        return englishService.decomposerPhrases(langue.getMessage());
    }

    @PostMapping(path = "/getMots")
    public String[] getMots(@RequestBody Message langue) throws IOException {
        return englishService.decomposeMot(langue.getMessage());
    }

    @PostMapping(path = "/categorie")
    public String getCategorie(@RequestBody Message categorie) throws IOException {
        DoccatModel model = englishService.trainCategorizerModelEn();
        String[] token = englishService.decomposeMot(categorie.getMessage());
        return englishService.detectCategory(model, token);
    }

}
