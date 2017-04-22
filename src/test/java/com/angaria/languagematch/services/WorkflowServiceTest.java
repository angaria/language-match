package com.angaria.languagematch.services;

import com.angaria.languagematch.wrappers.SRTObjects;
import com.angaria.languagematch.entities.SRTObject;
import com.angaria.languagematch.entities.SubTitle;
import com.angaria.languagematch.entities.SubTitleBuilder;
import com.angaria.languagematch.entities.SubTitleMatch;
import com.angaria.languagematch.wrappers.SubTitleMatches;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import java.io.File;
import java.text.ParseException;
import java.util.*;

import static com.angaria.languagematch.entities.SubTitle.COMPLETE_DATE_FORMAT;
import static com.angaria.languagematch.entities.SubTitle.REFERENCE_DAY;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

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

    @Before
    public void setup() throws ParseException {
        setupMocks();
        prepareExpectationObjects();
    }

    private void prepareExpectationObjects() throws ParseException {
        subTitle1 = SubTitleBuilder.getInstance()
                .content("English blah blah")
                .startDate(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 01:40:32,384"))
                .endDate(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 01:40:35,971"))
                .language("en")
                .srtObject(srtRefObject)
                .build();

        subTitle2 = SubTitleBuilder.getInstance()
                .content("Vietnamese blah blah")
                .startDate(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 01:40:32,884"))
                .endDate(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 01:40:36,271"))
                .language("vi")
                .srtObject(srtTargetObject)
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
    public void getSRTFilesFromFileSystem_noInputFilesExceptionType() throws Exception {
        when(fileSystemService.listSRTFiles(any(String.class))).thenReturn(new ArrayList<>());
        try{
            workflowService.getSRTFilesFromFileSystem();
            fail();
        }
        catch(Exception e){
            assertEquals(Exception.class, e.getClass());
        }
    }

    @Test
    public void getSRTFilesFromFileSystem_noInputFilesExceptionMessage() throws Exception {
        when(fileSystemService.listSRTFiles(any(String.class))).thenReturn(new ArrayList<>());
        try{
            workflowService.getSRTFilesFromFileSystem();
            fail();
        }
        catch(Exception e){
            assertEquals("No input file found!", e.getMessage());
        }
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
    public void findMatchingSubTitles_overlapOK_stRefStartsBeforeTargetAndEndsAfter() throws Exception {

        //  SubTitleRef    :  ========================
        //  SubTitleTarget :    =====================

        subTitle1.setStartDate(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 01:40:32,384"));
        subTitle1.setEndDate(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 01:40:36,371"));

        subTitle2.setStartDate(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 01:40:32,884"));
        subTitle2.setEndDate(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 01:40:36,271"));

        SubTitleMatches matches = workflowService.findMatchingSubTitles(srtRefObject, srtTargetObject);
        assertEquals(1 , matches.size());
        assertEquals("Vietnamese blah blah", matches.next().getTargetContent());
    }

    @Test
    public void findMatchingSubTitles_overlapOK_stRefStartsBeforeTargetAndEndsBefore() throws Exception {

        //  SubTitleRef    :  ========================
        //  SubTitleTarget :    ========================

        subTitle1.setStartDate(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 01:40:32,384"));
        subTitle1.setEndDate(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 01:40:35,971"));

        subTitle2.setStartDate(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 01:40:32,884"));
        subTitle2.setEndDate(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 01:40:36,271"));

        SubTitleMatches matches = workflowService.findMatchingSubTitles(srtRefObject, srtTargetObject);
        assertEquals(1 , matches.size());
        assertEquals("Vietnamese blah blah", matches.next().getTargetContent());
    }

    @Test
    public void findMatchingSubTitles_overlapOK_stRefStartsAfterTargetAndEndsBefore() throws Exception {

        //  SubTitleRef    :   ====================
        //  SubTitleTarget :  ========================

        subTitle1.setStartDate(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 01:40:33,084"));
        subTitle1.setEndDate(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 01:40:35,971"));

        subTitle2.setStartDate(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 01:40:32,884"));
        subTitle2.setEndDate(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 01:40:36,271"));

        SubTitleMatches matches = workflowService.findMatchingSubTitles(srtRefObject, srtTargetObject);
        assertEquals(1 , matches.size());
        assertEquals("Vietnamese blah blah", matches.next().getTargetContent());
    }

    @Test
    public void findMatchingSubTitles_overlapOK_stRefStartsAfterTargetAndEndsAfter() throws Exception {

        //  SubTitleRef    :   ========================
        //  SubTitleTarget :  ========================

        subTitle1.setStartDate(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 01:40:33,084"));
        subTitle1.setEndDate(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 01:40:36,971"));

        subTitle2.setStartDate(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 01:40:32,884"));
        subTitle2.setEndDate(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 01:40:36,671"));

        SubTitleMatches matches = workflowService.findMatchingSubTitles(srtRefObject, srtTargetObject);
        assertEquals(1 , matches.size());
        assertEquals("Vietnamese blah blah", matches.next().getTargetContent());
    }

    @Test
    public void findMatchingSubTitles_overlapKO_stRefStartsAfterTarget() throws Exception {

        //  SubTitleRef    :                    ========================
        //  SubTitleTarget :  ========================

        subTitle1.setStartDate(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 01:40:34,084"));
        subTitle1.setEndDate(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 01:40:36,971"));

        subTitle2.setStartDate(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 01:40:32,884"));
        subTitle2.setEndDate(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 01:40:36,671"));

        SubTitleMatches matches = workflowService.findMatchingSubTitles(srtRefObject, srtTargetObject);
        assertEquals(0 , matches.size());
    }

    @Test
    public void findMatchingSubTitles_overlapKO_stRefStartsBeforeTarget() throws Exception {

        //  SubTitleRef    :  ========================
        //  SubTitleTarget :               ========================

        subTitle1.setStartDate(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 01:40:32,384"));
        subTitle1.setEndDate(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 01:40:33,971"));

        subTitle2.setStartDate(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 01:40:32,884"));
        subTitle2.setEndDate(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 01:40:36,271"));

        SubTitleMatches matches = workflowService.findMatchingSubTitles(srtRefObject, srtTargetObject);
        assertEquals(0 , matches.size());
    }

    @Test
    public void findMatchingSubTitles_NoOverlap_stRefStartsBeforeTarget() throws Exception {

        //  SubTitleRef    :  ===========
        //  SubTitleTarget :                 ===============

        subTitle1.setStartDate(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 01:40:32,384"));
        subTitle1.setEndDate(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 01:40:32,771"));

        subTitle2.setStartDate(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 01:40:32,884"));
        subTitle2.setEndDate(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 01:40:36,271"));

        SubTitleMatches matches = workflowService.findMatchingSubTitles(srtRefObject, srtTargetObject);
        assertEquals(0 , matches.size());
    }

    @Test
    public void findMatchingSubTitles_NoOverlap_stRefStartsAfterTarget() throws Exception {

        //  SubTitleRef    :                    ========================
        //  SubTitleTarget :  ==============

        subTitle1.setStartDate(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 01:40:34,084"));
        subTitle1.setEndDate(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 01:40:36,971"));

        subTitle2.setStartDate(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 01:40:32,884"));
        subTitle2.setEndDate(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 01:40:33,671"));

        SubTitleMatches matches = workflowService.findMatchingSubTitles(srtRefObject, srtTargetObject);
        assertEquals(0 , matches.size());
    }

    @Test
    public void findMatchingSubTitles_ReferenceFileMissing_ExceptionMessage() throws Exception {
        try{
            workflowService.findMatchingSubTitles(null, srtTargetObject);
            fail();
        }
        catch(Exception e){
            assertEquals("Reference SRT file missing!", e.getMessage());
        }
    }

    @Test
    public void findMatchingSubTitles_ReferenceFileMissing_ExceptionType() throws Exception {
        try{
            workflowService.findMatchingSubTitles(null, srtTargetObject);
            fail();
        }
        catch(Exception e){
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }

    @Test
    public void findMatchingSubTitles_SecondFileMissing_ExceptionType() throws Exception {
        try{
            workflowService.findMatchingSubTitles(srtRefObject, null);
            fail();
        }
        catch(Exception e){
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }

    @Test
    public void findMatchingSubTitles_SecondFileMissing_ExceptionMessage() throws Exception {
        try{
            workflowService.findMatchingSubTitles(srtRefObject, null);
            fail();
        }
        catch(Exception e){
            assertEquals("Second SRT file missing!", e.getMessage());
        }
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
