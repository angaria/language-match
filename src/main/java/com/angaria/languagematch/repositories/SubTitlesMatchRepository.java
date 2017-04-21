package com.angaria.languagematch.repositories;

import com.angaria.languagematch.entities.SubTitleMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface SubTitlesMatchRepository extends JpaRepository<SubTitleMatch, Long> {

}