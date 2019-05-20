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
    private static final String KEY_OBJID = "objid";
    private static final String KEY_VK = "vk";
    private static final String KEY_ADDINFO = "addinfo";
    private static final String KEY_PRINTERMAC = "printermac";
    private static final String KEY_PRINTED = "printed";
    private static final String KEY_CANCELED = "canceled";
    private static final String KEY_RETURNED = "returned";
    private static final String KEY_PAID = "paid";
    private static final String KEY_TIP = "tip";
    private static final String KEY_TOGO = "togo";

    private static final String[] COLUMNS = { KEY_ID, KEY_TABLENAME, KEY_BILLNR,
            KEY_CASHIERNAME, KEY_BILLINGDATE, KEY_CATEGORY, KEY_PRODUCT, KEY_OBJID,
            KEY_VK, KEY_ADDINFO, KEY_PRINTERMAC, KEY_PRINTED, KEY_CANCELED, KEY_PAID,
            KEY_RETURNED, KEY_TIP, KEY_TOGO };

    public SQLiteDatabaseHandler_TableBills(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATION_TABLE = "CREATE TABLE TableBills ( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "tablename TEXT, "
                + "billnr INTEGER, " + "cashiername TEXT, " + "billingdate TEXT, "
                + "category TEXT, " + "product TEXT, " + "objid TEXT, " + "vk TEXT, "
                + "addinfo TEXT, " + "printermac TEXT, " + "printed INTEGER, "
                + "canceled INTEGER, " + "paid INTEGER, " + "returned INTEGER, " + "tip TEXT, "
                + "togo INTEGER )";

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
                                objBill.setTable(Integer.parseInt(cursor.getString(1)) +1);
                                objBill.setBillNr(Integer.parseInt(cursor.getString(2)));
                                objBill.setCashierName(cursor.getString(3));
                                objBill.setBillingDate(cursor.getString(4));
                                objBill.setTip(Double.parseDouble(cursor.getString(15)));

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
        objBillProduct.setID(Long.parseLong(cursor.getString(7)));
        objBillProduct.setVK(Double.parseDouble(cursor.getString(8)));
        objBillProduct.setAddInfo(cursor.getString(9));

        //set printer
        for(ObjPrinter objprinter : GlobVar.g_lstPrinter){
            if(objprinter.getMacAddress().equals(cursor.getString(10))){
                objBillProduct.setPrinter(objprinter);
            }
        }

        //set printed
        boolean b_Printed = true;
        if(cursor.getString(11).equals("0")){
            b_Printed = false;
        }
        objBillProduct.setPrinted(b_Printed);

        //set canceled
        boolean b_Canceled = true;
        if(cursor.getString(12).equals("0")){
            b_Canceled = false;
        }
        objBillProduct.setCanceled(b_Canceled);

        //set paid
        boolean b_Paid = true;
        if(cursor.getString(13).equals("0")){
            b_Paid = false;
        }
        objBillProduct.setPaid(b_Paid);

        //set returned
        boolean b_Returned = true;
        if(cursor.getString(14).equals("0")){
            b_Returned = false;
        }

        //set togo
        boolean b_ToGo = true;
        if(cursor.getString(16).equals("0")){
            b_ToGo = false;
        }
        objBillProduct.setToGo(b_ToGo);

        //set sql variables
        objBillProduct.setSqlSaved(true);
        objBillProduct.setSqlChanged(false);

        return objBillProduct;
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
                values.put(KEY_BILLINGDATE, GlobVar.g_lstTableBills.get(p_iTable).get(iBill).getBillingDate());
                values.put(KEY_TIP, GlobVar.g_lstTableBills.get(p_iTable).get(iBill).getTip());
                values.put(KEY_CATEGORY, objproduct.getCategory());
                values.put(KEY_PRODUCT, objproduct.getProduct().getName());
                values.put(KEY_OBJID, objproduct.getID());
                values.put(KEY_VK, objproduct.getVK());
                values.put(KEY_ADDINFO, objproduct.getAddInfo());
                values.put(KEY_PRINTERMAC, objproduct.getPrinter().getMacAddress());

                int key_printed = objproduct.getPrinted() ? 1 : 0;
                values.put(KEY_PRINTED, key_printed);

                int key_canceled = objproduct.getCanceled() ? 1 : 0;
                values.put(KEY_CANCELED, key_canceled);

                int key_paid = objproduct.getPaid() ? 1 : 0;
                values.put(KEY_PAID, key_paid);

                int key_returned = objproduct.getReturned() ? 1 : 0;
                values.put(KEY_RETURNED, key_returned);

                int key_togo = objproduct.getToGo() ? 1 : 0;
                values.put(KEY_TOGO, key_togo);

                //set sql variables
                objproduct.setSqlSaved(true);
                objproduct.setSqlChanged(false);

                // insert
                db.insert(TABLE_NAME,null, values);
            }

            //only product has changed
            if(objproduct.getqlChanged()){

                int key_printed = objproduct.getPrinted() ? 1 : 0;
                values.put(KEY_PRINTED, key_printed);

                int key_canceled = objproduct.getCanceled() ? 1 : 0;
                values.put(KEY_CANCELED, key_canceled);

                int key_paid = objproduct.getPaid() ? 1 : 0;
                values.put(KEY_PAID, objproduct.getPaid());

                int key_returned = objproduct.getReturned() ? 1 : 0;
                values.put(KEY_RETURNED, objproduct.getReturned());

                int i = db.update(TABLE_NAME, // table
                        values, // column/value
                        "billnr = ? AND product = ? AND objid = ?", // selections
                        new String[] { String.valueOf(p_iBill), String.valueOf(objproduct.getProduct().getName())
                                        , String.valueOf(objproduct.getID()) });

                //set sql variables
                objproduct.setSqlSaved(true);
                objproduct.setSqlChanged(false);
            }

            if(GlobVar.g_lstTableBills.get(p_iTable).get(iBill).getSqlChanged()){
                values.put(KEY_TIP, GlobVar.g_lstTableBills.get(p_iTable).get(iBill).getTip());

                int i = db.update(TABLE_NAME, // table
                        values, // column/value
                        "billnr = ?" , // selections
                        new String[] { String.valueOf(p_iBill) });

                GlobVar.g_lstTableBills.get(p_iTable).get(iBill).setSqlChanged(false);
            }
        }
    }
}
