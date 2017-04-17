package com.angaria.languagematch.entities;

import org.junit.Test;
import java.text.ParseException;
import static com.angaria.languagematch.entities.SubTitle.COMPLETE_DATE_FORMAT;
import static com.angaria.languagematch.entities.SubTitle.REFERENCE_DAY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class SubTitleTest {

    @Test
    public void setStartDateFromLine_positive() throws ParseException {
        SubTitle subTitle = new SubTitle();
        subTitle.setStartDateFromLine("01:40:32,884 --> 01:40:35,971");
        assertEquals(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 01:40:32,884") , subTitle.getStartDate());
    }

    @Test
    public void setStartDateFromLine_inputBadContent_ExceptionType() throws ParseException {
        try{
            new SubTitle().setStartDateFromLine("another random content");
            fail();
        }
        catch(Exception e){
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }

    @Test
    public void setStartDateFromLine_inputBadContent_ExceptionMessage() throws ParseException {
        try{
            new SubTitle().setStartDateFromLine("another random content");
        }
        catch(Exception e){
            assertEquals("parsing a line which is not a timing line!", e.getMessage());
        }
    }

    @Test
    public void setEndDateFromLine_positive() throws ParseException {
        SubTitle subTitle = new SubTitle();
        subTitle.setEndDateFromLine("01:40:32,884 --> 01:40:35,971");
        assertEquals(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 01:40:35,971") , subTitle.getEndDate());
    }

    @Test
    public void setEndDateFromLine_inputBadContent_ExceptionType() throws ParseException {
        try{
            new SubTitle().setEndDateFromLine("another random content");
            fail();
        }
        catch(Exception e){
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }

    @Test
    public void setEndDateFromLine_inputBadContent_ExceptionMessage() throws ParseException {
        try{
            new SubTitle().setEndDateFromLine("another random content");
        }
        catch(Exception e){
            assertEquals("parsing a line which is not a timing line!", e.getMessage());
        }
    }
}
