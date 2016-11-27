package com.example.yuesh.sshmaster;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.content.SharedPreferences.Editor;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;


public class MainActivity extends AppCompatActivity {
    private static String create = "已创建";
    String jiancha = "已检查";
    ActionBar actionBar;
    ListView listView;
    SQLiteDatabase db;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById (R.id.activity_main_ListView);
        actionBar = getSupportActionBar();
        db = SQLiteDatabase.openOrCreateDatabase(this.getFilesDir().toString()+"/my.db3",null);
        sharedPreferences = getSharedPreferences("count", MODE_PRIVATE);
        boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);
        Editor editor = sharedPreferences.edit();
        if (isFirstRun)
        {
            editor.putBoolean("isFirstRun", false);
            editor.commit();
            db.execSQL("create table hosts_table( _id integer" +
                    "primary key," +
                    "host_name varchar(50)," +
                    "host_port varchar(50)," +
                    "host_username varchar(50)," +
                    "host_userpasswd varchar(50));");
            Log.d("debug", "第一次运行");
            finish();
        }
        else
        {
            Log.d("debug", "不是第一次运行");
        }
        shows();

    }
    public void open(View v)
    {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this,Add.class);
        startActivity(intent);
    }
    public void show(View view)
    {
        shows();
    }
    public void shows()
    {
        final Cursor cursor = db.rawQuery("select * from hosts_table",null);
        final SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                MainActivity.this,
                R.layout.listview_layout,
                cursor,new String[] {"_id","host_name","host_username"},
                new int[] {R.id.my_host_id,R.id.my_host_name,R.id.my_host_username},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
                );
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id){

                final String txt =((TextView)view.findViewById(R.id.my_host_name)).getText().toString();
                new AlertDialog.Builder(MainActivity.this)
                .setTitle("对主机进行操作")
                .setItems(new String[] {"连接","删除"},new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(which==1)
                        {
                            Toast.makeText(MainActivity.this, "已删除"+txt, Toast.LENGTH_LONG).show();
                            db.execSQL("delete from hosts_table where host_name = "+"'"+txt+"'");
                            shows();
                        }
                        else if(which == 0)
                        {
                            Intent intent = new Intent();
                            intent.setClass(MainActivity.this,Conn.class);
                            intent.putExtra("host_name",txt);
                            startActivity(intent);
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, "别瞎几把点", Toast.LENGTH_LONG).show();
                        }


                    }
                })
                .show();
            }


        });





    }


}
