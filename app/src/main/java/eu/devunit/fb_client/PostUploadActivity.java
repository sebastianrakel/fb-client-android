package eu.devunit.fb_client;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class PostUploadActivity extends ActionBarActivity {

    private String mURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_upload);

        mURL = getIntent().getExtras().getString("paste_url");

        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/DejaVuSansMono.ttf");

        TextView url_text_view = (TextView)findViewById(R.id.url_text);
        url_text_view.setTypeface(type);
        url_text_view.setText(mURL);
        url_text_view.setTextSize(16);
        url_text_view.setMovementMethod(LinkMovementMethod.getInstance());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onCopyToClipboardButtonClick(View view) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("paste_url", mURL);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(this, "Copied " + mURL + " to clipboard!", Toast.LENGTH_LONG).show();
    }
}
