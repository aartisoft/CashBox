package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dd.cashbox.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import objects.ObjBillProduct;

public class RecyclerViewMainBillAdapter extends RecyclerView.Adapter<RecyclerViewMainBillAdapter.MyViewHolder>{
    private Context context;
    private List<ObjBillProduct> billproductList;
    private ArrayList<Integer> m_lstHiddenPositions = new ArrayList<>();


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textview_itemname;
        public TextView textview_prize;
        public TextView textview_printerQ;
        public ImageView imageview_printer;

        public MyViewHolder(@NonNull View view) {
            super(view);

            textview_itemname = view.findViewById(R.id.am_bill_rv_name);
            textview_prize = view.findViewById(R.id.am_bill_rv_prize);
            textview_printerQ = view.findViewById(R.id.am_bill_rv_printer_tv);
            imageview_printer = view.findViewById(R.id.am_bill_rv_printerimage);
        }
    }

    public RecyclerViewMainBillAdapter(Context context, List<ObjBillProduct> billproductList) {
        this.context = context;
        this.billproductList = billproductList;

        int iProductCounter = 0;
        for(ObjBillProduct objBillProduct : billproductList){
            //if more than one open product available
            int iItemCount = objBillProduct.getQuantity() - objBillProduct.getCanceled() - objBillProduct.getReturned() - objBillProduct.getPaid();
            if(iItemCount == 0) {
                this.m_lstHiddenPositions.add(iProductCounter);
            }
            iProductCounter++;
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.am_bill_recyclerview_recyclerview_items, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        //code snippet for hidden items
        for(Integer hiddenIndex : m_lstHiddenPositions) {
            if(hiddenIndex <= position) {
                position = position + 1;
            }
        }

        DecimalFormat df = new DecimalFormat("0.00");
        final ObjBillProduct item = billproductList.get(position);

        int iItemCount = item.getQuantity() - item.getCanceled() - item.getReturned() - item.getPaid();
        //set name
        String strName = (item.getQuantity() - item.getCanceled() - item.getReturned() - item.getPaid()) + "x " + item.getProduct().getName();
        holder.textview_itemname.setText(strName);

        //set prize
        double prize =  iItemCount * item.getProduct().getVK();
        String strVK = df.format(prize);
        strVK = strVK + "€";
        holder.textview_prize.setText(strVK);

        //set image printer
        String strPrinted = item.getPrinted() + "x";
        holder.textview_printerQ.setText(strPrinted);
        holder.imageview_printer.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return billproductList.size() - m_lstHiddenPositions.size();
    }
}
