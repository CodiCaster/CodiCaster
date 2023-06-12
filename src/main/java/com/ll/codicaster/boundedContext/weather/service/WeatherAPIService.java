package com.ll.codicaster.boundedContext.weather.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.codicaster.boundedContext.weather.entity.Weather;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
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


    private String[] calcDateAndTime() {
        //0 : nowDate, 1 : nowTime, 2 : baseDate, 3 : baseTime
        String[] dateAndTime = new String[4];

        LocalTime nowTime = LocalTime.now();
        LocalDate nowDate = LocalDate.now();

        LocalTime dateStandard = LocalTime.of(3, 10, 0);

        if (nowTime.getMinute() >= 30) {
            nowTime = nowTime.plusHours(1);
        }

        dateAndTime[3] = "0200";
        if (nowTime.isBefore(dateStandard)) {
            nowDate = nowDate.minusDays(1);
            dateAndTime[3] = "2300";
        }

        dateAndTime[0] = nowDate.toString().replaceAll("-", "");
        dateAndTime[1] = nowTime.toString().substring(0, 2) + "00";
        dateAndTime[2] = nowDate.toString().replaceAll("-", "");
        return dateAndTime;
    }

    public Weather getApiWeather(int pointX, int pointY) {
        Weather weather = Weather.getDefaultWeather();

        //0 : nowDate, 1 : nowTime, 2 : baseDate, 3 : baseTime
        String[] dateAndTime = calcDateAndTime();

        try {
            URL url = getURL(dateAndTime, pointX, pointY);
            StringBuilder sb = getResponseString(url);
            weather = getWeatherFromJSON(sb, weather, dateAndTime);
            return weather;
        } catch (Exception e) {
            return weather;
        }
    }

    private StringBuilder getResponseString(URL url) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

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
        return sb;
    }

    private URL getURL(String[] dateAndTime, int xLan, int yLon) throws Exception {
        String urlString = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst" + "?" + URLEncoder.encode("serviceKey", StandardCharsets.UTF_8) + "=" + REST_KEY + /*Service Key*/
                "&" + URLEncoder.encode("pageNo", StandardCharsets.UTF_8) + "=" + URLEncoder.encode("1", StandardCharsets.UTF_8) + /*페이지번호*/
                "&" + URLEncoder.encode("numOfRows", StandardCharsets.UTF_8) + "=" + URLEncoder.encode("300", StandardCharsets.UTF_8) + /*한 페이지 결과 수*/
                "&" + URLEncoder.encode("dataType", StandardCharsets.UTF_8) + "=" + URLEncoder.encode("JSON", StandardCharsets.UTF_8) + /*요청자료형식(XML/JSON) Default: XML*/
                "&" + URLEncoder.encode("base_date", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(dateAndTime[2], StandardCharsets.UTF_8) + /*‘XX년 X월 XX일 발표*/
                "&" + URLEncoder.encode("base_time", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(dateAndTime[3], StandardCharsets.UTF_8) + /*XX시 발표*/
                "&" + URLEncoder.encode("nx", StandardCharsets.UTF_8) + "=" + URLEncoder.encode("" + xLan, StandardCharsets.UTF_8) + /*예보지점의 X 좌표값*/
                "&" + URLEncoder.encode("ny", StandardCharsets.UTF_8) + "=" + URLEncoder.encode("" + yLon, StandardCharsets.UTF_8); /*예보지점의 Y 좌표값*/
        return new URL(urlString);
    }

    private Weather getWeatherFromJSON(StringBuilder sb, Weather weather, String[] dateAndTime) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonData = objectMapper.readTree(sb.toString());

        JsonNode itemNode = jsonData
                .path("response")
                .path("body")
                .path("items")
                .path("item");

        if (!itemNode.isArray()) {
            return weather;
        }
        for (JsonNode node : itemNode) {
            String category = node.path("category").asText();
            if (node.path("fcstDate").asText().equals(dateAndTime[0])
                    && node.path("fcstTime").asText().equals(dateAndTime[1])) {
                if (category.equals("TMP")) {
                    weather.setTmp(Double.parseDouble(node.path("fcstValue").asText()));
                }
                if (category.equals("POP")) {
                    weather.setPop(Double.parseDouble(node.path("fcstValue").asText()));
                }
                if (category.equals("PTY")) {
                    weather.setPty(Integer.parseInt(node.path("fcstValue").asText()));
                }
                if (category.equals("REH")) {
                    weather.setReh(Double.parseDouble(node.path("fcstValue").asText()));
                }
                if (category.equals("SKY")) {
                    weather.setSky(Integer.parseInt(node.path("fcstValue").asText()));
                }
            }
            if (category.equals("TMN") && weather.getTmn() == null) {
                weather.setTmn(Double.parseDouble(node.path("fcstValue").asText()));
            }
            if (category.equals("TMX") && weather.getTmx() == null) {
                weather.setTmx(Double.parseDouble(node.path("fcstValue").asText()));
            }
        }
        return weather;
    }
}
