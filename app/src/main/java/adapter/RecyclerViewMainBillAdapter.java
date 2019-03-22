package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dd.cashbox.R;

import java.text.DecimalFormat;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import objects.ObjBill;
import objects.ObjBillProducts;
import objects.ObjProduct;

public class RecyclerViewMainBillAdapter extends RecyclerView.Adapter<RecyclerViewMainBillAdapter.MyViewHolder>{
    private Context context;
    private List<ObjBillProducts> billproductList;



    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textview_itemname;
        public TextView textview_prize;

        public MyViewHolder(@NonNull View view) {
            super(view);

            textview_itemname = view.findViewById(R.id.am_bill_rv_prize);
            textview_prize = view.findViewById(R.id.am_bill_rv_name);
        }
    }

    public RecyclerViewMainBillAdapter(Context context, List<ObjBillProducts> billproductList) {
        this.context = context;
        this.billproductList = billproductList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.editproduct_recyclerview_items, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        DecimalFormat df = new DecimalFormat("#.00");
        final ObjBillProducts item = billproductList.get(position);

        //set name
        String strName = item.getQuantity() + item.getProduct().getName();
        holder.textview_itemname.setText(strName);

        //set prize
        double prize = item.getQuantity() * item.getProduct().getVK();
        String strVK = df.format(prize);
        strVK = strVK + "€";
        holder.textview_prize.setText(strVK);
    }

    @Override
    public int getItemCount() {
        return billproductList.size();
    }

    public void removeItem(int position) {
        billproductList.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(ObjBillProducts item, int position) {
        billproductList.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }
}