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

public class SRTObjectTest extends EntityTest {

    private static final Logger logger = LogManager.getLogger(SRTObjectTest.class.getName());
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
    }

    @Test
    public void lookupForMatchingSubTitleFrame_assertResult(){
        srtTargetObject = new SRTObject(FILE_SRT_2.getName(), "vi", Sets.newHashSet(subTitleVI1), null);
        SubTitle subTitleResult = srtTargetObject.lookupForMatchingSubTitleFrame(subTitleEN1);
        assertEquals(subTitleVI1, subTitleResult);
    }

    @Test
    public void generateSubTitles_generationSize() throws ParseException {
        srtObject = new SRTObject(FILE_SRT_1);
        srtObject.generateSubTitles();
        assertEquals(334 , srtObject.getSubTitles().size());
    }

    @Test
    public void generateSubTitles_inDetailGenerationCheck() throws ParseException {
        srtObject = new SRTObject(FILE_SRT_1);
        srtObject.generateSubTitles();
        assertCollectionContains(srtObject.getSubTitles(), new SubTitle[]{subTitleEN1, subTitleEN2});
    }

    private void assertCollectionContains(Collection<SubTitle> subTitles, SubTitle[] elements) {
        Arrays.asList(elements).stream().forEach( e -> {
            logger.debug(e);
            assertTrue(subTitles.stream().filter(s -> s.equals(e)).findAny().isPresent());
        });
    }

    @Test
    public void generateSubTitles_requiredFileAttachment_raisesErrorFlag() throws ParseException {
        srtTargetObject = new SRTObject(FILE_SRT_2.getName(), "vi", Sets.newHashSet(subTitleVI1), null);
        srtTargetObject.generateSubTitles();
        assertTrue(srtTargetObject.hasErrors());
    }

    @Test
    public void generateSubTitles_requiredFileAttachment_errorMessage() throws ParseException {
        srtTargetObject = new SRTObject(FILE_SRT_2.getName(), "vi", Sets.newHashSet(subTitleVI1), null);
        srtTargetObject.generateSubTitles();

        String errorMessage = srtTargetObject.getErrors().iterator().next().getMessage();
        assertEquals("File must be set before in order to generate the SubTitles!" , errorMessage);
    }

    @Test
    public void getLastSubTitle(){
        srtTargetObject = new SRTObject(FILE_SRT_2.getName(), "vi", testUtil.newTreeSet(subTitleVI1, subTitleVI2), null);
        assertEquals(subTitleVI2, srtTargetObject.getLastSubTitle());
    }

    @Test
    public void constructorFromFile_setsLanguage(){
        srtTargetObject = new SRTObject(FILE_SRT_2);
        assertEquals("vi", srtTargetObject.getLanguage());
    }

    @Test
    public void constructorFromFile_setsFileName(){
        srtTargetObject = new SRTObject(FILE_SRT_2);
        assertEquals("fileTest2.vi.srt", srtTargetObject.getFileName());
    }

    @Test
    public void constructorFromFile_initializesSubTitles(){
        srtTargetObject = new SRTObject(FILE_SRT_2);
        assertTrue(srtTargetObject.getSubTitles().isEmpty());
    }

    @Override
    protected void prepareExpectationObjects() throws ParseException {
        srtObject = new SRTObject(FILE_SRT_1);
        srtTargetObject = new SRTObject(FILE_SRT_2);

        subTitleEN1 = SubTitleBuilder.getInstance()
                .content("Who's there?")
                .startDate(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 00:02:28,344"))
                .endDate(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 00:02:29,261"))
                .language("en")
                .srtObject(srtObject)
                .build();

        subTitleEN2 = SubTitleBuilder.getInstance()
                .content("they keep the shape\r\nof a cherry tree.")
                .startDate(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 01:40:32,884"))
                .endDate(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 01:40:35,971"))
                .language("en")
                .srtObject(srtObject)
                .build();

        subTitleVI1 = SubTitleBuilder.getInstance()
                .content("Vietnamese blah blah")
                .startDate(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 00:02:28,344"))
                .endDate(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 00:02:29,261"))
                .language("vi")
                .srtObject(srtTargetObject)
                .build();

        subTitleVI2 = SubTitleBuilder.getInstance()
                .content("Vietnamese blah blah 2")
                .startDate(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 01:40:32,884"))
                .endDate(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 01:40:35,971"))
                .language("vi")
                .srtObject(srtTargetObject)
                .build();
    }
}
