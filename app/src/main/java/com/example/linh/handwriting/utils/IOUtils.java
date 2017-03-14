package com.example.linh.handwriting.utils;

import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Created by Linh on 3/8/2017.
 */

public class IOUtils {
    public static String toString(InputStream input) throws IOException {
        StringWriter sw = new StringWriter();
        copy(input, sw);
        return sw.toString();
    }

    public static void copy(InputStream input, Writer output)
            throws IOException {
        InputStreamReader in = new InputStreamReader(input);
        copy(in, output);
    }

    public static int copy(Reader input, Writer output)
            throws IOException {
        char[] buffer = new char[10 * 1024];
        int count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    public static void copy(InputStream stream, String path) throws IOException {

        final File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
        final File parentFile = file.getParentFile();

        if (!parentFile.exists()) {
            //noinspection ResultOfMethodCallIgnored
            parentFile.mkdir();
        }

        if (file.exists()) {
            return;
        }
        OutputStream myOutput = new FileOutputStream(path, true);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = stream.read(buffer)) >= 0) {
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        stream.close();

    }

    public static byte[] read(InputStream stream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try {
            byte[] buffer = new byte[1024];
            int length;

            while ((length = stream.read(buffer)) >= 0) {
                byteArrayOutputStream.write(buffer, 0, length);
            }
            stream.close();
            return byteArrayOutputStream.toByteArray();
        } finally {
            byteArrayOutputStream.flush();
            byteArrayOutputStream.close();


        }
    }


    private static boolean mExternalStorageAvailable;
    private static boolean mExternalStorageWritable;

    public static boolean isSDCARDMounted() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mExternalStorageAvailable = mExternalStorageWritable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            mExternalStorageAvailable = true;
            mExternalStorageWritable = false;
        } else {
            mExternalStorageAvailable = mExternalStorageWritable = false;
        }
        return mExternalStorageAvailable && mExternalStorageWritable;
    }


    public static void createDirectory(String folderName) {
        File folder = new File(folderName);
        if (!folder.exists() || !folder.isDirectory()) {
            folder.mkdir();
        }
    }
}
