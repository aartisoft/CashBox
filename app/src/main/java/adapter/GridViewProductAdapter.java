package adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.example.dd.cashbox.R;

import java.util.List;

import objects.ObjProduct;

public class GridViewProductAdapter extends BaseAdapter {

    private List<ObjProduct> m_lstProducts;
    private Context m_Context;
    private int m_iProdColor;

    public GridViewProductAdapter(Context context, List<ObjProduct> p_lstProducts, int p_iProdColor){
        this.m_Context = context;
        this.m_lstProducts = p_lstProducts;
        this.m_iProdColor = p_iProdColor;
    }

    @Override
    public int getCount() {
        return m_lstProducts.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        GridView grid = (GridView)parent;
        int size = grid.getRequestedColumnWidth();

        TextView tempTextView = new TextView(m_Context);
        tempTextView.setText(m_lstProducts.get(position).getName());
        tempTextView.setLayoutParams(new GridView.LayoutParams(size, 150));


        tempTextView.setBackgroundColor(m_iProdColor);
        tempTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tempTextView.setTypeface(null, Typeface.BOLD);
        return tempTextView;
    }
}
