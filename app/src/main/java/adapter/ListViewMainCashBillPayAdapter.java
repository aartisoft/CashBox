package adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dd.cashbox.MainCash;
import com.example.dd.cashbox.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import objects.ObjBillProduct;
import objects.ObjMainBillProduct;
import objects.ObjMainCashBillProduct;

public class ListViewMainCashBillPayAdapter extends BaseAdapter {

    private Context m_Context;
    ArrayList<ObjMainCashBillProduct> m_List = new ArrayList<>();

    public ListViewMainCashBillPayAdapter(Context context, List<ObjBillProduct> p_lstBillProducts) {
        super();
        this.m_Context = context;

        //set shown false
        for(ObjBillProduct objBillProduct : p_lstBillProducts) {
            objBillProduct.setShown(false);
        }

        //set list
        for(ObjBillProduct objBillProductAdapter : p_lstBillProducts){
            if(objBillProductAdapter.getPayTransit() && !objBillProductAdapter.getPaid()
                    && !objBillProductAdapter.getCanceled() && !objBillProductAdapter.getReturned()
                    && !objBillProductAdapter.isShown()){
                //init variables
                ObjBillProduct objBillProductSearch = objBillProductAdapter;
                ObjMainCashBillProduct objMainBillProduct  = new ObjMainCashBillProduct();
                objMainBillProduct.setProduct(objBillProductSearch.getProduct());
                int iQuantity = 0;
                double dPrize = 0.0;
                boolean bFound = false;

                for(ObjBillProduct objBillProduct : p_lstBillProducts){
                    if(objBillProduct.getProduct() == objBillProductSearch.getProduct()){
                        if(objBillProduct.getPayTransit() && !objBillProduct.getPaid()
                                && !objBillProduct.getCanceled() && !objBillProduct.getReturned()
                                && !objBillProduct.isShown()){
                            iQuantity++;
                            dPrize += objBillProduct.getVK();
                            objBillProduct.setShown(true);
                            bFound = true;
                        }
                    }
                }
                if(bFound){
                    objMainBillProduct.setQuantity(iQuantity);
                    objMainBillProduct.setAddInfo(objBillProductSearch.getAddInfo());
                    objMainBillProduct.setSum(dPrize);

                    //add to adapter list
                    this.m_List.add(objMainBillProduct);
                }
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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder view = null;
        LayoutInflater inflator = ((Activity) m_Context).getLayoutInflater();

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            view = new ViewHolder();
            convertView = inflator.inflate(R.layout.activity_main_cash_pay_lvitems,  parent, false);

            // Lookup view for data population
            view.ivDel = (ImageView) convertView.findViewById(R.id.activity_main_cash_pay_lvitems_iv);
            view.txtArticle = (TextView) convertView.findViewById(R.id.activity_main_cash_pay_lvitems_article);
            view.txtSum = (TextView) convertView.findViewById(R.id.activity_main_cash_pay_lvitems_sum);

            convertView.setTag(view);
        }
        else {
            view = (ViewHolder) convertView.getTag();
        }

        // Populate the data into the template view using the data object
        view.txtArticle.setText(m_List.get(position).getQuantity() + "x "
                + m_List.get(position).getProduct().getName());

        DecimalFormat df = new DecimalFormat("0.00");
        String strSum = df.format(m_List.get(position).getSum());
        view.txtSum.setText(strSum);

        //item delete listener
        final View.OnClickListener delListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjMainCashBillProduct objMainCashBillProduct = m_List.get(position);
                ((MainCash)m_Context).delTransitItems(objMainCashBillProduct.getProduct().getCategory(), objMainCashBillProduct.getProduct().getName());
            }
        };
        view.ivDel.setOnClickListener(delListener);

        // Return the completed view to render on screen
        return convertView;
    }

    public class ViewHolder {
        public ImageView ivDel;
        public TextView txtArticle;
        public TextView txtSum;
    }
}