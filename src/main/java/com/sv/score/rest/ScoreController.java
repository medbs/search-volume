package com.sv.score.rest;


import com.sv.score.dto.ResponseDto;
import com.sv.score.dto.WordScoreDto;
import com.sv.score.service.ComputeScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/v1/estimate")
public class ScoreController {

    @Autowired
    private ComputeScoreService computeScoreService;


    @GetMapping(value = "")
    public ResponseEntity<ResponseDto<WordScoreDto>> computeScore(@RequestParam String keyWord) {

        ResponseDto<WordScoreDto> response = computeScoreService.computeScore(keyWord);

        return new ResponseEntity<>(response, new HttpHeaders(), response.getStatus());
    }
}
