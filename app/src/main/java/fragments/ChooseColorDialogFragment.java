package fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.example.dd.cashbox.R;
import com.jaredrummler.android.colorpicker.ColorPickerView;
import com.thebluealliance.spectrum.SpectrumPalette;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ChooseColorDialogFragment extends DialogFragment implements SpectrumPalette.OnColorSelectedListener, View.OnClickListener {

    private SpectrumPalette m_colorPickerView;
    private ChooseColorDialogListener m_listener;
    private Button m_button;
    private int m_ChoosenColor;
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

    public interface ChooseColorDialogListener {
        void onFinishChooseColorDialog(int colorInt);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_colorpicker, container, false);

        //set UI
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        //set variables
        m_colorPickerView = view.findViewById(R.id.cpv_color_picker_view);
        m_button = view.findViewById(R.id.color_picker_view_button);

        //set Listener
        m_colorPickerView.setOnColorSelectedListener(this);
        m_button.setOnClickListener(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onColorSelected(int color) {
        m_ChoosenColor = color;
    }

    @Override
    public void onClick(View v) {
        m_listener = (ChooseColorDialogListener)getActivity();
        m_listener.onFinishChooseColorDialog(m_ChoosenColor);
        m_frag.dismiss();
    }
}
