package com.angaria.languagematch.services;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Collection;

/**
 * Created by Alex on 11/04/2017.
 */
public class FileSystemService {

    public Collection<File> listFiles(String path){
        return FileUtils.listFiles(new File(path), null, false);
        //return FileUtils.listFiles(new File("src/main/resources/"), null, false);
    }

}
