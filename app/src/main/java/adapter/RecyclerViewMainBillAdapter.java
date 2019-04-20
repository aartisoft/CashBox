package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dd.cashbox.Main;
import com.example.dd.cashbox.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import global.GlobVar;
import objects.ObjBillProduct;
import objects.ObjCategory;
import objects.ObjPrinter;
import objects.ObjProduct;

public class RecyclerViewMainBillAdapter extends RecyclerView.Adapter<RecyclerViewMainBillAdapter.MyViewHolder>{
    private Context context;
    private List<ObjBillProduct> billproductList;
    private ArrayList<Integer> m_lstHiddenPositions = new ArrayList<>();


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        public TextView textview_itemname;
        public TextView textview_prize;
        public TextView textview_printerQ;
        public ImageView imageview_printer;
        public View mCardView;

        public MyViewHolder(@NonNull View view) {
            super(view);

            textview_itemname = view.findViewById(R.id.am_bill_rv_name);
            textview_prize = view.findViewById(R.id.am_bill_rv_prize);
            textview_printerQ = view.findViewById(R.id.am_bill_rv_printer_tv);
            imageview_printer = view.findViewById(R.id.am_bill_rv_printerimage);

            mCardView = view.findViewById(R.id.editproduct_recyclerview_items);
            mCardView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            int position = (int) v.getTag();
            ((Main)context).showRetoureStornoDialog(position); //implement interface instead if adapter is used in more than one activity!
            return false;
        }
    }

    public RecyclerViewMainBillAdapter(Context context, List<ObjBillProduct> billproductList) {
        this.context = context;
        this.billproductList = billproductList;

        int iProductCounter = 0;
        for(ObjBillProduct objBillProduct : billproductList){
            //if product is still open
            if(objBillProduct.getPaid() || objBillProduct.getCanceled() || objBillProduct.getReturned()) {
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

        holder.mCardView.setTag(position);

        DecimalFormat df = new DecimalFormat("0.00");
        final ObjBillProduct item = billproductList.get(position);

        //get item quantitiy
        int iQuantity = 0;
        int iPrinted = 0;
        double dPrize = 0.00;
        for(ObjCategory objCategory : GlobVar.g_lstCategory) {
            for (ObjProduct objProduct : objCategory.getListProduct()) {
                if (objProduct == item.getProduct()) {
                    if (!item.getPaid() || !item.getCanceled() || !item.getReturned()) {
                        dPrize =+ item.getVK();
                        iQuantity++;
                    }
                    if(item.getPrinted()){
                        iPrinted++;
                    }
                }
            }
        }

        //set name
        String strName = iQuantity + "x " + item.getProduct().getName();
        holder.textview_itemname.setText(strName);

        //set prize
        String strVK = df.format(dPrize);
        strVK = strVK + "€";
        holder.textview_prize.setText(strVK);

        //set image printer
        String strPrinted = iPrinted + "x";
        holder.textview_printerQ.setText(strPrinted);
        holder.imageview_printer.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return billproductList.size() - m_lstHiddenPositions.size();
    }
}
