package adapter;

import android.app.Activity;
import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.dd.cashbox.R;

import java.util.HashMap;
import java.util.List;

import objects.ObjPrinter;

public class ListViewPrinterDetailAdapter extends BaseAdapter {

    public SparseBooleanArray mCheckStates;
    private Context m_Context;
    List<HashMap<String,String>> m_List;

    public ListViewPrinterDetailAdapter(Context context, List<HashMap<String,String>> printers) {
        super();
        this.m_Context = context;
        this.m_List = printers;
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

    public String getTyp(int position){
        return m_List.get(position).get("typ");
    }
    public String getValue(int position){
        return m_List.get(position).get("value");
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder view = null;
        LayoutInflater inflator = ((Activity) m_Context).getLayoutInflater();

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            view = new ViewHolder();
            convertView = inflator.inflate(R.layout.ms_ap_detail_itemlistrow,  parent, false);

            // Lookup view for data population
            view.txtTyp = (TextView) convertView.findViewById(R.id.ms_ap_detail_ilr_typ);
            view.txtValue = (TextView) convertView.findViewById(R.id.ms_ap_detail_ilr_value);

            convertView.setTag(view);
        } else {
            view = (ListViewPrinterDetailAdapter.ViewHolder) convertView.getTag();
        }

        // Populate the data into the template view using the data object
        view.txtTyp.setText(m_List.get(position).get("typ"));
        view.txtValue.setText(m_List.get(position).get("value"));

        // Return the completed view to render on screen
        return convertView;
    }

    public class ViewHolder {
        public TextView txtTyp;
        public TextView txtValue;
    }
}