package org.haobtc.onekey.activities.transaction;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.adapter.InputaddrScanAdapter;
import org.haobtc.onekey.adapter.MoreAddressAdapter;
import org.haobtc.onekey.adapter.OutputaddrScanAdapter;
import org.haobtc.onekey.adapter.TranscationPayAddressAdapter;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.bean.CheckAddressAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeatilMoreAddressActivity extends BaseActivity {
    @BindView(R.id.img_backReceict)
    ImageView imgBackReceict;
    @BindView(R.id.recy_moreAddress)
    RecyclerView recyMoreAddress;
    @BindView(R.id.tv_in_tb2)
    TextView tvInTb2;
    private List jsondefGet;
    private List addressList;
    private List payAddress;
    private List jsondefGetScan;
    private List payAddressScan;
    private String plusNum;

    @Override
    public int getLayoutId() {
        return R.layout.activity_deatil_more_address;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        String addressType = getIntent().getStringExtra("addressType");
        jsondefGet = (List) getIntent().getSerializableExtra("jsondef_get");
        jsondefGetScan = (List) getIntent().getSerializableExtra("jsondef_getScan");
        addressList = (List) getIntent().getSerializableExtra("listdetail");
        payAddress = (List) getIntent().getSerializableExtra("payAddress");
        payAddressScan = (List) getIntent().getSerializableExtra("payAddressScan");
        plusNum = getIntent().getStringExtra("plusNum");//receive or send amount
        if ("pay".equals(addressType)) {
            tvInTb2.setText(getString(R.string.from));
        }
    }

    @Override
    public void initData() {
        if (jsondefGet != null) {
            Log.i("jsondefGet", "initData: ---  " + jsondefGet.toString());
            MoreAddressAdapter moreAddressAdapter = new MoreAddressAdapter(jsondefGet,plusNum);
            recyMoreAddress.setAdapter(moreAddressAdapter);

        } else if (jsondefGetScan != null) {
            OutputaddrScanAdapter outputaddrScanAdapter = new OutputaddrScanAdapter(jsondefGetScan);
            recyMoreAddress.setAdapter(outputaddrScanAdapter);

        } else if (addressList != null) {
            CheckAddressAdapter checkAddressAdapter = new CheckAddressAdapter(addressList);
            recyMoreAddress.setAdapter(checkAddressAdapter);

        } else if (payAddress != null) {
            TranscationPayAddressAdapter transcationPayAddressAdapter = new TranscationPayAddressAdapter(payAddress);
            recyMoreAddress.setAdapter(transcationPayAddressAdapter);
        } else if (payAddressScan != null) {
            InputaddrScanAdapter inputaddrScanAdapter = new InputaddrScanAdapter(payAddressScan);
            recyMoreAddress.setAdapter(inputaddrScanAdapter);
        }

    }

    @SingleClick
    @OnClick({R.id.img_backReceict})
    public void onViewClicked(View view) {
        if (view.getId() == R.id.img_backReceict) {
            finish();
        }
    }
}
