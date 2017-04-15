package com.angaria.languagematch.entities;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import java.text.ParseException;
import static com.angaria.languagematch.entities.SubTitle.COMPLETE_DATE_FORMAT;
import static com.angaria.languagematch.entities.SubTitle.REFERENCE_DAY;
import static org.junit.Assert.assertEquals;

public class SubTitleTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void setStartDateFromLine_positive() throws ParseException {
        SubTitle subTitle = new SubTitle();
        subTitle.setStartDateFromLine("01:40:32,884 --> 01:40:35,971");
        assertEquals(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 01:40:32,884") , subTitle.getStartDate());
    }

    @Test
    public void setStartDateFromLine_negative() throws ParseException {
        SubTitle subTitle = new SubTitle();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("parsing a line which is not a timing line!");
        subTitle.setStartDateFromLine("another random content");
    }

    @Test
    public void setEndDateFromLine_positive() throws ParseException {
        SubTitle subTitle = new SubTitle();
        subTitle.setEndDateFromLine("01:40:32,884 --> 01:40:35,971");
        assertEquals(COMPLETE_DATE_FORMAT.parse(REFERENCE_DAY + " 01:40:35,971") , subTitle.getEndDate());
    }

    @Test
    public void setEndDateFromLine_negative() throws ParseException {
        SubTitle subTitle = new SubTitle();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("parsing a line which is not a timing line!");
        subTitle.setEndDateFromLine("another random content");
    }
}
