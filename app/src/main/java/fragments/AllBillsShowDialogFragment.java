package fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.dd.cashbox.R;
import com.google.android.material.tabs.TabLayout;

import adapter.ViewPagerRetoureStornoAdapter;
import global.GlobVar;
import objects.ObjBill;
import pdf.CreateBillPdf;


import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.shockwave.pdfium.PdfDocument;

import java.io.File;
import java.util.List;

public class AllBillsShowDialogFragment extends DialogFragment implements OnPageChangeListener,OnLoadCompleteListener {

    private int m_iSessionTable = -1;
    private int m_iSessionBill = -1;
    private FragmentActivity m_Context;
    private PDFView m_pdfView;
    private Integer m_iPageNumber = 0;
    private String m_strPdfFileName;
    private String m_strPdfFilePath;
    private static File m_PdfFilePath;
    private static final String TAG = AllBillsShowDialogFragment.class.getSimpleName();
    private static AllBillsShowDialogFragment m_frag;

    public AllBillsShowDialogFragment() { }

    public static AllBillsShowDialogFragment newInstance() {
        m_frag = new AllBillsShowDialogFragment();
        return m_frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_allbillsshow, container, false);

        //activity variables
        m_iSessionTable = getArguments().getInt("TABLE", -1);
        m_iSessionBill = getArguments().getInt("BILL", -1);

        //set UI
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        Toolbar toolbar = view.findViewById(R.id.fragment_allbillshow_tb);
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        toolbar.setNavigationOnClickListener(tbOnClickListener);

        //set variables
        m_Context = getActivity();
        m_pdfView = view.findViewById(R.id.fragment_allbillshow_pdf);

        //create pdf
        createPdf();

        //show pdf
        displayFromAsset(m_strPdfFilePath);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    ///////////////////// LISTENER /////////////////////////////////////////////////////////////////////
    View.OnClickListener tbOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            m_frag.dismiss();
        }
    };

    @Override
    public void loadComplete(int nbPages) {
        PdfDocument.Meta meta = m_pdfView.getDocumentMeta();
        printBookmarksTree(m_pdfView.getTableOfContents(), "-");
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        m_iPageNumber = page;
        //setTitle(String.format("%s %s / %s", pdfFileName, page + 1, pageCount));
    }




    ///////////////////// METHODS /////////////////////////////////////////////////////////////////////
    private void displayFromAsset(String assetFileName) {
        m_strPdfFileName = assetFileName;

        /*m_pdfView.fromAsset(m_strPdfFilePath)
                .defaultPage(m_iPageNumber)
                .enableSwipe(true)

                .swipeHorizontal(false)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(m_Context))
                .load();*/
        m_pdfView.fromFile(m_PdfFilePath);

    }

    private void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {

            Log.e(TAG, String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));

            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }

    private void createPdf(){
        CreateBillPdf createBillPdf = new CreateBillPdf(m_Context);

        m_PdfFilePath = createBillPdf.createBillPdf();
    }

    private int getBillListPointer() {
        //get bill
        int iBill = 0;
        for (ObjBill objBill : GlobVar.g_lstTableBills.get(m_iSessionTable)) {
            if (objBill.getBillNr() == m_iSessionBill) {
                return iBill;
            }
            iBill++;
        }
        return 0;
    }
}
