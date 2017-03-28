package com.github.wslf.naivebayes;

import com.github.wslf.datastructures.pair.Pair;
import com.github.wslf.naivebayes.exams.ExamClassifier;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Wsl_F
 */
public class TestF1 {

    private NaiveBayes bayes;
    private double betta;

    public TestF1(NaiveBayes bayes, double betta) {
        this.bayes = bayes;
        this.betta = betta;
    }

    public double getScore(String trainPath, String testPath) {
        ArrayList<Pair<String, String>> samples = readSamples(trainPath);

        bayes.learn(samples);

        ArrayList<Pair<String, String>> tests = readSamples(testPath);
        HashMap<String, Integer> matches = new HashMap<>();
        HashMap<String, Integer> real = new HashMap<>();
        HashMap<String, Integer> prediction = new HashMap<>();

        for (Pair<String, String> test : tests) {
            String predicted = bayes.classify(test.getFirst());
            String realClass = test.getSecond();
            if (!real.containsKey(realClass)) {
                matches.put(realClass, 0);
                real.put(realClass, 0);
            }
            real.put(realClass, real.get(realClass) + 1);

            if (!prediction.containsKey(predicted)) {
                prediction.put(predicted, 0);
            }
            prediction.put(predicted, prediction.get(predicted) + 1);

            if (predicted.equals(test.getSecond())) {
                matches.put(predicted, matches.get(predicted) + 1);
            }
        }

        return calckF1(matches, real, prediction);
    }

    private double calckF1(HashMap<String, Integer> matches,
            HashMap<String, Integer> real,
            HashMap<String, Integer> prediction) {

        double res = 0.0;
        int n = 0;
        for (String className : real.keySet()) {
            n++;
            double p = matches.get(className);
            p /= prediction.get(className);

            double r = matches.get(className);
            r /= real.get(className);

            double t = (1 + betta * betta);
            t *= p * r;
            t /= (betta * betta * p + r);

            res += t;
        }

        res /= n;

        return res;
    }

    private static ArrayList<Pair<String, String>> readSamples(String path) {
        ArrayList<Pair<String, String>> samples = new ArrayList<>();
        try {
            ArrayList<String> lines = new ArrayList<>(Files.readAllLines(Paths.get(path)));
            int i = 0;
            int size = lines.size();
            while (i < size) {
                if (lines.get(i).length() == 0) {
                    i++;
                    continue;
                }

                String className = lines.get(i);
                i++;
                while (i < size && lines.get(i).length() == 0) {
                    i++;
                }
                int n = Integer.parseInt(lines.get(i));
                i++;

                for (int j = 0; j < n && i < size; j++) {
                    while (i < size && lines.get(i).length() == 0) {
                        i++;
                    }

                    Pair<String, String> sample = new Pair<>(lines.get(i), className);
                    samples.add(sample);
                    i++;
                }

            }
        } catch (IOException ex) {
            System.err.println("Couldn't read file: " + path
                    + "\n" + ex.getMessage());
        }

        return samples;
    }

}
