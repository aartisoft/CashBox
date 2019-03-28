package printer;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import global.GlobVar;
import objects.ObjPrintJob;
import objects.ObjPrinter;


public class PrintJobQueue{

    public static List<ObjPrintJob> m_lstPrinterJobs = new ArrayList<>();

    public static void startPrintJobQueue() {
        Thread tPrintJobs = new Thread(new PrinterJobs(m_lstPrinterJobs), "Printer");
        tPrintJobs.start();
    }

    public static void addPrintJob(ObjPrintJob p_objPrintJob){
        m_lstPrinterJobs.add(p_objPrintJob);
        synchronized (m_lstPrinterJobs){
            m_lstPrinterJobs.notify();
        }
    }

    static public class PrinterJobs extends Thread {

        private  EpsonPrintBill m_EpsonPrintBill;
        private  Context m_Context;
        private  ObjPrinter m_ObjPrinter;
        private  String[] m_arrBillText = new String[11];
        private  boolean m_bPrintStatus = false;
        public static List<ObjPrintJob> m_lstPrinterJob = new ArrayList<>();

        public PrinterJobs(List<ObjPrintJob> p_lstPrinterJobs){
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
                    System.out.println("Queue is empty " + Thread.currentThread().getName() + " is waiting , size: " + m_lstPrinterJob.size());
                    m_lstPrinterJob.wait();

                }
                Thread.sleep(1000);

                m_Context = m_lstPrinterJob.get(0).getContext();
                m_ObjPrinter = m_lstPrinterJob.get(0).getPrinter();
                m_arrBillText = m_lstPrinterJob.get(0).getBillText();
                m_EpsonPrintBill = new EpsonPrintBill(m_Context, m_ObjPrinter);
                m_bPrintStatus = m_EpsonPrintBill.runPrintBillSequence(m_arrBillText);

                ObjPrintJob objPrintJob = (ObjPrintJob) m_lstPrinterJob.remove(0);
                System.out.println("Consumed: " + m_EpsonPrintBill);
                m_lstPrinterJob.notifyAll();
            }
        }
    }


}
