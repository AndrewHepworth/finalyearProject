/*
        A Solver For Games Of Strategy
        Andrew Hepworth
        B421746
        Loughborough University
        Computer Science Final Year Project
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import games.checkers;
import games.tictactoe;
import model.dataSet;
import model.nn;
import model.nnSetup;

    public class main {
        public static void main(String[] args) throws Exception {

            bootMenu();
        }

        private static void bootMenu() throws Exception {
            boolean error = true;
            String menuNumber = "";
            boolean exit = false;
            while(!exit) {
                System.out.println("Choose One of the Following:");
                System.out.println("1: Tic tac toe");
                System.out.println("2: Checkers");
                System.out.println("3: ANN");
                System.out.println("4: Exit");
                boolean type = true;
                Scanner typeOfGame = new Scanner(System.in);
                int gameNumber = 0;
                while (type) {
                    try {
                        String numberSelected = typeOfGame.nextLine();
                        gameNumber = Integer.parseInt(numberSelected);
                        if (gameNumber > 0 && gameNumber < 5) {
                            type = false;
                        } else {
                            System.out.println("Enter a valid Number");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Enter a valid Number");
                        type = true;
                    }
                }

                if(gameNumber < 4) {
                    whichGame(gameNumber);
                }
                else{
                    exit = true;
                }

            }
        }
        private static void whichGame(int game) throws Exception {
            if(game == 1){
                tictactoe newTicGame = new tictactoe();
                newTicGame.tictactoe();
            }
            if(game == 2){
                checkers newCheckerGame = new checkers();
                newCheckerGame.checkers();
            }
            if(game == 3){


                nnSetup networks = new nnSetup();
                networks.startNeuralNetConfig();

            }
        }
    }

