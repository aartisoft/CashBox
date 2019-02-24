package fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.dd.cashbox.R;
import com.jaredrummler.android.colorpicker.ColorPickerView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ChooseColorDialogFragment extends DialogFragment implements View.OnClickListener {

    private ColorPickerView m_colorPickerView;
    private Button m_button;
    private static ChooseColorDialogFragment m_frag;

    public ChooseColorDialogFragment() {
    }

    public static ChooseColorDialogFragment newInstance(String title) {
        m_frag = new ChooseColorDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        m_frag.setArguments(args);
        return m_frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_colorpicker, container, false);

        //set variables
        m_colorPickerView = view.findViewById(R.id.cpv_color_picker_view);
        m_button = view.findViewById(R.id.color_picker_view_button);

        //set Listener
        m_button.setOnClickListener(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        int color = m_colorPickerView.getColor();
        m_frag.dismiss();
    }
}
