package com.angaria.languagematch.entities;


import java.util.Set;
import java.util.TreeSet;

public class SRTObject {

    private String fileName;

    private String language;

    private final Set<SubTitle> subTitles = new TreeSet<>();

    public SRTObject(String fileName, String language){
        this.fileName = fileName;
        this.language = language;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
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
