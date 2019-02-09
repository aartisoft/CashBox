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

import java.util.List;

import objects.ObjPrinter;

public class ListViewPrinterAdapter extends BaseAdapter {

    public SparseBooleanArray mCheckStates;
    private Context m_Context;
    List<ObjPrinter> m_List;

    public ListViewPrinterAdapter(Context context, List<ObjPrinter> printers) {
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
    public String getMacAddress(int position){
        return m_List.get(position).getMacAddress();
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder view = null;
        LayoutInflater inflator = ((Activity) m_Context).getLayoutInflater();

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            view = new ViewHolder();
            convertView = inflator.inflate(R.layout.ms_ap_itemlistrow,  parent, false);

            // Lookup view for data population
            view.txtTarget = (TextView) convertView.findViewById(R.id.ms_ap_ilr_ip);
            view.txtName = (TextView) convertView.findViewById(R.id.ms_ap_ilr_name);

            convertView.setTag(view);
        } else {
            view = (ListViewPrinterAdapter.ViewHolder) convertView.getTag();
        }

        // Populate the data into the template view using the data object
        view.txtTarget.setText(m_List.get(position).getIpAddress());
        view.txtName.setText(m_List.get(position).getDeviceName());

        // Return the completed view to render on screen
        return convertView;
    }

    public class ViewHolder {
        public TextView txtName;
        public TextView txtTarget;
        public CheckBox cbAdd;
    }
}