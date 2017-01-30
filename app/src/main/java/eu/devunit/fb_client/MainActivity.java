package eu.devunit.fb_client;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.widget.DrawerLayout;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.security.ProviderInstaller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import eu.devunit.fb_client.filebin.FilebinClient;
import eu.devunit.fb_client.fragments.BackgroundFragment;
import eu.devunit.fb_client.fragments.HistoryFragment;
import eu.devunit.fb_client.fragments.TestFragment;
import eu.devunit.fb_client.fragments.UploadFileFragment;
import eu.devunit.fb_client.fragments.UploadTextFragment;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
                   HistoryFragment.OnFragmentInteractionListener,
                   UploadFileFragment.OnFragmentInteractionListener,
                   UploadTextFragment.OnFragmentInteractionListener,
                   TestFragment.OnFragmentInteractionListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private Fragment activeFragment;
    private static final String TAG_BACKGROUND_FRAGMENT = "background_fragment";
    private BackgroundFragment mBackgroundFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mBackgroundFragment = (BackgroundFragment) getSupportFragmentManager().findFragmentByTag(TAG_BACKGROUND_FRAGMENT);

        if (mBackgroundFragment == null) {
            mBackgroundFragment = new BackgroundFragment();
            loadFilebinClient();

            getSupportFragmentManager().beginTransaction().add(mBackgroundFragment, TAG_BACKGROUND_FRAGMENT).commit();

            updateAndroidSecurityProvider(this);
        }

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        setFragment(0);

        Bundle bundle = getIntent().getExtras();

        if(getIntent().getAction() == Intent.ACTION_SEND) {
            switch (getIntent().getType()) {
                case "text/plain":
                    String uploadText = bundle.getString(Intent.EXTRA_TEXT);
                    setFragment(1);
                    ((UploadTextFragment) activeFragment).setUploadText(uploadText);
                    break;
                default:
                    Uri uri = (Uri)bundle.get(Intent.EXTRA_STREAM);
                    ((UploadFileFragment) activeFragment).AddFileToUploadList(uri);
            }
        }

        if (getIntent().getAction() == Intent.ACTION_SEND_MULTIPLE) {
            ArrayList<Uri> uris = (ArrayList<Uri>)bundle.get(Intent.EXTRA_STREAM);

            for(Uri uri : uris) {
                ((UploadFileFragment) activeFragment).AddFileToUploadList(uri);
            }
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        setFragment(position);
    }

    public FilebinClient getFbClient() {
        return mBackgroundFragment.getFilebinClient();
    }

    public void setFragment(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;

        switch (position) {
            case 0:
                mTitle = getString(R.string.title_UploadFile);
                fragment = UploadFileFragment.newInstance(position);
                break;
            case 1:
                mTitle = getString(R.string.title_UploadText);
                fragment = UploadTextFragment.newInstance(position);
                break;
            case 2:
                mTitle = getString(R.string.title_History);
                fragment = HistoryFragment.newInstance(position);
                break;
            case 3:
                mTitle = getString(R.string.title_Settings);

                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                MainActivity.this.startActivityForResult(intent, 1);

                break;
            case 4:
                mTitle = getString(R.string.title_Test);
                fragment = TestFragment.newInstance("", "");
                break;
        }

        if(fragment != null) {
            activeFragment = fragment;

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    public void onButton_UploadFileClick(View view) {
        ((UploadFileFragment) activeFragment).uploadFiles();
    }

    public void onButton_UploadTextClick(View view) {
        ((UploadTextFragment) activeFragment).uploadText();
    }

    public void onButton_AddFileClick(View view) {
        searchFiles();
    }

    @Override
    public void onFragmentInteraction(String id) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case (1) : {
                if(activeFragment instanceof UploadFileFragment) {
                    UploadFileFragment uploadFileFragment = (UploadFileFragment) activeFragment;
                    if (resultCode == Activity.RESULT_OK) {
                        if(data.getClipData() != null) {
                            for(int i = 0; i < data.getClipData().getItemCount(); i++) {
                                uploadFileFragment.AddFileToUploadList(data.getClipData().getItemAt(i).getUri());
                            }
                        } else if (data.getData() instanceof Uri) {
                            uploadFileFragment.AddFileToUploadList((Uri) data.getData());
                        }
                    }
                }
                break;
            }
        }
    }

    public void searchFiles() {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Files"), 1);
    }

    public void openPostUpload(String pasteURL) {
        Intent intent = new Intent(MainActivity.this, PostUploadActivity.class);
        intent.putExtra("paste_url", pasteURL);
        MainActivity.this.startActivity(intent);
    }

    public void loadFilebinClient() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

        String apikey = settings.getString("apikey", "");
        String hostname = settings.getString("hostname", getString(R.string.pref_default_hostname_display_name));

        FilebinClient filebinClient = new FilebinClient();

        try {
            filebinClient.setHostURI(new URI(hostname));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        filebinClient.setApikey(apikey);
        mBackgroundFragment.setFilebinClient(filebinClient);
    }

    private void updateAndroidSecurityProvider(Activity callingActivity) {
        try {
            ProviderInstaller.installIfNeeded(this);
        } catch (GooglePlayServicesRepairableException e) {
            // Thrown when Google Play Services is not installed, up-to-date, or enabled
            // Show dialog to allow users to install, update, or otherwise enable Google Play services.
            GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(), callingActivity, 0);
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e("SecurityException", "Google Play Services not available.");
        }
    }
}
