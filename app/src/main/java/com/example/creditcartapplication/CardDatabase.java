package com.example.creditcartapplication;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class CardDatabase extends SQLiteOpenHelper {

    public static final String databaseName = "card.db";
    public static String finalDatabaseName;
    public static String packageName = null;
    public static String currentDBPath;
    public Context mContext;

    public CardDatabase(Context context, String username) {
        super(context, username + "." + databaseName, null, 1);
        mContext = context;
        packageName = context.getPackageName();
        finalDatabaseName = username + "." + databaseName;
        currentDBPath = "//data//" + packageName + "//databases//" + finalDatabaseName;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table cards(id Integer primary key, bankName TEXT, " +
                "firstName TEXT, " +
                "lastName TEXT, " +
                "cardNumber TEXT, " +
                "validThru TEXT," +
                "CVC Integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists cards");
    }

    public Boolean insertCardsData(String bankName, String firstName, String lastName,
                                   String cardNumber, String ValidThru, int CVC) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("bankName", bankName);
        values.put("firstName", firstName);
        values.put("lastName", lastName);
        values.put("cardNumber", cardNumber);
        values.put("ValidThru", ValidThru);
        values.put("CVC", CVC);

        long result = db.insert("cards", null, values);
        return result != -1;
    }

    public Boolean updateCardData(String bankName, String firstName, String lastName,
                                  String cardNumber, String ValidThru, int CVC) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("bankName", bankName);
        values.put("firstName", firstName);
        values.put("lastName", lastName);
        values.put("cardNumber", cardNumber);
        values.put("ValidThru", ValidThru);
        values.put("CVC", CVC);

        long result = db.update("cards", values, "cardNumber=?",
                new String[]{cardNumber});
        return result != -1;
    }

    public Boolean deleteCard(String cardNumber) {
        SQLiteDatabase db = this.getWritableDatabase();

        long result = db.delete("cards", "cardNumber=?",
                new String[]{cardNumber});
        return result != -1;
    }

    public Boolean doesCardExist(String cardNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from cards where cardNumber=?",
                new String[] {cardNumber});
        Boolean result = cursor.getCount() > 0;
        cursor.close();
        return result;
    }

    public ArrayList<Card> readCardsFromDB() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from cards", null);

        ArrayList<Card> cardsList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                cardsList.add(new Card(cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getInt(6)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return cardsList;
    }

    public Boolean exportCardDatabase() {

        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_TITLE, "invoice.pdf");

        

        return true;
    }
}
