package android.view;

public class DisplayInfo {

    /**
     * Logical display identifier.
     */
    public int displayId;

    /**
     * Display address, or null if none.
     * Interpretation varies by display type.
     */
    public DisplayAddress address;

    /**
     * The rotation of the display relative to its natural orientation.
     * May be one of {@link android.view.Surface#ROTATION_0},
     * {@link android.view.Surface#ROTATION_90}, {@link android.view.Surface#ROTATION_180},
     * {@link android.view.Surface#ROTATION_270}.
     * <p>
     * The value of this field is indeterminate if the logical display is presented on
     * more than one physical display.
     * </p>
     */
    public int rotation;
}
