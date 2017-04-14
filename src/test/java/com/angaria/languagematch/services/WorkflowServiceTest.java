package com.angaria.languagematch.services;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WorkflowServiceTest {

    private static final File FILE_SRT_1 = new File("src/test/resources/fileTest1.srt");
    private static final File FILE_SRT_2 = new File("src/test/resources/fileTest2.vi.srt");
    private static final File FILE_NON_SRT = new File("src/test/resources/fileTest3.txt");

    @Mock
    private FileSystemService fileSystemService;

    @InjectMocks
    private WorkflowService workflowService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

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
}
