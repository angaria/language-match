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
        ApplicationContext context = new ClassPathXmlApplicationContext("context.xml");

        Workflow workflow = (Workflow) context.getBean("workflow");
        workflow.start();
    }
}
