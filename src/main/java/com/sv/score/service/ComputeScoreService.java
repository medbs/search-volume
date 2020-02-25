package com.sv.score.service;


import com.sv.score.dto.GlobalWordSuggestionDto;
import com.sv.score.dto.ResponseDto;
import com.sv.score.dto.WordSuggestionV2Dto;
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
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ComputeScoreService implements IScoreServiceV2 {

    private Logger logger = LoggerFactory.getLogger(ScoreService.class);

    @Value("${amazon.api.url}")
    private String apiUrl;

    @Override
    public ResponseDto<Map<String, Integer>> computeScoreV2(String keyWord) {

        HashMap<String, Integer> commonPrefixWords = new HashMap<>();
        List<String> suggestions = getWordSuggestions(keyWord).getData().getSuggestions();

        Integer removedLetters =0;

        for (String suggestion : suggestions) {
            commonPrefixWords.put(suggestion, removedLetters);
        }

        keyWord = removeLetterFromKeyWord(keyWord);
        removedLetters++;

        while (keyWord.length() != 0) {
            List<String> newSuggestions = getWordSuggestions(keyWord).getData().getSuggestions();

            for (String suggestion : newSuggestions) {

                if (commonPrefixWords.containsKey(suggestion)) {
                    commonPrefixWords.put(suggestion, removedLetters);
                }
            }

            keyWord = removeLetterFromKeyWord(keyWord);
            removedLetters++;
        }

        logger.info(String.valueOf(removedLetters));
        return new ResponseDto<>(commonPrefixWords);
    }


    public ResponseDto<Map<String, String>> computeScoreV2old(String keyWord) {

        GlobalWordSuggestionDto globalWordSuggestionDto = new GlobalWordSuggestionDto();

        HashMap<String, String> commonPrefixWords = new HashMap<>();

        int call = 0;
        while (keyWord.length() != 0) {
            List<String> suggestions = getWordSuggestions(keyWord).getData().getSuggestions();


            for (String suggestion : suggestions) {
                WordSuggestionV2Dto wordSuggestionV2Dto = new WordSuggestionV2Dto();

                if (!commonPrefixWords.containsKey(suggestion)) {
                    commonPrefixWords.put(suggestion, keyWord);
                }
            }

            /*for (int i = 0; i < suggestions.size(); i++) {
                if (!commonPrefixWords.containsKey(suggestions.get(i))) {
                    commonPrefixWords.put(suggestions.get(i), call);
                }
            }*/

            call++;
            /*suggestions.forEach(s -> {
                if (!commonPrefixWords.containsKey(s)) {

                    commonPrefixWords.put(s, 0);
                }
            });*/

            keyWord = removeLetterFromKeyWord(keyWord);
        }

        //HashMap<String, Integer> sortedMap = sortByValue(commonPrefixWords);

        return new ResponseDto<>(commonPrefixWords);
    }


    /**
     * function to sort hashmap by values
     */

    private HashMap<String, Integer> sortByValue(HashMap<String, Integer> hm) {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Integer>> list =
                new LinkedList<Map.Entry<String, Integer>>(hm.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        // put data from sorted list to hashmap
        HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
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
     * @param word             ,string
     * @param wordSuggestions, list of strings
     * @return True of a word exists in the structure,false if not
     */
    private Boolean isWordExistsInList(String word, List<String> wordSuggestions) {

        String foundWord = wordSuggestions
                .stream()
                .filter(word::equals)
                .findAny()
                .orElse(null);

        return foundWord != null;
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
