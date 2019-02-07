package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.dd.cashbox.R;

import java.util.ArrayList;

import objects.Printer;

public class ListViewPrinterAdapter extends ArrayAdapter<Printer> {
    public ListViewPrinterAdapter(Context context, ArrayList<Printer> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Printer printer = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.ms_ap_search_itemlistrow, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.ms_ap_search_ilr_name);
        TextView tvIP = (TextView) convertView.findViewById(R.id.ms_ap_search_ilr_ip);
        TextView tvBrand = (TextView) convertView.findViewById(R.id.ms_ap_search_ilr_brand);

        // Populate the data into the template view using the data object
        tvName.setText(printer.m_strName);
        tvIP.setText(printer.m_strIP);
        tvBrand.setText(printer.m_strBrand);

        // Return the completed view to render on screen
        return convertView;
    }
}