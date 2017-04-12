package com.angaria.languagematch.components;

import com.angaria.languagematch.entities.SRTObject;
import com.angaria.languagematch.entities.SubTitleMatch;
import com.angaria.languagematch.services.FileSystemService;
import com.angaria.languagematch.services.WorkflowService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Set;

@Component
public class Workflow {

    private static final Logger logger = LogManager.getLogger(Workflow.class.getName());

    @Autowired
    private WorkflowService workflowService;

    public void start() {
        Set<File> srtFiles = workflowService.lookupFileSystemForSRTFiles();
        Set<SRTObject> srtObjects = workflowService.buildSRTObjects(srtFiles);
        Set<SubTitleMatch> subTitleMatches = WorkflowService.extractMatches(srtObjects);
    }
}
