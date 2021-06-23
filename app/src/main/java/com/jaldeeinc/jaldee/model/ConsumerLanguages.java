package com.jaldeeinc.jaldee.model;

public class ConsumerLanguages {
    String language;
    boolean checked;

    public ConsumerLanguages(String language, boolean checked) {
        this.language = language;
        this.checked = checked;
    }

    public String getLanguage() {
        return language;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
