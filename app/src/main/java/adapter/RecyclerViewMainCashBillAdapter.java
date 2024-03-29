package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dd.cashbox.MainCash;
import com.example.dd.cashbox.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import objects.ObjMainBillProduct;

public class RecyclerViewMainCashBillAdapter extends RecyclerView.Adapter<RecyclerViewMainCashBillAdapter.MyViewHolder>{
    private Context context;
    private List<ObjMainBillProduct> m_billproductList = new ArrayList<>();


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        public TextView textview_itemname;
        public TextView textview_itemcount;
        public TextView textview_prize;
        public View mCardView;

        public MyViewHolder(@NonNull View view) {
            super(view);

            textview_itemname = view.findViewById(R.id.am_bill_rv_name);
            textview_itemcount = view.findViewById(R.id.am_bill_rv_count);
            textview_prize = view.findViewById(R.id.am_bill_rv_prize);

            mCardView = view.findViewById(R.id.am_bill_cash_recyclerview_items);
            mCardView.setOnClickListener(this);
            mCardView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            ObjMainBillProduct objMainBillProduct = m_billproductList.get(position);
            //implement interface instead if adapter is used in more than one activity!
            ((MainCash)context).showMainCashBillDialog(objMainBillProduct.getProduct().getCategory(), objMainBillProduct.getProduct().getName());
        }

        @Override
        public boolean onLongClick(View v) {
            int position = (int) v.getTag();
            ObjMainBillProduct objMainBillProduct = m_billproductList.get(position);
            //implement interface instead if adapter is used in more than one activity!
            ((MainCash)context).transferAllProductItems(objMainBillProduct);
            return true;
        }
    }

    public RecyclerViewMainCashBillAdapter(Context context, List<ObjMainBillProduct> billproductList) {
        this.context = context;
        this.m_billproductList = billproductList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.am_bill_cash_recyclerview_items, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.mCardView.setTag(position);

        DecimalFormat df = new DecimalFormat("0.00");
        final ObjMainBillProduct item = m_billproductList.get(position);

        //set name
        String strName = item.getProduct().getName();
        //if pawn is available
        if(item.getProduct().getbPawn()){
            strName += "*";
        }
        holder.textview_itemname.setText(strName);

        //set count
        String strCount = String.valueOf(item.getQuantity()) + "x";
        holder.textview_itemcount.setText(strCount);

        //set prize
        String strVK = df.format(item.getVK());
        strVK = strVK + "€";
        holder.textview_prize.setText(strVK);
    }

    @Override
    public int getItemCount() {
        return m_billproductList.size();
    }
}
