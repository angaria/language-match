package com.angaria.languagematch.services;

import com.angaria.languagematch.entities.SRTObject;
import com.angaria.languagematch.entities.SubTitle;
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
        fileSystemService = mock(FileSystemService.class);
        workflowService = new WorkflowService();
        workflowService.setFileSystemService(fileSystemService);

        subTitle1 = new SubTitle();
        subTitle1.setContent("English blah blah");
        subTitle1.setStartDate(new Date());
        subTitle1.setLanguage("en");
        subTitle1.setFileName(FILE_SRT_1.getName());

        subTitle2 = new SubTitle();
        subTitle2.setContent("Vietnamese blah blah");
        subTitle2.setStartDate(new Date());
        subTitle2.setLanguage("vi");
        subTitle2.setFileName(FILE_SRT_2.getName());

        srtRefObject = new SRTObject(FILE_SRT_1.getName(), "en", Sets.newHashSet(subTitle1));
        srtTargetObject = new SRTObject(FILE_SRT_2.getName(), "vi", Sets.newHashSet(subTitle2));
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
    public void buildSRTObjects() throws ParseException {
        Collection<SRTObject> srtObjects = workflowService.buildSRTObjects(Sets.newHashSet(FILE_SRT_1));

        SRTObject firstResult = srtObjects.iterator().next();
        SubTitle firstSubTitle = firstResult.getSubTitles().iterator().next();

        assertEquals(1 , srtObjects.size());
        assertEquals(334 , firstResult.getSubTitles().size());

        assertEquals(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 00:02:28,344") , firstSubTitle.getStartDate());
        assertEquals(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 00:02:29,261") , firstSubTitle.getEndDate());
        assertEquals("Who's there?" , firstSubTitle.getContent());
    }

    @Test
    public void getReferenceSRT(){
        Set<SRTObject> srtObjects = Sets.newHashSet(srtRefObject, srtTargetObject);
        SRTObject srtObject = workflowService.getReferenceSRT(srtObjects);

        assertEquals("en", srtObject.getLanguage());
        assertEquals(1, srtObject.getSubTitles().size());
    }

    @Test
    public void getTargetLanguageSRT(){
        Set<SRTObject> srtObjects = Sets.newHashSet(srtRefObject, srtTargetObject);
        SRTObject srtObject = workflowService.getTargetLanguageSRT(srtObjects);

        assertEquals("vi", srtObject.getLanguage());
        assertEquals(1, srtObject.getSubTitles().size());
    }
}
