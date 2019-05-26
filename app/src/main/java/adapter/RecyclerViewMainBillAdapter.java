package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.dd.cashbox.Main;
import com.example.dd.cashbox.R;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import global.GlobVar;
import objects.ObjMainBillProduct;

public class RecyclerViewMainBillAdapter extends RecyclerView.Adapter<RecyclerViewMainBillAdapter.MyViewHolder>{
    private Context context;
    private List<ObjMainBillProduct> m_billproductList = new ArrayList<>();


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView textview_itemname;
        public TextView textview_prize;
        public TextView textview_paidQ;
        public ImageView imageview_paid;
        public TextView textview_canceledQ;
        public TextView textview_returnedQ;
        public TextView textview_printerQ;
        public ImageView imageview_printer;
        public View mCardView;

        public MyViewHolder(@NonNull View view) {
            super(view);

            textview_itemname = view.findViewById(R.id.am_bill_rv_name);
            textview_prize = view.findViewById(R.id.am_bill_rv_prize);
            textview_paidQ = view.findViewById(R.id.am_bill_rv_paid_tv);
            imageview_paid = view.findViewById(R.id.am_bill_rv_paid_iv);
            textview_canceledQ = view.findViewById(R.id.am_bill_rv_storno_tv);
            textview_returnedQ = view.findViewById(R.id.am_bill_rv_retoure_tv);
            textview_printerQ = view.findViewById(R.id.am_bill_rv_printer_tv);
            imageview_printer = view.findViewById(R.id.am_bill_rv_printerimage);

            mCardView = view.findViewById(R.id.editproduct_recyclerview_items);
            mCardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            ObjMainBillProduct objMainBillProduct = m_billproductList.get(position);
            //implement interface instead if adapter is used in more than one activity!
            ((Main)context).showRetoureStornoDialog(objMainBillProduct.getProduct().getCategory(), objMainBillProduct.getProduct().getName());
        }
    }

    public RecyclerViewMainBillAdapter(Context context, List<ObjMainBillProduct> billproductList) {
        this.context = context;
        this.m_billproductList = billproductList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.am_bill_recyclerview_recyclerview_items, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.mCardView.setTag(position);

        DecimalFormat df = new DecimalFormat("0.00");
        final ObjMainBillProduct item = m_billproductList.get(position);

        //set name
        String strName = item.getQuantity() + "x " + item.getProduct().getName();
        //if pawn is available
        if(item.getProduct().getbPawn()){
            strName += "*";
        }
        holder.textview_itemname.setText(strName);

        //set prize
        String strVK = df.format(item.getVK());
        strVK = strVK + "€";
        holder.textview_prize.setText(strVK);

        //set image paid
        String strPaid = item.getPaid() + "x B";
        holder.textview_paidQ.setText(strPaid);
        holder.imageview_paid.setVisibility(View.GONE);

        //set canceled
        String strCanceled = item.getPaid() + "x S";
        holder.textview_canceledQ.setText(strCanceled);

        //set returned
        String strReturned = item.getPaid() + "x R";
        holder.textview_returnedQ.setText(strReturned);

        //set image printer
        //if used as main cash register
        if(GlobVar.g_bUseMainCash){
            holder.imageview_printer.setVisibility(View.INVISIBLE);
            holder.textview_printerQ.setVisibility(View.INVISIBLE);
        }
        else{
            holder.imageview_printer.setVisibility(View.VISIBLE);
            String strPrinted = item.getPrinted() + "x";
            holder.textview_printerQ.setText(strPrinted);
        }
    }

    @Override
    public int getItemCount() {
        return m_billproductList.size();
    }
}
