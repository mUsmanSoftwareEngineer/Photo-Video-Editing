package collagestudio.photocollage.collagemaker.fragment;

import collagestudio.photocollage.collagemaker.R;
import collagestudio.photocollage.collagemaker.utils.ALog;
import collagestudio.photocollage.collagemaker.activity.BaseFragmentActivity;
import collagestudio.photocollage.collagemaker.views.AlphaColorSelectorView;
import collagestudio.photocollage.collagemaker.views.ColorChooserView;
import collagestudio.photocollage.collagemaker.listener.OnChooseAlphaColorListener;
import collagestudio.photocollage.collagemaker.listener.OnChooseRGBListener;
import collagestudio.photocollage.collagemaker.listener.OnChooseColorListener;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ColorChooserFragment extends BaseFragment implements
        OnChooseAlphaColorListener, OnChooseRGBListener {
	private static final String TAG = ColorChooserFragment.class
			.getSimpleName();
	private View mColorView;
	private TextView mColorTextView;
	private AlphaColorSelectorView mAlphaColorSelectorView;
	private ColorChooserView mColorChooserView;
	private OnChooseColorListener mListener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof OnChooseColorListener) {
			mListener = (OnChooseColorListener) activity;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ALog.d(TAG, "onCreateView");
		setTitle(getString(R.string.select_color));
		View rootView = inflater.inflate(R.layout.fragment_color_chooser,
				container, false);
		mColorView = rootView.findViewById(R.id.colorView);
		mColorTextView = (TextView) rootView.findViewById(R.id.colorTextView);
		mAlphaColorSelectorView = (AlphaColorSelectorView) rootView
				.findViewById(R.id.alphaColorView);
		mAlphaColorSelectorView.setListener(this);
		mColorChooserView = (ColorChooserView) rootView
				.findViewById(R.id.colorChooserView);
		mColorChooserView.setListener(this);

		return rootView;
	}

	@Override
	public void onPause() {
		super.onPause();
		ALog.d(TAG, "onPause");
	}

	@Override
	public void onResume() {
		super.onResume();
		ALog.d(TAG, "onPause");
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_color_chooser, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_done) {
			if (mListener != null) {
				mListener.setSelectedColor(mAlphaColorSelectorView
						.getSelectedColor());
			}
			((BaseFragmentActivity) mActivity).getFragmentManager()
					.popBackStack();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onChooseRGB(int rgb) {
		mAlphaColorSelectorView.setOriginalColor(rgb);
	}

	@Override
	public void onChooseColor(int color) {
		mColorView.setBackgroundColor(color);
		mColorTextView.setText("#" + Integer.toHexString(color));
	}

	@Override
	public void onDestroyView() {
		mColorChooserView.recyleImages();
		super.onDestroyView();
		ALog.d(TAG, "onDestroyView");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		ALog.d(TAG, "onDestroy");
	}
}
