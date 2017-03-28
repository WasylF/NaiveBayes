package com.github.wslf.naivebayes.exams;

import com.github.wslf.naivebayes.TestF1;

/**
 *
 * @author Wsl_F
 */
public class Executor {

    static String trainPath = "resources/train.txt";
    static String testPath = "resources/test.txt";

    public static void main(String[] args) {
        TestF1 testF1 = new TestF1(new ExamClassifier(), 1);
        double score = testF1.getScore(trainPath, testPath);
        System.err.println("F1 score: " + score);
    }


}
