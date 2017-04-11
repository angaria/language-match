package com.angaria.languagematch.entities;

import java.util.Date;

public class SubTitleMatch {

    public SubTitleMatch(SubTitle subTitleReference) {
        this.startDate = subTitleReference.getStartDate();
        this.endDate = subTitleReference.getEndDate();
        this.referenceContent = subTitleReference.getContent();
    }

    private Date startDate;
    private Date endDate;

    private String referenceContent;
    private String targetContent;

    private String hashCode;

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getReferenceContent() {
        return referenceContent;
    }

    public void setReferenceContent(String referenceContent) {
        this.referenceContent = referenceContent;
    }

    public String getTargetContent() {
        return targetContent;
    }

    public void setTargetContent(String targetContent) {
        this.targetContent = targetContent;
    }

    public String getHashCode() {
        return hashCode;
    }

    public void setHashCode(String hashCode) {
        this.hashCode = hashCode;
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
