package SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import global.GlobVar;
import objects.ObjCategory;
import objects.ObjPrinter;
import objects.ObjSession;

public class SQLiteDatabaseHandler_Session extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "SessionDB";
    private static final String TABLE_NAME = "Session";
    private static final String KEY_ID = "id";
    private static final String KEY_CASHIERNAME = "cashiername";
    private static final String KEY_HOSTNAME = "hostname";
    private static final String KEY_PARTYNAME = "partyname";
    private static final String KEY_PARYTDATE = "partydate";

    private static final String[] COLUMNS = { KEY_ID, KEY_CASHIERNAME, KEY_HOSTNAME, KEY_PARTYNAME,
            KEY_PARYTDATE };

    public SQLiteDatabaseHandler_Session(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATION_TABLE = "CREATE TABLE Session ( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "cashiername TEXT, "
                + "hostname TEXT, " + "partyname TEXT, " + "partydate TEXT)";

        db.execSQL(CREATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(db);
    }

    public void getSession() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, // a. table
                COLUMNS, // b. column names
                " id = ?", // c. selections
                new String[] { String.valueOf(0) }, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit

        if (cursor != null)
            cursor.moveToFirst();

        ObjSession objSession = new ObjSession();
        objSession.setCashierName(cursor.getString(0));
        objSession.setHostName(cursor.getString(1));
        objSession.setPartyName(cursor.getString(2));
        objSession.setPartyDate(cursor.getString(3));

        //save globally
        GlobVar.g_ObjSession = objSession;
    }

    public void saveSession() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CASHIERNAME, GlobVar.g_ObjSession.getCashierName());
        values.put(KEY_HOSTNAME, GlobVar.g_ObjSession.getHostName());
        values.put(KEY_PARTYNAME, GlobVar.g_ObjSession.getPartyName());
        values.put(KEY_PARYTDATE, GlobVar.g_ObjSession.getPartyDate());

        // insert
        db.insert(TABLE_NAME,null, values);
        db.close();
    }

    public void deleteSession() {
        // Get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "id = ?", new String[] { String.valueOf(0) });
        db.close();
    }
}
