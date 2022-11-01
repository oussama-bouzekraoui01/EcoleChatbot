package com.example.ecolechatbot.services;

import com.example.ecolechatbot.models.Langue;
import opennlp.tools.langdetect.Language;
import opennlp.tools.langdetect.LanguageDetector;
import opennlp.tools.langdetect.LanguageDetectorME;
import opennlp.tools.langdetect.LanguageDetectorModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.HashMap;

@Service
public class LanguageService {

    @Autowired
    private EnglishService englishService;

    public Language[] getLanguages(String sentence) throws FileNotFoundException, IOException
    {
        InputStream modelIn = new FileInputStream("langdetect-183.bin");
        LanguageDetectorModel trainedModel = new LanguageDetectorModel(modelIn);
        // load the model
        LanguageDetector languageDetector = new LanguageDetectorME(trainedModel);
        Language[] languages = languageDetector.predictLanguages(sentence);
        return languages;
    }

    public Langue getLanguage(String sentence) throws FileNotFoundException, IOException
    {
        Langue language = englishService.detectLang(sentence);
        language.setPourcentage(language.getPourcentage()*100);
        return language;
    }

    public String getLanguageByCode(String langCode) throws IOException {

        HashMap<String, String> langMap = readToHashmap();

        return langMap.get(langCode);
    }

    public HashMap<String, String> readToHashmap() throws IOException {
        HashMap<String, String> map = new HashMap<String, String>();
        BufferedReader in = new BufferedReader(new FileReader("language_mapping.txt"));
        String line = "";

        while ((line = in.readLine()) != null) {
            String parts[] = line.split("\t");
            map.put(parts[0], parts[1]);
        }
        in.close();

        return map;
    }
}

