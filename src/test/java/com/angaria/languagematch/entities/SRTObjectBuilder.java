package com.angaria.languagematch.entities;

import java.io.File;
import java.util.Set;

public class SRTObjectBuilder extends SRTObject {

    public SRTObjectBuilder file(File file){
        this.file = file;
        return this;
    }

    public SRTObjectBuilder fileName(String fileName){
        this.fileName = fileName;
        return this;
    }

    public SRTObjectBuilder subTitles(Set<SubTitle> subTitles){
        this.subTitles = subTitles;
        return this;
    }

    public SRTObjectBuilder language(String language){
        this.language = language;
        return this;
    }

    public SRTObject build(){
        return new SRTObject(fileName , language , subTitles, file);
    }
}
