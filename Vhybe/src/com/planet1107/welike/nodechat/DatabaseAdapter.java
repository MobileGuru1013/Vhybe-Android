package com.planet1107.welike.nodechat;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseAdapter {

    public static final String DB_NAME = "vhybe.db";

    // public static final String KEY_ROWID = "id";
    public static final int DB_VERSION = 1;
    public static SharedPreferences sp;
    public static final String MY_PREF = "MyPreferences";
    Context context;

   // public static final String DB_TABLE_ChatDetail = "CREATE TABLE chatDetail (pkChatId INTEGER primary key autoincrement,fromUser ,toUser , message,imgUrl)";
    public static final String DB_TABLE_ChatDetail ="CREATE TABLE tbl_chat_detail(chat_detail_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,to_user_id	INTEGER NOT NULL , from_user_id	INTEGER NOT NULL,message TEXT NOT NULL, isImage	INTEGER DEFAULT 0,unread INTEGER NOT NULL DEFAULT 0, userid1_isDeleted	INTEGER NOT NULL DEFAULT 0, userid2_isDeleted INTEGER NOT NULL DEFAULT 0, isDeleted_user1 INTEGER NOT NULL DEFAULT 0, isDeleted_user2 INTEGER NOT NULL DEFAULT 0, created_date	timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,  server_date	timestamp)";
    private static class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_TABLE_ChatDetail);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {



        }

        @Override
        public void onOpen(SQLiteDatabase db) {
            super.onOpen(db);
        }
    }

    private DatabaseHelper dbh;
    private SQLiteDatabase db;

    public DatabaseAdapter(Context context) {
        try {
            this.context = context;

            dbh = new DatabaseHelper(context);
            sp = context.getSharedPreferences(MY_PREF, 0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DatabaseAdapter open() throws SQLException {
        try {
            db = dbh.getWritableDatabase();
            return this;
        } catch (Exception e) {
        }
        return this;
    }

    public void close() {

        dbh.close();
    }

    // Testing custome load



    public long insertChat(int to_user_id,int from_user_id,String message,String isImage ,String server_date) {

        ContentValues initialValues = new ContentValues();
        initialValues.put("to_user_id", to_user_id);
        initialValues.put("from_user_id", from_user_id);
        initialValues.put("message", message);
        initialValues.put("isImage", isImage);
        initialValues.put("server_date", server_date);     
        return db.insert("tbl_chat_detail", null, initialValues);
    }



    public Cursor getchat(int currntUser,int otherUser) {       
        Cursor cursor = null;
        cursor = db.rawQuery("SELECT * FROM tbl_chat_detail WHERE (to_user_id='"+currntUser+"' AND  from_user_id='"+otherUser+"') OR (to_user_id='"+otherUser+"' AND  from_user_id='"+currntUser+"') ORDER BY server_date ASC", null);
        return cursor;
    }
    
    public String  getlastupdateDate() {       
        Cursor cursor = null;
        cursor = db.rawQuery("SELECT server_date FROM tbl_chat_detail ORDER BY server_date DESC", null);
        if (cursor.moveToFirst()){
			do{
				String date =cursor.getString(0);	
				cursor.close();
				return date;
			}while(cursor.moveToNext());
		}
		cursor.close();
		return "2015-06-10";
    }
    
    public void clean() {
        db.execSQL("delete from tbl_chat_detail");
    }
}
