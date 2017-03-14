package com.example.linh.handwriting.witget;

import android.content.Context;
import android.content.ServiceConnection;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.linh.handwriting.recoInterface.RecognizerService;
import com.example.linh.handwriting.utils.WritePadManager;
import com.example.linh.handwriting.recoInterface.WritePadAPI;

import java.util.LinkedList;

/**
 * Created by Linh on 3/10/2017.
 */

public class OcrView extends View {
    private Path mPath;
    private int mCurrStroke;
    private Paint mPaint;
    private Paint mResultPaint;
    private LinearLayout recognizedTextContainer;
    private TextView readyText;
    private LinkedList<Path> mPathList;
    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 2;
    private boolean mMoved;
    private String lastResult = null;
    String result = "";

    public RecognizerService mBoundService;

    private ServiceConnection mConnection;

    private Path gridpath = new Path();

    private final float GRID_GAP = 30;

    public OcrView(Context context) {
        this(context, null, 0);
    }

    public OcrView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OcrView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPath = new Path();
        mPathList = new LinkedList<Path>();
        mCurrStroke = -1;
        mPaint = new Paint();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFF0000FF);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(3);

        mResultPaint = new Paint();
        mResultPaint.setTextSize(32);
        mResultPaint.setAntiAlias(true);
        mResultPaint.setARGB(0xff, 0x00, 0x00, 0x00);

        String lName = WritePadManager.getLanguageName();
        WritePadManager.setLanguage(lName, context);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setColor(0xFFFF0000);
        mPaint.setStrokeWidth(1);

        for (float y = GRID_GAP; y < canvas.getHeight(); y += GRID_GAP) {
            gridpath.reset();
            gridpath.moveTo(0, y);
            gridpath.lineTo(canvas.getWidth(), y);
            canvas.drawPath(gridpath, mPaint);
        }
        mPaint.setColor(0xFF0000FF);
        mPaint.setStrokeWidth(3);


        for (Path aMPathList : mPathList) {
            canvas.drawPath(aMPathList, mPaint);
        }
        canvas.drawPath(mPath, mPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    private void touch_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
        mMoved = false;
        mCurrStroke = WritePadManager.recoNewStroke(3.0f, 0xFF0000FF);
        if (mCurrStroke >= 0) {
            int res = WritePadManager.recoAddPixel(mCurrStroke, x, y);
            if (res < 1) {
                // TODO: error
            }
        }
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mMoved = true;
            mX = x;
            mY = y;
            if (mCurrStroke >= 0) {
                int res = WritePadManager.recoAddPixel(mCurrStroke, x, y);
                if (res < 1) {
                    // TODO: error
                }
            }
        }
    }

    private void touch_up() {
        mCurrStroke = -1;
        if (!mMoved)
            mX++;
        mMoved = false;
        mPath.lineTo(mX, mY);
        mPathList.add(mPath);
        mPath = new Path();
        invalidate();

        int nStrokeCnt = WritePadManager.recoStrokeCount();
        if (nStrokeCnt == 1) {
            int gestureType = WritePadAPI.GEST_DELETE + WritePadAPI.GEST_RETURN + WritePadAPI.GEST_SPACE +
                    WritePadAPI.GEST_TAB + WritePadAPI.GEST_BACK + WritePadAPI.GEST_UNDO;
            gestureType = WritePadManager.recoGesture(gestureType);
            if (gestureType != WritePadAPI.GEST_NONE) {
                WritePadManager.recoDeleteLastStroke();
                mPathList.removeLast();
                return;
            }
        } else if (nStrokeCnt > 1) {
            int gestureType = WritePadAPI.GEST_CUT + WritePadAPI.GEST_BACK + WritePadAPI.GEST_RETURN;
            gestureType = WritePadManager.recoGesture(gestureType);
            if (gestureType != WritePadAPI.GEST_NONE && gestureType != WritePadAPI.GEST_BACK) {
                WritePadManager.recoDeleteLastStroke();
                mPathList.removeLast();
                switch (gestureType) {
                    case WritePadAPI.GEST_BACK_LONG:
                        WritePadManager.recoDeleteLastStroke();
                        mPathList.removeLast();
                        if (WritePadManager.recoStrokeCount() < 1) {

                        }
                        return;
                    case WritePadAPI.GEST_CUT:
                        cleanView(false);
                        lastResult = null;
                        return;

                    case WritePadAPI.GEST_RETURN:
                        sendText();
                        cleanView(false);
                        lastResult = null;
                        return;
                    default:
                        break;
                }
            } else {

            }
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                for (int i = 0, n = event.getHistorySize(); i < n; i++) {
                    touch_move(event.getHistoricalX(i),
                            event.getHistoricalY(i));
                }
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        return true;
    }

    private void sendText() {
        if (lastResult == null) {
            int count = recognizedTextContainer.getChildCount();
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < count; i++) {
                Button button = (Button) recognizedTextContainer.getChildAt(i);
                stringBuilder.append(button.getText());
                stringBuilder.append(" ");
            }
            readyText.setText(String.format("%s%s", readyText.getText(), stringBuilder.toString()));
        } else {
            readyText.setText(String.format("%s %s", readyText.getText(), lastResult));
        }
    }

    public void cleanView(boolean emptyAll) {
        WritePadManager.recoResetInk();
        mCurrStroke = -1;
        mPathList.clear();
        mPath.reset();
//        recognizedTextContainer.removeAllViews();
        if (emptyAll) {
            //           readyText.setText("");
        }
        invalidate();
    }

    public String getLastResult() {
        String result = "";
        result = WritePadManager.recoInkData(mCurrStroke, false, false, false);
        /*
        int words = WritePadManager.recoResultColumnCount();
        for (int w = 0; w < words; w++) {
            int alternatives = WritePadManager.recoResultRowCount(w);
            if (alternatives > 0) {

                final CharSequence[] alternativesCollection = new CharSequence[alternatives];
                for (int a = 0; a < alternatives; a++) {
                    String word = WritePadManager.recoResultWord(w, a);
                    result += word;
                }
            }
        }
        */
        return result;
    }

    public void clickShowMenu() {
        Toast.makeText(getContext(), "Show Menu", Toast.LENGTH_SHORT).show();
    }

    public void clickDo() {
        Toast.makeText(getContext(), "Do", Toast.LENGTH_SHORT).show();
    }

    public void clickSpace() {
        Toast.makeText(getContext(), "Space", Toast.LENGTH_SHORT).show();
    }

    public void clickEnter() {
        int nStrokeCnt = WritePadManager.recoStrokeCount();
        result = WritePadManager.recoInkData(nStrokeCnt, false, false, false);
        WritePadManager.recoDeleteLastStroke();
        //WritePadManager.recoResetInk();
        cleanView(true);
        //Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
        if (delegate != null && result != null) {
            delegate.clickEnterOrc(result);
        }
        //touch_up();
    }

    public void clickBack() {
        Toast.makeText(getContext(), "Back", Toast.LENGTH_SHORT).show();
    }

    public void setResult(String result) {
        this.result = result;
    }

    public interface OcrViewDelegate {
        void clickEnterOrc(String result);
    }

    public void setDelegate(OcrViewDelegate delegate) {
        this.delegate = delegate;
    }

    public OcrViewDelegate delegate;

}
