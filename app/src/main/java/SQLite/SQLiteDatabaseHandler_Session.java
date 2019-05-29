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
import objects.ObjProduct;
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
    private static final String KEY_USEMAINCASH = "usemaincash";
    private static final String KEY_USESYNCBON = "usesyncbon";
    private static final String KEY_PRINTERMAC = "printermac";

    private static final String[] COLUMNS = { KEY_ID, KEY_CASHIERNAME, KEY_HOSTNAME, KEY_PARTYNAME,
            KEY_PARYTDATE, KEY_USEMAINCASH, KEY_USESYNCBON, KEY_PRINTERMAC };

    public SQLiteDatabaseHandler_Session(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATION_TABLE = "CREATE TABLE Session ( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "cashiername TEXT, "
                + "hostname TEXT, " + "partyname TEXT, " + "partydate TEXT, " + "usemaincash INTEGER, "
                + "usesyncbon INTEGER, " + "printermac TEXT )";

        db.execSQL(CREATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(db);
    }

    public void getSession() {
        String query = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                ObjSession objSession = new ObjSession();
                objSession.setCashierName(cursor.getString(1));
                objSession.setHostName(cursor.getString(2));
                objSession.setPartyName(cursor.getString(3));
                objSession.setPartyDate(cursor.getString(4));

                //set usemaincash
                boolean b_UseMainCash = true;
                if (cursor.getString(5).equals("0")) {
                    b_UseMainCash = false;
                }
                GlobVar.g_bUseMainCash = b_UseMainCash;

                //set usesyncbon
                boolean b_UseSyncBon = true;
                if (cursor.getString(6).equals("0")) {
                    b_UseSyncBon = false;
                }
                GlobVar.g_bUseSyncBon = b_UseSyncBon;

                //set printer
                for(ObjPrinter objprinter : GlobVar.g_lstPrinter){
                    if(objprinter.getMacAddress().equals(cursor.getString(7))){
                        GlobVar.g_objPrinter = objprinter;
                    }
                }

                //save globally
                if (GlobVar.g_ObjSession == null) {
                    GlobVar.g_ObjSession = objSession;
                }
            } while (cursor.moveToNext());
        }
    }

    public void saveSession() {
        String query = "SELECT COUNT(*) FROM " + TABLE_NAME;
        SQLiteDatabase db_read = this.getWritableDatabase();
        Cursor cursor = db_read.rawQuery(query, null);


        //session already saved --> update
        if(cursor != null && cursor.moveToFirst() && cursor.getInt (0) != 0){
            db_read.close();

            SQLiteDatabase db_write = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_CASHIERNAME, GlobVar.g_ObjSession.getCashierName());
            values.put(KEY_HOSTNAME, GlobVar.g_ObjSession.getHostName());
            values.put(KEY_PARTYNAME, GlobVar.g_ObjSession.getPartyName());
            values.put(KEY_PARYTDATE, GlobVar.g_ObjSession.getPartyDate());

            int key_usemaincash = GlobVar.g_bUseMainCash ? 1 : 0;
            values.put(KEY_USEMAINCASH, key_usemaincash);

            int key_usesyncbon = GlobVar.g_bUseSyncBon ? 1 : 0;
            values.put(KEY_USESYNCBON, key_usesyncbon);

            //write printer
            if(GlobVar.g_objPrinter != null){
                values.put(KEY_PRINTERMAC, GlobVar.g_objPrinter.getMacAddress());
            }
            else{
                values.put(KEY_PRINTERMAC, "");
            }


            int i = db_write.update(TABLE_NAME, // table
                    values, // column/value
                    "id = ? ", // selections
                    new String[] { String.valueOf(1) });

            db_write.close();
        }
        //write session
        else{
            db_read.close();

            SQLiteDatabase db_write = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_CASHIERNAME, GlobVar.g_ObjSession.getCashierName());
            values.put(KEY_HOSTNAME, GlobVar.g_ObjSession.getHostName());
            values.put(KEY_PARTYNAME, GlobVar.g_ObjSession.getPartyName());
            values.put(KEY_PARYTDATE, GlobVar.g_ObjSession.getPartyDate());

            int key_usemaincash = GlobVar.g_bUseMainCash ? 1 : 0;
            values.put(KEY_USEMAINCASH, key_usemaincash);

            int key_usesyncbon = GlobVar.g_bUseSyncBon ? 1 : 0;
            values.put(KEY_USESYNCBON, key_usesyncbon);

            //write printer
            if(GlobVar.g_objPrinter != null){
                values.put(KEY_PRINTERMAC, GlobVar.g_objPrinter.getMacAddress());
            }
            else{
                values.put(KEY_PRINTERMAC, "");
            }

            // insert
            db_write.insert(TABLE_NAME,null, values);
            db_write.close();
        }
    }

    public void deleteSession() {
        // Get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "id = ?", new String[] { String.valueOf(0) });
        db.close();
    }
}
