package com.github.wslf.naivebayes;

import java.util.List;
import com.github.wslf.datastructures.pair.Pair;

/**
 *
 * @author Wsl_F
 */
public interface NaiveBayes {

    String classify(String text);

    void learn(List<Pair<String, String>> samples);
}
