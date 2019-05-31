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
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dd.cashbox.MS_UserAccounts;
import com.example.dd.cashbox.MainShowBills;
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
            convertView = inflator.inflate(R.layout.activity_ms_useraccounts_itemlistrow,  parent, false);

            // Lookup view for data population
            view.txtUser = (TextView) convertView.findViewById(R.id.activity_ms_ua_ilr_user);
            view.txtUserInfo = (TextView) convertView.findViewById(R.id.activity_ms_ua_ilr_user2);

            convertView.setTag(view);
        } else {
                view = (ViewHolder) convertView.getTag();
        }

        //init onclicklistener
        view.ivUserSettings = convertView.findViewById(R.id.activity_ms_ua_ilr_iconsett);
        final ViewHolder OnClickView = view;
        final int OnClickPosition = position;
        final View.OnClickListener ChooseListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MS_UserAccounts)m_Context).openUser(OnClickPosition);
            }
        };
        view.ivUserSettings.setOnClickListener(ChooseListener);

        // Populate the data into the template view using the data object
        view.txtUser.setText(m_List.get(position).getUserName());

        if(m_List.get(position).isActive()){
            view.txtUserInfo.setText(convertView.getResources().getString(R.string.src_Aktiv));
        }
        else{
            view.txtUserInfo.setText("");
        }

        // Return the completed view to render on screen
        return convertView;
    }

    public class ViewHolder {
        public TextView txtUser;
        public TextView txtUserInfo;
        public ImageView ivUserSettings;
        public CheckBox cbUserDelete;
    }
}
