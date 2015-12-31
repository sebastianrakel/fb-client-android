package eu.devunit.fb_client.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

import eu.devunit.fb_client.MainActivity;
import eu.devunit.fb_client.R;
import eu.devunit.fb_client.filebin.FileSizeInfo;
import eu.devunit.fb_client.filebin.FilebinAsyncUploader;
import eu.devunit.fb_client.filebin.FilebinClient;
import eu.devunit.fb_client.filebin.UploadProgress;
import eu.devunit.fb_client.filebin.UploadResult;
import eu.devunit.fb_client.filebin.UriReader;

public class UploadFileFragment extends Fragment {
    private static final String ARG_POSITION = "position";

    private int mPosition;

    private OnFragmentInteractionListener mListener;

    private ProgressDialog dialog = null;

    ArrayList<Uri> uploadFiles;
    ArrayList<String> uploadFileNames;
    ArrayAdapter<String> uploadFileAdapter;

    public static UploadFileFragment newInstance(int position) {
        UploadFileFragment fragment = new UploadFileFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    public UploadFileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            InitUploadList();
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_upload_file, container, false);

        for(Uri uri : uploadFiles) {
            uploadFileNames.add(new File(UriReader.GetPath(getActivity(), uri)).getName());
        }

        uploadFileAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, uploadFileNames);

        final ListView fileListView = (ListView) view.findViewById(R.id.list_uploadFiles);
        fileListView.setAdapter(uploadFileAdapter);
        fileListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                showDeleteMessage(uploadFileNames.get(pos));
                return true;
            }
        });

        dialog = null;

        initUploader();

        return view;
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

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

    public void AddFileToUploadList(Uri uri) {
        if(uploadFiles == null) {
            ClearUploadList();
        }

        uploadFiles.add(uri);

        if(uploadFileAdapter != null) {
            uploadFileNames.add(new File(UriReader.GetPath(getActivity(), uri)).getName());
            uploadFileAdapter.notifyDataSetChanged();
        }
    }

    public void InitUploadList() {
        if(uploadFiles == null) {
            uploadFiles = new ArrayList<Uri>();
        }

        if(uploadFileNames == null) {
            uploadFileNames = new ArrayList<>();
        }
    }

    public void ClearUploadList() {
        uploadFiles = new ArrayList<Uri>();
        uploadFileNames = new ArrayList<>();

        if(uploadFileAdapter != null) {
            uploadFileAdapter.notifyDataSetChanged();
        }
    }

    private void DeleteFromUploadList(String deleteFileName) {
        for(Uri uri : uploadFiles) {
            String fileName = new File(UriReader.GetPath(getActivity(), uri)).getName();

            if(fileName.equals(deleteFileName)) {
                uploadFiles.remove(uri);
                uploadFileNames.remove(fileName);
                uploadFileAdapter.notifyDataSetChanged();
                return;
            }
        }
    }

    String lastDeleteFileName;
    private void showDeleteMessage(String fileName) {
        lastDeleteFileName = fileName;
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        DeleteFromUploadList(lastDeleteFileName);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Remove file from list?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener)
                .show();
    }

    public String[] getUploadFilePaths() {
        ArrayList<String> filePaths = new ArrayList<>();

        for(Uri uri : uploadFiles) {
            filePaths.add(UriReader.GetPath(getActivity(), uri));
        }

        return filePaths.toArray(new String[uploadFiles.size()]);
    }

    public void uploadFiles() {
        final MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.getFbClient().uploadFile(getUploadFilePaths());
    }

    private void initUploader() {
        final MainActivity mainActivity = (MainActivity) getActivity();

        if(mainActivity.getFbClient() == null) {
            return;
        }

        FilebinAsyncUploader uploader = mainActivity.getFbClient().getAsyncUploader();

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
                ClearUploadList();
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
}
