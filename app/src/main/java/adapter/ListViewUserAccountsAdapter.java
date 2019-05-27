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
import java.util.List;

import objects.ObjPrinterSearch;
import objects.ObjUser;

public class ListViewUserAccountsAdapter extends BaseAdapter {

    private Context m_Context;
    List<ObjUser> m_List;

    public ListViewUserAccountsAdapter(Context context, List<ObjUser> user) {
        super();
        this.m_Context = context;
        this.m_List = user;
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

    public ObjUser getObjUser(int position){
        return m_List.get(position);
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
            view.txtName = (TextView) convertView.findViewById(R.id.activity_ms_ua_ilr_user);
            //view.txtTarget = (TextView) convertView.findViewById(R.id.ms_ap_search_ilr_ip);
            view.cbAdd = (CheckBox)convertView.findViewById(R.id.ms_ap_search_ilr_cb);

            view.cbAdd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //mCheckStates.put(position, isChecked);
                    m_List.get(position).setChecked(isChecked);
                }
            });
            convertView.setTag(view);
        } else {
                view = (ViewHolder) convertView.getTag();
        }

        // Populate the data into the template view using the data object
        view.txtName.setText(m_List.get(position).getUserName());
        //view.txtTarget.setText(m_List.get(position).getIpAddress());
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
