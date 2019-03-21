package fragments;


import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.dd.cashbox.R;

import adapter.GridViewProductAdapter;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import global.GlobVar;

public class ViewPagerRegisterFragment extends Fragment {

    int position;
    private GridView m_GridView;
    private GridViewProductAdapter m_gridViewProductAdapter;

    public static Fragment getInstance(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("pos", position);
        ViewPagerRegisterFragment tabFragment = new ViewPagerRegisterFragment();
        tabFragment.setArguments(bundle);
        return tabFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt("pos");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tabregister, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        m_GridView = view.findViewById(R.id.fragment_tagregister_gridview);

        m_gridViewProductAdapter = new GridViewProductAdapter(getActivity().getApplicationContext(),
                                    GlobVar.g_lstCategory.get(position).getListProduct(), GlobVar.g_lstCategory.get(position).getProdColor());
        m_GridView.setAdapter(m_gridViewProductAdapter);
    }
}
