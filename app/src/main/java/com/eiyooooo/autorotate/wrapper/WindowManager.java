package com.eiyooooo.autorotate.wrapper;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.IBinder;
import android.os.IInterface;
import android.util.Log;
import android.view.IDisplayWindowListener;

import androidx.annotation.RequiresApi;

import java.lang.reflect.Method;

@SuppressLint("PrivateApi,DiscouragedPrivateApi,LogNotTimber")
public final class WindowManager {

    private static final Method GET_SERVICE_METHOD;

    static {
        try {
            GET_SERVICE_METHOD = Class.forName("android.os.ServiceManager").getDeclaredMethod("getService", String.class);
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    private final IInterface manager;

    private Method freezeDisplayRotationMethod;
    private int freezeDisplayRotationMethodVersion;

    private Method isDisplayRotationFrozenMethod;
    private int isDisplayRotationFrozenMethodVersion;

    private Method thawDisplayRotationMethod;
    private int thawDisplayRotationMethodVersion;

    private static WindowManager INSTANCE;

    public static WindowManager getInstance() {
        if (INSTANCE == null) {
            IInterface manager;
            try {
                IBinder binder = (IBinder) GET_SERVICE_METHOD.invoke(null, "window");
                Method asInterfaceMethod = Class.forName("android.view.IWindowManager" + "$Stub").getMethod("asInterface", IBinder.class);
                manager = (IInterface) asInterfaceMethod.invoke(null, binder);
            } catch (Exception e) {
                throw new AssertionError(e);
            }
            INSTANCE = new WindowManager(manager);
        }
        return INSTANCE;
    }

    private WindowManager(IInterface manager) {
        this.manager = manager;
    }

    private Method getFreezeDisplayRotationMethod() throws NoSuchMethodException {
        if (freezeDisplayRotationMethod == null) {
            try {
                // Android 15 preview and 14 QPR3 Beta added a String caller parameter for debugging:
                // <https://android.googlesource.com/platform/frameworks/base/+/670fb7f5c0d23cf51ead25538bcb017e03ed73ac%5E%21/>
                freezeDisplayRotationMethod = manager.getClass().getMethod("freezeDisplayRotation", int.class, int.class, String.class);
                freezeDisplayRotationMethodVersion = 0;
            } catch (NoSuchMethodException e) {
                try {
                    // New method added by this commit:
                    // <https://android.googlesource.com/platform/frameworks/base/+/90c9005e687aa0f63f1ac391adc1e8878ab31759%5E%21/>
                    freezeDisplayRotationMethod = manager.getClass().getMethod("freezeDisplayRotation", int.class, int.class);
                    freezeDisplayRotationMethodVersion = 1;
                } catch (NoSuchMethodException e1) {
                    freezeDisplayRotationMethod = manager.getClass().getMethod("freezeRotation", int.class);
                    freezeDisplayRotationMethodVersion = 2;
                }
            }
        }
        return freezeDisplayRotationMethod;
    }

    private Method getIsDisplayRotationFrozenMethod() throws NoSuchMethodException {
        if (isDisplayRotationFrozenMethod == null) {
            try {
                // New method added by this commit:
                // <https://android.googlesource.com/platform/frameworks/base/+/90c9005e687aa0f63f1ac391adc1e8878ab31759%5E%21/>
                isDisplayRotationFrozenMethod = manager.getClass().getMethod("isDisplayRotationFrozen", int.class);
                isDisplayRotationFrozenMethodVersion = 0;
            } catch (NoSuchMethodException e) {
                isDisplayRotationFrozenMethod = manager.getClass().getMethod("isRotationFrozen");
                isDisplayRotationFrozenMethodVersion = 1;
            }
        }
        return isDisplayRotationFrozenMethod;
    }

    private Method getThawDisplayRotationMethod() throws NoSuchMethodException {
        if (thawDisplayRotationMethod == null) {
            try {
                // Android 15 preview and 14 QPR3 Beta added a String caller parameter for debugging:
                // <https://android.googlesource.com/platform/frameworks/base/+/670fb7f5c0d23cf51ead25538bcb017e03ed73ac%5E%21/>
                thawDisplayRotationMethod = manager.getClass().getMethod("thawDisplayRotation", int.class, String.class);
                thawDisplayRotationMethodVersion = 0;
            } catch (NoSuchMethodException e) {
                try {
                    // New method added by this commit:
                    // <https://android.googlesource.com/platform/frameworks/base/+/90c9005e687aa0f63f1ac391adc1e8878ab31759%5E%21/>
                    thawDisplayRotationMethod = manager.getClass().getMethod("thawDisplayRotation", int.class);
                    thawDisplayRotationMethodVersion = 1;
                } catch (NoSuchMethodException e1) {
                    thawDisplayRotationMethod = manager.getClass().getMethod("thawRotation");
                    thawDisplayRotationMethodVersion = 2;
                }
            }
        }
        return thawDisplayRotationMethod;
    }

    public void freezeRotation(int displayId, int rotation) {
        try {
            Method method = getFreezeDisplayRotationMethod();
            switch (freezeDisplayRotationMethodVersion) {
                case 0:
                    method.invoke(manager, displayId, rotation, "AutoRotate#freezeRotation");
                    break;
                case 1:
                    method.invoke(manager, displayId, rotation);
                    break;
                default:
                    if (displayId != 0) {
                        Log.e("WindowManager", "Secondary display rotation not supported on this device");
                        return;
                    }
                    method.invoke(manager, rotation);
                    break;
            }
        } catch (ReflectiveOperationException e) {
            Log.e("WindowManager", "Could not invoke method", e);
        }
    }

    public boolean isRotationFrozen(int displayId) {
        try {
            Method method = getIsDisplayRotationFrozenMethod();
            switch (isDisplayRotationFrozenMethodVersion) {
                case 0:
                    return (boolean) method.invoke(manager, displayId);
                default:
                    if (displayId != 0) {
                        Log.e("WindowManager", "Secondary display rotation not supported on this device");
                        return false;
                    }
                    return (boolean) method.invoke(manager);
            }
        } catch (ReflectiveOperationException e) {
            Log.e("WindowManager", "Could not invoke method", e);
            return false;
        }
    }

    public void thawRotation(int displayId) {
        try {
            Method method = getThawDisplayRotationMethod();
            switch (thawDisplayRotationMethodVersion) {
                case 0:
                    method.invoke(manager, displayId, "AutoRotate#thawRotation");
                    break;
                case 1:
                    method.invoke(manager, displayId);
                    break;
                default:
                    if (displayId != 0) {
                        Log.e("WindowManager", "Secondary display rotation not supported on this device");
                        return;
                    }
                    method.invoke(manager);
                    break;
            }
        } catch (ReflectiveOperationException e) {
            Log.e("WindowManager", "Could not invoke method", e);
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    public int[] registerDisplayWindowListener(IDisplayWindowListener listener) {
        try {
            return (int[]) manager.getClass().getMethod("registerDisplayWindowListener", IDisplayWindowListener.class).invoke(manager, listener);
        } catch (Exception e) {
            Log.e("WindowManager", "Could not register display window listener", e);
        }
        return null;
    }

    @RequiresApi(Build.VERSION_CODES.R)
    public void unregisterDisplayWindowListener(IDisplayWindowListener listener) {
        try {
            manager.getClass().getMethod("unregisterDisplayWindowListener", IDisplayWindowListener.class).invoke(manager, listener);
        } catch (Exception e) {
            Log.e("WindowManager", "Could not unregister display window listener", e);
        }
    }
}
