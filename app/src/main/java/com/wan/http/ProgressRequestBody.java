package com.wan.http;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.internal.Util;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

/**
 * Author wan
 * 上传进度监听的RequestBody
 * Created on 2017/6/16 0016.
 */

public class ProgressRequestBody extends RequestBody {
    private static final int SEGMENT_SIZE = 2048; //

    private final File file;
    private final ProgressListener listener;
    private final String contentType;

    public ProgressRequestBody(File file, String contentType, ProgressListener listener) {
        this.file = file;
        this.contentType = contentType;
        this.listener = listener;
    }

    @Override
    public long contentLength() {
        return file.length();
    }

    @Override
    public MediaType contentType() {
        return MediaType.parse(contentType);
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        Source source = null;
        try {
            source = Okio.source(file);
            long total = 0;
            long read;
            while ((read = source.read(sink.buffer(), SEGMENT_SIZE)) != -1) {
                total += read;
                sink.flush();
                this.listener.transferred(total / file.length());
            }
        } finally {
            Util.closeQuietly(source);
        }
    }

    //num：0-1；
    public interface ProgressListener {
        void transferred(double num);
    }
}
