package com.angaria.languagematch.services;

import com.angaria.languagematch.entities.SRTObject;
import com.angaria.languagematch.repositories.SRTObjectRepository;
import com.angaria.languagematch.wrappers.SRTObjects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

@Transactional
@Service
public class SRTObjectsServiceImpl implements SRTObjectsService {

    @Autowired
    private SRTObjectRepository srtObjectRepository;

    @Override
    public SRTObjects persist(SRTObjects srtObjects) {
        List<SRTObject> srtObjectList = srtObjectRepository.save(srtObjects.getSrtObjects());
        srtObjects.setSrtObjects(srtObjectList);
        return srtObjects;
    }
}
