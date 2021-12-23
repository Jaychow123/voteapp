package com.example.votegvp;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class ViewPartiesActivity extends AppCompatActivity {
    DBConnection controllerdb = new DBConnection(this);
    SQLiteDatabase db;

    private ArrayList<String> Name = new ArrayList<String>();
    private ArrayList<String> CName = new ArrayList<String>();

    ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_parties);
        lv = (ListView) findViewById(R.id.lvParties);
    }
    @Override
    protected void onResume() {
        displayData();
        super.onResume();
    }
    private void displayData() {
        db = controllerdb.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM  Parties",null);

        Name.clear();
        CName.clear();

        if (cursor.moveToFirst()) {
            do {
                Name.add(cursor.getString(cursor.getColumnIndex("name")));
                CName.add(cursor.getString(cursor.getColumnIndex("candidate")));
            } while (cursor.moveToNext());
        }
        CustomAdapter ca = new CustomAdapter(ViewPartiesActivity.this, Name,CName);
        lv.setAdapter(ca);
        //code to set adapter to populate list
        cursor.close();
    }
}