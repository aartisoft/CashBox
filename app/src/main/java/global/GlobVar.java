package global;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import objects.ObjBill;
import objects.ObjCategory;
import objects.ObjPrintJob;
import objects.ObjPrinter;
import objects.ObjProduct;
import objects.ObjSession;

public abstract class GlobVar extends Application {

    public static boolean g_bReadSQL = true;
    public static List<ObjPrinter> g_lstPrinter = new ArrayList<ObjPrinter>();
    public static List<ObjCategory> g_lstCategory = new ArrayList<ObjCategory>();
    public static List<List<ObjBill>> g_lstTableBills = new ArrayList<List<ObjBill>>();
    public static int g_iTables = -1;
    public static int g_iBillNr = -1;
    public static int g_iSessionTable = -1;
    public static int g_iSessionBill = -1;
    public static List<ObjPrintJob> g_lstPrintJob = new ArrayList<>();
    public static boolean g_bPrintQueueStarted = false;
    public static boolean g_bPrintQueueFilling = false;
    public static long g_BillObjID = 0;


    //setting variables
    public static ObjSession g_ObjSession = new ObjSession();
    public static List<ObjUser> g_lstUser = new ArrayList<ObjUser>();
    public static boolean g_bUseMainCash = false;
    public static ObjPrinter g_objPrinter;
}
