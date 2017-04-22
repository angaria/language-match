package com.angaria.languagematch.services;

import com.angaria.languagematch.repositories.SRTObjectRepository;
import com.angaria.languagematch.wrappers.SRTObjects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;

@Transactional
@Service
public class SRTObjectsServiceImpl implements SRTObjectsService {

    @Autowired
    private SRTObjectRepository srtObjectRepository;

    @Override
    public void persist(SRTObjects srtObjects) {
        srtObjectRepository.save(srtObjects.getSrtObjects());
    }
}
