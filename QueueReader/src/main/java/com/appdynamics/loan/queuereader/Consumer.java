package com.appdynamics.loan.queuereader;

/**
 * Created by amod.gupta on 7/30/15.
 */

import org.apache.commons.lang3.SerializationException;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

public class Consumer implements MessageListener {

    private static final Logger log = Logger.getLogger(Consumer.class.getName());

    @Override
    public void onMessage(Message message) {

        try {
            SerializationUtils.deserialize(message.getBody());
        } catch (IllegalArgumentException | SerializationException e) {
            e.printStackTrace();
        }
    }
}