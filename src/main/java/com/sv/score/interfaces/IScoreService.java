package com.sv.score.interfaces;

import com.sv.score.dto.ResponseDto;
import com.sv.score.dto.WordScoreDto;
import com.sv.score.dto.WordSuggestionsDto;

public interface IScoreService {


    //ResponseDto<WordScoreDto> calculateScore(String word);
    ResponseDto<WordScoreDto> computeScore(String keyWord);
}
