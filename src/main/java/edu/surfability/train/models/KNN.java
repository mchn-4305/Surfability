package edu.surfability.train.models;
import java.lang.reflect.Array;
import java.util.*;
import edu.surfability.train.data.*;

public class KNN {
    private ArrayList<Row> training;
    private ArrayList<Row> validation;
    private ArrayList<Row> testing;

    public KNN(ArrayList<Row> training, ArrayList<Row> validation, ArrayList<Row> testing) {
        this.training = training;
        this.validation = validation;
        this.testing = testing;
    }


}
