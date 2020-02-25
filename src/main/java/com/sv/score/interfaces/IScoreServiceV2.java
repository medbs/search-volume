package com.sv.score.interfaces;

import com.sv.score.dto.ResponseDto;
import com.sv.score.dto.WordScoreDto;

import java.util.Map;

public interface IScoreServiceV2 {


    ResponseDto<WordScoreDto> computeScore(String keyWord);
}
