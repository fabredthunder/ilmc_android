package fr.ilovemycity.android.utils;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

public class ToastUtils {

    public static final int TOAST_DURATION = 1000; /** Duration in ms **/


    /**
     * Create and display a Toast with custom length (in sec)
     */
    public static Toast makeToast(Context context, CharSequence text, int length) {

        final Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.show();

        /**
         * Used to reduce toast duration
         */
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, (TOAST_DURATION * length));
        return toast;
    }

}
