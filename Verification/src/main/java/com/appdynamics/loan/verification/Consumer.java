package com.appdynamics.loan.verification;

/**
 * Created by amod.gupta on 7/30/15.
 */

import com.appdynamics.loan.common.LoanApplication;
import com.appdynamics.loan.model.Applications;
import com.appdynamics.loan.service.ApplicationsService;
import com.appdynamics.loan.util.SpringContext;
import org.apache.commons.lang3.SerializationException;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

public class Consumer implements MessageListener {

    private static final Logger log = Logger.getLogger(Consumer.class.getName());

    public static ApplicationsService getApplicationsService() {
        return (ApplicationsService) SpringContext.getBean("applicationsService");
    }

    @Override
    public void onMessage(Message message) {
        LoanApplication loanApplication = null;
        boolean fVerified = false;

        try {
            loanApplication = SerializationUtils.deserialize(message.getBody());
        } catch (IllegalArgumentException | SerializationException e) {
            e.printStackTrace();
        }

        if (loanApplication != null)
            fVerified = doWork(loanApplication);

        if (fVerified)
            persistToDB(loanApplication);
    }

    private boolean doWork(LoanApplication loanapp) {

        boolean verified = true;
        int sleepDuration = (int) (Math.random() * 2999 + 1);

        try {
            Thread.sleep(sleepDuration);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        if (sleepDuration < 300)
            verified = false;

        System.out.println("Verification: " + verified + " " + loanapp.getApplicantName());

        return verified;
    }

    private static void persistToDB(LoanApplication loanApplication) {
        try {
            Applications applications = new Applications();
            applications.setId(loanApplication.getApplicationId().toString());
            applications.setLoanType(loanApplication.getLoanType());
            applications.setAmount(loanApplication.getLoanAmount());
            applications.setCustomerId(loanApplication.getApplicantId());
            applications.setApplicationStatus("CC");
            getApplicationsService().saveNewApplications(applications);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }
}