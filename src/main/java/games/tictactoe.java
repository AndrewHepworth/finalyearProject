package games;

import model.nn;
import model.nnSetup;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class tictactoe {
    private int boardX;
    private int boardY;
    private String[][] board;
    private int[][] gBoard;
    private String[] players;
    private String currentPlayer;
    private int maxDepth;
    private int maxNumberOfGames;
    private int heuristicSelecter;
    private int win;
    private int draw;
    private int loss;
    private int bmove;
    private double DataSetPlayerOne = 1;
    private double DataSetPlayerTwo = -1;
    private boolean playerTwoFormat = false;
    private boolean actionOutput = false;
    private double actionNumber;
    private nn customHeuristicNN;
    private nn customPlayerNN;
    private nn defaultHeuristicNN;
    private nn getDefaultPlayerNN;
    private boolean isCustom = false;



    public void tictactoe() throws IOException, ParseException {
        this.players = new String[2];
        this.players[0]="X";
        this.players[1]="O";
        this.board = new String[3][3];
        this.gBoard = new int[3][3];
        this.boardX = 3;
        this.boardY = 3;
        this.maxDepth = 1;
        this.maxNumberOfGames=100;
        this.heuristicSelecter = 1;
        dynamicGenerateBoard(board,boardX,boardY);
        startGame();
    }

    public void startGame() throws IOException, ParseException {
        //Get user to declare size of required board
        boolean type = true;
        type = true;
        boolean exit = false;
        while(!exit) {
            type = true;
            this.board = new String[3][3];
            this.gBoard = new int[3][3];
            this.boardX = 3;
            this.boardY = 3;
            System.out.println("--------------------------");
            System.out.println("Type In Game Type you want:");
            System.out.println("0: Human Vs Computer(MiniMax)");
            System.out.println("1: Human Vs Computer(Alpha-Beta)");
            System.out.println("2: Human Vs Computer(Random)");
            System.out.println("3: Human Vs Artificial Neural Network");
            System.out.println("4: Computer(Minimax) Vs Computer(Minimax) ");
            System.out.println("5: Computer(Random and Alpha-Beta) Vs Computer(Minimax) ");
            System.out.println("6: Computer(Neural neuralNetwork) vs Computer(Minimax)");
            System.out.println("---------Testing Area---------");
            System.out.println("7:  Simulate games of Minimax Vs Random(Produces File)");
            System.out.println("8:  Simulate games of Alpha-Beta Vs Random(Produces File)");
            System.out.println("9: Simulate games of Alpha-Beta Vs Minimax(Produces File)");
            System.out.println("10: Simulate games of Minimax(Part Random) Vs Random");
            System.out.println("11: Simulate games of Alpha-beta(Part Random) Vs Random");
            System.out.println("12: Simulate games of Neural Network vs Minimax(Produces File");
            System.out.println("13: Simulate games of Alpha-Beta vs Neural Network(Produces FIle)");
            System.out.println("14: Simulate games of Neural Network vs Minimax(Part-Random)(Produces FIle)");
            System.out.println("15: Simulate games of Neural Network vs random(Produces FIle)");
            System.out.println("16: Simulate games of random vs Minimax(Produces File)");
            System.out.println("17: Return to main menu");
            Scanner typeOfGame = new Scanner(System.in);
            int gameNumber = 0;

            while (type) {
                try {
                    String numberSelected = typeOfGame.nextLine();
                    gameNumber = Integer.parseInt(numberSelected);
                    if (gameNumber >= 0 && gameNumber < 18) {
                        type = false;
                    } else {
                        System.out.println("Enter a valid Number");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Enter a valid Number");
                    type = true;
                }
            }

//        generateGameDataBoard();

            if (gameNumber < 17) {
                typeGame(gameNumber);
            } else {
                exit = true;
            }
        }
    }
    public void typeGame(int number) throws IOException, ParseException {
        //this will be where the type of game will be declared
        if(number == 0){
            dynamicHumanVsMinMax(false);
        }
        if(number == 1){
            dynamicHumanVsMinMax(true);
        }
        if(number == 2){
            dynamicHumanVsAIrandom();
        }
        if(number == 3){
//            dynamicMiniMaxVsMinMax();
            advancedSettings();
            dynaminHumanVsNN();
        }
        if(number == 4){
//            humanVsAIrandom();
            dynamicMiniMaxVsMinMax(false,false);
        }
        if(number == 5){
            dynamicMiniMaxVsMinMax(true,true);

        }
        if(number == 6){
//            aiVsAiNeural();
            advancedSettings();
            dynamicMinimaxVsRandom(false,false,true,true,false,false,false,false,false);
        }
        if(number == 7){
//            HumanVsNeural();
            gameSettings();
            dataRepresentation();
            dynamicMinimaxVsRandom(false,false,false,false,false,false,true,true,false);
        }
        if(number == 8){
//            HumanVsNeural();
            gameSettings();
            dataRepresentation();
            dynamicMinimaxVsRandom(true,false,false,false,false,false,true,true,false);

        }
        if(number == 9){
            gameSettings();
            dataRepresentation();
            dynamicMinimaxVsRandom(true,false,true,false,false,false,false,true,false);
        }
        if(number == 10){
            gameSettings();
            dataRepresentation();
            dynamicMinimaxVsRandom(false,true,false,false,false,false,false,true,false);
        }
        if(number == 11){
            gameSettings();
            dataRepresentation();
            dynamicMinimaxVsRandom(true,true,false,false,false,false,true,true,false);
        }
        if(number == 12){
            gameSettings();
            advancedSettings();
            dynamicMinimaxVsRandom(false,false,true,true,false,false,false,false,false);
        }
        if(number== 13){
            gameSettings();
            advancedSettings();
            dynamicMinimaxVsRandom(true, false, false,false,true,false,false,false,false);
        }
        if(number== 14){
            gameSettings();
            advancedSettings();
            dynamicMinimaxVsRandom(false, false, true,true,true,true,false,false,false);
        }
        if(number== 15){
            gameSettings();
            advancedSettings();
            dynamicMinimaxVsRandom(false, false, false,true,false,false,true,false,false);
        }
        if(number == 16){
            gameSettings();
            dataRepresentation();
            dynamicMinimaxVsRandom(false,false,true,false,false,false,false,false,true);
        }
    }

    private void dynaminHumanVsNN() {
        String endResult = "";
        boolean suggest = false;
        int wantsSuggest= -1;
        wantsSuggest =  wantsSuggestions(1,true,suggest, 0);
        while (true) {
            //Cpu Turn
            System.out.println("-----Computer Turn-----");
            endResult = neuralNetworkTurn(players[0]);
            //Check win
//            dynamicGenerateGameBoard(gBoard, boardX, boardY);
            dynamicGenerateBoard(board,boardX,boardY);
            if (endResult.length() > 0 && endResult.contains("1")) {
                System.out.println(" Player " +endResult+" won");
                break;
            }
            if (endResult.length() > 0 && endResult.contains("Draw")) {
                System.out.println(endResult);
                break;
            }
            //Player Turn
            //This part declares the turns
            System.out.println("-----User Turn-----");
            if(wantsSuggest > 0)
            {
                System.out.println("Suggested Position: "+wantsSuggestions(1,false,true,wantsSuggest));
            }
            endResult = userTurn(1);
            //Check win
            if (endResult.length() > 0 && endResult.contains("2")) {
                System.out.println(endResult + " Player " +endResult+" won");
                break;
            }
            if (endResult.length() > 0 && endResult.contains("Draw")) {
                System.out.println(endResult);
                break;
            }
        }
    }

    private void advancedSettings() throws IOException, ParseException {
        boolean error = true;
        nnSetup load = new nnSetup();
        Scanner scan = new Scanner(System.in);
        String userInput = "";
        System.out.println("Type in the neural network Json file? y/n");
        while(error){
            userInput = scan.nextLine();
            if(userInput.contains("y")){
                this.isCustom = true;

                System.out.println("Remember if your neural network is trained to be player 1");
                //This will load the ANN player
                this.customPlayerNN = load.loadNetwork();
                error = false;

            }
            else if(userInput.contains("n")){
                error = false;
            }
            else{
                System.out.println("Try again");
            }
        }
    }

    private void dataRepresentation() {
        boolean error = true;
        Scanner scan = new Scanner(System.in);
        String userInput = "";
        while(error){
            System.out.println("Would you like to change the way the data will be formatted? y/n");
            userInput = scan.nextLine();
            if(userInput.contains("y")){
                System.out.println("Select the Data set you want to form from the Game:");
                System.out.println("1: Generate Dataset to Focus on player 1 (INPUT: board before play, OUTPUT: board after play)");
                System.out.println("2: Generate Dataset to Focus on player 2 (INPUT: board before play, OUTPUT: board after play)");
                System.out.println("3: Generate Dataset based on player 1's action (INPUT: board state before play, OUTPUT: action taken)");
                System.out.println("4: Generate Dataset based on player 2's action (INPUT: board state before play, OUTPUT: action taken)");
                System.out.println("5: Default");
                while(error) {
                    try {
                        userInput = scan.nextLine();
                        int selection = Integer.parseInt(userInput);
                        if (selection > 0 && selection < 5) {
                            changeDataSet(selection);
                            error = false;
                        }else if(selection == 5){
                            error = true;
                        }
                        else {
                            System.out.println("Invalid number was given");
                            error = true;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Enter a valid number");
                    }
                }
            }
            else if(userInput.contains("n")){
                error = false;
            }
        }
    }

    private void changeDataSet(int selection) {
        boolean error = true;
        Scanner scan = new Scanner(System.in);
        String userInput = "";
        while(error) {
            try{
                System.out.println("What Value Is player One? ");
                userInput = scan.nextLine();
                double playerOneValue = Double.parseDouble(userInput);
                this.DataSetPlayerOne = playerOneValue;
                error = false;

            }catch (NumberFormatException e){
                System.out.println("Enter Valid Number");
            }

        }
        error = true;
        while(error) {
            try{
                System.out.println("What Value Is player Two? ");
                userInput = scan.nextLine();
                double playerTwoValue = Double.parseDouble(userInput);

                this.DataSetPlayerTwo = playerTwoValue;
                error = false;
            }catch (NumberFormatException e){
                System.out.println("Enter Valid Number");
            }
        }
        switch (selection){
            case 1 :
                //
                this.playerTwoFormat = false;
                this.actionOutput = false;
                break;
            case 2:
                //
                this.playerTwoFormat = true;
                this.actionOutput = false;
                break;
            case 3:
                //
                this.playerTwoFormat = false;
                this.actionOutput = true;
                break;
            case 4:
                //
                this.playerTwoFormat = true;
                this.actionOutput = true;
                break;

        }
    }

    private void gameSettings(){
        int i =0 ;
        int j =0;
        boolean type =true;
        Scanner userSpec = new Scanner(System.in);
        while(type){
            try{
                System.out.println("Enter the Height of the Game");
                String height = userSpec.nextLine();
                i = Integer.parseInt(height);
                type = false;
            } catch(NumberFormatException e){
                System.out.println("Wrong type entered. Please Enter a Proper Value");
                type = true;
            }
        }

        type = true;
        while(type){
            try{
                System.out.println("Enter the Width of the Game");
                String width = userSpec.nextLine();
                j = Integer.parseInt(width);
                type = false;
            } catch(NumberFormatException e){
                System.out.println("Wrong type entered. Please Enter a Proper Value");
                type = true;
            }
        }
        this.boardX = i;
        this.boardY = j;
        this.board = new String[boardX][boardY];
        this.gBoard = new int[boardX][boardY];
        type = true;
        while(type){
            System.out.println("How many games to Simulate");
            String userInput = "";
            userInput = userSpec.nextLine();
            try{
                int games = Integer.parseInt(userInput);
                this.maxNumberOfGames = games;
                type = false;
            }
            catch (NumberFormatException e){
                System.out.println("Enter a valid number");
                type = true;
            }
        }

    }

    public String dynamicCheckWin(int[][] board, int boardSizeX, int boardSizeY) {
        //This method checks all possibilities of winning relative to a piece on the board
        int playerCheck;
        int reversePlayerCheck;

        //Scan for X or O
        for (int i = 0; i < boardSizeX; i++) {
            for (int j = 0; j < boardSizeY; j++) {
                //Need to check if out of bounds So need exception
                // X == 1 or O == 2

                if (board[i][j] == 1 || board[i][j] == 2) {
                    //assign player number from board to variables
                    playerCheck = board[i][j];
                    reversePlayerCheck = board[i][j];

                    //Horizontal check
                    for (int k = 1; k < 3; k++) {
                        //Horizontal check
                        //Right hand side check
                        if(i+k < boardSizeX) {
                            if (board[i + k][j] == board[i][j]) {
                                playerCheck += 2;
                            }
                        }
                        //Left hand side check
                        if(i-k >= 0) {
                            if (board[i - k][j] == board[i][j]) {
                                reversePlayerCheck += 2;
                            }
                        }
                    }
                    //Player One wins
                    if (playerCheck == 5 || reversePlayerCheck == 5) {
                        return "1";
                    }

                    //Player Two wins
                    if (playerCheck == 6 || reversePlayerCheck == 6) {
                        return "2";
                    } else {
                        //Reset the check variables
                        playerCheck = board[i][j];
                        reversePlayerCheck = board[i][j];
                    }

                    //Vertical Check
                    for (int k = 1; k < 3; k++) {
                        //Up side check
                        if(j-k >=0) {
                            if (board[i][j - k] == board[i][j]) {
                                playerCheck += 2;
                            }
                        }
                        //Down side check
                        if(j+k < boardSizeY) {
                            if (board[i][j + k] == board[i][j]) {
                                reversePlayerCheck += 2;
                            }
                        }
                    }
                    //Player One wins
                    if (playerCheck == 5 || reversePlayerCheck == 5) {
                        return "1";
                    }

                    //Player Two wins
                    if (playerCheck == 6 || reversePlayerCheck == 6) {
                        return "2";
                    } else {
                        //Reset the check variables
                        playerCheck = board[i][j];
                        reversePlayerCheck = board[i][j];
                    }

                    //Diagonal Check (First Diagonal) NW- SE
                    for (int k = 1; k < 3; k++) {
                        //North West  check
                        if(i - k >=0 && j-k >=0) {
                            if (board[i - k][j - k] == board[i][j]) {
                                playerCheck += 2;
                            }
                        }
                        //South East check
                        if(i+k < boardSizeX && j+k < boardSizeY ) {
                            if (board[i + k][j + k] == board[i][j]) {
                                reversePlayerCheck += 2;
                            }
                        }
                    }
                    //Player One wins
                    if (playerCheck == 5 || reversePlayerCheck == 5) {
                        return "1";
                    }

                    //Player Two wins
                    if (playerCheck == 6 || reversePlayerCheck == 6) {
                        return "2";
                    } else {
                        //Reset the check variables
                        playerCheck = board[i][j];
                        reversePlayerCheck = board[i][j];
                    }

                    //Second diagonal check NE - SW
                    for (int k = 1; k < 3; k++) {
                        //North East check
                        if(i - k >= 0 && j+k < boardSizeY) {
                            if (board[i - k][j + k] == board[i][j]) {
                                playerCheck += 2;
                            }
                        }
                        //South West Check
                        if(i + k < boardSizeX && j-k >= 0) {
                            if (board[i + k][j - k] == board[i][j]) {
                                reversePlayerCheck += 2;
                            }
                        }
                    }
                    //Player One wins
                    if (playerCheck == 5 || reversePlayerCheck == 5) {
                        return "1";
                    }

                    //Player Two wins
                    if (playerCheck == 6 || reversePlayerCheck == 6) {
                        return "2";
                    } else {
                        //Reset the check variables
                        playerCheck = board[i][j];
                        reversePlayerCheck = board[i][j];
                    }


                }
            }
        }
        //Draw Case
        //when the board is full of 1's or 2's and no 0's
        for (int i = 0; i < boardSizeX; i++) {
            for (int j = 0; j < boardSizeY; j++) {
                if (board[i][j] == 0) {
                    //Return nothing -Game Still in play
                    return "";
                } else if (boardSizeX - 1 == i && boardSizeY - 1 == j) {
                    //When the entire has been scanned
                    return "Draw";
                }
            }
        }
        return "";
    }


    public void dynamicGenerateGameBoard(int[][] gBoard, int boardSizeX, int boardSizeY) {
        System.out.println("------");
        for (int i = 0; i < boardSizeX; i++) {
            for (int j = 0; j < boardSizeY; j++) {
                if(gBoard[i][j] == 0){

                }
                System.out.print("" + gBoard[i][j] + "  ");
            }
            System.out.println("   ");

        }
        System.out.println("------");
    }

    public void dynamicUpdateBoard(int position, String currentPlayer, String[][] board,int[][] gBoard,int boardSizeX,int boardSizeY, boolean algorithm){
        //This puts the piece where the player wanted it
        int positionMarker = 0;
        for(int i = 0; i<boardSizeX ; i++){
            for(int j = 0;j< boardSizeY; j++){
                positionMarker += 1;
                if(positionMarker == position){
                    board[i][j] = currentPlayer;
                    if(currentPlayer.equals("X")) {
                        gBoard[i][j] = 1;
                    }
                    else if(currentPlayer.equals("O")) {
                        gBoard[i][j] = 2;
                    }
                    else if(currentPlayer.equals("")){
                        //Delete / Undo
                        board[i][j] = "";
                        gBoard[i][j] = 0;
                    }
                }

            }
        }
        //After a new move is placed, generate the new board
        if(!algorithm) {
            dynamicGenerateBoard(board, boardSizeX, boardSizeY);
        }

    }

    private boolean dynamicCheckValidMove(int chosenNumber, int[][] gameBoard, int boardSizeX, int boardSizeY) {
        //Checks that where the selected position is free
        int index = 0;
        for (int i = 0; i < boardSizeX; i++) {
            for (int j = 0; j < boardSizeY; j++) {
                index += 1;
                if(index == chosenNumber){
                    if(gameBoard[i][j] > 0){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void dynamicGenerateBoard(String[][] board, int boarderSizeX, int boarderSizeY ){
        //The visible board to the user
        //gets rid of initial nulls
        for (int i = 0; i < boarderSizeX; i++) {
            StringBuilder boarderCounter = new StringBuilder();
            StringBuilder boardGameRow = new StringBuilder();
            for (int j = 0; j < boarderSizeY; j++) {
                if(board[i][j] == null || gBoard[i][j] == 0) {
                    board[i][j] = " ";
                }
                //  System.out.print(this.board[i][j] +"|");


                if( j == boarderSizeY - 1) {
                    boarderCounter.append("-");
                    boardGameRow.append(board[i][j]);
                }
                else{
                    boarderCounter.append("-+");
                    boardGameRow.append(board[i][j]);
                    boardGameRow.append("|");
                }
            }
            if(i < boarderSizeX - 1) {
                System.out.println(boardGameRow.toString());
                System.out.println(boarderCounter.toString());
            }
            else{
                System.out.println(boardGameRow.toString());
            }
        }
        System.out.println("------");
    }

    private void dynamicHumanVsAIrandom() {
        String endResult = "";
        int totalNumberOfPositions = boardX * boardY;
        boolean suggest = false;
        int wantsSuggest= -1;
        wantsSuggest =  wantsSuggestions(1,true,suggest, 0);
        while(true) {
            Scanner uInput = new Scanner(System.in);
            boolean input = true;
            int position = 0;
            boolean cpuInput = true;
            System.out.println("-----USER TURN -----");
            dynamicGenerateBoard(board,boardX,boardY);
            if(wantsSuggest > 0)
            {
                System.out.println("Suggested Position: "+wantsSuggestions(1,false,true,wantsSuggest));
            }

            //This part declares the turns
            endResult = userTurn(0);
            if(endResult.equals("Draw")){
                System.out.println("It is a draw");
                break;
            }
            if(endResult.length() > 0 && !endResult.equals("Draw")){
                System.out.println(endResult +" Player 1 won");
                break;
            }

            //Computer Plays THIS IS WHERE THE CPU USES AI
            System.out.println("-----Random Turn-----");
            endResult = randomTurn(totalNumberOfPositions,players[1], false);
            if(endResult.length() > 0){
                System.out.println(endResult +" Player 2 won");
                break;
            }
        }
    }

    private String randomTurn(int totalNumberOfPositions, String player, boolean algo){
        boolean input = true;
        int position = 0;
        boolean cpuInput = true;
        String endResult ="";
        int cpuTurn =0;
        ArrayList<Integer> freePositions = new ArrayList<Integer>();
        int counter = 0;
        int comuputerPosition=0;
        for(int i = 0; i< boardX ; i++){
            for(int j = 0; j<boardY; j++){
                counter++;
                if(gBoard[i][j] == 0){
                    freePositions.add(counter);
                }
            }
        }
        while(cpuInput) {


            Random rand = new Random();
            try {
                cpuTurn = rand.nextInt(freePositions.size());
                comuputerPosition = freePositions.get(cpuTurn);
            }catch(IllegalArgumentException e){
                System.out.println(cpuTurn);
                dynamicGenerateBoard(board,boardX,boardY);
                dynamicGenerateGameBoard(gBoard,boardX,boardY);
            }
            if(!dynamicCheckValidMove(comuputerPosition,gBoard, boardX, boardY)) {
                cpuInput = true;
                freePositions.remove(cpuTurn);
            }
            else{
                cpuInput = false;
                dynamicUpdateBoard(comuputerPosition, player, this.board, this.gBoard, boardX, boardY, algo);
                endResult = dynamicCheckWin(gBoard,boardX, boardY);
            }
        }
        this.actionNumber = comuputerPosition;
        return endResult;

    }

    private String neuralNetworkTurn(String player){
        int position;
        String endResult = "";
        if(!isCustom){
            double[] input = encryptInput(this.getDefaultPlayerNN);
            position =  decryptOutput(this.getDefaultPlayerNN.giveInput(input));
        }
        else{
            double[] input = encryptInput(this.customPlayerNN);
            position =  decryptOutput(this.customPlayerNN.giveInput(input));
        }
        if(dynamicCheckValidMove(position,gBoard,boardX,boardY)){
            dynamicUpdateBoard(position,player,board,gBoard,boardX,boardY,true);
            endResult = dynamicCheckWin(gBoard,boardX,boardY);
        }
        return endResult;
    }

    private int decryptOutput(double[] input) {
        int position = 0;
        //process the data
        for(int i = 0; i < input.length ; i ++){
            if(input[i] > 0.5){
                input[i] = 1;
                position = i+1;
            }
            else{
                input[i] = 0;
            }
        }
        return position;
    }

    private double[] encryptInput(nn neuralNet) {
        double[] input = new double[neuralNet.getInputLayerSize()];
        int index= 0;
        for(int i = 0 ; i < boardX ; i++){
            for( int j = 0; j< boardY ; j++){
                if(gBoard[i][j] == 1) {
                    input[index] = this.DataSetPlayerOne;
                }
                if(gBoard[i][j] == 2){
                    input[index] = this.DataSetPlayerTwo;
                }
                if(gBoard[i][j] == 0){
                    input[index] = 0;
                }
                index++;
            }
        }
        return input;
    }

    public int dynamicMiniMax(String[][] board, int[][] gameBoard, int boardSizeX, int boardSizeY, boolean MaxizingPlayer, int player, int depth, String[] aPlayers, int originalPlayer){
        int score;
        int mPosition =0;

        int bestScore;
        //Terminal Node
        if(dynamicCheckWin(gameBoard, boardSizeX, boardSizeY).equals("1") && originalPlayer == 0){
            //Player one wins and is the maximiser
            score = 25 -depth;
            return score ;
        }
        if(dynamicCheckWin(gameBoard, boardSizeX, boardSizeY).equals("2")&& originalPlayer == 0){
            score = -25+ depth;
            return score ;
        }

        if(dynamicCheckWin(gameBoard,boardSizeX,boardSizeY).equals("Draw") && originalPlayer == 0){
            score = 0;
            return score ;
        }
        if(dynamicCheckWin(gameBoard, boardSizeX, boardSizeY).equals("1") && originalPlayer == 1){
            score = -25 +depth;
            return score ;
        }
        if(dynamicCheckWin(gameBoard, boardSizeX, boardSizeY).equals("2")&& originalPlayer == 1){
            score = 25 - depth;
            return score ;
        }

        if(dynamicCheckWin(gameBoard,boardSizeX,boardSizeY).equals("Draw") && originalPlayer == 1){
            score = 0;
            return score ;
        }

        if(MaxizingPlayer){
            bestScore = -1000;

            for(int i = 0; i<boardSizeX ; i++){
                for(int j = 0;j < boardSizeY ; j++){
                    mPosition += 1;
                    if(gameBoard[i][j] == 0){
                        dynamicUpdateBoard(mPosition, aPlayers[player], board, gameBoard, boardSizeX, boardSizeY, true);

                        bestScore = Math.max(dynamicMiniMax(board,gameBoard,boardSizeX,boardSizeY,false, (player+1)%2, depth +1,aPlayers,originalPlayer ), bestScore);


                        dynamicUpdateBoard(mPosition, "", board, gameBoard,boardSizeX,boardSizeY,true);

                    }
                }
            }
            return bestScore;
        }
        else{
            bestScore = 1000;
            for(int i = 0; i<boardSizeX ; i++){
                for(int j = 0;j < boardSizeY ; j++){
                    mPosition += 1;
                    if(gameBoard[i][j] == 0){

                        dynamicUpdateBoard(mPosition, aPlayers[player], board, gameBoard, boardSizeX, boardSizeY, true);

                        bestScore = Math.min(dynamicMiniMax(board,gameBoard,boardSizeX,boardSizeY,true, (player+1)%2, depth +1,aPlayers,originalPlayer ), bestScore);

                        dynamicUpdateBoard(mPosition, "", board, gameBoard,boardSizeX,boardSizeY,true);

                    }
                }
            }
            return bestScore;
        }


    }

    private int alphaBeta(int alpha, int beta, String[][] board, int[][] gameBoard,
                          int boardSizeX, int boardSizeY, boolean MaxizingPlayer, int player, int depth, String[] aPlayers, int originalPlayer){
        int bestScore;
        int score;
        int  mPosition = 0;
        //Teriminate
        if(dynamicCheckWin(gameBoard, boardSizeX, boardSizeY).equals("1") && originalPlayer == 0){
            //Player one wins and is the maximiser
            score = 25 -depth;
            return score ;
        }
        if(dynamicCheckWin(gameBoard, boardSizeX, boardSizeY).equals("2")&& originalPlayer == 0){
            score = -25+ depth;
            return score ;
        }

        if(dynamicCheckWin(gameBoard,boardSizeX,boardSizeY).equals("Draw") && originalPlayer == 0){
            score = 0;
            return score ;
        }
        if(dynamicCheckWin(gameBoard, boardSizeX, boardSizeY).equals("1") && originalPlayer == 1){
            score = -25 +depth;
            return score ;
        }
        if(dynamicCheckWin(gameBoard, boardSizeX, boardSizeY).equals("2")&& originalPlayer == 1){
            score = 25 - depth;
            return score ;
        }

        if(dynamicCheckWin(gameBoard,boardSizeX,boardSizeY).equals("Draw") && originalPlayer == 1){
            score = 0;
            return score ;
        }

        if(MaxizingPlayer){
            bestScore = -10000;
            for(int i =0; i< boardX ; i++){
                for(int j = 0; j<boardY ; j++){
                    mPosition ++;
                    if(gBoard[i][j] == 0){
                        dynamicUpdateBoard(mPosition, aPlayers[player], board, gameBoard, boardSizeX, boardSizeY, true);

                        bestScore = Math.max(alphaBeta(alpha,beta,board,gameBoard,boardSizeX,boardSizeY,false, (player+1)%2, depth + 1, aPlayers ,originalPlayer ), bestScore);

                        dynamicUpdateBoard(mPosition, "", board, gameBoard,boardSizeX,boardSizeY,true);
                        alpha = Math.max(alpha, bestScore);
                        if(beta < alpha ){
                            break;
                        }

                    }
                    if(beta < alpha ){
                        break;
                    }
                }
                if(beta < alpha ){
                    break;
                }
            }
            return bestScore;
        }
        else{
            bestScore = 10000;
            for(int i =0; i< boardX ; i++){
                for(int j = 0; j<boardY ; j++){
                    mPosition ++;
                    if(gBoard[i][j] == 0){
                        dynamicUpdateBoard(mPosition, aPlayers[player], board, gameBoard, boardSizeX, boardSizeY, true);

                        bestScore = Math.min(alphaBeta(alpha,beta, board,gameBoard,boardSizeX,boardSizeY,false, (player+1)%2, depth + 1, aPlayers,originalPlayer ), bestScore);

                        dynamicUpdateBoard(mPosition, "", board, gameBoard,boardSizeX,boardSizeY,true);
                        beta = Math.min(beta, bestScore);
                        if(beta < alpha ){
                            break;
                        }
                    }
                    if(beta < alpha ){
                        break;
                    }
                }
                if(beta < alpha ){
                    break;
                }
            }
            return bestScore;
        }
    }

    public int nextMove(String[][] board, int[][] gameBoard, int boardSizeX, int boardSizeY, int player, String[] allPlayers, boolean suggestions, boolean maximizer, boolean useAlpha){
        boolean max = true;
        String[][] newBoard = board;
        int[][] newGameBoard = gameBoard;

        int bestMoveVal = -1000;
        int alpha = -10000;
        int beta = 10000;
        int predVal;
        int nPosition = 0;
        int bestMovePosition = 0;
        for(int i = 0; i<boardSizeX ; i++){
            for(int j = 0;j < boardSizeY ; j++){
                nPosition += 1;
                if(gameBoard[i][j] == 0){
                    if(dynamicCheckValidMove(nPosition,newGameBoard,boardSizeX,boardSizeY)) {
                        dynamicUpdateBoard(nPosition, allPlayers[player], newBoard, newGameBoard, boardSizeX, boardSizeY, true);


                        if(useAlpha){
                            predVal = alphaBeta(alpha,beta,newBoard, newGameBoard, boardSizeX, boardSizeY, maximizer, (player + 1) % 2, 0, allPlayers, player);
                        }
                        else{
                            predVal = dynamicMiniMax(newBoard, newGameBoard, boardSizeX, boardSizeY, maximizer, (player + 1) % 2, 0, allPlayers, player);
                        }

                        dynamicUpdateBoard(nPosition, "", newBoard, newGameBoard, boardSizeX, boardSizeY, true);

                        if (predVal > bestMoveVal) {
                            bestMovePosition = nPosition;
                            bestMoveVal = predVal;
                            bmove = nPosition;
                        }
                    }
                }
            }
        }
        return bestMovePosition;

    }

    public String computerTurn(int player, boolean maximizer, boolean algo, boolean useAlpha){
        String endResult = "";
        boolean cpuInput = true;
        int cpuTurn = 0;
        while (cpuInput) {
            cpuTurn = nextMove(board, gBoard, boardX, boardY, player, players, false, maximizer, useAlpha);
            if (!dynamicCheckValidMove(cpuTurn, gBoard, boardX, boardY)) {
                cpuInput = true;
            } else {
                cpuInput = false;
                dynamicUpdateBoard(cpuTurn, players[player], this.board, this.gBoard, boardX, boardY, algo);
                endResult = dynamicCheckWin(gBoard, boardX, boardY);
                this.actionNumber = cpuTurn;
                return endResult;
            }
        }
        return "";
    }

    public String userTurn(int player){
        Scanner uInput = new Scanner(System.in);
        boolean input = true;
        int position = 0;
        String endResult;
        int totalNumberOfPositions = boardX * boardY;
        while (input) {
            //Need to catch  exception

            //User Turn
            if (position < 1 || position > totalNumberOfPositions) {
                //Ask player to type in again
                System.out.println("Put in a number ");
                position = uInput.nextInt();

                //Need to check that the move is legal
                if(!dynamicCheckValidMove(position,gBoard, boardX, boardY)){
                    System.out.println("Invalid Move");
                    position = 0;
                }
            } else {
                input = false;
                //User Plays
                dynamicUpdateBoard(position, players[player], this.board, this.gBoard, boardX, boardY, false );
                endResult = dynamicCheckWin(gBoard, boardX, boardY);
                return  endResult;
            }
        }
        return "";
    }

    private void dynamicHumanVsMinMax(boolean useAlpha) {
        String endResult = "";
        boolean suggest = false;
        int wantsSuggest= -1;
        wantsSuggest =  wantsSuggestions(1,true,suggest, 0);
        while (true) {
            //Cpu Turn
            System.out.println("-----Computer Turn-----");
            endResult = computerTurn(0, false,false, useAlpha);
            //Check win
//            dynamicGenerateGameBoard(gBoard, boardX, boardY);
            if (endResult.length() > 0 && endResult.contains("1")) {
                System.out.println(" Player " +endResult+" won");
                break;
            }
            if (endResult.length() > 0 && endResult.contains("Draw")) {
                System.out.println(endResult);
                break;
            }
            //Player Turn
            //This part declares the turns
            System.out.println("-----User Turn-----");
            if(wantsSuggest > 0)
            {
                System.out.println("Suggested Position: "+wantsSuggestions(1,false,true,wantsSuggest));
            }
            endResult = userTurn(1);
            //Check win
            if (endResult.length() > 0 && endResult.contains("2")) {
                System.out.println(endResult + " Player " +endResult+" won");
                break;
            }
            if (endResult.length() > 0 && endResult.contains("Draw")) {
                System.out.println(endResult);
                break;
            }
        }
    }

    private int wantsSuggestions(int player,boolean input, boolean suggestions, int type) {
        Scanner uInput = new Scanner(System.in);
        String wants;
        boolean nextInput =false;
        int totalNumberOfPositions = boardX * boardY;
        while (input) {
            System.out.println("Do you want suggestions? y/n ");
            wants = uInput.nextLine();
            if(wants.contains("y")){
                suggestions = true;
                input = false;
                nextInput = true;
            }
            else if(wants.contains("n")){
                suggestions = false;
                input = false;
                nextInput = false;
            }
            else{
                System.out.println("Wrong input, try again.");
                input = true;
            }
        }
        while(nextInput){
            System.out.println("Which AI would you like to Assist you:");
            System.out.println("1: Minimax");
            System.out.println("2: Alpha-Beta");
            System.out.println("3: Artificial Neural neuralNetwork");
            wants = uInput.nextLine();
            if(wants.contains("1")){
                type = 1;
                nextInput = false;
                return 1;
            }
            else if(wants.contains("2")){
                type = 2;
                nextInput = false;
                return 2;
            }
            else if(wants.contains("3")){
                type = 3;
                nextInput = false;
                return 3;
            }
            else{
                System.out.println("Wrong input, try again.");
                nextInput = true;
            }

        }
        if(suggestions){
            if(type == 1) {
                int nextPossible = nextMove(board, gBoard, boardX, boardY, player, players, suggestions, false,false);
                return nextPossible;
            }
            if(type == 2){

            }
            if(type == 3){

            }
        }
        else{
            return -1;
        }
        return -1;
    }

    private void dynamicMiniMaxVsMinMax(boolean useAlpha, boolean isPartRandom) {

        String endResult = "";
        Random rand = new Random();
        int doRandom = 0;
        int totalNumberOfPositions = boardX * boardY;
        while (true) {

            if(isPartRandom) {
                doRandom = rand.nextInt(100);
            }
            if(doRandom > 40){
                endResult = randomTurn(boardX*boardY, players[0],true);
                System.out.println("Random move");
            }
            else {
                endResult = computerTurn(0, false, false, useAlpha);
            }
//            dynamicGenerateGameBoard(gBoard, boardX, boardY);
            if (endResult.length() > 0) {
                System.out.println(endResult);
                break;
            }

            endResult = computerTurn(1,false,false, false);
//            dynamicGenerateGameBoard(gBoard, boardX, boardY);
            if(endResult.equals("Draw")){
                System.out.println("It is a draw");
                break;
            }
            //Draw Condition
            if(endResult.length() > 0 && !endResult.equals("Draw")){
                System.out.println(endResult +" Player 2 won");
                break;
            }
        }
    }

    private void dynamicMinimaxVsRandom(boolean isAlpha, boolean isPartRandom, boolean isVsMinimax, boolean isNN, boolean isVsNN, boolean isVsPartRandom , boolean isVsRandom, boolean isMax, boolean isRandom) throws IOException{
        long startTime =0;
        long finishTime = 0;
        long duration =0;
        long totalTimeNano =0;
        long totalTimeMilli= 0;
        int currentGameNumber =0;
        boolean notFinished = true;
        int turn = 0;
        //Need a file writer method
        String fileName = createFile();
        FileWriter saveData = new FileWriter(fileName);
        win= 0;
        draw = 0;
        loss = 0;
        String endResult = "";
        Random rand = new Random();
        int doRandom= 0;
        while(currentGameNumber < maxNumberOfGames) {
            System.out.println("Game: "+ currentGameNumber);
            while (notFinished) {
                if (turn % 2 == 0) {
                    //AI turn
                    if(!this.playerTwoFormat) {
                        //Before the Move
                        environmentConverter(saveData, false);
                    }
                    startTime = System.nanoTime();
                    if(isPartRandom){
                        doRandom = rand.nextInt(100);
                    }
                    if(isMax) {
                        if (doRandom > 50) {
                            endResult = randomTurn(boardX * boardY, players[0], true);
                        } else {
                            endResult = computerTurn(0, false, true, isAlpha);
                        }
                    }
                    else if(isNN){
                        endResult = neuralNetworkTurn(players[0]);
                    }
                    else if(isRandom){
                        endResult = randomTurn(boardX*boardY, players[0], true);
                    }
                    finishTime = System.nanoTime();
                    if(endResult.length()>0 && endResult.contains("1")){
                        win++;
                        notFinished = false;
                    }
                    if(endResult.contains("draw")){
                        draw++;
                        notFinished = false;
                    }
                    if(!this.playerTwoFormat) {
                        //After player one move
                        environmentConverter(saveData, true);
                        saveData.append("\n");
                    }

                } else {
                    //Random Turn Or AI turn
                    if(this.playerTwoFormat){
                        environmentConverter(saveData, false);
                    }
                    if(isVsRandom){
                        endResult = randomTurn(boardX * boardY, players[1], true);
                    }
                    if(isVsMinimax){
                        if(isVsPartRandom){
                            doRandom = rand.nextInt(100);
                        }
                        if (doRandom > 50) {
                            endResult = randomTurn(boardX * boardY, players[1], true);
                        } else {
                            endResult = computerTurn(1,false,true,false);
                        }
                    }
                    else if(isVsNN) {
                        endResult = neuralNetworkTurn(players[1]);
                    }

                    if(this.playerTwoFormat){
                        environmentConverter(saveData, true);
                        saveData.append("\n");
                    }

                    if(endResult.length()>0 && endResult.contains("2")){
                        loss++;
                        notFinished = false;
                    }
                    if(endResult.contains("Draw")){
                        draw++;
                        notFinished = false;
                    }
                }
                turn++;
                duration = (finishTime - startTime);
                totalTimeNano += duration;
            }

            currentGameNumber++;
            turn = 0;
            //Reset the board
            notFinished = true;
            this.board = new String[boardX][boardY];
            this.gBoard = new int[boardX][boardY];
        }
        saveData.close();
        //Print out the details after all games
        totalTimeMilli = TimeUnit.NANOSECONDS.toMillis(totalTimeNano);
        System.out.println("Win: "+win +",Loss: "+loss+", Draw: "+draw+ ", Average Turn time :"+(totalTimeMilli/maxNumberOfGames));

    }

    private String createFile(){
        boolean error = true;
        Scanner scan = new Scanner(System.in);
        String userInput = "";
        while(error){
            System.out.println("Would you like to choose a File name for the DataSet? y/n");
            userInput = scan.nextLine();
            if(userInput.contains("y")){
                System.out.println("What is the filename?");
                userInput = scan.nextLine();
                error = false;
            }
            else if(userInput.contains("n")){
                userInput = "customDataSetTicTacToe";
                error = false;
            }
        }
        return  userInput+".csv";
    }

    public void environmentConverter(FileWriter gameRecorder, boolean isOutput) throws IOException {
        int counter = 1;
        if(isOutput) {
            gameRecorder.append(";");
        }
        for(int i = 0; i< boardX ; i++){
            for(int j = 0; j < boardY ; j++){

                //IF
                if(this.actionOutput && isOutput && this.actionNumber != counter){
                    gameRecorder.append(0 + ",");
                }
                else if(this.actionOutput && isOutput && this.actionNumber == counter){
                    gameRecorder.append(1 + ",");
                }
                else {
                    if (gBoard[i][j] == 2) {
                        gameRecorder.append(String.valueOf(this.DataSetPlayerTwo + ","));
                    } else if (gBoard[i][j] == 1) {
                        gameRecorder.append(String.valueOf(this.DataSetPlayerOne + ","));
                    }else{
                        gameRecorder.append(0+",");
                    }
                }
                counter++;
            }
        }
    }



}
