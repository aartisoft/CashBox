package adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.dd.cashbox.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import global.GlobVar;
import objects.ObjBill;
import objects.ObjBillProduct;

public class ListViewBillAdapter extends BaseExpandableListAdapter {

    private Context m_Context;
    private List<ObjBill> m_List;
    private ArrayList<Integer> m_lstHiddenPositions = new ArrayList<>();

    public ListViewBillAdapter(Context context, List<ObjBill> bills) {
        super();
        this.m_Context = context;
        this.m_List = bills;

        int iBillCounter = 0;
        for(ObjBill objBill : bills){
            boolean bProdFound = false;
            for(ObjBillProduct objBillProduct : objBill.m_lstProducts) {
                //if more than one open product available
                int iItemCount = objBillProduct.getQuantity() - objBillProduct.getCanceled() - objBillProduct.getReturned() - objBillProduct.getPaid();
                if (iItemCount > 0) {
                    bProdFound = true;
                    break;
                }
            }
            //if no product found then add to hidden list
            if(!bProdFound){
                this.m_lstHiddenPositions.add(iBillCounter);
            }
            iBillCounter++;
        }
    }

    public ObjBill getObjBill(int position){
        //code snippet for hidden items
        for(Integer hiddenIndex : m_lstHiddenPositions) {
            if(hiddenIndex <= position) {
                position = position + 1;
            }
        }
        return m_List.get(position);
    }


    @Override
    public int getGroupCount() {
        return m_List.size() - m_lstHiddenPositions.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        //code snippet for hidden items
        for(Integer hiddenIndex : m_lstHiddenPositions) {
            if(hiddenIndex <= groupPosition) {
                groupPosition = groupPosition + 1;
            }
        }
        return m_List.get(groupPosition).toString();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return 1;
    }

    @Override
    public long getGroupId(int groupPosition) {
        //code snippet for hidden items
        for(Integer hiddenIndex : m_lstHiddenPositions) {
            if(hiddenIndex <= groupPosition) {
                groupPosition = groupPosition + 1;
            }
        }
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewHolderParent view = null;
        LayoutInflater inflator = ((Activity) m_Context).getLayoutInflater();

        //code snippet for hidden items
        for(Integer hiddenIndex : m_lstHiddenPositions) {
            if(hiddenIndex <= groupPosition) {
                groupPosition = groupPosition + 1;
            }
        }


        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            view = new ViewHolderParent();
            convertView = inflator.inflate(R.layout.activity_main_showbills_itemlistrow_parent,  parent, false);

            // Lookup view for data population
            view.txtBill= (TextView) convertView.findViewById(R.id.activity_main_showbills_ilr_billnr);
            view.txtBillDate= (TextView) convertView.findViewById(R.id.activity_main_showbills_ilr_billdate);

            convertView.setTag(view);
        } else {
            view = (ListViewBillAdapter.ViewHolderParent) convertView.getTag();
        }

        // Populate the data into the template view using the data object
        String strBillName = m_Context.getResources().getString(R.string.src_Beleg) + " " + String.valueOf(m_List.get(groupPosition).getBillNr());
        view.txtBill.setText(strBillName);
        view.txtBillDate.setText(m_List.get(groupPosition).getBillingDate());

        // Return the completed view to render on screen
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolderChild view = null;
        LayoutInflater inflator = ((Activity) m_Context).getLayoutInflater();

        //code snippet for hidden items
        for(Integer hiddenIndex : m_lstHiddenPositions) {
            if(hiddenIndex <= groupPosition) {
                groupPosition = groupPosition + 1;
            }
        }


        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            view = new ViewHolderChild();
            convertView = inflator.inflate(R.layout.activity_main_showbills_itemlistrow_child,  parent, false);

            // Lookup view for data population
            view.txtArticles = (TextView) convertView.findViewById(R.id.activity_main_showbills_ilr_articles);

            convertView.setTag(view);
        } else {
            view = (ListViewBillAdapter.ViewHolderChild) convertView.getTag();
        }

        //populate articles
        String strAllArticles = "";
        for(ObjBillProduct objBillProduct : m_List.get(groupPosition).m_lstProducts){
            int iQuantity = objBillProduct.getQuantity() - objBillProduct.getReturned() - objBillProduct.getCanceled();
            if(iQuantity > 0){
                String strArticle = objBillProduct.getProduct().getName();
                strAllArticles += iQuantity + "x " + strArticle + "\n";
            }
        }

        //populate open sum
        String strOpenSum;
        double prize = 0.00;
        for(ObjBillProduct objBillProduct : m_List.get(groupPosition).m_lstProducts) {
            int iItemCount = objBillProduct.getQuantity() - objBillProduct.getCanceled()
                    - objBillProduct.getReturned() - objBillProduct.getPaid();

            prize += iItemCount * objBillProduct.getProduct().getVK();
        }

        DecimalFormat df = new DecimalFormat("0.00");
        strOpenSum = df.format(prize);
        strOpenSum = strOpenSum + "€";

        //concat articles and sum
        String strChild = strAllArticles + "\n" + m_Context.getString(R.string.src_OffeneSumme) + " "+ strOpenSum;

        view.txtArticles.setText(strChild);

        // Return the completed view to render on screen
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public class ViewHolderParent {
        public TextView txtBill;
        public TextView txtBillDate;
    }

    public class ViewHolderChild {
        public TextView txtArticles;
    }
}