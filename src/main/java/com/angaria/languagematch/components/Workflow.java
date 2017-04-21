package com.angaria.languagematch.components;

import com.angaria.languagematch.services.WorkflowService;
import com.angaria.languagematch.wrappers.SRTObjects;
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

    public void start() throws Exception {
        Collection<File> srtFiles = workflowService.getSRTFilesFromFileSystem();
        SRTObjects srtObjects = workflowService.buildSRTObjects(srtFiles);

        workflowService.findMatchingSubTitles(srtObjects.getReferenceSRTObject() , srtObjects.getSecondarySRTObject());
    }
}
