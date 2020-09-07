package org.haobtc.keymanager.fragment.mainwheel;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import org.haobtc.keymanager.R;
import org.haobtc.keymanager.activities.CreateWalletActivity;
import org.haobtc.keymanager.aop.SingleClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddViewFragment extends WheelViewpagerFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_view, container, false);
        CardView cardAdd = view.findViewById(R.id.wallet_card_add);
        cardAdd.setOnClickListener(new View.OnClickListener() {
            @SingleClick
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateWalletActivity.class);
                getActivity().startActivity(intent);
            }
        });

        return view;
    }
    @Override
    public void setValue(String msgVote) {
    }
}
