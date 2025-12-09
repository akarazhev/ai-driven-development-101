package com.confluence.publisher.provider.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfluenceSearchResponse {
    
    private List<ConfluencePageResponse> results;
    private int start;
    private int limit;
    private int size;
}
