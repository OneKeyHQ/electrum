package org.haobtc.onekey.activities.personalwallet;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chaquo.python.PyObject;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.yzq.zxinglibrary.encode.CodeCreator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.activities.settings.HardwareDetailsActivity;
import org.haobtc.onekey.adapter.WalletAddressAdapter;
import org.haobtc.onekey.adapter.WalletDetailKeyAdapter;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.bean.GetCodeAddressBean;
import org.haobtc.onekey.bean.HardwareFeatures;
import org.haobtc.onekey.event.FirstEvent;
import org.haobtc.onekey.event.FixWalletNameEvent;
import org.haobtc.onekey.event.WalletAddressEvent;
import org.haobtc.onekey.event.WalletDetailBixinKeyEvent;
import org.haobtc.onekey.utils.Daemon;
import org.haobtc.onekey.utils.MyDialog;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WalletDetailsActivity extends BaseActivity {

    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.tet_Name)
    TextView tetName;
    @BindView(R.id.tet_deleteWallet)
    Button tetDeleteWallet;
    @BindView(R.id.recl_wallet_addr)
    RecyclerView reclWalletAddr;
    @BindView(R.id.recl_publicPerson)
    RecyclerView reclPublicPerson;
    @BindView(R.id.text_fix_name)
    TextView textFixName;
    @BindView(R.id.test_no_key)
    TextView testNoKey;
    @BindView(R.id.card_three_public)
    CardView cardThreePublic;
    @BindView(R.id.test_no_address)
    TextView testNoAddress;
    @BindView(R.id.ima_receive_code)
    ImageView imaReceiveCode;
    @BindView(R.id.text_addr)
    TextView textAddr;
    private String walletName;
    private Dialog dialogBtom;
    private MyDialog myDialog;
    private ArrayList<WalletDetailBixinKeyEvent> addEventsDatas;
    private ArrayList<WalletAddressEvent> walletAddressList;
    private String walletType;

    @Override
    public int getLayoutId() {
        return R.layout.activity_check_wallet_detail;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        myDialog = MyDialog.showDialog(WalletDetailsActivity.this);
        Intent intent = getIntent();
        walletName = intent.getStringExtra("wallet_name");
        walletType = intent.getStringExtra("wallet_type");
        if ("standard".equals(walletType)) {
            cardThreePublic.setVisibility(View.GONE);
        } else {
            addEventsDatas = new ArrayList<>();
            getBixinKeyList();
        }
        tetName.setText(walletName);

    }

    @Override
    public void initData() {
        walletAddressList = new ArrayList<>();
        //get wallet address
        getAllFundedAddress();
        //Generate QR code
        mGeneratecode();

    }

    private void mGeneratecode() {
        PyObject walletAddressShowUi = null;
        try {
            walletAddressShowUi = Daemon.commands.callAttr("get_wallet_address_show_UI");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        if (walletAddressShowUi != null) {
            String strCode = walletAddressShowUi.toString();
            Log.i("strCode", "mGenerate--: " + strCode);
            Gson gson = new Gson();
            GetCodeAddressBean getCodeAddressBean = gson.fromJson(strCode, GetCodeAddressBean.class);
            String qrData = getCodeAddressBean.getQrData();
            String addr = getCodeAddressBean.getAddr();
            textAddr.setText(addr);
            Bitmap  bitmap = CodeCreator.createQRCode(qrData, 250, 250, null);
            imaReceiveCode.setImageBitmap(bitmap);
        }

    }

    private void getBixinKeyList() {
        List<HardwareFeatures> deviceValue = new ArrayList<>();
        SharedPreferences devices = getSharedPreferences("devices", MODE_PRIVATE);
        Map<String, ?> devicesAll = devices.getAll();
        //key
        for (Map.Entry<String, ?> entry : devicesAll.entrySet()) {
            String mapValue = (String) entry.getValue();
            HardwareFeatures hardwareFeatures = new Gson().fromJson(mapValue, HardwareFeatures.class);
            deviceValue.add(hardwareFeatures);
        }
        try {
            PyObject deviceInfo = Daemon.commands.callAttr("get_device_info");
            String strDeviceId = deviceInfo.toString();
            if (!TextUtils.isEmpty(strDeviceId)) {
                for (HardwareFeatures entity : deviceValue) {
                    if (strDeviceId.contains(entity.getDeviceId())) {
                        WalletDetailBixinKeyEvent addBixinKeyEvent = new WalletDetailBixinKeyEvent();
                        if (!TextUtils.isEmpty(entity.getLabel())) {
                            addBixinKeyEvent.setLabel(entity.getLabel());
                        } else {
                            addBixinKeyEvent.setLabel(entity.getBleName());
                        }
                        addBixinKeyEvent.setBleName(entity.getBleName());
                        addBixinKeyEvent.setDeviceId(entity.getDeviceId());
                        addBixinKeyEvent.setMajorVersion(entity.getMajorVersion());
                        addBixinKeyEvent.setMinorVersion(entity.getMinorVersion());
                        addBixinKeyEvent.setPatchVersion(entity.getPatchVersion());
                        addEventsDatas.add(addBixinKeyEvent);
                    }
                }
                WalletDetailKeyAdapter publicPersonAdapter = new WalletDetailKeyAdapter(addEventsDatas);
                reclPublicPerson.setAdapter(publicPersonAdapter);
                publicPersonAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                        String firmwareVersion = "V" + addEventsDatas.get(position).getMajorVersion() + "." + addEventsDatas.get(position).getMinorVersion() + "." + addEventsDatas.get(position).getPatchVersion();
                        Intent intent = new Intent(WalletDetailsActivity.this, HardwareDetailsActivity.class);
                        intent.putExtra("label", addEventsDatas.get(position).getLabel());
                        intent.putExtra("bleName", addEventsDatas.get(position).getBleName());
                        intent.putExtra("firmwareVersion", firmwareVersion);
                        intent.putExtra("bleVerson", "V" + addEventsDatas.get(position).getMajorVersion() + "." + addEventsDatas.get(position).getPatchVersion());
                        intent.putExtra("device_id", addEventsDatas.get(position).getDeviceId());
                        startActivity(intent);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            reclPublicPerson.setVisibility(View.GONE);
            testNoKey.setVisibility(View.VISIBLE);
        }
    }

    private void getAllFundedAddress() {
        PyObject getAllFundedAddress = null;
        try {
            getAllFundedAddress = Daemon.commands.callAttr("get_all_funded_address");
            JSONArray jsonArray = new JSONArray(getAllFundedAddress.toString());
            if (jsonArray.length() != 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    WalletAddressEvent walletAddressEvent = new WalletAddressEvent();
                    walletAddressEvent.setAddress(jsonObject.getString("address"));
                    walletAddressEvent.setBalance(jsonObject.getString("balance"));
                    walletAddressList.add(walletAddressEvent);
                }
                WalletAddressAdapter walletAddressAdapter = new WalletAddressAdapter(walletAddressList);
                reclWalletAddr.setAdapter(walletAddressAdapter);
            } else {
                reclWalletAddr.setVisibility(View.GONE);
                testNoAddress.setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            reclWalletAddr.setVisibility(View.GONE);
            testNoAddress.setVisibility(View.VISIBLE);
        }
    }

    @SingleClick
    @OnClick({R.id.img_back, R.id.tet_deleteWallet, R.id.text_fix_name, R.id.text_copy})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.tet_deleteWallet:
                //delete wallet dialog
                showDialogs(WalletDetailsActivity.this, R.layout.delete_wallet);
                break;
            case R.id.text_fix_name:
                Intent intent = new Intent(WalletDetailsActivity.this, FixWalletNameActivity.class);
                intent.putExtra("wallet_name", walletName);
                startActivity(intent);
                break;
            case R.id.text_copy:
                //copy text
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // The text is placed on the system clipboard.
                Objects.requireNonNull(cm, "ClipboardManager not available").setPrimaryClip(ClipData.newPlainText(null, textAddr.getText()));
                Toast.makeText(WalletDetailsActivity.this, R.string.copysuccess, Toast.LENGTH_LONG).show();
                break;
            default:
        }
    }

    private void showDialogs(Context context, @LayoutRes int resource) {
        //set see view
        View view = View.inflate(context, resource, null);
        dialogBtom = new Dialog(context, R.style.dialog);
        TextView textSoftDelete = view.findViewById(R.id.text_soft_delete);
        if ("standard".equals(walletType)) {
            textSoftDelete.setVisibility(View.GONE);
        }
        //cancel dialog
        view.findViewById(R.id.tet_ConfirmDelete).setOnClickListener(v -> {
            myDialog.show();
            Log.i("wallet_name", "showDialogs: " + walletName);
            try {
                Daemon.commands.callAttr("delete_wallet", walletName);
                EventBus.getDefault().post(new FirstEvent("11"));
                mToast(getString(R.string.delete_succse));
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
            myDialog.dismiss();
            dialogBtom.cancel();
        });
        //cancel dialog
        view.findViewById(R.id.tet_cancle).setOnClickListener(v -> {
            dialogBtom.cancel();
        });

        dialogBtom.setContentView(view);
        Window window = dialogBtom.getWindow();
        //set pop_up size
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        //set locate
        window.setGravity(Gravity.BOTTOM);
        //set animal
        window.setWindowAnimations(R.style.AnimBottom);
        dialogBtom.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void fixName(FixWalletNameEvent event) {
        tetName.setText(event.getNewName());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
