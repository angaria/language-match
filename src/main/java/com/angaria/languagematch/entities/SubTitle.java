package com.angaria.languagematch.entities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SubTitle implements Comparable<SubTitle>{

    public static final String SRT_DATE_SEPARATOR = " --> ";
    public static final DateFormat COMPLETE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
    public static final String REFERENCE_DAY = "2017-04-09";

    protected Date startDate = new Date();
    protected Date endDate = new Date();
    protected String language;
    protected String fileName;
    protected String content;

    public SubTitle(){}

    public SubTitle(Date startDate, Date endDate, String language, String fileName, String content){
        this.startDate = startDate;
        this.endDate = endDate;
        this.language = language;
        this.fileName = fileName;
        this.content = content;
    }

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

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SubTitle subTitle = (SubTitle) o;

        if (!startDate.equals(subTitle.startDate)) return false;
        if (!endDate.equals(subTitle.endDate)) return false;
        if (!language.equals(subTitle.language)) return false;
        if (!fileName.equals(subTitle.fileName)) return false;
        return content != null ? content.equals(subTitle.content) : subTitle.content == null;
    }

    @Override
    public int hashCode() {
        int result = startDate.hashCode();
        result = 31 * result + language.hashCode();
        result = 31 * result + fileName.hashCode();
        return result;
    }

    @Override
    public int compareTo(SubTitle o) {

        if(this.getStartDate().before(o.getStartDate())){
            return -1;
        }
        else if(this.getStartDate().after(o.getStartDate())){
            return 1;
        }

        return 0;
    }

    public void setStartDateFromLine(String line) throws ParseException {
        if(!line.contains(SRT_DATE_SEPARATOR)){
            throw new IllegalArgumentException("parsing a line which is not a timing line!");
        }

        startDate = COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " " + line.substring(0, line.indexOf(SRT_DATE_SEPARATOR)));
    }

    public void setEndDateFromLine(String line) throws ParseException {
        if(!line.contains(SRT_DATE_SEPARATOR)){
            throw new IllegalArgumentException("parsing a line which is not a timing line!");
        }

        endDate = COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " " + line.substring(line.indexOf(SRT_DATE_SEPARATOR) + SRT_DATE_SEPARATOR.length(), line.length()));
    }

    @Override
    public String toString() {
        return "SubTitle{" +
                "startDate=" + startDate +
                ", endDate=" + endDate +
                ", language='" + language + '\'' +
                ", fileName='" + fileName + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
