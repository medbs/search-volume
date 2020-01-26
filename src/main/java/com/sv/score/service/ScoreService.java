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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ScoreService implements IScoreService {


    /*@Override
    public ResponseDto<WordScoreDto> computeScore(String keyWord) {
        int numberWordsWithHigherScore = 0;
        int numberWordsWithLowerScore = 0;
        String topWord = "";
        String bottomWord = "";

        // while ((numberWordsWithHigherScore + numberWordsWithLowerScore) < 100) {

        List<String> suggestions = getWordSuggestions(keyWord).getData().getSuggestions();

        if (!isWordExistsInStructure(keyWord, suggestions)) {
            return new ResponseDto<>(true, "", new WordScoreDto(keyWord, 0), HttpStatus.OK);
        }

        numberWordsWithLowerScore = numberWordsWithLowerScore + findNumberWordsWithLowerScore(keyWord, suggestions);
        numberWordsWithHigherScore = numberWordsWithHigherScore + findNumberWordsWithHigherScore(keyWord, suggestions);


        bottomWord = suggestions.get(suggestions.size() - 1);
        topWord = suggestions.get(0);

        int score = calculateScore(numberWordsWithLowerScore, numberWordsWithHigherScore);

        return new ResponseDto<>(true, "", new WordScoreDto(keyWord, score), HttpStatus.OK);

        //}
    } */

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


    private int scoreOfWordInBottom(String keyWord, List<String> suggestions) {

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

    private int scoreOfWordInBetween(String keyWord, List<String> suggestions) {

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

            //get suggestions of the top word
            highSuggestions = getWordSuggestions(topWord).getData().getSuggestions();

            //if the top word is not in the returned structure,then exist loop
            if (!isWordExistsInStructure(topWord, highSuggestions))
                break;

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
            String bottomWord = lowSuggestions.get(suggestions.size() - 1);

            //get the suggestions of the bottom word
            lowSuggestions = getWordSuggestions(bottomWord).getData().getSuggestions();

            //if the bottom word is not in returned the returned structure, then exit loop
            if (!isWordExistsInStructure(bottomWord, lowSuggestions))
                break;
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

    private int calculateScore(int numberWordsWithLowerScore, int numberWordsWithHigherScore) {

        return ((numberWordsWithLowerScore + 1) / (numberWordsWithHigherScore + numberWordsWithLowerScore + 1)) * 100;
    }


    /**
     * This method gets the suggestions (words) of a given keyWord ,
     *
     * @param keyWord string
     * @return wordSuggestionsDto: structure holding the keyword and its suggestions
     */
    private ResponseDto<WordSuggestionsDto> getWordSuggestions(String keyWord) {

        keyWord = keyWord.replaceAll(" ", "+");
        //HttpClient client = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://completion.amazon.com/search/complete?search-alias=aps&client=amazon-search-ui&mkt=1&q=" + keyWord);
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

    /*public ResponseDto<WordScoreDto> calculateScore(String keyWord) {

        try {
            WordSuggestionsDto wordSuggestions = getWordSuggestions(keyWord).getData();

            String foundWord = wordSuggestions.getSuggestions()
                    .stream()
                    .filter(keyWord::equals)
                    .findAny()
                    .orElse(null);

            if (foundWord == null)
                return new ResponseDto<>(new WordScoreDto(keyWord, 0));
            else {

                Integer foundWordIndex = wordSuggestions.getSuggestions().indexOf(foundWord);
                return new ResponseDto<>(true, "", new WordScoreDto(keyWord, 10 - foundWordIndex), HttpStatus.OK);
            }

        } catch (Exception e) {
            return new ResponseDto<>(false, e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
    }*/


}
