package com.appdynamics.loan.common;

/**
 * Created by amod.gupta on 7/24/15.
 */
public class UserData {

    private String[] username = {"Garth Lara","Ross Huber","Troy Phelps","Allistair Higgins", "Murphy Cleveland", "Berk Bass", "Marsden Stanley", "Levi Tyler", "Talon Moses", "Ryan Middleton", "Joel Singleton", "Graiden Singleton", "Guy Hurst", "Aladdin Flowers", "Noah Washington", "Guy Reynolds", "Keefe Lynn", "Ira Lambert", "Levi Wells", "Wing Lindsay", "Benjamin Ashley", "Xavier Harris", "Steel Clay", "Ivor Nicholson", "Barclay Pena", "Wylie Lamb", "Xander Stark", "Henry Durham", "Darius Vargas", "Cody Hinton", "Lance Payne", "Xavier Carver", "Silas Wynn", "Leonard Parrish", "Ryan Rodriquez", "Ian Lara", "Uriah Weber", "William Watts", "Leonard Skinner", "Rooney Gross", "Clinton Blackwell", "Cruz Rush", "Rigel Hull", "Abel Casey", "Plato Maddox", "Harper Mayo", "Gavin Floyd", "Keefe Potts", "Honorato Mayo", "Austin Hicks", "Bert Dalton", "Jonas Barrett", "Tiger Hubbard", "Lars Poole", "Kermit Jennings", "Travis Cantrell", "Caesar Schwartz", "Garth Pittman", "Darius Robertson", "Lucius Warren", "Guy Guzman", "Hedley Donovan", "Adrian Moore", "Dante Clayton", "Kyle Charles", "Ryder Copeland", "Cyrus Harding", "Darius Reed", "Linus Anderson", "Bert Soto", "Keefe Sears", "Ray Duke", "Jermaine Larson", "Vincent Rodriquez", "Amir Jensen", "Troy Dorsey", "Lucas Lyons", "Wade Oliver", "Hamish Mcneil", "Alden Waller","Sawyer Montoya","Phelan Knapp","Magee Maldonado","Dustin Hines","Paki Parker","Colin Berry","Harper Stanley","Dillon Mayo","Hamish Moody","Aidan Hensley","Mark Trujillo","Barrett Stephenson","Ross Humphrey","Adrian Cotton","Daquan Clemons","Hall Grimes","Guy Alford","Reed Bond","Barry Vaughn","Kenyon Santos"};
    public String name;
    public int passcode;

    public UserData()
    {
        int id = (int)(Math.random()*99 + 1);
        name = username[id];
        passcode = id+1;
    }

}
