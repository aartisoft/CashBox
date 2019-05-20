package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.dd.cashbox.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import objects.ObjProductCashPos;

public class ListViewCurrCashPositionProductAdapter extends BaseAdapter {

    private Context m_Context;
    List<ObjProductCashPos> m_List = new ArrayList<>();

    public ListViewCurrCashPositionProductAdapter(List<ObjProductCashPos> values) {
        super();
        this.m_List = values;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder view = null;
        m_Context = parent.getContext();
        LayoutInflater inflator = (LayoutInflater) m_Context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            view = new ViewHolder();
            convertView = inflator.inflate(R.layout.cashposition_product_lv_itemlistrow,  parent, false);

            // Lookup view for data population
            view.txtTyp = (TextView) convertView.findViewById(R.id.cashposition_itemlistrow_typ);
            view.txtValue = (TextView) convertView.findViewById(R.id.cashposition_itemlistrow_value);
            view.txtInfo = (TextView) convertView.findViewById(R.id.cashposition_itemlistrow_info);

            convertView.setTag(view);
        } else {
            view = (ListViewCurrCashPositionProductAdapter.ViewHolder) convertView.getTag();
        }

        // Populate the data into the template view using the data object
        view.txtTyp.setText(m_List.get(position).getName());
        view.txtValue.setText(m_List.get(position).getVK());
        view.txtInfo.setText(m_List.get(position).getInfo());

        // Return the completed view to render on screen
        return convertView;
    }

    public class ViewHolder {
        public TextView txtTyp;
        public TextView txtValue;
        public TextView txtInfo;
    }
}