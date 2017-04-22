package com.angaria.languagematch.repositories;

import com.angaria.languagematch.entities.SubTitle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface SubTitleRepository extends JpaRepository<SubTitle, Long> {

}