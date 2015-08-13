package com.appdynamics.loan.verification;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


/**
 * Created by amod.gupta on 7/26/15.
 */
public class Notary {

    public static void main(String [ ] args){

        // Get the next loan application from the queue
        System.out.println("Notary has started verifying documents");

        ApplicationContext ctx = new AnnotationConfigApplicationContext(RabbitMQConfig.class);

    }

}
