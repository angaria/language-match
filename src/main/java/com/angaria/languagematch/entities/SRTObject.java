package com.angaria.languagematch.entities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.*;
import java.io.*;
import java.text.ParseException;
import java.util.*;

import static javax.persistence.GenerationType.AUTO;

@Entity
@Table(name="srt_files")
public class SRTObject {

    private static final String LINE_SEPARATOR = "\r\n";
    private static final Logger logger = LogManager.getLogger(SRTObject.class.getName());

    @Id
    @Column(name="file_name")
    protected String fileName;

    protected String language;

    @OneToMany(mappedBy = "srtObject", cascade = { CascadeType.ALL }, orphanRemoval=true, fetch=FetchType.LAZY)
    protected Set<SubTitle> subTitles = new LinkedHashSet<>();

    @Transient
    private SubTitle tempSubTitle = null;

    @Transient
    private boolean previousLineWasAboutTiming = false;

    @Transient
    private String line;

    @Transient
    private Set<Exception> errors = new LinkedHashSet<>();

    @Transient
    protected File file;

    public SRTObject(File file){
        this();
        this.fileName = file.getName();
        this.language = extractLanguage(fileName);
        this.subTitles = new TreeSet<>();
        this.file = file;
    }

    public SRTObject(){}

    public SRTObject(String fileName, String language, Set<SubTitle> subTitles, File file){
        this();
        this.fileName = fileName;
        this.language = language;
        this.subTitles = subTitles;
        this.file = file;
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

    private void handleException(Exception e) {
        if(file == null){
            e = new NullPointerException("File must be set before in order to generate the SubTitles!");
        }

        errors.add(e);
        logger.info("An error happened during SubTitles generation of:" + this);
        logger.error(e.getMessage());
    }

    private void generateSubTitlesBody(BufferedReader br) throws IOException {

        while ((line = cleanupLine(br.readLine())) != null) {

            if(isTimingRelated(line)){
                storeLastSubTitle(tempSubTitle);
                tempSubTitle = buildSubTitleFromTiming(line);
                previousLineWasAboutTiming = true;
            }
            else{
                if(previousLineWasAboutTiming){
                    tempSubTitle.setContent(line);
                    previousLineWasAboutTiming = false;
                }
                else if(!isNumeric(line) && !line.isEmpty() && tempSubTitle != null) {
                    tempSubTitle.setContent(tempSubTitle.getContent() + LINE_SEPARATOR + line);
                }
            }
        }

        storeLastSubTitle(tempSubTitle);
    }

    private static String cleanupLine(String line){
        if(line == null) {
            return null;
        }

        line = line.replace("\u0000", ""); // removes NUL chars
        line = line.replace("\\u0000", ""); // removes backslash+u0000
        return line.trim();
    }

    private static boolean isTimingRelated(String line) {
        return line.contains(SubTitle.SRT_DATE_SEPARATOR);
    }

    private SubTitle buildSubTitleFromTiming(String line) {
        SubTitle subTitle = new SubTitle();
        subTitle.setLanguage(language);
        subTitle.setSRTObject(this);

        try {
            subTitle.setStartDateFromLine(line);
            subTitle.setEndDateFromLine(line);
        } catch (ParseException e) {
            throw new Error("The line content: '"+line+"' caused a problem while parsing the dates!");
        }

        return subTitle;
    }

    private void storeLastSubTitle(SubTitle subTitle) {
        if(subTitle != null){
            this.addSubTitle(subTitle);
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

    public boolean hasErrors(){
        return !errors.isEmpty();
    }

    private String extractLanguage(String fileName) {
        String fileNameWithNoExt = fileName.substring(0, fileName.lastIndexOf("."));

        if(!fileNameWithNoExt.contains(".")
                || !Arrays.asList(Locale.getISOLanguages()).contains(fileNameWithNoExt.substring(fileNameWithNoExt.lastIndexOf(".")+1).toLowerCase())){
            return Locale.ENGLISH.getLanguage();
        }

        return fileNameWithNoExt.substring(fileNameWithNoExt.lastIndexOf(".")+1).toLowerCase();
    }

    public SubTitle lookupForMatchingSubTitleFrame(SubTitle stReference) {
        return subTitles.stream()
                    .filter(s -> s.getStartDate().after(stReference.getStartDate()))
                    .findFirst()
                    .orElse(getLastSubTitle());

    }

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

    public Set<Exception> getErrors() {
        return errors;
    }

    public File getFile() {
        return file;
    }

    public String getFileName() {
        return fileName;
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
