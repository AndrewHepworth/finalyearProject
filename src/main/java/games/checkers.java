package games;

import model.nn;
import model.nnSetup;
import org.json.simple.parser.ParseException;

import java.util.*;
import java.io.*;
import java.util.concurrent.TimeUnit;

public class checkers {
    private int[][] gameBoardData;
    private String[][] board;
    private static final int FIRST_PLAYER = 1;
    private static final int FIRST_PLAYER_KING = 2;
    private static final int SECOND_PLAYER = 3;
    private static final int SECOND_PLAYER_KING = 4;
    private static final int EMPTY = 0;
    private int boardX;
    private int boardY;
    private final List<Integer> listOfAvailableMoves = new ArrayList<Integer>();
    private final List<Integer> ListOfPieces = new ArrayList<Integer>();
    private final List<String> moveOptions = new ArrayList<String>();
    private final HashMap<String, Integer> moveNumber = new HashMap<String, Integer>();
    private final HashMap<Integer, String> positionMove = new HashMap<>();
    private int maxTurn;
    private int maxNumberOfGames;
    private int maxDepth;
    private int heuristicSelecter;
    private int win;
    private int draw;
    private int loss;
    private boolean actionOutput = false;
    private double actionNumber;
    private double DataSetPlayerTwo = -1;
    private double DataSetPlayerOne = 1;
    private boolean playerTwoFormat = false;
    private nn customHeuristicNN;
    private nn customPlayerNN;
    private nn customPlayerAfterMoveNN;
    private boolean isCustom = false;
    private int pieceCountPlayerOne;
    private int pieceCountPlayerTwo;

    public void checkers() throws IOException, ParseException {
        //The row has to be even to make the verticals match up
        this.boardX = 8;
        this.boardY = 8;
        this.gameBoardData = new int[boardX][boardY];
        this.board = new String[boardX][boardY];
        this.maxTurn = 100;
        this.maxNumberOfGames= 100;
        this.heuristicSelecter = 1;
        this.maxDepth = 3;

        startCheckers();
    }

    public void startCheckers() throws IOException, ParseException {
        makeBoard( boardX,boardY );
        Boolean type;
        boolean exit = false;
        while(!exit) {
            type = true;
            System.out.println("--------------------------");
            System.out.println("Type In Game Type you want:");
            System.out.println("0: Human Vs Computer(MiniMax)");
            System.out.println("1: Human Vs Computer(Alpha-Beta)");
            System.out.println("2: Human Vs Computer(Random)");
            System.out.println("3: Computer( Minimax) Vs Computer(Minimax) ");
            System.out.println("4: Computer(Alpha-Beta) Vs Computer(Minimax) ");
            System.out.println("5: Random Vs Random");
            System.out.println("6: Computer(Random and Minimax) Vs Computer(Minimax) ");
            System.out.println("7: Computer(Random and Alpha-Beta) Vs Computer(Minimax) ");
            System.out.println("8: other(Solo)");
            System.out.println("---------Testing Area---------");
            System.out.println("9: Simulate games of Minimax Vs Random(Produces Data File)");
            System.out.println("10: Simulate games of Alpha-Beta Vs Random(Produces Data  File)");
            System.out.println("11: Simulate games of Alpha-Beta Vs Minimax(Produces Data File)");
            System.out.println("12: Simulate games of Minimax(Part random) vs random(Produces Data File)");
            System.out.println("13: Simulate games of Alpha-Beta(Part random) vs random(Produces Data File)");
            System.out.println("14: Custom");
            System.out.println("15: Change Neural Network Player");
            System.out.println("16: Return to main menu");
            Scanner typeOfGame = new Scanner(System.in);
            int gameNumber = 0;
            while (type) {
                try {
                    String numberSelected = typeOfGame.nextLine();
                    gameNumber = Integer.parseInt(numberSelected);
                    if (gameNumber >= 0 && gameNumber < 17) {
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

            if (gameNumber < 16) {
                typeGame(gameNumber);
            } else {
                exit = true;
            }
        }

    }

    private void typeGame(int game) throws IOException, ParseException {
        switch(game){
            case 0:
                //human vs AI minimax
                gameSettings(false);
                generateBoard();
                humanVsAI( false);
                break;
            case 1:
                //Human Vs Alpha
                gameSettings(false);
                generateBoard();
                humanVsAI(true);
                break;
            case 2:
                //Human v random
                generateBoard();
                humanVsRandom();
                break;
            case 3:
                // AI minimax vs random
                gameSettings(false);
                customGame(false,false,false,true,false,false,false,true,true);
                break;
            case 4:
                // Ai alpha beta vs Random
                gameSettings(false);
                customGame(true,false,false,true,false,false,false,true,true);
                break;

            case 5:
                //Random vs Random
                generateBoard();
                RandomVsRandom();
                break;

            case 6:
                //human like Computer Minimax vs Random
                gameSettings(false);
                generateBoard();
//                humanLikeMinivspureAI();
                customGame(false,true,true,true, false, false,false,false,true);

                break;

            case 7:
                //human like Computer Alpha-Beta vs minimax
                gameSettings(false);
                customGame(true,true,true,true, false,false,false,false,true);
                break;
            case 8:
                generateBoard();
                justHuman();
                break;

            case 9:
                //Area for testing minimax
                gameSettings(true);
                advancedSettings();
                customGame(false,false,false, false,false, false,false,true,true);
                break;

            case 10:
                gameSettings(true);
                advancedSettings();
                customGame(true,false,false, false, false, false,false,true,true);
                break;

            case 11:
                gameSettings(true);
                advancedSettings();
                generateBoard();
                customGame(true,false,true,false,false,false ,false,false,true);
                break;

            case 12:
                gameSettings(true);
                advancedSettings();
                generateBoard();
                customGame(false,true,false, false, false, false,false,true,true);
                break;

            case 13:
                gameSettings(true);
                advancedSettings();
                generateBoard();
                customGame(true,true,false,false, false ,false,false,true,true);
                break;

            case 14:
                //Custom Game
                gameSettings(true);
                advancedSettings();
                setUpGame();
                break;

            case 15:
                //
                networkSelector(false);
                break;

        }


    }

    private void setUpGame() throws IOException {
        boolean error = true;
        Scanner scan = new Scanner(System.in);
        String userInput = "";
        System.out.println("Choose Player 1:");
        System.out.println("1: Minimax");
        System.out.println("2: Alpha Beta");
        System.out.println("3: Neural Network");
        System.out.println("4: Minimax(Human-like)");
        System.out.println("5: Alpha-Beta(Human-Like)");
        boolean isMinMax = false;
        boolean isAlpha = false;
        boolean isNN = false;
        boolean isPartRandom= false;

        int player1 = 10;
        while(error) {
            try{
                userInput = scan.nextLine();
                player1 = Integer.parseInt(userInput);
                if(player1 > 0 && player1 < 5) {
                    error = false;
                }else{
                    System.out.println("Input Valid Number");
                    error = true;
                }


            }catch (NumberFormatException e){
                System.out.println("Invalid, try again");
            }
        }

        switch (player1){
            case 1:

                isMinMax = true;
                break;

            case 2:
                isAlpha = true;
                break;
            case 3:
                isNN = true;
                break;
            case 4:
                isMinMax = true;
                isPartRandom = true;
                break;
            case 5:
                isAlpha = true;
                isPartRandom = true;
                break;
        }
        int player2 = 10;

        error = true;
        userInput = "";
        System.out.println("Choose Player 2:");
        System.out.println("1: Minimax");
        System.out.println("2: Alpha Beta");
        System.out.println("3: Neural Network");
        System.out.println("4: Random");
        boolean isVSMinMax = false;
        boolean isVsAlpha = false;
        boolean isVsNN = false;
        boolean isVsPartRandom= false;
        boolean isRandom = false;

        while(error) {
            try{
                userInput = scan.nextLine();
                player2 = Integer.parseInt(userInput);
                if(player2 > 0 && player2 < 4) {
                    error = false;
                }else{
                    error = true;
                    System.out.println("Input Valid Number");
                }

            }catch (NumberFormatException e){
                System.out.println("Invalid, try again");
            }
        }

        switch (player2){
            case 1:

                isVSMinMax = true;
                break;

            case 2:
                isVsAlpha = true;
                break;
            case 3:
                isVsNN = true;
                break;
            case 4:
                isRandom = true;
                break;
        }

        customGame(isAlpha,isPartRandom,isVSMinMax,false,isNN,isVsNN, isVsAlpha, isRandom, isMinMax);
    }

    private void gameSettings(boolean totalGames) throws IOException, ParseException {
        Scanner typeOfGame = new Scanner(System.in);
        int gameNumber = 0;
        boolean type = true;
        System.out.println("Type in max depth between 0-7:");

        while(type){
            try{
                String numberSelected = typeOfGame.nextLine();
                gameNumber = Integer.parseInt(numberSelected);
                if(gameNumber >=0 && gameNumber < 8 ) {
                    type = false;
                }
                else{
                    System.out.println("Enter a valid Number");
                }
            } catch(NumberFormatException e){
                System.out.println("Enter a valid Number");
                type = true;
            }
        }

        this.maxDepth = gameNumber;

        type = true;
        System.out.println("Select Heuristic Option:");
        System.out.println("1: Board Pieces ");
        System.out.println("2: board Pieces + Captures ");
        System.out.println("3: board Pieces + Captures + pieces on opponent side");
        System.out.println("4: Neural Neural Network Evaluator");
        while(type){
            try{
                String numberSelected = typeOfGame.nextLine();
                gameNumber = Integer.parseInt(numberSelected);
                if(gameNumber >=1 && gameNumber < 5 ) {
                    type = false;
                }
                else{
                    System.out.println("Enter a valid Number");
                }
            } catch(NumberFormatException e){
                System.out.println("Enter a valid Number");
                type = true;
            }
        }
        this.heuristicSelecter = gameNumber;
        if(this.heuristicSelecter == 4){
            networkSelector(true);
        }

        if(totalGames) {
            type = true;
            System.out.println("Type in how many games to simulate:");
            while (type) {
                try {
                    String numberSelected = typeOfGame.nextLine();
                    gameNumber = Integer.parseInt(numberSelected);
                    type = false;
                } catch (NumberFormatException e) {
                    System.out.println("Enter a valid Number");
                    type = true;
                }
            }
            this.maxNumberOfGames = gameNumber;
        }
        else{
            this.maxNumberOfGames = 1;
        }

    }

    private void networkSelector(boolean isHeuristic) throws IOException, ParseException {
        boolean error = true;
        nnSetup load = new nnSetup();
        Scanner scan = new Scanner(System.in);
        String userInput = "";
        System.out.println("Type in the ANN json File : y/n");
        while(error){
            userInput = scan.nextLine();
            if(userInput.contains("y")){
                this.isCustom = true;
                if(isHeuristic){
                    //Provides Context to the user Input - This loads the heuristic evaluator
                    System.out.println("Load in the Heuristic ANN ");
                    this.customHeuristicNN = load.loadNetwork();

                }else{
                    System.out.println("Remember if your neural network is trained to be player 1 or 2");
                    System.out.println("Load in the Neural Network that gives the Selected Piece");
                    //This will load the ANN Selected Piece
                    this.customPlayerNN = load.loadNetwork();
                    System.out.println("load in the neural network that makes the move");
                    this.customPlayerAfterMoveNN = load.loadNetwork();
                }
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

    private void advancedSettings(){
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


    private void makeBoard(int row, int column) {
        //First two rows need declared
        //If the game is too small
        int playerOneCounter = 1;
        int playerTwoCounter = 1;
        for(int i = 0; i < row ; i++){
            for( int j = 0; j < column; j++){
                //initiate empty board
                this.board[i][j] = "   ";
                this.gameBoardData[i][j]= 0;
            }
        }
        //If the game is too small
        //Only gets one row of pawns
        //Fill the board with pawns
        for(int i = 0 ; i <row ; i++) {
            for (int j = 0; j < column; j += 2) {
                if (i == 1) {
                    this.board[i][j] = "w"+playerTwoCounter;
                    this.gameBoardData[i][j] = SECOND_PLAYER;
                    playerTwoCounter +=1;
                }
                if (i == 0 || i == 2){
                    if( j + 1 < column && row >4) {
                        this.board[i][j + 1] = "w" + playerTwoCounter;
                        this.gameBoardData[i][j + 1] = SECOND_PLAYER;
                        playerTwoCounter += 1;
                    }
                }
                if (i == row - 1 || i == row - 3) {
                    this.board[i][j] = "b"+playerOneCounter;
                    this.gameBoardData[i][j] = FIRST_PLAYER;
                    playerOneCounter +=1;
                }
                if (i == row - 2 && j + 1 < column && row >4) {
                    this.board[i][j + 1] = "b"+playerOneCounter;
                    this.gameBoardData[i][j+1]= FIRST_PLAYER;
                    playerOneCounter +=1;
                }
            }
        }
    }

    private void generateBoard() {
        StringBuilder border = new StringBuilder();
        for(int j = 0; j< this.boardY;j++){
            if(j != this.boardY - 1) {
                border.append("----");
            }
            else{
                border.append("-----");
            }
        }
        System.out.println(border.toString());
        for (int i = 0; i < this.boardX; i++) {
            StringBuilder borderBetweenRows = new StringBuilder();
            StringBuilder borderBetweenColumns = new StringBuilder();
            for (int j = 0; j < this.boardY; j++) {


                if(j == 0){
                    borderBetweenColumns.append("|");
//                    if(gameBoardData[i][j] == FIRST_PLAYER || gameBoardData[i][j] == SECOND_PLAYER){
//                        borderBetweenColumns.append(" "+board[i][j]);
//                    }
                    if(board[i][j].length() < 3){
                        borderBetweenColumns.append(" "+board[i][j]);
                    }

                    else {
                        borderBetweenColumns.append(board[i][j]);
                    }
                    borderBetweenColumns.append("|");
                    borderBetweenRows.append("|");
                }else if( j == this.boardY - 1) {
                    borderBetweenRows.append("---+---|");
//                    if(gameBoardData[i][j] == FIRST_PLAYER || gameBoardData[i][j] == SECOND_PLAYER){
//                        borderBetweenColumns.append(" "+board[i][j]);
//                    }
                    if(board[i][j].length() < 3){
                        borderBetweenColumns.append(" "+board[i][j]);
                    }
                    else {
                        borderBetweenColumns.append(board[i][j]);
                    }
                    borderBetweenColumns.append("|");
                }
                else{
                    borderBetweenRows.append("---+");
//                    if(gameBoardData[i][j] == FIRST_PLAYER || gameBoardData[i][j] == SECOND_PLAYER){
//                        borderBetweenColumns.append(" "+board[i][j]);
//                    }
                    if(board[i][j].length() < 3){
                        borderBetweenColumns.append(" "+board[i][j]);
                    }
                    else {
                        borderBetweenColumns.append(board[i][j]);
                    }
                    borderBetweenColumns.append("|");
                }
            }
            if(i < this.boardX - 1) {
                System.out.println(borderBetweenColumns.toString());
                System.out.println(borderBetweenRows.toString());
            }
            else{
                System.out.println(borderBetweenColumns.toString());
            }
        }
        System.out.println(border.toString());

    }

    private void generateGameDataBoard() {
        StringBuilder border = new StringBuilder();
        for(int j = 0; j< this.boardY;j++){
            if(j != this.boardY - 1) {
                border.append("---");
            }
            else{
                border.append("----");
            }
        }
        System.out.println(border.toString());
        for (int i = 0; i < this.boardX; i++) {
            StringBuilder borderBetweenRows = new StringBuilder();
            StringBuilder borderBetweenColumns = new StringBuilder();
            for (int j = 0; j < this.boardY; j++) {


                if(j == 0){
                    borderBetweenColumns.append("|");
                    borderBetweenColumns.append(" "+gameBoardData[i][j]);
                    borderBetweenColumns.append("|");
                    borderBetweenRows.append("|");
                }else if( j == this.boardY - 1) {
                    borderBetweenRows.append("--+--|");
                    borderBetweenColumns.append(" "+gameBoardData[i][j]);
                    borderBetweenColumns.append("|");
                }
                else{
                    borderBetweenRows.append("--+");
                    borderBetweenColumns.append(" "+gameBoardData[i][j]);
                    borderBetweenColumns.append("|");
                }
            }
            if(i < this.boardX - 1) {
                System.out.println(borderBetweenColumns.toString());
                System.out.println(borderBetweenRows.toString());
            }
            else{
                System.out.println(borderBetweenColumns.toString());
            }
        }
        System.out.println(border.toString());
    }

    private void humanVsAI(boolean isAlpha) {

        boolean userSuggestions = false;
        boolean notFinished = true;
        int suggestionSelector = getSuggestions(FIRST_PLAYER, true,userSuggestions, 0);
        int turn = 0;
        while(notFinished){
            if(turn % 2 == 0) {

                System.out.println("-----Human Turn-----");
                if(suggestionSelector > 0 ){
                    getSuggestions(FIRST_PLAYER, false, true,suggestionSelector);
                }
                System.out.println("Select Piece");
                notFinished = humanTurn(FIRST_PLAYER);
                generateBoard();
            }
            else{
                System.out.println("-----AI Turn -----");
                notFinished = aiTurn(SECOND_PLAYER, true, isAlpha,false);
                generateBoard();
//                generateGameDataBoard();
            }
            turn++;
        }
    }

    private int getSuggestions(int player, boolean  input, boolean userSuggestions, int type) {
        Scanner uInput = new Scanner(System.in);
        String wants;
        boolean nextInput = false;
        int totalNumberOfPositions = boardX * boardY;
        while (input) {
            System.out.println("Do you want suggestions? y/n ");
            wants = uInput.nextLine();
            if (wants.contains("y")) {
                userSuggestions = true;
                input = false;
                nextInput = true;
            } else if (wants.contains("n")) {
                userSuggestions = false;
                input = false;
                nextInput = false;
                return 0;
            } else {
                System.out.println("Wrong input, try again.");
                input = true;
            }
        }
        while (nextInput) {
            System.out.println("Which AI would you like to Assist you:");
            System.out.println("1: Minimax");
            System.out.println("2: Alpha-Beta");
            wants = uInput.nextLine();
            if (wants.contains("1")) {
                type = 1;
                nextInput = false;
            } else if (wants.contains("2")) {
                type = 2;
                nextInput = false;
            } else {
                System.out.println("Wrong input, try again.");
                nextInput = true;
            }
        }

        if (userSuggestions) {
            if (type == 1) {
                nextBestMove(gameBoardData,board,player,true,false,true);
                return 1;
            }
            if (type == 2) {

            }
            if (type == 3) {

            }
        }
        else{
            return -1;
        }
        return -1;

    }

    private void RandomVsRandom(){
        boolean notFinished = true;
        int turn = 0;
        while(notFinished){
            System.out.println("Select Piece");
            if(turn % 2 == 0) {
//                notFinished = humanTurn();
                notFinished = randomTurn(FIRST_PLAYER);
            }
            else{
                notFinished = randomTurn(SECOND_PLAYER);
            }
            turn++;
        }
    }

    private boolean humanTurn(int player){
        String pieceSelected;
        Scanner input = new Scanner(System.in);
        pieceSelected = input.nextLine();
        String optionTaken;

        boolean correctlySelected = selectPiece(pieceSelected, player);
//        System.out.println(correctlySelected);
        ArrayList<String> moves = getMoves(player);
        while(!correctlySelected){
            System.out.println("Incorrect");
            System.out.println("Possible Moves:");
            for(String move : moves){
                System.out.print(","+move);
            }
            pieceSelected = input.nextLine();
            correctlySelected =selectPiece(pieceSelected, player);
        }

        while(!checkAvailableMoves(pieceSelected,true)){
            System.out.println("piece selected has no moves");
            pieceSelected = input.nextLine();
            while(!correctlySelected){
                System.out.println("Incorrect:");
                System.out.print("Possible Moves ");
                for(String move : moves){
                    System.out.print(", "+move);
                }
                System.out.println();
                pieceSelected = input.nextLine();
                correctlySelected = selectPiece(pieceSelected, player);
            }
        }



        if(correctlySelected){
//            System.out.println("correct");
            //Want to check if there are available moves
            if(checkAvailableMoves(pieceSelected, true)){
//                System.out.println("checked");
                //If there are we show them to the user
                //Inside available moves we use nextMove, then nextMove will use force hops
                showAvailableMoves(pieceSelected);
                optionTaken = input.nextLine();
                playerMoveConverter(optionTaken, pieceSelected, 1);
                return !checkWin(false);
            }
        }
        else{
            System.out.println("Incorrect");
        }
        return true;
    }

    private boolean aiTurn(int player, boolean maximiser, boolean isAlpha, boolean showMoves) {
        nextBestMove(gameBoardData.clone(),board.clone(),player,maximiser, isAlpha, false);
        if(showMoves){
            generateBoard();
            return !checkWin(false);
        }else {
            return !checkWin(true);
        }
    }

    private boolean randomTurn(int player) {
        //Get list of robots current pieces on the board
        //Randomly select one
        //
        ListOfPieces.clear();
        moveOptions.clear();
        ArrayList<String> pieces = getMoves(player);
        Random rand = new Random();
        int compTurn = rand.nextInt(pieces.size());
        String selectedPiece = pieces.get(compTurn);
        Boolean rightPiece =selectPiece(selectedPiece,player);

        while(!rightPiece){
            compTurn = rand.nextInt(pieces.size());
            selectedPiece = pieces.get(compTurn);
            rightPiece = selectPiece(selectedPiece, player);
        }
        while(!checkAvailableMoves(selectedPiece,true) && moveOptions.isEmpty() ) {
            //Remove the piece if it has no available moves
            String remove = pieces.get(compTurn);
            pieces.remove(remove);
            try {
                compTurn = rand.nextInt(pieces.size());
            }catch(IllegalArgumentException e){
                generateBoard();
                generateGameDataBoard();
                System.out.println("Selected: "+selectedPiece);
                System.out.println("Moveoptions :" + moveOptions.size());
            }
            selectedPiece = pieces.get(compTurn);
            rightPiece =selectPiece(selectedPiece,player);
            while(!rightPiece){
                compTurn = rand.nextInt(pieces.size());
                selectedPiece = pieces.get(compTurn);
                rightPiece = selectPiece(selectedPiece, player);
            }
            checkAvailableMoves(selectedPiece,true);


        }
//        System.out.println("Moved piece: "+selectedPiece);
//        System.out.println("MoveOptions Size(Left/Right etc) "+moveOptions.size());
        compTurn = rand.nextInt(moveOptions.size());

        playerMoveConverter(moveOptions.get(compTurn),selectedPiece, player);
        return !checkWin(true);

    }

    private boolean neuralNetworkTurn(int player){
        if(!isCustom){
            double[] input = encryptInput(this.customPlayerNN);
            System.out.println(Arrays.toString(input));
            decryptOutput(this.customPlayerNN.giveInput(input),this.customPlayerAfterMoveNN.giveInput(input), player);
        }

        return !checkWin(true);
    }

    private double[] encryptInput(nn neuralNET) {
        double[] input = new double[32];
        int counter = 0;
        int counter2 = 0;
        int index = 0;
        for(int i = 0; i< boardX ; i++){
            counter++;
            for(int j = 0; j < boardY ; j++) {
                counter2++;
                //IF
                if (counter % 2 != 0 && counter2 % 2 == 0) {
                    if (gameBoardData[i][j] == SECOND_PLAYER) {
                        input[index] = this.DataSetPlayerTwo;
                    } else if (gameBoardData[i][j] == FIRST_PLAYER) {
                        input[index] = this.DataSetPlayerOne;

                    } else if (gameBoardData[i][j] == FIRST_PLAYER_KING) {
                        input[index] = this.DataSetPlayerOne * 2;
                    } else if (gameBoardData[i][j] == SECOND_PLAYER_KING) {
                        input[index] = this.DataSetPlayerTwo * 2;
                    } else {
                        input[index] = 0;
                    }
                    index++;
                }
                else if (counter % 2 == 0 && counter2 % 2 != 0){
                    if (gameBoardData[i][j] == SECOND_PLAYER) {
                        input[index] = this.DataSetPlayerTwo;
                    } else if (gameBoardData[i][j] == FIRST_PLAYER) {
                        input[index] = this.DataSetPlayerOne;

                    } else if (gameBoardData[i][j] == FIRST_PLAYER_KING) {
                        input[index] = this.DataSetPlayerOne * 2;
                    } else if (gameBoardData[i][j] == SECOND_PLAYER_KING) {
                        input[index] = this.DataSetPlayerTwo * 2;
                    } else {
                        input[index] = 0;
                    }
                    index++;
                }
            }
            counter2 = 0;
        }
        return input;
    }

    private void decryptOutput(double[] pieceOutput,double[] moveOutput, int player) {
        //FIRST NEED TO GET RID OF BAD INFO
//        System.out.println(Arrays.toString(output));
        HashMap<Integer, Integer> indexPosition = new HashMap<>();
        HashMap<Integer,Integer> moveIndex = new HashMap<>();
        double maxValueP = pieceOutput[0];
        double maxValueM = moveOutput[0];
        indexPosition.put(0,player);
        moveIndex.put(0,player);
        for(int i = 0 ; i < pieceOutput.length; i++){
            if(pieceOutput[i] > maxValueP){
                indexPosition.clear();
                indexPosition.put(i,player);
            }
        }
        for(int i = 0 ; i< moveOutput.length ; i++){
            if(moveOutput[i] > maxValueM){
                moveIndex.clear();
                moveIndex.put(i,player);
            }
        }




        String selectedPiece = "";
        int previousPosition =100;
        int nextPosition = 100;
        int position =0;
        int counter = 0;
        int counter2 = 0;
        int counter3= 0;
        for(int i = 0; i< boardX ; i++){
            counter++;
            for(int j = 0; j < boardY ; j++) {
                counter2++;
                if (counter % 2 != 0 && counter2 % 2 == 0) {
                    counter3++;
                    if (indexPosition.containsKey(counter3) && gameBoardData[i][j] != 0)  {
                        selectedPiece = board[i][j];
                        indexPosition.remove(counter3);
                        //Now we have the previous position and a list of a moves or moves.

                    }
                    if(moveIndex.containsKey(counter3) && gameBoardData[i][j] != 0){
                        nextPosition = getPosition(i,j);
                    }
                }
                else if(counter % 2 == 0 && counter2 % 2 !=0){
                    counter3++;
                    if (indexPosition.containsKey(counter3) && gameBoardData[i][j] != 0)  {
                        //This is the previous position
                        previousPosition = getPosition(i,j);
                        selectedPiece = board[i][j];

                        //remove the index from the list
                        indexPosition.remove(counter3);
                        //Now we have the previous position and a list of a moves or moves.

                    }
                    if(moveIndex.containsKey(counter3) && gameBoardData[i][j] != 0){
                        nextPosition = getPosition(i,j);
                    }
                }
            }
            counter2 = 0;
        }
        selectPiece(selectedPiece,player);
        //Check that the next move is a valid move
        if(checkAvailableMoves(selectedPiece,true)){
            System.out.println("here");
            if(positionMove.containsKey(nextPosition)){
                //Make the move
                playerMoveConverter(positionMove.get(nextPosition),selectedPiece,player);
            }
        }

    }

    private boolean selectPiece(String pieceSelected, int player) {
//        System.out.println(pieceSelected + ": " + player);
        //Check if selected piece is the player's piece
        this.ListOfPieces.clear();
        //NEEDS TO BE IN A WHILE LOOP TO CONTAIN ERROR OF SELECTION OF WRONG MOVE!
        if(player == FIRST_PLAYER || player == FIRST_PLAYER_KING){
            this.ListOfPieces.add(FIRST_PLAYER);
            this.ListOfPieces.add(FIRST_PLAYER_KING);
        }
        if(player == SECOND_PLAYER || player == SECOND_PLAYER_KING){
            this.ListOfPieces.add(SECOND_PLAYER);
            this.ListOfPieces.add(SECOND_PLAYER_KING);
        }

        for(int i = 0; i < boardX ; i++){
            for(int j = 0; j < boardY ; j++) {
                //See's whether piece is also a king or not
                for (Integer piece : ListOfPieces) {
//                    System.out.println(piece +"("+pieceSelected+"):" + gameBoardData[i][j] +"("+board[i][j]+")");
                    if (pieceSelected.contains(this.board[i][j]) && piece == this.gameBoardData[i][j]) {
                        this.ListOfPieces.clear();
                        this.ListOfPieces.add(piece);
                        //Need to check size of the list, should only be one within
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean checkAvailableMoves(String pieceSelected, boolean delete){
        if(delete) {
            listOfAvailableMoves.clear();
            moveOptions.clear();
            moveNumber.clear();
            positionMove.clear();
        }

        int position = 0;
        for(int i = 0 ; i< boardX; i++){
            for(int j = 0; j<boardY ; j++){
                //Checks Places for the player

                if(pieceSelected.equals(this.board[i][j])){
//                    for(Integer piece : ListOfPieces) {
                    int piece = gameBoardData[i][j];
                    //Moves for normal piece, first players king piece, second players king piece

                    if (piece == SECOND_PLAYER || piece == FIRST_PLAYER_KING || piece == SECOND_PLAYER_KING) {
                        //Move right
                        if (i + 1 < boardX && j + 1 < boardY) {
                            if (gameBoardData[i + 1][j + 1] == 0) {
                                position = getPosition(i + 1, j + 1);
                                this.listOfAvailableMoves.add(position);
                                if(piece == SECOND_PLAYER) {
                                    moveOptions.add("moveRight");
                                    moveNumber.put("moveRight",position);
                                    positionMove.put(position,"moveRight");
                                }
                                else{
                                    moveOptions.add("SEmoveRight");
                                    moveNumber.put("SEmoveRight", position);
                                    positionMove.put(position,"SEmoveRight");
                                }
                            }
                        }

                        //Move left
                        if (i + 1 < boardX && j - 1 >= 0) {
                            if (gameBoardData[i + 1][j - 1] == 0) {
                                position = getPosition(i + 1, j - 1);
                                this.listOfAvailableMoves.add(position);
                                if(piece == SECOND_PLAYER) {
                                    moveOptions.add("moveLeft");
                                    moveNumber.put("moveLeft", position);
                                    positionMove.put(position,"moveLeft");
                                }
                                else{
                                    moveOptions.add("SWmoveLeft");
                                    moveNumber.put("SWmoveLeft", position);
                                    positionMove.put(position,"SWmoveLeft");
                                }
                            }
                        }


                        //Jump Left
                        if (i + 2 < boardX && j - 2 >= 0) {
                            if (gameBoardData[i + 2][j - 2] == 0) {
                                if(piece != FIRST_PLAYER_KING) {
                                    if(gameBoardData[i+1][j-1] == FIRST_PLAYER_KING || gameBoardData[i+1][j-1]== FIRST_PLAYER) {
                                        position = getPosition(i + 2, j - 2);
                                        this.listOfAvailableMoves.add(position);
                                        if (piece == SECOND_PLAYER) {
                                            moveOptions.add("jumpLeft");
                                            moveNumber.put("jumpLeft", position);
                                            positionMove.put(position,"jumpLeft");
                                        } else {
                                            moveOptions.add("SWjumpLeft");
                                            moveNumber.put("SWjumpLeft", position);
                                            positionMove.put(position,"SWjumpLeft");
                                        }
                                    }
                                }
                                if(piece == FIRST_PLAYER_KING ){
                                    if(gameBoardData[i+1][j-1] == SECOND_PLAYER || gameBoardData[i+1][j-1] == SECOND_PLAYER_KING) {
                                        position = getPosition(i + 2, j - 2);
                                        this.listOfAvailableMoves.add(position);

                                        moveOptions.add("SWjumpLeft");
                                        moveNumber.put("SWjumpLeft", position);
                                        positionMove.put(position,"SWjumpLeft");
                                    }
                                }
                            }
                        }

                        //Jump Right
                        if (i + 2 < boardX && j + 2 < boardY) {
                            if (gameBoardData[i + 2][j + 2] == 0) {
                                if (piece != FIRST_PLAYER_KING) {
                                    if (gameBoardData[i + 1][j + 1] == FIRST_PLAYER || gameBoardData[i + 1][j + 1] == FIRST_PLAYER_KING) {
                                        position = getPosition(i + 2, j + 2);
                                        this.listOfAvailableMoves.add(position);
                                        if (piece == SECOND_PLAYER) {
                                            moveOptions.add("jumpRight");
                                            moveNumber.put("jumpRight", position);
                                            positionMove.put(position,"jumpRight");
                                        } else {
                                            moveOptions.add("SEjumpRight");
                                            moveNumber.put("SEjumpRight", position);
                                            positionMove.put(position,"SEjumpRight");
                                        }
                                    }
                                }
                                if (piece == FIRST_PLAYER_KING) {
                                    if (gameBoardData[i + 1][j + 1] == SECOND_PLAYER || gameBoardData[i+1][j+1] == SECOND_PLAYER_KING) {
                                        position = getPosition(i + 2, j + 2);
                                        this.listOfAvailableMoves.add(position);
                                        moveOptions.add("SEjumpRight");
                                        moveNumber.put("SEjumpRight", position);
                                        positionMove.put(position,"SEjumpRight");
                                    }
                                }
                            }
                        }
                    }
                    //Moves For First Player , Player One King, Second Player King
                    if ( piece == FIRST_PLAYER || piece == FIRST_PLAYER_KING || piece == SECOND_PLAYER_KING) {
                        //Move left
                        if (i - 1 >= 0 && j - 1 >= 0) {
                            if (gameBoardData[i - 1][j - 1] == 0) {
                                position = getPosition(i - 1, j - 1);
                                this.listOfAvailableMoves.add(position);
                                if(piece == FIRST_PLAYER) {
                                    moveOptions.add("moveLeft");
                                    moveNumber.put("moveLeft",position);
                                    positionMove.put(position,"moveLeft");
                                }
                                else{
                                    moveOptions.add("NWmoveLeft");
                                    moveNumber.put("NWmoveLeft", position);
                                    positionMove.put(position,"NWmoveLeft");
                                }
                            }
                        }

                        //Move Right
                        if (i - 1 >= 0 && j + 1 < boardY) {
                            if (gameBoardData[i - 1][j + 1] == 0) {
                                position = getPosition(i - 1, j + 1);
                                this.listOfAvailableMoves.add(position);
                                if(piece == FIRST_PLAYER){
                                    moveOptions.add("moveRight");
                                    moveNumber.put("moveRight", position);
                                    positionMove.put(position,"moveRight");
                                }
                                else {
                                    moveOptions.add("NEmoveRight");
                                    moveNumber.put("NEmoveRight", position);
                                    positionMove.put(position,"NEmoveRight");
                                }
                            }
                        }

                        //Jump Left
                        if (i - 2 >= 0 && j - 2 >= 0) {
                            if (gameBoardData[i - 2][j - 2] == 0) {
                                if (piece != SECOND_PLAYER_KING) {
                                    if(gameBoardData[i - 1][j - 1] == SECOND_PLAYER || gameBoardData[i - 1][j - 1] == SECOND_PLAYER_KING) {
                                        position = getPosition(i - 2, j - 2);
                                        this.listOfAvailableMoves.add(position);
                                        if(piece == FIRST_PLAYER) {
                                            moveOptions.add("jumpLeft");
                                            moveNumber.put("jumpLeft", position);
                                            positionMove.put(position,"jumpLeft");
                                        }
                                        else{
                                            moveOptions.add("NWjumpRight");
                                            moveNumber.put("NWjumpRight",position);
                                            positionMove.put(position,"NWjumpRight");
                                        }
                                    }
                                }
                                if(piece == SECOND_PLAYER_KING){
                                    if(gameBoardData[i - 1][j - 1] == FIRST_PLAYER || gameBoardData[i - 1][j - 1] == FIRST_PLAYER_KING){
                                        position = getPosition(i - 2, j - 2);
                                        this.listOfAvailableMoves.add(position);
                                        moveOptions.add("NWjumpLeft");
                                        moveNumber.put("NWjumpLeft", position);
                                        positionMove.put(position,"NWjumpLeft");

                                    }
                                }
                            }
                        }
                        //Jump Right
                        if (i - 2 >= 0 && j + 2 < boardY) {
                            if (gameBoardData[i - 2][j + 2] == 0) {
                                if(piece != SECOND_PLAYER_KING) {
                                    if(gameBoardData[i - 1][j + 1] == SECOND_PLAYER || gameBoardData[i - 1][j + 1] == SECOND_PLAYER_KING) {
                                        position = getPosition(i - 2, j + 2);
                                        this.listOfAvailableMoves.add(position);
                                        if(piece == FIRST_PLAYER) {
                                            moveOptions.add("jumpRight");
                                            moveNumber.put("jumpRight", position);
                                            positionMove.put(position,"jumpRight");
                                        }
                                        else{
                                            moveOptions.add("NEjumpRight");
                                            moveNumber.put("NEjumpRight", position);
                                            positionMove.put(position,"NEjumpRight");
                                        }
                                    }
                                }
                                if(piece == SECOND_PLAYER_KING){
                                    if(gameBoardData[i - 1][j + 1] == FIRST_PLAYER || gameBoardData[i - 1][j + 1] == FIRST_PLAYER_KING){
                                        position = getPosition(i - 2, j + 2);
                                        this.listOfAvailableMoves.add(position);
                                        moveOptions.add("NEjumpRight");
                                        moveNumber.put("NEjumpRight", position);
                                        positionMove.put(position,"NEjumpRight");
                                    }
                                }
                            }
                        }
                    }
//                    }

                }
            }
        }
        return !listOfAvailableMoves.isEmpty();
    }

    private int getPosition(int row, int column) {
        //Gets position if the row and column are given
        int counter = 0;
        for(int i = 0; i<boardX ; i++){
            for(int j = 0; j < boardX ; j++){
                counter += 1;

                if(i == row && j == column){
                    return counter;
                }
            }
        }
        return counter;
    }

    private int getPosition(String pos){
        int index =0;
        for(int i = 0; i<boardX ; i++){
            for(int j = 0; j < boardX ; j++){
                index+= 1;
                if(pos.equals(board[i][j])){
//                    boardLocations.put(i,j);
                    return index;
                }
            }
        }
        return index;
    }

    private void showAvailableMoves(String pieceSelected) {
//        System.out.println(pieceMoves.entrySet());
//        for(HashMap.Entry<Integer,String> moveList : pieceMoves.entrySet()){
//            System.out.println(moveList.getValue());
//        }
        System.out.print("Available Moves: ");
        for(String moveList : moveOptions){
            System.out.print(", " +moveList);
        }
        System.out.println();
    }

    private void playerMoveConverter(String move, String pieceSelected, int player){
        Integer newMovePos = 0;
        Integer oldPosition = 0;
        Boolean force = false;
        ArrayList<String> jumps = new ArrayList<String>();
        String placeholder ="";
        checkAvailableMoves(pieceSelected,true);
        for(Map.Entry<String,Integer> entry : moveNumber.entrySet()){
            if(move.equals(entry.getKey())){
                newMovePos = entry.getValue();
                oldPosition = getPosition(pieceSelected);
                placeholder = entry.getKey();

            }
        }
        makeMove(newMovePos,pieceSelected, oldPosition);
        if(placeholder.contains("jump") || placeholder.contains("jump")){

            try {
                removeOpponentPiece(oldPosition, player, placeholder);
            }catch(ArrayIndexOutOfBoundsException e){
                System.out.println("move:" + move + ", pieceSelected:"+pieceSelected+", player:"+player);
                System.out.println("Old Posisition:"+ oldPosition+ ", newPosition:"+newMovePos+", Player: "+player +", move:"+placeholder);
                generateBoard();
                generateGameDataBoard();
                System.out.println();
            }
            force = true;
        }
        promotePiece(player,newMovePos);
        int counter;
        placeholder ="";
        jumps.clear();
        while(force){
            counter = 0;
            //Check to see if we can move with the piece in the new position
            if(selectPiece(pieceSelected,player)) {
                if (checkAvailableMoves(pieceSelected, true)) {
                    for(Map.Entry<String,Integer> entry : moveNumber.entrySet()){
                        if(entry.getKey().contains("jump")){
                            newMovePos = entry.getValue();
                            oldPosition = getPosition(pieceSelected);
                            placeholder = entry.getKey();
                        }
                    }
                }
            }

            if(counter > 0){
                makeMove(newMovePos,pieceSelected, oldPosition);
                removeOpponentPiece(oldPosition,player,placeholder);
//                deleteMove(oldPosition,pieceSelected);
                promotePiece(player,newMovePos);
                force = true;
            }
            else{
                force = false;
            }
        }
    }


    private void removeOpponentPiece(Integer oldPosition, int player, String moveList) {
        int index = 0;
        for(int i = 0 ; i < boardX; i++ ){
            for(int j = 0; j < boardY ; j++){
                index +=1;
                if(oldPosition == index){
                    if(player == FIRST_PLAYER){
                        if(moveList.equals("jumpLeft")){
                            board[i-1][j-1] = "   ";
                            gameBoardData[i-1][j-1]= 0;
                        }
                        else if(moveList.equals("jumpRight")){
                            board[i-1][j+1] = "   ";
                            gameBoardData[i-1][j+1]= 0;
                        }
                    }
                    else if(player == SECOND_PLAYER){
                        if(moveList.equals("jumpLeft")){
                            board[i+1][j-1] = "   ";
                            gameBoardData[i+1][j-1]= 0;
                        }
                        else if(moveList.equals("jumpRight")){
                            board[i+1][j+1] = "   ";
                            gameBoardData[i+1][j+1]= 0;
                        }
                    }
                    if(moveList.equals("NWjumpLeft")){
                        board[i-1][j-1] = "   ";
                        gameBoardData[i-1][j-1]= 0;
                    }
                    if(moveList.equals("NEjumpRight")){
                        board[i-1][j+1] = "   ";
                        gameBoardData[i-1][j+1]= 0;
                    }
                    if(moveList.equals("SWjumpLeft")){
                        board[i+1][j-1] = "   ";
                        gameBoardData[i+1][j-1]= 0;
                    }
                    if(moveList.equals("SEjumpRight")){
                        board[i+1][j+1] = "   ";
                        gameBoardData[i+1][j+1]= 0;
                    }
                }
            }
        }
    }

    private void makeMove(Integer newMovePos, String pieceSelected, Integer oldPosition) {
        int counter = 0;
        String placeholder="";
        int playerNum=10;
        for(int i = 0; i< boardX ; i++){
            for(int j = 0;j< boardY ; j++){
                counter++;
                if(counter == oldPosition){
                    placeholder = board[i][j];
                    playerNum = gameBoardData[i][j];
                    board[i][j] = "   ";
                    gameBoardData[i][j] = 0;
                }
//                if(pieceSelected.equals(board[i][j])){
//                    System.out.println("here");
//                    board[i][j] = "  ";
//                    gameBoardData[i][j] = 0;
//                }
            }
        }
        counter =0;
        for(int i = 0; i< boardX ; i++) {
            for (int j = 0; j < boardY; j++) {
                counter++;
                if (counter == newMovePos) {
                    board[i][j] = placeholder;
                    gameBoardData[i][j] = playerNum;
                }
//                if(pieceSelected.equals(board[i][j])){
//                    System.out.println("here");
//                    board[i][j] = "  ";
//                    gameBoardData[i][j] = 0;
//                }
            }
        }

    }

    public boolean checkWin(boolean isAlgorithm){
//        generateBoard();
        int king;
        //A win is declared when there are no opposing pieces left or
        //When a player is unable to make a move
        //So what we want is a loop that goes through all selected pieces on the board with
        //Their respective player numbers and see all the moves they can make
        ListOfPieces.clear();
        moveOptions.clear();
        //Iterate For both players
        //Want to add these to a list where we can iterate to see what is on the board and
        //Then reuse check available moves to see if there are any potential moves the player can make
        for(int k = 1 ; k < 4 ; k+=2) {
            ListOfPieces.clear();
            moveOptions.clear();
            if (k == FIRST_PLAYER) {
                king = FIRST_PLAYER_KING;
            } else {
                king = SECOND_PLAYER_KING;
            }

            for (int i = 0; i < boardX; i++) {
                for (int j = 0; j < boardY; j++) {
                    if (gameBoardData[i][j] == k) {
                        ListOfPieces.add(k);
                        checkAvailableMoves(board[i][j], false);
                    }
                    if (gameBoardData[i][j] == king) {
                        ListOfPieces.add(king);
                        checkAvailableMoves(board[i][j], false);
                    }
                }
            }
            //If there are no pieces
            if (FIRST_PLAYER == k) {
                if (ListOfPieces.isEmpty()) {
                    if(!isAlgorithm) {
                        System.out.println("Player two Has won: Type 1");
                    }
                    else {
                        loss++;
                    }
                    return true;
                } else if (moveOptions.isEmpty()) {
                    if(!isAlgorithm) {
                        System.out.println("Player two Has won: Type 2");
                    }
//                    showAvailableMoves("");
                    else {
                        loss++;
                    }
                    return true;
                }
            } else if (k == SECOND_PLAYER) {
                if (ListOfPieces.isEmpty()) {
                    if(!isAlgorithm) {
                        System.out.println("Player One Has won : Type 1");
                        win++;
                    }
                    else {
                        win++;
                    }
                    return true;
                } else if (moveOptions.isEmpty()) {
                    if(!isAlgorithm) {
                        System.out.println("Player One Has won : Type 2");
                        win++;
                    }
                    else {
                        win++;
                    }
                    return true;
                }
            }
        }

        //Player two Check
        ListOfPieces.clear();
        moveOptions.clear();
        return false;
//        for(int i = 0; i < boardX ; i++){
//            for(int j = 0 ; j < boardY ; j++){
//                if(gameBoardData[i][j] == SECOND_PLAYER){
//                    ListOfPieces.add(SECOND_PLAYER);
//                    checkAvailableMoves(board[i][j], false);
//                }
//                if(gameBoardData[i][j] == SECOND_PLAYER_KING){
//                    ListOfPieces.add(SECOND_PLAYER_KING);
//                    checkAvailableMoves(board[i][j], false);
//                }
//            }
//        }
//        if(ListOfPieces.isEmpty()){
//            System.out.println("Player One Has won");
//        }
//        if(moveOptions.isEmpty()){
//            System.out.println("Player One Has won");
//        }
//        return false;
    }

    public ArrayList<String> getMoves(int player) {
        ArrayList<String> moveSet = new ArrayList<String>();
        int king = 0;
        int index = 0;
        if(player == FIRST_PLAYER){
            king = FIRST_PLAYER_KING;
        }
        if(player == SECOND_PLAYER){
            king = SECOND_PLAYER_KING;
        }
        for(int i = 0; i < boardX ; i++){
            for(int j = 0; j <boardY ; j++){
                index += 1;
                if(gameBoardData[i][j] == player || gameBoardData[i][j] == king){
                    moveSet.add(board[i][j]);
                    moveOptions.add(board[i][j]);
                }
            }
        }
        return moveSet;
    }

    public void promotePiece(int player, int newPosition){
        //This method see's whether a piece has reached the other side and
        //Gets promoted to that player's King.
        //Needs to be checked after every move made
        int counter = 1;
        for(int i = 0; i< boardX ; i++){
            for(int j = 0; j < boardY ; j++){
                if(player == FIRST_PLAYER) {
                    if (gameBoardData[i][j] == FIRST_PLAYER_KING || board[i][j].contains("Kb")){
                        counter++;
                    }
                }
                if(player == SECOND_PLAYER){
                    if(gameBoardData[i][j] == SECOND_PLAYER_KING || board[i][j].contains("Kw")){
                        counter++;
                    }
                }
            }
        }



        for(int i = 0; i < boardX ; i++){
            for(int j = 0; j < boardY; j++){
                //Scan the first row of the board
                if(i == 0){
                    if(gameBoardData[i][j] == player && board[i][j].contains("b")){
                        gameBoardData[i][j] = FIRST_PLAYER_KING;
                        board[i][j] = "Kb"+counter;
                    }
                }
                if(i == boardX - 1){
                    if(gameBoardData[i][j] == player && board[i][j].contains("w")){
                        gameBoardData[i][j] = SECOND_PLAYER_KING;
                        board[i][j] = "Kw"+counter;
                    }

                }
            }
        }
    }

    public void nextBestMove(int[][] gBoard, String[][] sBoard, int player, boolean maximiser, boolean isAlpha, boolean giveSuggestions){
        //Default Boards -- Saved
        int stackSize =0;
        int[][] gameBoard = copy2Darray(gBoard);
        String[][] stringBoard = copy2Darray(sBoard);
        //Get the number of pieces for each player before moves have been made.
        this.pieceCountPlayerOne = getpieceCount(FIRST_PLAYER);
        this.pieceCountPlayerTwo = getpieceCount(SECOND_PLAYER);


        int depth = 0;
        int alpha = -10000;
        int beta = 10000;
        Stack<String> stackOfMoves = new Stack<String>();
        double bestValue = -10000;
        double predVal =0;
        int bestMovePos;
        int nextPlayer;
        String bestPiece = "" ;
        String bestMove ="";
        if(player == FIRST_PLAYER){
            nextPlayer = SECOND_PLAYER;
        }
        else{
            nextPlayer = FIRST_PLAYER;
        }

        //get  all possible Moves for that player(Piece Selection)
        ArrayList<String> pieceSet = getMoves(player);
        ArrayList<String> movablePiece = new ArrayList<String>();

        for(String piece : pieceSet ){
            if(selectPiece(piece,player)){
                if(checkAvailableMoves(piece,true)){
                    for(String move : moveOptions){
                        stackOfMoves.push(move);
                    }
                    //One piece -> (move1, move2, move3) etc
                    //This takes the pieces and adds the moves to a stack to be explored

                    while(!stackOfMoves.isEmpty()){
                        //Player move converter makes changed to the actual game board
                        playerMoveConverter(stackOfMoves.peek(), piece,player);
                        //once this is done, assign
                        if(isAlpha){
                            predVal = alphaBeta(alpha,beta,gBoard,stringBoard,!maximiser,nextPlayer,depth,player);
                        }
                        else {
                            predVal = minimax(gBoard, stringBoard, !maximiser, nextPlayer, depth + 1, player);
                        }
                        //Revert back to default
                        gameBoardData = copy2Darray(gameBoard);
                        board = copy2Darray(stringBoard);
                        if(predVal > bestValue){
                            bestValue = predVal;
                            bestMove = stackOfMoves.peek();
                            bestPiece = piece;
//                            System.out.println(bestValue);
//                            System.out.println(bestPiece);
//                            System.out.println(stackOfMoves.peek());

                        }
                        stackOfMoves.pop();
                    }
                }

            }
        }
        if(bestMove.length() > 0 && bestPiece.length() > 0) {
            selectPiece(bestPiece,player);
            checkAvailableMoves(bestPiece,true);
//            System.out.println("making move: "+bestMove+", Piece using: "+bestPiece);
            if(!giveSuggestions) {
                playerMoveConverter(bestMove, bestPiece, player);
            }
            else{
                System.out.println("Suggestion: Piece, "+bestPiece+" , Move, "+bestMove);
            }
        }
    }

    private int getpieceCount(int player) {
        int countPiece = 0;
        for(int i = 0; i < boardX; i++){
            for(int j = 0; j < boardY ; j++){
                if(gameBoardData[i][j] == player){
                    countPiece ++;
                }
                if(player == FIRST_PLAYER && gameBoardData[i][j] == FIRST_PLAYER_KING){
                    countPiece++;
                }
                if(player == SECOND_PLAYER && gameBoardData[i][j] == SECOND_PLAYER_KING){
                    countPiece++;
                }
            }
        }
        return countPiece;
    }

    private double minimax(int[][] gBoard, String[][] sBoard, boolean maximiser, int player, int depth, int originalPlayer){
        double score;
        double bestScore;
        int[][] gameBoard = copy2Darray(gameBoardData);
        String[][] stringBoard = copy2Darray(board);
        ArrayList<String> pieceSet = getMoves(player);
        List<String> moveSet;
        Stack<String> stackOfMoves = new Stack<String>();
        int nextPlayer;
        if(player == FIRST_PLAYER){
            nextPlayer = SECOND_PLAYER;
        }
        else{
            nextPlayer = FIRST_PLAYER;
        }

        if(depth == this.maxDepth || checkWin(true)){
            score = heuristicPointFunction(originalPlayer, gameBoardData);
            if(maximiser) {
                return score;
            }
            else{
                return -score;
            }
        }
        if(maximiser) {
            bestScore = -10000;
            for (String piece : pieceSet) {
                if (selectPiece(piece, player)) {
                    if (checkAvailableMoves(piece, true)) {
                        for (String move : moveOptions) {
                            stackOfMoves.push(move);
                        }
                        while (!stackOfMoves.isEmpty()) {


                            //Player move converter makes changed to the actual game board
                            try {
                                playerMoveConverter(stackOfMoves.peek(), piece, player);
                            }catch(ArrayIndexOutOfBoundsException e){
                                System.out.println("Depth :"+depth+", Player:"+player+", pieces:"+pieceSet.size()+
                                        ", piece:"+piece+", moves:"+stackOfMoves.size()+", move:"+stackOfMoves.peek());
                            }
                            bestScore = Math.max(minimax(gameBoardData, board, false, nextPlayer, depth+1, originalPlayer),bestScore);

                            //Revert back to default
                            gameBoardData = copy2Darray(gameBoard);
                            board = copy2Darray(stringBoard);
                            stackOfMoves.pop();
                        }
                    }

                }
            }
            return bestScore;
        }
        else{
            bestScore = 10000;
            for (String piece : pieceSet) {
                if (selectPiece(piece, player)) {
                    if (checkAvailableMoves(piece, true)) {
                        for (String move : moveOptions) {
                            stackOfMoves.push(move);
                        }
                        while (!stackOfMoves.isEmpty()) {
//                            generateBoard();
//                            generateGameDataBoard();
                            try {
                                playerMoveConverter(stackOfMoves.peek(), piece, player);
                            }catch(ArrayIndexOutOfBoundsException e){
                                System.out.println("Depth :"+depth+", Player:"+player+", pieces:"+pieceSet.size()+
                                        ", piece:"+piece+", moves:"+stackOfMoves.size()+", move:"+stackOfMoves.peek());
                            }
                            //once this is done, assign

                            bestScore = Math.min(minimax(gameBoardData, board, true, nextPlayer, depth+1, originalPlayer),bestScore);

                            //Revert back to default
                            gameBoardData = copy2Darray(gameBoard);
                            board = copy2Darray(stringBoard);
                            stackOfMoves.pop();
                        }
                    }

                }
            }
            return bestScore;
        }

    }

    private double alphaBeta(double alpha, double beta, int[][] gBoard, String[][] sBoard, boolean maximiser, int player, int depth, int originalPlayer){
        double score;
        double bestScore;
        int[][] gameBoard = copy2Darray(gameBoardData);
        String[][] stringBoard = copy2Darray(board);
        ArrayList<String> pieceSet = getMoves(player);
        List<String> moveSet;
        Stack<String> stackOfMoves = new Stack<String>();
        int nextPlayer;
        if(player == FIRST_PLAYER){
            nextPlayer = SECOND_PLAYER;
        }
        else{
            nextPlayer = FIRST_PLAYER;
        }

        if(depth == this.maxDepth || checkWin(false)){
            if(maximiser){
                score = heuristicPointFunction(originalPlayer,gameBoard);
                return score;
            }
            else{
                score = -heuristicPointFunction(originalPlayer,gameBoard);
                return score;
            }
        }
        if(maximiser) {
            bestScore = -10000;
            for (String piece : pieceSet) {
                if (selectPiece(piece, player)) {
                    if (checkAvailableMoves(piece, true)) {
                        for (String move : moveOptions) {
                            stackOfMoves.push(move);
                        }
                        while (!stackOfMoves.isEmpty()) {


                            //Player move converter makes changed to the actual game board
                            try {
                                playerMoveConverter(stackOfMoves.peek(), piece, player);
                            }catch(ArrayIndexOutOfBoundsException e){
                                System.out.println("Depth :"+depth+", Player:"+player+", pieces:"+pieceSet.size()+
                                        ", piece:"+piece+", moves:"+stackOfMoves.size()+", move:"+stackOfMoves.peek());
                            }
//                            generateBoard();
                            //once this is done, assign

                            double predVal = alphaBeta(alpha,beta,gameBoardData, board, false, nextPlayer, depth+1, originalPlayer);

                            //Revert back to default
                            gameBoardData = copy2Darray(gameBoard);
                            board = copy2Darray(stringBoard);
                            stackOfMoves.pop();
                            bestScore = Math.max(bestScore,predVal);
                            alpha = Math.max(bestScore,alpha);

                            if(beta <= alpha){
                                break;
                            }
                        }
                        if(beta <= alpha){
                            break;
                        }
                    }
                }
            }
            return bestScore;
        }
        else{
            bestScore = 10000;
            for (String piece : pieceSet) {
                if (selectPiece(piece, player)) {
                    if (checkAvailableMoves(piece, true)) {
                        for (String move : moveOptions) {
                            stackOfMoves.push(move);
                        }
                        while (!stackOfMoves.isEmpty()) {
//                            generateBoard();
//                            generateGameDataBoard();
                            try {
                                playerMoveConverter(stackOfMoves.peek(), piece, player);
                            }catch(ArrayIndexOutOfBoundsException e){
                                System.out.println("Depth :"+depth+", Player:"+player+", pieces:"+pieceSet.size()+
                                        ", piece:"+piece+", moves:"+stackOfMoves.size()+", move:"+stackOfMoves.peek());
                            }
                            //once this is done, assign

                            double predVal = alphaBeta(alpha,beta,gameBoardData, board, true, nextPlayer, depth+1,originalPlayer);

                            //Revert back to default
                            gameBoardData = copy2Darray(gameBoard);
                            board = copy2Darray(stringBoard);
                            stackOfMoves.pop();
                            bestScore = Math.min(bestScore,predVal);
                            beta = Math.min(bestScore,beta);
                            if(beta <= alpha){
                                break;
                            }
                        }
                        if(beta <= alpha){
                            break;
                        }
                    }
                }
            }
            return bestScore;
        }

    }


    private static int[][] copy2Darray(int[][] inputArray){
        //Produces a deep copy of a 2D array
        //Possible to make a utility Class that Has a interface for this
        //Then just implement the interface? Not really needed but

        if(inputArray == null){
            return null;
        }
        int[][] newArray = new int[inputArray.length][];
        for(int i = 0; i<inputArray.length ; i++){
            newArray[i] = inputArray[i].clone();
        }
        return newArray;
    }

    private static String[][] copy2Darray(String[][] inputArray){
        if(inputArray == null){
            return null;
        }
        String[][] newArray = new String[inputArray.length][];
        for(int i = 0; i<inputArray.length ; i++){
            newArray[i] = inputArray[i].clone();
        }
        return newArray;
    }

    private double heuristicPointFunction(int player, int[][] gBoard) {
        int score = 0;
        int opponentScore = 0;
        int king =10;
        int opponent=10;
        int opponentKing=10;
        int counter= 0;
        int counter2= 0;
        int counter3 =0;
        int counter4=0;
        if(player == FIRST_PLAYER){
            king = FIRST_PLAYER_KING;
            opponent = SECOND_PLAYER;
            opponentKing = SECOND_PLAYER_KING;
        }
        if(player == SECOND_PLAYER){
            king = SECOND_PLAYER_KING;
            opponent = FIRST_PLAYER;
            opponentKing = FIRST_PLAYER_KING;
        }

        for (int i = 0; i < boardX; i++) {
            for (int j = 0; j < boardY; j++) {
                if (player == gBoard[i][j]) {
                    score += 10;
                    counter2++;
                }
                if(king == gBoard[i][j]){
                    score+= 20;
                    counter2++;
                }
                if(gBoard[i][j] == opponent ){
                    opponentScore -=10;
                    counter++;
                }
                if(gBoard[i][j] == opponentKing){
                    opponentScore -=20;
                    counter++;
                }
                if(i<4){
                    if(gBoard[i][j] ==FIRST_PLAYER || gBoard[i][j] == FIRST_PLAYER_KING){
                        counter3++;
                    }
                }
                if(i>4){
                    if(gBoard[i][j] == SECOND_PLAYER || gBoard[i][j] ==SECOND_PLAYER_KING){
                        counter4++;
                    }
                }

            }
        }
        int pieces = score - opponentScore;

        int captured =0;
        if(player == FIRST_PLAYER){
            captured = (pieceCountPlayerTwo-counter)*10 - (pieceCountPlayerOne-counter2)*10;
        }
        if(player == SECOND_PLAYER){
            captured = (pieceCountPlayerOne-counter)*10 - (pieceCountPlayerTwo-counter2)*10;
        }
        int otherSide = 5*(counter3)- 5*(counter4);

        if(this.heuristicSelecter == 1) {
            return pieces;
        }
        if(this.heuristicSelecter == 2){

            int capturedAndpieces = pieces + captured;
            return capturedAndpieces;
        }
        if(this.heuristicSelecter == 3){
            int piecesCapturedOther = pieces + captured +  otherSide;
            return piecesCapturedOther;

        }
        double getPoints =0;
        if(this.heuristicSelecter == 4){

            getPoints = pointsFromNN(this.customHeuristicNN);

            return getPoints;
        }
        return score;
    }

    private double pointsFromNN(nn neuralNet) {
        double[] input = encryptInput(neuralNet);
        double[] output = neuralNet.giveInput(input);
        double sum = 0;
        for(int i = 0; i < output.length ; i++){
            sum+= output[i];
        }
        return sum;
    }

    private void justHuman(){
        boolean notFinished = true;
        int turn = 0;
        while(notFinished){
            System.out.println("Select Piece 1");
            //Human goes first
            //SelectPiece
            //Show moves available
            //Force hops
            if(turn % 2 == 0) {
                System.out.println("human");
                notFinished = humanTurn(FIRST_PLAYER);
                generateBoard();
                generateGameDataBoard();
            }
            else{
                System.out.println("AI");
//                generateBoard();
//                generateGameDataBoard();
            }
            turn++;
        }
    }

    private void humanVsRandom() {
        boolean notFinished = true;
        int turn = 0;
        while(notFinished && turn != maxTurn){

            if(turn % 2 == 0) {
                System.out.println("humanTurn");
                System.out.println("Select Piece :");
                notFinished = humanTurn(FIRST_PLAYER);
                generateBoard();
//                generateGameDataBoard();
            }
            else{
                System.out.println("Random Turn");
                try {
                    Thread.sleep(2000);
                }catch (InterruptedException e){
                    System.out.println(e.getMessage());
                }
                notFinished = randomTurn(SECOND_PLAYER);
                generateBoard();
//                generateGameDataBoard();
            }
            turn++;
        }

    }

    private void humanLikeMinivspureAI() {
        //Minimax AI that sometimes makes a random move
        boolean notFinished = true;
        int turn = 0;
        Random rand = new Random();
        int random ;
        while (notFinished && turn != maxTurn) {
            random = rand.nextInt(100);
            if (turn % 2 == 0) {
                System.out.println("Player 1 turn");
                System.out.println("Random:"+random);
                if(random < 20){
                    System.out.println("random turn");
                    notFinished = randomTurn(FIRST_PLAYER);
                }
                else {
                    notFinished = aiTurn(FIRST_PLAYER,true,false,false);

                }
                generateBoard();
            } else {
                System.out.println("Player 2 turn");
                notFinished = aiTurn(SECOND_PLAYER, true,false,false);
                System.out.println(notFinished);
                generateBoard();
//                generateGameDataBoard();
            }
            turn++;
        }
    }

    private void customGame(boolean isAlpha, boolean isPartRandom, boolean isVsMinimax, boolean showMoves, boolean isNN, boolean isVsNN, boolean isVsAlpha, boolean isVsRandom, boolean isMax) throws IOException {
        long startTime =0;
        long finishTime = 0;
        long duration =0;
        long totalTimeNano =0;
        long totalTimeMilli= 0;
        int currentGameNumber =0;
        boolean notFinished = true;
        int turn = 0;

        FileWriter gameRecorder = new FileWriter(createFile());
        System.out.println("Create a file for the move after");
        FileWriter gameAfterMoveRecroder = new FileWriter(createFile());

        win= 0;
        draw = 0;
        loss = 0;
        int doRandom = 0;
        Random rand = new Random();

        while(currentGameNumber < maxNumberOfGames) {
            System.out.println("Game: "+ (currentGameNumber+1));
            while (notFinished && turn != maxTurn) {

                if (turn % 2 == 0) {
                    //Needs function
                    int[][] previousStateplayer1 = copy2Darray(gameBoardData);
                    if(!this.playerTwoFormat){
                        environmentConverter(gameRecorder,false, copy2Darray(gameBoardData),false);
                        environmentConverter(gameAfterMoveRecroder,false,copy2Darray(gameBoardData),false);
                    }

                    if(isPartRandom){
                        doRandom = rand.nextInt(100);
                    }
                    startTime = System.nanoTime();
                    //AI turn
//                    System.out.println("AI");
                    if(doRandom > 50){
                        randomTurn(FIRST_PLAYER);
                    }
                    else if(isMax) {
                        notFinished = aiTurn(FIRST_PLAYER, true,isAlpha, showMoves);
                    }
                    else if(isNN){
                        neuralNetworkTurn(FIRST_PLAYER);
                    }
                    finishTime = System.nanoTime();
                    //Needs Function
                    if(!this.playerTwoFormat){
                        environmentConverter(gameRecorder,true, previousStateplayer1,false);
                        environmentConverter(gameAfterMoveRecroder,true,previousStateplayer1,true);
                        gameAfterMoveRecroder.append("\n");
                        gameRecorder.append("\n");
                    }

                } else {
                    //Random Turn
//                    System.out.println("Rand");
                    //Needs Function
                    int[][] previousStatePlayer2 = copy2Darray(gameBoardData);

                    if(this.playerTwoFormat){
                        environmentConverter(gameRecorder,false, copy2Darray(gameBoardData),false);
                        environmentConverter(gameAfterMoveRecroder,false, copy2Darray(gameBoardData),false);
                    }


                    if(isVsMinimax){
                        notFinished = aiTurn(SECOND_PLAYER,true,false, showMoves);
                    }
                    else if(isVsAlpha){
                        notFinished = aiTurn(SECOND_PLAYER,true,true,showMoves);
                    }
                    else if(isVsRandom){
                        notFinished = randomTurn(SECOND_PLAYER);
                    }else if(isVsNN){
                        neuralNetworkTurn(SECOND_PLAYER);
                    }

                    //Needs Function
                    if(this.playerTwoFormat){
                        environmentConverter(gameRecorder,true, previousStatePlayer2,false);
                        environmentConverter(gameAfterMoveRecroder,true, previousStatePlayer2,true);
                        gameRecorder.append("\n");
                        gameRecorder.append("\n");
                    }
                }
                duration = (finishTime - startTime);
                totalTimeNano += duration;
                turn++;
                if(turn ==100){
                    draw+=1;
                }
            }
            currentGameNumber++;
            notFinished = true;
            turn = 0;
            //Reset the board
            makeBoard(boardX,boardY);
        }
        gameRecorder.close();
        totalTimeMilli = TimeUnit.NANOSECONDS.toMillis(totalTimeNano);
        System.out.println("Win: "+win +",Loss: "+loss+", Draw: "+draw+ ", Average Turn time :"+(totalTimeMilli/maxNumberOfGames));

    }


    private String createFile(){
//        boolean error = true;
        Scanner scan = new Scanner(System.in);
        String userInput = "";
        while(true){
            System.out.println("Would you like to choose a File name for the DataSet? y/n");
            userInput = scan.nextLine();
            if(userInput.contains("y")){
                System.out.println("What is the filename?");
                userInput = scan.nextLine();
//                error = false;
                break;
            }
            else if(userInput.contains("n")){
                userInput = "customDataSetCheckers";
//                error = false;
                break;
            }
        }
        return  userInput+".csv";
    }

    public void environmentConverter(FileWriter gameRecorder, boolean isOutput, int[][] previousBoard,boolean isAfter) throws IOException {
        if(isOutput) {
            gameRecorder.append(";");
        }
        int counter = 0;
        int counter2 = 0;
        for (int i = 0; i < boardX; i++) {
            counter++;
            for (int j = 0; j < boardY; j++) {
                counter2++;
                //IF
                if (counter % 2 != 0 && counter2 % 2 == 0) {
                    if (this.actionOutput && isOutput) {
                        if (isAfter) {
                            if (gameBoardData[i][j] != previousBoard[i][j] && gameBoardData[i][j] != 0) {
//                            gameBoardData[i][j] != 0;
                                gameRecorder.append(1 + ",");
                            } else {
                                gameRecorder.append(0 + ",");
                            }
                        } else if (!isAfter) {
                            if (gameBoardData[i][j] != previousBoard[i][j] && gameBoardData[i][j] != 1) {
//                            gameBoardData[i][j] != 0;
                                gameRecorder.append(1 + ",");
                            } else {
                                gameRecorder.append(0 + ",");
                            }
                        }
                    } else {
                        if (gameBoardData[i][j] == FIRST_PLAYER) {
                            gameRecorder.append(this.DataSetPlayerOne + ",");
                        } else if (gameBoardData[i][j] == SECOND_PLAYER) {
                            gameRecorder.append(this.DataSetPlayerTwo + ",");
                        } else if (gameBoardData[i][j] == FIRST_PLAYER_KING) {
                            gameRecorder.append(this.DataSetPlayerOne * 2 + ",");
                        } else if (gameBoardData[i][j] == SECOND_PLAYER_KING) {
                            gameRecorder.append(this.DataSetPlayerTwo * 2 + ",");
                        } else {
                            gameRecorder.append(0 + ",");
                        }
                    }
                } else if (counter % 2 == 0 && counter2 % 2 != 0) {
                    if (this.actionOutput && isOutput) {
                        if (isAfter) {
                            if (gameBoardData[i][j] != previousBoard[i][j] && gameBoardData[i][j] != 0) {
//                            gameBoardData[i][j] != 0;
                                gameRecorder.append(1 + ",");
                            } else {
                                gameRecorder.append(0 + ",");
                            }
                        } else if (!isAfter) {
                            if (gameBoardData[i][j] != previousBoard[i][j] && gameBoardData[i][j] != 1) {
//                            gameBoardData[i][j] != 0;
                                gameRecorder.append(1 + ",");
                            } else {
                                gameRecorder.append(0 + ",");
                            }
                        }

                    } else {
                        if (gameBoardData[i][j] == SECOND_PLAYER) {
                            gameRecorder.append(this.DataSetPlayerTwo + ",");
                        } else if (gameBoardData[i][j] == FIRST_PLAYER) {
                            gameRecorder.append(this.DataSetPlayerOne + ",");

                        } else if (gameBoardData[i][j] == FIRST_PLAYER_KING) {
                            gameRecorder.append(this.DataSetPlayerOne * 2 + ",");
                        } else if (gameBoardData[i][j] == SECOND_PLAYER_KING) {
                            gameRecorder.append(this.DataSetPlayerTwo * 2 + ",");
                        } else {
                            gameRecorder.append(0 + ",");
                        }
                    }
                }
            }
            counter2 = 0;
        }
    }
}
