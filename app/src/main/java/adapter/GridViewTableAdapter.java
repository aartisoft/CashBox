package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.example.dd.cashbox.R;

import java.text.DecimalFormat;
import java.util.List;

import objects.ObjBillProducts;
import objects.ObjProduct;

public class GridViewTableAdapter extends BaseAdapter {

    private int m_iTables;
    private Context m_Context;

    public GridViewTableAdapter(Context context, int p_iTables){
        this.m_Context = context;
        this.m_iTables = p_iTables;
    }

    @Override
    public int getCount() {
        return m_iTables;
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

        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(m_Context);
            convertView = layoutInflater.inflate(R.layout.activity_main_showtables_layout, null);
        }

        GridView grid = (GridView)parent;
        int size = grid.getRequestedColumnWidth();

        //title
        TextView titleTextView = convertView.findViewById(R.id.activity_main_showtables_layout_tv);
        titleTextView.setText(m_Context.getResources().getString(R.string.src_Tisch) + position);

        return convertView;
    }
}
