package org.haobtc.onekey.ui.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.InputType;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liyan
 */
public class PwdInputView extends AppCompatEditText {

    private Paint mBackPaint, mTextPaint;
    private Context mContext;
    private int mSpzceX, mSpzceY;
    private int mWide;
    private int mBackColor, mTextColor;
    private String mText;
    private int mTextLength;
    private List<RectF> mRectFS;

    public PwdInputView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public PwdInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public PwdInputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    /**
     * 输入监听
     */
    interface OnTextChangeListeven {
        void onTextChange(String pwd);
    }

    private OnTextChangeListeven onTextChangeListeven;

    public void setOnTextChangeListeven(OnTextChangeListeven onTextChangeListeven) {
        this.onTextChangeListeven = onTextChangeListeven;
    }

    public void clearText() {
        setText("");
        setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
    }

    private void init() {
        setTextColor(0X00ffffff);
        setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
        mSpzceX = dp2px(5);
        mSpzceY = dp2px(5);
        mWide = dp2px(50);
        mBackColor = 0xfff2f2f2;
        mTextColor = 0xFF142A3B;
        mBackPaint = new Paint();
        mTextPaint = new Paint();
        mRectFS = new ArrayList<>();

        mText = "";
        mTextLength = 6;

        this.setBackgroundDrawable(null);
        setLongClickable(false);
        setTextIsSelectable(false);
        setCursorVisible(false);

    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        if (mText == null) {
            return;
        }
        if (text.toString().length() <= mTextLength) {
            mText = text.toString();
        } else {
            setText(mText);
            setSelection(getText().toString().length());
            setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
        }
        if (onTextChangeListeven != null) onTextChangeListeven.onTextChange(mText);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        switch (heightMode) {
            case MeasureSpec.EXACTLY:
                heightSize = MeasureSpec.getSize(heightMeasureSpec);
                break;
            case MeasureSpec.AT_MOST:
                heightSize = widthSize / mTextLength;
                break;
        }
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mBackPaint.setStyle(Paint.Style.FILL);
        mBackPaint.setColor(mBackColor);

        mTextPaint.setTextSize(18);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setColor(mTextColor);
        for (int i = 0; i < mTextLength; i++) {
            RectF rect = new RectF(i * mWide + mSpzceX, mSpzceY, i * mWide + mWide - mSpzceX, mWide - mSpzceY);
            canvas.drawRoundRect(rect, 20, 20, mBackPaint);
            mRectFS.add(rect);
        }
        for (int j = 0; j < mText.length(); j++) {
            canvas.drawCircle(mRectFS.get(j).centerX(), mRectFS.get(j).centerY(), dp2px(5), mTextPaint);
        }
    }

    private int dp2px(float dpValue) {
        float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
