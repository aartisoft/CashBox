package epson;

import android.content.Context;
import android.util.Log;
import com.epson.epos2.printer.Printer;


public class Epson {

    private Context m_Context = null;
    private Printer  m_Printer = null;
    private String m_strTarget = null;

    //Constructor
    public void printBon(String p_strTarget){

        //init Variables
        m_strTarget = p_strTarget;

        //init printer
        try {
            m_Printer = new Printer(Printer.TM_M30, Printer.FALSE, m_Context);
        }
        catch (Exception e){
            Log.e("init mPrinter", e.toString());
        }

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
            textData.append("Beleg 1\n");
            textData.append("Tisch 13 \n");
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
            m_Printer.connect(m_strTarget, Printer.PARAM_DEFAULT);
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
