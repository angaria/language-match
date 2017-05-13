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
        Collection<File> srtFiles = workflowService.getSRTFilesFromFileSystem();
        SRTObjects srtObjects = workflowService.buildSRTObjects(srtFiles);
        srtObjectsService.persist(srtObjects);

        SubTitleMatches matches = workflowService.findMatchingSubTitles(srtObjects.getReferenceSRTObject() , srtObjects.getSecondarySRTObject());
        subTitleMatchesService.persist(matches);
    }
}
