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
    private static final String DATABASE_NAME = "ProductsDB";
    private static final String TABLE_NAME = "Products";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_VK = "vk";
    private static final String KEY_BPAWN = "bpawn";
    private static final String KEY_PAWN = "pawn";
    private static final String KEY_ENABLED = "enabled";
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
                + "vk REAL, " + "bpawn INTEGER, "
                + "pawn REAL, " + "enabled INTEGER, " + "category TEXT)";

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
                if(cursor.getString(6).equals(p_strCategory)){
                    product = new ObjProduct();
                    product.setName(cursor.getString(1));
                    product.setVK(Double.parseDouble(cursor.getString(2)));

                    //set pawn
                    boolean bPawn = true;
                    if(cursor.getString(3).equals("0")){
                        bPawn = false;
                    }
                    product.setbPAWN(bPawn);

                    product.setPAWN(Double.parseDouble(cursor.getString(4)));

                    //set enabled
                    boolean bEnabled = true;
                    if(cursor.getString(5).equals("0")){
                        bEnabled = false;
                    }
                    product.setEnabled(bEnabled);

                    product.set_Category(cursor.getString(6));

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
        product.setName(cursor.getString(1));
        product.setVK(Float.parseFloat(cursor.getString(2)));
        product.setbPAWN(Boolean.parseBoolean(cursor.getString(3)));
        product.setPAWN(Float.parseFloat(cursor.getString(4)));
        product.setEnabled(Boolean.parseBoolean(cursor.getString(5)));

        return product;
    }

    public void addProduct(ObjProduct product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, product.getName());
        values.put(KEY_VK, product.getVK());

        int bpawn = product.getbPawn() ? 1 : 0;
        values.put(KEY_BPAWN, bpawn);

        values.put(KEY_PAWN, product.getPawn());

        int key_enabled = product.getEnabled() ? 1 : 0;
        values.put(KEY_ENABLED, key_enabled);

        values.put(KEY_CATEGORY, product.getCategory());

        // insert
        db.insert(TABLE_NAME,null, values);
        db.close();
    }

    public int updateProduct(String oldname, ObjProduct product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, product.getName());
        values.put(KEY_VK, product.getVK());

        int bpawn = product.getbPawn() ? 1 : 0;
        values.put(KEY_BPAWN, bpawn);

        values.put(KEY_PAWN, product.getPawn());

        int key_enabled = product.getEnabled() ? 1 : 0;
        values.put(KEY_ENABLED, key_enabled);

        values.put(KEY_CATEGORY, product.getCategory());

        int i = db.update(TABLE_NAME, // table
                values, // column/value
                "name = ? AND category = ?", // selections
                new String[] { oldname, String.valueOf(product.getCategory()) });

        db.close();

        return i;
    }

    public int updateProductsCategory(String oldcategoryname, String newcategoryname) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CATEGORY, newcategoryname);

        int i = db.update(TABLE_NAME, // table
                values, // column/value
                "category = ?", // selections
                new String[] { oldcategoryname });

        db.close();

        return i;
    }

    public void deleteProduct(ObjProduct product) {
        // Get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "name = ?", new String[] { String.valueOf(product.getName()) });
        db.close();
    }

    public void deleteProductsCategory(String categoryname) {
        // Get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "category = ?", new String[] { categoryname });
        db.close();
    }
}
