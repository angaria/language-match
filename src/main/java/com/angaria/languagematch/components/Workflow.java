package com.angaria.languagematch.components;

import com.angaria.languagematch.entities.SRTObject;
import com.angaria.languagematch.entities.SubTitleMatch;
import com.angaria.languagematch.services.WorkflowService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Collection;
import java.util.Set;

@Component
public class Workflow {

    private static final Logger logger = LogManager.getLogger(Workflow.class.getName());

    @Autowired
    private WorkflowService workflowService;

    public void start() throws Exception {
        Collection<File> srtFiles = workflowService.getSRTFilesFromFileSystem();
        Set<SRTObject> srtObjects = workflowService.buildSRTObjects(srtFiles);

        SRTObject refObject = workflowService.getReferenceSRT(srtObjects);
        SRTObject targetObject = workflowService.getTargetLanguageSRT(srtObjects);

        Set<SubTitleMatch> subTitleMatches = workflowService.extractMatchingSubTitles(refObject , targetObject);
    }
}
