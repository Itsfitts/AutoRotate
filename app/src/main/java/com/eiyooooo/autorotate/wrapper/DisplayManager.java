package com.eiyooooo.autorotate.wrapper;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.DisplayInfo;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("PrivateApi,DiscouragedPrivateApi,LogNotTimber")
public final class DisplayManager {

    // android.hardware.display.DisplayManager.EVENT_FLAG_DISPLAY_CHANGED
    public static final long EVENT_FLAG_DISPLAY_CHANGED = 1L << 2;

    public interface DisplayListener {
        /**
         * Called whenever the properties of a logical {@link Display},
         * such as size and density, have changed.
         *
         * @param displayId The id of the logical display that changed.
         */
        void onDisplayChanged(int displayId);
    }

    public static final class DisplayListenerHandle {
        private final Object displayListenerProxy;

        private DisplayListenerHandle(Object displayListenerProxy) {
            this.displayListenerProxy = displayListenerProxy;
        }
    }

    private static DisplayManager INSTANCE;

    public static DisplayManager getInstance() {
        if (INSTANCE == null) {
            try {
                Class<?> clazz = Class.forName("android.hardware.display.DisplayManagerGlobal");
                Method getInstanceMethod = clazz.getDeclaredMethod("getInstance");
                Object dmg = getInstanceMethod.invoke(null);
                INSTANCE = new DisplayManager(dmg);
            } catch (ReflectiveOperationException e) {
                throw new AssertionError(e);
            }
        }
        return INSTANCE;
    }

    private final Object manager; // instance of hidden class android.hardware.display.DisplayManagerGlobal

    private DisplayManager(Object manager) {
        this.manager = manager;
    }

    public int[] getDisplayIds() {
        try {
            return (int[]) manager.getClass().getMethod("getDisplayIds").invoke(manager);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    public DisplayInfo getDisplayInfo(int displayId) {
        try {
            return (DisplayInfo) manager.getClass().getMethod("getDisplayInfo", int.class).invoke(manager, displayId);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    public List<DisplayInfo> getAllDisplayInfo() {
        int[] displayIds = getDisplayIds();
        List<DisplayInfo> displayInfos = new ArrayList<>(displayIds.length);
        for (int displayId : displayIds) {
            DisplayInfo displayInfo = getDisplayInfo(displayId);
            if (displayInfo != null) {
                displayInfos.add(displayInfo);
            }
        }
        return displayInfos;
    }

    /**
     * @noinspection SuspiciousInvocationHandlerImplementation
     */
    public DisplayListenerHandle registerDisplayListener(DisplayListener listener, Handler handler) {
        try {
            Class<?> displayListenerClass = Class.forName("android.hardware.display.DisplayManager$DisplayListener");
            Object displayListenerProxy = Proxy.newProxyInstance(
                    ClassLoader.getSystemClassLoader(),
                    new Class[]{displayListenerClass},
                    (proxy, method, args) -> {
                        if ("onDisplayChanged".equals(method.getName())) {
                            listener.onDisplayChanged((int) args[0]);
                        }
                        if ("toString".equals(method.getName())) {
                            return "DisplayListener";
                        }
                        return null;
                    });
            try {
                manager.getClass()
                        .getMethod("registerDisplayListener", displayListenerClass, Handler.class, long.class, String.class)
                        .invoke(manager, displayListenerProxy, handler, EVENT_FLAG_DISPLAY_CHANGED, "com.android.shell");
            } catch (NoSuchMethodException e) {
                try {
                    manager.getClass()
                            .getMethod("registerDisplayListener", displayListenerClass, Handler.class, long.class)
                            .invoke(manager, displayListenerProxy, handler, EVENT_FLAG_DISPLAY_CHANGED);
                } catch (NoSuchMethodException e2) {
                    manager.getClass()
                            .getMethod("registerDisplayListener", displayListenerClass, Handler.class)
                            .invoke(manager, displayListenerProxy, handler);
                }
            }

            return new DisplayListenerHandle(displayListenerProxy);
        } catch (Exception e) {
            // Rotation and screen size won't be updated, not a fatal error
            Log.e("DisplayManager", "Could not register display listener", e);
        }

        return null;
    }

    public void unregisterDisplayListener(DisplayListenerHandle listener) {
        try {
            Class<?> displayListenerClass = Class.forName("android.hardware.display.DisplayManager$DisplayListener");
            manager.getClass().getMethod("unregisterDisplayListener", displayListenerClass).invoke(manager, listener.displayListenerProxy);
        } catch (Exception e) {
            Log.e("DisplayManager", "Could not unregister display listener", e);
        }
    }
}
