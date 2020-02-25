package com.sv.score.dto;

import java.io.Serializable;
import java.util.List;

public class GlobalWordSuggestionDto implements Serializable {

    private static final long serialVersionUID = 5059159476462589989L;

    List<WordSuggestionV2Dto> subWordsSuggestions;

    public List<WordSuggestionV2Dto> getSubWordsSuggestions() {
        return subWordsSuggestions;
    }

    public void setSubWordsSuggestions(List<WordSuggestionV2Dto> subWordsSuggestions) {
        this.subWordsSuggestions = subWordsSuggestions;
    }
}
