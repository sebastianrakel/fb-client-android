package eu.devunit.fb_client.filebin;

import android.os.Environment;

import org.apache.http.BuildConfig;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import eu.devunit.fb_client.filebin.Answer.Base.ErrorAnswer;
import eu.devunit.fb_client.filebin.Answer.CreateApikeyAnswer;
import eu.devunit.fb_client.filebin.Answer.HistoryAnswer;
import eu.devunit.fb_client.filebin.Answer.Base.IAnswer;
import eu.devunit.fb_client.filebin.Answer.Base.SuccessAnswer;

public class FilebinClient {
    private static String sVersion = BuildConfig.VERSION_NAME;
    private static String sUserAgent = "fb-client-android/" + sVersion;

    private static String sApiVersion = "v2.1.0";

    private URI mHostURI;
    private String mApikey;

    private FilebinAsyncUploader mUploader;

    public URI getHostURI() {
        return mHostURI;
    }

    public void setHostURI(URI hostURI) {
        mHostURI = hostURI;
    }

    public String getApikey() {
        return mApikey;
    }

    public void setApikey(String apikey) {
        mApikey = apikey;
    }

    public String getUserAgent() {
        return sUserAgent;
    }

    public String getApiUri() {
        return mHostURI.toString() + "/api/" + sApiVersion;
    }

    public void uploadText(String text) {
        uploadFile(getFileFromText(text));
    }

    public void uploadFile(String filename) {
        uploadFile(new String[]{filename});
    }

    public FilebinAsyncUploader getAsyncUploader() {
        return mUploader;
    }

    public CreateApikeyAnswer generateApikey(String username, String password, String comment) throws FilebinException {
        HashMap<String, String> parameters = new HashMap<>();

        parameters.put("username", username);
        parameters.put("password", password);
        parameters.put("access_level", "apikey");

        if(!comment.isEmpty()) {
            parameters.put("comment", comment);
        }

        IAnswer answer = getFilebinApiAnswer("/user/create_apikey", parameters);

        if(answer.isSuccess()) {
            return ((SuccessAnswer) answer).getAnswerAs(CreateApikeyAnswer.class);
        } else {
            throw new FilebinException(answer);
        }
    }

    public void uploadFile(String[] filenames) {
        createNewAsyncUploader(true);
        getAsyncUploader().execute(filenames);
    }

    public void createNewAsyncUploader(boolean force) {
        if(mUploader != null && !force) {
            return;
        }

        mUploader = new FilebinAsyncUploader(this);
    }

    public HistoryAnswer getHistory() throws FilebinException {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("apikey", getApikey() );

        IAnswer answer = getFilebinApiAnswer("/file/history", parameters);

        if(answer.isSuccess()) {
            return ((SuccessAnswer) answer).getAnswerAs(HistoryAnswer.class);
        } else {
            throw new FilebinException(answer);
        }
    }

    protected static String getContent(HttpResponse response) throws IOException {
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

    public IAnswer getFilebinApiAnswer(String endpoint, HashMap<String, String> parameters) {
        HttpClient httpClient = getHttpsClient(new DefaultHttpClient());
        httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, sUserAgent);

        HttpPost httpPost;
        httpPost = new HttpPost(getApiUri() + endpoint);

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

        for(Map.Entry<String, String> entry : parameters.entrySet()) {
            builder.addTextBody(entry.getKey(), entry.getValue());
        }

        HttpEntity httpEntity = builder.build();

        httpPost.setEntity(httpEntity);
        HttpResponse response = null;
        String content = "";

        try {
            response = httpClient.execute(httpPost);
            content = getContent(response);

            return getApiAnswer(content);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    protected static IAnswer getApiAnswer(String raw) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(raw);

            String status = jsonObject.getString("status");

            if(status.equalsIgnoreCase("success")) {
                SuccessAnswer successAnswer = new SuccessAnswer();
                successAnswer.setData(jsonObject.getJSONObject("data"));

                return successAnswer;
            } else {
                ErrorAnswer errorAnswer = new ErrorAnswer();

                errorAnswer.setData(jsonObject.getJSONObject("data"));
                errorAnswer.setMessage(jsonObject.getString("message"));
                errorAnswer.setErrorId(jsonObject.getString("error_id"));

                return errorAnswer;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static HttpClient getHttpsClient(HttpClient client) {
        try{
            X509TrustManager x509TrustManager = new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {

                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {

                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[0];
                }
            };

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{x509TrustManager}, null);
            SSLSocketFactory sslSocketFactory = new ExSSLSocketFactory(sslContext);
            sslSocketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            ClientConnectionManager clientConnectionManager = client.getConnectionManager();
            SchemeRegistry schemeRegistry = clientConnectionManager.getSchemeRegistry();
            schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            schemeRegistry.register(new Scheme("https", sslSocketFactory, 443));
            return new DefaultHttpClient(clientConnectionManager, client.getParams());
        } catch (Exception ex) {
            return null;
        }
    }
}
