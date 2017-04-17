package com.angaria.languagematch.services;

import com.angaria.languagematch.components.SRTObjects;
import com.angaria.languagematch.entities.SRTObject;
import com.angaria.languagematch.entities.SubTitle;
import com.angaria.languagematch.entities.SubTitleBuilder;
import com.angaria.languagematch.entities.SubTitleMatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import static com.angaria.languagematch.entities.SubTitle.COMPLETE_DATE_FORMAT;
import static com.angaria.languagematch.entities.SubTitle.REFERENCE_DAY;
import static org.mockito.Mockito.*;

import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.text.ParseException;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WorkflowServiceTest {

    private static final File FILE_SRT_1 = new File("src/test/resources/fileTest1.srt");
    private static final File FILE_SRT_2 = new File("src/test/resources/fileTest2.vi.srt");

    private SubTitle subTitle1;
    private SubTitle subTitle2;
    private SRTObject srtRefObject;
    private SRTObject srtTargetObject;
    private FileSystemService fileSystemService;
    private WorkflowService workflowService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setup(){
        setupMocks();
        prepareExpectationObjects();
    }

    private void prepareExpectationObjects() {
        subTitle1 = SubTitleBuilder.getInstance()
                .content("English blah blah")
                .startDate(new Date())
                .language("en")
                .fileName(FILE_SRT_1.getName())
                .build();

        subTitle2 = SubTitleBuilder.getInstance()
                .content("Vietnamese blah blah")
                .startDate(new Date())
                .language("vi")
                .fileName(FILE_SRT_2.getName())
                .build();

        srtRefObject = new SRTObject(FILE_SRT_1.getName(), "en", Sets.newHashSet(subTitle1), null);
        srtTargetObject = new SRTObject(FILE_SRT_2.getName(), "vi", Sets.newHashSet(subTitle2), null);
    }

    private void setupMocks() {
        fileSystemService = mock(FileSystemService.class);
        workflowService = new WorkflowService();
        workflowService.setFileSystemService(fileSystemService);
    }

    @Test
    public void getSRTFilesFromFileSystem_noInputFiles() throws Exception {
        expectedException.expect(Exception.class);
        expectedException.expectMessage("No input file found!");

        when(fileSystemService.listSRTFiles(any(String.class))).thenReturn(new ArrayList<>());
        workflowService.getSRTFilesFromFileSystem();
    }

    @Test
    public void getSRTFilesFromFileSystem_positive2SRTFiles() throws Exception {
        List<File> files = Lists.newArrayList(FILE_SRT_1, FILE_SRT_2);

        when(fileSystemService.listSRTFiles(any(String.class))).thenReturn(files);
        Collection<File> results = workflowService.getSRTFilesFromFileSystem();
        assertEquals(2 , results.size());
        assertTrue(results.contains(FILE_SRT_1));
        assertTrue(results.contains(FILE_SRT_2));
    }

    @Test
    public void findMatchingSubTitles() throws Exception {
        Set<SubTitleMatch> subTitleMatches = workflowService.findMatchingSubTitles(srtRefObject, srtTargetObject);

        assertEquals(1 , subTitleMatches.size());
        assertEquals("Vietnamese blah blah", subTitleMatches.iterator().next().getTargetContent());
    }

    @Test
    public void findMatchingSubTitles_negative1() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Reference SRT file missing!");

        workflowService.findMatchingSubTitles(null, srtTargetObject);
    }

    @Test
    public void findMatchingSubTitles_negative2() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Second SRT file missing!");

        workflowService.findMatchingSubTitles(srtRefObject, null);
    }

    @Test
    public void buildSRTObjects_resultSize() throws ParseException {
        SRTObjects srtObjects = workflowService.buildSRTObjects(Sets.newHashSet(FILE_SRT_1));
        assertEquals(1, srtObjects.getSrtObjects().size());
    }

    @Test
    public void buildSRTObjects_resultSize2() throws ParseException {
        SRTObjects srtObjects = workflowService.buildSRTObjects(Sets.newHashSet(FILE_SRT_1, FILE_SRT_2));
        assertEquals(2, srtObjects.getSrtObjects().size());
    }
}
