package com.angaria.languagematch.util;

import com.angaria.languagematch.services.FileSystemService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class WorkflowUtilTest {

    @Mock
    private FileSystemService fileSystemService;

    @InjectMocks
    private WorkflowUtil workflowUtil;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static final File FILE_SRT_1 = new File("src/test/resources/fileTest1.srt");
    private static final File FILE_SRT_2 = new File("src/test/resources/fileTest2.vi.srt");
    private static final File FILE_NON_SRT = new File("src/test/resources/fileTest3.txt");

    @Test
    public void lookupFileSystemForSRTFiles_negative2SRTFiles() {
        List<File> files = new ArrayList<>();
        files.add(FILE_SRT_1);

        expectedException.expect(Error.class);
        expectedException.expectMessage("At least 2 SRT files needed for analysis!");

        when(fileSystemService.listFiles(any(String.class))).thenReturn(files);
        workflowUtil.lookupFileSystemForSRTFiles();
    }

    @Test
    public void lookupFileSystemForSRTFiles_positive2SRTFiles() {
        List<File> files = new ArrayList<>();
        files.add(FILE_SRT_1);
        files.add(FILE_SRT_2);

        when(fileSystemService.listFiles(any(String.class))).thenReturn(files);
        Set<File> results = workflowUtil.lookupFileSystemForSRTFiles();
        assertEquals(results.size(), 2);
        assertTrue(results.contains(FILE_SRT_1));
        assertTrue(results.contains(FILE_SRT_2));
    }

    @Test
    public void lookupFileSystemForSRTFiles_positive2SRTFiles_1NonSRTFile() {
        List<File> files = new ArrayList<>();
        files.add(FILE_SRT_1);
        files.add(FILE_SRT_2);
        files.add(FILE_NON_SRT);

        when(fileSystemService.listFiles(any(String.class))).thenReturn(files);

        Set<File> results = workflowUtil.lookupFileSystemForSRTFiles();
        assertEquals(results.size(), 2);
        assertTrue(results.contains(FILE_SRT_1));
        assertTrue(results.contains(FILE_SRT_2));
    }
}
