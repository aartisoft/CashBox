package adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dd.cashbox.MainShowBills;
import com.example.dd.cashbox.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import global.GlobVar;
import objects.ObjBill;
import objects.ObjBillProduct;
import objects.ObjCategory;
import objects.ObjProduct;

public class ListViewAllBillAdapter extends BaseExpandableListAdapter {

    private Context m_Context;
    private List<ObjBill> m_List;

    public ListViewAllBillAdapter(Context context, List<ObjBill> bills) {
        super();
        this.m_Context = context;
        this.m_List = bills;
    }

    public ObjBill getObjBill(int position){
        return m_List.get(position);
    }


    @Override
    public int getGroupCount() {
        return m_List.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupPosition;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return 1;
    }

    @Override
    public long getGroupId(int groupPosition) {
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


        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            view = new ViewHolderParent();
            convertView = inflator.inflate(R.layout.childfragment_allbills_lvitems_parent,  parent, false);

            // Lookup view for data population
            view.txtBill= (TextView) convertView.findViewById(R.id.childfragment_allbills_lvitems_child_billnr);
            view.txtBillDate= (TextView) convertView.findViewById(R.id.childfragment_allbills_lvitems_child_billdate);
            view.ivChoose = convertView.findViewById(R.id.childfragment_allbills_lvitems_child_iv);

            convertView.setTag(view);
        } else {
            view = (ListViewAllBillAdapter.ViewHolderParent) convertView.getTag();
        }

        // Populate the data into the template view using the data object
        String strBillName = m_Context.getResources().getString(R.string.src_Tisch) + " " + String.valueOf(m_List.get(groupPosition).getTable())
                + " - " + m_Context.getResources().getString(R.string.src_Beleg) + " " + String.valueOf(m_List.get(groupPosition).getBillNr());
        view.txtBill.setText(strBillName);
        view.txtBillDate.setText(m_List.get(groupPosition).getBillingDate());

        //item delete listener
        final ViewHolderParent OnClickView = view;
        final int OnClickPosition = groupPosition;
        final View.OnClickListener ChooseListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnClickView.ivChoose.setImageDrawable(m_Context.getResources().getDrawable(R.drawable.ic_search_grey_24dp));
                ((MainShowBills)m_Context).openBill(OnClickPosition);
            }
        };
        view.ivChoose.setOnClickListener(ChooseListener);

        // Return the completed view to render on screen
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolderChild view = null;
        LayoutInflater inflator = ((Activity) m_Context).getLayoutInflater();


        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            view = new ViewHolderChild();
            convertView = inflator.inflate(R.layout.childfragment_allbills_lvitems_child,  parent, false);

            // Lookup view for data population
            view.txtArticles = (TextView) convertView.findViewById(R.id.childfragment_allbills_lvitems_child_tv);

            convertView.setTag(view);
        } else {
            view = (ListViewAllBillAdapter.ViewHolderChild) convertView.getTag();
        }

        //populate articles
        //get item quantitiy
        double dPrize = 0.00;
        String strAllArticles = "";
        for(ObjCategory objCategory : GlobVar.g_lstCategory) {
            for(ObjProduct objProduct : objCategory.getListProduct()) {
                String strArticle = objProduct.getName();
                //if pawn is available
                if(objProduct.getbPawn()){
                    strArticle += "*";
                }

                //printed and paid
                int iQuantity = 0;
                boolean bFound = false;
                for(ObjBillProduct objBillProduct : m_List.get(groupPosition).m_lstProducts) {
                    if(!objBillProduct.getCanceled() && !objBillProduct.getReturned() && objBillProduct.getPrinted() && objBillProduct.getPaid()){
                        if (objProduct == objBillProduct.getProduct()) {
                            dPrize += objBillProduct.getVK();
                            //if pawn is available
                            if(objBillProduct.getProduct().getbPawn()){
                                dPrize += objBillProduct.getProduct().getPawn();
                            }

                            iQuantity++;
                            bFound = true;
                        }
                    }
                }
                if(bFound){
                    strAllArticles += iQuantity + "x " + strArticle  + " " + m_Context.getResources().getString(R.string.src_bezahlt) + "\n";
                }

                //printed and not paid
                iQuantity = 0;
                bFound = false;
                for(ObjBillProduct objBillProduct : m_List.get(groupPosition).m_lstProducts) {
                    if(!objBillProduct.getCanceled() && !objBillProduct.getReturned() && objBillProduct.getPrinted() && !objBillProduct.getPaid()){
                        if (objProduct == objBillProduct.getProduct()) {
                            dPrize += objBillProduct.getVK();
                            //if pawn is available
                            if(objBillProduct.getProduct().getbPawn()){
                                dPrize += objBillProduct.getProduct().getPawn();
                            }

                            iQuantity++;
                            bFound = true;
                        }
                    }
                }
                if(bFound){
                    strAllArticles += iQuantity + "x " + strArticle  + " " + m_Context.getResources().getString(R.string.src_offen) + "\n";
                }

                //not printed
                iQuantity = 0;
                bFound = false;
                for(ObjBillProduct objBillProduct : m_List.get(groupPosition).m_lstProducts) {
                    if(!objBillProduct.getCanceled() && !objBillProduct.getReturned() && !objBillProduct.getPaid() && !objBillProduct.getPrinted()){
                        if (objProduct == objBillProduct.getProduct()) {
                            dPrize += objBillProduct.getVK();
                            //if pawn is available
                            if(objBillProduct.getProduct().getbPawn()){
                                dPrize += objBillProduct.getProduct().getPawn();
                            }

                            iQuantity++;
                            bFound = true;
                        }
                    }
                }
                if(bFound){
                    strAllArticles += iQuantity + "x " + strArticle + " " + m_Context.getResources().getString(R.string.src_nichtgedruckt) + "\n";
                }
            }
        }

        //populate open sum
        String strSum;
        DecimalFormat df = new DecimalFormat("0.00");
        strSum = df.format(dPrize);
        strSum = strSum + "€";

        //concat articles and sum
        String strChild = strAllArticles + "\n" + m_Context.getString(R.string.src_Summme) + " "+ strSum;

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
        public ImageView ivChoose;
    }

    public class ViewHolderChild {
        public TextView txtArticles;
    }
}