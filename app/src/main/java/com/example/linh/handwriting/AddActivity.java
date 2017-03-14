package com.example.linh.handwriting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.example.linh.handwriting.witget.WritePadView;

/**
 * Created by Linh on 3/10/2017.
 */

public class AddActivity extends AppCompatActivity implements WritePadView.WritePadDelegate {
    EditText nameTextView;
    EditText jobTextView;
    WritePadView writePadView;
    ImageView doneImageView;
    private static final int NAME_CHOOSED = 1;
    private static final int JOB_CHOOSED = 2;
    private static final int YEAR_CHOOSED = 3;
    public int selected;

    private OnSingleClickListener mySingleListener = new OnSingleClickListener() {
        @Override
        public void onSingleClick(View v) {
            if (v.getId() == R.id.person_name_textview) {
                hideSoftKeyboard();
                selected = NAME_CHOOSED;
                Toast.makeText(AddActivity.this, "name choosed", Toast.LENGTH_SHORT).show();
            } else if (v.getId() == R.id.job_textview) {
                hideSoftKeyboard();
                selected = JOB_CHOOSED;
                Toast.makeText(AddActivity.this, "job choosed", Toast.LENGTH_SHORT).show();
            } else if (v.getId() == R.id.doneImage) {
                if(!TextUtils.isEmpty(nameTextView.getText()) && !TextUtils.isEmpty(jobTextView.getText())){
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
        writePadView.setListener(this);

        nameTextView.setOnClickListener(mySingleListener);
        jobTextView.setOnClickListener(mySingleListener);
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

    private void sendToListUser(int resultcode) {
        Intent intent = getIntent();
        Person person = new Person(nameTextView.getText() + "", jobTextView.getText() + "");
        intent.putExtra("person", person);
        setResult(resultcode, intent);
        finish();
    }
}
