package epson;

import com.epson.epos2.discovery.Discovery;
import com.epson.epos2.discovery.DiscoveryListener;
import com.epson.epos2.discovery.FilterOption;
import com.epson.epos2.discovery.DeviceInfo;
import com.epson.epos2.printer.Printer;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class EpsonDiscovery {

    private Context m_Context = null;
    private Printer  m_Printer = null;
    private String m_strTarget = null;
    private ArrayList<HashMap<String, String>> m_PrinterList = null;
    private HashMap<String, String> foundPrinter;

    private DiscoveryListener m_DiscoveryListener = new DiscoveryListener() {
        int test = 1;
        @Override
        public void onDiscovery(final DeviceInfo deviceInfo) {
            HashMap<String, String> item = new HashMap<String, String>();
            item.put("PrinterName", deviceInfo.getDeviceName());
            item.put("Target", deviceInfo.getTarget());
            m_PrinterList.add(item);
        }
    };

    public ArrayList<HashMap<String, String>> startDiscovery(Context p_Context){
        FilterOption filterOption = null;
        m_Context = p_Context;

        //m_PrinterList.clear();

        filterOption = new FilterOption();
        filterOption.setPortType(Discovery.PORTTYPE_ALL);
        filterOption.setDeviceModel(Discovery.MODEL_ALL);
        filterOption.setEpsonFilter(Discovery.FILTER_NONE);
        filterOption.setDeviceType(Discovery.TYPE_ALL);
        filterOption.setBroadcast("255.255.255.255");

        try {
            Discovery.start(m_Context, filterOption, m_DiscoveryListener);

        }
        catch (Exception e) {
            Log.e("start failed", e.toString());
        }

        return m_PrinterList;
    }
}
