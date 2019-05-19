package fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.dd.cashbox.R;

import java.util.ArrayList;
import java.util.List;

public class CurrCashPosTabSummaryFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)    {
        View  view = inflater.inflate(R.layout.fragment_currcashpostabsummary, container, false);
        return view;
    }
}