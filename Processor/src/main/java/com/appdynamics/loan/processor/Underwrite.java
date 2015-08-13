package com.appdynamics.loan.processor;

import com.appdynamics.loan.common.DBData;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.commons.lang3.SerializationUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.TimeoutException;

/**
 * Created by amod.gupta on 7/28/15.
 */
@WebServlet(name = "Underwrite",urlPatterns = {"/Underwrite"})
public class Underwrite extends HttpServlet {
    private int customerid;
    private String applicationid;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //Get application for Underwriting
        boolean ffound = getApplicationForUnderwriting();

        String message = "No application found for underwriting";
        if (ffound)
            message = "Customer ID:" + customerid + ", your application has been sent to the underwriter";

        response.setContentType("text/html");
        response.setHeader("Access-Control-Allow-Origin", "*");
        PrintWriter out = response.getWriter();
        out.println(message);
        out.flush();

    }

    private boolean sendToUnderwriter() throws TimeoutException,IOException {
        boolean approved = true;
        int num = (int)(Math.random()*99 + 1);
        if (num < 11)
            return false;

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("ec2-54-242-38-169.compute-1.amazonaws.com");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        String qname = "ApprovedAppsQueue";

        // Create queue if it doesn't exist
        channel.queueDeclare(qname, false, false, false, null);
        channel.basicPublish("", qname, null, this.applicationid.getBytes());

        channel.close();
        connection.close();

        return approved;
    }

    private boolean getApplicationForUnderwriting(){
        String getquery = "SELECT TOP 1 ApplicationID,CustomerID FROM dbo.newapplications where applicationstatus ='UW';";
        DBData dbinfo = new DBData();
        java.sql.Connection conn=null;
        Statement statement=null;
        ResultSet rs=null;
        boolean ffound=false;

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn = DriverManager.getConnection(dbinfo.getUrl(), dbinfo.getUser(), dbinfo.getPassword());

            statement = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            rs = statement.executeQuery(getquery);

            if (rs.next() && rs.getConcurrency() == ResultSet.CONCUR_UPDATABLE){
                ffound = true;
                this.customerid = rs.getInt("CustomerID");
                this.applicationid = rs.getObject(1).toString();

                //Send to UnderWriter
                boolean approved = sendToUnderwriter();

                rs.deleteRow();
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
