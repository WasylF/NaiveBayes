package com.github.wslf.naivebayes;

import com.github.wslf.datastructures.pair.Pair;
import com.github.wslf.naivebayes.exams.ExamClassifier;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

/**
 *
 * @author Wsl_F
 */
public class TestF {

    private NaiveBayes bayes;
    private double betta;

    public TestF(NaiveBayes bayes, double betta) {
        this.bayes = bayes;
        this.betta = betta;
    }

    private ArrayList<String> getClassesNames(ArrayList<Pair<String, String>> tests) {
        TreeSet<String> classes = new TreeSet<>();
        for (Pair<String, String> test : tests) {
            classes.add(test.getSecond());
        }

        return new ArrayList<>(classes);
    }

    private void initScores(HashMap<String, Integer> truePositive,
            HashMap<String, Integer> falsePositive, HashMap<String, Integer> trueNegative,
            HashMap<String, Integer> falseNegative, ArrayList<String> classes) {
        for (String class_ : classes) {
            truePositive.put(class_, 0);
            trueNegative.put(class_, 0);
            falsePositive.put(class_, 0);
            falseNegative.put(class_, 0);
        }
    }

    public double getScore(String trainPath, String testPath) {
        ArrayList<Pair<String, String>> samples = readSamples(trainPath);

        bayes.learn(samples);

        ArrayList<Pair<String, String>> tests = readSamples(testPath);
        HashMap<String, Integer> truePositive, falsePositive, trueNegative, falseNegative;
        truePositive = new HashMap<>();
        falsePositive = new HashMap<>();
        trueNegative = new HashMap<>();
        falseNegative = new HashMap<>();
        ArrayList<String> classes = getClassesNames(tests);

        initScores(truePositive, falsePositive, trueNegative, falseNegative, classes);

        for (Pair<String, String> test : tests) {
            String predicted = bayes.classify(test.getFirst());
            String realClass = test.getSecond();

            if (predicted.equals(realClass)) {
                for (String class_ : classes) {
                    if (class_.equals(realClass)) {
                        truePositive.put(class_, truePositive.get(class_) + 1);
                    } else {
                        trueNegative.put(class_, trueNegative.get(class_) + 1);
                    }
                }
            } else {
                for (String class_ : classes) {
                    if (class_.equals(predicted)) {
                        falsePositive.put(class_, falsePositive.get(class_) + 1);
                    } else {
                        if (class_.equals(realClass)) {
                            falseNegative.put(class_, falseNegative.get(class_) + 1);
                        } else {
                            trueNegative.put(class_, trueNegative.get(class_) + 1);
                        }
                    }
                }
            }

        }

        return calckF(truePositive, falsePositive, trueNegative, falseNegative, classes);
    }

    private double calckF(HashMap<String, Integer> truePositive,
            HashMap<String, Integer> falsePositive, HashMap<String, Integer> trueNegative,
            HashMap<String, Integer> falseNegative, ArrayList<String> classes) {

        double res = 0.0;
        int sum = 0;
        int matches = 0;
        System.out.println("Class\tTP\tFP\tTN\tFN");
        for (String class_ : classes) {
            double precision = truePositive.get(class_)
                    / (double) Math.max(truePositive.get(class_) + falsePositive.get(class_), 1);

            double recall = truePositive.get(class_)
                    / (double) Math.max(truePositive.get(class_) + falseNegative.get(class_), 1);

            double fScore = (1 + betta * betta) * (precision * recall)
                    / Math.max(betta * betta * precision + recall, 0.000001);

            System.out.println(class_
                    + "\t" + truePositive.get(class_)
                    + "\t" + falsePositive.get(class_)
                    + "\t" + trueNegative.get(class_)
                    + "\t" + falseNegative.get(class_));
            System.out.println("F1 score: " + fScore + "\n");

            res += fScore;
            sum += truePositive.get(class_) + falsePositive.get(class_)
                    + trueNegative.get(class_) + falseNegative.get(class_);
            matches += truePositive.get(class_);
        }

        res /= classes.size();

        double accuracy = (double) matches / (((double) sum) / classes.size());
        System.out.println("Accuracy: " + accuracy);

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
