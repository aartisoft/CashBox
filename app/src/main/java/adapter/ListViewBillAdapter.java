package adapter;

import android.app.Activity;
import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dd.cashbox.R;

import java.util.List;

import objects.ObjBill;
import objects.ObjBillProduct;
import objects.ObjPrinter;

public class ListViewBillAdapter extends BaseAdapter {

    private Context m_Context;
    List<ObjBill> m_List;

    public ListViewBillAdapter(Context context, List<ObjBill> bills) {
        super();
        this.m_Context = context;
        this.m_List = bills;
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

    public ObjBill getObjBill(int position){
        return m_List.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder view = null;
        LayoutInflater inflator = ((Activity) m_Context).getLayoutInflater();

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            view = new ViewHolder();
            convertView = inflator.inflate(R.layout.activity_main_showbills_itemlistrow,  parent, false);

            // Lookup view for data population
            view.txtBill= (TextView) convertView.findViewById(R.id.activity_main_showbills_ilr_billnr);
            view.txtArticles = (TextView) convertView.findViewById(R.id.activity_main_showbills_ilr_articles);

            convertView.setTag(view);
        } else {
            view = (ListViewBillAdapter.ViewHolder) convertView.getTag();
        }

        // Populate the data into the template view using the data object
        String strBillName = m_Context.getResources().getString(R.string.src_Beleg) + " " + String.valueOf(m_List.get(position).getBillNr());
        view.txtBill.setText(strBillName);

        //populate articles
        String strAllArticles = "";
        for(ObjBillProduct objBillProduct : m_List.get(position).m_lstProducts){
            int iQuantitiy = objBillProduct.getQuantity() - objBillProduct.getReturned() - objBillProduct.getCanceled();
            String strArticle = objBillProduct.getProduct().getName();
            strAllArticles += iQuantitiy + "x " + strArticle + "\n";
        }
        view.txtArticles.setText(strAllArticles);

        // Return the completed view to render on screen
        return convertView;
    }

    public class ViewHolder {
        public TextView txtBill;
        public TextView txtArticles;
    }
}