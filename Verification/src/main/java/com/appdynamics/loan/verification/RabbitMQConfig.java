package com.appdynamics.loan.verification;

import com.appdynamics.loan.common.LoanApplication;
import com.rabbitmq.client.*;
import org.apache.commons.lang3.SerializationException;
import org.apache.commons.lang3.SerializationUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import org.apache.log4j.Logger;


/**
 * Created by amod.gupta on 7/30/15.
 */
public class RabbitMQConfig {
    private static final String QUEUE_NAME = "Applications";

    private static final Logger log = Logger.getLogger(RabbitMQConfig.class.getName());

    public static void main(String[] argv) throws Exception {

        try {
            while (true) {
                Properties prop = new Properties();
                String propFileName = "config.properties";
                InputStream inputStream = RabbitMQConfig.class.getResourceAsStream(propFileName);
                ConnectionFactory factory = new ConnectionFactory();
                if (inputStream != null) {
                    prop.load(inputStream);
                } else {
                    throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
                }

                String mqUrl = prop.getProperty("mqurl");
                log.info(mqUrl);
                URI uri = new URI(mqUrl);
                factory.setUri(uri);

                com.rabbitmq.client.Connection connection = factory.newConnection();
                Channel channel = connection.createChannel();

                channel.queueDeclare(QUEUE_NAME, false, false, false, null);

                Consumer consumer = new DefaultConsumer(channel) {
                    @Override
                    public void handleDelivery(String consumerTag, Envelope envelope,
                                               AMQP.BasicProperties properties, byte[] body) throws IOException {

                        String message = new String(body, "UTF-8");
                        log.info(" [x] Received '" + message + "'");

                        LoanApplication loanApplication = null;
                        Connection connection = null;
                        boolean verified = true;

                        try {
                            loanApplication = SerializationUtils.deserialize(body);
                        } catch (IllegalArgumentException | SerializationException e) {
                            log.info(e.getMessage());
                        }

                        if (loanApplication != null) {

                            int sleepDuration = (int) (Math.random() * 2999 + 1);

                            try {
                                Thread.sleep(sleepDuration);
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }

                            if (sleepDuration < 1500)
                                verified = false;

                            log.info("Verification: " + verified + " " + loanApplication.getApplicantName());
                        }

                        if (verified) {
                            try {
                                Properties dataBaseProperties = new Properties();

                                InputStream inputStream = RabbitMQConfig.class.getResourceAsStream("database.properties");
                                if (inputStream != null) {
                                    dataBaseProperties.load(inputStream);
                                } else {
                                    throw new FileNotFoundException("property file 'database.properties' not found in the classpath");
                                }

                                String driver = dataBaseProperties.getProperty("mysqlDriver");
                                String dbUrl = dataBaseProperties.getProperty("mysqlUrl");
                                String username = dataBaseProperties.getProperty("mysqlUsername");
                                String password = dataBaseProperties.getProperty("mysqlPassword");

                                try {

                                    Class.forName(driver);
                                    connection = DriverManager.getConnection(dbUrl, username,
                                            password);
                                } catch (ClassNotFoundException e) {
                                    log.error(e.getMessage());
                                } catch (SQLException e) {
                                    log.error(e.getMessage());
                                }

                                String query = " insert into applications (applicationid, loantype, amount, customerid, applicationstatus)"
                                        + " values (?, ?, ?, ?, ?)";

                                log.info("Application ID: " + loanApplication.getApplicantId() + "Loan Type: " + loanApplication.getLoanType()
                                        + "Loan Amount: " + loanApplication.getLoanAmount());

                                // create the mysql insert preparedstatement
                                PreparedStatement preparedStmt = connection.prepareStatement(query);
                                preparedStmt.setString(1, loanApplication.getApplicationId().toString());
                                preparedStmt.setString(2, loanApplication.getLoanType());
                                preparedStmt.setInt(3, loanApplication.getLoanAmount());
                                preparedStmt.setInt(4, loanApplication.getApplicantId());
                                preparedStmt.setString(5, "CC");

                                // execute the preparedstatement
                                preparedStmt.execute();

                                connection.close();
                            } catch (Exception ex) {
                                log.error(ex.getMessage());
                            }
                        }
                    }
                };
                channel.basicConsume(QUEUE_NAME, true, consumer);
                try {
                    Thread.sleep(3 * 1000);
                } catch (InterruptedException ie) {
                    log.error(ie.getMessage());
                }
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

}
