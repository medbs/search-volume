package com.sv.score.service;


import com.sv.score.dto.ResponseDto;
import com.sv.score.dto.WordScoreDto;
import com.sv.score.dto.WordSuggestionsDto;
import com.sv.score.interfaces.IScoreServiceV2;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;


@Service
public class ComputeScoreService implements IScoreServiceV2 {

    private Logger logger = LoggerFactory.getLogger(ComputeScoreService.class);

    @Value("${amazon.api.url}")
    private String apiUrl;


    /**
     * compute score
     *
     * @param keyWord string
     * @return ResponseDto
     */
    @Override
    public ResponseDto<WordScoreDto> computeScore(String keyWord) {

        HashMap<String, Integer> commonPrefixWords = new HashMap<>();
        List<String> suggestions = getWordSuggestions(keyWord).getData().getSuggestions();

        Integer removedLetters = 0;

        for (String suggestion : suggestions) {
            commonPrefixWords.put(suggestion, removedLetters);
        }

        //used to keep the original value of keyword,without removing chars, to be returned in method response
        String unModifiedKeyword = keyWord;

        //remove one letter for keyword
        keyWord = removeLetterFromKeyWord(keyWord);
        removedLetters++;

        while (keyWord.length() != 0) {
            List<String> newSuggestions = getWordSuggestions(keyWord).getData().getSuggestions();

            for (String suggestion : newSuggestions) {

                if (commonPrefixWords.containsKey(suggestion)) {
                    commonPrefixWords.put(suggestion, removedLetters);
                }
            }

            logger.info("sSearched keyword after removal of 1 letter: " + keyWord);

            keyWord = removeLetterFromKeyWord(keyWord);
            removedLetters++;

        }

        logger.info("number of iterations" + removedLetters);
        return new ResponseDto<>(new WordScoreDto(unModifiedKeyword, calculateScore(commonPrefixWords, unModifiedKeyword)));

    }


    /**
     * This method gets the suggestions (words) of a given keyWord ,
     *
     * @param keyWord string
     * @return wordSuggestionsDto: structure holding the keyword and its suggestions
     */
    private ResponseDto<WordSuggestionsDto> getWordSuggestions(String keyWord) {

        keyWord = keyWord.replaceAll(" ", "+");
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(apiUrl + keyWord);

        WordSuggestionsDto wordSuggestionsDto = new WordSuggestionsDto();
        try {

            String responseJson = EntityUtils.toString(client.execute(httpPost).getEntity());

            JSONArray responseArray = new JSONArray(responseJson);

            wordSuggestionsDto.setKeyWord(responseArray.get(0).toString());

            JSONArray suggestions = new JSONArray(responseArray.get(1).toString());

            suggestions.forEach(suggestion -> {
                wordSuggestionsDto.getSuggestions().add(suggestion.toString());
            });

            return new ResponseDto<>(true, "", wordSuggestionsDto, HttpStatus.OK);

        } catch (IOException e) {
            return new ResponseDto<>(false, e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @param words,  Map<String, Integer>
     * @param keyWord string
     * @return score float
     */
    private float calculateScore(Map<String, Integer> words, String keyWord) {

        Integer keywordScore = words.get(keyWord);
        Integer lowestScore = Collections.min(words.values());
        Integer highestScore = Collections.max(words.values());

        if (keywordScore == null) {
            return Math.round(((float) lowestScore / highestScore) * 100);
        }

        return Math.round(((float) keywordScore / highestScore) * 100);
    }


    /**
     * Remove last char from a string (keyword)
     *
     * @param keyWord string
     * @return string
     */

    private String removeLetterFromKeyWord(String keyWord) {
        String polishedKeyWord = null;
        if ((keyWord != null) && (keyWord.length() > 0)) {
            polishedKeyWord = keyWord.substring(0, keyWord.length() - 1);
        }
        return polishedKeyWord;
    }


}
