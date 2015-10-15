package com.it.core.network;

import android.os.AsyncTask;
import android.util.Log;

import com.it.core.service.OnExecuteCompleted;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
* Класс отправки файла на сервер
*/
public class FileUploaderAsync extends AsyncTask<String, Void, String> {

    private OnExecuteCompleted listener;

    public FileUploaderAsync(OnExecuteCompleted listener){
        this.listener = listener;
    }

    public void setOnExecuteCompletedListener(OnExecuteCompleted listener){
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... params) {
        return sendFileToServer(params[0], params[1]);
        }

    @Override
    protected void onPostExecute(String result) {
        if(listener != null){
            listener.OnCompleted(result);
        }
    }

    private String sendFileToServer(String filename, String targetUrl) {
        String response = "error";
        Log.e("Upload filename", filename);
        Log.e("url", targetUrl);
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;

        String fileExtension = filename.substring(filename.lastIndexOf(".") + 1);
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024;
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(
                    filename));

            URL url = new URL(targetUrl);
            connection = (HttpURLConnection) url.openConnection();

            // Allow Inputs & Outputs
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setChunkedStreamingMode(1024);
            // Enable POST method
            connection.setRequestMethod("POST");

            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Accept-Charset", "UTF-8");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);

            String connstr = null;
            connstr = "Content-Disposition: form-data; name=\"uploadedfile\";filename=\""
                    + "uploadedfile."+ fileExtension + "\"" + lineEnd;
            Log.i("Connstr", connstr);

            outputStream.writeBytes(connstr);
            outputStream.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // Read file
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            Log.e("Image length", bytesAvailable + "");
            try {
                while (bytesRead > 0) {
                    try {
                        outputStream.write(buffer, 0, bufferSize);
                    } catch (OutOfMemoryError e) {
                        e.printStackTrace();
                        response = "outofmemoryerror";
                        return response;
                    }
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }
            } catch (Exception e) {
                e.printStackTrace();
                response = "error";
                return response;
            }
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens
                    + lineEnd);

            // Responses from the server (code and message)
            int serverResponseCode = connection.getResponseCode();
            String serverResponseMessage = connection.getResponseMessage();
            Log.i("Server Response Code ", "" + serverResponseCode);
            Log.i("Server Response Message", serverResponseMessage);

            if (serverResponseCode == 200) {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuffer serverResponse = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    serverResponse.append(inputLine);
                }
                in.close();
                response = serverResponse.toString();
            }

            fileInputStream.close();
            outputStream.flush();
            outputStream.close();
            outputStream = null;
        } catch (Exception ex) {
            // Exception handling
            response = "error";
            Log.e("Send file Exception", ex.getMessage() + "");
            ex.printStackTrace();
        }
        return response;
    }
}