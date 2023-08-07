package com.example.musify;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context){
        super(context,"songlist.db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase myDB) {
        myDB.execSQL("create Table Songs(name Text primary key,liked Boolean)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase myDB, int oldVersion, int newVersion) {
        myDB.execSQL("drop Table if exists Songs");
    }
    public Boolean insertData(String name){
        SQLiteDatabase myDB=this.getWritableDatabase();
        ContentValues contentValues =new ContentValues();
        contentValues.put("Songname",name);
        long result =myDB.insert("Songs",null,contentValues);
        if (result==-1){
            return false;
        }
        else {
            return true;
        }
    }

    public boolean checksong(String songname){
        SQLiteDatabase myDB =this.getWritableDatabase();
        Cursor cursor=myDB.rawQuery("select * from Songs where name=?",new String[]{songname});
        if (cursor.getCount()>0){
            return true;
        }
        else {
            return false;
        }
    }
@SuppressLint("Range")
public ArrayList<String>Songlist(){
        ArrayList <String> list =new ArrayList<>();
        SQLiteDatabase myDB =this.getReadableDatabase();
    String query ="SELECT * FROM Songs ";
        Cursor cursor =myDB.rawQuery(query,null);
    if (cursor!=null){
      if (cursor.moveToFirst()){
          do {  list.add(cursor.getString(cursor.getColumnIndex("Songname")));
          }while (cursor.moveToNext());
      }
    }
return list;
    }
}
