package printer;

import android.content.Context;
import android.os.AsyncTask;

import global.GlobVar;
import objects.ObjPrinter;


public class PrintJobQueue{

    private static EpsonPrintBill m_EpsonPrintBill;

    private static Context m_Context;
    private static ObjPrinter m_ObjPrinter;
    private static String[] m_arrBillText = new String[10];
    private static boolean m_bPrintStatus = false;

    public static void startPrintJobQueue(){

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while(true) {

                        AsyncPrintJobQueue asyncPrintJobQueue = new AsyncPrintJobQueue();
                        asyncPrintJobQueue.execute();

                        //sleep(1000);
                        //handler.post(this);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();
    }

    public static class AsyncPrintJobQueue extends AsyncTask<String, Integer, String[]> {
        @Override
        protected String[] doInBackground(String... strings) {

            //get first printjob and send it to printer
            if (GlobVar.g_lstPrintJob != null && GlobVar.g_lstPrintJob.size() > 0) {
                m_Context = GlobVar.g_lstPrintJob.get(0).getContext();
                m_ObjPrinter = GlobVar.g_lstPrintJob.get(0).getPrinter();
                m_arrBillText = GlobVar.g_lstPrintJob.get(0).getBillText();

                m_EpsonPrintBill = new EpsonPrintBill(m_Context, m_ObjPrinter);
                m_bPrintStatus = m_EpsonPrintBill.runPrintBillSequence(m_arrBillText);
            }

            return new String[0];
        }

        @Override
        protected void onPostExecute(String[] strings) {
            String strPrinterError = m_EpsonPrintBill.getPrinterError();
            String strPrinterWarning = m_EpsonPrintBill.getPrinterWarning();

            //if no error delete printjob
            if (strPrinterError.equals("") && strPrinterWarning.equals("")) {
                if (GlobVar.g_lstPrintJob != null && GlobVar.g_lstPrintJob.size() > 0) {
                    GlobVar.g_lstPrintJob.remove(0);
                }
            }

        }
    }
}
