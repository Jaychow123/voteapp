package com.example.votegvp;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class ResultsAdminActivity extends AppCompatActivity {

    DBConnection controllerdb = new DBConnection(this);
    SQLiteDatabase db;
    private ArrayList<String> names = new ArrayList<String>();
    private ArrayList<String> partyCounts = new ArrayList<String>();
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_admin);
        lv = (ListView) findViewById(R.id.lvresults);
    }

    protected void onResume() {
        displayData();
        super.onResume();
    }

    private void displayData() {
        db = controllerdb.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT partyname,count(partyname) FROM  Votes group by partyname order by count(partyname) desc", null);

        names.clear();
        partyCounts.clear();

        if (cursor.moveToFirst()) {
            do {
                names.add(cursor.getString(cursor.getColumnIndex("partyname")));
                partyCounts.add(cursor.getString(cursor.getColumnIndex("count(partyname)")));
            } while (cursor.moveToNext());
        }
        CustomAdapter ca = new CustomAdapter(ResultsAdminActivity.this, names, partyCounts);
        lv.setAdapter(ca);
        //code to set adapter to populate list
        cursor.close();
    }
}