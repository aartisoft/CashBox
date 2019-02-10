package epson;

import android.content.Context;
import android.util.Log;

import com.epson.epos2.discovery.DeviceInfo;
import com.epson.epos2.discovery.Discovery;
import com.epson.epos2.discovery.DiscoveryListener;
import com.epson.epos2.discovery.FilterOption;

import java.util.ArrayList;
import android.os.Handler;

import objects.ObjPrinterSearch;

public class EpsonDiscover {

    private ArrayList<ObjPrinterSearch> m_PrinterList = new ArrayList<ObjPrinterSearch>();
    private Context m_Context;
    private Runnable m_runnable;
    private Handler m_handler;
    private DiscoveryListener m_DiscoveryListener = new DiscoveryListener() {
        @Override
        public void onDiscovery(final DeviceInfo deviceInfo) {
            ObjPrinterSearch printer = new ObjPrinterSearch(deviceInfo.getDeviceName(), deviceInfo.getDeviceType(),
                    deviceInfo.getTarget(), deviceInfo.getIpAddress(),
                    deviceInfo.getMacAddress(), deviceInfo.getBdAddress(), false);

            m_PrinterList.add(printer);
        }
    };

    public EpsonDiscover(Context p_Context){
        m_Context = p_Context;
    }

    public void startDiscovery(){

        FilterOption filterOption = null;
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
            Log.e("Discovery failed", e.toString());
        }
    }

    public void stopDiscovery(){
        try {
            Discovery.stop();
        }
        catch (Exception e) {
            Log.e("stop Discovery failed", e.toString());
        }
    }

    public ArrayList<ObjPrinterSearch> getPrinterList(){
        return m_PrinterList;
    }
}
