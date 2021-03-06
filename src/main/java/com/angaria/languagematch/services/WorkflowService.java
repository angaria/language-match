package com.angaria.languagematch.services;

import com.angaria.languagematch.wrappers.SRTObjects;
import com.angaria.languagematch.entities.SRTObject;
import com.angaria.languagematch.entities.SubTitle;
import com.angaria.languagematch.entities.SubTitleMatch;
import com.angaria.languagematch.wrappers.SubTitleMatches;
import com.google.common.base.Preconditions;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Transactional
public class WorkflowService {

    private static final Logger logger = LogManager.getLogger(WorkflowService.class.getName());
    public static final String SRT_FILES_PATH = "src/main/resources/input";
    public static final String SRT_FILES_PATH_DEST = "src/main/resources/processed";

    @Autowired
    private FileSystemService fileSystemService;

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

    public SRTObjects buildSRTObjects(Collection<File> srtFiles){
        logger.log(Level.INFO, "Creating subtitle objects");

        Set<SRTObject> srtObjects = srtFiles.stream()
                                            .map(SRTObject::new)
                                            .collect(Collectors.toSet());

        return new SRTObjects(srtObjects);
    }

    public SubTitleMatches findMatchingSubTitles(SRTObject refSRT, SRTObject targetSRT) {
        Preconditions.checkArgument(refSRT != null, "Reference SRT file missing!");
        Preconditions.checkArgument(targetSRT != null, "Second SRT file missing!");
        Set<SubTitleMatch> matches = refSRT.getSubTitles()
                                            .stream()
                                            .filter(s -> s.hasOnlyOnePersonTalking())
                                            .filter(s -> s.hasNotForbiddenCharacters())
                                            .map(subTitleRef ->  {
                                                SubTitle match = targetSRT.lookupForMatchingSubTitleFrame(subTitleRef);
                                                return match != null ?
                                                        new SubTitleMatch(subTitleRef, match) : null;
                                            })
                                            .filter(out -> out != null)
                                            .collect(Collectors.toSet());

        return new SubTitleMatches(matches, refSRT.getFileNameShort());
    }

    public void setFileSystemService(FileSystemService fileSystemService) {
        this.fileSystemService = fileSystemService;
    }

    public Set<SubTitleMatches> findMatchingSubTitlesWithinGroups(Map<String, SRTObjects> groupsByTitle) {

        Set<SubTitleMatches> result = new LinkedHashSet<>();
        groupsByTitle.values()
                .stream()
                .forEach( g -> {
                    logger.log(Level.INFO, "Look for matching subtitles in "+g.getReferenceSRTObject().getFileNameShort());

                    try{
                        SubTitleMatches m = findMatchingSubTitles(g.getReferenceSRTObject(),
                                g.getSecondarySRTObject());
                        result.add(m);
                    }
                    catch (RuntimeException e){}

                });

        return result;
    }

    public void tryMoveProcessedFiles(SRTObjects srtObjects) {
        try {
            logger.log(Level.INFO, "Try moving processed files");

            moveProcessedFiles(srtObjects);
        } catch (IOException e1) {
            logger.log(Level.WARN, "Moving processed files has failed!");
            logger.log(Level.WARN, e1.getMessage());
        }
    }

    private void moveProcessedFiles(SRTObjects srtObjects) throws IOException {
        for(SRTObject srtObject : srtObjects.getSrtObjects()){
            logger.log(Level.INFO, "Moving " + srtObject.getFileName());

            FileUtils.copyFile(
                    new File(WorkflowService.SRT_FILES_PATH + "/" + srtObject.getFileName()),
                    new File(WorkflowService.SRT_FILES_PATH_DEST + "/" + srtObject.getFileName()));

            FileUtils.forceDelete(new File(WorkflowService.SRT_FILES_PATH + "/" + srtObject.getFileName()));
        }
    }
}
