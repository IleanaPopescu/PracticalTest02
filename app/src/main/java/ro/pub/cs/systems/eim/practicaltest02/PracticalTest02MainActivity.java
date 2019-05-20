package ro.pub.cs.systems.eim.practicaltest02;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PracticalTest02MainActivity extends AppCompatActivity {


    private EditText serverPortEditText = null;
    private Button getButton = null;
    private EditText stringEditText = null;
    private TextView afisTextView = null;
    private Button serverButton = null;

    private ServerThread serverThread = null;
    private ClientThread clientThread = null;



    private ConnectButtonClickListener connectButtonClickListener = new ConnectButtonClickListener();
    private class ConnectButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            String serverPort = serverPortEditText.getText().toString();
            if (serverPort == null || serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server port should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            serverThread = new ServerThread(Integer.parseInt(serverPort));
            if (serverThread.getServerSocket() == null) {
                return;
            }
            serverThread.start();
        }

    }




    private GetStringButtonClickListener getStringButtonClickListener = new GetStringButtonClickListener();
    private class GetStringButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            String serverPort = serverPortEditText.getText().toString();
            String clientString = stringEditText.getText().toString();
            if (clientString == null || clientString.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Client connection parameters should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] There is no server to connect to!", Toast.LENGTH_SHORT).show();
                return;
            }

            afisTextView.setText("");

            clientThread = new ClientThread(clientString);
            clientThread.start();
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02_main);


        serverPortEditText = (EditText)findViewById(R.id.portServer);
        getButton = (Button)findViewById(R.id.buttonGet);
        getButton.setOnClickListener(getStringButtonClickListener);

        serverButton = (Button)findViewById(R.id.buttonServer);
        getButton.setOnClickListener(connectButtonClickListener);


        stringEditText = (EditText)findViewById(R.id.stringClient);
        afisTextView = (TextView)findViewById(R.id.afis);

    }

    @Override
    protected void onDestroy() {
        if (serverThread != null) {
            serverThread.stopThread();
        }
        super.onDestroy();
    }
}
