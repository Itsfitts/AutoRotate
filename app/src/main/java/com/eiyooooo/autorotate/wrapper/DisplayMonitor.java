package com.eiyooooo.autorotate.wrapper;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.IDisplayWindowListener;

public class DisplayMonitor {

    private static final boolean USE_DEFAULT_METHOD = Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE;

    private DisplayManager.DisplayListenerHandle displayListenerHandle;
    private HandlerThread handlerThread;

    private IDisplayWindowListener displayWindowListener;

    public void start(DisplayManager.DisplayListener listener) {
        if (USE_DEFAULT_METHOD) {
            handlerThread = new HandlerThread("DisplayListener");
            handlerThread.start();
            Handler handler = new Handler(handlerThread.getLooper());
            displayListenerHandle = DisplayManager.getInstance().registerDisplayListener(listener, handler);
        } else {
            displayWindowListener = new DisplayWindowListener() {
                @Override
                public void onDisplayConfigurationChanged(int eventDisplayId, Configuration newConfig) {
                    listener.onDisplayChanged(eventDisplayId);
                }
            };
            WindowManager.getInstance().registerDisplayWindowListener(displayWindowListener);
        }
    }

    public void stopAndRelease() {
        if (USE_DEFAULT_METHOD) {
            if (displayListenerHandle != null) {
                DisplayManager.getInstance().unregisterDisplayListener(displayListenerHandle);
                displayListenerHandle = null;
            }
            if (handlerThread != null) {
                handlerThread.quitSafely();
            }
        } else if (displayWindowListener != null) {
            WindowManager.getInstance().unregisterDisplayWindowListener(displayWindowListener);
        }
    }
}
