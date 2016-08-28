package eu.devunit.fb_client.filebin;

import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import eu.devunit.fb_client.filebin.Answer.Base.IAnswer;
import eu.devunit.fb_client.filebin.Answer.Base.SuccessAnswer;
import eu.devunit.fb_client.filebin.Answer.FileMultipasteAnswer;
import eu.devunit.fb_client.filebin.Answer.FileUploadAnswer;

public class FilebinAsyncUploader extends AsyncTask<String[], UploadProgress, UploadResult> {
    public static interface UploadProgressCallback {
        public void progress(UploadProgress progress);
    }

    public static interface UploadResultCallback {
        public void result(UploadResult result);
    }

    private boolean _uploading = false;

    private FilebinClient _filebinClient;
    private UploadProgressCallback _uploadProgressCallback;
    private UploadResultCallback _uploadResultCallback;

    public FilebinAsyncUploader(FilebinClient filebinClient) {
        this._filebinClient = filebinClient;
    }

    public FilebinClient get_filebinClient() {
        return _filebinClient;
    }

    public UploadProgressCallback get_uploadProgressCallback() {
        return _uploadProgressCallback;
    }

    public void set_uploadProgressCallback(UploadProgressCallback _uploadProgressCallback) {
        this._uploadProgressCallback = _uploadProgressCallback;
    }

    public UploadResultCallback get_uploadResultCallback() {
        return _uploadResultCallback;
    }

    public void set_uploadResultCallback(UploadResultCallback _uploadResultCallback) {
        this._uploadResultCallback = _uploadResultCallback;
    }

    public boolean is_uploading() {
        return _uploading;
    }

    @Override
    protected UploadResult doInBackground(String[]... filePaths) {
        HttpClient httpClient = FilebinClient.getHttpsClient(new DefaultHttpClient());
        httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, get_filebinClient().getUserAgent());

        HttpPost httpPost;
        httpPost = new HttpPost(get_filebinClient().getApiUri() + "/file/upload");

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

        builder.addTextBody("apikey", get_filebinClient().getApikey());

        boolean is_multipaste = filePaths[0].length > 1;

        if(is_multipaste) {
            builder.addTextBody("multipaste","true");
        }

        for(int i=0; i<filePaths[0].length; i++) {

            String filename = filePaths[0][i];
            File file = new File(filename);
            FileBody fb = new FileBody(file);
            builder.addPart("file[" + i + "]", fb);
        }

        HttpEntity httpEntity = builder.build();

        ProgressHttpEntityWrapper.ProgressCallback progressCallback = new ProgressHttpEntityWrapper.ProgressCallback() {
            @Override
            public void progress(UploadProgress progress) {
                publishProgress(progress);
                //onProgressUpdate(progress);
            }
        };

        String content = "";

        try {
            _uploading = true;

            httpPost.setEntity(new ProgressHttpEntityWrapper(httpEntity, progressCallback));
            HttpResponse response;

            response = httpClient.execute(httpPost);
            content = FilebinClient.getContent(response);

            IAnswer answer =  FilebinClient.getApiAnswer(content);

            if(answer.isSuccess()) {
                FileUploadAnswer fileUploadAnswer = ((SuccessAnswer) answer).getAnswerAs(FileUploadAnswer.class);
                UploadResult uploadResult = new UploadResult();

                if(is_multipaste) {
                    HashMap<String, String> parameters = new HashMap<>();

                    parameters.put("apikey", get_filebinClient().getApikey());

                    for(int i = 0; i < fileUploadAnswer.getIds().length; i++) {
                        parameters.put(String.format("ids[%s]", i), fileUploadAnswer.getIds()[i]);
                    }
                    FileMultipasteAnswer multipasteAnswer = ((SuccessAnswer) get_filebinClient().getFilebinApiAnswer("/file/create_multipaste", parameters)).getAnswerAs(FileMultipasteAnswer.class);

                    uploadResult.set_pasteURL(multipasteAnswer.getUrl());
                } else {
                    uploadResult.set_pasteURL(fileUploadAnswer.getUrls()[0]);
                }

                return uploadResult;
            } else {
                throw new FilebinException(answer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FilebinException e) {
            e.printStackTrace();
        } finally {
            _uploading = false;
        }
        return null;
    }

    protected void onProgressUpdate(UploadProgress... progress) {
        _uploadProgressCallback.progress(progress[0]);
    }

    protected void onProgressUpdate(UploadProgress progress) {
        _uploadProgressCallback.progress(progress);
    }

    protected void onPostExecute(UploadResult result) {
        _uploadResultCallback.result(result);
    }
}
