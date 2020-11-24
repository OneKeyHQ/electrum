package org.haobtc.onekey.ui.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.haobtc.onekey.R;

/**
 * @author liyan
 */
public class LoadingTextView extends LinearLayout {

    private TextView mText;
    private ProgressBar mBar;
    private Context mContext;

    public LoadingTextView(Context context) {
        super(context);
        this.mContext = context;
    }

    public LoadingTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        LayoutInflater.from(context).inflate(R.layout.item_loading_text,this);
        mText = findViewById(R.id.item_text);
        mBar = findViewById(R.id.item_progress_bar);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoadingTextView);
        String text = typedArray.getText(R.styleable.LoadingTextView_text).toString();
        if(!TextUtils.isEmpty(text)){
            mText.setText(text);
        }
    }



    public void completeLoading(){
        if(mBar != null && mContext != null){
            mBar.setIndeterminateDrawable(null);
            mBar.setBackground(mContext.getDrawable(R.drawable.checked));
        }
    }

}
