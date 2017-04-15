package com.angaria.languagematch.services;

import com.angaria.languagematch.entities.SRTObject;
import com.angaria.languagematch.entities.SubTitle;
import com.angaria.languagematch.entities.SubTitleMatch;
import com.google.common.base.Preconditions;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class WorkflowService {

    private static final Logger logger = LogManager.getLogger(WorkflowService.class.getName());
    private static final String SRT_FILES_PATH = "src/main/resources/";

    @Autowired
    private FileSystemService fileSystemService;

    //COV
    public Collection<File> getSRTFilesFromFileSystem() throws Exception {
        logger.log(Level.INFO, "Lookup input directory...");

        Collection<File> srtFiles = fileSystemService.listSRTFiles(SRT_FILES_PATH);
        checkNonEmpty(srtFiles);
        return srtFiles;
    }

    private void checkNonEmpty(Collection<File> files) throws Exception {
        if(files.isEmpty()){
            throw new Exception("No input file found!");
        }
    }

    //COV
    public Set<SRTObject> buildSRTObjects(Collection<File> srtFiles){
        logger.log(Level.INFO, "Creating subtitle Objects...");

        return srtFiles.stream()
                            .map(f -> {
                                SRTObject srtObject = new SRTObject(f);
                                srtObject.generateSubTitles();
                                return srtObject;
                            })
                            .collect(Collectors.toSet());
    }

    //COV
    public Set<SubTitleMatch> findMatchingSubTitles(SRTObject refSRT, SRTObject targetSRT) {
        Preconditions.checkArgument(refSRT != null, "Reference SRT file missing!");
        Preconditions.checkArgument(targetSRT != null, "Second SRT file missing!");

        Set<SubTitleMatch> matches = refSRT.getSubTitles()
                                                .stream()
                                                .map(subTitleRef ->  {
                                                    SubTitle match = targetSRT.lookupForMatchingSubTitleFrame(subTitleRef);
                                                    return new SubTitleMatch(subTitleRef, match.getContent());
                                                })
                                                .collect(Collectors.toSet());

        logger.log(Level.INFO, matches);
        return matches;
    }

    //COV
    public SRTObject getTargetLanguageSRT(Set<SRTObject> srtObjects) {
        return srtObjects.stream()
                .filter(srt -> !srt.getLanguage().equals("en"))
                .findFirst().get();
    }

    //COV
    public SRTObject getReferenceSRT(Set<SRTObject> srtObjects){
        return srtObjects.stream()
                .filter(srt -> srt.getLanguage().equals("en"))
                .findFirst().get();
    }

    public void setFileSystemService(FileSystemService fileSystemService) {
        this.fileSystemService = fileSystemService;
    }
}
