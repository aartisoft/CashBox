package objects;

import java.util.ArrayList;
import java.util.List;

public class ObjXZBon {

    public ObjXZBon(){}

    //main variables
    private boolean m_bZBon = false;
    private String m_strDate = "";

    //general information
    private String m_strName = "";
    private String m_strFirstReceipt = "";
    private String m_strLastReceipt = "";
    private int m_iOpenReceipts = 0;

    //turnover products
    private List<ObjAllProductsXZBon> m_lstAllProductSales = new ArrayList<>();
    private double m_AllProductSalesNettoSum = 0.0;
    private double m_AllProductSalesBruttoSum = 0.0;
    private double m_dSumTaxes = 0.0;
    private double m_dSumTips = 0.0;

    //turnover categories
    private List<ObjAllCategoriesXZBon> m_lstAllCategorySales = new ArrayList<>();
    private double m_AllCategorySalesNettoSum = 0.0;
    private double m_AllCategorySalesBruttoSum = 0.0;

    //turnover taxes
    private List<ObjAllTaxesXZBon> m_lstAllTaxSales = new ArrayList<>();
    private double m_AllTaxSalesSum = 0.0;
    private double m_AllTaxSalesNettoSum = 0.0;
    private double m_AllTaxSalesBruttoSum = 0.0;

    //turnover cancellations
    private List<ObjAllCancellationsXZBon> m_lstAllCancellationSales = new ArrayList<>();
    private double m_AllCancellationSalesNettoSum = 0.0;
    private double m_AllCancellationSalesBruttoSum = 0.0;



    public class ObjAllProductsXZBon{
        public ObjAllProductsXZBon(){}

        private int m_iCountProducts = 0;
        private String m_strProductName = "";
        private double m_dNetto = 0.0;
        private double m_dBrutto = 0.0;
    }

    public class ObjAllCategoriesXZBon{
        public ObjAllCategoriesXZBon(){}

        private String m_strCategoryName = "";
        private double m_dNetto = 0.0;
        private double m_dBrutto = 0.0;
    }

    public class ObjAllTaxesXZBon{
        public ObjAllTaxesXZBon(){}

        private String m_strTaxName = "";
        private double m_dTax = 0.0;
        private double m_dNetto = 0.0;
        private double m_dBrutto = 0.0;
    }

    public class ObjAllCancellationsXZBon{
        public ObjAllCancellationsXZBon(){}

        private String m_strBillNr = "";
        private int m_iCountProducts = 0;
        private String m_strProductName = "";
        private double m_dNetto = 0.0;
        private double m_dBrutto = 0.0;
    }
}
