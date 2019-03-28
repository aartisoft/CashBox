package printer;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import global.GlobVar;
import objects.ObjPrintJob;
import objects.ObjPrinter;


public class PrintJobQueue{

    public static List<ObjPrintJob> m_lstPrinterJobs = new ArrayList<>();

    public static void startPrintJobQueue() {
        Thread tPrintJobs = new Thread(new PrintJob(m_lstPrinterJobs), "Printer");
        tPrintJobs.start();
    }

    public static void addPrintJob(ObjPrintJob p_objPrintJob){
        synchronized (m_lstPrinterJobs){
            m_lstPrinterJobs.add(p_objPrintJob);
            m_lstPrinterJobs.notify();
        }
    }

    static public class PrintJob implements Runnable {

        private  EpsonPrintBill m_EpsonPrintBill;
        private  Context m_Context;
        private  ObjPrinter m_ObjPrinter;
        private  String[] m_arrBillText = new String[11];
        private boolean m_bPrintStatus = false;
        public static List<ObjPrintJob> m_lstPrinterJob = new ArrayList<>();

        public PrintJob(List<ObjPrintJob> p_lstPrinterJobs){
            this.m_lstPrinterJob = p_lstPrinterJobs;
        }

        @Override
        public void run()
        {
            while (true)
            {
                try
                {
                    print();
                } catch (InterruptedException ex)
                {
                    ex.printStackTrace();
                }
            }
        }

        private void print() throws InterruptedException
        {
            synchronized (m_lstPrinterJob)
            {
                while (m_lstPrinterJob.isEmpty())
                {
                    Log.e("Printer ", "Queue is empty " + Thread.currentThread().getName() + " is waiting , size: " + m_lstPrinterJob.size());
                    m_lstPrinterJob.wait();

                }
                Thread.sleep(100);

                //print job available
                m_Context = m_lstPrinterJob.get(0).getContext();
                m_ObjPrinter = m_lstPrinterJob.get(0).getPrinter();
                m_arrBillText = m_lstPrinterJob.get(0).getBillText();
                m_EpsonPrintBill = new EpsonPrintBill(m_Context, m_ObjPrinter);

                //print bill
                m_bPrintStatus = m_EpsonPrintBill.runPrintBillSequence(m_arrBillText);

                //if printing process was successfull
                if(m_bPrintStatus){
                    //delete print job if printjob has been reveived from printer
                    do{
                        //wait till print job is done
                    }while(!m_EpsonPrintBill.getPrintJobDone());

                    if (m_EpsonPrintBill.getPrintSuccess()) {
                        ObjPrintJob objPrintJob = (ObjPrintJob) m_lstPrinterJob.remove(0);
                        Log.e("Printer ","Printed: " + m_EpsonPrintBill);
                    }
                }

                m_lstPrinterJob.notify();
            }
        }
    }
}
