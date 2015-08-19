package com.appdynamics.loan.queuereader;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


/**
 * Created by amod.gupta on 7/26/15.
 */
public class Notary {

    private static final Logger log = Logger.getLogger(Notary.class.getName());

    public static void main(String [ ] args){

        // Get the next loan application from the queue
        log.info("Notary has started verifying documents");
        ApplicationContext ctx = new AnnotationConfigApplicationContext(RabbitMQConfig.class);

    }

}
