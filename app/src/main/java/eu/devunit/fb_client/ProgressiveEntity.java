package eu.devunit.fb_client;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ProgressiveEntity implements HttpEntity {
    private HttpEntity _httpEntity;

    public ProgressiveEntity(HttpEntity httpEntity) {
        _httpEntity = httpEntity;
    }

    @Override
    public void consumeContent() throws IOException {
        _httpEntity.consumeContent();
    }
    @Override
    public InputStream getContent() throws IOException,
            IllegalStateException {
        return _httpEntity.getContent();
    }

    @Override
    public Header getContentEncoding() {
        return _httpEntity.getContentEncoding();
    }
    @Override
    public long getContentLength() {
        return _httpEntity.getContentLength();
    }
    @Override
    public Header getContentType() {
        return _httpEntity.getContentType();
    }
    @Override
    public boolean isChunked() {
        return _httpEntity.isChunked();
    }
    @Override
    public boolean isRepeatable() {
        return _httpEntity.isRepeatable();
    }
    @Override
    public boolean isStreaming() {
        return _httpEntity.isStreaming();
    } // CONSIDER put a _real_ delegator into here!

    @Override
    public void writeTo(OutputStream outstream) throws IOException {

        class ProxyOutputStream extends FilterOutputStream {
            /**
             * @author Stephen Colebourne
             */

            public ProxyOutputStream(OutputStream proxy) {
                super(proxy);
            }
            public void write(int idx) throws IOException {
                out.write(idx);
            }
            public void write(byte[] bts) throws IOException {
                out.write(bts);
            }
            public void write(byte[] bts, int st, int end) throws IOException {
                out.write(bts, st, end);
            }
            public void flush() throws IOException {
                out.flush();
            }
            public void close() throws IOException {
                out.close();
            }
        } // CONSIDER import this class (and risk more Jar File Hell)

        class ProgressiveOutputStream extends ProxyOutputStream {
            public ProgressiveOutputStream(OutputStream proxy) {
                super(proxy);
            }
            public void write(byte[] bts, int st, int end) throws IOException {

                // FIXME  Put your progress bar stuff here!

                out.write(bts, st, end);
            }
        }

        _httpEntity.writeTo(new ProgressiveOutputStream(outstream));
    }

};
