package com.angaria.languagematch.entities;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.text.ParseException;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import static com.angaria.languagematch.entities.SubTitle.COMPLETE_DATE_FORMAT;
import static com.angaria.languagematch.entities.SubTitle.REFERENCE_DAY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SRTObjectTest {

    private static final File FILE_SRT_1 = new File("src/test/resources/fileTest1.srt");
    private static final File FILE_SRT_2 = new File("src/test/resources/fileTest2.vi.srt");
    private SubTitle subTitleEN;
    private SubTitle subTitleVI1;
    private SubTitle subTitleVI2;
    private SRTObject srtObject;
    private SRTObject srtTargetObject;

    @Before
    public void setup(){
        subTitleEN = new SubTitle();
        subTitleEN.setContent("English blah blah");
        subTitleEN.setStartDate(new Date());
        subTitleEN.setLanguage("en");
        subTitleEN.setFileName(FILE_SRT_1.getName());

        subTitleVI1 = new SubTitle();
        subTitleVI1.setContent("Vietnamese blah blah");
        subTitleVI1.setStartDate(new Date());
        subTitleVI1.setLanguage("vi");
        subTitleVI1.setFileName(FILE_SRT_2.getName());

        subTitleVI2 = new SubTitle();
        subTitleVI2.setContent("Vietnamese blah blah 2");
        subTitleVI2.setStartDate(new Date());
        subTitleVI2.setLanguage("vi");
        subTitleVI2.setFileName(FILE_SRT_2.getName());

        srtObject = new SRTObject(FILE_SRT_1);
        srtTargetObject = new SRTObject(FILE_SRT_2.getName(), "vi", Sets.newHashSet(subTitleVI1));
    }

    @Test
    public void lookupForMatchingSubTitleFrame(){
        SubTitle subTitleResult = srtTargetObject.lookupForMatchingSubTitleFrame(subTitleEN);
        assertEquals(subTitleVI1, subTitleResult);
    }

    @Test
    public void generateSubTitles_positive() throws ParseException {
        srtObject.generateSubTitles();

        SubTitle firstSubTitle = srtObject.getSubTitles().iterator().next();
        SubTitle lastSubTitle = srtObject.getLastSubTitle();

        assertEquals(334 , srtObject.getSubTitles().size());

        assertEquals(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 00:02:28,344") , firstSubTitle.getStartDate());
        assertEquals(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 00:02:29,261") , firstSubTitle.getEndDate());
        assertEquals("Who's there?" , firstSubTitle.getContent());

        assertEquals(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 01:40:32,884") , lastSubTitle.getStartDate());
        assertEquals(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 01:40:35,971") , lastSubTitle.getEndDate());
        assertEquals("they keep the shape\r\nof a cherry tree." , lastSubTitle.getContent());
    }

    @Test
    public void generateSubTitles_negative() throws ParseException {
        srtTargetObject = new SRTObject(FILE_SRT_2.getName(), "vi", Sets.newHashSet(subTitleVI1));
        srtTargetObject.generateSubTitles();

        assertTrue(srtTargetObject.hasErrors());
        assertEquals("File must be set before in order to generate the SubTitles!" , srtTargetObject.getErrors().iterator().next().getMessage());
    }

    @Test
    public void getLastSubTitle(){
        Set<SubTitle> subTitles = new TreeSet<>();
        subTitles.add(subTitleVI1);
        subTitles.add(subTitleVI2);

        srtTargetObject = new SRTObject(FILE_SRT_2.getName(), "vi", subTitles);
        assertEquals(subTitleVI2, srtTargetObject.getLastSubTitle());
    }
}
