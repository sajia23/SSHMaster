package com.example.yuesh.sshmaster;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.trilead.ssh2.Connection;
import com.trilead.ssh2.Session;
import com.trilead.ssh2.StreamGobbler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class Conn extends AppCompatActivity {
    String host_name;
    String username;
    String userpasswd;
    Button connect;
    Button send;
    SQLiteDatabase db;
    Connection conn;
    com.trilead.ssh2.Session sess;
    EditText editText;
    TextView textView;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conn);
        Intent intent = getIntent();
        host_name = intent.getStringExtra("host_name");
        conn = new Connection(host_name);
        Toast.makeText(Conn.this, host_name, Toast.LENGTH_LONG).show();
        connect = (Button)findViewById(R.id.tee_button);
        send = (Button)findViewById(R.id.tee_send);
        db = SQLiteDatabase.openOrCreateDatabase(this.getFilesDir().toString()+"/my.db3",null);
        editText = (EditText) findViewById(R.id.tee_editText);
        textView = (TextView) findViewById(R.id.tee_textview);
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
        send.setEnabled(false);
        cursor = db.rawQuery("select * from hosts_table where host_name =?",new String[]{host_name});
        if(cursor.moveToFirst())//Move the cursor to the first row. This method will return false if the cursor is empty.
        {
            username=cursor.getString(3);
            userpasswd=cursor.getString(4);
        }
        cursor.close();
    }
    public void connect(View view) throws IOException {
        if(connect.getText().toString().equals("连接")) {
            Connect haha = new Connect();
            haha.execute(host_name, username, userpasswd);
        }
        else{
            disconn haha = new disconn();
            haha.execute();
        }
    }
    public void send (View view) throws IOException
    {
        String cmd = editText.getText().toString();
        Session getresult = new Session();
        getresult.execute(cmd);
    }
    public class Connect extends AsyncTask<String,Void,Integer>
    {
        @Override
        protected void onPreExecute() {
            Log.i("debug", "onPreExecute() called");
            textView.setText("loading...");
        }
        protected Integer doInBackground(String... params) {
            try {
                return executeRemoteCommand(params[1], params[2], params[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Integer result) {
            Log.i("debug", "onPostExecute(Result result) called");
            if(result == 1) {
                textView.setText("连接成功");
                send.setEnabled(true);
                connect.setText("断开");
            }
            else
            {
                textView.setText("连接未成功");
            }
        }
        public Integer executeRemoteCommand(String username, String password, String hostname)
                throws Exception {
            conn.connect();
            Log.i("debug", ".connect执行完成");
            boolean isAuthenticated = conn.authenticateWithPassword(username, password);
            if (isAuthenticated == false)
                throw new IOException("Authentication failed.");
            else
            {
                Log.i("debug", "连上了");

                return 1;
            }
        }
    }
    public class Session extends AsyncTask<String,Void,String>
    {
        @Override
        protected void onPreExecute() {
            Log.i("debug", "onPreExecute() called");
            textView.setText("正在发送命令");
        }
        protected String doInBackground(String... params) {
            try {
                return executeRemoteCommand(params[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            Log.i("debug", "onPostExecute(Result result) called");
            textView.setText(result);
        }
        protected String executeRemoteCommand(String cmd) throws IOException {
            sess = conn.openSession();
            sess.execCommand(cmd);
            InputStream stdout = new StreamGobbler(sess.getStdout());
            BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
            StringBuilder stringBuilder = new StringBuilder("");
            stringBuilder.append(username);
            stringBuilder.append("@");
            stringBuilder.append(host_name);
            stringBuilder.append(":"+cmd+"\n");
            while (true)
            {
                String line = br.readLine();
                if (line == null)
                    break;
                Log.i("debug", line);
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }
            String result = stringBuilder.toString();
            sess.close();
            return result;
        }
    }
    public class disconn extends AsyncTask<Void,Void,Integer>
    {
        @Override
        protected void onPreExecute() {
            Log.i("debug", "disconn_onPreExecute() called");
            textView.setText("正在断开");
        }
        @Override
        protected Integer doInBackground(Void... params) {
            try {
                conn.close();

                    return 1;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(Integer result) {
            Log.i("debug", "disconn_onPostExecute(Result result) called");
            if(result == 1) {
                textView.setText("已断开");
                send.setEnabled(false);
            }
            else
                textView.setText("未断开");
        }
    }
}
