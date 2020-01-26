package com.sv.score.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WordSuggestionsDto implements Serializable {


    private static final long serialVersionUID = 2767709159000113183L;
    private String keyWord;
    private List<String> suggestions = new ArrayList<>();

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }
}
