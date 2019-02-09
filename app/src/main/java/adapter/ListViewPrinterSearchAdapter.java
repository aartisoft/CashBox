package adapter;

import android.app.Activity;
import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.dd.cashbox.R;

import java.util.ArrayList;

import objects.ObjPrinterSearch;

public class ListViewPrinterSearchAdapter extends BaseAdapter {

    public SparseBooleanArray mCheckStates;
    private Context m_Context;
    ArrayList<ObjPrinterSearch> m_List;

    public ListViewPrinterSearchAdapter(Context context, ArrayList<ObjPrinterSearch> printers) {
        super();
        this.m_Context = context;
        this.m_List = printers;
        mCheckStates = new SparseBooleanArray();
    }

    @Override
    public int getCount() {
        return m_List.size();
    }
    @Override
    public String getItem(int position) {
        return m_List.get(position).toString();
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    public void onItemSelected(int position) {
    }

    public boolean isChecked(int position) {
        return mCheckStates.get(position, false);
    }

    public void setChecked(int position, boolean isChecked) {
        mCheckStates.put(position, isChecked);

    }

    public String getDeviceName(int position){
        return m_List.get(position).getDeviceName();
    }
    public int getDeviceType(int position){
        return m_List.get(position).getDeviceType();
    }
    public String getTarget(int position){
        return m_List.get(position).getTarget();
    }
    public String getIpAddress(int position){
        return m_List.get(position).getIpAddress();
    }
    public String getMacAddress(int position){
        return m_List.get(position).getMacAddress();
    }
    public String getBdAddress(int position){
        return m_List.get(position).getBdAddress();
    }




    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder view = null;
        LayoutInflater inflator = ((Activity) m_Context).getLayoutInflater();

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            view = new ViewHolder();
            convertView = inflator.inflate(R.layout.ms_ap_search_itemlistrow,  parent, false);

            // Lookup view for data population
            view.txtTarget = (TextView) convertView.findViewById(R.id.ms_ap_search_ilr_ip);
            view.txtName = (TextView) convertView.findViewById(R.id.ms_ap_search_ilr_name);
            view.cbAdd = (CheckBox)convertView.findViewById(R.id.ms_ap_search_ilr_cb);

            view.cbAdd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //int getPosition = (Integer) buttonView.getTag(); // Here we get  the position that we have set for the checkbox using setTag.

                    mCheckStates.put(position, isChecked);
                }
            });
            convertView.setTag(view);
        } else {
                view = (ViewHolder) convertView.getTag();
        }

        // Populate the data into the template view using the data object
        view.txtTarget.setText(m_List.get(position).getIpAddress());
        view.txtName.setText(m_List.get(position).getDeviceName());
        view.cbAdd.setChecked(m_List.get(position).isChecked());

        // Return the completed view to render on screen
        return convertView;
    }

    public class ViewHolder {
        public TextView txtName;
        public TextView txtTarget;
        public CheckBox cbAdd;
    }
}