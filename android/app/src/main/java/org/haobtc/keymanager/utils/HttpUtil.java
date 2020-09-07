package org.haobtc.keymanager.utils;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by SJ on 2018/4/19.
 */

public class HttpUtil {
    private Context context;

    public HttpUtil(Context context) {
        this.context = context;
    }

    public static void getDataByOk(String path, final Handler handler, final int tag) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request build = new Request.Builder().url(path).build();
        okHttpClient.newCall(build).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", "---------->: "+e.getMessage() );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                handler.obtainMessage(tag, response.body().string()).sendToTarget();

            }

        });
    }
}
