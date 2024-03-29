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
    List<ObjMainCashBillProduct> m_List = new ArrayList<>();

    public ListViewMainCashBillPayAdapter(Context context, List<ObjMainCashBillProduct> p_lstBillProducts) {
        super();
        this.m_Context = context;
        this.m_List = p_lstBillProducts;
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
        //set name
        String strName = m_List.get(position).getQuantity() + "x " + m_List.get(position).getProduct().getName();
        //if pawn is available
        if(m_List.get(position).getProduct().getbPawn()){
            strName += "*";
        }
        //add line break
        String parsedStr = strName.replaceAll("(.{20})", "$1\n");
        view.txtArticle.setText(parsedStr);

        //set vk
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