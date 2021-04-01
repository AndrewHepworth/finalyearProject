/*
 This Class functions as the interface and generation of customised Artificial Neural Networks
 It combines the methods from the data set class and the neural network class

 */

package model;

import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class nnSetup{
    private dataSet dataSelected;
    String fileName;
    int epochs;
    int numOfNetworks;
    private ArrayList<nn> listOfNeuralNetworks = new ArrayList<nn>();
    private ArrayList<Integer> listOfActivationFunctions = new ArrayList<Integer>();
    private ArrayList<Integer> errorCalculators = new ArrayList<Integer>();
    private ArrayList<Integer> listOfHiddenLayerSizes = new ArrayList<Integer>();
    private ArrayList<Integer> layerSizes = new ArrayList<Integer>();
    private boolean error;
    private Scanner scan = new Scanner(System.in);
    private String userInput;
    private int datafileSize;
    private int numberofHiddenLayers;
    private double learningRate;
    private boolean isShuffle;
    private boolean isSplit;
    private final ArrayList<Integer> minimumLayerSizes = new ArrayList<Integer>();
    private final ArrayList<Double> listOFLearningRates = new ArrayList<Double>();
    private int selectedError;
    private final ArrayList<Integer> listOfErrors = new ArrayList<Integer>();
    private boolean analysis;
    private final boolean fileSelected = false;

    public void nnSetup() throws Exception {
        this.error = true;
        this.scan = new Scanner(System.in);
        this.listOfNeuralNetworks = new ArrayList<nn>();
        this.listOfActivationFunctions = new ArrayList<Integer>();
        this.errorCalculators = new ArrayList<Integer>();
        this.listOfHiddenLayerSizes = new ArrayList<Integer>();
        this.layerSizes = new ArrayList<Integer>();
//        startNeuralNetConfig();
    }

    public void startNeuralNetConfig() throws Exception {
        //Select Custom Neural Net or default
        neuralNetController();
        //First Ask for a file for the data to be used,

        //Set how many epochs
    }

    private void neuralNetController() throws Exception {
        //If default -- go to contructor
        boolean exit = false;
        while(!exit){
            this.error = true;
            System.out.println("1: Default -- Set Up one network");
            System.out.println("2: Custom/Generate All possible variations");
            System.out.println("3: Load a Neural Network");
            System.out.println("4: Exit");
            userInput = scan.nextLine();
            if(userInput.contains("1") || userInput.contains("2") || userInput.contains("3")){
                String placeholder = userInput;
                //Get the data file First
                if(!fileSelected) {
                    fileSelection();
                }
                else{
                    error = true;
                    System.out.println("Would you like to load a different data set: y/n");
                    while (error) {
                        userInput = scan.nextLine();
                        if(userInput.contains("y")){
                            fileSelection();
                            error = false;
                        }
                        else if(userInput.contains("n")){
                            error = false;
                        }

                    }
                }
                //Set up networks
                setupConfiguration(Integer.parseInt(placeholder));

                this.error = false;
            }
            else if(userInput.contains("4")){
                exit = true;
            }


        }
        //if not -- go to custom controller
    }

    private void fileSelection() throws Exception {
        this.error = true;

        while(error){
            System.out.println("Select the data file you want to Use for the networks");
            System.out.println("Must Be in .csv format as : 4,5,6;7,8,9 ");
            System.out.println("; denotes the separation of Input and Output ");

            userInput = scan.nextLine();
            String filenameUser = userInput+".csv";
            this.fileName = filenameUser;
            File fileDirectory = new File(filenameUser);
//            Scanner inputFile = new Scanner(fileDirectory);
//            String path = "C:\\Users\\andy_\\OneDrive - Loughborough University\\CompSci-3rd Year\\project\\aiSolver\\customDataSet1.csv" ;


            if(!fileDirectory.exists()){
                System.out.println(fileDirectory.getName());
                error = true;
                System.out.println("Enter a Valid File");
            }
            else{
                error = false;
            }
        }

        dataSelected = new dataSet(fileName);
        dataSelected.convertDataFromFile();
        this.datafileSize = dataSelected.getSize();
        setSplit();
        setShuffle();


    }

    private void setupConfiguration(int selector) throws Exception {
        int numberOfNN = 0;
        listOfNeuralNetworks.clear();



        if (selector == 1) {
            //Configuration


            defaultSetup();
            //Start network
            nn defautNeural = new nn(this.layerSizes);
            defautNeural.setActivation(1,this.listOfActivationFunctions.get(0));
            defautNeural.setActivation(2,this.listOfActivationFunctions.get(1));
            if(listOfActivationFunctions.size()>2){
                defautNeural.setActivation(3,listOfActivationFunctions.get(2));
            }
            System.out.println(selectedError);
            defautNeural.setErrorCalculator(this.selectedError);
            defautNeural.setLearningRate(this.learningRate);

            //Start Training
            System.out.println("Training Started:");
            defautNeural.trainNetworkData(this.dataSelected, this.epochs,this.isSplit, true);
            ArrayList<Double> calculations = defautNeural.getAnalsysErrorTest() ;
            int index = 1;
            for(Double  error : calculations) {
                System.out.println("(TEST)Network 1:("+defautNeural.getActivationSelectorLayer1()+
                        ","+defautNeural.getActivationSelectorLayer2()+","+defautNeural.getActivationSelectorLayer3()
                        +"), EPOCH:" + index+ ",LR: "+ defautNeural.getLearningRate()+",ERROR TYPE: "+defautNeural.getErrorSelector()+", AVERAGE LOSS:" +error );
                index++;
            }
            System.out.println(Arrays.toString(dataSelected.getO(0)));
            for(int i = 0; i < 1 ;i++){
                System.out.println(Arrays.toString(defautNeural.forwardPass(dataSelected.getI(i))));
            }


            //Print Details out to the user.

            //Ask the user to save the network
            saveNetwork(defautNeural,false);

            listOfNeuralNetworks.add(defautNeural);

            //Default
        }
        else if(selector == 2) {
            //custom -- Asks for what the range of values for each field
            //Then generates the total amount of neural networks with those
            //Configurations
            customSetup();

            //Start Training
            error = true;
            while(error){
                System.out.println("Start Training? y/n");
                userInput = scan.nextLine();
                if(userInput.contains("y")){
                    int index = 1;
                    for(nn network : listOfNeuralNetworks){
                        network.trainNetworkData(this.dataSelected,this.epochs,this.isSplit,true);
                        System.out.println("(TEST)Network "+index+":("+network.getActivationSelectorLayer1()+
                                ","+network.getActivationSelectorLayer2()+","+network.getActivationSelectorLayer3()
                                +"), EPOCH:" + network.getAnalsysErrorTest().size()+
                                ",LR: "+ network.getLearningRate()+",ERROR TYPE: "+network.getErrorSelector()+
                                ", TOTAL LOSS:" +network.getAnalsysErrorTest().get(network.getAnalsysErrorTest().size()-1));
                        index++;
                    }
                    error = false;

                }else if(userInput.contains("n")){
                    error = false;
                }
            }
            error = true;

            for(nn network : listOfNeuralNetworks){
                if(Double.isNaN(network.getAnalsysErrorTest().size()-1)){
                    listOfNeuralNetworks.remove(network);
                }
            }

            Collections.sort(listOfNeuralNetworks, Comparator.comparing(nn::getFinalErrorCalTest));
            System.out.println("Top ten performing networks:");
            for(int i = 0 ; i < 10 ; i++){
                System.out.println("(TEST)Best Network "+(i+1)+":("+ listOfNeuralNetworks.get(i).getActivationSelectorLayer1()+
                        ","+listOfNeuralNetworks.get(i).getActivationSelectorLayer2()+","
                        +listOfNeuralNetworks.get(i).getActivationSelectorLayer3()
                        +"), EPOCH:" + listOfNeuralNetworks.get(i).getAnalsysErrorTest().size()+
                        ",LR: "+ listOfNeuralNetworks.get(i).getLearningRate()+",ERROR TYPE: "
                        +listOfNeuralNetworks.get(i).getErrorSelector()+
                        ", TOTAL LOSS:" +listOfNeuralNetworks.get(i).getAnalsysErrorTest().get(listOfNeuralNetworks.get(i).getAnalsysErrorTest().size()-1));
            }
            while(error){
                System.out.println("would you like to save networks? y/n");
                userInput = scan.nextLine();
                if(userInput.contains("y")){
                    System.out.println("Type in number of network to save");
                    while(error){
                        try{
                            userInput = scan.nextLine();
                            int networktoSave = Integer.parseInt(userInput);
                            if(networktoSave> 0 && networktoSave <listOfNeuralNetworks.size()){

                                saveNetwork(listOfNeuralNetworks.get(networktoSave-1),true);

                                System.out.println("Would you like to save another network? y/n");
                                userInput = scan.nextLine();
                                error = userInput.contains("y");
                            }
                            else{
                                System.out.println("Enter a valid network");
                            }
                        }catch (NumberFormatException e){
                            System.out.println("Enter a number");
                            error = true;
                        }
                    }

                }
                else if(userInput.contains("n")){
                    error =false;
                }
            }

        }
        else if(selector == 3){
            nn loadedNeural =  loadNetwork();
            if((loadedNeural.getInputLayerSize() != this.dataSelected.getInputArraySize()) || (loadedNeural.getOutputSize() != this.dataSelected.getOutputArraySize())){
                System.out.println("Either the input or Output's do not match and therefore");
                System.out.println("The network cannot be loaded.");
            }
            else{
                System.out.println("Would you like to start Training? y/n");
                this.error = true;
                while(error){
                    userInput = scan.nextLine();
                    if(userInput.contains("y")){
                        setEpoch();
                        loadedNeural.trainNetworkData(this.dataSelected,this.epochs,this.isSplit, this.analysis);
                        listOfNeuralNetworks.add(loadedNeural);
                        error = false;

                    }
                    else if(userInput.contains("n")){
                        error = false;
                    }
                }
            }
        }

        saveDataFromTraining();
    }

    private void saveDataFromTraining() throws IOException {
        this.error = true;
        while(error){
            System.out.println("Would you like to Save the Details from the Training and Testing? y/n");
            userInput = scan.nextLine();
            if(userInput.contains("y")){
                FileWriter trainingData = new FileWriter("trainingData.csv");
                FileWriter testData = new FileWriter("testData.csv");
                for(int j = 0; j < listOfNeuralNetworks.get(0).getAnalsysErrorTest().size()+1;j++) {
                    for (int i = 0; i < listOfNeuralNetworks.size(); i++) {
                        if(j == 0 ){
                            testData.append("N:"+(i+1)+"-lr:"+listOfNeuralNetworks.get(i).getLearningRate()+"-("+
                                    listOfNeuralNetworks.get(i).getActivationSelectorLayer1()+"-"+
                                    listOfNeuralNetworks.get(i).getActivationSelectorLayer2()+"-"+
                                    listOfNeuralNetworks.get(i).getActivationSelectorLayer3()+")-Hlayers("+
                                    listOfNeuralNetworks.get(i).getHiddenLayer1size()+"-"+
                                    listOfNeuralNetworks.get(i).getHiddenLayer2size()+"), ");
                        }
                        else {
                            testData.append(String.valueOf(listOfNeuralNetworks.get(i).getAnalsysErrorTest().get(j - 1))).append(",");
                        }
                    }
                    testData.append("\n");
                }
                testData.close();
                for(int j = 0; j < listOfNeuralNetworks.get(0).getAnalysisErrorTraining().size()+1;j++) {
                    for (int i = 0; i < listOfNeuralNetworks.size(); i++) {
                        if(j == 0 ){
                            trainingData.append("N:"+(i+1)+"-lr:"+listOfNeuralNetworks.get(i).getLearningRate()+"-("+
                                    listOfNeuralNetworks.get(i).getActivationSelectorLayer1()+"-"+
                                    listOfNeuralNetworks.get(i).getActivationSelectorLayer2()+"-"+
                                    listOfNeuralNetworks.get(i).getActivationSelectorLayer3()+")-Hlayers("+
                                    listOfNeuralNetworks.get(i).getHiddenLayer1size()+"-"+
                                    listOfNeuralNetworks.get(i).getHiddenLayer2size()+"), ");
                        }
                        else {
                            trainingData.append(String.valueOf(listOfNeuralNetworks.get(i).getAnalysisErrorTraining().get(j-1))).append(",");
                        }
                    }
                    trainingData.append("\n");
                }
                trainingData.close();
                error = false;
            }
            else if(userInput.contains("n")){
                error = false;
            }
            else{
                System.out.println("Enter : y/n");
            }
        }
    }

    private void defaultSetup(){
        layerSizes.add(dataSelected.getInputArraySize());
        setHiddenLayers(false);
        setEpoch();
        setListOfActivationFunctions(false);
        setLearningRate(false);
        setUpError(false);
        this.layerSizes.add(this.dataSelected.getOutputArraySize());
    }

    private void customSetup() {
        setHiddenLayers(true);
        setEpoch();
        setListOfActivationFunctions(true);
        setLearningRate(true);
        setUpError(true);
        int layerOneNeuronRange = listOfHiddenLayerSizes.get(0) - minimumLayerSizes.get(0);
        if(listOfHiddenLayerSizes.size() > 1) {
            int layerTwoNeuronRange = listOfHiddenLayerSizes.get(1) - minimumLayerSizes.get(1);
            ArrayList<Integer> layerTwoRangeOfNeurons = new ArrayList<Integer>(layerTwoNeuronRange);
        }
        ArrayList<Integer> layerOneRangeOfNeurons = new ArrayList<Integer>();
        ArrayList<Integer> layerTwoRangeOfNeurons = new ArrayList<Integer>();

        for( int i = 0  ; i < listOfHiddenLayerSizes.size() ; i++ ){
            //Get the range of Layer One neurons into a list
            if(i == 0) {
                for (int j = minimumLayerSizes.get(i); j < listOfHiddenLayerSizes.get(i); j++){
                    layerOneRangeOfNeurons.add(j);
                }
            }
            if(i == 1){
                for(int j = minimumLayerSizes.get(i) ; j < listOfHiddenLayerSizes.get(i); j++){
                    layerTwoRangeOfNeurons.add(j);
                }
            }
        }

        //Create All combinations
        for(int layer1neuronsIndex = 0; layer1neuronsIndex < layerOneRangeOfNeurons.size() ; layer1neuronsIndex++){
            if(listOfHiddenLayerSizes.size() >1) {
                for (int layer2neuronsIndex = 0; layer2neuronsIndex < layerTwoRangeOfNeurons.size(); layer2neuronsIndex++) {
                    for (int learningRateIndex = 0; learningRateIndex < listOFLearningRates.size(); learningRateIndex++) {
                        for (int activationFunctionIndexLayer1 = 0; activationFunctionIndexLayer1 < listOfActivationFunctions.size(); activationFunctionIndexLayer1++) {
                            for (int activationFunctionIndexLayer2 = 0; activationFunctionIndexLayer2 < listOfActivationFunctions.size(); activationFunctionIndexLayer2++) {
                                for(int activationFunctionIndexLayer3 = 0; activationFunctionIndexLayer3< listOfActivationFunctions.size(); activationFunctionIndexLayer3++){
                                    for(int errorIndex = 0 ; errorIndex < listOfErrors.size(); errorIndex++){
                                        layerSizes.clear();
                                        layerSizes.add(this.dataSelected.getInputArraySize());
                                        layerSizes.add(layerOneRangeOfNeurons.get(layer1neuronsIndex));
                                        layerSizes.add(layerTwoRangeOfNeurons.get(layer2neuronsIndex));
                                        layerSizes.add(this.dataSelected.getOutputArraySize());
                                        nn network = new nn(layerSizes);
                                        network.setErrorCalculator(listOfErrors.get(errorIndex));
                                        network.setActivationSelectorLayer3(listOfActivationFunctions.get(activationFunctionIndexLayer3));
                                        network.setActivationSelectorLayer2(listOfActivationFunctions.get(activationFunctionIndexLayer2));
                                        network.setActivationSelectorLayer1(listOfActivationFunctions.get(activationFunctionIndexLayer1));
                                        network.setLearningRate(listOFLearningRates.get(learningRateIndex));
                                        listOfNeuralNetworks.add(network);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else{
                for (int learningRateIndex = 0; learningRateIndex < listOFLearningRates.size(); learningRateIndex++) {
                    for (int activationFunctionIndexLayer1 = 0; activationFunctionIndexLayer1 < listOfActivationFunctions.size(); activationFunctionIndexLayer1++) {
                        for (int activationFunctionIndexLayer2 = 0; activationFunctionIndexLayer2 < listOfActivationFunctions.size(); activationFunctionIndexLayer2++) {
                            for(int errorIndex = 0 ; errorIndex < listOfErrors.size(); errorIndex++){
                                layerSizes.clear();
                                layerSizes.add(this.dataSelected.getInputArraySize());
                                layerSizes.add(layerOneRangeOfNeurons.get(layer1neuronsIndex));
                                layerSizes.add(this.dataSelected.getOutputArraySize());
                                nn network = new nn(layerSizes);
                                network.setErrorCalculator(listOfErrors.get(errorIndex));
                                network.setActivationSelectorLayer2(listOfActivationFunctions.get(activationFunctionIndexLayer2));
                                network.setActivationSelectorLayer1(listOfActivationFunctions.get(activationFunctionIndexLayer1));
                                network.setLearningRate(listOFLearningRates.get(learningRateIndex));
                                listOfNeuralNetworks.add(network);
                            }
                        }
                    }
                }
            }
        }

        System.out.println("Generated :"+listOfNeuralNetworks.size()+ " Neural Networks");


    }

    private void saveNetwork(nn saveNetwork, boolean isList) throws Exception {
        this.error = true;
        if(!isList) {
            while (error) {

                System.out.println("Save Network: y/n");
                userInput = scan.nextLine();
                if (userInput.equals("y")) {
                    System.out.println("Type in the file name ");

                    String saveName = scan.nextLine();
                    saveNetwork.save(saveName);
                    error = false;
                } else if (userInput.equals("n")) {
                    error = false;
                }
            }
        }
        else{
            System.out.println("Type in the file name ");
            String saveName = scan.nextLine();
            saveNetwork.save(saveName);
        }
    }

    public void setEpoch(){
        this.error = true;
        int numberOfNN;
        while (error) {

            System.out.println("Type in the amount of epochs:");
            userInput = scan.nextLine();


            try {
                numberOfNN = Integer.parseInt(userInput);
                this.epochs = numberOfNN;
                error = false;
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid number");
                error = true;
            }
        }
    }
    public void setLearningRate(boolean isCustom){
        this.error = true;
        while (error) {
            System.out.println("Type in the learning Rate: ");
            userInput = scan.nextLine();
            try {
                learningRate = Double.parseDouble(userInput);
                listOFLearningRates.add(learningRate);
                if(!isCustom) {
                    error = false;
                }
                else{
                    System.out.println("Do you want to add another learning Rate? y/n");
                    userInput = scan.nextLine();
                    if(userInput.contains("y")){
                        error = true;
                    }
                    else if(userInput.contains("n")){
                        error = false;
                    }

                }

            } catch (NumberFormatException e) {
                System.out.println("Enter a valid number");
                error = true;

            }
        }
    }
    public void setShuffle(){
        this.error = true;
        while (error) {
            System.out.println("Do you want to shuffle the data: y/n ");
            userInput = scan.nextLine();
            if(userInput.equals("y")){
                this.isShuffle = true;
                this.dataSelected.shuffle(this.isSplit);
                this.error = false;
            }
            else if(userInput.equals("n")){
                this.isShuffle = false;
                this.error = false;
            }
        }
    }
    public void setSplit(){
        this.error = true;
        while (error) {
            System.out.println("Do you want to Split the Data: y/n ");
            userInput = scan.nextLine();
            if(userInput.equals("y")){
                this.isSplit = true;

                while(error) {
                    try {
                        System.out.println("Type in Split Percentage(Between 1-0)");
                        userInput = scan.nextLine();
                        double split = Double.parseDouble(userInput);
                        if (split > 0 && split < 1) {
                            dataSelected.splitData(split);
                            this.error = false;
                        }
                    } catch (NumberFormatException e) {
                        this.error = true;
                        System.out.println("Enter a valid Number");
                    }
                }
            }
            else if(userInput.equals("n")){
                this.isSplit = false;
                this.error = false;
            }
        }
    }

    public void setListOfActivationFunctions(boolean isCustom){
        int numberOfNN;
        this.error = true;
        System.out.println("Activation Functions:");
        System.out.println("1: Sigmoid");
        System.out.println("2: Hyperbolic Tangent ");
        System.out.println("3: Leaky ReLU");
        System.out.println("4: Relu");
        if(!isCustom) {
            while (error) {
                for (int i = 0; i <= listOfHiddenLayerSizes.size(); i++) {
                    System.out.println("Type in the Activation Function for layer: " + i);
                    try {
                        userInput = scan.nextLine();
                        numberOfNN = Integer.parseInt(userInput);
                        if (numberOfNN < 5 && numberOfNN > 0) {
                            listOfActivationFunctions.add(numberOfNN);
                            error = false;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Enter a valid number");
                        error = true;
                    }
                }
            }
        }
        else{
            while (error) {
                System.out.println("Type in the Activation Function to Add");
                try {
                    userInput = scan.nextLine();
                    numberOfNN = Integer.parseInt(userInput);
                    if (numberOfNN < 5 && numberOfNN > 0 && !listOfActivationFunctions.contains(numberOfNN)) {
                        listOfActivationFunctions.add(numberOfNN);

                        System.out.println("Would you liketo add another Activation: y/n");

                        userInput = scan.nextLine();

                        if(userInput.contains("y")){
                            error = true;
                        }else if(userInput.equals("n")){
                            error = false;
                        }

                    }else{
                        System.out.println("Activation Function selected is either Invalid or already selected: ");
                        System.out.println("Do you wish to continue selection: y/n");
                        userInput = scan.nextLine();
                        error= userInput.contains("y");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Enter a valid number");
                    error = true;
                }

            }
        }
    }

    public void setHiddenLayers(boolean isCustom){
        int numberOfNN;
        this.error = true;
        while (error) {

            System.out.println("How many hidden layers : 1/2");
            userInput = scan.nextLine();


            try {
                numberOfNN = Integer.parseInt(userInput);
                if (numberOfNN == 1 || numberOfNN == 2) {
                    this.numberofHiddenLayers = numberOfNN;
                    error = false;
                }

            } catch (NumberFormatException e) {
                System.out.println("Enter a valid number");
                error = true;
            }
        }


        this.error = true;
        while (error) {
            for (int i = 0; i < this.numberofHiddenLayers; i++) {
                System.out.println("Whats the maximum number of neurons in hidden layer: " + (i + 1));

                try {
                    userInput = scan.nextLine();
                    numberOfNN = Integer.parseInt(userInput);
                    this.layerSizes.add(numberOfNN);
                    this.listOfHiddenLayerSizes.add(numberOfNN);
                    error = false;
                } catch (NumberFormatException e) {
                    System.out.println("Enter a valid number");
                    error = true;
                }

                if(isCustom){
                    System.out.println("whats the minimum number of neurons in hidden layer: "+ (i+1));
                    try {
                        userInput = scan.nextLine();
                        numberOfNN = Integer.parseInt(userInput);
                        this.minimumLayerSizes.add(numberOfNN);
                        error = false;
                    } catch (NumberFormatException e) {
                        System.out.println("Enter a valid number");
                        error = true;
                    }
                }
            }
        }
    }

    private void setUpError(boolean isCustom){
        this.error = true;
        int selectedError= 0;
        System.out.println("Enter the Error Type to use:");
        System.out.println("1: Standard (Guessed - True)");
        System.out.println("2: Mean Square Error");
        while (error) {
            userInput = scan.nextLine();
            try {
                selectedError = Integer.parseInt(userInput);
                if (selectedError < 5 && selectedError > 0 && !listOfErrors.contains(selectedError)) {
                    this.selectedError = selectedError;
                    listOfErrors.add(selectedError);
                    error = false;
                }
                if(!isCustom) {
                    error = false;
                }
                else{
                    System.out.println("Do you want to add another Error Function? y/n");
                    userInput = scan.nextLine();
                    if(userInput.contains("y")){
                        System.out.println("Type in the next To use");
                        error = true;
                    }
                    else if(userInput.contains("n")){
                        error = false;
                    }

                }

            } catch (NumberFormatException e) {
                System.out.println("Enter a valid number");
                error = true;

            }
        }
    }

    public nn loadNetwork() throws IOException, ParseException {
        this.error = true;
        while(error){

            System.out.println("Enter the file name for the neural network");
            System.out.println("must be configured to a json file -- derived from this program");


            userInput = scan.nextLine();

            File fileDirectory = new File(userInput+".json");
            System.out.println(fileDirectory.getName());

            if(!fileDirectory.exists()){
                error = true;
                System.out.println("Enter a Valid File");
            }
            else{
                error = false;
            }
        }

        nn load = new nn();
        load.loadNetwork(userInput+".json");
        return load;
    }
}
