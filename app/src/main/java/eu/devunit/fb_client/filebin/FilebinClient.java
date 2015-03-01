package eu.devunit.fb_client.filebin;

import android.os.Environment;

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
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by sebastian on 1/4/15.
 */
public class FilebinClient {
    private static String Version = "0.1";
    private static String UserAgent = "fb-client-android/" + Version;

    private static String ApiVersion = "v1.0.0";

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



    public String generateApikey(String username, String password, String comment) {
        HttpClient httpClient = getHttpsClient(new DefaultHttpClient());
        httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, UserAgent);

        HttpPost httpPost = new HttpPost(HostURI.toString() + "/user/create_apikey");

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

        builder.addTextBody("username", username);
        builder.addTextBody("password", password);

        if(!comment.isEmpty()) {
            builder.addTextBody("comment", comment);
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

    private String getApiUri() {
        return HostURI.toString() + "/api/" + ApiVersion;
    }

    public String uploadText(String text) {
        return uploadFile(getFileFromText(text));
    }

    public String uploadFile(String filename) {
        return uploadFile(new String[]{filename});
    }

    public String uploadFile(String[] filenames) {
        HttpClient httpClient = getHttpsClient(new DefaultHttpClient());
        httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, UserAgent);

        HttpPost httpPost;
        httpPost = new HttpPost(HostURI.toString() + "/file/do_upload");

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

        builder.addTextBody("apikey", getApikey());

        boolean is_multipaste = filenames.length > 1;

        if(is_multipaste) {
            builder.addTextBody("multipaste","true");
        }

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

        if(is_multipaste) {
            String[] fileNames = content.split(" ");
            return fileNames[fileNames.length - 1];
        } else {
            return content;
        }
    }

    public HistoryAnswer getHistory() {
        HttpClient httpClient = getHttpsClient(new DefaultHttpClient());
        httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, UserAgent);

        HttpPost httpPost;
        httpPost = new HttpPost(getApiUri() + "/file/history");

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

        builder.addTextBody("apikey", getApikey());

        HttpEntity httpEntity = builder.build();

        httpPost.setEntity(httpEntity);
        HttpResponse response = null;
        String content = "";

        try {
            response = httpClient.execute(httpPost);
            content = FilebinClient.getContent(response);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(content.length() > 0) {
            HistoryAnswer historyAnswer = new HistoryAnswer(content);
            return historyAnswer;
        }

        return null;
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
