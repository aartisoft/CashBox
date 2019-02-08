package adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.dd.cashbox.R;

import java.util.ArrayList;

import objects.Printer;

public class ListViewPrinterAdapter extends BaseAdapter {

    private Context m_Context;
    ArrayList<Printer> m_List = new ArrayList<>();

    public ListViewPrinterAdapter(Context context, ArrayList<Printer> printers) {
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder view = null;
        LayoutInflater inflator = ((Activity) m_Context).getLayoutInflater();

        // Get the data item for this position
        //Printer printer = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            view = new ViewHolder();
            convertView = inflator.inflate(R.layout.ms_ap_search_itemlistrow, null);


            // Lookup view for data population
            view.txtName = (TextView) convertView.findViewById(R.id.ms_ap_search_ilr_name);
            view.txtTarget = (TextView) convertView.findViewById(R.id.ms_ap_search_ilr_ip);
            view.cbAdd = (CheckBox)convertView.findViewById(R.id.ms_ap_search_ilr_cb);

            view.cbAdd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int getPosition = (Integer) buttonView.getTag(); // Here we get  the position that we have set for the checkbox using setTag.

                }
            });

            // Populate the data into the template view using the data object
            view.txtName.setText(m_List.get(position).getName());
            view.txtTarget.setText(m_List.get(position).getTarget());
            view.cbAdd.setChecked(m_List.get(position).isChecked());
        }
        // Return the completed view to render on screen
        return convertView;
    }

    public class ViewHolder {
        public TextView txtName;
        public TextView txtTarget;
        public CheckBox cbAdd;
    }
}