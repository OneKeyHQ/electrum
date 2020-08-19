package org.haobtc.wallet.activities.settings;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

import com.chad.library.adapter.base.BaseQuickAdapter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.wallet.R;
import org.haobtc.wallet.activities.base.BaseActivity;
import org.haobtc.wallet.activities.service.CommunicationModeSelector;
import org.haobtc.wallet.adapter.AddWhiteListAdapter;
import org.haobtc.wallet.event.EditWhiteListEvent;
import org.haobtc.wallet.utils.IndicatorSeekBar;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditWhiteListActivity extends BaseActivity {

    public static final String TAG_ADD_WHITE_LIST = "ADD_WHITE_LIST";
    public static final String TAG_DELETE_WHITE_LIST = "DELETE_WHITE_LIST";
    @BindView(R.id.recl_white_ist)
    RecyclerView reclWhiteIst;

    @Override
    public int getLayoutId() {
        return R.layout.activity_edit_white_list;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        String whiteListData = getIntent().getStringExtra("whiteListData");
        Log.i("whiteListData", "initView:-- " + whiteListData);

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
        addWhiteListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(EditWhiteListActivity.this, CommunicationModeSelector.class);
                intent.putExtra("tag", TAG_DELETE_WHITE_LIST);
                intent.putExtra("whiteAddress", whiteList.get(position));
                startActivity(intent);
            }
        });
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
        TextView testPaste = viewSpeed.findViewById(R.id.test_paste);
        ImageView imgCancel = viewSpeed.findViewById(R.id.cancel_select_wallet);
        imgCancel.setOnClickListener(v -> {
            alertDialog.dismiss();
        });
        testPaste.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard != null) {
                ClipData data = clipboard.getPrimaryClip();
                if (data != null && data.getItemCount() > 0) {
                    editInputAddr.setText(data.getItemAt(0).getText());
                }
            }
        });
        viewSpeed.findViewById(R.id.btn_next).setOnClickListener(v -> {
            if (TextUtils.isEmpty(editInputAddr.getText().toString())) {
                mToast(getString(R.string.input_address));
                return;
            }
            Intent intent = new Intent(this, CommunicationModeSelector.class);
            intent.putExtra("tag", TAG_ADD_WHITE_LIST);
            intent.putExtra("whiteAddress", editInputAddr.getText().toString());
            startActivity(intent);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void doEvent(EditWhiteListEvent event) {
        if ("addWhiteList".equals(event.getType())) {
            Log.i("addWhiteList", "doEvent: " + event.getContent());
        }
    }
}
