package org.haobtc.wallet.activities.settings;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.haobtc.wallet.R;
import org.haobtc.wallet.activities.base.BaseActivity;
import org.haobtc.wallet.adapter.AddWhiteListAdapter;
import org.haobtc.wallet.utils.IndicatorSeekBar;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditWhiteListActivity extends BaseActivity {

    @BindView(R.id.recl_white_ist)
    RecyclerView reclWhiteIst;

    @Override
    public int getLayoutId() {
        return R.layout.activity_edit_white_list;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);

    }

    @Override
    public void initData() {
        //get white list
        getWhiteList();

    }

    private void getWhiteList() {
        ArrayList<String> whiteList = new ArrayList<>();
        whiteList.add("asjgdjhbknlkcvbmcnwuy");
        whiteList.add("ablhituewmmlaplmhsgcvevdgyf");
        whiteList.add("asjgdjhbknlkcvbmcnwuy");
        whiteList.add("ablhituewmmlaplmhsgcvevdgyf");

        AddWhiteListAdapter addWhiteListAdapter = new AddWhiteListAdapter(whiteList);
        reclWhiteIst.setAdapter(addWhiteListAdapter);

    }

    @OnClick({R.id.img_back, R.id.text_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.text_add:
                //add white list dialog
                addWhiteListDialog();
                break;
        }
    }

    private void addWhiteListDialog() {
        View viewSpeed = LayoutInflater.from(this).inflate(R.layout.add_white_list_dialog, null, false);
        AlertDialog alertDialog = new AlertDialog.Builder(this).setView(viewSpeed).create();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        EditText editInputAddr = viewSpeed.findViewById(R.id.edt_input_addr);
        ImageView imgCancel = viewSpeed.findViewById(R.id.cancel_select_wallet);
        imgCancel.setOnClickListener(v -> {
            alertDialog.dismiss();
        });
        viewSpeed.findViewById(R.id.btn_next).setOnClickListener(v -> {

        });
        alertDialog.show();
        //show center
        Window dialogWindow = alertDialog.getWindow();
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = dialogWindow.getAttributes();
        p.width = (int) (d.getWidth() * 0.95);
        p.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(p);
    }
}
