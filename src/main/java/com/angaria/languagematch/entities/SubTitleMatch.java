package com.angaria.languagematch.entities;

import javax.persistence.*;
import java.util.Date;
import static javax.persistence.GenerationType.AUTO;

@Entity
@Table(name="matches")
public class SubTitleMatch {

    @Id
    @GeneratedValue(strategy = AUTO)
    protected Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start")
    private Date startDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end")
    private Date endDate;

    @Column(name = "ref_content")
    private String referenceContent;

    @Column(name = "target_content")
    private String targetContent;

    @Column(name = "ref_language")
    private String referenceLanguage;

    @Column(name = "target_language")
    private String targetLanguage;

    @Column(name = "hash_code")
    private String hashCode;

    @Column(name = "ref_file")
    private String referenceFileName;

    public SubTitleMatch(SubTitle subTitleReference, SubTitle subTitleTarget) {
        this.startDate = subTitleReference.getStartDate();
        this.endDate = subTitleReference.getEndDate();
        this.referenceContent = subTitleReference.getContent();
        this.targetContent = subTitleTarget.getContent();
        this.targetLanguage = subTitleTarget.getLanguage();
        this.referenceLanguage = subTitleReference.getLanguage();
        this.referenceFileName = subTitleReference.getFileName();
    }

    public String getTargetContent() {
        return targetContent;
    }

    @Override
    public String toString() {
        return "SubTitleMatch{" +
                "id=" + id +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", referenceContent='" + referenceContent + '\'' +
                ", targetContent='" + targetContent + '\'' +
                ", referenceLanguage='" + referenceLanguage + '\'' +
                ", targetLanguage='" + targetLanguage + '\'' +
                ", hashCode='" + hashCode + '\'' +
                ", referenceFileName='" + referenceFileName + '\'' +
                '}';
    }
}
