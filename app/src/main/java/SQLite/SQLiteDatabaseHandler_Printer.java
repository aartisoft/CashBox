package SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import objects.ObjPrinter;

public class SQLiteDatabaseHandler_Printer extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "PrintersDB";
    private static final String TABLE_NAME = "Printers";
    private static final String KEY_ID = "id";
    private static final String KEY_DEVICEBRAND = "devicebrand";
    private static final String KEY_DEVICENAME = "devicename";
    private static final String KEY_DEVICETYPE = "devicetype";
    private static final String KEY_TARGET = "target";
    private static final String KEY_IPADRESS = "ipadress";
    private static final String KEY_MACADRESS = "macadress";
    private static final String KEY_BDADRESS= "bdadress";
    private static final String KEY_CATEGORY = "category";

    private static final String[] COLUMNS = { KEY_ID, KEY_DEVICEBRAND, KEY_DEVICENAME, KEY_DEVICETYPE,
            KEY_TARGET, KEY_IPADRESS, KEY_MACADRESS, KEY_BDADRESS, KEY_CATEGORY };

    public SQLiteDatabaseHandler_Printer(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATION_TABLE = "CREATE TABLE Printers ( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "devicebrand TEXT, "
                + "devicename TEXT, " + "devicetype INTEGER, " + "target TEXT, "
                + "ipadress TEXT, " + "macadress TEXT, " + "bdadress TEXT, "
                + "category TEXT)";

        db.execSQL(CREATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(db);
    }

    public List<ObjPrinter> allPrinters() {

        List<ObjPrinter> printers = new ArrayList<ObjPrinter>();
        String query = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        ObjPrinter printer = null;

        if (cursor.moveToFirst()) {
            do {
                printer = new ObjPrinter();
                printer.setDeviceBrand(cursor.getString(1));
                printer.setDeviceName(cursor.getString(2));
                printer.setDeviceType(Integer.parseInt(cursor.getString(3)));
                printer.setTarget(cursor.getString(4));
                printer.setIpAdress(cursor.getString(5));
                printer.setMacAddress(cursor.getString(6));
                printer.setBdAddress(cursor.getString(7));
                printer.setCategory(cursor.getString(8));
                printers.add(printer);
            } while (cursor.moveToNext());
        }

        return printers;
    }

    public ObjPrinter getPrinter(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, // a. table
                COLUMNS, // b. column names
                " id = ?", // c. selections
                new String[] { String.valueOf(id) }, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit

        if (cursor != null)
            cursor.moveToFirst();

        ObjPrinter printer = new ObjPrinter();
        printer.setDeviceBrand(cursor.getString(1));
        printer.setDeviceName(cursor.getString(2));
        printer.setDeviceType(Integer.parseInt(cursor.getString(3)));
        printer.setTarget(cursor.getString(4));
        printer.setIpAdress(cursor.getString(5));
        printer.setMacAddress(cursor.getString(6));
        printer.setBdAddress(cursor.getString(7));
        printer.setCategory(cursor.getString(8));

        return printer;
    }

    public void addPrinter(ObjPrinter printer) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DEVICEBRAND, printer.getDeviceBrand());
        values.put(KEY_DEVICENAME, printer.getDeviceName());
        values.put(KEY_DEVICETYPE, printer.getDeviceType());
        values.put(KEY_TARGET, printer.getTarget());
        values.put(KEY_IPADRESS, printer.getIpAddress());
        values.put(KEY_MACADRESS, printer.getMacAddress());
        values.put(KEY_BDADRESS, printer.getBdAddress());
        values.put(KEY_CATEGORY, printer.getCategory());

        // insert
        db.insert(TABLE_NAME,null, values);
        db.close();
    }

    public int updatePrinter(ObjPrinter printer) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DEVICEBRAND, printer.getDeviceBrand());
        values.put(KEY_DEVICENAME, printer.getDeviceName());
        values.put(KEY_DEVICETYPE, printer.getDeviceType());
        values.put(KEY_TARGET, printer.getTarget());
        values.put(KEY_IPADRESS, printer.getIpAddress());
        values.put(KEY_MACADRESS, printer.getMacAddress());
        values.put(KEY_BDADRESS, printer.getBdAddress());
        values.put(KEY_CATEGORY, printer.getCategory());

        int i = db.update(TABLE_NAME, // table
                values, // column/value
                "macadress = ?", // selections
                new String[] { String.valueOf(printer.getMacAddress()) });

        db.close();

        return i;
    }

    public void deletePrinter(ObjPrinter printer) {
        // Get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "macadress = ?", new String[] { String.valueOf(printer.getMacAddress()) });
        db.close();
    }
}
