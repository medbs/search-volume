package com.sv.score.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class WordSuggestionV2Dto implements Serializable {

    private static final long serialVersionUID = 4206950658426484275L;

    List<HashMap<String, Integer>> suggestedWords;
    String word;
    Integer rank;

    public List<HashMap<String, Integer>> getSuggestedWords() {
        return suggestedWords;
    }

    public void setSuggestedWords(List<HashMap<String, Integer>> suggestedWords) {
        this.suggestedWords = suggestedWords;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }
}
