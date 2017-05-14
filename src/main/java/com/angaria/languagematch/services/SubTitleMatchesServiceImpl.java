package com.angaria.languagematch.services;

import com.angaria.languagematch.repositories.SubTitleMatchRepository;
import com.angaria.languagematch.wrappers.SubTitleMatches;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.Set;

@Transactional
@Service
public class SubTitleMatchesServiceImpl implements SubTitleMatchesService {

    private static final Logger logger = LogManager.getLogger(SubTitleMatchesServiceImpl.class.getName());

    @Autowired
    private SubTitleMatchRepository subTitleMatchRepository;

    @Override
    public void persist(SubTitleMatches subTitleMatches) {
        subTitleMatchRepository.save(subTitleMatches.getSubTitleMatches());
    }

    @Override
    public void persist(Set<SubTitleMatches> groupedItems) {

        groupedItems.stream().forEach(stm -> {
            logger.log(Level.INFO, "Persisting "+stm.getSubTitleMatches().size()+" matches for : " + stm.getTitle());
            subTitleMatchRepository.save(stm.getSubTitleMatches());
        });
    }
}
