package com.example.luggagescanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;

public class Poster {

    private static String surround(String str) {
        return "\"" + str + "\"";
    }

    private static String getJSONString(Map<String, String> jsonMap) {
        String result = "{";
        for (Iterator<String> it = jsonMap.keySet().iterator(); it.hasNext();) {
            String key = it.next();
            result += surround(key) + ": " + surround(jsonMap.get(key));
            if (it.hasNext()) {
                result += ",";
            } else {
                break;
            }
        }
        result += "}";
        return result;
    }

    public static String POST(String server, Map<String, String> jsonMap) {
        URL url;

        try {
            url = new URL(server);
        } catch (MalformedURLException e) {
            return "Error: " + e.getMessage();
        }

        HttpURLConnection connection;
        try {
            connection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }

        try {
            connection.setRequestMethod("POST");
        } catch (ProtocolException e) {
            return "Error: " + e.getMessage();
        }

        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);
        connection.setDoInput(true);

        String result = getJSONString(jsonMap);

        byte[] out = result.getBytes(StandardCharsets.UTF_8);
        int length = out.length;

        connection.setFixedLengthStreamingMode(length);
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        try {
            connection.connect();
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }

        try {
            OutputStream os = connection.getOutputStream();
            os.write(out);
            os.flush();
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }

        StringBuilder response = new StringBuilder();
        BufferedReader br;

        try {
            br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }

        while (true) {
            try {
                String responseLine = br.readLine();
                if (responseLine == null) {
                    break;
                }
                response.append(responseLine.trim());
            } catch (IOException e) {
                return "Error: " + e.getMessage();
            }
        }

        connection.disconnect();

        return response.toString();
    }
}

