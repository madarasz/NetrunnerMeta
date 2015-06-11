package com.madarasz.netrunnerstats.brokers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

/**
 * Loading from http and parsing html
 * Created by madarasz on 05/06/15.
 */
public final class HttpBroker {

    private static Document document;

    private HttpBroker() {
    }

    /**
     * Reads contents from URL
     * @param urlString URL
     * @param fixJson whether to add JSON fix to contents
     * @return
     */
    public static String readFromUrl(String urlString, boolean fixJson) {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            if (fixJson) {
                return "{\"input\": " + buffer.toString() + "}";
            } else {
                return buffer.toString();
            }

        } catch (Exception ex) {
            return "";

        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public static void parseHtml(String url) {
        try {
            document = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String textFromHtml(String jsoupExpression) {
        return document.select(jsoupExpression).first().text();
    }

    public static ArrayList<String> textsFromHtml(String jsoupExpression) {
        ArrayList<String> result = new ArrayList<String>();
        Elements elements = document.select(jsoupExpression);
        for(Element element : elements) {
            result.add(element.text());
        }
        return result;
    }

    public static int countFromHtml(String jsoupExpression) {
        Elements elements = document.select(jsoupExpression);
        return elements.size();
    }
}
