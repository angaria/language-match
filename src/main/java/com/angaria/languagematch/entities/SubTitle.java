package com.angaria.languagematch.entities;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static javax.persistence.GenerationType.AUTO;

@Entity
@Table(name="subtitles")
public class SubTitle implements Comparable<SubTitle>{

    public static final String SRT_DATE_SEPARATOR = " --> ";
    public static final DateFormat COMPLETE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
    public static final String REFERENCE_DAY = "2017-04-09";

    @Id
    @GeneratedValue(strategy = AUTO)
    protected Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start")
    protected Date startDate = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end")
    protected Date endDate = new Date();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "srt_file")
    protected SRTObject srtObject;

    protected String content;
    protected String language;

    public SubTitle(){}

    public SubTitle(Date startDate, Date endDate, String language, SRTObject srtObject, String content){
        this.startDate = startDate;
        this.endDate = endDate;
        this.language = language;
        this.srtObject = srtObject;
        this.content = content;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setSRTObject(SRTObject srtObject) {
        this.srtObject = srtObject;
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

        return id != null ? id.equals(subTitle.id) : subTitle.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
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
                ", srtObjectName='" + srtObject.getFileName() + '\'' +
                ", content='" + content + '\'' +
                "}";
    }
}
