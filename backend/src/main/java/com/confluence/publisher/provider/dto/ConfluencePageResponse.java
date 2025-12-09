package com.confluence.publisher.provider.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfluencePageResponse {
    
    private String id;
    private String type;
    private String status;
    private String title;
    private Space space;
    private Version version;
    private Links _links;
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Space {
        private String key;
        private String name;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Version {
        private int number;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Links {
        private String webui;
        private String self;
    }
}
