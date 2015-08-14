package com.appdynamics.loan.portal;

import com.appdynamics.loan.common.UserData;
import com.appdynamics.loan.model.Customer;
import com.appdynamics.loan.service.CustomerService;
import com.appdynamics.loan.util.SpringContext;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by swetha.ravichandran on 8/13/15.
 */
@WebServlet(name = "AuthServlet", urlPatterns = {"/Authenticate"})
public class AuthenticationServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(AuthenticationServlet.class.getName());
    public CustomerService getCustomerService() {
        return (CustomerService) SpringContext.getBean("customerService");
    }

    @Autowired
    private ApplicationContext appContext;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserData udata = new UserData();
        String level = "";
        String address = "";
        String userinfo = udata.name;
        if(getCustomerService().validateMember(udata.name, udata.passcode))
        {
            level = getCustomerService().getMemberByName(udata.name).getLevel();
            userinfo += ":" + level;

            if(level.equals("Platinum")){
                address = getCustomerService().getMemberByName(udata.name).getShippingAddress();
                userinfo += ":" + address;
            }
        }

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("Current User: " + userinfo);
        log.info("Current User: " + userinfo);
        out.flush();
    }
}
