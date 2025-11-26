package edu.surfability.train.knn;
import edu.surfability.train.data.*;
import java.util.*;

public interface Distance {
    public double findDistance(Row row1, Row row2, ArrayList<Row> rows);
}
