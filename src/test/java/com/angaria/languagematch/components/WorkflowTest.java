package com.angaria.languagematch.components;

import com.angaria.languagematch.entities.SRTObject;
import com.angaria.languagematch.services.SRTObjectsService;
import com.angaria.languagematch.services.SubTitleMatchesService;
import com.angaria.languagematch.services.WorkflowService;
import com.angaria.languagematch.wrappers.SRTObjects;
import com.angaria.languagematch.wrappers.SubTitleMatches;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import java.io.File;
import java.util.Collection;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WorkflowTest {

    private static final File FILE_SRT_1 = new File("src/test/resources/fileTest1.srt");
    private static final File FILE_SRT_2 = new File("src/test/resources/fileTest2.vi.srt");
    private SRTObject srtRefObject = new SRTObject(FILE_SRT_1.getName(), "en", null, null);
    private SRTObject srtTargetObject = new SRTObject(FILE_SRT_2.getName(), "vi", null, null);

    @Mock
    private WorkflowService workflowService;

    @Mock
    private SRTObjectsService srtObjectsService;

    @Mock
    private SubTitleMatchesService subTitleMatchesService;

    @InjectMocks
    private Workflow workflow;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        when(workflowService.buildSRTObjects(any(Collection.class))).thenReturn(new SRTObjects(srtRefObject, srtTargetObject));
    }

    @Test
    public void start_readsFileSystem() throws Exception {
        when(workflowService.getSRTFilesFromFileSystem()).thenReturn(null);
        workflow.start();
        verify(workflowService, atLeastOnce()).getSRTFilesFromFileSystem();
    }

    @Test
    public void start_buildsSRTObjects() throws Exception {
        workflow.start();
        verify(workflowService, atLeastOnce()).buildSRTObjects(any(Collection.class));
    }

    @Test
    public void start_persistsSRTObjects() throws Exception {
        workflow.start();
        verify(srtObjectsService, atLeastOnce()).persist(any(SRTObjects.class));
    }

    @Test
    public void start_looksForSubTitleMatches() throws Exception {
        workflow.start();
        verify(workflowService, atLeastOnce()).findMatchingSubTitles(any(SRTObject.class), any(SRTObject.class));
    }

    @Test
    public void start_persistsMatches() throws Exception {
        workflow.start();
        verify(subTitleMatchesService, atLeastOnce()).persist(any(SubTitleMatches.class));
    }
}
