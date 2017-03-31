package com.example.newcosts;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * TODO: Add a class header comment
 */

public class AsyncTaskGetCurrencyExchangeRates extends AsyncTask<Void, Void, Void> {

    private String currencyString = null;


    public AsyncTaskGetCurrencyExchangeRates(String currenciesToExchangeString) {
        currencyString = currenciesToExchangeString;
    }

    @Override
    protected void onPreExecute() {
        System.out.println("onPreExecute");
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (currencyString == null || currencyString == "")
            return null;

        URL url = null;
        InputStream inputStream = null;
        HttpURLConnection connection = null;

        String urlPartOneString = "http://download.finance.yahoo.com/d/quotes.csv?s=";
        String urlPartTwoString = "=X&f=sl1d1t1ba&e=.csv";

        try {
//            url = new URL("http://download.finance.yahoo.com/d/quotes.csv?s=msft&f=sl1p2");
//            url = new URL("http://download.finance.yahoo.com/d/quotes.csv?s=USDRUB=X&f=sl1d1t1ba&e=.csv");
            url = new URL(urlPartOneString + currencyString + urlPartTwoString);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            System.out.println(connection.getResponseCode() == HttpsURLConnection.HTTP_OK);

            inputStream = connection.getInputStream();
            if (inputStream != null) {
                System.out.println("in != null");

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    System.out.println(line);
                }
            } else
                System.out.println("in == null");

        } catch (MalformedURLException e) {
            System.out.println("MalformedURLException");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IOException");
            e.printStackTrace();
        } finally {
            if (inputStream != null)
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            if (connection != null)
                connection.disconnect();
        }

        return null;
    }
}
