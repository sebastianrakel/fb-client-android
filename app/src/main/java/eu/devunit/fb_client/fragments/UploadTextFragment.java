package eu.devunit.fb_client.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import eu.devunit.fb_client.MainActivity;
import eu.devunit.fb_client.R;
import eu.devunit.fb_client.filebin.FileSizeInfo;
import eu.devunit.fb_client.filebin.FilebinAsyncUploader;
import eu.devunit.fb_client.filebin.UploadProgress;
import eu.devunit.fb_client.filebin.UploadResult;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UploadTextFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UploadTextFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UploadTextFragment extends Fragment {
    private static final String ARG_POSITION = "position";

    private int mPosition;

    private OnFragmentInteractionListener mListener;

    private ProgressDialog dialog = null;

    private String initUploadText = null;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param position Position
     * @return A new instance of fragment UploadTextFragment.
     */
    public static UploadTextFragment newInstance(int position) {
        UploadTextFragment fragment = new UploadTextFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    public UploadTextFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPosition = getArguments().getInt(ARG_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_upload_text, container, false);

        if(initUploadText != null) {
            final EditText editText = (EditText)view.findViewById(R.id.pasteText);
            editText.setText(initUploadText);
        }

        dialog = null;

        attachToUploader();

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setUploadText(String text) {
        initUploadText = text;
    }

    public void uploadText() {
        final EditText editText = (EditText)getView().findViewById(R.id.pasteText);
        final String pasteText = editText.getText().toString();
        editText.setText(null);

        final MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.getFbClient().uploadText(pasteText);

        attachToUploader();
    }

    private void attachToUploader() {
        final MainActivity mainActivity = (MainActivity) getActivity();

        if(mainActivity.getFbClient() == null) {
            return;
        }

        FilebinAsyncUploader uploader = mainActivity.getFbClient().getAsyncUploader();

        if(uploader == null) {
            return;
        }

        uploader.set_uploadProgressCallback(null);
        uploader.set_uploadResultCallback(null);

        FilebinAsyncUploader.UploadProgressCallback uploadProgressCallback = new FilebinAsyncUploader.UploadProgressCallback() {
            @Override
            public void progress(UploadProgress progress) {
                if(dialog != null) {
                    FileSizeInfo maxSizeInfo = FileSizeInfo.getHumanReadableSizeInfo(progress.get_totalSizeBytes(), false);
                    FileSizeInfo uploadedSizeInfo = FileSizeInfo.getHumanReadableSizeInfo(progress.get_uploadedBytes(), false);

                    dialog.setMax(maxSizeInfo.getSize());
                    dialog.setProgress(uploadedSizeInfo.getSize());
                    dialog.setProgressNumberFormat("%1d " + uploadedSizeInfo.getSizeUnit() + " / %2d " + maxSizeInfo.getSizeUnit());
                } else {
                    initProgressbar();
                }
            }
        };

        FilebinAsyncUploader.UploadResultCallback uploadResultCallback = new FilebinAsyncUploader.UploadResultCallback() {
            @Override
            public void result(UploadResult result) {
                mainActivity.openPostUpload(result.get_pasteURL());
                if(dialog != null) {
                    dialog.dismiss();
                    dialog = null;
                }
            }
        };

        uploader.set_uploadProgressCallback(uploadProgressCallback);
        uploader.set_uploadResultCallback(uploadResultCallback);
    }

    private void initProgressbar() {
        dialog = new ProgressDialog(getActivity());
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setMessage(getString(R.string.progress_upload_file));
        dialog.setIndeterminate(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
