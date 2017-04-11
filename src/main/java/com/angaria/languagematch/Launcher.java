package com.angaria.languagematch;

import com.angaria.languagematch.others.Workflow;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Launcher {

    private static final Logger logger = LogManager.getLogger(Launcher.class.getName());

    public static void main(String[] args) {
        logger.log(Level.INFO, "---------- LANGUAGE MATCH v1 ----------");
        new Workflow().start();
    }

}
