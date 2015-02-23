package eu.devunit.fb_client;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UploadFileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UploadFileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UploadFileFragment extends Fragment {
    private static final String ARG_POSITION = "position";

    private int mPosition;

    private OnFragmentInteractionListener mListener;

    private MainActivity mainActivity;

    private ProgressDialog dialog = null;

    ArrayList<Uri> uploadFiles;
    ArrayList<String> uploadFileNames;
    ArrayAdapter<String> uploadFileAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param position Position
     * @return A new instance of fragment UploadFileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UploadFileFragment newInstance(int position, MainActivity mainActivity) {
        UploadFileFragment fragment = new UploadFileFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        fragment.setAdapter(mainActivity);
        return fragment;
    }

    public UploadFileFragment() {
        // Required empty public constructor
    }

    public void setAdapter(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_upload_file, container, false);

        ClearUploadList();
        uploadFileAdapter = new ArrayAdapter<String>(this.mainActivity, android.R.layout.simple_list_item_1, uploadFileNames);

        final ListView fileListView = (ListView) view.findViewById(R.id.list_uploadFiles);
        fileListView.setAdapter(uploadFileAdapter);
        fileListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                showDeleteMessage(uploadFileNames.get(pos));
                return true;
            }
        });

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

    public void AddFileToUploadList(Uri uri) {
        uploadFiles.add(uri);
        uploadFileNames.add(new File(UriReader.GetPath(this.mainActivity, uri)).getName());
        uploadFileAdapter.notifyDataSetChanged();
    }

    public void ClearUploadList() {
        uploadFiles = new ArrayList<Uri>();
        uploadFileNames = new ArrayList<>();
    }

    private void DeleteFromUploadList(String deleteFileName) {
        for(Uri uri : uploadFiles) {
            String fileName = new File(UriReader.GetPath(this.mainActivity, uri)).getName();

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

        AlertDialog.Builder builder = new AlertDialog.Builder(this.mainActivity);
        builder.setMessage("Remove file from list?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener)
                .show();
    }

    public String[] getUploadFilePaths() {
        ArrayList<String> filePaths = new ArrayList<>();

        for(Uri uri : uploadFiles) {
            filePaths.add(UriReader.GetPath(this.mainActivity, uri));
        }

        return filePaths.toArray(new String[uploadFiles.size()]);
    }



    public void uploadFiles() {
        dialog = ProgressDialog.show(this.mainActivity, "", "Uploading file...", true);
        new Thread(new Runnable() {
            public void run() {
                String pasteURL = "";

                try {
                    pasteURL = mainActivity.getFbClient().uploadFile(getUploadFilePaths());
                    ClearUploadList();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();

                mainActivity.openPostUpload(pasteURL);
            }
        }).start();
    }
}
