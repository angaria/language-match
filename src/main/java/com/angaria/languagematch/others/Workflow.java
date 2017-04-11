package com.angaria.languagematch.others;

import com.angaria.languagematch.entities.SRTObject;
import com.angaria.languagematch.entities.SubTitle;
import com.angaria.languagematch.entities.SubTitleMatch;
import com.angaria.languagematch.util.WorkflowUtil;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

public class Workflow {

    private static final Logger logger = LogManager.getLogger(Workflow.class.getName());

    private final Set<File> srtFiles;

    public Workflow(Set<File> srtFiles) {
        this.srtFiles = srtFiles;
    }

    public void start() {
        Set<SRTObject> srtObjects = buildSRTObjects(srtFiles);
        Set<SubTitleMatch> subTitleMatches = WorkflowUtil.extractMatches(srtObjects);
    }

    private static Set<SRTObject> buildSRTObjects(Collection<File> srtFilesAsFiles) {
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

    private static String extractLanguageFromFileName(String fileName) {
        String fileNameWithNoExt = fileName.substring(0, fileName.lastIndexOf("."));

        if(!fileNameWithNoExt.contains(".")
                || !Arrays.asList(Locale.getISOLanguages()).contains(fileNameWithNoExt.substring(fileNameWithNoExt.lastIndexOf(".")+1).toLowerCase())){
            return Locale.ENGLISH.getLanguage();
        }

        return fileNameWithNoExt.substring(fileNameWithNoExt.lastIndexOf(".")+1).toLowerCase();
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
                SubTitle match = WorkflowUtil.lookupForMatchingSubTitleFrame(targetSRTObject, subTitleReference);

                SubTitleMatch subTitleMatch = new SubTitleMatch(subTitleReference);
                subTitleMatch.setTargetContent(match.getContent());
                matches.add(subTitleMatch);
            }

        }

        logger.log(Level.INFO, matches);

        return matches;
    }

    private static void enrichSRTObjectWithSubTitles(File file, SRTObject srtObject) {

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
