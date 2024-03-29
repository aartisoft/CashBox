package adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dd.cashbox.R;

import java.util.List;

import global.GlobVar;
import objects.ObjCategory;
import objects.ObjPrinter;

public class RecyclerViewCategoryAdapter extends RecyclerView.Adapter<RecyclerViewCategoryAdapter.MyViewHolder>{
    private Context context;
    private List<ObjCategory> categoryList;
    private OnItemClickListener m_onItemClickListener;


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView textview_itemname;
        public TextView textview_printername;
        public TextView textview_status;

        OnItemClickListener onItemClickListener;

        public MyViewHolder(@NonNull View view, OnItemClickListener onItemClickListener) {
            super(view);
            this.onItemClickListener = onItemClickListener;

            textview_itemname = view.findViewById(R.id.editcategory_rv_items_name);
            textview_printername = view.findViewById(R.id.editcategory_rv_items_printer);
            textview_status = view.findViewById(R.id.editcategory_rv_items_status);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(getAdapterPosition());
        }
    }

    public RecyclerViewCategoryAdapter(Context context, List<ObjCategory> categoryList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.categoryList = categoryList;
        this.m_onItemClickListener = onItemClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.editcategory_recyclerview_items, parent, false);

        return new MyViewHolder(itemView, m_onItemClickListener);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final ObjCategory item = categoryList.get(position);
        holder.textview_itemname.setText((item).getName());


        //if used as main cash register
        if(GlobVar.g_bUseMainCash && !GlobVar.g_bUseBonPrint){
            holder.textview_printername.setVisibility(View.GONE);
        }
        else{
            //get printername
            ObjPrinter printer = (item).getPrinter();
            String printername;
            if(printer != null){
                printername = printer.getDeviceName() + " - " + printer.getMacAddress();
            }
            else{
                printername = context.getResources().getString(R.string.src_KeinDruckerHinzugefuegt);
            }

            holder.textview_printername.setText(printername);
        }

        //get status
        boolean bstatus = (item).getEnabled();
        String status;
        if(bstatus){
            status = context.getResources().getString(R.string.src_Aktiv);
        }
        else{
            status = context.getResources().getString(R.string.src_Inaktiv);
        }
        holder.textview_status.setText(status);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public void removeItem(int position) {
        categoryList.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(ObjCategory item, int position) {
        categoryList.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }

    public interface OnItemClickListener{
         void onItemClick(int position);
    }
}
