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
import org.springframework.core.convert.Property;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


@Service
public class EnglishService {

    @Autowired
    LanguageService languageService;

    Map<String, String> questionAnswer = new HashMap<>();


    public Map<String, String> initializeDataEn(){
        questionAnswer.put("unknown", "Sorry, I didn't understand your question");

        questionAnswer.put("greeting", "Hello, how can I help you?");

        questionAnswer.put("costs","2100dh per year for 1st and 2nd year student and 1900dh for 3rd year student");

        return questionAnswer;
    }


    public String[] decomposerPhrases(String data) throws FileNotFoundException, IOException {
        return languageService.decomposerPhrases(data, "en-sent.bin");
    }

    public String[] decomposeMot(String sentence) throws FileNotFoundException, IOException {
        return languageService.decomposeMot(sentence, "en-token.bin");
    }

    public DoccatModel trainCategorizerModelEn() throws FileNotFoundException, IOException {
        return languageService.trainCategorizerModel("en-categories.txt", "en");
    }


    public String detectCategory(DoccatModel model, String[] finalTokens) throws IOException {
        return languageService.detectCategory(model, finalTokens);
    }
}