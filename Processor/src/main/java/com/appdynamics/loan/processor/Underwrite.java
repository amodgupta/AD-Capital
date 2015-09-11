package com.appdynamics.loan.processor;

import com.appdynamics.loan.model.Applications;
import com.appdynamics.loan.service.ApplicationsService;
import com.appdynamics.loan.util.SpringContext;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.util.concurrent.TimeoutException;

/**
 * Created by amod.gupta on 7/28/15.
 */
@WebServlet(name = "Underwrite", urlPatterns = {"/Underwrite"})
public class Underwrite extends HttpServlet {

    private int customerid;
    private String applicationid;

    private static final Logger log = Logger.getLogger(Underwrite.class.getName());

    public static ApplicationsService getApplicationsService() {
        return (ApplicationsService) SpringContext.getBean("applicationsService");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //Get application for Underwriting
        boolean found = getApplicationForUnderwriting();

        String message = "No application found for underwriting";
        if (found)
            message = "Customer ID:" + customerid + ", your application has been sent to the underwriter";

        response.setContentType("text/html");
        response.setHeader("Access-Control-Allow-Origin", "*");
        PrintWriter out = response.getWriter();
        out.println(message);
        out.flush();

    }

    private boolean sendToUnderwriter() throws TimeoutException, IOException {
        boolean approved = true;
        int num = (int) (Math.random() * 99 + 1);
        if (num < 11)
            return false;

        ConnectionFactory factory = new ConnectionFactory();
        try {

            URI uri = new URI(GetRabbitMQURLFromConfigFiles());
            factory.setUri(uri);

            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            String queueName = "ApprovedAppsQueue";

            // Create queue if it doesn't exist
            channel.queueDeclare(queueName, false, false, false, null);
            channel.basicPublish("", queueName, null, this.applicationid.getBytes());
            log.info(" [x] Sent '" + this.applicationid + "'");

            channel.close();
            connection.close();
        } catch (Exception ex) {
            log.error("Error Submitting Application" + ex.getMessage());
        }

        return approved;
    }

    private boolean getApplicationForUnderwriting() {
        boolean found = false;
        try {
            Applications applications = getApplicationsService().getApplicationsWithUWStatus();
            if (applications != null) {
                found = true;
                this.customerid = applications.getCustomerId();
                this.applicationid = String.valueOf(applications.getId());
                //Send to UnderWriter
                sendToUnderwriter();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return found;

    }

    private String GetRabbitMQURLFromConfigFiles() {
        GetConfigProperties properties = new GetConfigProperties();
        try {
            return properties.getRabbitMQUrl();
        } catch (IOException ex) {
            // TODO Auto-generated catch block
            log.error(ex.getMessage());
        }
        return null;
    }
}
