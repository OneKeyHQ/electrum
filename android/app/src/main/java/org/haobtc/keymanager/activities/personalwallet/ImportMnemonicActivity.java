package org.haobtc.keymanager.activities.personalwallet;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;

import org.haobtc.keymanager.R;
import org.haobtc.keymanager.activities.base.BaseActivity;
import org.haobtc.keymanager.adapter.HelpWordAdapter;
import org.haobtc.keymanager.aop.SingleClick;
import org.haobtc.keymanager.utils.Daemon;
import org.haobtc.keymanager.utils.MyDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ImportMnemonicActivity extends BaseActivity {

    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.recl_helpWord)
    RecyclerView reclHelpWord;
    @BindView(R.id.edit_one)
    TextView editOne;
    @BindView(R.id.edit_two)
    TextView editTwo;
    @BindView(R.id.edit_three)
    TextView editThree;
    @BindView(R.id.edit_four)
    TextView editFour;
    @BindView(R.id.edit_five)
    TextView editFive;
    @BindView(R.id.edit_six)
    TextView editSix;
    @BindView(R.id.edit_seven)
    TextView editSeven;
    @BindView(R.id.edit_eight)
    TextView editEight;
    @BindView(R.id.edit_nine)
    TextView editNine;
    @BindView(R.id.edit_ten)
    TextView editTen;
    @BindView(R.id.edit_eleven)
    TextView editEleven;
    @BindView(R.id.edit_twelve)
    TextView editTwelve;
    private String strRemeber = "";
    private String strPass1;
    private MyDialog myDialog;
    private String strName;

    @Override
    public int getLayoutId() {
        return R.layout.activity_verification_mnemonic_word;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        myDialog = MyDialog.showDialog(ImportMnemonicActivity.this);
        Intent intent = getIntent();
        String strSeeds = intent.getStringExtra("strSeeds");
        strPass1 = intent.getStringExtra("strPass1");
        strName = intent.getStringExtra("strName");
        assert strSeeds != null;
        String[] wordsList = strSeeds.split("\\s+");

        ArrayList<String> strings = new ArrayList<>();
        Collections.addAll(strings, wordsList);

        List<String> randomList = createRandomList(strings, 12);

        reclHelpWord.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        HelpWordAdapter helpWordAdapter = new HelpWordAdapter(randomList);
        reclHelpWord.setAdapter(helpWordAdapter);
        helpWordAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @SingleClick
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                mIntent(AppWalletCreateFinishActivity.class);
                String strWord = randomList.get(position);
                helpWord(strWord);
            }
        });

    }


    //change list -- random number
    private List<String> createRandomList(List<String> list, int n) {
        Map<Integer, String> mmap = new HashMap<Integer, String>();
        List<String> mlistNew = new ArrayList<String>();
        while (mmap.size() < n) {
            int random = (int) (Math.random() * list.size());
            if (!mmap.containsKey(random)) {
                mmap.put(random, "");
                mlistNew.add(list.get(random));
            }
        }
        return mlistNew;
    }


    private void helpWord(String strWord) {
        strRemeber = strWord;
        if (TextUtils.isEmpty(editOne.getText().toString())) {
            editOne.setText(strWord);
        } else if (TextUtils.isEmpty(editTwo.getText().toString())) {
            editTwo.setText(strWord);
        } else if (TextUtils.isEmpty(editThree.getText().toString())) {
            editThree.setText(strWord);
        } else if (TextUtils.isEmpty(editFour.getText().toString())) {
            editFour.setText(strWord);
        } else if (TextUtils.isEmpty(editFive.getText().toString())) {
            editFive.setText(strWord);
        } else if (TextUtils.isEmpty(editSix.getText().toString())) {
            editSix.setText(strWord);
        } else if (TextUtils.isEmpty(editSeven.getText().toString())) {
            editSeven.setText(strWord);
        } else if (TextUtils.isEmpty(editEight.getText().toString())) {
            editEight.setText(strWord);
        } else if (TextUtils.isEmpty(editNine.getText().toString())) {
            editNine.setText(strWord);
        } else if (TextUtils.isEmpty(editTen.getText().toString())) {
            editTen.setText(strWord);
        } else if (TextUtils.isEmpty(editEleven.getText().toString())) {
            editEleven.setText(strWord);
        } else if (TextUtils.isEmpty(editTwelve.getText().toString())) {
            editTwelve.setText(strWord);
            //if ok
            helpWordOk();

        }

    }

    private void helpWordOk() {
        myDialog.show();
        String strone = editOne.getText().toString();
        String strtwo = editTwo.getText().toString();
        String strthree = editThree.getText().toString();
        String strfour = editFour.getText().toString();
        String strfive = editFive.getText().toString();
        String strsix = editSix.getText().toString();
        String strseven = editSeven.getText().toString();
        String streight = editEight.getText().toString();
        String strnine = editNine.getText().toString();
        String strten = editTen.getText().toString();
        String streleven = editEleven.getText().toString();
        String strtwelve = editTwelve.getText().toString();

        String strNewseed = strone + " " + strtwo + " " + strthree + " " + strfour + " " + strfive + " " + strsix + " " + strseven + " " + streight + " " + strnine + " " + strten + " " + streleven + " " + strtwelve;

        try {
            Daemon.commands.callAttr("check_seed", strNewseed, strPass1);
            Intent intent = new Intent(this, AppWalletCreateFinishActivity.class);
            intent.putExtra("strName", strName);
            myDialog.dismiss();
            startActivity(intent);
        } catch (Exception e) {
            myDialog.dismiss();
            if (Objects.requireNonNull(e.getMessage()).contains("pair seed failed")) {
                mToast(getString(R.string.helpword_wrong));
                changeNull();
            } else {
                mToast(getString(R.string.improt_wrong));
            }
            e.printStackTrace();
        }

    }

    private void changeNull() {
        editOne.setText("");
        editTwo.setText("");
        editThree.setText("");
        editFour.setText("");
        editFive.setText("");
        editSix.setText("");
        editSeven.setText("");
        editEight.setText("");
        editNine.setText("");
        editTen.setText("");
        editEleven.setText("");
        editTwelve.setText("");
    }

    @Override
    public void initData() {

    }

    @SingleClick
    @OnClick({R.id.img_back})
    public void onViewClicked(View view) {
        if (view.getId() == R.id.img_back) {
            finish();
        }
    }
}
