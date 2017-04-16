package com.angaria.languagematch.entities;

import com.angaria.languagematch.util.EntityTest;
import com.angaria.languagematch.util.TestUtility;
import com.google.common.collect.Sets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.text.ParseException;
import java.util.*;

import static com.angaria.languagematch.entities.SubTitle.COMPLETE_DATE_FORMAT;
import static com.angaria.languagematch.entities.SubTitle.REFERENCE_DAY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SRTObjectEntityTest extends EntityTest {

    private static final Logger logger = LogManager.getLogger(SRTObjectEntityTest.class.getName());
    private static final File FILE_SRT_1 = new File("src/test/resources/fileTest1.srt");
    private static final File FILE_SRT_2 = new File("src/test/resources/fileTest2.vi.srt");
    private TestUtility<SubTitle> testUtil = new TestUtility<>();
    private SubTitle subTitleEN1;
    private SubTitle subTitleEN2;
    private SubTitle subTitleVI1;
    private SubTitle subTitleVI2;
    private SRTObject srtObject;
    private SRTObject srtTargetObject;

    @Before
    public void setup() throws ParseException {
        prepareExpectationObjects();
        srtObject = new SRTObject(FILE_SRT_1);
        srtTargetObject = new SRTObject(FILE_SRT_2.getName(), "vi", Sets.newHashSet(subTitleVI1));
    }

    @Override
    protected void prepareExpectationObjects() throws ParseException {
        subTitleEN1 = new SubTitle();
        subTitleEN1.setContent("Who's there?");
        subTitleEN1.setStartDate(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 00:02:28,344"));
        subTitleEN1.setEndDate(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 00:02:29,261"));
        subTitleEN1.setLanguage("en");
        subTitleEN1.setFileName(FILE_SRT_1.getName());

        subTitleEN2 = new SubTitle();
        subTitleEN2.setContent("they keep the shape\r\nof a cherry tree.");
        subTitleEN2.setStartDate(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 01:40:32,884"));
        subTitleEN2.setEndDate(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 01:40:35,971"));
        subTitleEN2.setLanguage("en");
        subTitleEN2.setFileName(FILE_SRT_1.getName());

        subTitleVI1 = new SubTitle();
        subTitleVI1.setContent("Vietnamese blah blah");
        subTitleVI1.setStartDate(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 00:02:28,344"));
        subTitleVI1.setEndDate(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 00:02:29,261"));
        subTitleVI1.setLanguage("vi");
        subTitleVI1.setFileName(FILE_SRT_2.getName());

        subTitleVI2 = new SubTitle();
        subTitleVI2.setContent("Vietnamese blah blah 2");
        subTitleVI2.setStartDate(new Date());
        subTitleVI2.setLanguage("vi");
        subTitleVI2.setFileName(FILE_SRT_2.getName());
    }

    @Test
    public void lookupForMatchingSubTitleFrame_assertResult(){
        SubTitle subTitleResult = srtTargetObject.lookupForMatchingSubTitleFrame(subTitleEN1);
        assertEquals(subTitleVI1, subTitleResult);
    }

    @Test
    public void generateSubTitles_generationSize() throws ParseException {
        srtObject.generateSubTitles();
        assertEquals(334 , srtObject.getSubTitles().size());
    }

    @Test
    public void generateSubTitles_inDetailGenerationCheck() throws ParseException {
        srtObject.generateSubTitles();
        assertSubTitlesContain(srtObject.getSubTitles(), new SubTitle[]{subTitleEN1, subTitleEN2});
    }

    private void assertSubTitlesContain(Collection<SubTitle> subTitles, SubTitle[] elements) {
        Arrays.asList(elements).stream().forEach( e -> {
            assertTrue(subTitles.stream().filter(s -> s.equals(e)).findAny().isPresent());
        });
    }

    @Test
    public void generateSubTitles_requiredFileAttachment_raisesErrorFlag() throws ParseException {
        srtTargetObject.generateSubTitles();
        assertTrue(srtTargetObject.hasErrors());
    }

    @Test
    public void generateSubTitles_requiredFileAttachment_errorMessage() throws ParseException {
        srtTargetObject.generateSubTitles();
        String errorMessage = srtTargetObject.getErrors().iterator().next().getMessage();

        assertEquals("File must be set before in order to generate the SubTitles!" , errorMessage);
    }

    @Test
    public void getLastSubTitle(){
        srtTargetObject = new SRTObject(FILE_SRT_2.getName(), "vi", testUtil.newTreeSet(subTitleVI1, subTitleVI2));
        assertEquals(subTitleVI2, srtTargetObject.getLastSubTitle());
    }
}
