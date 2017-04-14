package com.angaria.languagematch.entities;

import java.io.*;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

public class SRTObject {

    private final String fileName;
    private final String language;
    private final Set<SubTitle> subTitles = new TreeSet<>();

    public SRTObject(File file){
        this.fileName = file.getName();
        this.language = extractLanguage(fileName);
        addSubTitlesFromSRTFile(file);
    }

    private void addSubTitlesFromSRTFile(File file) {

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
                        this.addSubTitle(subTitle);
                    }

                    subTitle = new SubTitle();
                    subTitle.setLanguage(language);
                    subTitle.setFileName(fileName);

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
        String fileNameWithNoExt = fileName.substring(0, fileName.lastIndexOf("."));

        if(!fileNameWithNoExt.contains(".")
                || !Arrays.asList(Locale.getISOLanguages()).contains(fileNameWithNoExt.substring(fileNameWithNoExt.lastIndexOf(".")+1).toLowerCase())){
            return Locale.ENGLISH.getLanguage();
        }

        return fileNameWithNoExt.substring(fileNameWithNoExt.lastIndexOf(".")+1).toLowerCase();
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

    @Override
    public String toString() {
        return "SRTObject{" +
                "fileName='" + fileName + '\'' +
                ", language='" + language + '\'' +
                ", subTitles=" + subTitles.size() +
                '}';
    }
}
