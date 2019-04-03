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
import objects.ObjCategory;
import objects.ObjPrinter;
import objects.ObjProduct;

public class SQLiteDatabaseHandler_TableBills extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "TableBillsDB";
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
    private static final String KEY_RETURNED = "returned";
    private static final String KEY_PAID = "paid";

    private static final String[] COLUMNS = { KEY_ID, KEY_TABLENAME, KEY_BILLNR,
            KEY_CASHIERNAME, KEY_BILLINGDATE, KEY_CATEGORY, KEY_PRODUCT,
            KEY_QUANTITY, KEY_PRINTERMAC, KEY_PRINTED, KEY_CANCELED,
            KEY_PAID, KEY_RETURNED };

    public SQLiteDatabaseHandler_TableBills(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATION_TABLE = "CREATE TABLE TableBills ( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "tablename TEXT, "
                + "billnr INTEGER, " + "cashiername TEXT, "
                + "billingdate TEXT, " + "category TEXT, " + "product TEXT, "
                + "quantity INTEGER, " + "printermac TEXT, " + "printed INTEGER, "
                + "canceled INTEGER, " + "paid INTEGER, " + "returned INTEGER)";

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

        //init global list
        for (int iTables = 0; iTables <= GlobVar.g_iTables; iTables++){
            List<ObjBill> lstBill = new ArrayList<ObjBill>();
            GlobVar.g_lstTableBills.add(lstBill);
        }

        //write tables
        if (cursor.moveToFirst()) {
            do {
                for(int iTableCounter = 0; iTableCounter <= GlobVar.g_iTables; iTableCounter++){
                    int iSqlTable = Integer.parseInt(cursor.getString(1));
                    if (iSqlTable == iTableCounter) {
                        if (GlobVar.g_lstTableBills != null) {
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

                            //get object billproduct
                            ObjBillProduct objBillProduct = writeProductList(cursor);

                            //if bill already exists in table
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
                    }
                }
            } while (cursor.moveToNext());
        }
    }

    public void addTableBill(int p_iTable, int p_iBill) {
        SQLiteDatabase db = this.getWritableDatabase();

        //get bill
        int iBill = 0;
        for(ObjBill objBill : GlobVar.g_lstTableBills.get(p_iTable)){
            if(objBill.getBillNr() == p_iBill){
                break;
            }
            iBill++;
        }

        for(ObjBillProduct objproduct : GlobVar.g_lstTableBills.get(p_iTable).get(iBill).m_lstProducts){
            ContentValues values = new ContentValues();

            //product not saved yet
            if(!objproduct.getSqlSaved()){
                values.put(KEY_TABLENAME, p_iTable);
                values.put(KEY_BILLNR, p_iBill);
                values.put(KEY_CASHIERNAME, GlobVar.g_lstTableBills.get(p_iTable).get(iBill).getCashierName());
                //values.put(KEY_BILLINGDATE, GlobVar.g_lstTableBills.get(p_iTable).get(p_iBill).getBillingDate());
                values.put(KEY_BILLINGDATE, "date");
                values.put(KEY_CATEGORY, objproduct.getCategory());
                values.put(KEY_PRODUCT, objproduct.getProduct().getName());
                values.put(KEY_QUANTITY, objproduct.getQuantity());
                values.put(KEY_PRINTERMAC, objproduct.getPrinter().getMacAddress());
                values.put(KEY_PRINTED, objproduct.getPrinted());
                values.put(KEY_CANCELED, objproduct.getCanceled());
                values.put(KEY_PAID, objproduct.getPaid());
                values.put(KEY_RETURNED, objproduct.getReturned());

                //set sql variables
                objproduct.setSqlSaved(true);
                objproduct.setSqlChanged(false);

                // insert
                db.insert(TABLE_NAME,null, values);
            }

            //only product has changed
            if(objproduct.getqlChanged()){

                values.put(KEY_QUANTITY, objproduct.getQuantity());
                values.put(KEY_PRINTED, objproduct.getPrinted());
                values.put(KEY_CANCELED, objproduct.getCanceled());
                values.put(KEY_PAID, objproduct.getPaid());
                values.put(KEY_RETURNED, objproduct.getReturned());

                int i = db.update(TABLE_NAME, // table
                        values, // column/value
                        "billnr = ?", // selections
                        new String[] { String.valueOf(p_iBill) });

                //set sql variables
                objproduct.setSqlSaved(true);
                objproduct.setSqlChanged(false);
            }

            //db.close();
        }
    }

    private ObjBillProduct writeProductList(Cursor cursor){
        ObjBillProduct objBillProduct = new ObjBillProduct();

        //set product
        for(ObjCategory objcategory : GlobVar.g_lstCategory){
            if(objcategory.getName().equals(cursor.getString(5))){
                for(ObjProduct objproduct : objcategory.getListProduct()){
                    if(objproduct.getName().equals(cursor.getString(6))){
                        objBillProduct.setProduct(objproduct);
                    }
                }
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
        objBillProduct.setPrinted(Integer.parseInt(cursor.getString(9)));

        //set canceled
        objBillProduct.setCanceled(Integer.parseInt(cursor.getString(10)));

        //set paid
        objBillProduct.setPaid(Integer.parseInt(cursor.getString(11)));

        //set returned
        objBillProduct.setReturned(Integer.parseInt(cursor.getString(12)));

        //set sql variables
        objBillProduct.setSqlSaved(true);
        objBillProduct.setSqlChanged(false);

        return objBillProduct;
    }
}
