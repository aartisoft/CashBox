package SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import global.GlobVar;
import objects.ObjPrinter;
import objects.ObjSession;

public class SQLiteDatabaseHandler_Settings extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "SettingsDB";
    private static final String TABLE_NAME = "Settings";
    private static final String KEY_ID = "id";
    private static final String KEY_CASHIERNAME = "cashiername";
    private static final String KEY_HOSTNAME = "hostname";
    private static final String KEY_PARTYNAME = "partyname";
    private static final String KEY_PARYTDATE = "partydate";
    private static final String KEY_USEMAINCASH = "usemaincash";
    private static final String KEY_USESYNCBON = "usesyncbon";
    private static final String KEY_USEBONPRINTMAIN = "usebonprintmain";
    private static final String KEY_USEBONPRINTCATEGORY = "usebonprintcategory";
    private static final String KEY_USEBONPRINTSYNC = "usebonprintsync";
    private static final String KEY_PRINTERMAC = "printermac";

    private static final String[] COLUMNS = { KEY_ID, KEY_CASHIERNAME, KEY_HOSTNAME,
            KEY_PARTYNAME, KEY_PARYTDATE, KEY_USEMAINCASH, KEY_USESYNCBON, KEY_USEBONPRINTMAIN,
            KEY_USEBONPRINTCATEGORY, KEY_USEBONPRINTSYNC, KEY_PRINTERMAC };

    public SQLiteDatabaseHandler_Settings(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATION_TABLE = "CREATE TABLE Settings ( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "cashiername TEXT, "
                + "hostname TEXT, " + "partyname TEXT, " + "partydate TEXT, " + "usemaincash INTEGER, "
                + "usesyncbon INTEGER, " + "usebonprintmain INTEGER, " + "usebonprintcategory INTEGER, "
                + "usebonprintsync INTEGER, " + "printermac TEXT )";

        db.execSQL(CREATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(db);
    }

    public void getSettings() {
        String query = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                ObjSession objSession = new ObjSession();
                objSession.setCashierName(cursor.getString(1));
                objSession.setHostName(cursor.getString(2));
                objSession.setPartyName(cursor.getString(3));
                GlobVar.g_ObjSession = objSession;

                //set usemaincash
                boolean b_UseMainCash = true;
                if (cursor.getString(5).equals("0")) {
                    b_UseMainCash = false;
                }
                GlobVar.g_bUseMainCash = b_UseMainCash;

                //set useprintbon
                boolean b_UseSyncBon = true;
                if (cursor.getString(6).equals("0")) {
                    b_UseSyncBon = false;
                }
                GlobVar.g_bUseBonPrint = b_UseSyncBon;

                //set usebonprintmain
                boolean b_UseBonPrintMain = true;
                if (cursor.getString(7).equals("0")) {
                    b_UseBonPrintMain = false;
                }
                GlobVar.g_bUseBonPrintMain = b_UseBonPrintMain;

                //set usebonprintcategory
                boolean b_UseBonPrintCategory = true;
                if (cursor.getString(8).equals("0")) {
                    b_UseBonPrintCategory = false;
                }
                GlobVar.g_bUseBonPrintCategory = b_UseBonPrintCategory;

                //set usebonprintsync
                boolean b_UseBonPrintSync = true;
                if (cursor.getString(9).equals("0")) {
                    b_UseBonPrintSync = false;
                }
                GlobVar.g_bUseBonPrintSync = b_UseBonPrintSync;

                //set printer
                for(ObjPrinter objprinter : GlobVar.g_lstPrinter){
                    if(objprinter.getMacAddress().equals(cursor.getString(10))){
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

    public void saveSettings() {
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

            int key_usemaincash = GlobVar.g_bUseMainCash ? 1 : 0;
            values.put(KEY_USEMAINCASH, key_usemaincash);

            int key_usesyncbon = GlobVar.g_bUseBonPrint ? 1 : 0;
            values.put(KEY_USESYNCBON, key_usesyncbon);

            int key_usebonprintmain = GlobVar.g_bUseBonPrintMain ? 1 : 0;
            values.put(KEY_USEBONPRINTMAIN, key_usebonprintmain);

            int key_usebonprintcategory = GlobVar.g_bUseBonPrintCategory ? 1 : 0;
            values.put(KEY_USEBONPRINTCATEGORY, key_usebonprintcategory);

            int key_usebonprintsync = GlobVar.g_bUseBonPrintSync ? 1 : 0;
            values.put(KEY_USEBONPRINTSYNC, key_usebonprintsync);

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

            int key_usemaincash = GlobVar.g_bUseMainCash ? 1 : 0;
            values.put(KEY_USEMAINCASH, key_usemaincash);

            int key_usesyncbon = GlobVar.g_bUseBonPrint ? 1 : 0;
            values.put(KEY_USESYNCBON, key_usesyncbon);

            int key_usebonprintmain = GlobVar.g_bUseBonPrintMain ? 1 : 0;
            values.put(KEY_USEBONPRINTMAIN, key_usebonprintmain);

            int key_usebonprintcategory = GlobVar.g_bUseBonPrintCategory ? 1 : 0;
            values.put(KEY_USEBONPRINTCATEGORY, key_usebonprintcategory);

            int key_usebonprintsync = GlobVar.g_bUseBonPrintSync ? 1 : 0;
            values.put(KEY_USEBONPRINTSYNC, key_usebonprintsync);

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
