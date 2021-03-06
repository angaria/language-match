package com.angaria.languagematch.services;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;
import java.io.File;
import java.util.Collection;

@Component
public class FileSystemService {

    public Collection<File> listSRTFiles(String path){
        return FileUtils.listFiles(new File(path), new String[]{"srt"}, false);
    }
}
