package com.example.yuesh.test;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.ByteArrayOutputStream;
import java.util.Properties;

public class MainActivity extends AppCompatActivity {
    TextView textView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.activity_main_textview);
        textView.setText("qunima");
        test haha = new test();
        haha.execute();
    }

    public class test extends AsyncTask<String, Void, String> {
        protected void onPreExecute() {
            Log.i("debug", "onPreExecute() called");
            textView.setText("loading...");
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                return executeRemoteCommand("sajia", "yue1995", "192.168.253.128", 22);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            textView.setText(result);
        }

        public String executeRemoteCommand(String username, String password, String hostname, int port)
                throws Exception {
            JSch jsch = new JSch();
            Session session = jsch.getSession(username, hostname, port);
            session.setPassword(password);

            // Avoid asking for key confirmation
            Properties prop = new Properties();
            prop.put("StrictHostKeyChecking", "no");
            session.setConfig(prop);

            session.connect();

            // SSH Channel
            ChannelExec channelssh = (ChannelExec)
                    session.openChannel("exec");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            channelssh.setOutputStream(baos);

            // Execute command
            channelssh.setCommand("touch test.txt");
            channelssh.connect();
            channelssh.disconnect();

            return baos.toString();
        }

    }
}

