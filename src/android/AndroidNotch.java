


package com.tobspr.androidnotch;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;
import android.view.DisplayCutout;
import android.view.View;
import android.view.WindowInsets;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONException;


public class AndroidNotch extends CordovaPlugin {
    private static final String TAG = "AndroidNotch";


    /**
     * Executes the request and returns PluginResult.
     *
     * @param action          The action to execute.
     * @param args            JSONArry of arguments for the plugin.
     * @param callbackContext The callback id used when calling back into JavaScript.
     * @return True if the action was valid, false otherwise.
     */
    @Override
    public boolean execute(final String action, final CordovaArgs args, final CallbackContext callbackContext) throws JSONException {
        if ("setLayout".equals(action)) {
            this.webView.getView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            return true;
        }

        if ("getScrollbarHeight".equals(action)) {
            int height = this.getStatusBarHeight();
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, pxToDp(height)));

            Log.d(TAG, "SBHeight: " + height);
            return true;
        }

        if (Build.VERSION.SDK_INT < 28) {

            // DisplayCutout is not available on api < 28
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, 0));
            return true;
        }

        final WindowInsets insets = getInsets();
        final DisplayCutout cutout = insets.getDisplayCutout();

        if ("hasCutout".equals(action)) {
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, cutout != null));
            return true;
        }

        if ("getInsetsTop".equals(action)) {
            Log.d(TAG, "System Inset Top:" + insets.getSystemWindowInsetTop());

            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, cutout != null ? pxToDp(cutout.getSafeInsetTop()) : 0));
            return true;
        }

        if ("getInsetsRight".equals(action)) {
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, cutout != null ? pxToDp(cutout.getSafeInsetRight()) : 0));
            return true;
        }

        if ("getInsetsBottom".equals(action)) {
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, cutout != null ? pxToDp(cutout.getSafeInsetBottom()) : 0));
            return true;
        }

        if ("getInsetsLeft".equals(action)) {
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, cutout != null ? pxToDp(cutout.getSafeInsetLeft()) : 0));
            return true;
        }


        return false;
    }

    @TargetApi(23)
    private WindowInsets getInsets() {
        return this.webView.getView().getRootWindowInsets();
    }

    private int getStatusBarHeight() {
        int resourceId = cordova.getActivity().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return cordova.getActivity().getResources().getDimensionPixelSize(resourceId);
        }
        else {
            Log.d(TAG, "Unable to get 'status_bar_height' resource");
            return 0;
        }
    }

    private float pxToDp(int px) {
        float density = cordova.getActivity().getResources().getDisplayMetrics().density;
        Log.d(TAG, "Density: " + density);

        return px / density;
    }
}
