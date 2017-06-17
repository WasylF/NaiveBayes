package com.github.wslf.naivebayes.exams;

import com.github.wslf.naivebayes.TestF;

/**
 *
 * @author Wsl_F
 */
public class Executor {

    //static String trainPath = "resources/full/train-full.txt";
    //static String testPath = "resources/full/test-full.txt";  
    static String trainPath = "resources/test.txt";
    static String testPath = "resources/train.txt";

    public static void main(String[] args) {
        TestF testF1 = new TestF(new ExamClassifier(), 1);
        double score = testF1.getScore(trainPath, testPath);
        System.err.println("Total F1 score: " + score);
    }


}
