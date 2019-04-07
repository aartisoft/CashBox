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

import java.util.ArrayList;
import java.util.List;

import objects.ObjBill;
import objects.ObjBillProduct;
import objects.ObjPrinter;

public class ListViewBillAdapter extends BaseAdapter {

    private Context m_Context;
    private List<ObjBill> m_List;
    private ArrayList<Integer> m_lstHiddenPositions = new ArrayList<>();

    public ListViewBillAdapter(Context context, List<ObjBill> bills) {
        super();
        this.m_Context = context;
        this.m_List = bills;

        int iBillCounter = 0;
        for(ObjBill objBill : bills){
            boolean bProdFound = false;
            for(ObjBillProduct objBillProduct : objBill.m_lstProducts) {
                //if more than one open product available
                int iItemCount = objBillProduct.getQuantity() - objBillProduct.getCanceled() - objBillProduct.getReturned() - objBillProduct.getPaid();
                if (iItemCount > 0) {
                    bProdFound = true;
                    break;
                }
            }
            //if no product found then add to hidden list
            if(!bProdFound){
                this.m_lstHiddenPositions.add(iBillCounter);
            }
            iBillCounter++;
        }
    }

    @Override
    public int getCount() {
        return m_List.size() - m_lstHiddenPositions.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder view = null;
        LayoutInflater inflator = ((Activity) m_Context).getLayoutInflater();

        //code snippet for hidden items
        for(Integer hiddenIndex : m_lstHiddenPositions) {
            if(hiddenIndex <= position) {
                position = position + 1;
            }
        }


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
            int iQuantity = objBillProduct.getQuantity() - objBillProduct.getReturned() - objBillProduct.getCanceled();
            if(iQuantity > 0){
                String strArticle = objBillProduct.getProduct().getName();
                strAllArticles += iQuantity + "x " + strArticle + "\n";
            }
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