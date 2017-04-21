package com.angaria.languagematch;

import com.angaria.languagematch.components.Workflow;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class Launcher {

    private static final Logger logger = LogManager.getLogger(Launcher.class.getName());

    public static void main(String[] args) {
        logger.log(Level.INFO, "---------- LANGUAGE MATCH v1 ----------");

        ApplicationContext context = new ClassPathXmlApplicationContext("META-INF/context.xml");

        handleWorkflow((Workflow) context.getBean("workflow"));
    }

    private static void handleWorkflow(Workflow workflow){
        try {
            workflow.start();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
