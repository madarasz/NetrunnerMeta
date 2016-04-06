package com.madarasz.netrunnerstats.brokers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.util.ArrayList;

/**
 * Loading from http and parsing html
 * Created by madarasz on 05/06/15.
 */
@Component
public class HttpBroker {

    private static final Logger logger = LoggerFactory.getLogger(HttpBroker.class);

    private Document document;

    /**
     * Reads contents from URL
     * @param urlString URL
     * @param fixJson whether to add JSON fix to contents
     * @return
     */
    public String readFromUrl(String urlString, boolean fixJson) {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            // remove unicode characters
            String result = buffer.toString().replaceAll("\\p{C}", "").trim();

            if (fixJson) {
                return "{\"input\": " + result + "}";
            } else {
                return result;
            }

        } catch (Exception ex) {
            logger.error("logged exception", ex);
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

    public void parseHtml(String url) {
        int retry = 0;
        boolean readok = true;
        do {
            readok = true;
            try {
//                Connection.Response response = Jsoup.connect(url).userAgent("Mozilla").followRedirects(true).execute();
//                logger.debug(response.url());
                document = Jsoup.connect(url).userAgent("Mozilla").followRedirects(true).timeout(5000).get();
            } catch (IOException e) {
                logger.error(String.format("ERROR - could not read HTML - %s - retrying", url));
                retry++;
                readok = false;
                if (retry == 5) e.printStackTrace();
            }
        } while ((!readok) && (retry < 5));
    }

    public String textFromHtml(String jsoupExpression) {
        return document.select(jsoupExpression).first().text().replaceAll("\\p{C}", "");    // removing unicode characters as well
    }

    public String htmlFromHtml(String jsoupExpression) {
        return document.select(jsoupExpression).first().html().replaceAll("\\p{C}", "");    // removing unicode characters as well
    }

    public String attirubuteFromHhtml(String jsoupExpression, String attribute) {
        return document.select(jsoupExpression).first().attr(attribute);
    }

    public Elements elementsFromHtml(String jsoupExpression) {
        try {
            return document.select(jsoupExpression);
        } catch (Exception ex) {
            return new Elements();
        }
    }

    public ArrayList<String> linesFromHtml(String jsoupExpression) {
        ArrayList<String> result = new ArrayList<String>();
        Elements elements = document.select(jsoupExpression);
        for(Element element : elements) {
            result.add(element.text().replaceAll("\\p{C}", ""));    // removing unicode characters as well
        }
        return result;
    }

    public int countFromHtml(String jsoupExpression) {
        Elements elements = document.select(jsoupExpression);
        return elements.size();
    }

    // accepting https
    static {
        final TrustManager[] trustAllCertificates = new TrustManager[] {
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws CertificateException {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[0];
                    }
                }
        };

        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCertificates, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (GeneralSecurityException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}
