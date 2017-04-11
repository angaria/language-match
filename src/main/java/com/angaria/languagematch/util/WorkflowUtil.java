package com.angaria.languagematch.util;

import com.angaria.languagematch.entities.SRTObject;
import com.angaria.languagematch.entities.SubTitle;
import com.angaria.languagematch.entities.SubTitleMatch;
import com.angaria.languagematch.services.FileSystemService;
import org.apache.commons.io.FileSystemUtils;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Alex on 10/04/2017.
 */
public class WorkflowUtil {

    private static final Logger logger = LogManager.getLogger(WorkflowUtil.class.getName());

    private FileSystemService fileSystemService = new FileSystemService();

    public Set<File> lookupFileSystemForSRTFiles() {

        logger.log(Level.INFO, "Lookup input directory...");
        Collection<File> files = fileSystemService.listFiles("src/main/resources/");

        if(files.isEmpty()){
            throw new Error("No input file found!");
        }

        Set<File> srtFiles = new LinkedHashSet<>();

        for( File file : files ) {
            if(file.getName().endsWith(".srt")){
                logger.log(Level.DEBUG, "Found SRT file >>>> " + file.getName());
                srtFiles.add(file);
            }
            else{
                logger.log(Level.DEBUG, "Found >>>> " + file.getName());
            }
        }

        if(srtFiles.size() <= 1){
            throw new Error("At least 2 SRT files needed for analysis!");
        }

        return srtFiles;
    }

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

    public void setFileSystemService(FileSystemService fileSystemService) {
        this.fileSystemService = fileSystemService;
    }
}
