package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class dataSet {
    private int inputSize;
    private int outputSize;
    private String fileName;
    ArrayList<double[][]> data = new ArrayList<double[][]>();
    ArrayList<double[]> inputData = new ArrayList<double[]>();
    ArrayList<double[]> outputData = new ArrayList<double[]>();
    private ArrayList<double[]> trainingOutputdata = new ArrayList<double[]>();
    private ArrayList<double[]> trainingInputData = new ArrayList<double[]>();
    private ArrayList<double[]> testOutputData = new ArrayList<double[]>();
    private ArrayList<double[]> testInputData = new ArrayList<double[]>();

    public dataSet(String file){
//        this.inputSize = inputSize;
        this.fileName = file;
//        this.outputSize = outputSize;

    }

    public void convertDataFromFile() throws Exception {
        //Read data from  csv file, first 9 are inputs, second are output;
        //Put into seperate list
        String row;
        ArrayList<String> inputString = new ArrayList<String>();
        ArrayList<String> outputString = new ArrayList<String>();
        BufferedReader csvFile = new BufferedReader(new FileReader(fileName));
        int in = 1;
        while((row = csvFile.readLine())!= null){
            String[] data  = row.split(";");
            inputString.add(data[0]);
            try {
                outputString.add(data[1]);
            }
            catch(ArrayIndexOutOfBoundsException e){
                System.out.println("Line : "+ in);
            }
            in++;
        }
        for(String i : inputString){
            String[] valuesForInput =  i.split(",");
            double[] input = new double[valuesForInput.length];
            for(int j = 0; j < valuesForInput.length; j++){
                input[j] = Double.parseDouble(valuesForInput[j]);
            }
            this.inputData.add(input);
        }

        for(String i : outputString){
            String[] valuesForOutput =  i.split(",");
            double[] output = new double[valuesForOutput.length];
            for(int j = 0; j < valuesForOutput.length; j++){
                output[j] = Double.parseDouble(valuesForOutput[j]);
            }
            this.outputData.add(output);
        }

        this.inputSize = this.inputData.get(0).length;
        this.outputSize = this.outputData.get(0).length;
    }

    public void addData( double[] input , double[] output) {
        inputData.add(input);
        outputData.add(output);
        data.add(new double[][]{input,output});
    }

    public void shuffle(boolean isSplit){
        ArrayList<Integer> indexs = new ArrayList<Integer>();
        if(isSplit) {
            for (int i = 0; i < trainingInputData.size(); i++) {
                indexs.add(i);
            }
            Collections.shuffle(indexs);

            for (int i = 0; i < trainingInputData.size(); i++) {
                Collections.swap(trainingInputData, i, indexs.get(i));
                Collections.swap(trainingOutputdata, i, indexs.get(i));
            }
        }
        else{
            for (int i = 0; i < inputData.size(); i++) {
                indexs.add(i);
            }
            Collections.shuffle(indexs);

            for (int i = 0; i < inputData.size(); i++) {
                Collections.swap(inputData, i, indexs.get(i));
                Collections.swap(outputData, i, indexs.get(i));
            }
        }

    }
    public void splitData(double percentage){
        double howManytoTrain =  Math.floor(inputData.size()*percentage);
        double howManytoTest = inputData.size()-howManytoTrain;
        int trainingIndex = (int) howManytoTrain;
        int testIndex = (int) howManytoTest;
        for(int i = 0; i < trainingIndex ; i++) {
            trainingInputData.add(inputData.get(i));
            trainingOutputdata.add(outputData.get(i));
        }
        for(int j = 0; j < testIndex ; j++){
            testInputData.add(inputData.get(trainingIndex-1+j));
            testOutputData.add(outputData.get(trainingIndex-1+j));
        }
        System.out.println("training Input Data Size: "+trainingInputData.size());
        System.out.println("training output Data Size: "+trainingOutputdata.size());
        System.out.println("test Input Data Size: "+testInputData.size());
        System.out.println("test output Data Size: "+testOutputData.size());
    }


    public double[] getI(int i){
        return inputData.get(i);
    }
    public double[] getTrainingInputData(int i){
        return trainingInputData.get(i);
    }
    public double[] getTrainingOutputData(int i){
        return trainingOutputdata.get(i);
    }
    public double[] getTestInputData(int i){
        return testInputData.get(i);
    }
    public double[] getTestOutputData(int i){
        return testOutputData.get(i);
    }
    public double[] getO(int i){
        return outputData.get(i);
    }
    public int getInputArraySize(){
        return inputSize;
    }
    public int getOutputArraySize(){
        return  outputSize;
    }

    public int getTrainingDataSize(){
        return trainingInputData.size();
    }
    public int getTestDataSize(){
        return testInputData.size();
    }

    public int getSize(){
//        return data.size();
        return inputData.size();
    }
    @Override
    public String toString() {
        for(double[][] a: data){
            System.out.println(Arrays.deepToString(a));
        }
        return super.toString();
    }
}
