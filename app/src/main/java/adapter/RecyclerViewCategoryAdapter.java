package adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dd.cashbox.R;

import java.util.List;

import objects.ObjCategory;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

public class RecyclerViewCategoryAdapter extends RecyclerView.Adapter<RecyclerViewCategoryAdapter.MyViewHolder>{
    private Context context;
    private List<ObjCategory> categoryList;
    private OnItemClickListener m_onItemClickListener;


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView textview_itemname;
        public RelativeLayout viewBackground, viewForeground;
        OnItemClickListener onItemClickListener;

        public MyViewHolder(@NonNull View view, OnItemClickListener onItemClickListener) {
            super(view);
            this.onItemClickListener = onItemClickListener;

            textview_itemname = view.findViewById(R.id.editcategory_recycview_textviewname);
            viewBackground = view.findViewById(R.id.editcategory_recycview_background);
            viewForeground = view.findViewById(R.id.editcategory_recycview_foreground);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(v, getAdapterPosition());
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

        /*Glide.with(context)
                   .load(item.getThumbnail())
                   .into(holder.thumbnail);*/
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
         void onItemClick(View view, int position);
    }
}
