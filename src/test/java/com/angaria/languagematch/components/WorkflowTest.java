package com.angaria.languagematch.components;

import com.angaria.languagematch.entities.SRTObject;
import com.angaria.languagematch.services.WorkflowService;
import com.google.common.collect.Sets;
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
    private SRTObject srtRefObject = new SRTObject(FILE_SRT_1.getName(), "en", null);
    private SRTObject srtTargetObject = new SRTObject(FILE_SRT_2.getName(), "vi", null);

    @Mock
    private WorkflowService workflowService;

    @InjectMocks
    private Workflow workflow;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void start_readsFileSystem() throws Exception {
        when(workflowService.getSRTFilesFromFileSystem()).thenReturn(null);
        workflow.start();
        verify(workflowService, atLeastOnce()).getSRTFilesFromFileSystem();
    }

    @Test
    public void start_buildsSRTObjects() throws Exception {
        when(workflowService.buildSRTObjects(any(Collection.class))).thenReturn(Sets.newHashSet(srtRefObject, srtTargetObject));
        workflow.start();
        verify(workflowService, atLeastOnce()).buildSRTObjects(any(Collection.class));
    }

    @Test
    public void start_looksForSubTitleMatches() throws Exception {
        when(workflowService.findMatchingSubTitles(any(SRTObject.class), any(SRTObject.class))).thenReturn(null);
        workflow.start();
        verify(workflowService, atLeastOnce()).findMatchingSubTitles(any(SRTObject.class), any(SRTObject.class));
    }
}
