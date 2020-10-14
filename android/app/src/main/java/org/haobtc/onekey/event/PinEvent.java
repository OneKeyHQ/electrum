package org.haobtc.onekey.event;

public class PinEvent {
    private String pinCode;
    private String passphrass;
    public PinEvent(String pin, String passphrass) {
        this.pinCode = pin;
        this.passphrass = passphrass;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPassphrass(String passphrass) {
        this.passphrass = passphrass;
    }

    public String getPassphrass() {
        return passphrass;
    }
}
