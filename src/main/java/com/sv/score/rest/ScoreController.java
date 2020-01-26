package com.sv.score.rest;


import com.sv.score.dto.ResponseDto;
import com.sv.score.dto.WordScoreDto;
import com.sv.score.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/estimate")
public class ScoreController {

    @Autowired
    private ScoreService scoreService;

    @GetMapping(value = "")
   // @PostMapping(value = "")
    public ResponseEntity<ResponseDto<WordScoreDto>> submitNewProposal(@RequestParam String keyWord) {

        //ResponseDto<WordScoreDto> response = scoreService.calculateScore(keyWord);
        ResponseDto<WordScoreDto> response = scoreService.computeScore(keyWord);

        return new ResponseEntity<>(response, new HttpHeaders(), response.getStatus());
    }
}
