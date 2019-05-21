package fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.dd.cashbox.R;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerCurrCashPosTabs extends FragmentPagerAdapter {

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();
    private Context m_Context;

    public ViewPagerCurrCashPosTabs(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }


    public void addFragment(Fragment fragment, String title, Context context) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
        m_Context = context;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

    public View getTabView(int position) {
        // Given you have a custom layout in `res/layout/custom_tab.xml` with a TextView and ImageView
        View v = LayoutInflater.from(m_Context).inflate(R.layout.activity_currcashpos_tablayout, null);
        TextView tv = (TextView) v.findViewById(R.id.activity_currcashpos_tablayout_tv);
        tv.setText(mFragmentTitleList.get(position));

        return v;
    }
}