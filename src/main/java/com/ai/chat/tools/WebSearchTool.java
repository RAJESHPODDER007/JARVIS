package com.ai.chat.tools;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class WebSearchTool {

    private static final RestTemplate restTemplate = new RestTemplate();
    private static final String WEB_SEARCH_API_KEY="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";
    private static final String API_URL = "https://api.tavily.com/search";

    @Tool(description = "This function do a web search using tavily API based on the user input", returnDirect = true)
    public String search(String query) {
        log.info("Invoked web search tool {}", query);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(WEB_SEARCH_API_KEY);
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", query);
        requestBody.put("search_depth", "basic"); // Options: basic, advanced
        requestBody.put("max_results", 1);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);
        return transform(response.getBody()).getResults().get(0).getContent();
    }

    private SearchResponse transform(String response){
        try{
            ObjectMapper mapper = new ObjectMapper();
            var result= mapper.readValue(response,SearchResponse.class);
            log.info("Response {}", result);
            return result;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class SearchResponse{
        private List<Result> results;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Result {
        String title;
        String url;
        String content;
    }
}
