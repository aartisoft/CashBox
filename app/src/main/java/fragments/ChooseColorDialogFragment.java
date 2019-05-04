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

public class ChooseColorDialogFragment extends DialogFragment implements SpectrumPalette.OnColorSelectedListener {

    private SpectrumPalette m_colorPickerView;
    private ChooseColorDialogListener m_listener;
    private static int m_ProdColor;
    private static ChooseColorDialogFragment m_frag;

    public ChooseColorDialogFragment() {
    }

    public static ChooseColorDialogFragment newInstance(String title, int color) {
        m_ProdColor = color;
        m_frag = new ChooseColorDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putInt("color", color);
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

        //set Listener
        m_colorPickerView.setOnColorSelectedListener(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onColorSelected(int color) {
        m_listener = (ChooseColorDialogListener)getActivity();
        m_listener.onFinishChooseColorDialog(color);
        m_frag.dismiss();
    }
}
