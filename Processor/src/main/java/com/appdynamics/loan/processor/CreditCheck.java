package com.appdynamics.loan.processor;

import com.appdynamics.loan.common.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by amod.gupta on 7/28/15.
 */
@javax.servlet.annotation.WebServlet(name = "CreditCheck", urlPatterns = {"/CreditCheck"})
public class CreditCheck extends javax.servlet.http.HttpServlet {

    String getquery = "SELECT TOP 1 ApplicationID,CustomerID FROM dbo.newapplications where applicationstatus ='CC';";
    String getscore = "SELECT CreditScore from dbo.customer where id=";
    String updateapplication = "UPDATE dbo.newapplications SET applicationstatus='UW' where ApplicationID='";
    String deleteapplication = "DELETE FROM dbo.newapplications where ApplicationID='";

    int customerid;
    String applicationid;
    int score;


    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {

    }

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        boolean approve = true;
        boolean ffound;
        String message = "No application found for credit approval";

        // Get Next Application for credit processing
        ffound = getApplicationForCreditCheck();

        if (ffound){
            // Check FICO Score
            getFICOScore();

            // Decide
            if (score < 650)
                approve = false;

            // Update Status
            updateApplicationStatus(approve);

            message = "Customer ID:" + customerid + " FICO Score: " + score + "Approved: " + approve;

        }

        response.setContentType("text/html");
        response.setHeader("Access-Control-Allow-Origin", "*");
        PrintWriter out = response.getWriter();
        out.println(message);
        out.flush();

    }

    private boolean updateApplicationStatus(boolean approve){
        boolean fsuccess = false;
        int rows=0;
        String query;
        DBData dbinfo = new DBData();
        java.sql.Connection conn=null;
        Statement statement=null;


        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn = DriverManager.getConnection(dbinfo.getUrl(), dbinfo.getUser(), dbinfo.getPassword());

            statement = conn.createStatement();
            if (approve)
                query = this.updateapplication;
            else
                query = this.deleteapplication;

            rows = statement.executeUpdate(query+this.applicationid+"';");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (statement != null) statement.close(); } catch (Exception e) {};
            try { if (conn != null) conn.close(); } catch (Exception e) {};
        }

        if (rows == 1)
            fsuccess = true;
        return fsuccess;
    }

    private void getFICOScore()
    {
        DBData dbinfo = new DBData();
        java.sql.Connection conn=null;
        Statement statement=null;
        ResultSet rs=null;

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn = DriverManager.getConnection(dbinfo.getUrl(), dbinfo.getUser(), dbinfo.getPassword());

            statement = conn.createStatement();
            rs = statement.executeQuery(this.getscore + this.customerid + ";");

            if (rs.next())
                this.score = rs.getInt(1);

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {};
            try { if (statement != null) statement.close(); } catch (Exception e) {};
            try { if (conn != null) conn.close(); } catch (Exception e) {};
        }
    }

    private boolean getApplicationForCreditCheck(){
        DBData dbinfo = new DBData();
        java.sql.Connection conn=null;
        Statement statement=null;
        ResultSet rs=null;
        boolean ffound = false;

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn = DriverManager.getConnection(dbinfo.getUrl(), dbinfo.getUser(), dbinfo.getPassword());

            statement = conn.createStatement();
            rs = statement.executeQuery(getquery);

            if (rs.next()){
                ffound = true;
                this.customerid = rs.getInt("CustomerID");
                this.applicationid = rs.getObject(1).toString();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {};
            try { if (statement != null) statement.close(); } catch (Exception e) {};
            try { if (conn != null) conn.close(); } catch (Exception e) {};
        }

        return ffound;

    }


}
