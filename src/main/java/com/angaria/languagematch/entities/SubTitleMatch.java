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

    @Column(name = "hash_code")
    private String hashCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ref_file")
    private SRTObject srtReferenceObject;

    public SubTitleMatch(SubTitle subTitleReference, String targetContent) {
        this.startDate = subTitleReference.getStartDate();
        this.endDate = subTitleReference.getEndDate();
        this.referenceContent = subTitleReference.getContent();
        this.targetContent = targetContent;
    }

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
