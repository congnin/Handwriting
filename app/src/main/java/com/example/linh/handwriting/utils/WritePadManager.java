package com.example.linh.handwriting.utils;

import android.content.Context;
import android.util.Log;

import com.example.linh.handwriting.recoInterface.WritePadAPI;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

/**
 * Created by Linh on 3/8/2017.
 */

public class WritePadManager {
    private static WritePadAPI writePadAPI;

    public enum Language {
        da(9),
        de(3),
        nl(8),
        en_uk(15),
        en(1),
        es(4),
        fr(2),
        it(5),
        nb(7),
        pt_BR(11),
        pt_PT(10),
        sv(6),
        fi(13),
        ind(14);

        private boolean init;
        private int id;

        Language() {
            init = false;
        }

        Language(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public boolean isInit() {
            return init;
        }

        public void setInit(boolean init) {
            this.init = init;
        }
    }

    static {
        writePadAPI = new WritePadAPI();
    }

    static Language language = Language.en;


    public static boolean reloadAutocorrector() {
        return writePadAPI.recoReloadAutocorrector();
    }

    public static boolean reloadUserDict() {
        return writePadAPI.recoReloadUserDict();
    }

    public static boolean reloadLearner() {
        return writePadAPI.recoReloadLearner();
    }

    public static boolean recoResetResult() {
        return writePadAPI.recoResetResult();
    }

    public static int recoResultColumnCount() {
        return writePadAPI.recoResultColumnCount();
    }

    public static int recoResultRowCount(int column) {
        return writePadAPI.recoResultRowCount(column);
    }

    public static String recoResultWord(int column, int row) {
        return writePadAPI.recoResultWord(column, row);
    }

    public static boolean isDictionaryWord(String word, int flags) {
        return writePadAPI.isDictionaryWord(word, flags);
    }


    public static String getLanguageName() {
        return writePadAPI.getLanguageName();
    }

    public static int recoGesture(int type) {
        return writePadAPI.recoGesture(type);
    }

    public static boolean recoDeleteLastStroke() {
        return writePadAPI.recoDeleteLastStroke();
    }

    public static int recoStrokeCount() {
        return writePadAPI.recoStrokeCount();
    }

    public static int recoAddPixel(int stroke, float x, float y) {
        return writePadAPI.recoAddPixel(stroke, x, y);
    }

    public static void recoResetInk() {
        writePadAPI.recoResetInk();
    }

    public static int recoNewStroke(float width, int color) {
        return writePadAPI.recoNewStroke(width, color);
    }

    public static int recoGetFlags() {
        return writePadAPI.recoGetFlags();
    }

    public static void recoSetFlags(int flags) {
        writePadAPI.recoSetFlags(flags);
    }

    public static int recoStop() {
        return writePadAPI.recoStop();
    }

    public static String recoInkData(int nDataLen, boolean bAsync, boolean bFlipY, boolean bSort ) {
        return writePadAPI.recoInkData(nDataLen, bAsync, bFlipY, bSort );
    }

    public static int recoInit(Context context) {
        int result = -1;
        try {
            String dir = context.getDir("user", 0).getAbsolutePath();
            result = writePadAPI.recoInit(dir, language, null);
            setMainDict(language, context);
        } catch (Exception ex) {
            Log.e("WritePadManager", ex.getMessage());
        }
        return result;
    }

    public static void recoFree() {
        writePadAPI.recoFree(getLanguage());
        language.setInit(false);
    }

    public static Language getLangByString(String defaultLanguage) {
        defaultLanguage = defaultLanguage == null ? Locale.getDefault().getLanguage() : defaultLanguage;
        if (defaultLanguage == null) {
            return Language.en;
        }
        Language language;
        try {
            language = Language.valueOf(defaultLanguage);
        } catch (Exception ignored) {
            language = Language.en;
        }

        return language;
    }

    public static void setLanguage(String pLanguage, Context context) {
        Language langByString = getLangByString(pLanguage);
        if (langByString.name() == null) {
            return;
        }
        Language newLanguage = getLangByString(langByString.name());

        if (newLanguage == language && language.isInit()) {
            return;
        }

        WritePadManager.language = langByString;
        initLanguage(langByString.name(), context);
    }

    public static Language getLanguage() {
        return WritePadManager.language;
    }

    static void initLanguage(String newLanguageStr, Context context) {
        Language newLanguage = getLangByString(newLanguageStr);

        try {
            int init = recoInit(context);
            setMainDict(newLanguage, context);
            if (init >= 0) {
                reloadAutocorrector();
                reloadLearner();
                reloadUserDict();

                recoResetInk();
                recoResetResult();
                newLanguage.setInit(true);

                WritePadFlagManager.initialize(context);
            }

        } catch (Exception e) {
            Log.e("WRM", e.getMessage(), e);
        }
    }

    private static boolean setMainDict(Language newLanguage, Context context) throws IOException {
        boolean result = false;
        String dictFileName = "English";
        switch (newLanguage) {

            case en:
                dictFileName = "English";
                break;
            case en_uk:
                dictFileName = "EnglishUK";
                break;
            case de:
                dictFileName = "German";
                break;
            case fr:
                dictFileName = "French";
                break;
            case es:
                dictFileName = "Spanish";
                break;
            case it:
                dictFileName = "Italian";
                break;
            case sv:
                dictFileName = "Swedish";
                break;
            case nb:
                dictFileName = "Norwegian";
                break;
            case nl:
                dictFileName = "Dutch";
                break;
            case da:
                dictFileName = "Danish";
                break;
            case pt_PT:
                dictFileName = "Portuguese";
                break;
            case pt_BR:
                dictFileName = "Brazilian";
                break;
            case ind:
                dictFileName = "Indonesian";
                break;
            case fi:
                dictFileName = "Finnish";
                break;
        }


        final String fileName = String.format("%s.dct", dictFileName);
        InputStream dictionary = context.getAssets().open(fileName);
        try {
            if (dictionary != null) {
                byte[] bytes = IOUtils.read(dictionary);
                result = writePadAPI.recoSetDict(bytes);
            }
        } catch (RuntimeException e) {
            Log.e("WRM", e.getMessage(), e);
        } catch (Throwable e) {
            Log.e("WRM", e.getMessage(), e);
        }
        return result;

    }
}
