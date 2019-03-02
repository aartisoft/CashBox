package SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import objects.ObjPrinter;
import objects.ObjProduct;

public class SQLiteDatabaseHandler_Product extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Product";
    private static final String TABLE_NAME = "Products";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_VK = "vk";
    private static final String KEY_BPAWN = "bpawn";
    private static final String KEY_PAWN = "pawn";
    private static final String KEY_ENABLED= "enabled";
    private static final String KEY_CATEGORY= "category";

    private static final String[] COLUMNS = { KEY_ID, KEY_NAME, KEY_VK,
            KEY_BPAWN, KEY_PAWN, KEY_ENABLED, KEY_CATEGORY };

    public SQLiteDatabaseHandler_Product(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATION_TABLE = "CREATE TABLE Products ( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "name TEXT, "
                + "vk FLOAT, " + "bpwan BOOLEAN, "
                + "pawn FLOAT, " + "enabled BOOLEAN, " + "category TEXT)";

        db.execSQL(CREATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(db);
    }

    public List<ObjProduct> allCategoryProducts(String p_strCategory) {

        List<ObjProduct> products = new ArrayList<ObjProduct>();
        String query = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        ObjProduct product = null;

        if (cursor.moveToFirst()) {
            do {
                if(cursor.getString(7).equals(p_strCategory)){
                    product = new ObjProduct();
                    product.setID(Integer.parseInt(cursor.getString(0)));
                    product.setName(cursor.getString(1));
                    product.setVK(Float.parseFloat(cursor.getString(2)));
                    product.setbPAWN(Boolean.parseBoolean(cursor.getString(3)));
                    product.setPAWN(Float.parseFloat(cursor.getString(4)));
                    product.setEnabled(Boolean.parseBoolean(cursor.getString(5)));
                    products.add(product);
                }
            } while (cursor.moveToNext());
        }

        return products;
    }

    public ObjProduct getProduct(int id) {
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

        ObjProduct product = new ObjProduct();
        product.setID(Integer.parseInt(cursor.getString(0)));
        product.setName(cursor.getString(1));
        product.setVK(Float.parseFloat(cursor.getString(2)));
        product.setbPAWN(Boolean.parseBoolean(cursor.getString(3)));
        product.setPAWN(Float.parseFloat(cursor.getString(4)));
        product.setEnabled(Boolean.parseBoolean(cursor.getString(5)));

        return product;
    }

    public void addPrinter(ObjProduct product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, product.getID());
        values.put(KEY_NAME, product.getName());
        values.put(KEY_VK, product.getVK());
        values.put(KEY_BPAWN, product.getbPawn());
        values.put(KEY_PAWN, product.getPawn());
        values.put(KEY_ENABLED, product.getEnabled());
        values.put(KEY_CATEGORY, product.getCategory());

        // insert
        db.insert(TABLE_NAME,null, values);
        db.close();
    }

    public int updatePrinter(ObjProduct product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, product.getID());
        values.put(KEY_NAME, product.getName());
        values.put(KEY_VK, product.getVK());
        values.put(KEY_BPAWN, product.getbPawn());
        values.put(KEY_PAWN, product.getPawn());
        values.put(KEY_ENABLED, product.getEnabled());
        values.put(KEY_CATEGORY, product.getCategory());

        int i = db.update(TABLE_NAME, // table
                values, // column/value
                "id = ?", // selections
                new String[] { String.valueOf(product.getID()) });

        db.close();

        return i;
    }

    public void deletePrinter(ObjProduct product) {
        // Get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "id = ?", new String[] { String.valueOf(product.getID()) });
        db.close();
    }
}
