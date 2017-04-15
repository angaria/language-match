package com.angaria.languagematch.entities;

import com.google.common.base.Preconditions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.text.ParseException;
import java.util.*;

public class SRTObject {

    private final File file;
    private final String fileName;
    private final String language;
    private final Set<SubTitle> subTitles;
    private SubTitle subTitle = null;
    private boolean previousLineWasAboutTiming = false;
    private String line;
    private Set<Exception> errors = new LinkedHashSet<>();
    private static final String LINE_SEPARATOR = "\r\n";
    private static final Logger logger = LogManager.getLogger(SRTObject.class.getName());

    public SRTObject(File file){
        this.fileName = file.getName();
        this.language = extractLanguage(fileName);
        this.subTitles = new TreeSet<>();
        this.file = file;
    }

    private static String cleanupLine(String line){
        if(line == null) {
            return null;
        }

        line = line.replace("\u0000", ""); // removes NUL chars
        line = line.replace("\\u0000", ""); // removes backslash+u0000
        return line.trim();
    }

    public void generateSubTitles() {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file),"UTF-8"))) {
            generateSubTitlesBody(br);
        }
        catch(Exception e){
            handleException(e);
        }
    }

    private void generateSubTitlesBody(BufferedReader br) throws IOException {

        while ((line = cleanupLine(br.readLine())) != null) {

            if(isTimingRelated(line)){
                storeLastSubTitle(subTitle);
                subTitle = buildSubTitleFromTiming(line);
                previousLineWasAboutTiming = true;
            }
            else{
                if(previousLineWasAboutTiming){
                    subTitle.setContent(line);
                    previousLineWasAboutTiming = false;
                }
                else if(!isNumeric(line) && !line.isEmpty() && subTitle != null) {
                    if (subTitle.getContent().trim().isEmpty()) {
                        subTitle.setContent(line);
                    } else {
                        subTitle.setContent(subTitle.getContent() + LINE_SEPARATOR + line);
                    }
                }
            }
        }

        storeLastSubTitle(subTitle);
    }

    private void handleException(Exception e) {
        if(file == null){
            e = new NullPointerException("File must be set before in order to generate the SubTitles!");
        }

        errors.add(e);
        logger.info("An error happened during SubTitles generation of:" + this);
        logger.error(e.getMessage());
    }

    public boolean hasErrors(){
        return !errors.isEmpty();
    }

    private static boolean isTimingRelated(String line) {
        return line.contains(SubTitle.SRT_DATE_SEPARATOR);
    }

    private void storeLastSubTitle(SubTitle subTitle) {
        if(subTitle != null){
            this.addSubTitle(subTitle);
        }
    }

    private SubTitle buildSubTitleFromTiming(String line) {
        SubTitle subTitle = new SubTitle();
        subTitle.setLanguage(language);
        subTitle.setFileName(fileName);

        try {
            subTitle.setStartDateFromLine(line);
            subTitle.setEndDateFromLine(line);
        } catch (ParseException e) {
            throw new Error("The line content: '"+line+"' caused a problem while parsing the dates!");
        }

        return subTitle;
    }

    private static boolean isNumeric(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String extractLanguage(String fileName) {
        String fileNameWithNoExt = fileName.substring(0, fileName.lastIndexOf("."));

        if(!fileNameWithNoExt.contains(".")
                || !Arrays.asList(Locale.getISOLanguages()).contains(fileNameWithNoExt.substring(fileNameWithNoExt.lastIndexOf(".")+1).toLowerCase())){
            return Locale.ENGLISH.getLanguage();
        }

        return fileNameWithNoExt.substring(fileNameWithNoExt.lastIndexOf(".")+1).toLowerCase();
    }

    //COV
    public SubTitle lookupForMatchingSubTitleFrame(SubTitle stReference) {
        return subTitles.stream()
                    .filter(s -> s.getStartDate().after(stReference.getStartDate()))
                    .findFirst()
                    .orElse(getLastSubTitle());

    }

    //COV
    public SubTitle getLastSubTitle(){
        return subTitles.stream()
                .reduce((first, second) -> second).get();
    }

    public String getLanguage() {
        return language;
    }

    public Set<SubTitle> getSubTitles() {
        return subTitles;
    }

    public void addSubTitle(SubTitle subTitle) {
        this.subTitles.add(subTitle);
    }

    public SRTObject(String fileName, String language, Set<SubTitle> subTitles){
        this.fileName = fileName;
        this.language = language;
        this.subTitles = subTitles;
        this.file = null;
    }

    public Set<Exception> getErrors() {
        return errors;
    }

    @Override
    public String toString() {
        return "SRTObject{" +
                "fileName='" + fileName + '\'' +
                ", language='" + language + '\'' +
                ", subTitles=" + subTitles.size() +
                '}';
    }
}
