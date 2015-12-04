package it.egidiocaprino.busyplace;

import android.os.AsyncTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class CountAsyncTask extends AsyncTask<Double, Void, Long> {

    final OnCountListener listener;

    public CountAsyncTask(OnCountListener listener) {
        this.listener = listener;
    }

    @Override protected Long doInBackground(Double... params) {
        String result;
        InputStream response = null;
        String url = "https://busy-place.herokuapp.com/api/position?latitude=" + params[0]
                   + "&longitude=" + params[1] + "&distance=" + params[2];

        try {
            HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
            response = connection.getInputStream();

            int length;
            byte[] buffer = new byte[1024 * 4];
            ByteArrayOutputStream resultBytes = new ByteArrayOutputStream();

            while ((length = response.read(buffer)) != -1) {
                resultBytes.write(buffer, 0, length);
            }

            result = new String(resultBytes.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return Long.parseLong(result);
    }

    @Override protected void onPostExecute(Long count) {
        listener.onCount(count);
    }

}
