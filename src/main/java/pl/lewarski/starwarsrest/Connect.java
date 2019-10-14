package pl.lewarski.starwarsrest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class Connect {
    public static String getUrlContents(String theUrl) {
        StringBuilder content = new StringBuilder();
        try {
            URLConnection connection = new URL(theUrl).openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");

            connection.connect();

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));

            String line;
            while ((line = in.readLine()) != null) {
                content.append(line + "\n");
            }
            in.close();
        } catch (
                Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }
}
