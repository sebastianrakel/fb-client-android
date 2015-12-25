package eu.devunit.fb_client.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.net.URI;
import java.net.URISyntaxException;

import eu.devunit.fb_client.MainActivity;
import eu.devunit.fb_client.R;
import eu.devunit.fb_client.filebin.FilebinClient;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BackgroundFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BackgroundFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BackgroundFragment extends Fragment {
    private FilebinClient mFilebinClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public FilebinClient getFilebinClient() {
        return mFilebinClient;
    }

    public void setFilebinClient(FilebinClient filebinClient) {
        mFilebinClient = filebinClient;
    }
}
