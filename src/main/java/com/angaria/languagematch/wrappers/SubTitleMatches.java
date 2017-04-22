package com.angaria.languagematch.wrappers;

import com.angaria.languagematch.entities.SubTitleMatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.LinkedHashSet;
import java.util.Set;

public class SubTitleMatches {

    private static final Logger logger = LogManager.getLogger(SubTitleMatches.class.getName());

    private Set<SubTitleMatch> subTitleMatches = new LinkedHashSet<>();

    public SubTitleMatches(Set<SubTitleMatch> subTitleMatches) {
        this.subTitleMatches = subTitleMatches;
    }

    public Set<SubTitleMatch> getSubTitleMatches() {
        return subTitleMatches;
    }

    public void setSubTitleMatches(Set<SubTitleMatch> subTitleMatches) {
        this.subTitleMatches = subTitleMatches;
    }

    public int size(){
        return subTitleMatches.size();
    }

    public SubTitleMatch next(){
        return subTitleMatches.iterator().next();
    }
}
