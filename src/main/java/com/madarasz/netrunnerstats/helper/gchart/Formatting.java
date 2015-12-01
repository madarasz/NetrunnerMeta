package com.madarasz.netrunnerstats.helper.gchart;

/**
 * Created by madarasz on 11/10/15.
 * Object representation of formatting field.
 */
public class Formatting {
    private boolean html;

    public Formatting(boolean html) {
        this.html = html;
    }

    public boolean isHtml() {
        return html;
    }
}
