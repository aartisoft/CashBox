package adapter;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import fragments.ViewPagerRegisterFragment;
import objects.ObjCategory;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private List<String> m_lstTitle = new ArrayList<String>();

    public ViewPagerAdapter(FragmentManager manager, List<ObjCategory> p_listCategory) {
        super(manager);
        setTitle(p_listCategory);
    }

    public void setTitle(List<ObjCategory> p_listCategory){
        for(ObjCategory objcategory : p_listCategory){
            //set tab only if category is active
            if(objcategory.getEnabled()){
                m_lstTitle.add(objcategory.getName());
            }
        }
    }

    @Override
    public Fragment getItem(int position) {
        return ViewPagerRegisterFragment.getInstance(position);
    }

    @Override
    public int getCount() {
        return m_lstTitle.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return m_lstTitle.get(position);
    }
}
