package com.angaria.languagematch.services;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FileSystemServiceTest {

    private static final File FILE_SRT = new File("src/test/resources/fileTest1.srt");
    private static final File FILE_NON_SRT = new File("src/test/resources/fileTestRandom.txt");
    private static final String TEST_PATH = "src/test/resources/";

    @BeforeClass
    public static void setup(){
        Collection<File> listOfFiles = FileUtils.listFiles(new File(TEST_PATH), null, false);
        assertTrue(listOfFiles.size() >= 2);
        assertTrue(listOfFiles.contains(FILE_SRT));
        assertTrue(listOfFiles.contains(FILE_NON_SRT));
    }

    @Test
    public void listSRTFiles(){
        FileSystemService fileSystemService = new FileSystemService();
        Collection<File> results = fileSystemService.listSRTFiles(TEST_PATH);
        assertEquals(1 , results.size());
        assertTrue(results.contains(FILE_SRT));
    }
}
