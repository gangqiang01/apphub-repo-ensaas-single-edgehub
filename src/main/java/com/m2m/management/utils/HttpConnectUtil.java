package com.m2m.management.utils;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @date ：Created in 6/4/19 5:55 PM
 * @description：connect server by http protocol
 */
public class HttpConnectUtil {

    public static boolean checkURLOK(String surl) {

        try {
            URL url = new URL(surl);
            HttpURLConnection.setFollowRedirects(false);

            HttpURLConnection con =  (HttpURLConnection) url.openConnection();

            con.setRequestMethod("HEAD");

            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
