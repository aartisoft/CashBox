package printer;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import global.GlobVar;
import objects.ObjPrintJob;
import objects.ObjPrinter;


public class PrintJobQueue{

    public static List<ObjPrintJob> m_lstPrintJob = new ArrayList<>();

    public static void startPrintJobQueue() {
        Thread tPrintJobs = new Thread(new PrinterJobs(m_lstPrintJob), "Printer");
        tPrintJobs.start();
    }

    public static void addPrintJob(ObjPrintJob p_objPrintJob){
        m_lstPrintJob.add(p_objPrintJob);
    }

    static class PrinterJobs implements Runnable {

        private  EpsonPrintBill m_EpsonPrintBill;
        private  Context m_Context;
        private  ObjPrinter m_ObjPrinter;
        private  String[] m_arrBillText = new String[11];
        private  boolean m_bPrintStatus = false;
        public static List<ObjPrintJob> m_lstPrinterJobs = new ArrayList<>();


        public PrinterJobs(List<ObjPrintJob> p_lstPrintJob){
            m_lstPrinterJobs = p_lstPrintJob;
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
            synchronized (m_lstPrinterJobs)
            {
                while (m_lstPrinterJobs.isEmpty())
                {
                    System.out.println("Queue is empty " + Thread.currentThread().getName() + " is waiting , size: " + m_lstPrinterJobs.size());
                    m_lstPrinterJobs.wait();

                }
                Thread.sleep(1000);

                m_Context = m_lstPrinterJobs.get(0).getContext();
                m_ObjPrinter = m_lstPrinterJobs.get(0).getPrinter();
                m_arrBillText = m_lstPrinterJobs.get(0).getBillText();
                m_EpsonPrintBill = new EpsonPrintBill(m_Context, m_ObjPrinter);
                m_bPrintStatus = m_EpsonPrintBill.runPrintBillSequence(m_arrBillText);

                ObjPrintJob objPrintJob = (ObjPrintJob) m_lstPrinterJobs.remove(0);
                System.out.println("Consumed: " + m_EpsonPrintBill);
                m_lstPrinterJobs.notifyAll();
            }
        }
    }
}
