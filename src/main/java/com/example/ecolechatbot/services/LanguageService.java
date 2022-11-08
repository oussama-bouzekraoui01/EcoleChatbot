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

@Service
public class LanguageService {

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
        Langue language = new Langue();
        try (InputStream modelIn = new FileInputStream("langdetect-183.bin")) {
            LanguageDetectorModel trainedModel = new LanguageDetectorModel(modelIn);
            // load the model
            LanguageDetector languageDetector = new LanguageDetectorME(trainedModel);
            Language[] languages = languageDetector.predictLanguages(sentence);
            for (Language lang : languages) {
                if (getLanguageByCode(lang.getLang()).equals("French") || getLanguageByCode(lang.getLang()).equals("English")) {
                    language.setLangue(getLanguageByCode(lang.getLang()));
                    language.setPourcentage(lang.getConfidence());
                    break;
                }
            }
            return language;
        }

    }

    public String[] decomposerPhrases(String data, String file) throws FileNotFoundException, IOException {

        try (InputStream is = new FileInputStream(file)) {
            SentenceModel model = new SentenceModel(is);
            SentenceDetectorME detector = new SentenceDetectorME(model);
            String[] sentences = detector.sentDetect(data);
			/*SentenceDetectorEvaluator sentenceDetectorEvaluator= new SentenceDetectorEvaluator(detector, null);
			System.out.println("Evaluation:"+sentenceDetectorEvaluator.getFMeasure());*/
            // System.out.println("Sentence Detection: " + Arrays.stream(sentences).collect(Collectors.joining(" | ")));
            return sentences;
        }
    }

    public String[] decomposeMot(String sentence, String file) throws FileNotFoundException, IOException {

        try (InputStream modelInputStream = new FileInputStream(file)) {

            TokenizerModel model = new TokenizerModel(modelInputStream);
            TokenizerME tokenizer = new TokenizerME(model);

            String[] tokens = tokenizer.tokenize(sentence);
            return tokens;
        }
    }

    public DoccatModel trainCategorizerModel(String file, String  languageCode) throws FileNotFoundException, IOException {
        InputStreamFactory inputStreamFactory = new MarkableFileInputStreamFactory(new File(file));

        ObjectStream<String> lineStream = new PlainTextByLineStream(inputStreamFactory, StandardCharsets.UTF_8);
        ObjectStream<DocumentSample> sampleStream = new DocumentSampleStream(lineStream);

        DoccatFactory factory = new DoccatFactory(new FeatureGenerator[] { new BagOfWordsFeatureGenerator() });

        TrainingParameters params = ModelUtil.createDefaultTrainingParameters();
        params.put(TrainingParameters.CUTOFF_PARAM, 0);

        DoccatModel model = DocumentCategorizerME.train(languageCode, sampleStream, params, factory);
        return model;
    }

    public String detectCategory(DoccatModel model, String[] finalTokens) throws IOException {
        DocumentCategorizerME myCategorizer = new DocumentCategorizerME(model);
        double[] probabilitiesOfOutcomes = myCategorizer.categorize(finalTokens);
        System.out.println(myCategorizer.getAllResults(probabilitiesOfOutcomes));
        String category = myCategorizer.getBestCategory(probabilitiesOfOutcomes);
        return category;
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

