package com.angaria.languagematch.services;

import com.angaria.languagematch.repositories.SubTitleMatchRepository;
import com.angaria.languagematch.wrappers.SubTitleMatches;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;

@Transactional
@Service
public class SubTitleMatchesServiceImpl implements SubTitleMatchesService {

    @Autowired
    private SubTitleMatchRepository subTitleMatchRepository;

    @Override
    public void persist(SubTitleMatches subTitleMatches) {
        subTitleMatchRepository.save(subTitleMatches.getSubTitleMatches());
    }
}
