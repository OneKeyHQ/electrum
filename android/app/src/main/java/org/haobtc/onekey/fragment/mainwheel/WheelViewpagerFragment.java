package org.haobtc.onekey.fragment.mainwheel;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.chaquo.python.PyObject;
import com.google.common.base.Strings;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.MainActivity;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.ReceivedPageActivity;
import org.haobtc.onekey.activities.SendOne2OneMainPageActivity;
import org.haobtc.onekey.activities.personalwallet.WalletDetailsActivity;
import org.haobtc.onekey.activities.service.CommunicationModeSelector;
import org.haobtc.onekey.activities.settings.recovery_set.BackupRecoveryActivity;
import org.haobtc.onekey.activities.sign.SignActivity;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.bean.MainNewWalletBean;
import org.haobtc.onekey.event.CardUnitEvent;
import org.haobtc.onekey.event.FirstEvent;
import org.haobtc.onekey.utils.Daemon;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import static org.haobtc.onekey.constant.Constant.CURRENT_CURRENCY_SYMBOL;
import static org.haobtc.onekey.constant.Constant.CURRENT_SELECTED_WALLET_TYPE;

/**
 * A simple {@link Fragment} subclass.
 */
public class WheelViewpagerFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private TextView walletCardName;
    private TextView walletpersonce;
    private TextView walletBlance;
    private String name;
    private String personce;
    private Button btnLeft;
    private Button btncenetr;
    private PyObject selectWallet;
    private TextView tetCny;
    private TextView btnAppWallet;
    private boolean isFirst = false;
    private SharedPreferences.Editor edit;
    private SharedPreferences preferences;
    private TextView tetFiat;
    private LinearLayout conlayBback;
    private Button btnRight;
    private String strCNY;
    private String substring;
    private TextView walletCard;
    private LinearLayout linearSend;
    private LinearLayout linearReceive;
    private LinearLayout linearSign;
    private TextView testStar;
    private LinearLayout linCheck;
    private TextView testStarCny;

    public WheelViewpagerFragment() {

    }

    public WheelViewpagerFragment(String name, String personce) {
        this.name = name;
        this.personce = personce;
    }

    public WheelViewpagerFragment(String name, String personce, boolean isFirst) {
        this.isFirst = isFirst;
        this.name = name;
        this.personce = personce;
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wheel_viewpager, container, false);
        EventBus.getDefault().register(this);
        preferences = requireActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        edit = preferences.edit();
        walletCardName = view.findViewById(R.id.wallet_card_name);
        walletpersonce = view.findViewById(R.id.wallet_card_tv2);
        walletBlance = view.findViewById(R.id.wallet_card_tv4);
        tetFiat = view.findViewById(R.id.tet_fiat);
        btnLeft = view.findViewById(R.id.wallet_card_bn1);
        btncenetr = view.findViewById(R.id.wallet_card_bn2);
        btnRight = view.findViewById(R.id.wallet_card_bn3);
        btnAppWallet = view.findViewById(R.id.app_wallet);
        tetCny = view.findViewById(R.id.tet_Cny);
        conlayBback = view.findViewById(R.id.conlay_back);
        walletCard = view.findViewById(R.id.wallet_card_tv3);
        linearSend = view.findViewById(R.id.linear_send);
        linearReceive = view.findViewById(R.id.linear_receive);
        linearSign = view.findViewById(R.id.linear_sign);
        testStar = view.findViewById(R.id.test_star);
        linCheck = view.findViewById(R.id.lin_check_money);
        testStarCny = view.findViewById(R.id.test_star_cny);
        CheckBox radioCheck = view.findViewById(R.id.img_check_money);
        radioCheck.setOnCheckedChangeListener(this);

        init();
        initdata();

        return view;
    }

    private void init() {
        linearSend.setOnClickListener(this);
        linearReceive.setOnClickListener(this);
        linearSign.setOnClickListener(this);
        conlayBback.setOnClickListener(this);
        walletCardName.setText(name);
        if (getActivity() != null) {
            if (!TextUtils.isEmpty(personce)) {
                if ("standard".equals(personce)) {
                    btnAppWallet.setVisibility(View.VISIBLE);
                    walletpersonce.setVisibility(View.GONE);
                    conlayBback.setBackground(getActivity().getDrawable(R.drawable.home_bg));
                    btnLeft.setBackground(getActivity().getDrawable(R.drawable.button_bk_small));
                    btncenetr.setBackground(getActivity().getDrawable(R.drawable.button_bk_small));
                    btnRight.setBackground(getActivity().getDrawable(R.drawable.button_bk_small));
                } else {
                    walletpersonce.setText(personce);
                    conlayBback.setBackground(getActivity().getDrawable(R.drawable.home_gray_bg));
                    btnLeft.setBackground(getActivity().getDrawable(R.drawable.text_tou_back_blue));
                    btncenetr.setBackground(getActivity().getDrawable(R.drawable.text_tou_back_blue));
                    btnRight.setBackground(getActivity().getDrawable(R.drawable.text_tou_back_blue));
                }
            }
        }
    }

    private void initdata() {
        // first wallet
        if (isFirst) {
            refreshList();
        }
        //get wallet unit
        String baseUnit = preferences.getString("base_unit", "");
        walletCard.setText(String.format("%s(%s)", getString(R.string.balance), baseUnit));

    }

    public void refreshList() {
        if (preferences != null) {
            String cnyStrunit = preferences.getString(CURRENT_CURRENCY_SYMBOL, "CNY");
            testStarCny.setText(String.format("%s%s", getString(R.string.cny_star), cnyStrunit));
        }

        try {
            Daemon.commands.callAttr("load_wallet", name);
            getWalletMsg();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //get wallet message
    public void getWalletMsg() {
        try {
            selectWallet = Daemon.commands.callAttr("select_wallet", name);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        if (selectWallet != null) {
            String toString = selectWallet.toString();
            Gson gson = new Gson();
            MainNewWalletBean mainWheelBean = gson.fromJson(toString, MainNewWalletBean.class);
            String walletType = mainWheelBean.getWalletType();
            String balanceC = mainWheelBean.getBalance();
            if (!"standard".equals(personce)) {
                EventBus.getDefault().post(new FirstEvent("load_wallet_finish"));
            } else {
                EventBus.getDefault().post(new FirstEvent("load_wallet_finish_visible"));
            }
            if (!TextUtils.isEmpty(walletType)) {
                String streplaceC = walletType.replaceAll("of", "/");
                walletpersonce.setText(streplaceC);
            }
            if (!TextUtils.isEmpty(balanceC)) {
                if (balanceC.contains("(")) {
                    substring = balanceC.substring(0, balanceC.indexOf("("));
                    walletBlance.setText(substring);

                    strCNY = balanceC.substring(balanceC.indexOf("(") + 1, balanceC.indexOf(")"));
                    if (!TextUtils.isEmpty(strCNY)) {
                        tetCny.setText(String.format("≈ %s", strCNY));
                    }
                } else {
                    walletBlance.setText(balanceC);
                }
            }
        }
    }

    public void setValue(String msgVote) {
        try {
            JSONObject jsonObject = new JSONObject(msgVote);
            if (msgVote.contains("balance")) {
                String balance = jsonObject.getString("balance");
                // Log.i("getWalletMsgJXM", "event+substring:::" + balance);
                if (!TextUtils.isEmpty(balance)) {
                    walletBlance.setText(balance);
                }
            }
            if (msgVote.contains("fiat")) {
                String fiat = jsonObject.getString("fiat");
                if (!TextUtils.isEmpty(fiat)) {
                    tetCny.setText(String.format("≈ %s", fiat));
                }
            }
            if (msgVote.contains("unconfirmed")) {
                String unconfirmed = jsonObject.getString("unconfirmed");
                tetFiat.setText(String.format("%s%s", unconfirmed, getString(R.string.unconfirm)));
            } else {
                tetFiat.setText("");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SingleClick
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.conlay_back:
                Intent intent = new Intent(getActivity(), WalletDetailsActivity.class);
                intent.putExtra("wallet_name", name);
                intent.putExtra("wallet_type", personce);
                startActivity(intent);
                break;
            case R.id.linear_send:
                if (!MainActivity.isBacked && !"standard".equals(personce)) {
                    //unBackup key dialog
                    unBackupKeyDialog();

                } else {
                    edit.putString("wallet_type_to_sign", personce);
                    edit.apply();
                    Intent intent1 = new Intent(getActivity(), SendOne2OneMainPageActivity.class);
                    intent1.putExtra("wallet_name", name);
                    intent1.putExtra("wallet_type", personce);
                    intent1.putExtra("strNowBtc", substring);
                    intent1.putExtra("hideRefresh", "");
                    intent1.putExtra("strNowCny", strCNY);
                    startActivity(intent1);
                }

                break;
            case R.id.linear_receive:
                if (!MainActivity.isBacked && !"standard".equals(personce)) {
                    //unBackup key dialog
                    unBackupKeyDialog();
                } else {
                    Intent intent2 = new Intent(getActivity(), ReceivedPageActivity.class);
                    intent2.putExtra(CURRENT_SELECTED_WALLET_TYPE, personce);
                    intent2.putExtra("hideWalletReceive", "");
                    startActivity(intent2);
                }
                break;
            case R.id.linear_sign:
                if (!MainActivity.isBacked && !"standard".equals(personce)) {
                    //unBackup key dialog
                    unBackupKeyDialog();
                } else {
                    Intent intent3 = new Intent(getActivity(), SignActivity.class);
                    intent3.putExtra("personceType", personce);
                    startActivity(intent3);
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }
    }

    //unBackup key dialog
    private void unBackupKeyDialog() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.un_backup_dialog, null, false);
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).setView(view).create();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        view.findViewById(R.id.img_cancel).setOnClickListener(v -> {
            alertDialog.dismiss();
        });
        view.findViewById(R.id.btn_add_Speed).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), BackupRecoveryActivity.class);
            intent.putExtra("home_un_backup", "home_un_backup");
            startActivity(intent);
            alertDialog.dismiss();
        });
        alertDialog.show();
        //show center
        Window dialogWindow = alertDialog.getWindow();
        WindowManager m = getActivity().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = dialogWindow.getAttributes();
        p.width = (int) (d.getWidth() * 0.95);
        p.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(p);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            testStar.setVisibility(View.GONE);
            linCheck.setVisibility(View.VISIBLE);
            testStarCny.setVisibility(View.GONE);
            tetCny.setVisibility(View.VISIBLE);

        } else {
            testStar.setVisibility(View.VISIBLE);
            linCheck.setVisibility(View.GONE);
            testStarCny.setVisibility(View.VISIBLE);
            tetCny.setVisibility(View.GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(CardUnitEvent cardUnitEvent) {
        String cnyStrunit = preferences.getString(CURRENT_CURRENCY_SYMBOL, "CNY");
        testStarCny.setText(String.format("%s%s", getString(R.string.cny_star), cnyStrunit));

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}