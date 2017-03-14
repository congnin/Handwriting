package com.example.linh.handwriting.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;

import com.example.linh.handwriting.MainSettings;
import com.example.linh.handwriting.R;

/**
 * Created by Linh on 3/8/2017.
 */

public class DialogHelper {
    private static CharSequence[] languageValues;
    private static CharSequence[] languages;
    private static int activeLang;
    private static final int DEFAULT_LANG_INDEX = 3;
    public static final int[] FLAG_DRAWABLE = {
            R.drawable.flag_da,
            R.drawable.flag_de,
            R.drawable.flag_en_uk,
            R.drawable.flag_en,
            R.drawable.flag_es,
            R.drawable.flag_fr,
            R.drawable.flag_it,
            R.drawable.flag_nl,
            R.drawable.flag_nb,
            R.drawable.flag_pt_br,
            R.drawable.flag_pt_pt,
            R.drawable.flag_sv,
            R.drawable.flag_fi,
            R.drawable.flag_ind,
    };


    public static AlertDialog createLanguageDialog(final Context context, final IBinder windowToken) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setIcon(R.drawable.sym_keyboard_done);
        builder.setNegativeButton(android.R.string.cancel, null);

        String language = MainSettings.getLanguage(context);
        if (languages == null) {
            languages = context.getResources().getTextArray(R.array.preference_main_settings_language_list);
        }
        if (languageValues == null) {
            languageValues = context.getResources().getTextArray(R.array.preference_main_settings_language_list_values);
        }

        activeLang = DEFAULT_LANG_INDEX;

        for (int i = 0; i < languageValues.length; i++) {
            if (languageValues[i].toString().equalsIgnoreCase(language)) {
                activeLang = i;
                break;
            }
        }
        final int defaultPadding = context.getResources().getDimensionPixelSize(R.dimen.keyboard_bottom_padding);
        final LayoutInflater layoutInflater = LayoutInflater.from(context);
        builder.setSingleChoiceItems(new ArrayAdapter<String>(context, R.layout.simple_list_item_checked) {

            @Override
            public int getCount() {
                return languages.length;
            }

            @Override
            public String getItem(final int position) {
                return languages[position].toString();
            }

            @Override
            public View getView(final int position, final View convertView, final ViewGroup parent) {
                CheckedTextView view;
                if (convertView == null) {
                    view = (CheckedTextView) layoutInflater.inflate(R.layout.simple_list_item_checked, null, false);
                } else {
                    view = (CheckedTextView) convertView;
                }

                view.setText(getItem(position));
                view.setChecked(position == activeLang);
                view.setCompoundDrawablePadding(defaultPadding);
                view.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(FLAG_DRAWABLE[position]), null, null, null);
                return view;
            }
        }, activeLang, new LanguageOnClickListener(context));
        builder.setSingleChoiceItems(languages, activeLang, new LanguageOnClickListener(context));
        builder.setTitle(R.string.preference_main_settings_language_summary);
        AlertDialog mOptionsDialog = builder.create();
        Window window = mOptionsDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.token = windowToken;
        lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG;
        window.setAttributes(lp);
        window.addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);


        return mOptionsDialog;

    }

    public static AlertDialog createAlternativesDialog(final Context context, final IBinder windowToken, final CharSequence[] alternatives) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setNegativeButton(android.R.string.cancel, null);

        builder.setItems(alternatives, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });


        builder.setTitle(R.string.alternatives);
        AlertDialog mOptionsDialog = builder.create();
        Window window = mOptionsDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.token = windowToken;
        lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG;
        window.setAttributes(lp);
        window.addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        return mOptionsDialog;

    }

    private static class LanguageOnClickListener implements DialogInterface.OnClickListener {
        private final Context context;

        private LanguageOnClickListener(final Context context) {
            this.context = context;
        }


        @Override
        public void onClick(DialogInterface di, int position) {
            di.dismiss();
            if (languageValues == null) {
                languageValues = context.getResources().getTextArray(R.array.preference_main_settings_language_list_values);
            }
            CharSequence languageCharSequence = languageValues[position];
            if (languageCharSequence != null) {
                String lang = languageCharSequence.toString();
                MainSettings.setLanguage(context, lang);
                WritePadManager.setLanguage(lang, context);
            }
        }
    }
}
