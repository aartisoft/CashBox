package epson;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import com.epson.epos2.printer.Printer;
import com.example.dd.cashbox.R;

import global.GlobVar;
import objects.ObjPrinter;


public class Epson {

    private Context m_Context = null;
    private Printer  m_Printer = null;
    private ObjPrinter m_objPrinter;

    //Constructor
    public Epson(Context p_Context, ObjPrinter p_objPrinter){
        m_Context = p_Context;
        m_objPrinter = p_objPrinter;

        //init printer
        try {
            m_Printer = new Printer(m_objPrinter.getDeviceType(), Printer.FALSE, m_Context);
        }
        catch (Exception e){
            Log.e("init mPrinter", e.toString());
        }
    }

    public void printTestMsg(){

        //create message for printer
        StringBuilder textData = new StringBuilder();
        try {
            m_Printer.addTextAlign(Printer.ALIGN_CENTER);
            m_Printer.addFeedLine(0);
            m_Printer.addTextSize(1, 2);
            textData.append("Druckerinformationen: \n");
            m_Printer.addTextSize(1, 1);
            textData.append("Druckername: " + m_objPrinter.getDeviceName() + "\n");
            textData.append("\n");
            textData.append("IP-Adresse: " + m_objPrinter.getIpAddress() + "\n");
            textData.append("MAC-Adresse: " + m_objPrinter.getMacAddress() + "\n");

            m_Printer.addText(textData.toString());
            textData.delete(0, textData.length());
            m_Printer.addFeedLine(1);

            Bitmap icon = BitmapFactory.decodeResource(m_Context.getResources(), R.drawable.loginlogo);

            m_Printer.addImage(icon, 50, 50,
                    300,
                    150,
                    Printer.COLOR_1,
                    Printer.MODE_MONO,
                    Printer.HALFTONE_DITHER,
                    Printer.PARAM_DEFAULT,
                    Printer.COMPRESS_AUTO);

            m_Printer.addCut(Printer.CUT_FEED);
        }
        catch (Exception e){
            Log.e("createTestMsg failed", e.toString());
        }

        //print message
        try {
            sendData();
        }
        catch (Exception e){
            Log.e("sendData failed", e.toString());
        }
    }

    public void printBon(){

        //create message for printer
        try {
            createBon();
        }
        catch (Exception e){
            Log.e("createBon failed", e.toString());
        }

        //print message
        try {
            sendData();
        }
        catch (Exception e){
            Log.e("sendData failed", e.toString());
        }
    }

    private boolean createBon() {
        StringBuilder textData = new StringBuilder();

        try {

            m_Printer.addTextAlign(Printer.ALIGN_CENTER);

            /*Bitmap icon = BitmapFactory.decodeResource(.getResources(),
                    R.drawable.logo);

            m_Printer.addImage(icon, 0, 0,
                    300,
                    150,
                    Printer.COLOR_1,
                    Printer.MODE_MONO,
                    Printer.HALFTONE_DITHER,
                    Printer.PARAM_DEFAULT,
                    Printer.COMPRESS_AUTO);*/

            m_Printer.addFeedLine(0);
            textData.append(GlobVar.m_strBedienername + "\n");
            textData.append("\n");
            textData.append("25/01/2019 16:58 \n");
            textData.append("\n");

            m_Printer.addText(textData.toString());
            textData.delete(0, textData.length());

            m_Printer.addTextSize(2, 2);

            m_Printer.addText("1x BIER\n");

            m_Printer.addFeedLine(1);

            m_Printer.addCut(Printer.CUT_FEED);
        }
        catch (Exception e) {
            Log.e("create receipt failed", e.toString());
            return false;
        }
        return true;
    }

    private boolean sendData() {
        if (m_Printer == null) {
            return false;
        }

        if (!connectPrinter()) {
            return false;
        }

        try {
            m_Printer.sendData(Printer.PARAM_DEFAULT);
        }
        catch (Exception e) {
            Log.e("printData", e.toString());
            return false;
        }

        return true;
    }
    private boolean connectPrinter() {

        try {
            m_Printer.connect(m_objPrinter.getTarget(), Printer.PARAM_DEFAULT);
        }
        catch (Exception e) {
            Log.e("Connect failed", e.toString());
            return false;
        }

        try {
            m_Printer.beginTransaction();
            //isBeginTransaction = true;
        }
        catch (Exception e) {
            Log.e("beginTransaction failed", e.toString());
            return false;
        }
        return true;
    }

}
