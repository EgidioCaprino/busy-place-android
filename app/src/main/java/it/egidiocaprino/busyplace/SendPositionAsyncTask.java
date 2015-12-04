package it.egidiocaprino.busyplace;

import android.os.AsyncTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class SendPositionAsyncTask extends AsyncTask<Object, Void, Void> {

    final String jsonFormat = "{\"id\":null,\"deviceId\":\"%s\",\"latitude\":%f,\"longitude\":%f,\"date\":null}";

    @Override protected Void doInBackground(Object... params) {
        String deviceId = (String) params[0];
        double latitude = (double) params[1];
        double longitude = (double) params[2];

        OutputStream requestBodyOutputStream = null;
        InputStream responseErrorInputStream = null;

        try {
            URL url = new URL("https://busy-place.herokuapp.com/api/position");
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");

            String json = String.format(jsonFormat, deviceId, latitude, longitude);

            requestBodyOutputStream = connection.getOutputStream();
            requestBodyOutputStream.write(json.getBytes());

            int statusCode = connection.getResponseCode();
            if (statusCode < 200 && statusCode >= 400) {
                String response;

                responseErrorInputStream = connection.getErrorStream();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                int length;
                byte[] buffer = new byte[1024 * 4];

                while ((length = responseErrorInputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, length);
                }

                response = byteArrayOutputStream.toString();
                throw new RuntimeException(statusCode + " - " + connection.getResponseMessage() + " - " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (requestBodyOutputStream != null) {
                try {
                    requestBodyOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (responseErrorInputStream != null) {
                try {
                    responseErrorInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

}
