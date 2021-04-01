/*
This class describes the flow of data through a neural network
 */

package model;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class nn implements Serializable {

    private ArrayList<Integer> listOfLayerSizes;
    private int inputLayerSize;
    private int outputLayerSize;
    private int totalLayerSize;
    private Matrix in;
    private Matrix layer1Weights;
    private Matrix layer2Weights;
    private Matrix layer3Weights;
    private Matrix layer1outputs;
    private Matrix layer2outputs;
    private Matrix layer3outputs;
    private double layer1Bias;
    private double layer2Bias;
    private double layer3Bias;
    private Matrix derivedOutputErrorLayer1;
    private Matrix derivedOutputErrorLayer2;
    private Matrix derivedOutputLayer3;
    private Matrix errorDeltaLayer1;
    private Matrix errorDeltaLayer2;
    private Matrix errorDeltaLayer3;
    private int activationSelectorLayer1;
    private int activationSelectorLayer2;
    private int activationSelectorLayer3;
    private int errorSelector ;
    private final ArrayList<Double> listOFlossTest = new ArrayList<Double>();
    private final ArrayList<Double> listOFTrainingLoss = new ArrayList<Double>();
    private Matrix outputMatrix;
    private double learningRate;
    private Double finalErrorCalculationTest;
    private Double getFinalErrorCalculationTraining;

    public nn(){
        //Empty constructor for loading
    }

    public nn(ArrayList<Integer> layerSizes){
        //List of all layers
        this.listOfLayerSizes = layerSizes;
        //input Layer Size
        this.inputLayerSize = listOfLayerSizes.get(0);
        //Output Layer size
        this.outputLayerSize = layerSizes.get(listOfLayerSizes.size()-1);
        //Size of the list of layers
        this.totalLayerSize = listOfLayerSizes.size();

        //initiate Random Variables
        Matrix in = new Matrix(inputLayerSize,1);
        this.layer1Weights = new Matrix(inputLayerSize,listOfLayerSizes.get(1));
        this.layer2Weights = new Matrix(listOfLayerSizes.get(1),listOfLayerSizes.get(2));
        //Randomise the initial weights
        this.layer1Weights.randomizeElements();
        this.layer2Weights.randomizeElements();
        Random rand = new Random();
        this.layer1Bias = rand.nextDouble();
        this.layer2Bias = rand.nextDouble();

        this.activationSelectorLayer1= 2;
        this.activationSelectorLayer2 = 2;
        this.errorSelector = 1;

        if(layerSizes.size()==4){
            this.layer3Weights = new Matrix(listOfLayerSizes.get(2),listOfLayerSizes.get(3));
            this.layer3Weights.randomizeElements();
            this.layer3Bias = rand.nextDouble();
            this.outputMatrix = new Matrix(1,listOfLayerSizes.get(3));

            //Default the activation Functions to sigma
            this.activationSelectorLayer3 = 2;
            //Default error calculator Selector
        }
        else{
            this.outputMatrix = new Matrix(1,listOfLayerSizes.get(2));
        }
    }

    public double[] forwardPass(double[] input) {
        double[] output;
        if (listOfLayerSizes.size() > 2) {

            this.in = new Matrix(1, input.length);
            for (int i = 0; i < input.length; i++) {
                in.setMatrixElement( input[i], 0, i);
            }

            //Calculate the sum of all weights going into next layer neurons
            this.layer1outputs = Matrix.MatMult(in,layer1Weights);
            //Initiate a similar  matrix to hold the derivatives to be used for updating the weights
            this.derivedOutputErrorLayer1 = new Matrix(layer1outputs.getRows(),layer1outputs.getColumns());

            //Need to go to each element within the outputs for layer one to apply the sigmoid function to each value
            for (int i = 0; i < layer1outputs.getRows(); i++) {
                for (int j = 0; j < layer1outputs.getColumns(); j++) {

                    //Applying the activation function to the output for the first layer and store into Matrix
                    layer1outputs.setMatrixElement( activationFunction(layer1outputs.getMatrixElement(i, j)+layer1Bias,
                            activationSelectorLayer1), i, j);

                    //Calculate the current layers derivative value, dependant on whih activation function selected
                    this.derivedOutputErrorLayer1.setMatrixElement(derivedActivationFunction(layer1outputs,i,j,
                            activationSelectorLayer1),i,j);
                }
            }

            //Repeat for the second layer
            this.layer2outputs = Matrix.MatMult(layer1outputs,layer2Weights);
            this.derivedOutputErrorLayer2 = new Matrix(layer2outputs.getRows(),layer2outputs.getColumns());
            for (int i = 0; i < layer2outputs.getRows(); i++) {
                for (int j = 0; j < layer2outputs.getColumns(); j++) {

                    layer2outputs.setMatrixElement(activationFunction(layer2outputs.getMatrixElement(i, j)+layer2Bias,
                            activationSelectorLayer2), i, j);

                    this.derivedOutputErrorLayer2.setMatrixElement(derivedActivationFunction(layer2outputs,i,j,
                            activationSelectorLayer2),i,j);
                }
            }

            if(listOfLayerSizes.size() == 4){
                //Do the same if there is another hidden layer

                this.outputMatrix = new Matrix(1,listOfLayerSizes.get(3));
                //Initiate the output the neural network is going to calculate with same dimensions
                //As the final layer given upon initiation
                output = new double[listOfLayerSizes.get(3)];
                this.layer3outputs = Matrix.MatMult(layer2outputs,layer3Weights);
                this.derivedOutputLayer3 = new Matrix(layer3outputs.getRows(),layer3outputs.getColumns());

                for (int i = 0; i < layer3outputs.getRows(); i++) {
                    for (int j = 0; j < layer3outputs.getColumns(); j++) {

                        layer3outputs.setMatrixElement(activationFunction(layer3outputs.getMatrixElement(i, j)+layer3Bias,
                                activationSelectorLayer3), i, j);
                        this.derivedOutputLayer3.setMatrixElement(derivedActivationFunction(layer3outputs,i,j,
                                activationSelectorLayer3),i,j);

                    }
                }

                for (int i = 0; i < output.length; i++) {
                    output[i] = layer3outputs.getMatrixElement(0,i);
                    outputMatrix.setMatrixElement(layer3outputs.getMatrixElement(0,i),0,i);
                }

            }else {
                //This is if the second layer is the final layer
                output = new double[listOfLayerSizes.get(2)];
                this.outputMatrix = new Matrix(1,listOfLayerSizes.get(2));
                for (int i = 0; i < output.length; i++) {
                    output[i] = layer2outputs.getMatrixElement(0, i);
                    outputMatrix.setMatrixElement(layer2outputs.getMatrixElement(0,i),0,i);
                }
            }
            return output;
        }
        double [] empty = new double[0];
        return empty;
    }


    public void backPropagation(double[] trueOutput){
        //Define the error in final layer
        if(listOfLayerSizes.size()==4){
            this.errorDeltaLayer3 = new Matrix(1,layer3outputs.getColumns());
            Matrix convertOutput = convertTrueOutput(trueOutput);
            Matrix layer3errors = outputErrorCalculation(layer3outputs,convertOutput,1);
            for(int i = 0; i < errorDeltaLayer3.getRows(); i++){


                for(int j = 0; j < errorDeltaLayer3.getColumns(); j++){
                    errorDeltaLayer3.setMatrixElement(layer3errors.getMatrixElement(i,j)*
                            derivedOutputLayer3.getMatrixElement(i,j),i,j);
                }
            }
            Matrix layer3WeightsTransposed = Matrix.transpose(layer3Weights);


            //The layer 2  error is the weight matrix from layer 3 to layer 2 (or 2 to 3) * the errors from layer 3;
            //Need the dimensions to be correct, so needs transposing
            this.errorDeltaLayer2 = Matrix.MatMult(errorDeltaLayer3,layer3WeightsTransposed);
            for(int i = 0; i < errorDeltaLayer2.getRows(); i++){
                for(int j = 0; j < errorDeltaLayer2.getColumns(); j++){
                    errorDeltaLayer2.setMatrixElement(
                            errorDeltaLayer2.getMatrixElement(i,j)*derivedOutputErrorLayer2.getMatrixElement(i,j),i,j);
                }
            }

            //Layer 1 error is the weights from layer one to two(But backwards) * the errors from layer 2;
            Matrix layer2weightsTransposed = Matrix.transpose(layer2Weights);
            this.errorDeltaLayer1 = Matrix.MatMult(errorDeltaLayer2,layer2weightsTransposed);

            for(int i = 0; i < errorDeltaLayer1.getRows(); i++){
                for(int j = 0; j < errorDeltaLayer1.getColumns(); j++){
                    errorDeltaLayer1.setMatrixElement(
                            errorDeltaLayer1.getMatrixElement(i,j)*derivedOutputErrorLayer1.getMatrixElement(i,j),i,j);
                }
            }

        }
        if(listOfLayerSizes.size() == 3){

            Matrix convertOutput = convertTrueOutput(trueOutput);
            Matrix layer2errors = outputErrorCalculation(layer2outputs,convertOutput,1);
            this.errorDeltaLayer2 = new Matrix(1,layer2outputs.getColumns());
            for(int i = 0; i < errorDeltaLayer2.getRows(); i++){
                for(int j = 0; j < errorDeltaLayer2.getColumns(); j++){

                    errorDeltaLayer2.setMatrixElement(layer2errors.getMatrixElement(i,j)*
                            derivedOutputErrorLayer2.getMatrixElement(i,j),i,j);
                }
            }

            Matrix layer2weightsTransposed = Matrix.transpose(layer2Weights);
            this.errorDeltaLayer1 = Matrix.MatMult(errorDeltaLayer2,layer2weightsTransposed);

            for(int i = 0; i < errorDeltaLayer1.getRows(); i++){
                for(int j = 0; j < errorDeltaLayer1.getColumns(); j++){
                    errorDeltaLayer1.setMatrixElement(
                            errorDeltaLayer1.getMatrixElement(i,j)*derivedOutputErrorLayer1.getMatrixElement(i,j),i,j);
                }
            }

        }
    }

    public void changeWeights(double learningRate){
        Matrix transposedlayer1error = Matrix.transpose(errorDeltaLayer1);
        Matrix deltaLayer1 = Matrix.transpose(Matrix.MatMult(transposedlayer1error,in));
        deltaLayer1.matrixScale(-learningRate);
        layer1Weights.matrixAddMatrix(deltaLayer1);

        Matrix transposedlayer2error = Matrix.transpose(errorDeltaLayer2);
        Matrix deltaLayer2 = Matrix.transpose(Matrix.MatMult(transposedlayer2error,layer1outputs));
        deltaLayer2.matrixScale(-learningRate);
        layer2Weights.matrixAddMatrix(deltaLayer2);

        if(listOfLayerSizes.size() == 4) {
            Matrix transposedlayer3error = Matrix.transpose(errorDeltaLayer3);
            Matrix deltaLayer3 = Matrix.transpose(Matrix.MatMult(transposedlayer3error, layer2outputs));
            deltaLayer3.matrixScale(-learningRate);
            layer3Weights.matrixAddMatrix(deltaLayer3);
        }



    }

    private double activationFunction(double z, int selector){
        switch (selector){
            case 1: //Sigmoid Function : 1/(1+e^(-x))
                return 1d / (1 + Math.exp(-z));

            case 2: //Hyperbolic Tangent tanH : (e^x - e^(-x))/(e^x + e^(-x))
                return (Math.exp(z)-Math.exp(-z))/ (Math.exp(z)+ Math.exp(-z));

            case 3: //Leaky Relu
                if(z > 0){
                    return z;
                }
                else {
                    return z * 0.01;
                }
            case 4: //Relu
                if(z > 0){
                    return z;
                }
                else{
                    return 0;
                }
        }
        return -1;
    }

    private double derivedActivationFunction (Matrix outputsFromLayer, int row, int column , int selector){

        switch (selector){
            case 1:
                //Derived Sigmoid Function :f'(x) = f(x)*(1-f(x))
                return outputsFromLayer.getMatrixElement(row,column)*(1-outputsFromLayer.getMatrixElement(row,column));
            case 2:
                //Derived hyperbolic Tangent : f'(x) = 1 - f(x)^2
                return 1-(outputsFromLayer.getMatrixElement(row,column)*outputsFromLayer.getMatrixElement(row,column));
            case 3:
                //Dervied Leaky Relu
                if(outputsFromLayer.getMatrixElement(row,column) < 0){
                    return 0.25;
                }
                else{
                    return 1;
                }
            case 4:
                //Relu
                if(outputsFromLayer.getMatrixElement(row,column) < 0){
                    return 0;
                }
                else{
                    return 1;
                }

        }

        return 0.0;
    }

    public static Matrix outputErrorCalculation(Matrix guessedOutput, Matrix trueOutput, int errorSelector) {
        double x = 0;
        Matrix errorsCalculated = new Matrix(guessedOutput.getRows(), guessedOutput.getColumns());

        switch (errorSelector) {
            case 1: //Calculate normal difference
                for (int i = 0; i < guessedOutput.getColumns(); i++) {
                    x = guessedOutput.getMatrixElement(0, i) - trueOutput.getMatrixElement(0, i);
                    errorsCalculated.setMatrixElement(x, 0, i);
                }
                return errorsCalculated;

            case 2: //Mean Squared Error
                for (int i = 0; i < guessedOutput.getColumns(); i++) {
                    x += (trueOutput.getMatrixElement(0, i) - guessedOutput.getMatrixElement(0, i)) *
                            (trueOutput.getMatrixElement(0, i) - guessedOutput.getMatrixElement(0, i));
                }
                x = x / (2d * trueOutput.getColumns());
                for (int i = 0; i < guessedOutput.getColumns(); i++) {
                    errorsCalculated.setMatrixElement(x, 0, i);

                }
                return errorsCalculated;
        }
        return errorsCalculated;
    }

    public static Matrix convertTrueOutput(double[] trueOutput){
        Matrix convertedOutput = new Matrix(1, trueOutput.length);
        for(int i = 0 ; i < trueOutput.length ; i++){
            convertedOutput.setMatrixElement(trueOutput[i],0,i);
        }
        return convertedOutput;
    }

    public void trainNetwork(double[] input, double[] trueOutput, double learningRate){
        forwardPass(input);
        backPropagation(trueOutput);
        changeWeights(learningRate);
    }

    private double getAnalysis(dataSet data, boolean isSplit, int dataSize ,boolean isTraining) {
        int size = data.getSize();
        int testSize = data.getTestDataSize();
        int trainingSize = data.getTrainingDataSize() ;
        double totalLoss = 0;
        if(!isTraining) {
            if (isSplit) {
                for (int j = 0; j < testSize; j++) {
                    totalLoss += testNetwork(data.getTestInputData(j), data.getTestOutputData(j), dataSize);
                }
                if (errorSelector > 1) {
                    return totalLoss / dataSize;
                } else {
                    return totalLoss;
                }
            } else {
                for (int j = 0; j < size; j++) {
//                System.out.println("---------");
                    totalLoss += testNetwork(data.getI(j), data.getO(j), dataSize);
//               System.out.println(totalLoss);
                }
                if (errorSelector > 1) {
                    return totalLoss / dataSize;
                } else {
                    return totalLoss;
                }
//            System.out.println("---------");
            }
        }
        else{
            if (isSplit) {
                for (int j = 0; j < trainingSize; j++) {
                    totalLoss += testNetwork(data.getTrainingInputData(j), data.getTrainingOutputData(j), dataSize);
                }
                if (errorSelector > 1) {

                    return totalLoss / dataSize;
                } else {
                    return totalLoss;
                }
            } else {
                for (int j = 0; j < size; j++) {
//
                    totalLoss += testNetwork(data.getI(j), data.getO(j), dataSize);
//
                }
                System.out.println(totalLoss);
                if (errorSelector > 1) {
                    return totalLoss / dataSize;
                } else {
                    return totalLoss;
                }
//            System.out.println("---------");
            }
        }
    }

    private double testNetwork(double[] input, double[] output,int dataSize){
        forwardPass(input);
        double total =0;
        Matrix convertOutput = convertTrueOutput(output);
        if(listOfLayerSizes.size() ==4) {
            Matrix layer3errors = outputErrorCalculation(layer3outputs, convertOutput, this.errorSelector);
//            layer3errors.printOut();
//            System.out.println("-----------");
            for(int i = 0; i < layer3errors.getColumns(); i++){
//                System.out.println(total);
                total += layer3errors.getMatrixElement(0,i);
            }
//            System.out.println("total: "+ total);
//            System.out.println("--------------");
            return total;
        }
        else{
            Matrix layer2errors = outputErrorCalculation(layer2outputs, convertOutput, this.errorSelector);
            for(int i = 0; i < layer2errors.getColumns(); i++){
                total += layer2errors.getMatrixElement(0,i);
            }
            return total;

        }

    }

    public void trainNetworkData(dataSet data, int epochs,boolean isSplit, boolean analysis){
        int size = data.getSize();
        int trainingSize = data.getTrainingDataSize();
        if(isSplit) {
            for (int i = 0; i < epochs; i++) {

                for (int j = 0; j < trainingSize; j++) {
                    this.trainNetwork(data.getTrainingInputData(j), data.getTrainingOutputData(j), this.learningRate);

                }
                if(analysis){
                    this.listOFTrainingLoss.add(getAnalysis(data,true,data.getTrainingDataSize(),true));
                    this.listOFlossTest.add(getAnalysis(data,true, data.getTestDataSize(),false));
                }

            }
            this.finalErrorCalculationTest = listOFlossTest.get(epochs-1);
            this.getFinalErrorCalculationTraining = listOFTrainingLoss.get(epochs-1);
        }else
        {
            for (int i = 0; i < epochs; i++) {
                for (int j = 0; j < size; j++) {
//                this.trainNetwork(data.getInput(j), data.getOutput(j), 0.3);
                    this.trainNetwork(data.getI(j), data.getO(j), this.learningRate);

                }
                if(analysis){
                    this.listOFTrainingLoss.add(getAnalysis(data,false,data.getSize(),true));
                    this.listOFlossTest.add(getAnalysis(data,false,data.getSize(),false));
//                    System.out.println(listOFlossTest.get(i));
                }
            }
            this.finalErrorCalculationTest = listOFlossTest.get(epochs-1);
            this.getFinalErrorCalculationTraining = listOFTrainingLoss.get(epochs-1);
        }


    }

    @SuppressWarnings("unchecked")
    public void save(String fileName) throws Exception {
        JSONObject networkLayers = new JSONObject();
        //The topology
        networkLayers.put(1, this.inputLayerSize);
        networkLayers.put(2,this.listOfLayerSizes.get(1));
        networkLayers.put(3,this.listOfLayerSizes.get(2));
        if(this.totalLayerSize > 3){
            networkLayers.put(4,this.listOfLayerSizes.get(3));
        }


        //Store the weights
        JSONObject weights = new JSONObject();
        weights.put(1,this.layer1Weights.saveMatrix());
//        layer1Weights.printOut();
        weights.put(2,this.layer1Bias);
        weights.put(3,this.layer2Weights.saveMatrix());
        weights.put(4,this.layer2Bias);
        if(this.totalLayerSize > 3) {
            weights.put(5, this.layer3Weights.saveMatrix());
            weights.put(6, this.layer3Bias);
        }


        //Save the activation functions used for each layer
        JSONObject activationSelector = new JSONObject();
        activationSelector.put(1,this.activationSelectorLayer1);
        activationSelector.put(2,this.activationSelectorLayer2);
        if(this.totalLayerSize > 3) {
            activationSelector.put(3, this.activationSelectorLayer3);
        }

        JSONObject errorSelector = new JSONObject();
        errorSelector.put(1,this.errorSelector);

        JSONObject saveLearningRate = new JSONObject();
        saveLearningRate.put(1,this.learningRate);

        //Add all details to the file
        JSONObject sN = new JSONObject();
        sN.put("layers", networkLayers);
        sN.put("weights", weights);
        sN.put("activations", activationSelector);
        sN.put("error", errorSelector);
        sN.put("learningRate", saveLearningRate);

        FileWriter file = new FileWriter(fileName+".json");
        file.write(sN.toJSONString());
        file.flush();
        file.close();


    }

    public void loadNetwork(String filename) throws IOException, ParseException {
        org.json.simple.parser.JSONParser jsonp = new org.json.simple.parser.JSONParser();

        this.listOfLayerSizes = new ArrayList<Integer>();
        FileReader file = new FileReader(filename);
        JSONObject loadN = (JSONObject) jsonp.parse(file);
        System.out.println(loadN);
        System.out.println(loadN.get("layers"));


        JSONObject loadLayers = (JSONObject) loadN.get("layers");
//        String placeholder = (String) loadLayers.get("1");
        int Currentlayer = Integer.parseInt(loadLayers.get("1").toString());
        this.listOfLayerSizes.add(0, Currentlayer);
        this.inputLayerSize = Currentlayer;

        Currentlayer = Integer.parseInt(loadLayers.get("2").toString());
        this.listOfLayerSizes.add(1, Currentlayer);
//
        Currentlayer = Integer.parseInt(loadLayers.get("3").toString());
        this.listOfLayerSizes.add(2, Currentlayer);
        this.outputLayerSize = Currentlayer;


//
//
        if (loadLayers.containsKey("4")) {
            Currentlayer = Integer.parseInt(loadLayers.get("4").toString());
            this.listOfLayerSizes.add(3, Currentlayer);
            this.outputLayerSize = Currentlayer;
        }
//
//
        //Load in the Weights and bias
        JSONObject loadWeightJson = (JSONObject) loadN.get("weights");
        JSONArray loadWeightsArray = (JSONArray) loadWeightJson.get("1");
        this.layer1Weights = Matrix.loadMatrix(loadWeightsArray);
//
        double bias = Double.parseDouble(loadWeightJson.get("2").toString());
        this.layer1Bias = bias;
//
        loadWeightsArray = (JSONArray) loadWeightJson.get("3");
        this.layer2Weights = Matrix.loadMatrix(loadWeightsArray);

        bias = Double.parseDouble(loadWeightJson.get("4").toString());
        this.layer2Bias = bias;


        if (loadWeightJson.containsKey("5")) {
//            System.out.println("here");
            loadWeightsArray = (JSONArray) loadWeightJson.get("5");
            this.layer3Weights = Matrix.loadMatrix(loadWeightsArray);

            bias = Double.parseDouble(loadWeightJson.get("6").toString());
            this.layer3Bias = bias;
        }

        //Load in activation Settings

        JSONObject loadSettings = (JSONObject) loadN.get("activations");
//        JSONObject loadactivations = (JSONObject) loadLayers.get("1");
        Currentlayer = Integer.parseInt(loadSettings.get("1").toString());
        this.activationSelectorLayer1 = Currentlayer;

        Currentlayer = Integer.parseInt(loadSettings.get("2").toString());
        this.activationSelectorLayer2 = Currentlayer;

        if (loadSettings.containsKey("3"))
            Currentlayer = Integer.parseInt(loadSettings.get("3").toString());
        this.activationSelectorLayer3 = Currentlayer;

        JSONObject loadErrorSelector = (JSONObject) loadN.get("error");
        Currentlayer = Integer.parseInt(loadErrorSelector.get("1").toString());
        this.errorSelector = Currentlayer;

        JSONObject loadLearningRate = (JSONObject) loadN.get("learningRate");
        bias = Double.parseDouble(loadLearningRate.get("1").toString());
        this.learningRate = bias;
        System.out.println("Loaded");


    }

    public void setActivation(int layer, int activationSelected) {
        switch (layer){
            case 1:
                //Layer 1 activation function
                this.activationSelectorLayer1 = activationSelected;
                break;
            case 2:
                //Layer 2 activation function
                this.activationSelectorLayer2 = activationSelected;
                break;
            case 3:
                //Layer 3 activation function
                this.activationSelectorLayer3 = activationSelected;
                break;
        }
    }

    public void setLearningRate(double learningRate){
        this.learningRate = learningRate;
    }
    public double getLearningRate(){
        return this.learningRate;
    }
    public String getErrorSelector(){
        switch(this.errorSelector)
        {
            case 1:
                return "Default";
            case 2:
                return "MSE";
        }
        return "";
    }
    public String getActivationSelectorLayer1(){
        switch(this.activationSelectorLayer1){
            case 1:
                return "Sigmoid";
            case 2:
                return "TanH";

            case 3:
                return "LReLU";
            case 4:
                return "ReLU";

        }
        return "";
    }
    public String getActivationSelectorLayer2(){
        switch(this.activationSelectorLayer2){
            case 1:
                return "Sigmoid";
            case 2:
                return "TanH";

            case 3:
                return "LReLU";
            case 4:
                return "ReLU";

        }
        return "";
    }
    public String getActivationSelectorLayer3(){
        switch(this.activationSelectorLayer3){
            case 1:
                return "Sigmoid";
            case 2:
                return "TanH";

            case 3:
                return "LReLU";
            case 4:
                return "ReLU";

        }
        return "";
    }

    public void setActivationSelectorLayer1(int i){
        this.activationSelectorLayer1 = i;
    }

    public void setActivationSelectorLayer2(int i){
        this.activationSelectorLayer2 = i;
    }

    public void setActivationSelectorLayer3(int i){
        this.activationSelectorLayer3 = i;
    }

    public void setLayer1Weights(Matrix layer1){
        this.layer1Weights = layer1;
    }
    public void setLayer2Weights(Matrix layer2){
        this.layer2Weights = layer2;
    }
    public void setLayer3Weights(Matrix layer3){
        this.layer3Weights = layer3;
    }
    public void setInputLayerSize(int i ){
        this.inputLayerSize = i;
    }
    public void setOutputLayerSize(int i){
        this.outputLayerSize = i;
    }
    public void setListOfLayerSizes(ArrayList<Integer> list){
        this.listOfLayerSizes = list;
    }
    public void setErrorCalculator(int i){
        this.errorSelector = i;
    }
    public ArrayList<Double> getAnalsysErrorTest(){
        return listOFlossTest;
    }
    public ArrayList<Double> getAnalysisErrorTraining(){
        return listOFTrainingLoss;
    }
    public int getInputLayerSize(){
        return this.inputLayerSize;
    }

    public int getOutputSize() {
        return this.outputLayerSize;
    }
    public double getFinalErrorCalTest(){
        return this.finalErrorCalculationTest;
    }
    public double getFinalTrainingErrorCal(){
        return this.getFinalErrorCalculationTraining;
    }
    public double getHiddenLayer1size(){
        return this.listOfLayerSizes.get(1);
    }
    public double getHiddenLayer2size(){
        return this.listOfLayerSizes.get(2);
    }
    public double[] giveInput(double[] input){
        return forwardPass(input);
    }
}