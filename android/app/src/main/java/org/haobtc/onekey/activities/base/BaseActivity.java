package org.haobtc.onekey.activities.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.common.base.Strings;

import org.haobtc.onekey.activities.service.CommunicationModeSelector;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.utils.NfcUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

public abstract class BaseActivity extends AppCompatActivity {
    private String utf8;
    private String filed1utf;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isSplash()) {
            setContentView(getLayoutId());
        }
        if (requireSecure()) {
            requestSecure();
        }
        mBinitState();
        initView();
        initData();

    }

    /**
     * 禁止录屏和截图
     * add by li
     */
    private void requestSecure() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

    /**
     * 禁止录屏和截图的钩子
     */
    public boolean requireSecure() {
        return false;
    }

    public boolean isSplash() {
        return false;
    }

    /**
     * @return the view's layout file id
     */
    public abstract int getLayoutId();

    public abstract void initView();

    public abstract void initData();

    //activity intent
    public void mIntent(Class<?> mActivity) {
        Intent intent = new Intent(this, mActivity);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if ("nfc".equals(getSharedPreferences("Preferences", Context.MODE_PRIVATE).getString(Constant.WAY, Constant.WAY_MODE_BLE)) &&
                getSharedPreferences("Preferences", Context.MODE_PRIVATE).getBoolean(Constant.NFC_SUPPORT, true)) {
            NfcUtils.nfc(this, false);
        }
        if (NfcUtils.mNfcAdapter != null && NfcUtils.mNfcAdapter.isEnabled()) {
            // enable nfc discovery for the app
            Log.i("NFC", "为本App启用NFC感应");
            NfcUtils.mNfcAdapter.enableForegroundDispatch(this, NfcUtils.mPendingIntent, NfcUtils.mIntentFilter, NfcUtils.mTechList);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (NfcUtils.mNfcAdapter != null && NfcUtils.mNfcAdapter.isEnabled()) {
            // disable nfc discovery for the app
            NfcUtils.mNfcAdapter.disableForegroundDispatch(this);
            Log.i("NFC", "禁用本App的NFC感应");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NfcUtils.mNfcAdapter = null;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String action = intent.getAction();
        // to fix the activity started with flag `FLAG_ACTIVITY_SINGLE_TOP` in stm32 1.9.5
        if (!Strings.isNullOrEmpty(action)) {
            // NDEF type
            if (Objects.equals(action, NfcAdapter.ACTION_NDEF_DISCOVERED)
                    || Objects.equals(action, NfcAdapter.ACTION_TECH_DISCOVERED)
                    || Objects.requireNonNull(action).equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
//            isNFC = true;
                CommunicationModeSelector.nfcTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            }
        }

    }

    //toast short
    public void mToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    //toast long
    public void mlToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_LONG).show();
    }

    //switch Chinese
    public void mTextChinese() {
        Locale.setDefault(Locale.CHINESE);
        Configuration config = getBaseContext().getResources().getConfiguration();
        config.locale = Locale.CHINESE;
        getBaseContext().getResources().updateConfiguration(config
                , getBaseContext().getResources().getDisplayMetrics());
    }

    //switch English
    public void mTextEnglish() {
        Locale.setDefault(Locale.ENGLISH);
        Configuration config1 = getBaseContext().getResources().getConfiguration();
        config1.locale = Locale.ENGLISH;
        getBaseContext().getResources().updateConfiguration(config1
                , getBaseContext().getResources().getDisplayMetrics());
    }

    //UTF-8 to text
    public String mUTFTtoText(String str) {
        try {
            utf8 = URLDecoder.decode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return utf8;
    }

    //text  to  utf-8
    public String mmTextToutf8(String strFiled) {
        String filed1HangyeUTF = null;
        try {
            filed1HangyeUTF = new String(strFiled.getBytes(StandardCharsets.UTF_8));
            filed1utf = URLEncoder.encode(filed1HangyeUTF, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return filed1utf;

    }

    //get now time
    public String mGetNowDatetime() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }

    //judge mobile is wrong or right
    public boolean isMobileNO(String mobiles) {
        Pattern p = compile("^((17[0-9])|(14[0-9])|(13[0-9])|(15[^4,\\D])|(16[0-9])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);

        return m.matches();
    }

    //judge pass type  must have Punctuation、Case letters、num、At least 8.
    public boolean isPassType(String mobiles) {
        Pattern p = compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@$!%*?&])[A-Za-z\\d$@$!%*?&]{8,}");
        Matcher m = p.matcher(mobiles);

        return m.matches();
    }

    //get versionCode
    public int getLocalVersion(Context ctx) {
        int localVersion = 0;
        try {
            PackageInfo packageInfo = ctx.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(ctx.getPackageName(), 0);
            localVersion = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }

    /**
     * get  versionName
     */
    public String mGetVersionName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo;
        String versionName = "";
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    //Picture compression
    public Bitmap mPicYasuo(String imgPath) {
        /**
         * Compress picture
         */
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = 2;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imgPath, options);

    }

    /**
     * Set transparent immersion bar
     */
    public void mInitState() {
        //transparent immersion bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //Transparent navigation bar will cause virtual buttons to disappear (for example, Huawei)
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }

    /**
     * Set transparent immersion bar:white text
     */
    public void mWhiteinitState() {
//        LinearLayout top_manger=findViewById(R.id.top_manger);
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

    }

    /**
     * Set transparent immersion bar : white backgrand black text
     */
    public void mBinitState() {
        //other one write
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

    }

    //Transformation  price
    public String mNumToPrice(String string) {
        // string type price to double
        Double numDouble = Double.parseDouble(string);
        // Want to convert to the currency format of the specified country 
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.CHINA);
        // Return the string type of the converted currency 
        return format.format(numDouble);
    }

}
