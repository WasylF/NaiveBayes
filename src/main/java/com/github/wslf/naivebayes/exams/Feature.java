package com.github.wslf.naivebayes.exams;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

/**
 *
 * @author Wsl_F
 */
public class Feature extends com.github.wslf.naivebayes.Feature implements Comparable<Feature> {

    private static final Set<String> USELESS_WORDS;
 
    static {
        USELESS_WORDS = new HashSet<>();
        try (Stream<String> stream = Files.lines(Paths.get("resources/uselessWords.txt"))) {

            stream.forEach(USELESS_WORDS::add);

        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }

    }

    
    public Feature(String text) {
        String[] words = text.split(" ");
        StringBuilder feature = new StringBuilder("");
        int num = 0;
        for (String word : words) {
            if (!USELESS_WORDS.contains(word.toLowerCase())) {
                feature.append(word);
                num++;
            }
        }
        setText(feature.toString());

        int importance;
        switch (num) {
            case 0:
                importance = 0;
                break;
            case 1:
                importance = 4;
                break;
            case 2:
                importance = 2;
                break;
            default:
                importance = 1;
        }

        setImportance(importance);
    }

    public Feature(String text, int importance) {
        super(text, importance);
    }

    
    @Override
    public int compareTo(Feature f2) {
        int textCompare = getText().compareTo(f2.getText());
        return textCompare != 0 ? textCompare
                : Integer.compare(getImportance(), f2.getImportance());
    }

}
