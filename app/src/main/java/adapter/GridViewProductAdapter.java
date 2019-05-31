package adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.example.dd.cashbox.R;

import java.text.DecimalFormat;
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
    public ObjProduct getItem(int position) {
        return m_lstProducts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(m_Context);
            convertView = layoutInflater.inflate(R.layout.fragment_tabregister_layout, null);
        }

        GridView grid = (GridView)parent;
        int size = grid.getRequestedColumnWidth();

        //title
        TextView titleTextView = convertView.findViewById(R.id.fragm_tabregister_layout_tvtitle);
        titleTextView.setText(m_lstProducts.get(position).getName());
        titleTextView.setBackgroundColor(m_iProdColor);

        //subtitle
        TextView subtitleTextView = convertView.findViewById(R.id.fragm_tabregister_layout_tvsubtitle);
        subtitleTextView.setBackgroundColor(m_iProdColor);
        DecimalFormat df = new DecimalFormat("0.00");
        String strVK = df.format(m_lstProducts.get(position).getVK());
        strVK = strVK + "€";
        subtitleTextView.setText(strVK);

        //subtitle pawn
        TextView subtitlePawnTextView = convertView.findViewById(R.id.fragm_tabregister_layout_tvpawn);
        subtitlePawnTextView.setBackgroundColor(m_iProdColor);
        //if pawn is available
        String strPawn = "";
        if(m_lstProducts.get(position).getbPawn()){
            strPawn = m_Context.getResources().getString(R.string.src_Pfand);
            strPawn += " " + df.format(m_lstProducts.get(position).getPawn());
            strPawn += "€";

        }
        subtitlePawnTextView.setText(strPawn);

        return convertView;
    }
}
