package com.github.wslf.naivebayes;

/**
 *
 * @author Wsl_F
 */
public class Feature extends com.github.wslf.datastructures.pair.Pair<String, Integer> {

    public Feature() {
        super();
    }

    public Feature(String text, int importance) {
        super(text, importance);
    }

    public void setText(String className) {
        setFirst(className);
    }

    public String getText() {
        return getFirst();
    }

    public void setImportance(int importance) {
        setSecond(importance);
    }

    public int getImportance() {
        Integer importance = getSecond();
        return importance == null ? 0 : importance;
    }

    public boolean equalsText(Feature f2) {
        return f2 != null && f2.getText() != null && f2.getText().equals(getText());
    }
}
