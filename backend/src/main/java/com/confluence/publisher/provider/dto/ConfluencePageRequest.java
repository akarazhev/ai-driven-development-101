package com.confluence.publisher.provider.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConfluencePageRequest {
    
    private String type;
    private String title;
    private Space space;
    private Body body;
    private List<Ancestor> ancestors;
    private Version version;
    
    @Data
    @Builder
    public static class Space {
        private String key;
    }
    
    @Data
    @Builder
    public static class Body {
        private Storage storage;
    }
    
    @Data
    @Builder
    public static class Storage {
        private String value;
        private String representation;
    }
    
    @Data
    @Builder
    public static class Ancestor {
        private String id;
    }
    
    @Data
    @Builder
    public static class Version {
        private int number;
    }
}
