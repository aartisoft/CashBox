package fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dd.cashbox.R;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ChooseColorDialogFragment extends DialogFragment {


    public ChooseColorDialogFragment() {
    }

    public static ChooseColorDialogFragment newInstance(String title) {
        ChooseColorDialogFragment frag = new ChooseColorDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_colorpicker, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
