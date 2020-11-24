
package org.haobtc.onekey.bean;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author liyan
 */
public class HardwareFeatures {
    /**
     * vendor : trezor.io
     * bootloader_mode:
     * major_version : 1
     * minor_version : 9
     * patch_version : 2
     * device_id : 0B00000059B244CA5875D78F
     * pin_protection : false
     * passphrase_protection : true
     * language : en-US
     * label : BiXinKEY
     * initialized : true
     * revision : 641932dd27a887c1042501566494758e99bd8a77
     * bootloader_hash : 003590dab9bf34e04180f2fa1b1c69826c0dd81eb5e2132c40347df839d60a51
     * imported : false
     * pin_cached : false
     * needs_backup : false
     * flags : 4294967295
     * model : 1
     * unfinished_backup : false
     * no_backup : false
     * capabilities : ["Bitcoin","Bitcoin_like","Crypto","Ethereum","Lisk","NEM","Stellar","U2F"]
     * wipe_code_protection : false
     * session_id : 9e77cabf6d9c8d605a7dcf2bbda3693ce11fa59284bf639dfbb3d056d1abbd0b
     * ble_name : BixinKEY1114131433
     * ble_ver : 1.0.5
     * ble_enable: true
     * se_enable: false
     * se_version: ""
     * backup_only: false
     * backup_message: ""
     */

    @SerializedName("vendor")
    private String vendor;
    @SerializedName("major_version")
    private int majorVersion;
    @SerializedName("minor_version")
    private int minorVersion;
    @SerializedName("patch_version")
    private int patchVersion;
    @SerializedName("device_id")
    private String deviceId;
    @SerializedName("pin_protection")
    private boolean pinProtection;
    @SerializedName("passphrase_protection")
    private boolean passphraseProtection;
    @SerializedName("language")
    private String language;
    @SerializedName("label")
    private String label;
    @SerializedName("initialized")
    private boolean initialized;
    @SerializedName("revision")
    private String revision;
    @SerializedName("bootloader_hash")
    private String bootloaderHash;
    @SerializedName("imported")
    private boolean imported;
    @SerializedName("pin_cached")
    private boolean pinCached;
    @SerializedName("needs_backup")
    private boolean needsBackup;
    @SerializedName("flags")
    private long flags;
    @SerializedName("model")
    private String model;
    @SerializedName("unfinished_backup")
    private boolean unfinishedBackup;
    @SerializedName("no_backup")
    private boolean noBackup;
    @SerializedName("wipe_code_protection")
    private boolean wipeCodeProtection;
    @SerializedName("session_id")
    private String sessionId;
    @SerializedName("ble_name")
    private String bleName;
    @SerializedName("ble_ver")
    private String bleVer;
    @SerializedName("capabilities")
    private List<String> capabilities;
    @SerializedName("bootloader_mode")
    private boolean bootloaderMode;
    @SerializedName("ble_enable")
    private boolean bleEnable;
    @SerializedName("se_enable")
    private boolean seEnable;
    @SerializedName("se_version")
    private String seVersion;
    @SerializedName("backup_only")
    private boolean backupOnly;
    @SerializedName("backup_message")
    private String backupMessage;

    public static HardwareFeatures objectFromData(String str) {

        return new Gson().fromJson(str, HardwareFeatures.class);
    }


    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public void setMajorVersion(int majorVersion) {
        this.majorVersion = majorVersion;
    }

    public int getMinorVersion() {
        return minorVersion;
    }

    public void setMinorVersion(int minorVersion) {
        this.minorVersion = minorVersion;
    }

    public int getPatchVersion() {
        return patchVersion;
    }

    public void setPatchVersion(int patchVersion) {
        this.patchVersion = patchVersion;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public boolean isPinProtection() {
        return pinProtection;
    }

    public void setPinProtection(boolean pinProtection) {
        this.pinProtection = pinProtection;
    }

    public boolean isPassphraseProtection() {
        return passphraseProtection;
    }

    public void setPassphraseProtection(boolean passphraseProtection) {
        this.passphraseProtection = passphraseProtection;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public String getBootloaderHash() {
        return bootloaderHash;
    }

    public void setBootloaderHash(String bootloaderHash) {
        this.bootloaderHash = bootloaderHash;
    }

    public boolean isImported() {
        return imported;
    }

    public void setImported(boolean imported) {
        this.imported = imported;
    }

    public boolean isPinCached() {
        return pinCached;
    }

    public void setPinCached(boolean pinCached) {
        this.pinCached = pinCached;
    }

    public boolean isNeedsBackup() {
        return needsBackup;
    }

    public void setNeedsBackup(boolean needsBackup) {
        this.needsBackup = needsBackup;
    }

    public long getFlags() {
        return flags;
    }

    public void setFlags(long flags) {
        this.flags = flags;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public boolean isUnfinishedBackup() {
        return unfinishedBackup;
    }

    public void setUnfinishedBackup(boolean unfinishedBackup) {
        this.unfinishedBackup = unfinishedBackup;
    }

    public boolean isNoBackup() {
        return noBackup;
    }

    public void setNoBackup(boolean noBackup) {
        this.noBackup = noBackup;
    }

    public boolean isWipeCodeProtection() {
        return wipeCodeProtection;
    }

    public void setWipeCodeProtection(boolean wipeCodeProtection) {
        this.wipeCodeProtection = wipeCodeProtection;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getBleName() {
        return bleName;
    }

    public void setBleName(String bleName) {
        this.bleName = bleName;
    }

    public String getBleVer() {
        return bleVer;
    }

    public void setBleVer(String bleVer) {
        this.bleVer = bleVer;
    }

    public List<String> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(List<String> capabilities) {
        this.capabilities = capabilities;
    }

    public boolean isBootloaderMode() {
        return bootloaderMode;
    }

    public void setBootloader_mode(boolean bootloaderMode) {
        this.bootloaderMode = bootloaderMode;
    }
    public void setBleEnable(boolean bleEnable) {
        this.bleEnable = bleEnable;
    }
    public boolean isSeEnable() {
        return seEnable;
    }
    public boolean isBleEnable() {
        return bleEnable;
    }
    public void setSeEnable(boolean seEnable) {
        this.seEnable = seEnable;
    }
    public void setBackupMessage(String backupMessage) {
        this.backupMessage = backupMessage;
    }
    public String getBackupMessage() {
        return backupMessage;
    }
    public String getSeVersion() {
        return seVersion;
    }

    public boolean isBackupOnly() {
        return backupOnly;
    }

    public void setSeVersion(String seVersion) {
        this.seVersion = seVersion;
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}