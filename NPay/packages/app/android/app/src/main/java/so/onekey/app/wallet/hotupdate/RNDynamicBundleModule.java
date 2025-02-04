package so.onekey.app.wallet.hotupdate;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.module.annotations.ReactModule;

import java.io.File;

@ReactModule(name = "RNDynamicBundle")
public class RNDynamicBundleModule extends ReactContextBaseJavaModule {
  public interface OnReloadRequestedListener {
    void onReloadRequested();
  }

  private final ReactApplicationContext reactContext;
  private final SharedPreferences bundlePrefs;
  private final SharedPreferences extraPrefs;
  private OnReloadRequestedListener listener;

  /* Sadly need this to avoid a circular dependency in the ReactNativeHost
   * TODO: Refactor to avoid code duplication.
   */
  public static String launchResolveBundlePath(Context ctx) {
    SharedPreferences bundlePrefs = ctx.getSharedPreferences("_bundles", Context.MODE_PRIVATE);
    SharedPreferences extraPrefs = ctx.getSharedPreferences("_extra", Context.MODE_PRIVATE);

    String activeBundle = extraPrefs.getString("activeBundle", null);
    if (activeBundle == null) {
      return null;
    }
    return bundlePrefs.getString(activeBundle, null);
  }

  public RNDynamicBundleModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
    this.bundlePrefs = reactContext.getSharedPreferences("_bundles", Context.MODE_PRIVATE);
    this.extraPrefs = reactContext.getSharedPreferences("_extra", Context.MODE_PRIVATE);
  }

  @Override
  public String getName() {
    return "RNDynamicBundle";
  }

  @ReactMethod
  public void setActiveBundle(String bundleId) {
    SharedPreferences.Editor editor = this.extraPrefs.edit();
    editor.putString("activeBundle", bundleId);
    editor.commit();
  }
  @ReactMethod
  public void registerBundle(String bundleId, String relativePath) {
    File absolutePath = new File(relativePath);
    Log.d("RNDynamicBundle", "Registering bundle: " + absolutePath.getAbsolutePath());

    SharedPreferences.Editor editor = this.bundlePrefs.edit();
    editor.putString(bundleId, absolutePath.getAbsolutePath());
    editor.commit();

    // 调试路径存储
    String storedPath = bundlePrefs.getString(bundleId, null);
    Log.d("RNDynamicBundle", "Stored path: " + storedPath);
  }

  @ReactMethod
  public void unregisterBundle(String bundleId) {
    SharedPreferences.Editor editor = this.bundlePrefs.edit();
    editor.remove(bundleId);
    editor.commit();
  }

  @ReactMethod
  public void reloadBundle() {
    if (listener != null) {
      listener.onReloadRequested();
    }
  }

  @ReactMethod
  public void getBundles(Promise promise) {
    WritableMap bundles = Arguments.createMap();
    for (String bundleId: bundlePrefs.getAll().keySet()) {
      String path = bundlePrefs.getString(bundleId, null);
      Uri url = Uri.fromFile(new File(path));

      bundles.putString(bundleId, url.toString());
    }

    promise.resolve(bundles);
  }

  @ReactMethod
  public void getActiveBundle(Promise promise) {
    promise.resolve(extraPrefs.getString("activeBundle", null));
  }


  public String resolveBundlePath() {
    String activeBundle = extraPrefs.getString("activeBundle", null);
    if (activeBundle == null) {
        return null;
    }
    String path = bundlePrefs.getString(activeBundle, null);
    Log.d("RNDynamicBundle", "Resolved bundle path: " + path);
    return path;
}

  public OnReloadRequestedListener getListener() {
    return listener;
  }

  public void setListener(OnReloadRequestedListener listener) {
    this.listener = listener;
  }

}
