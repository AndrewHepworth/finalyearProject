package model;

import org.json.simple.JSONArray;

import java.util.Random;

public class Matrix {
    private int rows;
    private int columns;
    private double[][] matrix;

    public Matrix(int rows, int columns){
        //need to change to have a condition for the rows and columns
        //incase an input of zero is given
        this.rows = rows;
        this.columns = columns;
        this.matrix = new double[rows][columns];

        for (int i = 0; i < this.rows; i++){
            for( int j = 0; j< this.columns ; j++) {
                matrix[i][j] = 0;
            }
        }

    }

    public double getMatrixElement(int rows, int columns){
        //Gets the matrix element, minus one because array starts from 0
        return matrix[rows][columns];
    }
    public void setMatrixElement(double newNumber, int row, int column){
        for(int i = 0; i < this.rows ; i++){
            for(int j = 0; j<this.columns; j++){
                if(i == row && j ==column){
                    this.matrix[i][j] = newNumber;
                }
            }
        }
    }

    public void matrixScale(double n){
        //Scales the matrix
        for(int i = 0; i< this.rows; i++){
            for(int j = 0 ; j< this.columns ; j++){
                this.matrix[i][j] = this.matrix[i][j]* n;
            }
        }
    }
    public void matrixElementAdd(double n){
        //Adds specific amount to all elements in a matrix
        for(int i = 0; i< this.rows; i++){
            for(int j = 0 ; j< this.columns ; j++){
                this.matrix[i][j] += n;
            }
        }
    }
    public void matrixAddMatrix(Matrix a){
        //Two Matrices added together -- need to put conditions on.
        try {
            for (int i = 0; i < this.rows; i++) {
                for (int j = 0; j < this.columns; j++) {
                    this.matrix[i][j] += a.getMatrixElement(i, j);
                }
            }
        }catch(ArrayIndexOutOfBoundsException e){
            System.out.println("the matrix:");
            this.printOut();
            System.out.println("The addition:");
            a.printOut();
        }
    }

    public static Matrix MatMult(Matrix A, Matrix B){
        double sum;
        if(A.getColumns() != B.getRows()){
            System.out.println("Columns of Matrix A: "+A.getColumns() +"does Not match Matrix B rows :"+B.getRows());
            System.out.println("A:");
            A.printOut();
            System.out.println("B:");
            B.printOut();
            return null;
        }else {

            Matrix resultingMatrix = new Matrix(A.getRows(), B.getColumns());
            for(int i = 0 ; i < resultingMatrix.getRows(); i++){
                for(int j = 0 ; j < resultingMatrix.getColumns(); j++){
                    sum = 0;
                    for(int k = 0 ; k < A.getColumns() ; k++){
                        sum += A.getMatrixElement(i,k)*B.getMatrixElement(k,j);
                    }
                    resultingMatrix.setMatrixElement(sum, i, j);
                }
            }
            return resultingMatrix;
        }
    }

    public void elementMult(Matrix a){
        //Put conditions in
        if(a.getRows() == this.getRows() && a.getColumns() == this.getColumns()) {
            for (int i = 0; i < a.rows; i++) {
                for (int j = 0; j < a.columns; j++) {
                    this.matrix[i][j] *= a.getMatrixElement(i, j);
                }
            }
        }
    }

    public static Matrix transpose(Matrix A){
        Matrix transposedMatrix = new Matrix(A.getColumns(),A.getRows());
        for (int i = 0; i < A.getRows(); i++){
            for( int j = 0; j< A.getColumns() ; j++) {
                transposedMatrix.setMatrixElement(A.getMatrixElement(i,j),j,i);
            }
        }
        return transposedMatrix;
    }

    public void randomizeElements(){
        Random rand = new Random();
        for (int i = 0; i < this.rows; i++){
            for( int j = 0; j< this.columns ; j++) {
                this.matrix[i][j] = rand.nextDouble();
            }
        }
    }
    public static Matrix matrixSubtract(Matrix A, Matrix B){
        //Need to put in an error checker, make sure they both have same rows and columns
        Matrix outcome = new Matrix(A.getRows(),B.getColumns());
        for (int i = 0; i < outcome.rows; i++){
            for( int j = 0; j< outcome.columns ; j++) {
                outcome.setMatrixElement(A.getMatrixElement(i,j) - B.getMatrixElement(i,j),i,j) ;
            }
        }
        return outcome;
    }
    public void printOut(){
        //Prints out Matrix to console
        System.out.println("------");
        for(int i = 0; i< this.rows; i++){
            for(int j = 0 ; j< this.columns ; j++){
                System.out.print(""+this.matrix[i][j]+"  ");
            }
            System.out.println("   ");

        }
        System.out.println("------");
    }


    public int getRows() {

        return this.rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getColumns() {
        return this.columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }
    public void setRow(int Row){
        this.rows = Row;
    }
    public void setColumn(int Column){
        this.columns = Column;
    }

    public double[][] getMatrix(){
        return this.matrix;
    }

    @SuppressWarnings("unchecked")
    public JSONArray saveMatrix(){
        JSONArray sMatrix = new JSONArray();
        for(int i = 0; i < this.rows ; i ++){
            JSONArray subColumns = new JSONArray();
            for(int j= 0; j < this.columns; j++){
                subColumns.add(this.matrix[i][j]);
            }
            sMatrix.add(subColumns);
        }
        return sMatrix;
    }

    public static Matrix loadMatrix(JSONArray array){

        JSONArray columnSize = (JSONArray) array.get(0);
        Matrix matrixFromJson = new Matrix(array.size(),columnSize.size());
        for(int i = 0; i < matrixFromJson.getRows() ; i++){
            JSONArray row = (JSONArray) array.get(i);
            for(int j = 0; j < matrixFromJson.getColumns(); j++ ){

                double value =  Double.parseDouble(row.get(j).toString());
                matrixFromJson.matrix[i][j] = value;
            }
        }
        return  matrixFromJson;
    }

}

