package com.madarasz.netrunnerstats.database.DOs.admin;

/**
 * Created by madarasz on 12/10/15.
 * For decks with verification problem.
 */
public class VerificationProblem {
    private String title;
    private String url;
    private String problemType;
    private String reason;

    public VerificationProblem(String title, String url, String problemType, String reason) {
        this.title = title;
        this.url = url;
        this.problemType = problemType;
        this.reason = reason;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getProblemType() {
        return problemType;
    }

    public String getReason() {
        return reason;
    }
}
