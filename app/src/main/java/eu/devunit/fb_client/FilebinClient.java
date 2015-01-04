package eu.devunit.fb_client;

import android.os.Environment;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

/**
 * Created by sebastian on 1/4/15.
 */
public class FilebinClient {
    private static String Version = "0.1";
    private static String UserAgent = "fb-client-android/" + _Version;

    private URI HostURI;
    private String Apikey;

    public URI getHostURI() {
        return HostURI;
    }

    public void setHostURI(URI hostURI) {
        HostURI = hostURI;
    }

    public String getApikey() {
        return Apikey;
    }

    public void setApikey(String apikey) {
        Apikey = apikey;
    }

    public String generateApikey(String username, String password) {
        HttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, UserAgent);

        HttpPost httpPost = new HttpPost(HostURI.toString() + "/user/create_apikey");

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

        builder.addTextBody("username", username);
        builder.addTextBody("password", password);

        HttpEntity httpEntity = builder.build();

        //ProgressiveEntity myEntity = new ProgressiveEntity();

        httpPost.setEntity(httpEntity);
        HttpResponse response = null;
        String content = "";

        try {
            response = httpClient.execute(httpPost);
            content = FilebinClient.getContent(response);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content;
    }

    public String uploadText(String text) {
        return uploadFile(getFileFromText(text));
    }

    public String uploadFile(String filename) {
        return uploadFile(new String[]{filename});
    }

    public String uploadFile(String[] filenames) {
        HttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, UserAgent);

        HttpPost httpPost = new HttpPost(HostURI.toString() + "/file/do_upload");

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

        builder.addTextBody("apikey", getApikey());

        for(int i=0; i<filenames.length; i++) {

            String filename = filenames[i];
            File file = new File(filename);
            FileBody fb = new FileBody(file);
            builder.addPart("file[" + i + "]", fb);
        }

        HttpEntity httpEntity = builder.build();

        //ProgressiveEntity myEntity = new ProgressiveEntity();

        httpPost.setEntity(httpEntity);
        HttpResponse response = null;
        String content = "";

        try {
            response = httpClient.execute(httpPost);
            content = FilebinClient.getContent(response);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content;
    }

    private static String getContent(HttpResponse response) throws IOException {
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String body = "";
        String content = "";

        while ((body = rd.readLine()) != null)
        {
            content += body + "\n";
        }
        return content.trim();
    }

    private String getFileFromText(String text) {
        File root = new File(Environment.getExternalStorageDirectory(), "Notes");
        if (!root.exists()) {
            root.mkdirs();
        }

        try {
            File stdinFile = new File(root, "stdin");
            FileWriter writer = new FileWriter(stdinFile);

            writer.append(text);
            writer.flush();
            writer.close();

            return stdinFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }
}
