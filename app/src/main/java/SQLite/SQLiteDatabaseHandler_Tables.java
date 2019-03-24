package SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import objects.ObjPrinter;

public class SQLiteDatabaseHandler_Tables extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "TablesDB";
    private static final String TABLE_NAME = "Tables";
    private static final String KEY_ID = "id";
    private static final String KEY_TABLES = "tables";

    private static final String[] COLUMNS = { KEY_ID, KEY_TABLES };

    public SQLiteDatabaseHandler_Tables(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATION_TABLE = "CREATE TABLE Tables ( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "tables INTEGER)";

        db.execSQL(CREATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(db);
    }

    public int getTables() {
        String query = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
       int iTables = -1;

        if (cursor.moveToFirst()) {
            do {
                iTables = Integer.parseInt(cursor.getString(1));

            } while (cursor.moveToNext());
        }

        return iTables;
    }

    public void setTables(int p_iTables) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TABLES, p_iTables);

        // insert
        db.insert(TABLE_NAME,null, values);
        db.close();
    }

    public int updateTables(int p_iTables) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TABLES, p_iTables);

        int i = db.update(TABLE_NAME, // table
                values, // column/value
                "tables = ?", // selections
                new String[] { String.valueOf(p_iTables) });

        db.close();

        return i;
    }

    public boolean isDatabaseEmpty(){
        String query = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.getCount() == 0){
            return true;
        }
        return false;
    }
}
