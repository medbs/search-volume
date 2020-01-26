package com.sv.score.dto;

import java.io.Serializable;

public class WordScoreDto implements Serializable {

    private static final long serialVersionUID = 4073797816654019357L;

    private String keyWord;
    private Integer score;

    public WordScoreDto() {
    }

    public WordScoreDto(String keyWord, Integer score) {
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

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}
