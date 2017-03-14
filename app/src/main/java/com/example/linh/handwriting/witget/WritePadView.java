package com.example.linh.handwriting.witget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.linh.handwriting.R;

/**
 * Created by Linh on 3/14/2017.
 */

public class WritePadView extends LinearLayout implements View.OnClickListener, OcrView.OcrViewDelegate {
    View rootView;
    TextView showMenuButton;
    TextView doButton;
    TextView spaceButton;
    TextView enterButton;
    TextView backButton;
    OcrView ocrView;
    String result = "";

    public WritePadView(Context context) {
        super(context);
        init(context);
    }

    public WritePadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WritePadView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        rootView = inflate(context, R.layout.view_write_pad, this);
        showMenuButton = (TextView) rootView.findViewById(R.id.showMenuButton);
        doButton = (TextView) rootView.findViewById(R.id.dotButton);
        spaceButton = (TextView) rootView.findViewById(R.id.spaceButton);
        enterButton = (TextView) rootView.findViewById(R.id.enterButton);
        backButton = (TextView) rootView.findViewById(R.id.backspButton);
        ocrView = (OcrView) rootView.findViewById(R.id.ink_view);

        showMenuButton.setOnClickListener(this);
        doButton.setOnClickListener(this);
        spaceButton.setOnClickListener(this);
        enterButton.setOnClickListener(this);
        backButton.setOnClickListener(this);

        ocrView.setResult(result);
        ocrView.setDelegate(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.showMenuButton:
                ocrView.clickShowMenu();
                break;
            case R.id.dotButton:
                ocrView.clickDo();
                break;
            case R.id.spaceButton:
                ocrView.clickSpace();
                if(dele != null){
                    dele.setSpace();
                }
                break;
            case R.id.enterButton:
                ocrView.clickEnter();
                break;
            case R.id.backspButton:
                ocrView.clickBack();
                if(dele != null){
                    dele.deleteCharacter();
                }
                break;
        }
    }

    @Override
    public void clickEnterOrc(String result) {
        if (dele != null) {
            dele.setResult(result);
        }
    }

    public WritePadDelegate dele;

    public void setListener(WritePadDelegate dele) {
        this.dele = dele;
    }

    public interface WritePadDelegate {
        void setResult(String result);

        void deleteCharacter();

        void setSpace();
    }

}
