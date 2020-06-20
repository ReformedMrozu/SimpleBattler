package com.company.main.services;

import com.company.main.models.Player;
import com.company.main.shared.GameLogic;
import com.company.main.ui.GUI;

import javax.swing.*;
import java.awt.event.ActionListener;

public class Game {
    public int playerId = 1; //temp change later
    public int enemyId;
    public GUI ui;
    public Player playerInstance;

    String[] champs = {"Wizard", "Knight", "Fairy", "Warden", "Dragon", "NEXT TURN"};

    public Game(){
        ui = new GUI(756,480);
        ui.setUpUi();
        buttonHandler();
        handlePlayer();

        Thread t = new Thread(this::handleTurn);
        t.start();

        ui.HEALTH = new JLabel("Your HP: \n" + playerInstance.health );
        ui.GOLD = new JLabel("Your Gold: \n" + playerInstance.gold );

        if(playerInstance.playerNumber == 1){
            ui.systemInfo.setText("->You are player " + playerInstance.playerNumber + "\n->Waiting for second player");
        } else {
            ui.systemInfo.setText("->You are player " + playerInstance.playerNumber + "\nBoth players connected");
        }
        ui.statusBar.add(ui.HEALTH);
        ui.statusBar.add(ui.GOLD);
    }

    public void handlePlayer(){ // handling player interactions
        playerInstance = new Player(100,100);
        if( playerInstance.playerNumber == 1){
            ui.isMyTurn = true;
        } else if( playerId  == 0){
            ui.gameLogs.setText("Error connecting to server!");
        }
        else {
            ui.isMyTurn = false;
        }
        GameLogic.toggleButtons(ui, playerInstance);
    }

    public void handleTurn() {
        GameLogic.checkIfEnoughGold(playerInstance, ui);
        int n = playerInstance.cl.csc.receiveButtonPressed();

        if( n == 69){
            //ui.frame.setVisible(false);
            JFrame win = new JFrame();
            win.setSize(200,40);
            win.setResizable(false);
            win.add(new JTextArea("VICTORY!"));
            win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            win.setVisible(true);
        } else {
            if(n != 5){
                playerInstance.listOfEnemyHeroes.add(playerInstance.Heroes.get(n));
            }
            ui.gameLogs.setText("Enemy clicked button #" + n);
            ui.isMyTurn = true;
            System.out.println("toggling in HT");
            GameLogic.toggleButtons(ui, playerInstance);
        }
    }

    public void buttonHandler(){
        ActionListener al = e -> {
            JButton buttonPressed = (JButton) e.getSource();
            String buttonText = buttonPressed.getText();

            int whichObject = GameLogic.getPressedButton(buttonText); // id of pressed button
            ui.gameLogs.setText("you bought " + champs[whichObject]);
            playerInstance.gold -= GameLogic.getHeroPrice(buttonText);
            ui.GOLD.setText("Your gold: " + playerInstance.gold);

            ui.gameLogs.setText("You clicked button #" + whichObject+  "Waiting for player #" + enemyId);

            ui.isMyTurn = !ui.isMyTurn;
            GameLogic.toggleButtons(ui, playerInstance);

            playerInstance.listOfHeroes.add(whichObject);
            GameLogic.showHeroes(playerInstance, ui);
            GameLogic.checkIfEnoughGold(playerInstance, ui);
            playerInstance.health -= GameLogic.calculateDMG(playerInstance);
            ui.HEALTH.setText("Your hp: "+playerInstance.health);
            ui.gameLogs.setText("You took " + GameLogic.calculateDMG(playerInstance) + " DMG");

            if(playerInstance.health < 1){
                JFrame defeat = new JFrame();
                defeat.setSize(200,40);
                defeat.setResizable(false);
                defeat.add(new JTextArea("Defeat!"));
                defeat.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                defeat.setVisible(true);
                playerInstance.cl.csc.sendButtonPressed(69);
            } else {
                playerInstance.cl.csc.sendButtonPressed(whichObject);
            }
            Thread th = new Thread(this::handleTurn);
            th.start();
        };
        ui.button1.addActionListener(al);
        ui.button2.addActionListener(al);
        ui.button3.addActionListener(al);
        ui.button4.addActionListener(al);
        ui.button5.addActionListener(al);
        ui.button6.addActionListener(al);
    }
}