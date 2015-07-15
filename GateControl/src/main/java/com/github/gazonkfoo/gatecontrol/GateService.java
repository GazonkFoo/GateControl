package com.github.gazonkfoo.gatecontrol;

import android.content.SharedPreferences;
import android.util.Base64;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Sebastian Maurer on 13.07.2015.
 */
public class GateService {

    private static final int CONNECT_TIMEOUT = 10 * 1000;
    private static final int READ_TIMEOUT = 10 * 1000;

    private String baseUrl;
    private String username;
    private String password;

    public GateService(SharedPreferences sharedPreferences) {
        this.baseUrl = sharedPreferences.getString(Constants.PREF_URL, null);
        this.username = sharedPreferences.getString(Constants.PREF_USERNAME, null);
        this.password = sharedPreferences.getString(Constants.PREF_PASSWORD, null);
    }

    public String register(String token) throws IOException {
        return call("/register", token);
    }

    public void unregister(String token) throws IOException {
        call("/unregister", token);
    }

    public void buttonDown() throws IOException {
        call("/buttonDown", "");
    }

    public String getState() throws IOException {
        return call("/state", null);
    }

    public List<String> getRegistrations() throws IOException {
        String resp = call("/registrations", null);

        List<String> registrations = new ArrayList<String>();
        if (resp.length() < 2) {
            registrations = Arrays.asList(resp.substring(1, resp.length() - 2).split(","));
        }

        return registrations;
    }

    private String call(String url, String requestBody) throws IOException {
        HttpURLConnection connection = null;
        try {
            if(this.baseUrl == null)
                throw new IOException("Invalid URL");

            URL urlToRequest = new URL(this.baseUrl + url);
            connection = (HttpURLConnection) urlToRequest.openConnection();
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);

            if(this.username != null && !this.username.isEmpty() && this.password != null && !this.password.isEmpty()) {
                String encoded = Base64.encodeToString((this.username + ":" + this.password).getBytes("UTF-8"), Base64.NO_WRAP);
                connection.setRequestProperty("Authorization", "Basic " + encoded);
            }

            if (requestBody != null) {
                connection.setDoOutput(true);
                connection.setChunkedStreamingMode(0);
                connection.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
                writeStream(connection.getOutputStream(), requestBody);
            }

            int statusCode = connection.getResponseCode();
            if (statusCode < 200 || statusCode >= 300) {
                throw new IOException("Invalid response from server (" + statusCode + "): " + connection.getResponseMessage());
            }

            return readStream(connection.getInputStream());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private void writeStream(OutputStream out, String data) throws IOException {
        try {
            out.write(data.getBytes("UTF-8"));
        } finally {
            if(out != null) {
                out.close();
            }
        }
    }

    private String readStream(InputStream in) throws IOException {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder str = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                str.append(line);
            }
            return str.toString();
        } finally {
            if(in != null) {
                in.close();
            }
        }
    }

}
