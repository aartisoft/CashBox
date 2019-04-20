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
import objects.ObjMainBillProduct;
import objects.ObjPrinter;
import objects.ObjProduct;

public class RecyclerViewMainBillAdapter extends RecyclerView.Adapter<RecyclerViewMainBillAdapter.MyViewHolder>{
    private Context context;
    private List<ObjMainBillProduct> m_billproductList = new ArrayList<>();


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
            ObjMainBillProduct objMainBillProduct = m_billproductList.get(position);
            //implement interface instead if adapter is used in more than one activity!
            ((Main)context).showRetoureStornoDialog(objMainBillProduct.getProduct().getCategory(), objMainBillProduct.getProduct().getName());
            return false;
        }
    }

    public RecyclerViewMainBillAdapter(Context context, List<ObjBillProduct> billproductList) {
        this.context = context;


        m_billproductList = new ArrayList<>();

        //set shown false
        for(ObjBillProduct objBillProduct : billproductList) {
            objBillProduct.setShown(false);
        }

        //set list
        for(ObjBillProduct objBillProductAdapter : billproductList){
            if(!objBillProductAdapter.getPaid() && !objBillProductAdapter.getCanceled()
                    && !objBillProductAdapter.getReturned() && !objBillProductAdapter.isShown()){
                //init variables
                ObjBillProduct objBillProductSearch = objBillProductAdapter;
                ObjMainBillProduct objMainBillProduct = new ObjMainBillProduct();
                objMainBillProduct.setProduct(objBillProductSearch.getProduct());
                int iQuantity = 0;
                int iPrinted = 0;
                double dPrize = 0.0;
                boolean bFound = false;

                for(ObjBillProduct objBillProduct : billproductList){
                    if(objBillProduct.getProduct() == objBillProductSearch.getProduct()){
                        if(!objBillProduct.getPaid() && !objBillProduct.getCanceled()
                                && !objBillProduct.getReturned() && !objBillProduct.isShown()){
                            iQuantity++;
                            dPrize += objBillProduct.getVK();
                            if(objBillProduct.getPrinted()){
                                iPrinted++;
                            }
                            objBillProduct.setShown(true);
                            bFound = true;
                        }
                    }
                }
                if(bFound){
                    objMainBillProduct.setQuantity(iQuantity);
                    objMainBillProduct.setPrinted(iPrinted);
                    objMainBillProduct.setVK(dPrize);

                    //add to adapter list
                    this.m_billproductList.add(objMainBillProduct);
                }
            }
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
        holder.mCardView.setTag(position);

        DecimalFormat df = new DecimalFormat("0.00");
        final ObjMainBillProduct item = m_billproductList.get(position);

        //set name
        String strName = item.getQuantity() + "x " + item.getProduct().getName();
        holder.textview_itemname.setText(strName);

        //set prize
        String strVK = df.format(item.getVK());
        strVK = strVK + "€";
        holder.textview_prize.setText(strVK);

        //set image printer
        String strPrinted = item.getPrinted() + "x";
        holder.textview_printerQ.setText(strPrinted);
        holder.imageview_printer.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return m_billproductList.size();
    }
}
