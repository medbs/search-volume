package com.sv.score.service;

import com.sv.score.dto.ResponseDto;
import com.sv.score.dto.WordScoreDto;
import com.sv.score.dto.WordSuggestionsDto;
import com.sv.score.interfaces.IScoreService;
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
import java.util.List;

@Service
public class ScoreService implements IScoreService {

    private Logger logger = LoggerFactory.getLogger(ScoreService.class);

    @Value("${amazon.api.url}")
    private String apiUrl;


    /**
     * 4 possible scenarios : word is not in the first returned structure, word is in top of the first returned structure,
     * word is in the bottom of the first returned structure , word is in between the top and bottom of the first returned structure
     *
     * @param keyWord string
     * @return responseDto containing the key word ant its score.
     */
    @Override
    public ResponseDto<WordScoreDto> computeScore(String keyWord) {

        List<String> suggestions = getWordSuggestions(keyWord).getData().getSuggestions();

        if (!isWordExistsInStructure(keyWord, suggestions)) {
            return new ResponseDto<>(true, "", new WordScoreDto(keyWord, 0), HttpStatus.OK);
        }

        if (isWordInTopStructure(keyWord, suggestions)) {
            // if numberWordsWithHigherScore = 0, the result will always be 100 when applying the score formula
            return new ResponseDto<>(true, "", new WordScoreDto(keyWord, 100), HttpStatus.OK);
        }

        if (isWordInBottomStructure(keyWord, suggestions)) {
            return new ResponseDto<>(true, "", new WordScoreDto(keyWord, scoreOfWordInBottom(keyWord, suggestions)), HttpStatus.OK);
        }


        return new ResponseDto<>(true, "", new WordScoreDto(keyWord, scoreOfWordInBetween(keyWord, suggestions)), HttpStatus.OK);


    }

    /**
     * calculate the score of a word in the bottom of the first returned structure
     *
     * @param keyWord     string
     * @param suggestions list of strings
     * @return float
     */

    private float scoreOfWordInBottom(String keyWord, List<String> suggestions) {

        int numberWordsWithHigherScore = 0;
        List<String> highSuggestions = suggestions;

        //count number of words with higher score
        numberWordsWithHigherScore = numberWordsWithHigherScore + findNumberWordsWithHigherScore(keyWord, highSuggestions);

        //20 is a random number, it can be changed
        while (numberWordsWithHigherScore < 20) {
            //get the top word that have the highest score
            String topWord = highSuggestions.get(0);

            //get suggestions of the top word
            highSuggestions = getWordSuggestions(topWord).getData().getSuggestions();

            //if the top word is not in the returned structure,then exist loop
            if (!isWordExistsInStructure(topWord, highSuggestions))
                break;

            //count number of words with higher score
            numberWordsWithHigherScore = numberWordsWithHigherScore + findNumberWordsWithHigherScore(topWord, highSuggestions);
        }
        return calculateScore(0, numberWordsWithHigherScore);
    }

    /**
     * calculate the score of a word that exists the first returned structure, but not in top or bottom
     *
     * @param keyWord     string
     * @param suggestions list of strings
     * @return float
     */
    private float scoreOfWordInBetween(String keyWord, List<String> suggestions) {

        int numberWordsWithHigherScore = 0;
        int numberWordsWithLowerScore = 0;

        List<String> highSuggestions = suggestions;
        List<String> lowSuggestions = suggestions;

        /**
         * calculating the number of word with higher score
         * **/

        //count number of words with higher score
        numberWordsWithHigherScore = numberWordsWithHigherScore + findNumberWordsWithHigherScore(keyWord, highSuggestions);

        //20 is a random number, it can be changed
        while (numberWordsWithHigherScore < 50) {
            //get the top word that have the highest score
            String topWord = highSuggestions.get(0);

            logger.info("top word: " + topWord);

            //get suggestions of the top word
            highSuggestions = getWordSuggestions(topWord).getData().getSuggestions();

            //if the top word is not in the returned structure,then exist loop
            if (!isWordExistsInStructure(topWord, highSuggestions))
                break;

            //if the top word is also the top word in the returned structure, then there's no point to continue
            if (0 == (findNumberWordsWithHigherScore(topWord, highSuggestions))) {
                break;
            }

            //count number of words with higher score
            numberWordsWithHigherScore = numberWordsWithHigherScore + findNumberWordsWithHigherScore(topWord, highSuggestions);
        }

        /**
         * calculating the number of word with lower score
         * **/

        //count the number of words with lower score
        numberWordsWithLowerScore = numberWordsWithLowerScore + findNumberWordsWithLowerScore(keyWord, lowSuggestions);

        while (numberWordsWithLowerScore < 50) {

            //get the bottom word that have the lowest score
            String bottomWord = lowSuggestions.get(lowSuggestions.size() - 1);

            logger.info("bottom word: " + bottomWord);

            //get the suggestions of the bottom word
            lowSuggestions = getWordSuggestions(bottomWord).getData().getSuggestions();

            //if the bottom word is not in returned structure, then exit loop
            if (!isWordExistsInStructure(bottomWord, lowSuggestions))
                break;

            //if the bottom word is also the bottom word in the returned structure, then there's no point to continue
            if (0 == (findNumberWordsWithLowerScore(bottomWord, lowSuggestions))) {
                break;
            }

            numberWordsWithLowerScore = numberWordsWithLowerScore + findNumberWordsWithLowerScore(bottomWord, lowSuggestions);
        }

        return calculateScore(numberWordsWithLowerScore, numberWordsWithHigherScore);
    }

    /**
     * This method gets the number of words with higher search volume than a given keyWord
     *
     * @param keyWord          , string
     * @param wordSuggestions, list the word suggestions
     * @return True of a word exists in the structure,false if not
     */

    private int findNumberWordsWithHigherScore(String keyWord, List<String> wordSuggestions) {

        return wordSuggestions.indexOf(keyWord);
    }


    /**
     * This method gets the number of words with lower search volume than a given keyWord
     *
     * @param keyWord          ,string
     * @param wordSuggestions, list the word suggestions
     * @return True of a word exists in the structure,false if not
     */

    private int findNumberWordsWithLowerScore(String keyWord, List<String> wordSuggestions) {

        return wordSuggestions.size() - wordSuggestions.indexOf(keyWord) - 1;

    }

    /**
     * @param keyWord          ,string
     * @param wordSuggestions, list of strings
     * @return True of a word exists in the structure,false if not
     */
    private Boolean isWordExistsInStructure(String keyWord, List<String> wordSuggestions) {

        String foundWord = wordSuggestions
                .stream()
                .filter(keyWord::equals)
                .findAny()
                .orElse(null);

        return foundWord != null;
    }

    /***
     * checks if a given word is in the top of a structure (highest score)
     * @param keyWord string
     * @param wordSuggestions list of strings
     * @return true if in top, false if not
     */
    private Boolean isWordInTopStructure(String keyWord, List<String> wordSuggestions) {
        String topWord = wordSuggestions.get(0);
        return keyWord.equals(topWord);
    }

    /**
     * checks if a given word is in the bottom of a structure (lowest score)
     *
     * @param keyWord         string
     * @param wordSuggestions list of strings
     * @return true if in bottom,false if not
     */
    private Boolean isWordInBottomStructure(String keyWord, List<String> wordSuggestions) {

        String bottomWord = wordSuggestions.get(wordSuggestions.size() - 1);
        return keyWord.equals(bottomWord);
    }

    /**
     * calculate the score using this formula : ((numberWordsWithLowerScore + 1) / ( numberWordsWithHigherScore + numberWordsWithLowerScore + 1)) * 100
     *
     * @param numberWordsWithLowerScore  int
     * @param numberWordsWithHigherScore int
     * @return the score
     */

    private float calculateScore(int numberWordsWithLowerScore, int numberWordsWithHigherScore) {
        int a = numberWordsWithLowerScore + 1;
        int b = numberWordsWithLowerScore + numberWordsWithHigherScore + 1;
        return Math.round(((float) a / b) * 100);

        // return Math.round((float)((numberWordsWithLowerScore + 1) / (numberWordsWithHigherScore + numberWordsWithLowerScore + 1)) / 100);
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



}
