package eu.devunit.fb_client;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;


import java.util.ArrayList;

import eu.devunit.fb_client.dummy.DummyContent;
import eu.devunit.fb_client.filebin.HistoryAnswer;
import eu.devunit.fb_client.filebin.HistoryItem;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class HistoryFragment extends Fragment implements AbsListView.OnItemClickListener {
    private OnFragmentInteractionListener mListener;

    private static final String ARG_POSITION = "position";

    private int mPosition;
    private ArrayList<HistoryItem> items;

    private MainActivity mainActivity;

    private ProgressDialog dialog = null;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    ArrayList<String> historyItemNames;
    private ArrayAdapter<String> mAdapter;

    public static HistoryFragment newInstance(int position, MainActivity mainActivity) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        fragment.setAdapter(mainActivity);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HistoryFragment() {
    }

    public void setAdapter(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {

        }

        items = new ArrayList<HistoryItem>();
        historyItemNames = new ArrayList<>();
        mAdapter = new ArrayAdapter<String>(this.mainActivity,
                android.R.layout.simple_list_item_1, android.R.id.text1, historyItemNames);

        getHistory();
    }

    private void getHistory() {
        dialog = ProgressDialog.show(this.mainActivity, "", "Uploading file...", true);
        new Thread(new Runnable() {
            public void run() {
                String pasteURL = "";

                ArrayList<HistoryItem> items = new ArrayList<HistoryItem>();

                try {
                    HistoryAnswer historyAnswer = mainActivity.getFbClient().getHistory();

                    for(HistoryItem historyItem : historyAnswer.getItems()) {
                        historyItemNames.add(historyItem.getFilename());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });

                dialog.dismiss();
            }
        }).start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

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


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
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
        public void onFragmentInteraction(String id);
    }

}
