package com.example.network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import com.example.tetris.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AdMgmtClass {
    private final String baseUrl;
    private static final String ADS_Endpoint = "/ads";
    private static final String ADS_Index = "/index.html";

    public volatile boolean isPlaying = true;


    private final List<String> adRequestStrings = new ArrayList<>();

    private ImageView iv;

    public AdMgmtClass(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void requestIndexHtml() throws Exception {
        String urlString = baseUrl + ADS_Endpoint + ADS_Index;
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
                adRequestStrings.add(inputLine);
            }
            in.close();

        } else {
            throw new RuntimeException("Failed : HTTP error code : " + responseCode);
        }
    }

    public String getRandomAdRequestString() {
        if (adRequestStrings.isEmpty()) {
            return "";
        }

        Random randomizer = new Random();
        return adRequestStrings.get(randomizer.nextInt(adRequestStrings.size()));
    }

    public Bitmap getRandomAd() {
        String adURL = this.baseUrl + ADS_Endpoint + "/" + getRandomAdRequestString() + ".png";
        Bitmap bitmap = null;
        try {
            URL url = new URL(adURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public void RunAds() {
        this.isPlaying = true;
        new Thread(() -> {
            while (isPlaying) {
                try {
                    Bitmap img = this.getRandomAd();
                    this.iv.setImageBitmap(img);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("AD", e.toString());
                }
            }
        }).start();
    }

    public synchronized void StopAd() {
        this.isPlaying = false;
    }
}
