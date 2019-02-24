package adapter;

import android.content.ClipData;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.dd.cashbox.R;

import java.util.List;

import objects.ObjCategory;

import com.bumptech.glide.Glide;

public class RecyclerViewCategoryAdapter extends RecyclerView.Adapter<RecyclerViewCategoryAdapter.MyViewHolder> {
    private Context context;
    private List<ObjCategory> categoryList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textview_itemname;
        public RelativeLayout viewBackground, viewForeground;

        public MyViewHolder(View view) {
            super(view);
            textview_itemname = view.findViewById(R.id.editcategory_recycview_textviewname);
            viewBackground = view.findViewById(R.id.editcategory_recycview_background);
            viewForeground = view.findViewById(R.id.editcategory_recycview_foreground);
        }
    }


    public RecyclerViewCategoryAdapter(Context context, List<ObjCategory> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.editcategory_recyclerview_items, parent, false);

        return new MyViewHolder(itemView);
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
}
