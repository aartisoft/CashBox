package SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import global.GlobVar;
import objects.ObjBill;
import objects.ObjBillProduct;
import objects.ObjPrinter;
import objects.ObjProduct;

public class SQLiteDatabaseHandler_TableBills extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ProductsDB";
    private static final String TABLE_NAME = "TableBills";
    private static final String KEY_ID = "id";
    private static final String KEY_TABLENAME = "tablename";
    private static final String KEY_BILLNR = "billnr";
    private static final String KEY_CASHIERNAME = "cashiername";
    private static final String KEY_BILLINGDATE = "billingdate";
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_PRODUCT = "product";
    private static final String KEY_QUANTITY = "quantity";
    private static final String KEY_PRINTERMAC = "printermac";
    private static final String KEY_PRINTED = "printed";
    private static final String KEY_CANCELED = "canceled";
    private static final String KEY_PAID = "paid";

    private static final String[] COLUMNS = { KEY_ID, KEY_TABLENAME, KEY_BILLNR,
            KEY_CASHIERNAME, KEY_BILLINGDATE, KEY_CATEGORY, KEY_PRODUCT,
            KEY_QUANTITY, KEY_PRINTERMAC, KEY_PRINTED, KEY_CANCELED, KEY_PAID};

    public SQLiteDatabaseHandler_TableBills(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATION_TABLE = "CREATE TABLE Products ( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "tablename TEXT, "
                + "billnr INTEGER, " + "cashiername TEXT, "
                + "billingdate TEXT, " + "category TEXT, " + "product TEXT, "
                + "quantity INTEGER, " + "printermac TEXT, " + "printed INT, "
                + "canceled INT, " + "paid INTEGER)";

        db.execSQL(CREATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(db);
    }

    public void readAllTableBills() {

        String query = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        //write tables
        if (cursor.moveToFirst()) {
            do {
                int iTableCounter = 0;
                if (cursor.getString(1).equals(iTableCounter)) {
                    if (GlobVar.g_lstTableBills != null) {
                        //if table exists yet
                        if (GlobVar.g_lstTableBills.size() > iTableCounter) {
                            //search for bill in list
                            int iBillCounter = 0;
                            boolean bBillFound = false;
                            for (ObjBill objBillSearch : GlobVar.g_lstTableBills.get(iTableCounter)) {
                                if (objBillSearch.getBillNr() == Integer.parseInt(cursor.getString(2))) {
                                    bBillFound = true;
                                    break;
                                }
                                iBillCounter++;
                            }

                            //if bill already exists in table
                            ObjBillProduct objBillProduct = writeProductList(cursor, iTableCounter);
                            if (bBillFound) {
                                GlobVar.g_lstTableBills.get(iTableCounter).get(iBillCounter).m_lstProducts.add(objBillProduct);
                            }
                            //if bill doesn't exists in table
                            else {
                                ObjBill objBill = new ObjBill();
                                objBill.setBillNr(Integer.parseInt(cursor.getString(2)));
                                objBill.setCashierName(cursor.getString(3));
                                //objBill.setBillingDate();

                                objBill.m_lstProducts = new ArrayList<ObjBillProduct>();
                                objBill.m_lstProducts.add(objBillProduct);
                                GlobVar.g_lstTableBills.get(iTableCounter).add(objBill);
                            }
                        }
                        //if table doesn't exists yet
                        else if (GlobVar.g_lstTableBills.size() < iTableCounter) {
                            ObjBill objBill = new ObjBill();
                            objBill.setBillNr(Integer.parseInt(cursor.getString(2)));
                            objBill.setCashierName(cursor.getString(3));
                            //objBill.setBillingDate();

                            ObjBillProduct objBillProduct = writeProductList(cursor, iTableCounter);
                            objBill.m_lstProducts = new ArrayList<ObjBillProduct>();
                            objBill.m_lstProducts.add(objBillProduct);

                            List<ObjBill> lstObjBill = new ArrayList<ObjBill>();
                            lstObjBill.add(objBill);

                            GlobVar.g_lstTableBills.add(lstObjBill);
                        }
                    }
                }
            } while (cursor.moveToNext());
        }
    }

 /*   public ObjProduct getProduct(int id) {
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
    }*/

    private ObjBillProduct writeProductList(Cursor cursor, int iTableCounter){
        ObjBillProduct objBillProduct = new ObjBillProduct();

        //set product
        for(ObjProduct objproduct : GlobVar.g_lstProduct){
        if(objproduct.getName().equals(cursor.getString(6))){
        objBillProduct.setProduct(objproduct);
        }
        }

        objBillProduct.setCategory(cursor.getString(5));
        objBillProduct.setQuantity(Integer.parseInt(cursor.getString(7)));

        //set printer
        for(ObjPrinter objprinter : GlobVar.g_lstPrinter){
        if(objprinter.getMacAddress().equals(cursor.getString(8))){
        objBillProduct.setPrinter(objprinter);
        }
        }

        //set printed
        boolean bPrinted = true;
        if(cursor.getString(9).equals("0")){
        bPrinted = false;
        }
        objBillProduct.setPrinted(bPrinted);

        //set canceled
        boolean bCanceled = true;
        if(cursor.getString(10).equals("0")){
        bCanceled = false;
        }
        objBillProduct.setCanceled(bCanceled);

        //set paid
        boolean bPaid = true;
        if(cursor.getString(11).equals("0")){
        bPaid = false;
        }
        objBillProduct.setPaid(bPaid);

        return objBillProduct;
    }
}
