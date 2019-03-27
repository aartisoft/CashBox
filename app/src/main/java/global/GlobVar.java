package global;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import objects.ObjBill;
import objects.ObjCategory;
import objects.ObjPrintJob;
import objects.ObjPrinter;
import objects.ObjProduct;

public abstract class GlobVar extends Application {

    public static boolean g_bReadSQL = true;
    public static List<ObjPrinter> g_lstPrinter = new ArrayList<ObjPrinter>();
    public static String g_strBedienername = "Bedienername";
    public static int g_iTables = -1;
    public static List<ObjCategory> g_lstCategory = new ArrayList<ObjCategory>();
    public static List<ObjProduct> g_lstProduct = new ArrayList<ObjProduct>();
    public static List<List<ObjBill>> g_lstTableBills = new ArrayList<List<ObjBill>>();
    public static int g_iBillNr = 0;
    public static List<ObjPrintJob> g_lstPrintJob = new ArrayList<>();
    public static boolean g_bPrintQueueStarted = false;
}
