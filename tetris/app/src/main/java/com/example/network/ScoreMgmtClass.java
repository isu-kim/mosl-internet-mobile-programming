package com.example.network;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class ScoreMgmtClass {

    private final String baseUrl;

    // Endpoints
    private static final String USER_INFO_ENDPOINT = "/user_info";
    private static final String UPLOAD_SCORE = "/upload_score";
    private static final String ALL_SCORES = "/all_scores";


    public ScoreMgmtClass(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public int getUserInfo(int userId) throws Exception {
        String urlString = baseUrl + USER_INFO_ENDPOINT + "?userid=" + userId;
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Parse JSON response
            JSONObject jsonResponse = new JSONObject(response.toString());
            int returnedUserId = jsonResponse.getInt("user_id");
            int score = jsonResponse.getInt("score");
            return score;
        } else {
            throw new RuntimeException("Failed : HTTP error code : " + responseCode);
        }
    }
    public void uploadScore(long userId, int score) throws Exception {
        String urlString = baseUrl + UPLOAD_SCORE;
        URL url = new URL(urlString);
        HttpURLConnection conn = null;

        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Create JSON payload
            JSONObject jsonPayload = new JSONObject();
            jsonPayload.put("user_id", userId);
            jsonPayload.put("score", score);

            // Write JSON payload to the connection
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonPayload.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Check HTTP response code
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                System.out.println("Score uploaded successfully for user " + userId);
            } else {
                throw new RuntimeException("Failed : HTTP error code : " + responseCode);
            }
        } catch (Exception e) {
            // Handle exceptions appropriately
            e.printStackTrace();
            throw e; // Rethrow the exception to propagate it
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}