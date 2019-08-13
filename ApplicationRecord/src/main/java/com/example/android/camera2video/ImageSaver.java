package com.example.android.camera2video;

import android.media.Image;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 作者: hzx
 * 时间: 2019/8/13
 * ========================================
 * 描述:
 */


public class ImageSaver implements Runnable {
    private final Image mImage;
    private final File mFile;

    ImageSaver(Image image, File file) {
        mImage = image;
        mFile = file;
    }

    @Override
    public void run() {
        ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(mFile);
            output.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mImage.close();
            if (null != output) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
