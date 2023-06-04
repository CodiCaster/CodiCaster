package com.ll.codicaster.boundedContext.weather.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.codicaster.boundedContext.location.entity.Point;
import com.ll.codicaster.boundedContext.weather.repository.WeatherRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class WeatherService {

    @Value("${api.weather.key}")
    private String REST_KEY;
    private WeatherRepository weatherRepository;
    private String temperatureMinimum;
    private String temperatureMaximum;

    /**
     * @return weatherInfo
     */
    public String[] getApiWeather(Point point) throws IOException {
        int xLan = point.getX();
        int yLon = point.getY();

        String[] bases = calcBaseDateAndTime();
        String baseDate = bases[0];
        String baseTime = bases[1];

        /*URL*/
        String urlString = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst" + "?" + URLEncoder.encode("serviceKey", StandardCharsets.UTF_8) + "=" + REST_KEY + /*Service Key*/
                "&" + URLEncoder.encode("pageNo", StandardCharsets.UTF_8) + "=" + URLEncoder.encode("1", StandardCharsets.UTF_8) + /*페이지번호*/
                "&" + URLEncoder.encode("numOfRows", StandardCharsets.UTF_8) + "=" + URLEncoder.encode("200", StandardCharsets.UTF_8) + /*한 페이지 결과 수*/
                "&" + URLEncoder.encode("dataType", StandardCharsets.UTF_8) + "=" + URLEncoder.encode("JSON", StandardCharsets.UTF_8) + /*요청자료형식(XML/JSON) Default: XML*/
                "&" + URLEncoder.encode("base_date", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(baseDate, StandardCharsets.UTF_8) + /*‘XX년 X월 XX일 발표*/
                "&" + URLEncoder.encode("base_time", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(baseTime, StandardCharsets.UTF_8) + /*XX시 발표*/
                "&" + URLEncoder.encode("nx", StandardCharsets.UTF_8) + "=" + URLEncoder.encode("" + xLan, StandardCharsets.UTF_8) + /*예보지점의 X 좌표값*/
                "&" + URLEncoder.encode("ny", StandardCharsets.UTF_8) + "=" + URLEncoder.encode("" + yLon, StandardCharsets.UTF_8); /*예보지점의 Y 좌표값*/
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
//        System.out.println("Response code: " + conn.getResponseCode());
        BufferedReader rd;
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();


        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonData = objectMapper.readTree(sb.toString());

            JsonNode itemNode = jsonData
                    .path("response")
                    .path("body")
                    .path("items")
                    .path("item");

            if (itemNode.isArray()) {
                for (JsonNode node : itemNode) {
                    String category = node.path("category").asText();
                    if (category.equals("TMN")) {
                        temperatureMinimum = node.path("fcstValue").asText();
                    }
                    if (category.equals("TMX")) {
                        temperatureMaximum = node.path("fcstValue").asText();
                        break;
                    }
                }
            } else {
                System.out.println("No item found in JSON data.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String[]{temperatureMinimum, temperatureMaximum};
    }

    /**
     * @return baseDateAndTime
     */
    private String[] calcBaseDateAndTime() {
        LocalTime nowTime = LocalTime.now();
        LocalDate nowDate = LocalDate.now();

        LocalTime dateStandard = LocalTime.of(2, 10, 0);

        String baseTime = "0200";
        if (nowTime.isBefore(dateStandard)) {
            nowDate = nowDate.minusDays(1);
            baseTime = "2300";
        }
        String baseDate = nowDate.toString().replaceAll("-", "");
        return new String[]{baseDate, baseTime};
    }
}
