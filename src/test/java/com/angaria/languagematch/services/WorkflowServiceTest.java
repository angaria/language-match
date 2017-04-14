package com.angaria.languagematch.services;

import com.angaria.languagematch.entities.SRTObject;
import com.angaria.languagematch.entities.SubTitle;
import com.angaria.languagematch.entities.SubTitleMatch;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.*;

import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.time.Instant;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WorkflowServiceTest {

    private static final File FILE_SRT_1 = new File("src/test/resources/fileTest1.srt");
    private static final File FILE_SRT_2 = new File("src/test/resources/fileTest2.vi.srt");
    private static final File FILE_NON_SRT = new File("src/test/resources/fileTest3.txt");

    private FileSystemService fileSystemService;
    private WorkflowService workflowService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setup(){
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
        List<File> files = new ArrayList<>();
        files.add(FILE_SRT_1);
        files.add(FILE_SRT_2);

        when(fileSystemService.listSRTFiles(any(String.class))).thenReturn(files);
        Collection<File> results = workflowService.getSRTFilesFromFileSystem();
        assertEquals(2 , results.size());
        assertTrue(results.contains(FILE_SRT_1));
        assertTrue(results.contains(FILE_SRT_2));
    }

    @Test
    public void findMatchingSubTitles() throws Exception {
        SubTitle subTitle = new SubTitle();
        subTitle.setContent("English blah blah");
        subTitle.setStartDate(new Date());
        subTitle.setLanguage("en");
        subTitle.setFileName(FILE_SRT_1.getName());

        SubTitle subTitle2 = new SubTitle();
        subTitle2.setContent("Vietnamese blah blah");
        subTitle2.setStartDate(new Date());
        subTitle2.setLanguage("vi");
        subTitle2.setFileName(FILE_SRT_2.getName());

        SRTObject srtRefObject = new SRTObject(FILE_SRT_1.getName(), "en", Sets.newHashSet(subTitle));
        SRTObject srtTargetObject = new SRTObject(FILE_SRT_2.getName(), "vi", Sets.newHashSet(subTitle2));

        Set<SubTitleMatch> subTitleMatches = workflowService.findMatchingSubTitles(srtRefObject, srtTargetObject);

        assertEquals(1 , subTitleMatches.size());
        assertEquals("Vietnamese blah blah", subTitleMatches.iterator().next().getTargetContent());
    }

    @Test
    public void buildSRTObjects() {
        Collection<SRTObject> srtObjects = workflowService.buildSRTObjects(Sets.newHashSet(FILE_SRT_1));

        SRTObject firstResult = srtObjects.iterator().next();
        SubTitle firstSubTitle = firstResult.getSubTitles().iterator().next();

        assertEquals(1 , srtObjects.size());
        assertEquals(334 , firstResult.getSubTitles().size());

        assertEquals(1491692548344L , firstSubTitle.getStartDate().getTime());
        assertEquals(1491692549261L , firstSubTitle.getEndDate().getTime());
        assertEquals("Who's there?" , firstSubTitle.getContent());
    }
}
