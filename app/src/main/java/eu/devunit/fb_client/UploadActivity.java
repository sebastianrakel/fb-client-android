package eu.devunit.fb_client;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;


public class UploadActivity extends ActionBarActivity {

    ProgressDialog dialog = null;
    FilebinClient fbClient;
    ArrayList<Uri> uploadFiles;
    ArrayList<String> fileNames;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        initFilebinClient();

        clearUploadList();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fileNames);

        final ListView fileListView = (ListView)findViewById(R.id.fileListView);
        fileListView.setAdapter(adapter);
        fileListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                showDeleteMessage(fileNames.get(pos));
                return true;
            }
        });

        Bundle bundle = getIntent().getExtras();

        if(getIntent().getAction() == Intent.ACTION_SEND) {
            Uri uri = (Uri)bundle.get(Intent.EXTRA_STREAM);
            addFileToUploadList(uri);
        }

        if (getIntent().getAction() == Intent.ACTION_SEND_MULTIPLE) {
            ArrayList<Uri> uris = (ArrayList<Uri>)bundle.get(Intent.EXTRA_STREAM);

            for(Uri uri : uris) {
                addFileToUploadList(uri);
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
                        deleteFromUploadList(lastDeleteFileName);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Remove file from list?")
               .setPositiveButton("Yes", dialogClickListener)
               .setNegativeButton("No", dialogClickListener)
               .show();
    }

    private void deleteFromUploadList(String deleteFileName) {
        for(Uri uri : uploadFiles) {
            String fileName = new File(getPath(this, uri)).getName();

            if(fileName.equals(deleteFileName)) {
                uploadFiles.remove(uri);
                fileNames.remove(fileName);
                adapter.notifyDataSetChanged();
                return;
            }
        }
    }


    private void addFileToUploadList(Uri uri) {
        uploadFiles.add(uri);
        fileNames.add(new File(getPath(this, uri)).getName());
        adapter.notifyDataSetChanged();
    }


    private void clearUploadList() {
        uploadFiles = new ArrayList<Uri>();
        fileNames = new ArrayList<>();
    }

    private String[] getUploadFilePaths() {
        ArrayList<String> filePaths = new ArrayList<>();

        for(Uri uri : uploadFiles) {
            filePaths.add(getPath(this, uri));
        }

        return filePaths.toArray(new String[uploadFiles.size()]);
    }

    private void initFilebinClient() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(UploadActivity.this);

        String apikey = settings.getString("apikey", "");
        String hostname = settings.getString("hostname", getString(R.string.pref_default_hostname_display_name));

        fbClient = new FilebinClient();

        try {
            fbClient.setHostURI(new URI(hostname));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        fbClient.setApikey(apikey);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_upload, menu);
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
            Intent intent = new Intent(UploadActivity.this, SettingsActivity.class);
            UploadActivity.this.startActivityForResult(intent, 1);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onBtnUploadTextClick(View view) {
        uploadText();
    }

    public void onBtnUploadFileClick(View view) {
        uploadFiles();
    }

    public void onBtnAddFileClick(View view) {
        searchFiles();
    }

    private void uploadText() {
        final EditText editText = (EditText)findViewById(R.id.uploadText);
        final String pasteText = editText.getText().toString();
        editText.setText(null);

        dialog = ProgressDialog.show(this, "", "Uploading text...", true);
        new Thread(new Runnable() {
            public void run() {
                String pasteURL = "";

                try {
                    pasteURL = fbClient.uploadText(pasteText);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();

                openPostUpload(pasteURL);
            }
        }).start();
    }

    private void uploadFiles() {
        dialog = ProgressDialog.show(this, "", "Uploading file...", true);
        new Thread(new Runnable() {
            public void run() {
                String pasteURL = "";

                try {
                    pasteURL = fbClient.uploadFile(getUploadFilePaths());
                    clearUploadList();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();

                openPostUpload(pasteURL);
            }
        }).start();
    }

    private void searchFiles() {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Files"), 2);
    }

    private void openPostUpload(String pasteURL) {
        Intent intent = new Intent(UploadActivity.this, PostUploadActivity.class);
        intent.putExtra("paste_url", pasteURL);
        UploadActivity.this.startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (1) : {
                    initFilebinClient();
            }
            case (2) : {
                if (resultCode == Activity.RESULT_OK) {
                    if(data.getClipData() != null) {
                        for(int i = 0; i < data.getClipData().getItemCount(); i++) {
                            addFileToUploadList(data.getClipData().getItemAt(i).getUri());
                        }
                    } else if (data.getData() instanceof Uri) {
                        addFileToUploadList((Uri)data.getData());
                    }
                }
                break;
            }
        }
    }

    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
