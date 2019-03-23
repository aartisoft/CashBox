package adapter;

import android.app.Application;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.dd.cashbox.R;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import fragments.ViewPagerRegisterFragment;
import objects.ObjCategory;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private List<String> m_lstTitle = new ArrayList<String>();
    private List<Integer> m_lstColor = new ArrayList<Integer>();
    private final List<Fragment> m_FragmentList = new ArrayList<>();
    private Context m_Context;

    public ViewPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        return m_FragmentList.get(position);
    }

    @Override
    public int getCount() {
        return m_FragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return m_lstTitle.get(position);
    }

    public void addFragment(Fragment fragment, String title, int color,  Context context) {
        m_FragmentList.add(fragment);
        m_lstTitle.add(title);
        m_lstColor.add(color);
        m_Context = context;
    }

    public View getTabView(int position) {
        // Given you have a custom layout in `res/layout/custom_tab.xml` with a TextView and ImageView
        View v = LayoutInflater.from(m_Context).inflate(R.layout.am_register_tablayout, null);
        TextView tv = (TextView) v.findViewById(R.id.am_register_tablayout_tv);
        tv.setText(m_lstTitle.get(position));
        tv.setBackgroundColor(m_lstColor.get(position));
        return v;
    }
}
