package ro.pub.cs.systems.eim.practicaltest02;

import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread extends Thread{

    private String clientString;
    private TextView afis;
    private Socket socket;

    public ClientThread(String clientString) {
        this.clientString = clientString;
    }


    @Override
    public void run() {
        try {
            socket = new Socket();
            if (socket == null) {
                return;
            }
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                return;
            }
            printWriter.println(clientString);
            printWriter.flush();
            String afisInformation;
            while ((afisInformation = bufferedReader.readLine()) != null) {
                final String finalizedAfis = afisInformation;
                afis.post(new Runnable() {
                    @Override
                    public void run() {
                        afis.setText(finalizedAfis);
                    }
                });
            }
        } catch (IOException ioException) {
            if (true) {
                ioException.printStackTrace();
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
