package com.angaria.languagematch.util;

import com.angaria.languagematch.entities.SRTObject;
import com.angaria.languagematch.entities.SubTitle;
import com.angaria.languagematch.entities.SubTitleMatch;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Alex on 10/04/2017.
 */
public class WorkflowUtil {

    private static final Logger logger = LogManager.getLogger(WorkflowUtil.class.getName());

    public static Set<SubTitleMatch> extractMatches(Set<SRTObject> srtObjects) {

        SRTObject srtReferenceObject =  srtObjects.stream()
                .filter(srt -> srt.getLanguage().equals("en"))
                .findFirst()
                .get();

        Set<SRTObject> otherSRTs =  srtObjects.stream()
                .filter(srt -> !srt.getLanguage().equals("en"))
                .collect(Collectors.toSet());

        Set<SubTitleMatch> matches = new LinkedHashSet<>();

        for(SRTObject targetSRTObject : otherSRTs){

            for(SubTitle subTitleReference : srtReferenceObject.getSubTitles()){
                SubTitle match = lookupForMatchingSubTitleFrame(targetSRTObject, subTitleReference);

                SubTitleMatch subTitleMatch = new SubTitleMatch(subTitleReference);
                subTitleMatch.setTargetContent(match.getContent());
                matches.add(subTitleMatch);
            }

        }

        logger.log(Level.INFO, matches);

        return matches;
    }

    public static SubTitle lookupForMatchingSubTitleFrame(SRTObject targetSRTObject, SubTitle subTitleReference) {

        SubTitle previousSubTitleTarget = null;

        for(SubTitle subTitleTarget : targetSRTObject.getSubTitles()){
            if(subTitleTarget.getStartDate().after(subTitleReference.getStartDate())){
                return previousSubTitleTarget;
            }
            previousSubTitleTarget = subTitleTarget;
        }

        return previousSubTitleTarget;
    }

}
