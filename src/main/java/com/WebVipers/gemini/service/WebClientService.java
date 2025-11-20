package com.WebVipers.gemini.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class WebClientService {
	
	@Value("${backstage.access.token}")
	private String backstageAccessToken;
    
    private final WebClient webClient;
    
    public WebClientService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }
    
    public <T, R> R callPostApi(String url, T requestBody, Class<R> responseType) {
        return webClient.post()
            .uri(url)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, backstageAccessToken)
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(responseType)
            .block(); // For synchronous call
    }
    
}
