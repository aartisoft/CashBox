
package printer;

import android.content.Context;
import android.util.Log;

import com.epson.epos2.ConnectionListener;
import com.epson.epos2.Epos2CallbackCode;
import com.epson.epos2.Epos2Exception;
import com.epson.epos2.printer.Printer;
import com.epson.epos2.printer.PrinterStatusInfo;
import com.epson.epos2.printer.ReceiveListener;
import com.epson.epos2.printer.StatusChangeListener;
import com.example.dd.cashbox.R;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import objects.ObjPrinter;


public class EpsonPrintBill {

    private Context m_Context = null;
    private Printer m_Printer = null;
    private ObjPrinter m_objPrinter;
    private String m_PrinterStatus = "Offline";
    private String m_PrintStatus = "";
    private String m_PrinterError = "";
    private String m_PrinterWarning = "";
    private String m_PrintJobStatus = "";
    private boolean m_bPrintJobDone = false;
    private boolean m_bPrintSuccess = false;
    boolean m_bConnect = false;

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
                    connectPrinter();
                    break;
                default:
                    break;
            }
        }
    };

    private ReceiveListener m_ReceiveListener = new ReceiveListener(){
        @Override
        public void onPtrReceive(Printer printer, int eventType, PrinterStatusInfo printerStatusInfo, String s) {

            dispPrinterWarnings(printerStatusInfo);
            m_PrinterError = makeErrorMessage(printerStatusInfo);
            m_bPrintJobDone = true;

            if(eventType == Epos2CallbackCode.CODE_SUCCESS){
                m_bPrintSuccess = true;
            }
            else{
                m_bPrintSuccess = false;
            }
        }
    };


    //Constructor
    public EpsonPrintBill(Context p_Context, ObjPrinter p_objPrinter){
        m_Context = p_Context;
        m_objPrinter = p_objPrinter;
    }

    public boolean runInitPrinterSequence(){
        if (!initalizePrinter()) {
        	finalizePrinter();
            return false;
        }
        Log.e("Init Printer", "ok");

        if (!connectPrinter()) {
        	finalizePrinter();
            return false;
        }
        Log.e("Connect Printer", "ok");

        return true;
    }


    public boolean runPrintBillSequence(String[] p_arrBillText) {

    	//init member variables
    	m_bPrintSuccess = false;
    	m_bPrintJobDone = false;
    	
        if (!createBillJob(p_arrBillText)) {
            return false;
        }
        Log.e("createBillJob", "ok");

        if (!printData()) {
            return false;
        }
        Log.e("printdata", "ok");

        return true;
    }

    private boolean initalizePrinter(){
        //init printer
        try {
            m_Printer = new Printer(m_objPrinter.getDeviceType(), Printer.FALSE, m_Context);
        }
        catch (Exception e){
            Log.e("Init Printer", e.toString());
            return false;
        }

        m_Printer.setReceiveEventListener(m_ReceiveListener);
        return true;
    }

    private void finalizePrinter() {
        if (m_Printer == null) {
            return;
        }

        m_Printer.clearCommandBuffer();
        m_Printer.setReceiveEventListener(null);
        m_Printer = null;
    }

    private boolean connectPrinter() {

        if (m_Printer == null) {
            return false;
        }

        //new connect
        //Log.e("Connect Printer", "New Connect");
        /*if(!connectPrinterThread()){
            return false;
        }*/

        // old connect
        try {
            m_Printer.connect(m_objPrinter.getTarget(), 1000);
        }
        catch (Exception e) {
            Log.e("Connect Printer", e.toString());
            return false;
        }
        return true;
    }

    private boolean connectPrinterThread(){
        m_bConnect = false;
        Thread rt = new Thread() {
            @Override
            public void run() {
                try {
                    m_Printer.connect(m_objPrinter.getTarget(), 1000);
                    m_bConnect = true;
                }
                catch (Exception e) {
                    Log.e("Connect Printer", e.toString());
                    m_bConnect = false;
                }
            }
        };

        rt.start();

        try {
            rt.join(6 * 1000);
            if(rt.isAlive()){
                rt.interrupt();
                m_bConnect = false;
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // standard result is false
        return m_bConnect;
    }

    public boolean disconnectPrinter() {
        if (m_Printer == null) {
            return false;
        }

        try {
            m_Printer.disconnect();
        }
        catch (final Exception e) {
            Log.e("Disconnect Printer", e.toString());
            return false;
        }

        finalizePrinter();
        return true;
    }

    private boolean printData() {
    	
        if (m_Printer == null) {
            return false;
        }

        try {
            m_Printer.beginTransaction();
        }
        catch (Exception e) {
            Log.e("BeginTrans Printer", e.toString());
            return false;
        }

        PrinterStatusInfo status = m_Printer.getStatus();

        dispPrinterWarnings(status);

        if (!isPrintable(status)) {
            m_PrinterError = makeErrorMessage(status);
            return false;
        }

        try {
            m_Printer.sendData(5000);
            m_Printer.clearCommandBuffer();
        }
        catch (Exception e) {
        	Log.e("Send PrintJob", e.toString()); 
            return false;
        }

        try {
            m_Printer.endTransaction();
        }
        catch (final Exception e) {
            Log.e("EndTrans Printer", e.toString());
        }

        return true;
    }

    private boolean isPrintable(PrinterStatusInfo status) {
        if (status == null) {
            return false;
        }

        if (status.getConnection() == Printer.FALSE) {
            return false;
        }
        else if (status.getOnline() == Printer.FALSE) {
            return false;
        }
        else {
            ;//print available
        }

        return true;
    }

    private String makeErrorMessage(PrinterStatusInfo status) {
        String msg = "";

        if (status.getOnline() == Printer.FALSE) {
            msg += m_Context.getResources().getString(R.string.handlingmsg_err_offline);
        }
        if (status.getOnline() == Printer.TRUE) {
            msg += m_Context.getResources().getString(R.string.handlingmsg_online);
        }
        if (status.getConnection() == Printer.FALSE) {
            msg += m_Context.getResources().getString(R.string.handlingmsg_err_no_response);
        }
        if (status.getCoverOpen() == Printer.TRUE) {
            msg += m_Context.getResources().getString(R.string.handlingmsg_err_cover_open);
        }
        if (status.getPaper() == Printer.PAPER_EMPTY) {
            msg += m_Context.getResources().getString(R.string.handlingmsg_err_receipt_end);
        }
        if (status.getPaperFeed() == Printer.TRUE || status.getPanelSwitch() == Printer.SWITCH_ON) {
            msg += m_Context.getResources().getString(R.string.handlingmsg_err_paper_feed);
        }
        if (status.getErrorStatus() == Printer.MECHANICAL_ERR || status.getErrorStatus() == Printer.AUTOCUTTER_ERR) {
            msg += m_Context.getResources().getString(R.string.handlingmsg_err_autocutter);
            msg += m_Context.getResources().getString(R.string.handlingmsg_err_need_recover);
        }
        if (status.getErrorStatus() == Printer.UNRECOVER_ERR) {
            msg += m_Context.getResources().getString(R.string.handlingmsg_err_unrecover);
        }
        if (status.getErrorStatus() == Printer.AUTORECOVER_ERR) {
            if (status.getAutoRecoverError() == Printer.HEAD_OVERHEAT) {
                msg += m_Context.getResources().getString(R.string.handlingmsg_err_overheat);
                msg += m_Context.getResources().getString(R.string.handlingmsg_err_head);
            }
            if (status.getAutoRecoverError() == Printer.MOTOR_OVERHEAT) {
                msg += m_Context.getResources().getString(R.string.handlingmsg_err_overheat);
                msg += m_Context.getResources().getString(R.string.handlingmsg_err_motor);
            }
            if (status.getAutoRecoverError() == Printer.BATTERY_OVERHEAT) {
                msg += m_Context.getResources().getString(R.string.handlingmsg_err_overheat);
                msg += m_Context.getResources().getString(R.string.handlingmsg_err_battery);
            }
            if (status.getAutoRecoverError() == Printer.WRONG_PAPER) {
                msg += m_Context.getResources().getString(R.string.handlingmsg_err_wrong_paper);
            }
        }
        if (status.getBatteryLevel() == Printer.BATTERY_LEVEL_0) {
            msg += m_Context.getResources().getString(R.string.handlingmsg_err_battery_real_end);
        }

        return msg;
    }

    private void dispPrinterWarnings(PrinterStatusInfo status) {
        String warningsMsg = "";

        if (status == null) {
            return;
        }

        if (status.getPaper() == Printer.PAPER_NEAR_END) {
            warningsMsg += m_Context.getResources().getString(R.string.handlingmsg_warn_receipt_near_end);
        }

        if (status.getBatteryLevel() == Printer.BATTERY_LEVEL_1) {
            warningsMsg += m_Context.getResources().getString(R.string.handlingmsg_warn_battery_near_end);
        }

        m_PrinterWarning = warningsMsg;
    }

    public boolean createBillJob(String[] arrBillText){

        if (m_Printer == null) {
            return false;
        }

        //create message for printer
        StringBuilder textData = new StringBuilder();
        try {
            m_Printer.addTextAlign(Printer.ALIGN_CENTER);
            m_Printer.addTextSize(1, 1);
            textData.append(arrBillText[0] + "\n");
            textData.append(arrBillText[1] + "\n");
            m_Printer.addText(textData.toString());
            textData.delete(0, textData.length());
            m_Printer.addFeedLine(1);

            m_Printer.addTextAlign(Printer.ALIGN_CENTER);
            m_Printer.addTextSize(1, 1);
            textData.append(arrBillText[2] + "\n");
            textData.append(arrBillText[3] + " - " + arrBillText[6] + "\n");
            m_Printer.addText(textData.toString());
            textData.delete(0, textData.length());
            m_Printer.addFeedLine(1);

            m_Printer.addTextAlign(Printer.ALIGN_CENTER);
            m_Printer.addTextSize(1, 2);
            textData.append(arrBillText[4] + "\n");
            m_Printer.addTextSize(1, 1);
            textData.delete(0, textData.length());
            textData.append(arrBillText[5] + "\n");
            m_Printer.addText(textData.toString());
            textData.delete(0, textData.length());

            m_Printer.addCut(Printer.CUT_FEED);
        }
        catch (Exception e){
            Log.e("createBillJob failed", e.toString());
            return false;
        }

        return true;
    }

    public void threadDisconnectPrinter(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                disconnectPrinter();
            }
        }).start();
    }

    public String getPrinterError(){
        return m_PrinterError;
    }
    public String getPrintJobStatus(){
        return m_PrintJobStatus;
    }
    public String getPrinterWarning(){
        return m_PrinterWarning;
    }
    public boolean getPrintJobDone(){
        return m_bPrintJobDone;
    }
    public boolean getPrintSuccess(){
        return m_bPrintSuccess;
    }
}
