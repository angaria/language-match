package com.angaria.languagematch.entities;

import com.angaria.languagematch.components.CharsetDetector;
import com.angaria.languagematch.wrappers.SRTObjects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;

import javax.persistence.*;
import java.io.*;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.*;

@Entity
@Table(name="srt_files")
public class SRTObject {

    private static final Logger logger = LogManager.getLogger(SRTObject.class.getName());
    private static final String LINE_SEPARATOR = "\r\n";

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
        tryGenerateSubTitles();
    }

    protected SRTObject(){}

    public SRTObject(String fileName, String language, Set<SubTitle> subTitles, File file){
        this();
        this.fileName = fileName;
        this.language = language;
        this.subTitles = subTitles;
        this.file = file;
        tryGenerateSubTitles();
    }

    private void tryGenerateSubTitles() {
        if(file != null){
            CharsetDetector cd = new CharsetDetector();
            Charset charset = cd.detectCharset(file, new String[]{"UTF-8", "utf-16le"});

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), charset))) {
                generateSubTitles(br);
            }
            catch(Exception e){
                handleException(e);
            }
        }
    }

    private void generateSubTitles(BufferedReader br) throws IOException {

        while ((line = cleanup(br.readLine())) != null) {

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
                    tempSubTitle.setContent(cleanup(tempSubTitle.getContent() + " " + line));
                }
            }
        }

        storeLastSubTitle(tempSubTitle);
    }

    private void handleException(Exception e) {
        if(file == null){
            e = new NullPointerException("File must be set before in order to generate the SubTitles!");
        }

        errors.add(e);
        logger.info("An error happened during SubTitles generation of:" + this);
        logger.error(e.getMessage());
    }

    private static String cleanup(String line){
        if(line == null) {
            return null;
        }

        line = line.replace("\u0000", ""); // removes NUL chars
        line = line.replace("\\u0000", ""); // removes backslash+u0000

        line = line.replace(LINE_SEPARATOR, ""); // removes carriage returns
        line = line.replace("\r", ""); // removes carriage returns
        line = line.replace("\n", ""); // removes carriage returns

        line = line.replace("  ", " "); // removes double spaces
        line = line.replace("  ", " "); // removes double spaces

        line = Jsoup.parse(line).text();

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

    private String extractLanguage(String fileName) {
        String fileNameWithNoExt = fileName.substring(0, fileName.lastIndexOf('.'));
        String code = getPotentialLanguage(fileNameWithNoExt);

        if(!fileNameWithNoExt.contains(".") || !isSecondaryLanguageCode(code)){
            return Locale.ENGLISH.getLanguage();
        }

        return fileNameWithNoExt.substring(fileNameWithNoExt.lastIndexOf('.') + 1).toLowerCase();
    }

    private boolean isSecondaryLanguageCode(String code){
        return Arrays.asList(SRTObjects.SECONDARY_LANGUAGES).contains(code);
    }

    private String getPotentialLanguage(String fileNameWithNoExt){
        return fileNameWithNoExt.substring(fileNameWithNoExt.lastIndexOf('.')+1).toLowerCase();
    }

    public SubTitle lookupForMatchingSubTitleFrame(SubTitle stReference) {
        return subTitles.stream()
                    .filter(s -> s.isOverlappingEnoughWith(stReference))
                    .filter(s -> s.hasOnlyOnePersonTalking())
                    .filter(s -> s.hasNotForbiddenCharacters())
                    .findFirst()
                    .orElse(null);
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
