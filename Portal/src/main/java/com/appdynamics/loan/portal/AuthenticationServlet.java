package com.appdynamics.loan.portal;

import com.appdynamics.loan.common.UserData;
import com.appdynamics.loan.model.Customer;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by swetha.ravichandran on 8/13/15.
 */
@WebServlet(name = "AuthServlet", urlPatterns = {"/Authenticate"})
public class AuthenticationServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(AuthenticationServlet.class.getName());



    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserData udata = new UserData();
        String level = "";
        String address = "";
        String userinfo = udata.name;

        Client client = Client.create();
        WebResource webResource = client.resource(GetConfigFiles()
                + "/login");

        MultivaluedMap formData = new MultivaluedMapImpl();
        formData.add("username", udata.name);
        formData.add("password", Integer.toString(udata.passcode));
        String returnResponse = webResource
                .type("application/x-www-form-urlencoded")
                .post(String.class, formData);
        if(returnResponse.equalsIgnoreCase("success")){
            ClientConfig clientConfig = new DefaultClientConfig();
            clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING,
                    Boolean.TRUE);
            Customer customer = Client.create(clientConfig)
                    .resource(GetConfigFiles() + "/getcust")
                    .accept(MediaType.APPLICATION_JSON)
                    .header("username", udata.name)
                    .get(Customer.class);
            level = customer.getLevel();
            userinfo += ", Level: " + level;
            if(level.equalsIgnoreCase("platinum")) {
                address = customer.getShippingAddress();
                userinfo += ", Shipping Address: " + address;
            }
        }

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("Current User: " + userinfo);
        log.info("Current User: " + userinfo);
        out.flush();
    }

    private String GetConfigFiles() {
        GetConfigProperties properties = new GetConfigProperties();
        try {
            return properties.getOrderUrl();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
