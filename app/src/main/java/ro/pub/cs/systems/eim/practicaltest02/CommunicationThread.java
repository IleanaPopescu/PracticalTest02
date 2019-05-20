package ro.pub.cs.systems.eim.practicaltest02;

import android.provider.SyncStateContract;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.protocol.HTTP;

public class CommunicationThread extends Thread {

    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }


    @Override
    public void run() {
        if (socket == null) {
            return;
        }
        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                return;
            }
            String stringClient = bufferedReader.readLine();
            if (stringClient == null || stringClient.isEmpty()) {
                return;
            }
            HashMap<String, WunderGround> data = serverThread.getData();
            WunderGround afis = null;
            if (data.containsKey(stringClient)) {
                afis = data.get(stringClient);
            } else {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://autocomplete.wunderground.com/aq?query=");
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("query", stringClient));
                UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                httpPost.setEntity(urlEncodedFormEntity);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String pageSourceCode = httpClient.execute(httpPost, responseHandler);
                if (pageSourceCode == null) {
                    return;
                }
                Document document = Jsoup.parse(pageSourceCode);
                Element element = document.child(0);
                Elements elements = element.getElementsByTag("name");
                for (Element script: elements) {
                    String scriptData = script.data();
                    if (scriptData.contains(stringClient)) {
                        int position = scriptData.indexOf(stringClient) + stringClient.length();
                        scriptData = scriptData.substring(position);
                        JSONObject content = new JSONObject(scriptData);
                        JSONObject currentObservation = content.getJSONObject("current_observation");
                        String temperature = currentObservation.getString("name");

                        afis = new WunderGround();
                        serverThread.setData(stringClient, afis);
                        break;
                    }
                }
            }
            if (afis == null) {
                return;
            }

            printWriter.println(afis);
            printWriter.flush();
        } catch (IOException ioException) {
            if (true) {
                ioException.printStackTrace();
            }
        } catch (JSONException jsonException) {
            if (true) {
                jsonException.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    if (true) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }





}
