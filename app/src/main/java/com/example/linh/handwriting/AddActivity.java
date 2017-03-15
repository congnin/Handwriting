package com.example.linh.handwriting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.linh.handwriting.R;
import com.example.linh.handwriting.utils.OnSingleClickListener;
import com.example.linh.handwriting.utils.Person;
import com.example.linh.handwriting.witget.Tabbar;
import com.example.linh.handwriting.witget.WritePadView;

/**
 * Created by Linh on 3/10/2017.
 */

public class AddActivity extends AppCompatActivity implements WritePadView.WritePadDelegate,
        View.OnFocusChangeListener, Tabbar.TabbarDelegate {
    EditText nameTextView;
    EditText jobTextView;
    WritePadView writePadView;
    ImageView doneImageView;
    private static final int NAME_CHOOSED = 1;
    private static final int JOB_CHOOSED = 2;
    private static final int YEAR_CHOOSED = 3;
    public int selected;
    Tabbar tabbar;

    public int inputMethod;

    private static final int TYPE_SOFT_KEY = 1;
    private static final int TYPE_HAND = 2;
    public int selectedInputType = TYPE_SOFT_KEY;

    private OnSingleClickListener mySingleListener = new OnSingleClickListener() {
        @Override
        public void onSingleClick(View v) {
            if (v.getId() == R.id.doneImage) {
                if (!TextUtils.isEmpty(nameTextView.getText()) && !TextUtils.isEmpty(jobTextView.getText())) {
                    sendToListUser(UserListActivity.REQUEST_ADD);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        nameTextView = (EditText) findViewById(R.id.person_name_textview);
        jobTextView = (EditText) findViewById(R.id.job_textview);
        writePadView = (WritePadView) findViewById(R.id.padview);
        doneImageView = (ImageView) findViewById(R.id.doneImage);
        tabbar = (Tabbar) findViewById(R.id.tabbar);
        tabbar.calculateLineTab();
        tabbar.setTab(Tabbar.TABBAR_LEFT);
        tabbar.setDelegate(this);
        writePadView.setListener(this);
        inputMethod = nameTextView.getInputType();

        nameTextView.setOnFocusChangeListener(this);
        jobTextView.setOnFocusChangeListener(this);
        doneImageView.setOnClickListener(mySingleListener);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    @Override
    public void setResult(String result) {
        switch (selected) {
            case NAME_CHOOSED:
                nameTextView.getText().insert(nameTextView.getSelectionStart(), result);
                break;
            case JOB_CHOOSED:
                jobTextView.getText().insert(jobTextView.getSelectionStart(), result);
                break;
        }
    }

    @Override
    public void deleteCharacter() {
        int position;
        switch (selected) {
            case NAME_CHOOSED:
                position = nameTextView.getSelectionStart();
                if (position > nameTextView.getText().length() || position <= 0) {
                    return;
                }
                nameTextView.getText().delete(position - 1, position);
                break;

            case JOB_CHOOSED:
                position = jobTextView.getSelectionStart();
                if (position > jobTextView.getText().length() || position <= 0) {
                    return;
                }
                jobTextView.getText().delete(position - 1, position);
                break;
        }

    }

    @Override
    public void setSpace() {
        switch (selected) {
            case NAME_CHOOSED:
                nameTextView.getText().insert(nameTextView.getSelectionStart(), " ");
                break;

            case JOB_CHOOSED:
                jobTextView.getText().insert(jobTextView.getSelectionStart(), " ");
                break;
        }
    }

    public void hideSoftKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) getSystemService(
                            Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(
                    view.getWindowToken(), 0);
        }
    }

    public void setNoInputMethod() {
        nameTextView.setInputType(InputType.TYPE_NULL);
        nameTextView.setRawInputType(InputType.TYPE_CLASS_TEXT);
        nameTextView.setTextIsSelectable(true);

        jobTextView.setInputType(InputType.TYPE_NULL);
        jobTextView.setRawInputType(InputType.TYPE_CLASS_TEXT);
        jobTextView.setTextIsSelectable(true);
    }

    public void setInputMethod() {
        nameTextView.setInputType(inputMethod);
        jobTextView.setInputType(inputMethod);
    }

    private void sendToListUser(int resultcode) {
        Intent intent = getIntent();
        Person person = new Person(nameTextView.getText() + "", jobTextView.getText() + "");
        intent.putExtra("person", person);
        setResult(resultcode, intent);
        finish();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v.getId() == R.id.person_name_textview) {
            selected = NAME_CHOOSED;
        } else if (v.getId() == R.id.job_textview) {
            selected = JOB_CHOOSED;
        }
    }

    @Override
    public void changeTab(int index) {
        if (index == Tabbar.TABBAR_LEFT) {
            selectedInputType = TYPE_SOFT_KEY;
            setInputMethod();
        } else if (index == Tabbar.TABBAR_RIGHT) {
            selectedInputType = TYPE_HAND;
            setNoInputMethod();
        }
    }
}
