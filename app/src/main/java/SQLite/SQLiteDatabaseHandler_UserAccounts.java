package SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import global.GlobVar;
import objects.ObjUser;

public class SQLiteDatabaseHandler_UserAccounts extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "UserAccountsDB";
    private static final String TABLE_NAME = "UserAccounts";
    private static final String KEY_ID = "id";
    private static final String KEY_USER = "user";
    private static final String KEY_ACTIVE = "active";


    public SQLiteDatabaseHandler_UserAccounts(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATION_TABLE = "CREATE TABLE UserAccounts ( "
                + "id INTEGER PRIMARY KEY, " + "user TEXT, " + "active INTEGER)";

        db.execSQL(CREATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(db);
    }

    public void getUser() {
        String query = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                ObjUser objUser = new ObjUser();
                objUser.setUserName(cursor.getString(1));

                //set active
                if (cursor.getString(2).equals("0")) {
                    objUser.setActive(false);
                }
                else if(cursor.getString(2).equals("1")){
                    objUser.setActive(true);
                    GlobVar.g_ObjSession.setCashierName(objUser.getUserName());
                }

                GlobVar.g_lstUser.add(objUser);

            } while (cursor.moveToNext());
        }
    }

    public void addUser(ObjUser p_objUser){
        SQLiteDatabase db_write = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USER, p_objUser.getUserName());

        // insert
        db_write.insert(TABLE_NAME,null, values);

        db_write.close();
    }

    public void updateUser() {
        SQLiteDatabase db_write = this.getWritableDatabase();

        for(ObjUser objUser : GlobVar.g_lstUser){
            ContentValues values = new ContentValues();

            int key_active = objUser.isActive() ? 1 : 0;
            values.put(KEY_ACTIVE, key_active);

            int i = db_write.update(TABLE_NAME, // table
                    values, // column/value
                    "user = ? ", // selections
                    new String[] { objUser.getUserName() });
        }

        db_write.close();
    }

    public void deleteUser(ObjUser p_objUser) {
        // Get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "user = ?", new String[] { p_objUser.getUserName() });
        db.close();
    }
}
