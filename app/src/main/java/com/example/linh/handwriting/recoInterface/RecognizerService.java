package com.example.linh.handwriting.recoInterface;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.example.linh.handwriting.utils.WritePadManager;

/**
 * Created by Linh on 3/8/2017.
 */

public class RecognizerService extends Service {
    private ConditionVariable mCondition;
    private boolean mRunRecognizerThread;
    private int mStrokeCnt;
    private boolean mReady;
    public String lastResult = null;

    public Handler mHandler;

    @Override
    public void onCreate()
    {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.
        mRunRecognizerThread = true;
        mStrokeCnt = 0;
        mReady = false;
        mHandler = null;
        Thread recognizeThread = new Thread(null, mTask, "RecognizerService");
        mCondition = new ConditionVariable(false);
        recognizeThread.start();
    }

    @Override
    public void onDestroy()
    {
        synchronized( mCondition )
        {
            // Stop the thread from generating further notifications
            // stopRecognizer(); -- causes problems when recognizing
            mRunRecognizerThread = false;
            mCondition.notify();
        }
    }

    private Runnable mTask = new Runnable()
    {
        public void run()
        {
            while( mRunRecognizerThread )
            {
                int strokes = 0;
                synchronized( mCondition )
                {
                    while ( ! mReady )
                    {
                        try
                        {
                            mCondition.wait();
                        }
                        catch (InterruptedException e)
                        {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            continue;
                        }
                    }
                    mReady = false;
                }
                if ( ! mRunRecognizerThread )
                    break;

                synchronized( this )
                {
                    strokes = mStrokeCnt;
                }

                if ( strokes > 0 && mHandler != null )
                {
                    // call recognizer
                    String result  = null;
                    result = WritePadManager.recoInkData(strokes, false, false, false);
                    if ( result != null )
                    {
                        // send message to view
                        Message msg = mHandler.obtainMessage();
                        Bundle b = new Bundle();
                        b.putString("result", result);
                        msg.setData(b);
                        mHandler.sendMessage(msg);

                        lastResult = result;
                        // get alternatives...
                        /*
                        int words = WritePadManager.recoResultColumnCount();
                        for ( int w = 0; w < words; w++ )
                        {
                        	int alternatives = WritePadAPI.recoResultRowCount( w );
                        	for ( int a = 0; a < alternatives; a++ )
                        	{
                        		String 	word = WritePadAPI.recoResultWord( w, a );				// word alternative
                        		int    	weight = WritePadAPI.recoResulWeight( w, a );			// probability of alternative (51...100)
                        		int 	str = WritePadAPI.recoResultStrokesNumber( w, a );	// number of strokes used for this alternative
                        		Log.d( "recoResult", String.format( "%s  %d   %d", word, weight, str ) );
                        	}
                        }
                        */
                    }
                }
            }
            // Done with our work...  stop the service!
            RecognizerService.this.stopSelf();
        }
    };

    public void dataNotify( int nStrokeCnt )
    {
        WritePadManager.recoStop();
        synchronized( this )
        {
            mStrokeCnt = nStrokeCnt;
        }
        synchronized( mCondition )
        {
            mReady = true;
            mCondition.notify();
        }
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return mBinder;
    }

    // Local Binder for Recognizer service
    public class RecognizerBinder extends Binder
    {
        public RecognizerService getService()
        {
            return RecognizerService.this;
        }

    }

    // Instantiate local binder
    private final IBinder mBinder = new RecognizerBinder();
}
