
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
        Thread tPrintJobs = new Thread(new PrintJob(m_lstPrinterJobs), "PrintJob");
        Thread tGetPrintJobs = new Thread(new GetPrintJob(), "GetPrintJob");
        tPrintJobs.start();
        tGetPrintJobs.start();
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
        private List<String[]> g_lstBillText = new ArrayList<>();
        private  String[] m_arrBillText = new String[10];
        private boolean m_bPrintStatus = false;
        private boolean m_bPrintConnect = false;
        public static List<ObjPrintJob> m_lstPrinterJob = new ArrayList<>();
        public int m_iPrintJobCounter = 0;

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
                //m_lstPrinterJob
                while (m_lstPrinterJob.isEmpty() || GlobVar.g_bPrintQueueFilling)
                {
                    Log.e("Printer ", "Queue is empty " + Thread.currentThread().getName() + " is waiting , size: " + m_lstPrinterJob.size());
                    m_lstPrinterJob.wait();
                }
                Thread.sleep(100);

                for(ObjPrinter objPrinter : GlobVar.g_lstPrinter){
                	//search available printjob for printer
                    boolean bPrinterJobAvailable = false;
                    for(ObjPrintJob objPrintJob : m_lstPrinterJob) {
                        if (objPrintJob.getPrinter().getMacAddress().equals(objPrinter.getMacAddress())) {
                            bPrinterJobAvailable = true;
                            break;
                        }
                    }

                    //if printjob is available start print process
                    if(bPrinterJobAvailable){
                        //init printer
                        m_Context = m_lstPrinterJob.get(m_iPrintJobCounter).getContext();
                        m_ObjPrinter = m_lstPrinterJob.get(m_iPrintJobCounter).getPrinter();
                        m_arrBillText = m_lstPrinterJob.get(m_iPrintJobCounter).getBillText();
                        m_EpsonPrintBill = new EpsonPrintBill(m_Context, m_ObjPrinter);
                       
                       //connect to printer
                        m_bPrintConnect = m_EpsonPrintBill.runInitPrinterSequence();
                        
                        boolean bPrinterConnected = false;
                        if(!m_bPrintConnect){
                            Log.e("Printer ","PrinterConnect Unsuccessfull");
                            bPrinterConnected = false;
                        }
                        else{
                        	Log.e("Printer ","PrinterConnect Successfull");
                        	bPrinterConnected = true;
                        }
                        
                        //print data
                        if(bPrinterConnected){
                        	int iPrintJobCounter = 0;
                            for(ObjPrintJob objPrintJob : m_lstPrinterJob){
                                if(objPrintJob.getPrinter().getMacAddress().equals(objPrinter.getMacAddress())){
                                    int iPrintTry = 0;
                                    boolean bPrintedOK = false;
                                    //try 10 times to print data
                                    do{
                                        m_bPrintStatus = m_EpsonPrintBill.runPrintBillSequence(m_lstPrinterJob.get(iPrintJobCounter).getBillText());

                                        //if printing process was successfull
                                        if(m_bPrintStatus){
                                            Log.e("Printer ","PrintJob Send Successfull");
                                            //delete print job if printjob has been reveived from printer
                                            do{
                                                //wait till print job is done
                                            }while(!m_EpsonPrintBill.getPrintJobDone());

                                            if (m_EpsonPrintBill.getPrintSuccess()) {
                                                m_lstPrinterJob.get(iPrintJobCounter).setPrinted(true);
                                                Log.e("Printer ","PrintJob Successfull: " + m_EpsonPrintBill);
                                                bPrintedOK = true;
                                                break;
                                            }
                                            else{
                                                Log.e("Printer ","PrintJob Unsuccessfull");
                                                iPrintTry++;
                                            }
                                        }
                                        else{
                                            Log.e("Printer ","PrintJob could not been sent");
                                            iPrintTry++;
                                        }
                                    }while(iPrintTry < 10);

                                    //if printjob not successfully printed --> try next printer
                                    if(!bPrintedOK){
                                       break;
                                    }
                                }
                                iPrintJobCounter++;
                            }

                            //disconnect with printer
                            //m_EpsonPrintBill.disconnectPrinter();
                            m_EpsonPrintBill.threadDisconnectPrinter();
                        }                    
                    }
                }

                //delete printed jobs
                for(int i = m_lstPrinterJob.size(); i-- > 0; ){
                    if(m_lstPrinterJob.get(i).getPrinted()){
                        m_lstPrinterJob.remove(i);
                    }
                }
                
                m_lstPrinterJob.notify();
            }
        }
    }
    static public class GetPrintJob implements Runnable {

        public static List<ObjPrintJob> m_lstPrinterJob = new ArrayList<>();


        @Override
        public void run()
        {
            while (true)
            {
                try
                {
                    get();
                } catch (InterruptedException ex)
                {
                    ex.printStackTrace();
                }
            }
        }

        private void get() throws InterruptedException
        {
            synchronized (m_lstPrinterJobs)
            {
                if(GlobVar.g_lstPrintJob != null && GlobVar.g_lstPrintJob.size() > 0){
                    m_lstPrinterJobs.add(GlobVar.g_lstPrintJob.get(0));
                    GlobVar.g_lstPrintJob.remove(0);
                }
                m_lstPrinterJobs.notify();
            }
        }
    }
}
