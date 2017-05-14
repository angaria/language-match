package com.angaria.languagematch.components;

import com.angaria.languagematch.services.SRTObjectsService;
import com.angaria.languagematch.services.SubTitleMatchesService;
import com.angaria.languagematch.services.WorkflowService;
import com.angaria.languagematch.wrappers.SRTObjects;
import com.angaria.languagematch.wrappers.SubTitleMatches;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

@Component
public class Workflow {

    private static final Logger logger = LogManager.getLogger(Workflow.class.getName());

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private SRTObjectsService srtObjectsService;

    @Autowired
    private SubTitleMatchesService subTitleMatchesService;

    public void start() throws Exception {
        SRTObjects srtObjects = persist(buildSRTObjects(getSRTFiles()));

        persist(findMatchesAmongRelatedFiles(srtObjects.getGroupsByTitle()));

        moveProcessedFiles(srtObjects);
    }

    private SRTObjects buildSRTObjects(Collection<File> files) {
        return workflowService.buildSRTObjects(files);
    }

    private Collection<File> getSRTFiles() throws Exception {
        return workflowService.getSRTFilesFromFileSystem();
    }

    private SRTObjects persist(SRTObjects srtObjects){
        return srtObjectsService.persist(srtObjects);
    }

    private Set<SubTitleMatches> findMatchesAmongRelatedFiles(Map<String, SRTObjects> groupsByTitle){
        return workflowService.findMatchingSubTitlesWithinGroups(groupsByTitle);
    }

    private void persist(Set<SubTitleMatches> matchesByGroups){
        subTitleMatchesService.persist(matchesByGroups);
    }

    private void moveProcessedFiles(SRTObjects srtObjects){
        workflowService.tryMoveProcessedFiles(srtObjects);
    }
}
