package org.haobtc.keymanager.bean;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UpdateInfo {


    /**
     * APK : {"versionCode":4000000,"versionName":"4.0.0","url":"data/mainnet/1/bixin-4.0.0-0-RegTest-debug.apk","size":"47M","changelog_cn":"新特性","changelog_en":"New feature update"}
     * stm32 : {"required":false,"version":[1,9,0,1],"bootloader_version":[1,8,0],"min_bridge_version":[2,0,25],"min_firmware_version":[1,6,2],"min_bootloader_version":[1,5,0],"url":"data/mainnet/1/bixin-1.9.0.1.bin","url_bitcoinonly":"","fingerprint":"e912cd6815df8ae9d374da841036fab737609dad4d3252ffb03c46241b413db9","fingerprint_bitcoinonly":"","changelog_cn":"新特性","changelog_en":"New feature update", "need_upload": false}
     * nrf : {"version":"1.0.1","url":"data/mainnet/1/bixin-1.0.1.zip","changelog_cn":"新特性","changelog_en":"New feature update", "need_upload": false}
     */

    @SerializedName("APK")
    private APKBean APK;
    @SerializedName("stm32")
    private Stm32Bean stm32;
    @SerializedName("nrf")
    private NrfBean nrf;

    public static UpdateInfo objectFromData(String str) {

        return new Gson().fromJson(str, UpdateInfo.class);
    }

    public APKBean getAPK() {
        return APK;
    }

    public void setAPK(APKBean APK) {
        this.APK = APK;
    }

    public Stm32Bean getStm32() {
        return stm32;
    }

    public void setStm32(Stm32Bean stm32) {
        this.stm32 = stm32;
    }

    public NrfBean getNrf() {
        return nrf;
    }

    public void setNrf(NrfBean nrf) {
        this.nrf = nrf;
    }

    public static class APKBean {
        /**
         * versionCode : 4000000
         * versionName : 4.0.0
         * url : data/mainnet/1/bixin-4.0.0-0-RegTest-debug.apk
         * size : 47M
         * changelog_cn : 新特性
         * changelog_en : New feature update
         * "need_upload": false
         */

        @SerializedName("versionCode")
        private int versionCode;
        @SerializedName("versionName")
        private String versionName;
        @SerializedName("url")
        private String url;
        @SerializedName("size")
        private String size;
        @SerializedName("changelog_cn")
        private String changelogCn;
        @SerializedName("changelog_en")
        private String changelogEn;

        public static APKBean objectFromData(String str) {

            return new Gson().fromJson(str, APKBean.class);
        }

        public int getVersionCode() {
            return versionCode;
        }

        public void setVersionCode(int versionCode) {
            this.versionCode = versionCode;
        }

        public String getVersionName() {
            return versionName;
        }

        public void setVersionName(String versionName) {
            this.versionName = versionName;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public String getChangelogCn() {
            return changelogCn;
        }

        public void setChangelogCn(String changelogCn) {
            this.changelogCn = changelogCn;
        }

        public String getChangelogEn() {
            return changelogEn;
        }

        public void setChangelogEn(String changelogEn) {
            this.changelogEn = changelogEn;
        }
    }

    public static class Stm32Bean {
        /**
         * required : false
         * version : [1,9,0,1]
         * bootloader_version : [1,8,0]
         * min_bridge_version : [2,0,25]
         * min_firmware_version : [1,6,2]
         * min_bootloader_version : [1,5,0]
         * url : data/mainnet/1/bixin-1.9.0.1.bin
         * url_bitcoinonly :
         * fingerprint : e912cd6815df8ae9d374da841036fab737609dad4d3252ffb03c46241b413db9
         * fingerprint_bitcoinonly :
         * changelog_cn : 新特性
         * changelog_en : New feature update
         * "need_upload": false
         */

        @SerializedName("required")
        private boolean required;
        @SerializedName("url")
        private String url;
        @SerializedName("url_bitcoinonly")
        private String urlBitcoinonly;
        @SerializedName("fingerprint")
        private String fingerprint;
        @SerializedName("fingerprint_bitcoinonly")
        private String fingerprintBitcoinonly;
        @SerializedName("changelog_cn")
        private String changelogCn;
        @SerializedName("changelog_en")
        private String changelogEn;
        @SerializedName("version")
        private List<Integer> version;
        @SerializedName("bootloader_version")
        private List<Integer> bootloaderVersion;
        @SerializedName("min_bridge_version")
        private List<Integer> minBridgeVersion;
        @SerializedName("min_firmware_version")
        private List<Integer> minFirmwareVersion;
        @SerializedName("min_bootloader_version")
        private List<Integer> minBootloaderVersion;
        @SerializedName("need_upload")
        private boolean needUpload;

        public static Stm32Bean objectFromData(String str) {

            return new Gson().fromJson(str, Stm32Bean.class);
        }

        public boolean isRequired() {
            return required;
        }

        public void setRequired(boolean required) {
            this.required = required;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUrlBitcoinonly() {
            return urlBitcoinonly;
        }

        public void setUrlBitcoinonly(String urlBitcoinonly) {
            this.urlBitcoinonly = urlBitcoinonly;
        }

        public String getFingerprint() {
            return fingerprint;
        }

        public void setFingerprint(String fingerprint) {
            this.fingerprint = fingerprint;
        }

        public String getFingerprintBitcoinonly() {
            return fingerprintBitcoinonly;
        }

        public void setFingerprintBitcoinonly(String fingerprintBitcoinonly) {
            this.fingerprintBitcoinonly = fingerprintBitcoinonly;
        }

        public String getChangelogCn() {
            return changelogCn;
        }

        public void setChangelogCn(String changelogCn) {
            this.changelogCn = changelogCn;
        }

        public String getChangelogEn() {
            return changelogEn;
        }

        public void setChangelogEn(String changelogEn) {
            this.changelogEn = changelogEn;
        }

        public List<Integer> getVersion() {
            return version;
        }

        public void setVersion(List<Integer> version) {
            this.version = version;
        }

        public List<Integer> getBootloaderVersion() {
            return bootloaderVersion;
        }

        public void setBootloaderVersion(List<Integer> bootloaderVersion) {
            this.bootloaderVersion = bootloaderVersion;
        }

        public List<Integer> getMinBridgeVersion() {
            return minBridgeVersion;
        }

        public void setMinBridgeVersion(List<Integer> minBridgeVersion) {
            this.minBridgeVersion = minBridgeVersion;
        }

        public List<Integer> getMinFirmwareVersion() {
            return minFirmwareVersion;
        }

        public void setMinFirmwareVersion(List<Integer> minFirmwareVersion) {
            this.minFirmwareVersion = minFirmwareVersion;
        }

        public List<Integer> getMinBootloaderVersion() {
            return minBootloaderVersion;
        }

        public void setMinBootloaderVersion(List<Integer> minBootloaderVersion) {
            this.minBootloaderVersion = minBootloaderVersion;
        }

        public boolean isNeedUpload() {
            return needUpload;
        }

        public void setNeedUpload(boolean needUpload) {
            this.needUpload = needUpload;
        }
    }

    public static class NrfBean {
        /**
         * version : 1.0.1
         * url : data/mainnet/1/bixin-1.0.1.zip
         * changelog_cn : 新特性
         * changelog_en : New feature update
         * "need_upload": false
         */

        @SerializedName("version")
        private String version;
        @SerializedName("url")
        private String url;
        @SerializedName("changelog_cn")
        private String changelogCn;
        @SerializedName("changelog_en")
        private String changelogEn;
        @SerializedName("need_upload")
        private boolean needUpload;

        public static NrfBean objectFromData(String str) {

            return new Gson().fromJson(str, NrfBean.class);
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getChangelogCn() {
            return changelogCn;
        }

        public void setChangelogCn(String changelogCn) {
            this.changelogCn = changelogCn;
        }

        public String getChangelogEn() {
            return changelogEn;
        }

        public void setChangelogEn(String changelogEn) {
            this.changelogEn = changelogEn;
        }

        public boolean isNeedUpload() {
            return needUpload;
        }

        public void setNeedUpload(boolean needUpload) {
            this.needUpload = needUpload;
        }
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
