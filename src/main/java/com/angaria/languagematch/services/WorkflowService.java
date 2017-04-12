package com.angaria.languagematch.services;

import com.angaria.languagematch.entities.SRTObject;
import com.angaria.languagematch.entities.SubTitle;
import com.angaria.languagematch.entities.SubTitleMatch;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class WorkflowService {

    private static final Logger logger = LogManager.getLogger(WorkflowService.class.getName());

    @Autowired
    private FileSystemService fileSystemService;

    public Set<File> lookupFileSystemForSRTFiles() {

        logger.log(Level.INFO, "Lookup input directory...");
        Collection<File> files = fileSystemService.listFiles("src/main/resources/");

        if(files.isEmpty()){
            throw new Error("No input file found!");
        }

        Set<File> srtFiles = new LinkedHashSet<>();

        for( File file : files ) {
            if(file.getName().endsWith(".srt")){
                logger.log(Level.DEBUG, "Found SRT file: " + file.getName());
                srtFiles.add(file);
            }
            else{
                logger.log(Level.DEBUG, "Found: " + file.getName());
            }
        }

        if(srtFiles.size() < 2){
            throw new Error("At least 2 SRT files needed for analysis!");
        }

        return srtFiles;
    }

    public static Set<SubTitleMatch> extractMatches(Set<SRTObject> srtObjects) {

        SRTObject srtReferenceObject =  srtObjects.stream()
                .filter(srt -> srt.getLanguage().equals("en"))
                .findFirst()
                .get();

        Set<SRTObject> otherSRTs =  srtObjects.stream()
                .filter(srt -> !srt.getLanguage().equals("en"))
                .collect(Collectors.toSet());

        Set<SubTitleMatch> matches = new LinkedHashSet<>();

        for(SRTObject targetSRTObject : otherSRTs){

            for(SubTitle subTitleReference : srtReferenceObject.getSubTitles()){
                SubTitle match = lookupForMatchingSubTitleFrame(targetSRTObject, subTitleReference);

                SubTitleMatch subTitleMatch = new SubTitleMatch(subTitleReference);
                subTitleMatch.setTargetContent(match.getContent());
                matches.add(subTitleMatch);
            }

        }

        logger.log(Level.INFO, matches);

        return matches;
    }

    public static SubTitle lookupForMatchingSubTitleFrame(SRTObject targetSRTObject, SubTitle subTitleReference) {

        SubTitle previousSubTitleTarget = null;

        for(SubTitle subTitleTarget : targetSRTObject.getSubTitles()){
            if(subTitleTarget.getStartDate().after(subTitleReference.getStartDate())){
                return previousSubTitleTarget;
            }
            previousSubTitleTarget = subTitleTarget;
        }

        return previousSubTitleTarget;
    }

    public void setFileSystemService(FileSystemService fileSystemService) {
        this.fileSystemService = fileSystemService;
    }

    public Set<SRTObject> buildSRTObjects(Collection<File> srtFilesAsFiles) {
        logger.log(Level.INFO, "Creating subtitle Objects...");

        Set<SRTObject> srtFilesAsObjects = new LinkedHashSet<>();

        for (File file : srtFilesAsFiles){
            SRTObject srtObject = new SRTObject(file.getName(), extractLanguageFromFileName(file.getName()));

            enrichSRTObjectWithSubTitles(file, srtObject);

            srtFilesAsObjects.add(srtObject);
            logger.log(Level.DEBUG, srtObject.toString());
        }

        return srtFilesAsObjects;
    }

    private String extractLanguageFromFileName(String fileName) {
        String fileNameWithNoExt = fileName.substring(0, fileName.lastIndexOf("."));

        if(!fileNameWithNoExt.contains(".")
                || !Arrays.asList(Locale.getISOLanguages()).contains(fileNameWithNoExt.substring(fileNameWithNoExt.lastIndexOf(".")+1).toLowerCase())){
            return Locale.ENGLISH.getLanguage();
        }

        return fileNameWithNoExt.substring(fileNameWithNoExt.lastIndexOf(".")+1).toLowerCase();
    }

    private void enrichSRTObjectWithSubTitles(File file, SRTObject srtObject) {

        SubTitle subTitle = null;
        boolean previousLineWasAboutTiming = false;

        try {

            BufferedReader br  = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file),"UTF-8"));

            String currentLine = null;

            while ((currentLine = br.readLine()) != null) {

                //cleanup
                currentLine = currentLine.replace("\u0000", ""); // removes NUL chars
                currentLine = currentLine.replace("\\u0000", ""); // removes backslash+u0000

                if(currentLine.contains(SubTitle.SRT_DATE_SEPARATOR)){

                    if(subTitle != null){
                        srtObject.addSubTitle(subTitle);
                    }

                    subTitle = new SubTitle();
                    subTitle.setLanguage(srtObject.getLanguage());
                    subTitle.setFileName(srtObject.getFileName());

                    try {
                        subTitle.setStartDateFromLine(currentLine);
                        subTitle.setEndDateFromLine(currentLine);
                    } catch (ParseException e) {
                        throw new Error("The line content: '"+currentLine+"' caused a problem while parsing the dates!");
                    }

                    previousLineWasAboutTiming = true;
                    continue;
                }

                if(previousLineWasAboutTiming){
                    subTitle.setContent(currentLine);
                    previousLineWasAboutTiming = false;
                    continue;
                }
                else{
                    if(isNumeric(currentLine) || currentLine.trim().isEmpty()){
                        continue;
                    }
                    else if(subTitle!= null){
                        if(subTitle.getContent().trim().isEmpty()){
                            subTitle.setContent(currentLine);
                        }
                        else{
                            subTitle.setContent(subTitle.getContent() + "\r\n" + currentLine);
                        }
                    }
                }
            }

            br.close();

        } catch (FileNotFoundException e) {
            System.out.println( "File " + file.getAbsolutePath()+ "not found for Scanning!");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(subTitle != null){
            srtObject.addSubTitle(subTitle);
        }

    }

    private static boolean isNumeric(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
