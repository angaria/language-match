package com.angaria.languagematch.components;

import com.angaria.languagematch.services.SRTObjectsService;
import com.angaria.languagematch.services.SubTitleMatchesService;
import com.angaria.languagematch.services.WorkflowService;
import com.angaria.languagematch.wrappers.SRTObjects;
import com.angaria.languagematch.wrappers.SubTitleMatches;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
        Collection<File> srtFiles = workflowService.getSRTFilesFromFileSystem();
        SRTObjects srtObjects = workflowService.buildSRTObjects(srtFiles);
        srtObjects = srtObjectsService.persist(srtObjects);

        srtObjects.groupByTitle().entrySet()
                .stream()
                .forEach( e -> {
                        SubTitleMatches m = workflowService.findMatchingSubTitles(e.getValue().getReferenceSRTObject(),
                                                                    e.getValue().getSecondarySRTObject());
                        subTitleMatchesService.persist(m);

                        try {
                            FileUtils.copyFile(
                                        new File(WorkflowService.SRT_FILES_PATH + "/" + e.getValue().getReferenceSRTObject().getFileName()),
                                        new File(WorkflowService.SRT_FILES_PATH_DEST + "/" + e.getValue().getReferenceSRTObject().getFileName()));

                            FileUtils.copyFile(
                                    new File(WorkflowService.SRT_FILES_PATH + "/" + e.getValue().getSecondarySRTObject().getFileName()),
                                    new File(WorkflowService.SRT_FILES_PATH_DEST + "/" + e.getValue().getSecondarySRTObject().getFileName()));

                            FileUtils.forceDelete(new File(WorkflowService.SRT_FILES_PATH + "/" + e.getValue().getReferenceSRTObject().getFileName()));
                            FileUtils.forceDelete(new File(WorkflowService.SRT_FILES_PATH + "/" + e.getValue().getSecondarySRTObject().getFileName()));

                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }

                });
    }
}
