package org.haobtc.onekey.activities.jointwallet;

import android.annotation.SuppressLint;
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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chaquo.python.PyObject;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.MyApplication;
import org.haobtc.onekey.adapter.AddedXpubAdapter;
import org.haobtc.onekey.adapter.PublicAdapter;
import org.haobtc.onekey.adapter.PublicPersonAdapter;
import org.haobtc.onekey.adapter.SignNumAdapter;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.asynctask.BusinessAsyncTask;
import org.haobtc.onekey.bean.CNYBean;
import org.haobtc.onekey.bean.XpubItem;
import org.haobtc.onekey.constant.PyConstant;
import org.haobtc.onekey.event.AddBixinKeyEvent;
import org.haobtc.onekey.event.AddXpubEvent;
import org.haobtc.onekey.event.ButtonRequestEvent;
import org.haobtc.onekey.event.ChangePinEvent;
import org.haobtc.onekey.event.CreateWalletEvent;
import org.haobtc.onekey.event.DeviceSearchEvent;
import org.haobtc.onekey.event.FirstEvent;
import org.haobtc.onekey.event.GetXpubEvent;
import org.haobtc.onekey.manager.PyEnv;
import org.haobtc.onekey.ui.base.BaseActivity;
import org.haobtc.onekey.ui.activity.SearchDevicesActivity;
import org.haobtc.onekey.ui.dialog.ChooseAddXpubWayDialog;
import org.haobtc.onekey.ui.dialog.ValidateXpubDialog;
import org.haobtc.onekey.ui.fragment.DevicePINFragment;
import org.haobtc.onekey.utils.Daemon;
import org.haobtc.onekey.utils.MyDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author liyan
 */
public class MultiSigWalletCreator extends BaseActivity implements TextWatcher, BusinessAsyncTask.Helper {

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
    @BindView(R.id.button)
    Button button;
    @BindView(R.id.bn_add_key)
    RelativeLayout bnAddKey;
    @BindView(R.id.img_Progree1)
    ImageView imgProgree1;
    @BindView(R.id.img_Progree2)
    ImageView imgProgree2;
    @BindView(R.id.img_Progree3)
    ImageView imgProgree3;
    @BindView(R.id.card_viewOne)
    LinearLayout cardViewOne;
    @BindView(R.id.card_viewThree)
    RelativeLayout cardViewThree;
    @BindView(R.id.recl_BinxinKey)
    RecyclerView reclBinxinKey;
    @BindView(R.id.bn_complete_add_cosigner)
    Button bnCompleteAddCosigner;
    @BindView(R.id.tet_who_wallet)
    TextView tetWhoWallet;
    @BindView(R.id.tet_many_key)
    TextView tetManyKey;
    @BindView(R.id.recl_publicPerson)
    RecyclerView reclPublicPerson;
    public String pin = "";
    public static final String TAG = MultiSigWalletCreator.class.getSimpleName();
    @BindView(R.id.recl_public)
    RecyclerView reclPublic;
    @BindView(R.id.recl_sign_num)
    RecyclerView reclSignNum;
    @BindView(R.id.text_pub_num)
    TextView textPubNum;
    @BindView(R.id.rel_text_two)
    RelativeLayout relTextTwo;
    private RxPermissions rxPermissions;
    private static final int REQUEST_CODE = 0;
    private EditText editScan;
    private TextView textView;
    private ArrayList<XpubItem> addEventsDatas;
    private MyDialog myDialog;
    private PyObject walletAddressShowUi;
    private SharedPreferences.Editor edit;
    private int walletNameNum;
    private Bitmap bitmap;
    private SharedPreferences preferences;
    private int defaultKeyNameNum;
    private int page = 0;
    private int cosignerNum;
    private String publicNum = "3";
    private String signNum = "2";
    private SignNumAdapter signNumAdapter;

    /**
     * init
     */
    @Override
    public void init() {

        preferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        int defaultName = preferences.getInt("defaultName", 0);
        edit = preferences.edit();
//        rxPermissions = new RxPermissions(this);
//        myDialog = MyDialog.showDialog(MultiSigWalletCreator.this);
        editWalletname.addTextChangedListener(this);
        walletNameNum = defaultName + 1;
        editWalletname.setText(String.format("钱包%s", String.valueOf(walletNameNum)));
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        reclPublicPerson.setLayoutManager(manager);
        reclPublicPerson.setNestedScrollingEnabled(false);
        addEventsDatas = new ArrayList<>();
        publicData();
        signNumData(3);
    }
    /***
     * init layout
     * @return
     */
    @Override
    public int getContentViewId() {
        return R.layout.activity_many_wallet_together;
    }

    private void publicData() {
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(6, StaggeredGridLayoutManager.VERTICAL);
        reclPublic.setLayoutManager(layoutManager);
        ArrayList<CNYBean> pubList = new ArrayList<>();
        for (int i = 2; i < 13; i++) {
            CNYBean cnyBean = new CNYBean(i + "", false);
            pubList.add(cnyBean);
        }
        PublicAdapter publicAdapter = new PublicAdapter(MultiSigWalletCreator.this, pubList, 1);
        reclPublic.setAdapter(publicAdapter);
        publicAdapter.setOnLisennorClick(new PublicAdapter.onLisennorClick() {
            @Override
            public void itemClick(int pos) {
                publicNum = pubList.get(pos).getName();
                signNumData(Integer.parseInt(publicNum));
            }
        });

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void signNumData(int num) {
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(6, StaggeredGridLayoutManager.VERTICAL);
        reclSignNum.setLayoutManager(layoutManager);
        ArrayList<CNYBean> signNumList = new ArrayList<>();
        for (int i = 1; i < 13; i++) {
            CNYBean cnyBean = new CNYBean(i + "", false);
            signNumList.add(cnyBean);
        }
        if (Integer.parseInt(signNum) > Integer.parseInt(publicNum)) {
            signNumAdapter = new SignNumAdapter(MultiSigWalletCreator.this, signNumList, -1, num);
            signNum = "";
        } else {
            signNumAdapter = new SignNumAdapter(MultiSigWalletCreator.this, signNumList, Integer.parseInt(signNum) - 1, num);
        }

        reclSignNum.setAdapter(signNumAdapter);
        signNumAdapter.setOnLisennorClick(new SignNumAdapter.onLisennorClick() {
            @Override
            public void itemClick(int pos) {
                signNum = signNumList.get(pos).getName();

            }
        });
        if (!TextUtils.isEmpty(editWalletname.getText().toString()) && !TextUtils.isEmpty(signNum)) {
            button.setEnabled(true);
            button.setBackground(getDrawable(R.drawable.btn_checked));

        } else {
            button.setEnabled(false);
            button.setBackground(getDrawable(R.drawable.btn_no_check));
        }

    }

    @SingleClick
    @OnClick({R.id.img_back, R.id.button, R.id.bn_add_key, R.id.btn_Finish, R.id.bn_complete_add_cosigner})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                if (page == 0) {
                    finish();
                } else if (page == 1) {
                    cardViewOne.setVisibility(View.VISIBLE);
                    button.setVisibility(View.VISIBLE);
                    imgProgree1.setVisibility(View.VISIBLE);
                    imgProgree2.setVisibility(View.GONE);
                    imgProgree3.setVisibility(View.GONE);
                    reclBinxinKey.setVisibility(View.GONE);
                    bnAddKey.setVisibility(View.GONE);
                    relTextTwo.setVisibility(View.GONE);
                    bnCompleteAddCosigner.setVisibility(View.GONE);
                    tetTrtwo.setTextColor(getColor(R.color.text_two));
                    page = 0;
                } else if (page == 2) {
                    cardViewOne.setVisibility(View.GONE);
                    button.setVisibility(View.GONE);
                    imgProgree1.setVisibility(View.GONE);
                    imgProgree2.setVisibility(View.VISIBLE);
                    imgProgree3.setVisibility(View.GONE);
                    relTextTwo.setVisibility(View.VISIBLE);
                    reclBinxinKey.setVisibility(View.VISIBLE);
                    bnAddKey.setVisibility(View.VISIBLE);
                    bnCompleteAddCosigner.setVisibility(View.VISIBLE);
                    tetTrthree.setTextColor(getColor(R.color.text_two));
                    cardViewThree.setVisibility(View.GONE);
                    page = 1;
                }

                break;
            case R.id.button:
                mCreatWalletNext();
                break;
            case R.id.bn_add_key:
                new ChooseAddXpubWayDialog().show(getSupportFragmentManager(), "xpub");
                break;
            case R.id.btn_Finish:
                finish();
                break;
            case R.id.bn_complete_add_cosigner:
                myDialog.show();
                handler.sendEmptyMessage(1);
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

    private void mCreatWalletNext() {
        page = 1;
        cardViewOne.setVisibility(View.GONE);
        button.setVisibility(View.GONE);
        imgProgree1.setVisibility(View.GONE);
        imgProgree2.setVisibility(View.VISIBLE);
        imgProgree3.setVisibility(View.GONE);
        reclBinxinKey.setNestedScrollingEnabled(false);
        reclBinxinKey.setVisibility(View.VISIBLE);
        bnAddKey.setVisibility(View.VISIBLE);
        bnCompleteAddCosigner.setVisibility(View.VISIBLE);
        relTextTwo.setVisibility(View.VISIBLE);
        tetTrtwo.setTextColor(getColor(R.color.onekey));
        textPubNum.setText(String.format("%s/%s", addEventsDatas.size() + "", publicNum));
        if (addEventsDatas.size() == Integer.parseInt(publicNum)) {
            bnCompleteAddCosigner.setEnabled(true);
            bnCompleteAddCosigner.setBackground(getDrawable(R.drawable.btn_checked));
        } else {
            bnCompleteAddCosigner.setEnabled(false);
            bnCompleteAddCosigner.setBackground(getDrawable(R.drawable.btn_no_check));
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onSearchDeviceEvent(DeviceSearchEvent event) {
       Intent intent = new Intent(this, SearchDevicesActivity.class);
       intent.putExtra(org.haobtc.onekey.constant.Constant.SEARCH_DEVICE_MODE, org.haobtc.onekey.constant.Constant.SearchDeviceMode.MODE_BIND_ADMIN_PERSON);
       startActivity(intent);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddXpubEvent(AddXpubEvent event) {
        addXpub(event.getName(), event.getXpub());
    }
    private void addXpub(String name, String xpub) {

            AddBixinKeyEvent addBixinKeyEvent = new AddBixinKeyEvent();
            boolean exist = false;
        if (addEventsDatas.size() != 0) {
            for (XpubItem item : addEventsDatas) {
                if (xpub.equals(item.getXpub())) {
                    showToast(getString(R.string.please_change_xpub));
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                addEventsDatas.add(new XpubItem(name, xpub));
            }
        } else {
            addEventsDatas.add(new XpubItem(name, xpub));
        }
            //public person
            PublicPersonAdapter publicPersonAdapter = new PublicPersonAdapter(addEventsDatas);
            reclPublicPerson.setAdapter(publicPersonAdapter);
            //bixinKEY
            AddedXpubAdapter addedXpubAdapter = new AddedXpubAdapter(addEventsDatas);
            reclBinxinKey.setAdapter(addedXpubAdapter);
            cosignerNum = Integer.parseInt(publicNum);
            textPubNum.setText(String.format("%s/%s", addEventsDatas.size() + "", cosignerNum));

            if (addEventsDatas.size() == cosignerNum) {
                bnCompleteAddCosigner.setEnabled(true);
                bnCompleteAddCosigner.setBackground(getDrawable(R.drawable.btn_checked));
                bnAddKey.setVisibility(View.GONE);
            }
            edit.putInt("defaultKeyNum", defaultKeyNameNum);
            edit.apply();
            addedXpubAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
                @Override
                public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                    cosignerNum = Integer.parseInt(publicNum);
                    if (view.getId() == R.id.img_deleteKey) {
                        try {
                            Daemon.commands.callAttr("delete_xpub", xpub);
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (e.getMessage().contains("the xpub to be delete not in keystore")) {
                                showToast(getString(R.string.no_delete_xpub));
                            }
                        }
                        addEventsDatas.remove(position);
                        addedXpubAdapter.notifyDataSetChanged();
                        bnAddKey.setVisibility(View.VISIBLE);
                        textPubNum.setText(String.format("%s/%s", addEventsDatas.size() + "", cosignerNum));
                        if (addEventsDatas.size() == cosignerNum) {
                            bnCompleteAddCosigner.setEnabled(true);
                            bnCompleteAddCosigner.setBackground(getDrawable(R.drawable.btn_checked));
                            bnAddKey.setVisibility(View.GONE);
                        } else {
                            bnCompleteAddCosigner.setEnabled(false);
                            bnCompleteAddCosigner.setBackground(getDrawable(R.drawable.btn_no_check));
                        }
                    } else {
                        throw new IllegalStateException("Unexpected value: " + view.getId());
                    }
                }
            });
    }

//    private void showConfirmPubDialog(Context context, @LayoutRes int resource, String xpub, String device_id, String label) {
//        //set see view
//        View view = View.inflate(context, resource, null);
//        Dialog dialogBtoms = new Dialog(context, R.style.dialog);
//
//        TextView edit_bixinName = view.findViewById(R.id.edit_keyName);
//        textView = view.findViewById(R.id.text_public_key_cosigner_popup);
//        textView.setText(xpub);
//        if (!TextUtils.isEmpty(label)) {
//            edit_bixinName.setText(label);
//        } else {
//            edit_bixinName.setText(getString(R.string.BixinKey));
//        }
//        view.findViewById(R.id.btn_confirm).setOnClickListener(v -> {
//            String strBixinname = edit_bixinName.getText().toString();
//            String strSweep = textView.getText().toString();
//            if (TextUtils.isEmpty(strBixinname)) {
//                mToast(getString(R.string.input_name));
//                return;
//            }
//            if (TextUtils.isEmpty(strSweep)) {
//                mToast(getString(R.string.input_public_address));
//                return;
//            }
//            AddBixinKeyEvent addBixinKeyEvent = new AddBixinKeyEvent();
//            boolean exist = false;
//            if (addEventsDatas.size() != 0) {
//                for (int i = 0; i < addEventsDatas.size(); i++) {
//                    if (strSweep.equals(addEventsDatas.get(i).getKeyaddress())) {
//                        mToast(getString(R.string.please_change_xpub));
//                        exist = true;
//                        break;
//                    }
//                }
//                if (!exist) {
//                    addBixinKeyEvent = new AddBixinKeyEvent();
//                    addBixinKeyEvent.setKeyname(strBixinname);
//                    addBixinKeyEvent.setKeyaddress(strSweep);
//                    addBixinKeyEvent.setDeviceId(device_id);
//                    addEventsDatas.add(addBixinKeyEvent);
//                    dialogBtoms.cancel();
//                }
//            } else {
//                addBixinKeyEvent.setKeyname(strBixinname);
//                addBixinKeyEvent.setKeyaddress(strSweep);
//                addBixinKeyEvent.setDeviceId(device_id);
//                addEventsDatas.add(addBixinKeyEvent);
//                dialogBtoms.cancel();
//            }
//
//            //public person
//            PublicPersonAdapter publicPersonAdapter = new PublicPersonAdapter(addEventsDatas);
//            reclPublicPerson.setAdapter(publicPersonAdapter);
//            //bixinKEY
//            AddBixinKeyAdapter addBixinKeyAdapter = new AddBixinKeyAdapter(addEventsDatas);
//            reclBinxinKey.setAdapter(addBixinKeyAdapter);
//            cosignerNum = Integer.parseInt(publicNum);
//            textPubNum.setText(String.format("%s/%s", addEventsDatas.size() + "", cosignerNum));
//            if (addEventsDatas.size() == cosignerNum) {
//                bnCompleteAddCosigner.setEnabled(true);
//                bnCompleteAddCosigner.setBackground(getDrawable(R.drawable.little_radio_blue));
//                bnAddKey.setVisibility(View.GONE);
//            }
//
//            addBixinKeyAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
//                @Override
//                public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
//                    cosignerNum = Integer.parseInt(publicNum);
//                    if (view.getId() == R.id.img_deleteKey) {
//                        try {
//                            Daemon.commands.callAttr("delete_xpub", strSweep);
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        addEventsDatas.remove(position);
//                        addBixinKeyAdapter.notifyDataSetChanged();
//                        textPubNum.setText(String.format("%s/%s", addEventsDatas.size() + "", cosignerNum));
//                        bnAddKey.setVisibility(View.VISIBLE);
//                        if (addEventsDatas.size() == cosignerNum) {
//                            bnCompleteAddCosigner.setEnabled(true);
//                            bnCompleteAddCosigner.setBackground(getDrawable(R.drawable.little_radio_blue));
//                            bnAddKey.setVisibility(View.GONE);
//                        } else {
//                            bnCompleteAddCosigner.setEnabled(false);
//                            bnCompleteAddCosigner.setBackground(getDrawable(R.drawable.little_radio_qian));
//                        }
//                    }
//                }
//            });
//            dialogBtoms.cancel();
//        });
//
//        //cancel dialog
//        view.findViewById(R.id.img_cancle).setOnClickListener(v -> {
//            dialogBtoms.cancel();
//        });
//
//
//        dialogBtoms.setContentView(view);
//        Window window = dialogBtoms.getWindow();
//        //set pop_up size
//        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
//        //set locate
//        window.setGravity(Gravity.BOTTOM);
//        //set animal
//        window.setWindowAnimations(R.style.AnimBottom);
//        dialogBtoms.show();
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 0 && resultCode == RESULT_OK) {
//            if (data != null) {
//                String content = data.getStringExtra(Constant.CODED_CONTENT);
//                editScan.setText(content);
//            }
//        }
//    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!TextUtils.isEmpty(s.toString()) && !TextUtils.isEmpty(signNum)) {
            if (s.length() > 13) {
                editWalletname.setTextSize(13);
            } else {
                editWalletname.setTextSize(15);
            }
            button.setEnabled(true);
            button.setBackground(getDrawable(R.drawable.btn_checked));

        } else {
            button.setEnabled(false);
            button.setBackground(getDrawable(R.drawable.btn_no_check));
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
                for (int i = 0; i < addEventsDatas.size(); i++) {
                    String keyaddress = addEventsDatas.get(i).getXpub();
                    String deviceId = addEventsDatas.get(i).getName();
                    pubList.add("[\"" + keyaddress + "\",\"" + deviceId + "\"]");
                }

                try {
                    Daemon.commands.callAttr("import_create_hw_wallet", strWalletname, Integer.parseInt(signNum), Integer.parseInt(publicNum), pubList.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    myDialog.dismiss();
                    String message = e.getMessage();
                    if ("BaseException: file already exists at path".equals(message)) {
                        showToast(getString(R.string.changewalletname));
                    } else if (message.contains("The same xpubs have create wallet")) {
                        String haveWalletName = message.substring(message.indexOf("name=") + 5);
                        showToast(getString(R.string.xpub_have_wallet) + haveWalletName);
                    } else if (message.contains("invaild type of xpub")) {
                        showToast(getString(R.string.xpub_wrong));
                    } else if (message.contains("Wrong key type p2wpkh")) {
                        showToast(getString(R.string.wrong_key_type));
                    }
                    return;
                }
                page = 2;
                EventBus.getDefault().post(new FirstEvent("11"));
                edit.putInt("defaultName", walletNameNum);
                edit.apply();
                myDialog.dismiss();
                cardViewOne.setVisibility(View.GONE);
                button.setVisibility(View.GONE);
                imgProgree1.setVisibility(View.GONE);
                imgProgree2.setVisibility(View.GONE);
                imgProgree3.setVisibility(View.VISIBLE);
                relTextTwo.setVisibility(View.GONE);
                reclBinxinKey.setVisibility(View.GONE);
                bnAddKey.setVisibility(View.GONE);
                bnCompleteAddCosigner.setVisibility(View.GONE);
                tetTrthree.setTextColor(getColor(R.color.onekey));
                cardViewThree.setVisibility(View.VISIBLE);
                tetWhoWallet.setText(String.format("%s %s of %s %s", getString(R.string.created_succse), signNum, publicNum, getString(R.string.public_wallet)));
                tetManyKey.setText(String.format("%s%s%s%s%s", getString(R.string.use_front), publicNum, getString(R.string.use_center), signNum, getString(R.string.use_behind)));
            }
        }
    };

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void event(MutiSigWalletEvent event) {
//        String xpub = event.getXpub();
//        String deviceId = event.getDeviceId();
//        String label = event.getLable();
////        showConfirmPubDialog(this, R.layout.bixinkey_confirm, xpub, deviceId, label);
//    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetXpub(GetXpubEvent event) {
        getXpubP2wsh();
    }
    /**
     * 获取用于个人钱包的扩展公钥
     * */
    private void getXpubP2wsh() {
        new BusinessAsyncTask().setHelper(this).execute(BusinessAsyncTask.GET_EXTEND_PUBLIC_KEY,
                MyApplication.getInstance().getDeviceWay());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCreateWallet(CreateWalletEvent event) {

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangePin(ChangePinEvent event) {
        // 回写PIN码
        PyEnv.setPin(event.getPinNew());
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onButtonRequest(ButtonRequestEvent event) {
        if (PyConstant.PIN_CURRENT == event.getType()) {
            startFragment(new DevicePINFragment(PyConstant.PIN_CURRENT));
        }
    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onException(Exception e) {

    }

    @Override
    public void onResult(String s) {
        startFragment(new ValidateXpubDialog(s));
    }

    @Override
    public void onCancelled() {

    }

    @Override
    public void currentMethod(String methodName) {

    }


    @Override
    public boolean needEvents() {
        return true;
    }
}
