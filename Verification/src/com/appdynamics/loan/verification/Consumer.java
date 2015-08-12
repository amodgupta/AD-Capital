package com.appdynamics.loan.verification;

/**
 * Created by amod.gupta on 7/30/15.
 */
import com.appdynamics.loan.common.DBData;
import com.appdynamics.loan.common.LoanApplication;
import org.apache.commons.lang3.SerializationException;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

import java.sql.DriverManager;
import java.sql.Statement;

public class Consumer implements MessageListener {

    @Override
    public void onMessage(Message message) {
        LoanApplication loanapp = null;
        boolean fVerified = false;

        try {
            loanapp = SerializationUtils.deserialize(message.getBody());
        } catch (IllegalArgumentException | SerializationException e) {
            e.printStackTrace();
        }

        if (loanapp != null)
            fVerified = doWork(loanapp);

        if (fVerified)
            persistToDB(loanapp);
    }

    private boolean doWork(LoanApplication loanapp){

        boolean verified = true;
        int sleepduration = (int)(Math.random()*2999 + 1);

        try {
            Thread.sleep(sleepduration);
        }catch(InterruptedException ex){
            ex.printStackTrace();
        }

        if (sleepduration < 300)
            verified = false;

        System.out.println("Verification: " + verified + " " + loanapp.getApplicantName());

        return verified;
    }

    private static void persistToDB(LoanApplication loanapp){
        DBData dbinfo = new DBData();
        Statement statement=null;
        java.sql.Connection conn=null;

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn = DriverManager.getConnection(dbinfo.getUrl(), dbinfo.getUser(), dbinfo.getPassword());

            statement = conn.createStatement();
            String queryString = getQueryString(loanapp);
            int rs = statement.executeUpdate(queryString);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (statement != null) statement.close(); } catch (Exception e) {};
            try { if (conn != null) conn.close(); } catch (Exception e) {};
        }

    }

    private static String getQueryString(LoanApplication loanapp)
    {
        String query = "INSERT INTO dbo.newapplications VALUES (";

        query += "'" + loanapp.getApplicationId() + "',";
        query += "'" + loanapp.getLoanType() + "',";
        query += loanapp.getLoanAmount() + ",";
        query += loanapp.getApplicantId() + ",";
        query += "'CC'";
        query += ");";

        return query;
    }
}