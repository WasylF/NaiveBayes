package com.github.wslf.naivebayes.exams;

import com.github.wslf.datastructures.pair.Pair;
import com.github.wslf.levenshteindistance.Calculator;
import com.github.wslf.naivebayes.NaiveBayes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author Wsl_F
 */
public class ExamClassifier implements NaiveBayes {

    Map<String, Integer> classes = new HashMap<>();
    int totalSamples;
    /**
     * key - classname, value - Set<feature>
     */
    Map<String, TreeSet<Feature>> features = new HashMap<>();

    @Override
    public String classify(String text) {
        double probability = 0.0;
        String bestMatch = null;
        
        for (String className : classes.keySet()) {
            double classProb = classes.get(className);
            double belongClass = getProbability(text, className);
            
            double p = classProb * belongClass;
            if (p > probability) {
                bestMatch = className;
                probability = p;
            }
        }
        
        return bestMatch;
    }

    private double getProbability(String text, String className) {
        double probability = 1.0;
        Calculator calculator = new Calculator();
        TreeSet<Feature> set = features.get(className);

        ArrayList<Feature> textFeatures = parseFeatures(text);
        for (Feature textFeature : textFeatures) {
            double fProb = 0.0;
            Feature bestMatch = null;
            for (Feature feature : set) {
                double d = calculator.getDistanceIgnoreCase(textFeature.getText(), feature.getText());
                d = 1 - d;
                if (d > fProb) {
                    fProb = d;
                    bestMatch = feature;
                }
            }
            
            if (bestMatch != null) {
                fProb = Math.pow(fProb, 1.0 / bestMatch.getImportance());
                probability *= fProb;
            } else {
                probability *= (1.0 / set.size());
            }
        }
        
        return probability;
    }

    /**
     *
     * @param samples List {feature, className}
     */
    @Override
    public void learn(List<Pair<String, String>> samples) {
        totalSamples = samples.size();
        
        for (Pair<String, String> sample : samples) {
            String className = sample.getSecond();
            learnClassName(className);

            String text = sample.getFirst();
            learnText(text, className);
        }
    }

    private void learnText(String text, String className) {
        ArrayList<Feature> features_ = parseFeatures(text);
        if (!features.containsKey(className)) {
            features.put(className, new TreeSet<>());
        }
        TreeSet<Feature> set = features.get(className);

        for (Feature newFeature : features_) {

            int realImportance = newFeature.getImportance();
            newFeature.setImportance(-1);
            Feature higher = set.higher(newFeature);
            newFeature.setImportance(realImportance);

            if (higher != null && higher.equalsText(newFeature)) {
                higher.setImportance(higher.getImportance() + realImportance);
            } else {
                set.add(newFeature);
            }
        }
    }

    private void learnClassName(String className) {
        if (!classes.containsKey(className)) {
            classes.put(className, 0);
        }

        int freq = classes.get(className);
        classes.put(className, freq + 1);
    }

    private ArrayList<Feature> parseFeatures(String text) {
        String[] parts = text.split("\\.|\\,|\\!|\\?|\\;| ");
        ArrayList<Feature> features = new ArrayList<>(parts.length);
        for (String part : parts) {
            Feature feature = new Feature(part);
            if (feature.getImportance() > 0) {
                features.add(feature);
            }
        }

        return features;
    }
    
    public int getClassPower(String className) {
        if (!classes.containsKey(className)) {
            return 0;
        } else {
            return classes.get(className);
        }
    }
}
