package com.example.yuesh.sshmaster;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class Add extends AppCompatActivity {
    int n = 1000000;
    int nums = 1;
    ActionBar actionBar;
    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        db = SQLiteDatabase.openOrCreateDatabase(this.getFilesDir().toString()+"/my.db3",null);
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.activity_add, menu);
//        return true;
//    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void save(View view)
    {
        String host_name = ((EditText)findViewById( R.id.editText_hostname)).getText().toString();
        for (long i = 0; i < n; i++)
        {
            if (host_name == null || host_name.length() <= 0)
            {
                Toast.makeText(this, "别瞎几把填", Toast.LENGTH_LONG).show();
                return;
            }
        }
        String host_port = ((EditText)findViewById( R.id.editText_port)).getText().toString();
        String host_username = ((EditText)findViewById( R.id.editText_username)).getText().toString();
        String host_userpasswd = ((EditText)findViewById( R.id.editText_userpasswd)).getText().toString();
        insertData( db, host_name, host_port, host_username, host_userpasswd );
        Add.this.finish();
    }
    public void insertData(SQLiteDatabase db, String host_name, String host_port, String host_username, String host_userpasswd)
    {
        db.execSQL("insert into hosts_table (_id,host_name,host_port,host_username,host_userpasswd) values(null,'"+host_name+"','"+host_port+"','"+host_username+"','"+host_userpasswd+"')");
        nums ++;
    }
}
