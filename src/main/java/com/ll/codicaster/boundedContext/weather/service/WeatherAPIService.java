package com.ll.codicaster.boundedContext.weather.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.codicaster.boundedContext.weather.entity.Weather;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class WeatherAPIService {

    @Value("${api.weather.key}")
    private String REST_KEY;

    /**
     * @return weather
     */
    public Weather getApiWeather(Integer pointX, Integer pointY) {
        String tmp = "";
        String pop = "";
        String pty = "";
        String reh = "";
        String sky = "";
        String tmn = "";
        String tmx = "";

        int xLan = pointX;
        int yLon = pointY;

        LocalTime nowTime = LocalTime.now();
        LocalDate nowDate = LocalDate.now();

        LocalTime dateStandard = LocalTime.of(2, 10, 0);

        String baseTime = "0200";
        String nowDateStr = nowDate.toString().replaceAll("-", "");
        String nowTimeStr = nowTime.toString().substring(0, 2) + "00";

        if (nowTime.isBefore(dateStandard)) {
            nowDate = nowDate.minusDays(1);
            baseTime = "2300";
        }
        String baseDate = nowDate.toString().replaceAll("-", "");

        try {
            /*URL*/
            String urlString = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst" + "?" + URLEncoder.encode("serviceKey", StandardCharsets.UTF_8) + "=" + REST_KEY + /*Service Key*/
                    "&" + URLEncoder.encode("pageNo", StandardCharsets.UTF_8) + "=" + URLEncoder.encode("1", StandardCharsets.UTF_8) + /*페이지번호*/
                    "&" + URLEncoder.encode("numOfRows", StandardCharsets.UTF_8) + "=" + URLEncoder.encode("300", StandardCharsets.UTF_8) + /*한 페이지 결과 수*/
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
                        if (node.path("fcstDate").asText().equals(nowDateStr)
                                && node.path("fcstTime").asText().equals(nowTimeStr)) {
                            if (category.equals("TMP") && tmp.equals("")) {
                                tmp = node.path("fcstValue").asText();
                            }
                            if (category.equals("POP") && pop.equals("")) {
                                pop = node.path("fcstValue").asText();
                            }
                            if (category.equals("PTY") && pty.equals("")) {
                                pty = node.path("fcstValue").asText();
                            }
                            if (category.equals("REH") && reh.equals("")) {
                                reh = node.path("fcstValue").asText();
                            }
                            if (category.equals("SKY") && sky.equals("")) {
                                sky = node.path("fcstValue").asText();
                            }
                        }
                        if (category.equals("TMN") && tmn.equals("")) {
                            tmn = node.path("fcstValue").asText();
                        }
                        if (category.equals("TMX") && tmx.equals("")) {
                            tmx = node.path("fcstValue").asText();
                        }
                    }
                } else {
                    System.out.println("No item found in JSON data.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Weather(tmp, pop, pty, reh, sky, tmn, tmx);
    }
}
