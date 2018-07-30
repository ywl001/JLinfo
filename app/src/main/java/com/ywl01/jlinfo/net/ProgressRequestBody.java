package com.ywl01.jlinfo.net;

import com.ywl01.jlinfo.events.ProgressEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

/**
 * Created by ywl01 on 2017/2/1.
 * 改写的requestBody，通过事件将上传进度发送出去
 */

public class ProgressRequestBody extends RequestBody {
    private File mFile;

    private static final int DEFAULT_BUFFER_SIZE = 2048;


    public ProgressRequestBody(final File file) {
        mFile = file;
    }

    @Override
    public MediaType contentType() {
        // i want to upload only images
        return MediaType.parse("image_dialog/*");
    }

    @Override
    public long contentLength() throws IOException {
        return mFile.length();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        long fileLength = mFile.length();
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        FileInputStream in = new FileInputStream(mFile);
        long uploaded = 0;

        try {
            int read;
            while ((read = in.read(buffer)) != -1) {
                uploaded += read;
                sink.write(buffer, 0, read);

                ProgressEvent event = new ProgressEvent();
                event.progress = uploaded;
                event.total = fileLength;
                event.dispatch();
            }
        } finally {
            in.close();
        }
    }
}
