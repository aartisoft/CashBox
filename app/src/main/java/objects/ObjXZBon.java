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
    public List<ObjAllProductsXZBon> m_lstAllProductSales = new ArrayList<>();
    private double m_dAllProductSalesNettoSum = 0.0;
    private double m_dAllProductSalesBruttoSum = 0.0;
    private double m_dSumTaxes = 0.0;
    private double m_dSumTips = 0.0;

    //turnover categories
    public List<ObjAllCategoriesXZBon> m_lstAllCategorySales = new ArrayList<>();
    private double m_dAllCategorySalesNettoSum = 0.0;
    private double m_dAllCategorySalesBruttoSum = 0.0;

    //turnover taxes
    public List<ObjAllTaxesXZBon> m_lstAllTaxSales = new ArrayList<>();
    private double m_dAllTaxSalesSum = 0.0;
    private double m_dAllTaxSalesNettoSum = 0.0;
    private double m_dAllTaxSalesBruttoSum = 0.0;

    //turnover cancellations
    public List<ObjAllCancellationsXZBon> m_lstAllCancellationSales = new ArrayList<>();
    private double m_dAllCancellationSalesNettoSum = 0.0;
    private double m_dAllCancellationSalesBruttoSum = 0.0;

    //turnover return
    public List<ObjAllReturnedXZBon> m_lstAllReturnSales = new ArrayList<>();
    private double m_dAllReturnSalesNettoSum = 0.0;
    private double m_dAllReturnSalesBruttoSum = 0.0;

    public class ObjAllProductsXZBon{
        public ObjAllProductsXZBon(){}

        public int m_iCountProducts = 0;
        public String m_strProductName = "";
        public double m_dNetto = 0.0;
        public double m_dBrutto = 0.0;
    }

    public class ObjAllCategoriesXZBon{
        public ObjAllCategoriesXZBon(){}

        public String m_strCategoryName = "";
        public double m_dNetto = 0.0;
        public double m_dBrutto = 0.0;
    }

    public class ObjAllTaxesXZBon{
        public ObjAllTaxesXZBon(){}

        public String m_strTaxName = "";
        public double m_dTax = 0.0;
        public double m_dNetto = 0.0;
        public double m_dBrutto = 0.0;
    }

    public class ObjAllCancellationsXZBon{
        public ObjAllCancellationsXZBon(){}

        public String m_strBillNr = "";
        public int m_iCountProducts = 0;
        public String m_strProductName = "";
        public double m_dNetto = 0.0;
        public double m_dBrutto = 0.0;
    }

    public class ObjAllReturnedXZBon{
        public ObjAllReturnedXZBon(){}

        public String m_strBillNr = "";
        public int m_iCountProducts = 0;
        public String m_strProductName = "";
        public double m_dNetto = 0.0;
        public double m_dBrutto = 0.0;
    }


    ///////////////////////////////////// METHODS ///////////////////////////////////////////////////////////

    //GETTER
    public boolean isbZBon() {
        return m_bZBon;
    }

    public String getstrDate() {
        return m_strDate;
    }

    public String getstrName() {
        return m_strName;
    }

    public String getstrFirstReceipt() {
        return m_strFirstReceipt;
    }

    public String getstrLastReceipt() {
        return m_strLastReceipt;
    }

    public int getiOpenReceipts() {
        return m_iOpenReceipts;
    }

    public double getdAllProductSalesNettoSum() {
        return m_dAllProductSalesNettoSum;
    }

    public double getdAllProductSalesBruttoSum() {
        return m_dAllProductSalesBruttoSum;
    }

    public double getdSumTaxes() {
        return m_dSumTaxes;
    }

    public double getdSumTips() {
        return m_dSumTips;
    }

    public double getdAllCategorySalesNettoSum() {
        return m_dAllCategorySalesNettoSum;
    }

    public double getdAllCategorySalesBruttoSum() {
        return m_dAllCategorySalesBruttoSum;
    }

    public double getdAllTaxSalesSum() {
        return m_dAllTaxSalesSum;
    }

    public double getdAllTaxSalesNettoSum() {
        return m_dAllTaxSalesNettoSum;
    }

    public double getdAllTaxSalesBruttoSum() {
        return m_dAllTaxSalesBruttoSum;
    }

    public double getdAllCancellationSalesNettoSum() {
        return m_dAllCancellationSalesNettoSum;
    }

    public double getdAllCancellationSalesBruttoSum() {
        return m_dAllCancellationSalesBruttoSum;
    }

    public double getdAllReturnSalesNettoSum() {
        return m_dAllReturnSalesNettoSum;
    }

    public double getdAllReturnSalesBruttoSum() {
        return m_dAllReturnSalesBruttoSum;
    }


    //SETTER
    public void setbZBon(boolean m_bZBon) {
        this.m_bZBon = m_bZBon;
    }

    public void setstrDate(String m_strDate) {
        this.m_strDate = m_strDate;
    }

    public void setstrName(String m_strName) {
        this.m_strName = m_strName;
    }

    public void setstrFirstReceipt(String m_strFirstReceipt) {
        this.m_strFirstReceipt = m_strFirstReceipt;
    }

    public void setstrLastReceipt(String m_strLastReceipt) {
        this.m_strLastReceipt = m_strLastReceipt;
    }

    public void setiOpenReceipts(int m_iOpenReceipts) {
        this.m_iOpenReceipts = m_iOpenReceipts;
    }

    public void setdAllProductSalesNettoSum(double m_dAllProductSalesNettoSum) {
        this.m_dAllProductSalesNettoSum = m_dAllProductSalesNettoSum;
    }

    public void setdAllProductSalesBruttoSum(double m_dAllProductSalesBruttoSum) {
        this.m_dAllProductSalesBruttoSum = m_dAllProductSalesBruttoSum;
    }

    public void setdSumTaxes(double m_dSumTaxes) {
        this.m_dSumTaxes = m_dSumTaxes;
    }

    public void setdSumTips(double m_dSumTips) {
        this.m_dSumTips = m_dSumTips;
    }

    public void setdAllCategorySalesNettoSum(double m_dAllCategorySalesNettoSum) {
        this.m_dAllCategorySalesNettoSum = m_dAllCategorySalesNettoSum;
    }

    public void setdAllCategorySalesBruttoSum(double m_dAllCategorySalesBruttoSum) {
        this.m_dAllCategorySalesBruttoSum = m_dAllCategorySalesBruttoSum;
    }

    public void setdAllTaxSalesSum(double m_dAllTaxSalesSum) {
        this.m_dAllTaxSalesSum = m_dAllTaxSalesSum;
    }

    public void setdAllTaxSalesNettoSum(double m_dAllTaxSalesNettoSum) {
        this.m_dAllTaxSalesNettoSum = m_dAllTaxSalesNettoSum;
    }

    public void setdAllTaxSalesBruttoSum(double m_dAllTaxSalesBruttoSum) {
        this.m_dAllTaxSalesBruttoSum = m_dAllTaxSalesBruttoSum;
    }

    public void setdAllCancellationSalesNettoSum(double m_dAllCancellationSalesNettoSum) {
        this.m_dAllCancellationSalesNettoSum = m_dAllCancellationSalesNettoSum;
    }

    public void setdAllCancellationSalesBruttoSum(double m_dAllCancellationSalesBruttoSum) {
        this.m_dAllCancellationSalesBruttoSum = m_dAllCancellationSalesBruttoSum;
    }

    public void setdAllReturnSalesNettoSum(double m_dAllReturnSalesNettoSum) {
        this.m_dAllReturnSalesNettoSum = m_dAllReturnSalesNettoSum;
    }

    public void setdAllReturnSalesBruttoSum(double m_dAllReturnSalesBruttoSum) {
        this.m_dAllReturnSalesBruttoSum = m_dAllReturnSalesBruttoSum;
    }
}















