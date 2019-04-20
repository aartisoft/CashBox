package adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.dd.cashbox.R;

import java.util.ArrayList;

import objects.ObjBillProduct;
import objects.ObjPrinterSearch;

public class ListViewRetoureStornoAdapter extends BaseAdapter {

    private Context m_Context;
    ArrayList<ObjBillProduct> m_List = new ArrayList<>();

    public ListViewRetoureStornoAdapter(Context context, ArrayList<ObjBillProduct> p_lstBillProducts) {
        super();
        this.m_Context = context;

        for(ObjBillProduct objBillProduct : p_lstBillProducts){
            if(!objBillProduct.getPaid() && !objBillProduct.getReturned() && !objBillProduct.getCanceled()){
                this.m_List.add(objBillProduct);
            }
        }
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
    public ObjBillProduct getObjBillProduct(int position){
        return m_List.get(position);
    }
    public ArrayList<ObjBillProduct> getObjBillProductList(){
        return m_List;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder view = null;
        LayoutInflater inflator = ((Activity) m_Context).getLayoutInflater();

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            view = new ViewHolder();
            convertView = inflator.inflate(R.layout.childfragment_retourestorno_lvitems,  parent, false);

            // Lookup view for data population
            view.txtName = (TextView) convertView.findViewById(R.id.cf_refourestorno_iv_name);
            view.txtAddInfo = (TextView) convertView.findViewById(R.id.cf_refourestorno_iv_addinfo);
            view.txtVK = (TextView) convertView.findViewById(R.id.cf_refourestorno_iv_vk);
            view.cbAdd = (CheckBox)convertView.findViewById(R.id.cf_refourestorno_cb);

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
        view.txtName.setText(m_List.get(position).getProduct().getName());
        view.txtAddInfo.setText(m_List.get(position).getAddInfo());
        view.txtVK.setText(String.valueOf(m_List.get(position).getVK()));
        view.cbAdd.setChecked(m_List.get(position).isChecked());

        // Return the completed view to render on screen
        return convertView;
    }

    public class ViewHolder {
        public TextView txtName;
        public TextView txtAddInfo;
        public TextView txtVK;
        public CheckBox cbAdd;
    }
}