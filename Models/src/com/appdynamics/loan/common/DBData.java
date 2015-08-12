package com.appdynamics.loan.common;

/**
 * Created by amod.gupta on 7/27/15.
 */
public class DBData {

    String url = "jdbc:sqlserver://moj7bax0dr.database.windows.net:1433;DatabaseName=dbStockMarket";
    String user = "amodgupta";
    String password = "welcome-101";

    public String getPassword() {
        return password;
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }


}
