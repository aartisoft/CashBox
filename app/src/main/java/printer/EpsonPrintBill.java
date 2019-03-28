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

            dispPrinterWarnings(printerStatusInfo);
            m_PrinterError = makeErrorMessage(printerStatusInfo);
            m_bPrintJobDone = true;

            if(eventType == Epos2CallbackCode.CODE_SUCCESS){
                m_bPrintSuccess = true;
            }
            else{
                m_bPrintSuccess = false;
            }

            //works only with intelligent epson printer
            /*try{
                m_bReceived = true;
                printer.requestPrintJobStatus(s);
            }
            catch (Epos2Exception e){
                m_bReceived = false;
            }*/

            threadDisconnectPrinter();
        }
    };


    //Constructor
    public EpsonPrintBill(Context p_Context, ObjPrinter p_objPrinter){
        m_Context = p_Context;
        m_objPrinter = p_objPrinter;
    }


    public boolean runPrintBillSequence(String[] p_arrBillText) {
        if (!initalizePrinter()) {
            return false;
        }

        if (!createBillJob(p_arrBillText)) {
            finalizePrinter();
            return false;
        }

        if (!printData()) {
            finalizePrinter();
            return false;
        }

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
        boolean isBeginTransaction = false;

        if (m_Printer == null) {
            return false;
        }

        try {
            m_Printer.connect(m_objPrinter.getTarget(), Printer.PARAM_DEFAULT);
        }
        catch (Exception e) {
            Log.e("Connect Printer", e.toString());
            return false;
        }

        try {
            m_Printer.beginTransaction();
            isBeginTransaction = true;
        }
        catch (Exception e) {
            Log.e("BeginTrans Printer", e.toString());
        }

        if (isBeginTransaction == false) {
            try {
                m_Printer.disconnect();
            }
            catch (Epos2Exception e) {
                // Do nothing
                return false;
            }
        }

        return true;
    }

    private void disconnectPrinter() {
        if (m_Printer == null) {
            return;
        }

        try {
            m_Printer.endTransaction();
        }
        catch (final Exception e) {
            Log.e("BeginTrans Printer", e.toString());
        }

        try {
            m_Printer.disconnect();
        }
        catch (final Exception e) {
            Log.e("Disconnect Printer", e.toString());
        }

        finalizePrinter();
    }

    private boolean printData() {
        if (m_Printer == null) {
            return false;
        }

        if (!connectPrinter()) {
            return false;
        }

        PrinterStatusInfo status = m_Printer.getStatus();

        dispPrinterWarnings(status);

        if (!isPrintable(status)) {
            m_PrinterError = makeErrorMessage(status);
            try {
                m_Printer.disconnect();
            }
            catch (Exception ex) {
                // Do nothing
            }
            return false;
        }

        try {
            m_Printer.sendData(Printer.PARAM_DEFAULT);
        }
        catch (Exception e) {
            //ShowMsg.showException(e, "sendData", mContext);
            try {
                m_Printer.disconnect();
            }
            catch (Exception ex) {
                // Do nothing
            }
            return false;
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

    public boolean createBillJob(String[] p_arrBillText){

        if (m_Printer == null) {
            return false;
        }

        //create message for printer
        StringBuilder textData = new StringBuilder();
        try {

            m_Printer.addTextAlign(Printer.ALIGN_CENTER);
            m_Printer.addTextSize(1, 1);
            textData.append(p_arrBillText[0] + "\n");
            textData.append(p_arrBillText[1] + "\n");
            textData.append(p_arrBillText[2] + "\n");
            textData.append(p_arrBillText[3] + "\n");
            m_Printer.addText(textData.toString());
            textData.delete(0, textData.length());
            m_Printer.addFeedLine(1);

            m_Printer.addTextAlign(Printer.ALIGN_CENTER);
            m_Printer.addTextSize(1, 1);
            textData.append(p_arrBillText[4] + "\n");
            textData.append(p_arrBillText[5] + " - " + p_arrBillText[6] + "\n");
            textData.append(p_arrBillText[7] + "\n");
            m_Printer.addText(textData.toString());
            textData.delete(0, textData.length());
            m_Printer.addFeedLine(1);

            m_Printer.addTextAlign(Printer.ALIGN_CENTER);
            m_Printer.addTextSize(1, 2);
            textData.append(p_arrBillText[8] + "\n");
            m_Printer.addTextSize(1, 1);
            textData.append(p_arrBillText[19] + "\n");
            m_Printer.addText(textData.toString());
            textData.delete(0, textData.length());
            m_Printer.addFeedLine(1);

            m_Printer.addCut(Printer.CUT_FEED);
        }
        catch (Exception e){
            Log.e("createBillJOb failed", e.toString());
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

    public String getPrinterStatus(){

        if (!initalizePrinter()) {
            String msg = "";
            threadDisconnectPrinter();
            return msg;
        }

        if (m_Printer == null) {
            String msg = "";
            threadDisconnectPrinter();
            return msg;
        }

        if (!connectPrinter()) {
            String msg = "";
            threadDisconnectPrinter();
            return msg;
        }

        PrinterStatusInfo status = m_Printer.getStatus();
        String errmsg = makeErrorMessage(status);
        threadDisconnectPrinter();
        return errmsg;
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
