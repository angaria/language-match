package com.angaria.languagematch.repositories;

import com.angaria.languagematch.entities.SRTObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface SRTObjectRepository extends JpaRepository<SRTObject, String> {

}

