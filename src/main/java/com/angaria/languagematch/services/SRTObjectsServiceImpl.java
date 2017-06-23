package com.angaria.languagematch.services;

import com.angaria.languagematch.entities.SRTObject;
import com.angaria.languagematch.repositories.SRTObjectRepository;
import com.angaria.languagematch.wrappers.SRTObjects;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class SRTObjectsServiceImpl implements SRTObjectsService {

    private static final Logger logger = LogManager.getLogger(SRTObjectsServiceImpl.class.getName());

    @Autowired
    private SRTObjectRepository srtObjectRepository;

    @Override
    public SRTObjects persist(SRTObjects srtObjects) {
        logger.log(Level.INFO, "Persisting subtitle objects");

        List<SRTObject> srtObjectList = new ArrayList<>();
        for (SRTObject SRTObject : srtObjects.getSrtObjects()){
            SRTObject object = srtObjectRepository.findOne(SRTObject.getFileName());
            if(object == null){
                srtObjectRepository.save(SRTObject);
                srtObjectList.add(SRTObject);
            }
            else{
                logger.log(Level.INFO, SRTObject.getFileName() + " already persisted... will not be reattempted!");
            }
        }

        srtObjects.setSrtObjects(srtObjectList);
        return srtObjects;
    }
}
