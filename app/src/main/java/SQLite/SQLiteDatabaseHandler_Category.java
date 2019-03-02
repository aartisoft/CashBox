package SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import objects.ObjCategory;
import objects.ObjPrinter;

public class SQLiteDatabaseHandler_Category extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "CategoriesDB";
    private static final String TABLE_NAME = "Categories";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_COLOR = "color";
    private static final String KEY_PRINTERMACADRESS = "macadress";
    private static final String KEY_ENABLED= "enabled";

    private static final String[] COLUMNS = { KEY_ID, KEY_NAME, KEY_COLOR, KEY_PRINTERMACADRESS,
                                                KEY_ENABLED };

    public SQLiteDatabaseHandler_Category(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATION_TABLE = "CREATE TABLE Categories ( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "name TEXT, "
                + "color TEXT, " + "macadress TEXT, " + "KEY_ENABLED BOOLEAN)";

        db.execSQL(CREATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(db);
    }

    public List<ObjCategory> allCategories() {

        List<ObjCategory> categories = new ArrayList<ObjCategory>();
        String query = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        ObjCategory category = null;

        if (cursor.moveToFirst()) {
            do {
                category = new ObjCategory();
                category.setID(Integer.parseInt(cursor.getString(0)));
                category.setName(cursor.getString(1));
                category.setProdColor(2);

                //set printer
                ObjPrinter printer = new ObjPrinter();
                printer.setMacAddress(cursor.getString(3));
                category.setPrinter(printer);

                category.setEnabled(Boolean.parseBoolean(cursor.getString(4)));
                categories.add(category);
            } while (cursor.moveToNext());
        }

        return categories;
    }

    /*public ObjPrinter getPrinter(int id) {
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
    }*/

    public void addCategory(ObjCategory category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, category.getID());
        values.put(KEY_NAME, category.getName());
        values.put(KEY_COLOR, category.getProdColor());

        //get printer macadress
        ObjPrinter printer = category.getPrinter();
        values.put(KEY_PRINTERMACADRESS, printer.getMacAddress());

        values.put(KEY_ENABLED, category.getEnabled());

        // insert
        db.insert(TABLE_NAME,null, values);
        db.close();
    }

    public int updatePrinter(ObjCategory category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, category.getID());
        values.put(KEY_NAME, category.getName());
        values.put(KEY_COLOR, category.getProdColor());

        //get printer macadress
        ObjPrinter printer = category.getPrinter();
        values.put(KEY_PRINTERMACADRESS, printer.getMacAddress());

        values.put(KEY_ENABLED, category.getEnabled());

        int i = db.update(TABLE_NAME, // table
                values, // column/value
                "id = ?", // selections
                new String[] { String.valueOf(category.getID()) });

        db.close();

        return i;
    }

    public void deleteCategory(ObjCategory category) {
        // Get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "id = ?", new String[] { String.valueOf(category.getID()) });
        db.close();
    }
}
