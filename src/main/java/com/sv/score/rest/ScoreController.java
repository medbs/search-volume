package com.sv.score.rest;


import com.sv.score.dto.ResponseDto;
import com.sv.score.dto.WordScoreDto;
import com.sv.score.service.ComputeScoreService;
import com.sv.score.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/v1/estimate")
public class ScoreController {

    @Autowired
    private ScoreService scoreService;

    @Autowired
    private ComputeScoreService computeScoreService;

    @GetMapping(value = "")
    public ResponseEntity<ResponseDto<WordScoreDto>> computeScore(@RequestParam String keyWord) {

        ResponseDto<WordScoreDto> response = scoreService.computeScore(keyWord);

        return new ResponseEntity<>(response, new HttpHeaders(), response.getStatus());
    }

    @GetMapping(value = "/v2")
    public ResponseEntity<ResponseDto<Map<String, Integer>>> computeScoreV2(@RequestParam String keyWord) {

        ResponseDto<Map<String, Integer>> response = computeScoreService.computeScoreV2(keyWord);

        return new ResponseEntity<>(response, new HttpHeaders(), response.getStatus());
    }
}
