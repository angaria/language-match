package com.angaria.languagematch;

import com.angaria.languagematch.others.Workflow;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.*;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Launcher {

    private static final Logger logger = LogManager.getLogger(Launcher.class.getName());

    public static void main(String[] args) {

        logger.log(Level.INFO, "---------- LANGUAGE MATCH v1 ----------");
        new Workflow(lookupFileSystemForSRTFiles()).start();

    }

    private static Set<File> lookupFileSystemForSRTFiles() {

        logger.log(Level.INFO, "Lookup input directory...");
        Collection<File> files = FileUtils.listFiles(new File("src/main/resources/"), null, false);

        if(files.isEmpty()){
            throw new Error("No input file found!");
        }

        Set<File> srtFiles = new LinkedHashSet<>();

        for( File file : files ) {
            if(file.getName().endsWith(".srt")){
                logger.log(Level.DEBUG, "Found SRT file >>>> " + file.getName());
                srtFiles.add(file);
            }
            else{
                logger.log(Level.DEBUG, "Found >>>> " + file.getName());
            }
        }

        if(srtFiles.size() <= 1){
            throw new Error("At least 2 SRT files needed for analysis!");
        }

        return srtFiles;
    }



}
