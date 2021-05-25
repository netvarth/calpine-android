package com.jaldeeinc.jaldee.enums;

public enum  JCashRewrd {
    APP_SIGNUP("App Signup");
    private String rewardCause;
    private JCashRewrd (final String rewardCause) {
        this.rewardCause = rewardCause;
    }

    public String getRewardCause() {
        return rewardCause;
    }
}
