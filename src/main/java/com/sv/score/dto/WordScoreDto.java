package com.sv.score.dto;

import java.io.Serializable;

public class WordScoreDto implements Serializable {

    private static final long serialVersionUID = 4073797816654019357L;

    private String keyWord;
    private float score;

    public WordScoreDto() {
    }

    public WordScoreDto(String keyWord, float score) {
        this.keyWord = keyWord;
        this.score = score;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public float getScore() {
        return score;
    }

    public void setScore(Long score) {
        this.score = score;
    }
}
