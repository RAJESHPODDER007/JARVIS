package com.ai.chat.tools;

import com.ai.chat.model.ForecastResponse;
import com.ai.chat.model.WeatherResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@Slf4j
public class WeatherTool {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String WEATHER_API_KEY="05649ee98e894ddcb3114558260105";

    @Tool(description = "get weather forecast for the given city and date (yyyy-MM-dd)")
    public WeatherResult getWeather(String city, String date) {
        try {
            String url = UriComponentsBuilder
                    .fromUriString("https://api.weatherapi.com/v1/forecast.json")
                    .queryParam("key", WEATHER_API_KEY)
                    .queryParam("q", city)
                    .queryParam("dt", date)
                    .toUriString();
            ForecastResponse response = restTemplate.getForObject(url, ForecastResponse.class);
            if(response==null){
                return new WeatherResult(city,date,"N/A","N/A");
            }
            ForecastResponse.ForecastDay forecastDay = response.getForecast().getForecastday().get(0);
            String condition = forecastDay.getDay().getCondition().getText();
            double temp = forecastDay.getDay().getAvgtemp_c();
            return new WeatherResult(city,date,temp+" °C ",condition);
        }catch (Exception e){
            log.error(e.getMessage());
            return new WeatherResult(city,date,"N/A","N/A");
        }
    }
}
