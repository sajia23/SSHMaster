package com.example.yuesh.sshmaster;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.trilead.ssh2.Connection;
import com.trilead.ssh2.Session;
import com.trilead.ssh2.StreamGobbler;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class Conn extends AppCompatActivity {
    String host_name;
    String host_username;
    String host_userpasswd;
    Button connect;
    Button send;
    SQLiteDatabase db;
    Connection conn;
    Session sess;
    EditText editText;
    TextView textView;

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
        send.setEnabled(false);
    }
    public void connect(View view) throws IOException {
        Cursor cursor = db.rawQuery("select from hosts_table where"+"'"+host_name+"'",null);
        host_username = cursor.getString(3);
        host_userpasswd = cursor.getString(4);
        if(connect.getText().toString()=="连接")
        {
            conn.connect();
            boolean isAuthenticated = conn.authenticateWithPassword(host_username, host_userpasswd);
            if (isAuthenticated == false)
                throw new IOException("Authentication failed.");

            else
            {
                sess = conn.openSession();
                connect.setText("断开");
                textView.setText("已连接");
                send.setEnabled(true);
            }
        }
        else if(connect.getText().toString()=="断开")
        {
            sess.close();
            conn.close();
        }

    }
    public void send (View view) throws IOException
    {
        String cmd = editText.getText().toString();
        sess.execCommand(cmd);
        InputStream stdout = new StreamGobbler(sess.getStdout());
        BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
        String line = "";
        while (true)
        {
            line +=  br.readLine();
            if (line == null)
                break;
        }
        textView.setText(line);
    }
}
