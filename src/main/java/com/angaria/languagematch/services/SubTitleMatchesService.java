package com.angaria.languagematch.services;

import com.angaria.languagematch.wrappers.SubTitleMatches;

import java.util.Set;

public interface SubTitleMatchesService {

    void persist(SubTitleMatches subTitleMatches);

    void persist(Set<SubTitleMatches> subTitleMatchesSet);
}

