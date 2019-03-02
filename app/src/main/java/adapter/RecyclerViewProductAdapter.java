package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dd.cashbox.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import objects.ObjCategory;
import objects.ObjPrinter;
import objects.ObjProduct;

public class RecyclerViewProductAdapter extends RecyclerView.Adapter<RecyclerViewProductAdapter.MyViewHolder>{
    private Context context;
    private List<ObjProduct> productList;



    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView textview_itemname;
        public TextView textview_category;
        public TextView textview_pawn;
        public TextView textview_vk;

        OnItemClickListener onItemClickListener;

        public MyViewHolder(@NonNull View view) {
            super(view);

            textview_itemname = view.findViewById(R.id.editproduct_rv_items_name);
            textview_category = view.findViewById(R.id.editproduct_rv_items_category);
            textview_pawn = view.findViewById(R.id.editproduct_rv_items_pawn);
            textview_pawn = view.findViewById(R.id.editproduct_rv_items_vk);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(getAdapterPosition());
        }
    }

    public RecyclerViewProductAdapter(Context context, List<ObjProduct> productList) {
        this.context = context;
        this.productList = productList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.editcategory_recyclerview_items, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final ObjProduct item = productList.get(position);
        holder.textview_itemname.setText((item).getName());
        holder.textview_category.setText((item).getCategory());
        holder.textview_vk.setText((item).getVK().toString());

        //set pawn
        String strPawn = "";
        if((item).getbPawn()){
            strPawn = (item).getName();
        }
        holder.textview_pawn.setText(strPawn);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void removeItem(int position) {
        productList.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(ObjProduct item, int position) {
        productList.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }

    public interface OnItemClickListener{
         void onItemClick(int position);
    }
}
