package com.example.linh.handwriting;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Point;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.linh.handwriting.recoInterface.RecognizerService;
import com.example.linh.handwriting.utils.Person;
import com.example.linh.handwriting.utils.WritePadFlagManager;
import com.example.linh.handwriting.utils.WritePadManager;
import com.example.linh.handwriting.witget.InkView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private boolean mRecoInit;
    private InkView inkView;
    LinearLayout recognizedTextContainer;
    TextView readyText;
    Button saveButton;
    public RecognizerService mBoundService;

    private ServiceConnection mConnection;

    @Override
    protected void onResume() {
        if (inkView != null) {
            inkView.cleanView(true);
        }

        WritePadFlagManager.initialize(this);
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        String lName = WritePadManager.getLanguageName();
        WritePadManager.setLanguage(lName, this);

        // initialize ink inkView class
        inkView = (InkView) findViewById(R.id.ink_view);
        saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(this);

        recognizedTextContainer = (LinearLayout) findViewById(R.id.recognized_text_container);
        readyText = (TextView) findViewById(R.id.ready_text);

        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        defaultDisplay.getSize(size);
        int screenHeight = size.y;
        int textViewHeight = 15 * screenHeight / 100;
        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, textViewHeight);
        recognizedTextContainer.setLayoutParams(params);
        readyText.setLayoutParams(params);

        inkView.setRecognizedTextContainer(recognizedTextContainer);
        inkView.setReadyText(readyText);

        mConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder service) {
                // This is called when the connection with the service has been
                // established, giving us the service object we can use to
                // interact with the service.  Because we have bound to a explicit
                // service that we know is running in our own process, we can
                // cast its IBinder to a concrete class and directly access it.
                mBoundService = ((RecognizerService.RecognizerBinder) service).getService();
                mBoundService.mHandler = inkView.getHandler();
            }

            public void onServiceDisconnected(ComponentName className) {
                // This is called when the connection with the service has been
                // unexpectedly disconnected -- that is, its process crashed.
                // Because it is running in our same process, we should never
                // see this happen.
                mBoundService = null;
            }
        };

        bindService(new Intent(MainActivity.this,
                RecognizerService.class), mConnection, Context.BIND_AUTO_CREATE);
    }


    @Override
    protected void onDestroy() {
        unbindService(mConnection);
        super.onDestroy();
        if (mRecoInit) {
            WritePadManager.recoFree();
        }
        mRecoInit = false;
    }


    private static final int CLEAR_MENU_ID = Menu.FIRST + 1;
    private static final int SETTINGS_MENU_ID = Menu.FIRST + 2;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, CLEAR_MENU_ID, 0, "Clear").setShortcut('2', 'x');
        menu.add(0, SETTINGS_MENU_ID, 0, "Settings").setShortcut('5', 'z');
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case CLEAR_MENU_ID:
                inkView.cleanView(true);
                return true;
            case SETTINGS_MENU_ID:
                Intent intent = new Intent(this, MainSettings.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void sendToMain(int resultcode) {
        Intent intent = getIntent();
        Person person = new Person(readyText.getText() + "", "123123");
        intent.putExtra("person", person);
        setResult(resultcode, intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_button: {
                sendToMain(UserListActivity.REQUEST_ADD);
                break;
            }
        }
    }

    public interface OnInkViewListener {
        void cleanView(boolean emptyAll);

        Handler getHandler();
    }
}
