package com.ll.codicaster.boundedContext.location.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class KakaoAPIService {

    @Value("${api.kakao.key}")
    private String REST_KEY;

    public String getAddressFromKakao(double longitude, double latitude) {
        String address = "";
        String urlString = "https://dapi.kakao.com/v2/local/geo/coord2regioncode?x=" + longitude + "&y=" + latitude;

        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "KakaoAK " + REST_KEY);

            // 응답 내용 읽기
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONObject json = new JSONObject(response.toString());
            JSONArray documents = json.getJSONArray("documents");

            for (int i = 0; i < documents.length(); i++) {
                JSONObject document = documents.getJSONObject(i);

                if (document.getString("region_type").equals("B")) {
                    String address1 = document.getString("region_1depth_name");
                    String address2 = document.getString("region_2depth_name");
                    String address3 = document.getString("region_3depth_name");

                    address = address1 + " " + address2 + " " + address3;
                }
            }
            // 연결 종료
            conn.disconnect();
        } catch (Exception e) {
            return null;
        }
        return address;
    }
}
