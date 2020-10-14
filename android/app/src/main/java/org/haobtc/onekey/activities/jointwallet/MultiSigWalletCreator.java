package org.haobtc.onekey.activities.jointwallet;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chaquo.python.PyObject;
import com.google.gson.Gson;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;
import com.yzq.zxinglibrary.encode.CodeCreator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.MainActivity;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.activities.personalwallet.ImportHistoryWalletActivity;
import org.haobtc.onekey.activities.service.CommunicationModeSelector;
import org.haobtc.onekey.adapter.AddBixinKeyAdapter;
import org.haobtc.onekey.adapter.PublicPersonAdapter;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.bean.GetCodeAddressBean;
import org.haobtc.onekey.event.AddBixinKeyEvent;
import org.haobtc.onekey.event.FirstEvent;
import org.haobtc.onekey.event.MutiSigWalletEvent;
import org.haobtc.onekey.utils.Daemon;
import org.haobtc.onekey.utils.IndicatorSeekBar;
import org.haobtc.onekey.utils.MyDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MultiSigWalletCreator extends BaseActivity implements TextWatcher {

    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.tet_Trone)
    TextView tetTrone;
    @BindView(R.id.tet_Trtwo)
    TextView tetTrtwo;
    @BindView(R.id.tet_Trthree)
    TextView tetTrthree;
    @BindView(R.id.edit_Walletname)
    EditText editWalletname;
    @BindView(R.id.seek_bar_fee)
    IndicatorSeekBar seekBarFee;
    @BindView(R.id.seek_bar_num)
    IndicatorSeekBar seekBarNum;
    @BindView(R.id.button)
    Button button;
    @BindView(R.id.tv_indicator)
    TextView tvIndicator;
    @BindView(R.id.tv_indicatorTwo)
    TextView tvIndicatorTwo;
    @BindView(R.id.bn_add_key)
    LinearLayout bnAddKey;
    @BindView(R.id.img_Progree1)
    ImageView imgProgree1;
    @BindView(R.id.img_Progree2)
    ImageView imgProgree2;
    @BindView(R.id.img_Progree3)
    ImageView imgProgree3;
    @BindView(R.id.card_viewOne)
    CardView cardViewOne;
    @BindView(R.id.card_viewThree)
    CardView cardViewThree;
    @BindView(R.id.recl_BinxinKey)
    RecyclerView reclBinxinKey;
    @BindView(R.id.rel_Finish)
    RelativeLayout relFinish;
    @BindView(R.id.rel_TwoNext1)
    RelativeLayout relTwoNext1;
    @BindView(R.id.bn_complete_add_cosigner)
    Button bnCompleteAddCosigner;
    @BindView(R.id.card_three_public)
    CardView cardThreePublic;
    @BindView(R.id.tet_who_wallet)
    TextView tetWhoWallet;
    @BindView(R.id.tet_many_key)
    TextView tetManyKey;
    @BindView(R.id.img_Orcode)
    ImageView imgOrcode;
    @BindView(R.id.tet_Preservation)
    TextView tetPreservation;
    @BindView(R.id.recl_publicPerson)
    RecyclerView reclPublicPerson;
    @BindView(R.id.btn_Finish)
    Button btnFinish;
    @BindView(R.id.testseekNum)
    TextView testseekNum;
    public String pin = "";
    public static final String TAG = MultiSigWalletCreator.class.getSimpleName();
    @BindView(R.id.test_input_wallet)
    TextView testInputWallet;
    @BindView(R.id.test_public_key)
    TextView testPublicKey;
    @BindView(R.id.test_sign_num)
    TextView testSignNum;
    private RxPermissions rxPermissions;
    private static final int REQUEST_CODE = 0;
    private EditText editScan;
    private TextView textView;
    private ArrayList<AddBixinKeyEvent> addEventsDatas;
    private String strInditor2;
    private String strInditor1;
    private MyDialog myDialog;
    private PyObject walletAddressShowUi;
    private SharedPreferences.Editor edit;
    private int walletNameNum;
    private Bitmap bitmap;
    private int strUp1;
    private int strUp2;
    private SharedPreferences preferences;
    private int defaultKeyNameNum;
    private int page = 0;
    private int cosignerNum;
    private String indicatorTextUp;
    private String indicatorTextDown;

    @Override
    public int getLayoutId() {
        return R.layout.activity_many_wallet_together;
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    public void initView() {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        preferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        int defaultName = preferences.getInt("defaultName", 0);
        edit = preferences.edit();
        rxPermissions = new RxPermissions(this);
        myDialog = MyDialog.showDialog(MultiSigWalletCreator.this);
        editWalletname.addTextChangedListener(this);
        walletNameNum = defaultName + 1;
        editWalletname.setText(String.format("钱包%s", String.valueOf(walletNameNum)));
        reclPublicPerson.setNestedScrollingEnabled(false);
    }

    @Override
    public void initData() {
        addEventsDatas = new ArrayList<>();
        seekbarLatoutup();
        seekbarLatoutdown();
    }

    private void seekbarLatoutup() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tvIndicator.getLayoutParams();
        seekBarFee.setProgress(1);
        seekBarFee.setOnSeekBarChangeListener(new IndicatorSeekBar.OnIndicatorSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, float indicatorOffset) {
                indicatorTextUp = String.valueOf(progress + 2);
                seekBarNum.setMax(progress + 1);
                testseekNum.setText(indicatorTextUp);
                tvIndicator.setText(indicatorTextUp);
                params.leftMargin = (int) indicatorOffset;
                testPublicKey.setText(String.format("%s%s%s", getString(R.string.public1), indicatorTextUp, getString(R.string.key1)));
                if ("English".equals(preferences.getString("language", ""))) {
                    testSignNum.setText(String.format("%s%s%s%s%s", getString(R.string.tet_tips1), indicatorTextDown, getString(R.string.tet_tips2), indicatorTextUp, getString(R.string.tet_tips3)));
                } else {
                    testSignNum.setText(String.format("%s%s%s%s%s", getString(R.string.tet_tips1), indicatorTextUp, getString(R.string.tet_tips2), indicatorTextDown, getString(R.string.tet_tips3)));
                }
                tvIndicator.setLayoutParams(params);
                if (testseekNum.getText().toString().equals(tvIndicatorTwo.getText().toString())) {
                    testseekNum.setVisibility(View.GONE);
                } else {
                    testseekNum.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                tvIndicator.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                tvIndicator.setVisibility(View.VISIBLE);

            }
        });

    }

    private void seekbarLatoutdown() {
        RelativeLayout.LayoutParams paramsTwo = (RelativeLayout.LayoutParams) tvIndicatorTwo.getLayoutParams();
        seekBarNum.setProgress(1);
        seekBarNum.setOnSeekBarChangeListener(new IndicatorSeekBar.OnIndicatorSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, float indicatorOffset) {
                indicatorTextDown = String.valueOf(progress + 1);
                tvIndicatorTwo.setText(indicatorTextDown);
                paramsTwo.leftMargin = (int) indicatorOffset;
                tvIndicatorTwo.setLayoutParams(paramsTwo);
                if ("English".equals(preferences.getString("language", ""))) {
                    testSignNum.setText(String.format("%s%s%s%s%s", getString(R.string.tet_tips1), indicatorTextDown, getString(R.string.tet_tips2), indicatorTextUp, getString(R.string.tet_tips3)));
                } else {
                    testSignNum.setText(String.format("%s%s%s%s%s", getString(R.string.tet_tips1), indicatorTextUp, getString(R.string.tet_tips2), indicatorTextDown, getString(R.string.tet_tips3)));
                }
                if (testseekNum.getText().toString().equals(tvIndicatorTwo.getText().toString())) {
                    testseekNum.setVisibility(View.GONE);
                } else {
                    testseekNum.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                tvIndicatorTwo.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                tvIndicatorTwo.setVisibility(View.VISIBLE);

            }
        });
    }

    @SingleClick
    @OnClick({R.id.img_back, R.id.button, R.id.bn_add_key, R.id.btn_Finish, R.id.bn_complete_add_cosigner, R.id.tet_Preservation, R.id.test_input_wallet})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                if (page == 0) {
                    finish();
                } else if (page == 1) {
                    testInputWallet.setVisibility(View.VISIBLE);
                    cardViewOne.setVisibility(View.VISIBLE);
                    button.setVisibility(View.VISIBLE);
                    imgProgree1.setVisibility(View.VISIBLE);
                    imgProgree2.setVisibility(View.GONE);
                    imgProgree3.setVisibility(View.GONE);
                    reclBinxinKey.setVisibility(View.GONE);
                    bnAddKey.setVisibility(View.GONE);
                    relTwoNext1.setVisibility(View.GONE);
                    tetTrtwo.setTextColor(getColor(R.color.light_text));
                    page = 0;
                } else if (page == 2) {
                    cardViewOne.setVisibility(View.GONE);
                    button.setVisibility(View.GONE);
                    imgProgree1.setVisibility(View.GONE);
                    imgProgree2.setVisibility(View.VISIBLE);
                    imgProgree3.setVisibility(View.GONE);
                    reclBinxinKey.setVisibility(View.VISIBLE);
                    bnAddKey.setVisibility(View.VISIBLE);
                    relTwoNext1.setVisibility(View.VISIBLE);
                    relFinish.setVisibility(View.GONE);
                    tetTrthree.setTextColor(getColor(R.color.light_text));
                    cardViewThree.setVisibility(View.GONE);
                    relFinish.setVisibility(View.GONE);
                    cardThreePublic.setVisibility(View.GONE);
                    page = 1;
                }

                break;
            case R.id.button:
                mCreatWalletNext();
                break;
            case R.id.bn_add_key:
                CommunicationModeSelector.runnables.clear();
                CommunicationModeSelector.runnables.add(runnable);
                CommunicationModeSelector.runnables.add(null);
                Intent intent = new Intent(this, CommunicationModeSelector.class);
                intent.putExtra("tag", TAG);
                startActivity(intent);
                break;
            case R.id.btn_Finish:
                Intent intent1 = new Intent(this, MainActivity.class);
                startActivity(intent1);
                finishAffinity();
                break;
            case R.id.bn_complete_add_cosigner:
                myDialog.show();
                handler.sendEmptyMessage(1);
                break;
            case R.id.tet_Preservation:
                rxPermissions
                        .request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe(granted -> {
                            if (granted) { // Always true pre-M
                                boolean toGallery = saveBitmap(bitmap);
                                if (toGallery) {
                                    mToast(getString(R.string.preservationbitmappic));
                                } else {
                                    Toast.makeText(this, R.string.preservationfail, Toast.LENGTH_SHORT).show();
                                }

                            } else { // Oups permission denied
                                Toast.makeText(this, R.string.reservatpion_photo, Toast.LENGTH_SHORT).show();
                            }
                        }).dispose();
                break;
            case R.id.test_input_wallet:
                boolean set_syn_server = preferences.getBoolean("set_syn_server", false);
                if (set_syn_server) {
                    mIntent(ImportHistoryWalletActivity.class);
                } else {
                    mToast(getString(R.string.open_server_input));
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }

    public boolean saveBitmap(Bitmap bitmap) {
        try {
            File filePic = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + System.currentTimeMillis() + ".jpg");
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            boolean success = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            return success;

        } catch (IOException ignored) {
            return false;
        }
    }

    //get code
    private void mGeneratecode() {
        try {
            walletAddressShowUi = Daemon.commands.callAttr("get_wallet_address_show_UI");

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (walletAddressShowUi != null) {
            String strCode = walletAddressShowUi.toString();
            Gson gson = new Gson();
            GetCodeAddressBean getCodeAddressBean = gson.fromJson(strCode, GetCodeAddressBean.class);
            String qrData = getCodeAddressBean.getQrData();
            bitmap = CodeCreator.createQRCode(qrData, 248, 248, null);
            imgOrcode.setImageBitmap(bitmap);

        }
    }

    private void mCreatWalletNext() {
        page = 1;
        strInditor1 = tvIndicator.getText().toString();
        strInditor2 = tvIndicatorTwo.getText().toString();
        String strWalletname = editWalletname.getText().toString();
        strUp1 = Integer.parseInt(strInditor1);
        strUp2 = Integer.parseInt(strInditor2);
        if (TextUtils.isEmpty(strWalletname)) {
            mToast(getString(R.string.set_wallet));
            return;
        }
        if (strUp1 == 0) {
            mToast(getString(R.string.set_public_num));
            return;
        }
        if (strUp2 == 0) {
            mToast(getString(R.string.set_sign_num));
            return;
        }
        if (strUp1 < 2) {
            mToast(getString(R.string.public_person_2));
            return;
        }
        if (strUp2 > strUp1) {
            mToast(getString(R.string.signnum_dongt_public));
            return;
        }
        testInputWallet.setVisibility(View.GONE);
        cardViewOne.setVisibility(View.GONE);
        button.setVisibility(View.GONE);
        imgProgree1.setVisibility(View.GONE);
        imgProgree2.setVisibility(View.VISIBLE);
        imgProgree3.setVisibility(View.GONE);
        reclBinxinKey.setNestedScrollingEnabled(false);
        reclBinxinKey.setVisibility(View.VISIBLE);
        bnAddKey.setVisibility(View.VISIBLE);
        relTwoNext1.setVisibility(View.VISIBLE);
        tetTrtwo.setTextColor(getColor(R.color.button_bk_disableok));
        bnCompleteAddCosigner.setText(String.format("%s (%s-%s)", getString(R.string.next), addEventsDatas.size() + "", strInditor1));
        if (addEventsDatas.size() == strUp1) {
            bnCompleteAddCosigner.setEnabled(true);
            bnCompleteAddCosigner.setBackground(getDrawable(R.drawable.little_radio_blue));
            bnAddKey.setVisibility(View.GONE);
        } else {
            bnCompleteAddCosigner.setEnabled(false);
            bnCompleteAddCosigner.setBackground(getDrawable(R.drawable.little_radio_qian));
        }
    }


    private void showInputDialogs(Context context, @LayoutRes int resource) {
        //set see view
        View view = View.inflate(context, resource, null);
        Dialog dialogBtoms = new Dialog(context, R.style.dialog);
        EditText editBixinName = view.findViewById(R.id.edit_keyName);
        TextView tetNum = view.findViewById(R.id.txt_textNum);
        editScan = view.findViewById(R.id.edit_public_key_cosigner_popup);
        int defaultKeyNum = preferences.getInt("defaultKeyNum", 0);
        defaultKeyNameNum = defaultKeyNum + 1;
        editBixinName.setText(String.format("BixinKey%s", String.valueOf(defaultKeyNameNum)));
        editBixinName.addTextChangedListener(new TextWatcher() {
            CharSequence input;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                input = s;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tetNum.setText(String.format(Locale.CHINA, "%d/20", input.length()));
                if (input.length() > 19) {
                    Toast.makeText(MultiSigWalletCreator.this, R.string.moreinput_text, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editScan.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String strLine = editScan.getText().toString();
                if (!TextUtils.isEmpty(strLine)) {
                    view.findViewById(R.id.btn_ConfirmAll).setVisibility(View.GONE);
                    view.findViewById(R.id.lin_ComfirmAll).setVisibility(View.VISIBLE);
                } else {
                    view.findViewById(R.id.btn_ConfirmAll).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.lin_ComfirmAll).setVisibility(View.GONE);
                }
            }
        });
        //sweep
        view.findViewById(R.id.sweep_cosigner_popup).setOnClickListener(v -> {
            rxPermissions
                    .request(Manifest.permission.CAMERA)
                    .subscribe(granted -> {
                        if (granted) { // Always true pre-M
                            //If you have already authorized it, you can directly jump to the QR code scanning interface
                            Intent intent2 = new Intent(this, CaptureActivity.class);
                            ZxingConfig config = new ZxingConfig();
                            config.setPlayBeep(true);
                            config.setShake(true);
                            config.setDecodeBarCode(false);
                            config.setFullScreenScan(true);
                            config.setShowAlbum(false);
                            config.setShowbottomLayout(false);
                            intent2.putExtra(Constant.INTENT_ZXING_CONFIG, config);
                            startActivityForResult(intent2, REQUEST_CODE);
                        } else { // Oups permission denied
                            Toast.makeText(this, R.string.photopersion, Toast.LENGTH_SHORT).show();
                        }
                    }).dispose();

        });
        //stick
        view.findViewById(R.id.paste_cosigner_popup1).setOnClickListener(v -> {
            //get Shear plate TODO：
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard != null) {
                ClipData data = clipboard.getPrimaryClip();
                if (data != null && data.getItemCount() > 0) {
                    editScan.setText(data.getItemAt(0).getText());
                }
            }
        });

        view.findViewById(R.id.btn_Clear).setOnClickListener(v -> {
            editScan.setText("");
        });
        view.findViewById(R.id.btn_ConfirmAll).setOnClickListener(v -> {
            String strBixinname = editBixinName.getText().toString();
            String strSweep = editScan.getText().toString();
            if (TextUtils.isEmpty(strBixinname)) {
                mToast(getString(R.string.input_name));
                return;
            }
            if (TextUtils.isEmpty(strSweep)) {
                mToast(getString(R.string.input_public_address));
            }
        });
        view.findViewById(R.id.btn_Confirm).setOnClickListener(v -> {
            String strBixinname = editBixinName.getText().toString();
            String strSweep = editScan.getText().toString();
            if (TextUtils.isEmpty(strBixinname)) {
                mToast(getString(R.string.input_name));
                return;
            }
            if (TextUtils.isEmpty(strSweep)) {
                mToast(getString(R.string.input_public_address));
                return;
            }
            AddBixinKeyEvent addBixinKeyEvent = new AddBixinKeyEvent();
            boolean exist = false;
            if (addEventsDatas.size() != 0) {
                for (int i = 0; i < addEventsDatas.size(); i++) {
                    if (strSweep.equals(addEventsDatas.get(i).getKeyaddress())) {
                        mToast(getString(R.string.please_change_xpub));
                        exist = true;
                        break;
                    }
                }
                if (!exist) {
                    addBixinKeyEvent = new AddBixinKeyEvent();
                    addBixinKeyEvent.setKeyname(strBixinname);
                    addBixinKeyEvent.setKeyaddress(strSweep);
                    addBixinKeyEvent.setDeviceId("");
                    addEventsDatas.add(addBixinKeyEvent);
                    dialogBtoms.cancel();
                }
            } else {
                addBixinKeyEvent.setKeyname(strBixinname);
                addBixinKeyEvent.setKeyaddress(strSweep);
                addEventsDatas.add(addBixinKeyEvent);
                dialogBtoms.cancel();
            }
            //public person
            PublicPersonAdapter publicPersonAdapter = new PublicPersonAdapter(addEventsDatas);
            reclPublicPerson.setAdapter(publicPersonAdapter);
            //bixinKEY
            AddBixinKeyAdapter addBixinKeyAdapter = new AddBixinKeyAdapter(addEventsDatas);
            reclBinxinKey.setAdapter(addBixinKeyAdapter);
            cosignerNum = Integer.parseInt(tvIndicator.getText().toString());
            bnCompleteAddCosigner.setText(String.format("%s (%s-%s)", getString(R.string.next), addEventsDatas.size() + "", cosignerNum));

            if (addEventsDatas.size() == cosignerNum) {
                bnCompleteAddCosigner.setEnabled(true);
                bnCompleteAddCosigner.setBackground(getDrawable(R.drawable.little_radio_blue));
                bnAddKey.setVisibility(View.GONE);
            }
            edit.putInt("defaultKeyNum", defaultKeyNameNum);
            edit.apply();
            addBixinKeyAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
                @Override
                public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                    cosignerNum = Integer.parseInt(tvIndicator.getText().toString());
                    switch (view.getId()) {
                        case R.id.img_deleteKey:
                            try {
                                Daemon.commands.callAttr("delete_xpub", strSweep);
                            } catch (Exception e) {
                                e.printStackTrace();
                                if (e.getMessage().contains("the xpub to be delete not in keystore")) {
                                    mToast(getString(R.string.no_delete_xpub));
                                }
                            }
                            addEventsDatas.remove(position);
                            addBixinKeyAdapter.notifyDataSetChanged();
                            bnAddKey.setVisibility(View.VISIBLE);
                            bnCompleteAddCosigner.setText(String.format("%s (%s-%s)", getString(R.string.next), addEventsDatas.size() + "", cosignerNum));
                            if (addEventsDatas.size() == cosignerNum) {
                                bnCompleteAddCosigner.setEnabled(true);
                                bnCompleteAddCosigner.setBackground(getDrawable(R.drawable.little_radio_blue));
                                bnAddKey.setVisibility(View.GONE);
                            } else {
                                bnCompleteAddCosigner.setEnabled(false);
                                bnCompleteAddCosigner.setBackground(getDrawable(R.drawable.little_radio_qian));
                            }
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + view.getId());
                    }
                }
            });
        });

        //cancel dialog
        view.findViewById(R.id.img_cancle).setOnClickListener(v -> {
            dialogBtoms.cancel();
        });

        dialogBtoms.setContentView(view);
        Window window = dialogBtoms.getWindow();
        //set pop_up size
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        //set locate
        window.setGravity(Gravity.BOTTOM);
        //set animal
        window.setWindowAnimations(R.style.AnimBottom);
        dialogBtoms.show();
    }

    private void showConfirmPubDialog(Context context, @LayoutRes int resource, String xpub, String device_id, String label) {
        //set see view
        View view = View.inflate(context, resource, null);
        Dialog dialogBtoms = new Dialog(context, R.style.dialog);

        TextView edit_bixinName = view.findViewById(R.id.edit_keyName);
        textView = view.findViewById(R.id.text_public_key_cosigner_popup);
        textView.setText(xpub);
        if (!TextUtils.isEmpty(label)) {
            edit_bixinName.setText(label);
        } else {
            edit_bixinName.setText(getString(R.string.BixinKey));
        }
        view.findViewById(R.id.btn_confirm).setOnClickListener(v -> {
            String strBixinname = edit_bixinName.getText().toString();
            String strSweep = textView.getText().toString();
            if (TextUtils.isEmpty(strBixinname)) {
                mToast(getString(R.string.input_name));
                return;
            }
            if (TextUtils.isEmpty(strSweep)) {
                mToast(getString(R.string.input_public_address));
                return;
            }
            AddBixinKeyEvent addBixinKeyEvent = new AddBixinKeyEvent();
            boolean exist = false;
            if (addEventsDatas.size() != 0) {
                for (int i = 0; i < addEventsDatas.size(); i++) {
                    if (strSweep.equals(addEventsDatas.get(i).getKeyaddress())) {
                        mToast(getString(R.string.please_change_xpub));
                        exist = true;
                        break;
                    }
                }
                if (!exist) {
                    addBixinKeyEvent = new AddBixinKeyEvent();
                    addBixinKeyEvent.setKeyname(strBixinname);
                    addBixinKeyEvent.setKeyaddress(strSweep);
                    addBixinKeyEvent.setDeviceId(device_id);
                    addEventsDatas.add(addBixinKeyEvent);
                    dialogBtoms.cancel();
                }
            } else {
                addBixinKeyEvent.setKeyname(strBixinname);
                addBixinKeyEvent.setKeyaddress(strSweep);
                addBixinKeyEvent.setDeviceId(device_id);
                addEventsDatas.add(addBixinKeyEvent);
                dialogBtoms.cancel();
            }

            //public person
            PublicPersonAdapter publicPersonAdapter = new PublicPersonAdapter(addEventsDatas);
            reclPublicPerson.setAdapter(publicPersonAdapter);
            //bixinKEY
            AddBixinKeyAdapter addBixinKeyAdapter = new AddBixinKeyAdapter(addEventsDatas);
            reclBinxinKey.setAdapter(addBixinKeyAdapter);
            cosignerNum = Integer.parseInt(tvIndicator.getText().toString());
            bnCompleteAddCosigner.setText(String.format("%s (%s-%s)", getString(R.string.next), addEventsDatas.size() + "", cosignerNum));
            if (addEventsDatas.size() == cosignerNum) {
                bnCompleteAddCosigner.setEnabled(true);
                bnCompleteAddCosigner.setBackground(getDrawable(R.drawable.little_radio_blue));
                bnAddKey.setVisibility(View.GONE);
            }

            addBixinKeyAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
                @Override
                public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                    cosignerNum = Integer.parseInt(tvIndicator.getText().toString());
                    if (view.getId() == R.id.img_deleteKey) {
                        try {
                            Daemon.commands.callAttr("delete_xpub", strSweep);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        addEventsDatas.remove(position);
                        addBixinKeyAdapter.notifyDataSetChanged();
                        bnCompleteAddCosigner.setText(String.format("%s (%s-%s)", getString(R.string.next), addEventsDatas.size() + "", cosignerNum));
                        bnAddKey.setVisibility(View.VISIBLE);
                        if (addEventsDatas.size() == cosignerNum) {
                            bnCompleteAddCosigner.setEnabled(true);
                            bnCompleteAddCosigner.setBackground(getDrawable(R.drawable.little_radio_blue));
                            bnAddKey.setVisibility(View.GONE);
                        } else {
                            bnCompleteAddCosigner.setEnabled(false);
                            bnCompleteAddCosigner.setBackground(getDrawable(R.drawable.little_radio_qian));
                        }
                    }
                }
            });
            dialogBtoms.cancel();
        });

        //cancel dialog
        view.findViewById(R.id.img_cancle).setOnClickListener(v -> {
            dialogBtoms.cancel();
        });


        dialogBtoms.setContentView(view);
        Window window = dialogBtoms.getWindow();
        //set pop_up size
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        //set locate
        window.setGravity(Gravity.BOTTOM);
        //set animal
        window.setWindowAnimations(R.style.AnimBottom);
        dialogBtoms.show();
    }


    private Runnable runnable = () -> showInputDialogs(MultiSigWalletCreator.this, R.layout.bixinkey_input);

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            if (data != null) {
                String content = data.getStringExtra(Constant.CODED_CONTENT);
                editScan.setText(content);
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!TextUtils.isEmpty(s.toString())) {
            if (s.length() > 13) {
                editWalletname.setTextSize(13);
            } else {
                editWalletname.setTextSize(15);
            }
            button.setEnabled(true);
            button.setBackground(getDrawable(R.drawable.button_bk));

        } else {
            button.setEnabled(false);
            button.setBackground(getDrawable(R.drawable.button_bk_grey));
        }

    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                ArrayList<String> pubList = new ArrayList<>();
                String strWalletname = editWalletname.getText().toString();
                strInditor1 = tvIndicator.getText().toString();
                strInditor2 = tvIndicatorTwo.getText().toString();
                for (int i = 0; i < addEventsDatas.size(); i++) {
                    String keyaddress = addEventsDatas.get(i).getKeyaddress();
                    String deviceId = addEventsDatas.get(i).getDeviceId();
                    pubList.add("[\"" + keyaddress + "\",\"" + deviceId + "\"]");
                }

                try {
                    Daemon.commands.callAttr("import_create_hw_wallet", strWalletname, strUp2, strUp1, pubList.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    myDialog.dismiss();
                    String message = e.getMessage();
                    if ("BaseException: file already exists at path".equals(message)) {
                        mToast(getString(R.string.changewalletname));
                    } else if (message.contains("The same xpubs have create wallet")) {
                        String haveWalletName = message.substring(message.indexOf("name=") + 5);
                        mToast(getString(R.string.xpub_have_wallet) + haveWalletName);
                    } else if (message.contains("invaild type of xpub")) {
                        mToast(getString(R.string.xpub_wrong));
                    } else if (message.contains("Wrong key type p2wpkh")) {
                        mToast(getString(R.string.wrong_key_type));
                    }
                    return;
                }
                page = 2;
                EventBus.getDefault().post(new FirstEvent("11"));
                edit.putInt("defaultName", walletNameNum);
                edit.apply();
                //Generate QR code
                mGeneratecode();
                myDialog.dismiss();
                cardViewOne.setVisibility(View.GONE);
                button.setVisibility(View.GONE);
                imgProgree1.setVisibility(View.GONE);
                imgProgree2.setVisibility(View.GONE);
                imgProgree3.setVisibility(View.VISIBLE);
                reclBinxinKey.setVisibility(View.GONE);
                bnAddKey.setVisibility(View.GONE);
                relTwoNext1.setVisibility(View.GONE);
                relFinish.setVisibility(View.VISIBLE);
                tetTrthree.setTextColor(getColor(R.color.button_bk_disableok));
                cardViewThree.setVisibility(View.VISIBLE);
                relFinish.setVisibility(View.VISIBLE);
                cardThreePublic.setVisibility(View.VISIBLE);
                tetWhoWallet.setText(String.format("%s  （%s/%s）", strWalletname, strInditor1, strInditor2));
                tetManyKey.setText(String.format("%s%s%s", getString(R.string.is_use), strInditor1, getString(R.string.the_only_bixinkey)));
            }
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(MutiSigWalletEvent event) {
        String xpub = event.getXpub();
        String deviceId = event.getDeviceId();
        String label = event.getLable();
        showConfirmPubDialog(this, R.layout.bixinkey_confirm, xpub, deviceId, label);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
