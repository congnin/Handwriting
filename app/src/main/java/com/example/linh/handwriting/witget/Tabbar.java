package com.example.linh.handwriting.witget;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;

import com.example.linh.handwriting.R;

/**
 * Created by Linh on 3/15/2017.
 */

public class Tabbar extends RelativeLayout implements View.OnClickListener{
    View rootView;

    RelativeLayout leftLayout;

    RelativeLayout rightLayout;

    int colorEnable;
    int colorDisable;
    int widthLine;
    Boolean bInit = true;
    public static final int TABBAR_LEFT = 0;
    public static final int TABBAR_RIGHT = 1;
    int currentTab = TABBAR_LEFT;
    ObjectAnimator translateY;

    TabbarDelegate delegate;
    int numberTab = 2;

    View lineView;
    public Tabbar(Context context) {
        super(context);
        init(context);

    }

    public Tabbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Tabbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        rootView = inflate(context, R.layout.view_tabbar, this);

        leftLayout = (RelativeLayout) rootView.findViewById(R.id.left_layout);
        leftLayout.setOnClickListener(this);

        rightLayout = (RelativeLayout) rootView.findViewById(R.id.right_layout);
        rightLayout.setOnClickListener(this);
        lineView = rootView.findViewById(R.id.line_tabbar_view);


        rightLayout.setOnClickListener(this);
        colorEnable = ContextCompat.getColor(context, R.color.colorPrimary);
        colorDisable = ContextCompat.getColor(context, R.color.white);
    }

    public void calculateLineTab() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        widthLine = displaymetrics.widthPixels / numberTab;
        lineView.setBackgroundColor(colorEnable);
        RelativeLayout.LayoutParams params = (LayoutParams) lineView.getLayoutParams();
        params.width = widthLine;
    }

    public void setTab(int iTab) {
        if (getContext() != null) {
            hideSoftKeyboard((Activity) getContext());
        }
        if (delegate != null) {
            delegate.changeTab(iTab);
        }
        moveLineTab(iTab);
    }

    void moveLineTab(int tabChange) {

        if (bInit == true) {
            bInit = false;
            RelativeLayout.LayoutParams params = (LayoutParams) lineView.getLayoutParams();
            params.setMargins(tabChange * widthLine, 0, 0, 0);
        } else if (currentTab != tabChange) {
            if (translateY != null) {
                translateY.cancel();
            }
            final int distance = tabChange * widthLine;
            translateY = ObjectAnimator.ofFloat(lineView, "translationX", distance);
            translateY.start();
            currentTab = tabChange;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_layout: {
                setTab(TABBAR_LEFT);
                break;
            }
            case R.id.right_layout: {
                setTab(TABBAR_RIGHT);
                break;
            }

        }
    }

    public interface TabbarDelegate {
        void changeTab(int index);
    }

    public void setDelegate(TabbarDelegate delegate){
        this.delegate = delegate;
    }

    public void hideSoftKeyboard(Activity activity) {
        // Check if no view has focus:
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            view.clearFocus();
        }
    }
}
