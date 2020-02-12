package com.celikmustafa.ml.unsupervised;

import com.github.chen0040.data.frame.DataFrame;
import com.github.chen0040.data.frame.DataQuery;
import com.github.chen0040.lof.CBLOF;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public class ChenUnsupervisedCBLOF4BowlingData_2 {

    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static Random rand = new Random();

    public static void main(String[] args) throws FileNotFoundException {

        // - generates model for each column
        // - add each model's anomaly result to dataframe
        // - create an anomaly table for the records

        int[] inputColumnIndexes = {4,5,10,13,15};
        int column_shot_RPM = 23;
        int column_id = 0;
        boolean skipFirstLine = true;
        String columnSplitter = ",";

        // read data from csv file
        InputStream inputStream1 = new FileInputStream("bowling_mergedv2_small.csv");
        DataFrame dataframeOriginal = DataQuery.csv(columnSplitter, skipFirstLine)
                .from(inputStream1)
                .selectColumn(column_id).asNumeric().asInput("id")
                /*.selectColumn(column_id).transform(cellValue -> StringUtils.parseDouble(cellValue)*0 == 0 ? 0 : -2).asNumeric().asOutput("column_" + inputColumnIndexes[0])
                .selectColumn(column_id).transform(cellValue -> StringUtils.parseDouble(cellValue)*0 == 0 ? 0 : -2).asNumeric().asOutput("column_" + inputColumnIndexes[1])
                .selectColumn(column_id).transform(cellValue -> StringUtils.parseDouble(cellValue)*0 == 0 ? 0 : -2).asNumeric().asOutput("column_" + inputColumnIndexes[2])
                .selectColumn(column_id).transform(cellValue -> StringUtils.parseDouble(cellValue)*0 == 0 ? 0 : -2).asNumeric().asOutput("column_" + inputColumnIndexes[3])
                .selectColumn(column_id).transform(cellValue -> StringUtils.parseDouble(cellValue)*0 == 0 ? 0 : -2).asNumeric().asOutput("column_" + inputColumnIndexes[4])
                .selectColumn(column_id).asNumeric().transform(cellValue -> StringUtils.parseDouble(cellValue)*0 == 0 ? 0 : -2).asOutput("isAnomaly")*/
                .build();

        int repeatedTotalTruePrediction = 0;
        int repeatedTotalFalsePrediction = 0;
        for (int i = 0; i < inputColumnIndexes.length; i++) {

            // create input stream
            InputStream inputStream = null;
            inputStream = new FileInputStream("bowling_mergedv2_small.csv");

            // read data from csv file
            DataFrame dataframe = DataQuery.csv(columnSplitter, skipFirstLine)
                    .from(inputStream)
                    .selectColumn(inputColumnIndexes[i]).asNumeric().asInput("column_" + inputColumnIndexes[i])
                    .selectColumn(column_id).asNumeric().asOutput("id")
                    .build();

            // create model
            CBLOF methodCBLOF = new CBLOF();
            DataFrame resultantTrainedData = methodCBLOF.fitAndTransform(dataframe);

            dataframe.unlock();
            int truePrediction = 0;
            int falsePrediction = 0;
            List<Integer> anomalyIDList = new ArrayList<Integer>();
            for(int j = 0; j < dataframe.rowCount(); ++j){
                boolean predicted = methodCBLOF.isAnomaly(dataframe.row(j));
                if (predicted){
                    //System.out.println(dataframe.row(j));
                    truePrediction++;
                    anomalyIDList.add((int) dataframe.row(j).getTargetCell("id"));
                    //System.out.println(frame2.row(j).getTargetCell("id"));
                    //frame2.row(j).setTargetCell("anomaly", 1);
                }
                else{
                    falsePrediction++;
                    //frame2.row(j).setTargetCell("anomaly", 0);
                }
                dataframeOriginal.row(j).setTargetCell("isAnomaly", predicted ? 1 : (double)dataframeOriginal.row(j).getTargetCell("isAnomaly"));
                dataframeOriginal.row(j).setTargetCell("column_" + inputColumnIndexes[i], predicted ? 1 : (double)dataframeOriginal.row(j).getTargetCell("column_" + i));
            }
            System.out.println("Model Result for column_" + inputColumnIndexes[i]);
            System.out.println("True: " + truePrediction + "\nFalse: " + falsePrediction + "\n");
            repeatedTotalTruePrediction += truePrediction;
            repeatedTotalFalsePrediction += falsePrediction;
        }

        int totalTruePrediction = 0;
        int totalFalsePrediction = 0;
        for (int i = 0; i < dataframeOriginal.rowCount(); i++) {
            if (dataframeOriginal.row(i).getTargetCell("isAnomaly") == 1){
                totalTruePrediction++;
            }else {
                totalFalsePrediction++;
            }
        }

        System.out.println("Total true: " + totalTruePrediction);
        System.out.println("Total false: " + totalFalsePrediction);

        System.out.println("Repeated Total true: " + repeatedTotalTruePrediction);
        System.out.println("Repeated Total false: " + repeatedTotalFalsePrediction);

        System.out.println("Multiple true prediction count: " + (repeatedTotalTruePrediction - totalTruePrediction));
        System.out.println("Multiple false prediction count: " + (repeatedTotalFalsePrediction - totalFalsePrediction));
    }
}