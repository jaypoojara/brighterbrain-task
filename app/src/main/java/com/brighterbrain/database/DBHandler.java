package com.brighterbrain.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.brighterbrain.model.Item;
import com.brighterbrain.model.ItemImage;
import com.brighterbrain.util.Constants;

import java.util.ArrayList;

/**
 * Created by jaypoojara on 21-04-2018.
 */
public class DBHandler extends SQLiteOpenHelper {
    private static final String DB_NAME = "brighter_brain.db";
    private static final int DB_VERSION = 1;

    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE items (id INTEGER PRIMARY KEY AUTOINCREMENT" +
                ", name TEXT" +
                ", description Text" +
                ", latitude TEXT" +
                ", longitude TEXT" +
                ", cost TEXT)");


        sqLiteDatabase.execSQL("CREATE TABLE item_images (id INTEGER PRIMARY KEY AUTOINCREMENT" +
                ", image_path TEXT" +
                ", item_id INTEGER,FOREIGN KEY (item_id) REFERENCES items (id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public ArrayList<Item> getAllItems() {
        String sql = "select * from items";
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(sql, null);
        cursor.moveToFirst();
        ArrayList<Item> items = new ArrayList<>();
        for (int i = 0; i < cursor.getCount(); i++) {
            Item item = new Item();
            item.setName(cursor.getString(cursor.getColumnIndex(Constants.NAME)));
            item.setDescription(cursor.getString(cursor.getColumnIndex(Constants.DESCRIPTION)));
            item.setCost(cursor.getInt(cursor.getColumnIndex(Constants.COST)));
            item.setLatitude(cursor.getString(cursor.getColumnIndex(Constants.LATITUDE)));
            item.setLongitude(cursor.getString(cursor.getColumnIndex(Constants.LONGITUDE)));
            item.setId(cursor.getInt(cursor.getColumnIndex(Constants.ID)));

            Cursor imageCursor = database.rawQuery("Select * from item_images where item_id = " + cursor.getInt(cursor.getColumnIndex(Constants.ID)), null);
            imageCursor.moveToFirst();

            ArrayList<ItemImage> images = new ArrayList<>();
            for (int j = 0; j < imageCursor.getCount(); j++) {
                ItemImage itemImage = new ItemImage();
                itemImage.setId(imageCursor.getInt(imageCursor.getColumnIndex(Constants.ID)));
                itemImage.setImagePath(imageCursor.getString(imageCursor.getColumnIndex(Constants.IMAGE_PATH)));
                images.add(itemImage);
                imageCursor.moveToNext();
            }

            item.setItemImages(images);
            items.add(item);
            cursor.moveToNext();
        }
        cursor.close();
        database.close();
        return items;
    }

    public long insertRecord(String table, ContentValues contentValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        long id = database.insert(table, null, contentValues);
        database.close();
        return id;
    }


    public void updateRecord(String table, String whereClaus, ContentValues contentValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        long id = database.update(table, contentValues, whereClaus, null);
        database.close();
    }

    public void deleteRecord(String table, String whereClause) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(table, whereClause, null);
        database.close();
    }
}
