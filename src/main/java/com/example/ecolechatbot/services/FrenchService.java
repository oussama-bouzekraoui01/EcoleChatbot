package com.example.ecolechatbot.services;

import com.example.ecolechatbot.models.Langue;
import opennlp.tools.doccat.*;
import opennlp.tools.langdetect.Language;
import opennlp.tools.langdetect.LanguageDetector;
import opennlp.tools.langdetect.LanguageDetectorME;
import opennlp.tools.langdetect.LanguageDetectorModel;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.*;
import opennlp.tools.util.model.ModelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class FrenchService {

    @Autowired
    LanguageService languageService;

    Map<String, String> questionAnswer = new HashMap<>();


    public Map<String, String> initializeDataFr(){
        questionAnswer.put("unknown", "Pardon, j'ai pas compris ta question");

        questionAnswer.put("greeting", "Salut, comment je peux vous aider?");

        questionAnswer.put("frais","2100dh pour la première et la deuxième année et 1900dh pour la troisième année");

        questionAnswer.put("filière", "1-filière Génie Informatique \n 2-filière Génie Indus \n 3-filière Génie MIS" );

        return questionAnswer;
    }



    public String[] decomposerPhrases(String data) throws FileNotFoundException, IOException {
        return languageService.decomposerPhrases(data, "fr-sent.bin");
    }

    public String[] decomposeMot(String sentence) throws FileNotFoundException, IOException {

        return languageService.decomposeMot(sentence, "fr-token.bin");
    }

    public DoccatModel trainCategorizerModelFr() throws FileNotFoundException, IOException {
        return languageService.trainCategorizerModel("fr-categories.txt", "en");
    }


    public String detectCategory(DoccatModel model, String[] finalTokens) throws IOException {
        return languageService.detectCategory(model, finalTokens);
    }
}
