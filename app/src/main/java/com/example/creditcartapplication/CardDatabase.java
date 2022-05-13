package com.example.creditcartapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.widget.Toast;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    public Boolean exportCardDatabase(String username) {

        String filename = username + "-card.csv";
        File exportDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        if(!exportDir.exists()) {
            exportDir.mkdirs();
        }

        File file = new File(exportDir, filename);

        try {
            if(!file.exists()) {
                file.createNewFile();

            } else {
                file.delete();
                file.createNewFile();
            }

            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor curCSV = db.rawQuery("SELECT * FROM cards",null);
            csvWrite.writeNext(curCSV.getColumnNames());
            while(curCSV.moveToNext())
            {
                //Which column you want to exprort
                String arrStr[] ={Integer.toString(curCSV.getInt(0)),curCSV.getString(1),
                        curCSV.getString(2), curCSV.getString(3), curCSV.getString(4),
                        curCSV.getString(5), Integer.toString(curCSV.getInt(6))};
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();

            Toast.makeText(mContext.getApplicationContext(), "Zapisano!",
                    Toast.LENGTH_LONG).show();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Boolean importCardDatabase(String username) {

        String filename = username + "-card.csv";
        File exportDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        File file = new File(exportDir, filename);

        try {

            CSVReader csvReader = new CSVReader(new FileReader(file));
            String[] cardRecord = csvReader.readNext(); // Odczytanie pierwszego wiersza z nagłówkami
            while((cardRecord = csvReader.readNext()) != null) {
                insertCardsData(cardRecord[1], cardRecord[2], cardRecord[3],
                        cardRecord[4], cardRecord[5], Integer.parseInt(cardRecord[6]));
            }

            Toast.makeText(mContext.getApplicationContext(), "Zapisano!",
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    public Cursor readCardsFromDBForContentProvider() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from cards", null);
    }
}
