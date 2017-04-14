package com.angaria.languagematch.entities;

import java.util.Date;

public class SubTitleMatch {

    public SubTitleMatch(SubTitle subTitleReference, String targetContent) {
        this.startDate = subTitleReference.getStartDate();
        this.endDate = subTitleReference.getEndDate();
        this.referenceContent = subTitleReference.getContent();
        this.targetContent = targetContent;
    }

    private Date startDate;
    private Date endDate;
    private String referenceContent;
    private String targetContent;
    private String hashCode;

    public String getTargetContent() {
        return targetContent;
    }

    @Override
    public String toString() {
        return "SubTitleMatch{" +
                "startDate=" + startDate +
                ", referenceContent='" + referenceContent + '\'' +
                ", targetContent='" + targetContent + '\'' +
                ", hashCode='" + hashCode + '\'' +
                '}';
    }
}
