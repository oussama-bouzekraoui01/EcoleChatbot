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

    public Language[] detectLanguages(String sentence) throws IOException {
        InputStream modelIn = new FileInputStream("langdetect-183.bin");
        LanguageDetectorModel trainedModel = new LanguageDetectorModel(modelIn);
        // load the model
        LanguageDetector languageDetector = new LanguageDetectorME(trainedModel);
        Language[] languages = languageDetector.predictLanguages(sentence);
//        System.out.println("Langue : " + languagesServices.getLanguageByCode(languages[0].getLang()));
        return languages;
    }

    public Langue detectLang(String sentence) throws FileNotFoundException, IOException {
        Langue language = new Langue();
        try (InputStream modelIn = new FileInputStream("langdetect-183.bin")) {
            LanguageDetectorModel trainedModel = new LanguageDetectorModel(modelIn);
            // load the model
            LanguageDetector languageDetector = new LanguageDetectorME(trainedModel);
            Language[] languages = languageDetector.predictLanguages(sentence);
            for (Language lang : languages) {
                if (languageService.getLanguageByCode(lang.getLang()).equals("French") || languageService.getLanguageByCode(lang.getLang()).equals("English")) {
                    language.setLangue(languageService.getLanguageByCode(lang.getLang()));
                    language.setPourcentage(lang.getConfidence());
                    break;
                }
            }
            return language;
        }

    }

    public String[] decomposerPhrases(String data) throws FileNotFoundException, IOException {

        try (InputStream is = new FileInputStream("en-sent.bin")) {
            SentenceModel model = new SentenceModel(is);
            SentenceDetectorME detector = new SentenceDetectorME(model);
            String[] sentences = detector.sentDetect(data);
			/*SentenceDetectorEvaluator sentenceDetectorEvaluator= new SentenceDetectorEvaluator(detector, null);
			System.out.println("Evaluation:"+sentenceDetectorEvaluator.getFMeasure());*/
           // System.out.println("Sentence Detection: " + Arrays.stream(sentences).collect(Collectors.joining(" | ")));
            return sentences;
        }
    }

    public String[] decomposeMot(String sentence) throws FileNotFoundException, IOException {

        try (InputStream modelInputStream = new FileInputStream("en-token.bin")) {

            TokenizerModel model = new TokenizerModel(modelInputStream);
            TokenizerME tokenizer = new TokenizerME(model);

            String[] tokens = tokenizer.tokenize(sentence);
            return tokens;
        }
    }

    public DoccatModel trainCategorizerModel() throws FileNotFoundException, IOException {
        InputStreamFactory inputStreamFactory = new MarkableFileInputStreamFactory(new File("en-categories.txt"));

        ObjectStream<String> lineStream = new PlainTextByLineStream(inputStreamFactory, StandardCharsets.UTF_8);
        ObjectStream<DocumentSample> sampleStream = new DocumentSampleStream(lineStream);

        DoccatFactory factory = new DoccatFactory(new FeatureGenerator[] { new BagOfWordsFeatureGenerator() });

        TrainingParameters params = ModelUtil.createDefaultTrainingParameters();
        params.put(TrainingParameters.CUTOFF_PARAM, 0);

        DoccatModel model = DocumentCategorizerME.train("en", sampleStream, params, factory);
        return model;
    }


    public String detectCategory(DoccatModel model, String[] finalTokens) throws IOException {
        DocumentCategorizerME myCategorizer = new DocumentCategorizerME(model);
        double[] probabilitiesOfOutcomes = myCategorizer.categorize(finalTokens);
        System.out.println(myCategorizer.getAllResults(probabilitiesOfOutcomes));
        String category = myCategorizer.getBestCategory(probabilitiesOfOutcomes);
        return category;
    }
}