package com.angaria.languagematch.wrappers;

import com.angaria.languagematch.entities.SRTObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;

public class SRTObjects {

    private static final Logger logger = LogManager.getLogger(SRTObjects.class.getName());

    private static final String REF_LANGUAGE = "en";
    public static final String[] SECONDARY_LANGUAGES = {"vi"};

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

    private void addSRTObject(SRTObject srtObject){
        srtObjects.add(srtObject);
    }

    public void setSrtObjects(Collection<SRTObject> items) {
        srtObjects = new LinkedHashSet<>();
        srtObjects.addAll(items);
    }

    public SRTObject first(){
        return srtObjects.iterator().next();
    }

    public int size(){
        return srtObjects.size();
    }

    public SRTObject getSecondarySRTObject() {
        Optional<SRTObject> srtObject = srtObjects.stream()
                .filter(srt -> Arrays.asList(SECONDARY_LANGUAGES).contains(srt.getLanguage()))
                .findFirst();

        if(srtObject.isPresent()){
            return srtObject.get();
        }

        throw new RuntimeException("No file with Secondary language has been found!");
    }

    public SRTObject getReferenceSRTObject(){
        Optional<SRTObject> srtObject = srtObjects.stream()
                .filter(srt -> REF_LANGUAGE.equals(srt.getLanguage())
                                    || !Arrays.asList(SECONDARY_LANGUAGES).contains(srt.getLanguage()))
                .findFirst();

        if(srtObject.isPresent()){
            return srtObject.get();
        }

        throw new RuntimeException("No file with Reference language has been found!");
    }

    public Map<String, SRTObjects> getGroupsByTitle(){

        Map<String, Set<String>> textFileListBydate =
                srtObjects.stream()
                        .map(srt -> srt.getFileName())
                        .collect(groupingBy(s -> s.substring(0, 5), toSet()));

        Map<String, SRTObjects> result = new HashMap<>();

        for(Map.Entry<String, Set<String>> entry : textFileListBydate.entrySet()) {
            for(String title : entry.getValue()) {
                SRTObject srtObject = srtObjects.stream()
                                                .filter(s -> s.getFileName().equals(title))
                                                .findAny()
                                                .get();

                if(result.get(entry.getKey()) == null){
                    result.put(entry.getKey(), new SRTObjects());
                }
                result.get(entry.getKey()).addSRTObject(srtObject);
            }
        }

        return result;
    }
}
