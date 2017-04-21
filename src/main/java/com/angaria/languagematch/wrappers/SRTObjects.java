package com.angaria.languagematch.wrappers;

import com.angaria.languagematch.entities.SRTObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class SRTObjects {

    private static final Logger logger = LogManager.getLogger(SRTObjects.class.getName());
    private static final String REF_LANGUAGE = "en";

    private Set<SRTObject> srtObjects = new LinkedHashSet<>();

    public SRTObjects(Set<SRTObject> e){
        this.srtObjects = e;
    }

    public SRTObjects(SRTObject... e){
        this.srtObjects.addAll(Arrays.asList(e));
    }

    public Set<SRTObject> getSrtObjects() {
        return srtObjects;
    }

    public SRTObject first(){
        return srtObjects.iterator().next();
    }

    public int size(){
        return srtObjects.size();
    }

    public SRTObject getSecondarySRTObject() {
        return srtObjects.stream()
                .filter(srt -> !srt.getLanguage().equals(REF_LANGUAGE))
                .findFirst().get();
    }

    public SRTObject getReferenceSRTObject(){
        return srtObjects.stream()
                .filter(srt -> srt.getLanguage().equals(REF_LANGUAGE))
                .findFirst().get();
    }
}
