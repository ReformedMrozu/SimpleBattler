package com.company.main.models;

import com.company.main.services.Client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Player {
    public int health;
    public int gold;
    public int playerNumber;
    public Client cl;
    //public int [] heroCounter = {0,0,0,0,0,0};
    public ArrayList<Integer> listOfHeroes = new ArrayList<>();
    public ArrayList<Integer> heroesOnField = new ArrayList<>();

    // set ups values
    public Player(int hp, int g){
        this.health = hp;
        this.gold = g;
        cl = new Client();
        cl.connectToServer();
        playerNumber = cl.player;
        System.out.println("Im player no: " + playerNumber);
    }

}