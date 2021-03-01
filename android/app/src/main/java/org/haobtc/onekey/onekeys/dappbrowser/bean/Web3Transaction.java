package org.haobtc.onekey.onekeys.dappbrowser.bean;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import java.math.BigInteger;
import org.haobtc.onekey.onekeys.dappbrowser.utils.StyledStringBuilder;
import org.haobtc.onekey.onekeys.walletconnect.entity.WCEthereumTransaction;
import org.haobtc.onekey.utils.HexUtils;

public class Web3Transaction implements Parcelable {

    public final Address recipient;
    public final Address contract;
    public final BigInteger value;
    public final BigInteger gasPrice;
    public final BigInteger gasLimit;
    public final long nonce;
    public final String payload;
    public final long leafPosition;

    public Web3Transaction(
            Address recipient,
            Address contract,
            BigInteger value,
            BigInteger gasPrice,
            BigInteger gasLimit,
            long nonce,
            String payload) {
        this(recipient, contract, value, gasPrice, gasLimit, nonce, payload, 0);
    }

    public Web3Transaction(
            Address recipient,
            Address contract,
            BigInteger value,
            BigInteger gasPrice,
            BigInteger gasLimit,
            long nonce,
            String payload,
            long leafPosition) {
        this.recipient = recipient;
        this.contract = contract;
        this.value = value;
        this.gasPrice = gasPrice;
        this.gasLimit = gasLimit;
        this.nonce = nonce;
        this.payload = payload;
        this.leafPosition = leafPosition;
    }

    /**
     * Initialise from WalletConnect Transaction
     *
     * @param wcTx
     * @param callbackId
     */
    public Web3Transaction(WCEthereumTransaction wcTx, long callbackId) {
        String gasPrice = wcTx.getGasPrice() != null ? wcTx.getGasPrice() : "0";
        String gasLimit = wcTx.getGasLimit() != null ? wcTx.getGasLimit() : "0";
        String nonce = wcTx.getNonce() != null ? wcTx.getNonce() : "";

        this.recipient =
                TextUtils.isEmpty(wcTx.getTo()) ? Address.EMPTY : new Address(wcTx.getTo());
        this.contract = null;
        this.value =
                wcTx.getValue() == null
                        ? BigInteger.ZERO
                        : HexUtils.hexToBigInteger(wcTx.getValue(), BigInteger.ZERO);
        this.gasPrice = HexUtils.hexToBigInteger(gasPrice, BigInteger.ZERO);
        this.gasLimit = HexUtils.hexToBigInteger(gasLimit, BigInteger.ZERO);
        this.nonce = HexUtils.hexToLong(nonce, -1);
        this.payload = wcTx.getData();
        this.leafPosition = callbackId;
    }

    Web3Transaction(Parcel in) {
        recipient = in.readParcelable(Address.class.getClassLoader());
        contract = in.readParcelable(Address.class.getClassLoader());
        value = new BigInteger(in.readString());
        gasPrice = new BigInteger(in.readString());
        gasLimit = new BigInteger(in.readString());
        nonce = in.readLong();
        payload = in.readString();
        leafPosition = in.readLong();
    }

    public static final Creator<Web3Transaction> CREATOR =
            new Creator<Web3Transaction>() {
                @Override
                public Web3Transaction createFromParcel(Parcel in) {
                    return new Web3Transaction(in);
                }

                @Override
                public Web3Transaction[] newArray(int size) {
                    return new Web3Transaction[size];
                }
            };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(recipient, flags);
        dest.writeParcelable(contract, flags);
        dest.writeString((value == null ? BigInteger.ZERO : value).toString());
        dest.writeString((gasPrice == null ? BigInteger.ZERO : gasPrice).toString());
        dest.writeString((gasLimit == null ? BigInteger.ZERO : gasLimit).toString());
        // dest.writeLong(gasLimit);
        dest.writeLong(nonce);
        dest.writeString(payload);
        dest.writeLong(leafPosition);
    }

    public boolean isConstructor() {
        return (recipient.equals(Address.EMPTY) && payload != null);
    }

    /**
     * Can be used anywhere to generate an 'instant' human readable transaction dump
     *
     * @param ctx
     * @param chainId
     * @return
     */
    public CharSequence getFormattedTransaction(Context ctx, int chainId, String symbol) {
        StyledStringBuilder sb = new StyledStringBuilder();
        //        sb.startStyleGroup().append(ctx.getString(R.string.to)).append(":\n ");
        //        sb.setStyle(new StyleSpan(Typeface.BOLD));
        //        sb.append(recipient.toString());
        //
        //
        // sb.startStyleGroup().append("\n").append(ctx.getString(R.string.value)).append(":\n ");
        //        sb.setStyle(new StyleSpan(Typeface.BOLD));
        //        sb.append(BalanceUtils.getScaledValueWithLimit(new BigDecimal(value), 18));
        //        sb.append(" ").append(symbol);
        //
        //
        // sb.startStyleGroup().append("\n").append(ctx.getString(R.string.label_gas_price)).append(":\n ");
        //        sb.setStyle(new StyleSpan(Typeface.BOLD));
        //        sb.append(BalanceUtils.weiToGwei(gasPrice));
        //
        //
        // sb.startStyleGroup().append("\n").append(ctx.getString(R.string.label_gas_limit)).append(":\n ");
        //        sb.setStyle(new StyleSpan(Typeface.BOLD));
        //        sb.append(gasLimit.toString());
        //
        //
        // sb.startStyleGroup().append("\n").append(ctx.getString(R.string.label_nonce)).append(":\n
        // ");
        //        sb.setStyle(new StyleSpan(Typeface.BOLD));
        //        sb.append(String.valueOf(nonce));
        //
        //
        // sb.startStyleGroup().append("\n").append(ctx.getString(R.string.payload)).append(":\n ");
        //        sb.setStyle(new StyleSpan(Typeface.BOLD));
        //        sb.append(payload);
        //
        //
        // sb.startStyleGroup().append("\n").append(ctx.getString(R.string.subtitle_network)).append(":\n ");
        //        sb.setStyle(new StyleSpan(Typeface.BOLD));
        //        sb.append(MagicLinkInfo.getNetworkNameById(chainId));

        sb.applyStyles();

        return sb;
    }

    @Override
    public String toString() {
        return "Web3Transaction{"
                + "recipient="
                + recipient
                + ", contract="
                + contract
                + ", value="
                + value
                + ", gasPrice="
                + gasPrice
                + ", gasLimit="
                + gasLimit
                + ", nonce="
                + nonce
                + ", payload='"
                + payload
                + '\''
                + ", leafPosition="
                + leafPosition
                + '}';
    }
}
