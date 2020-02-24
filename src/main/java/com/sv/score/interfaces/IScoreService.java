package com.sv.score.interfaces;

import com.sv.score.dto.ResponseDto;
import com.sv.score.dto.WordScoreDto;

public interface IScoreService {

    ResponseDto<WordScoreDto> computeScore(String keyWord);

}
