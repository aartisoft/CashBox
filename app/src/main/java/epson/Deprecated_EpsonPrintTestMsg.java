package epson;

import android.content.Context;
import android.util.Log;

import com.epson.epos2.ConnectionListener;
import com.epson.epos2.Epos2CallbackCode;
import com.epson.epos2.printer.PrinterStatusInfo;
import com.epson.epos2.printer.ReceiveListener;
import com.epson.epos2.printer.StatusChangeListener;
import com.epson.epos2.printer.Printer;

import global.GlobVar;
import objects.ObjPrinter;


public class Deprecated_EpsonPrintTestMsg {

    private Context m_Context = null;
    private Printer  m_Printer = null;
    private ObjPrinter m_objPrinter;
    private String m_PrinterStatus = "Offline";
    private String m_PrintStatus = "";

    private StatusChangeListener m_StatusChangeListener = new StatusChangeListener() {
        @Override
        public void onPtrStatusChange(Printer printer, final int eventType) {
                    switch (eventType) {
                        case Printer.EVENT_DISCONNECT:
                            m_PrinterStatus = "Offline";

                            //disconnect Printer
                            try {
                                m_Printer.disconnect();
                            }
                            catch (Exception e){
                                Log.e("init mPrinter", e.toString());
                            }
                            break;
                        case Printer.EVENT_ONLINE:
                            m_PrinterStatus = "Online";
                            break;
                        case Printer.EVENT_OFFLINE:
                            m_PrinterStatus = "Offline";

                            //disconnect Printer
                            try {
                                m_Printer.disconnect();
                            }
                            catch (Exception e){
                                Log.e("init mPrinter", e.toString());
                            }
                            break;
                        case Printer.EVENT_COVER_CLOSE:
                            m_PrinterStatus = "Online";
                            break;
                        case Printer.EVENT_COVER_OPEN:
                            m_PrinterStatus = "Online - Abdeckung geöffnet";
                            break;
                        case Printer.EVENT_PAPER_OK:
                            m_PrinterStatus = "Online";
                            break;
                        case Printer.EVENT_PAPER_NEAR_END:
                            m_PrinterStatus = "Online - Papier fast leer";
                            break;
                        case Printer.EVENT_PAPER_EMPTY:
                            m_PrinterStatus = "Online - Papier leer";
                            break;
                        case Printer.EVENT_DRAWER_HIGH:
                            //Displays notification messages
                            break;
                        case Printer.EVENT_DRAWER_LOW:
                            break;
                        default:
                            break;
                    }
                }
    };

    private ConnectionListener m_ConnectionListener = new ConnectionListener() {
        @Override
        public void onConnection(Object o, int eventType) {
            switch (eventType) {
                case Printer.EVENT_DISCONNECT:
                    //disconnect Printer
                    try {
                        m_Printer.disconnect();
                    }
                    catch (Exception e){
                        Log.e("init mPrinter", e.toString());
                    }
                    break;
                case Printer.EVENT_RECONNECTING:
                    //m_status = "Online";
                    break;
                default:
                    break;
            }
        }
    };

    private ReceiveListener m_ReceiveListener = new ReceiveListener(){
        @Override
        public void onPtrReceive(Printer printer, int eventType, PrinterStatusInfo printerStatusInfo, String s) {
            if (eventType == Epos2CallbackCode.CODE_SUCCESS) {
                m_PrintStatus = "Testnachricht erfolgreich gesendet";
                Log.e("Printer Status", "send success");
            }
            else {
                m_PrintStatus = "Testnachricht nicht erfolgreich gesendet";
                Log.e("Printer Status", "send unsuccessful");
            }

            //disconnect Printer
            try {
                m_Printer.disconnect();
            }
            catch (Exception e){
                Log.e("init mPrinter", e.toString());
            }
        }
    };


    //Constructor
    public Deprecated_EpsonPrintTestMsg(Context p_Context, ObjPrinter p_objPrinter){
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

        //connect to Printer
        connectPrinter();

        //set Listener
        m_Printer.setStatusChangeEventListener(m_StatusChangeListener);
        m_Printer.setConnectionEventListener(m_ConnectionListener);
        m_Printer.setReceiveEventListener(m_ReceiveListener);

        //start monitoring
        try {
            m_Printer.startMonitor();
            m_Printer.setInterval(1000);
        }
        catch(Exception e){
            Log.e("start Monitor failed", e.toString());
        }


        m_Printer.clearCommandBuffer();
        //create message for printer
        StringBuilder textData = new StringBuilder();
        try {
            m_Printer.addTextAlign(Printer.ALIGN_CENTER);
            m_Printer.addFeedLine(0);

            m_Printer.addTextSize(1, 2);
            textData.append("Druckerinformationen: \n");
            m_Printer.addText(textData.toString());
            textData.delete(0, textData.length());
            m_Printer.addFeedLine(1);

            m_Printer.addTextSize(1, 1);
            textData.append("Hersteller: " + m_objPrinter.getDeviceBrand() + "\n");
            textData.append("Druckername: " + m_objPrinter.getDeviceName() + "\n");
            textData.append("IP-Adresse: " + m_objPrinter.getIpAddress() + "\n");
            textData.append("MAC-Adresse: " + m_objPrinter.getMacAddress() + "\n");

            m_Printer.addText(textData.toString());
            textData.delete(0, textData.length());
            m_Printer.addFeedLine(1);

            /*Bitmap icon = BitmapFactory.decodeResource(m_Context.getResources(), R.drawable.loginlogo);

            m_Printer.addImage(icon, 50, 50,
                    300,
                    150,
                    Printer.COLOR_1,
                    Printer.MODE_MONO,
                    Printer.HALFTONE_DITHER,
                    Printer.PARAM_DEFAULT,
                    Printer.COMPRESS_NONE);*/

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

    private boolean sendData() {
        if (m_Printer == null) {
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

    public void reconnectPrinter(){
        PrinterStatusInfo printerStatusInfo = m_Printer.getStatus();
        boolean bConnection = false;

        if(printerStatusInfo.getOnline() == Printer.TRUE) {
            bConnection = true;
        }
        else {
            bConnection = false;
        }

        while(!bConnection) {
            try {
                m_Printer.connect(m_objPrinter.getTarget(), Printer.PARAM_DEFAULT);
            } catch (Exception e) {
                Log.e("Connect failed", e.toString());
            }

            if(printerStatusInfo.getOnline() == Printer.TRUE) {
                bConnection = true;
            }
            else {
                bConnection = false;
            }
        }

        try {
            m_Printer.beginTransaction();
            //isBeginTransaction = true;
        }
        catch (Exception e) {
            Log.e("beginTransaction failed", e.toString());
        }
    }

    public String getPrinterStatus(){

        //connect to Printer
        connectPrinter();

        //set Listener
        m_Printer.setStatusChangeEventListener(m_StatusChangeListener);

        //start monitoring
        try {
            m_Printer.startMonitor();
            m_Printer.setInterval(1000);
        }
        catch(Exception e){
            Log.e("start Monitor failed", e.toString());
        }

        return m_PrinterStatus;
    }

    public String getPrintStatus(){

        try {
            m_Printer.clearCommandBuffer();
            m_Printer.endTransaction();
        }
        catch(Exception e){
            Log.e("start Monitor failed", e.toString());
        }

        connectPrinter();


        //set Listener
        m_Printer.setReceiveEventListener(m_ReceiveListener);

        //start monitoring
        try {
            m_Printer.startMonitor();
            m_Printer.setInterval(1000);
        }
        catch(Exception e){
            Log.e("start Monitor failed", e.toString());
        }
        return m_PrintStatus;
    }
}
