package pl.lewarski.starwarsrest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

public class Connect {
    public static String getUrlContents(String theUrl) {
        StringBuilder content = new StringBuilder();
        try {
            URLConnection connection = new URL(theUrl).openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.connect();

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));

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

