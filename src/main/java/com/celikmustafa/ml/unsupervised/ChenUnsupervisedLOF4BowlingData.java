package com.celikmustafa.ml.unsupervised;

import com.github.chen0040.data.frame.DataFrame;
import com.github.chen0040.data.frame.DataQuery;
import com.github.chen0040.lof.LOF;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class ChenUnsupervisedLOF4BowlingData {

    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static Random rand = new Random();

    public static void main(String[] args) {

        // 1. load data from csv file
        int column_env_HookEnd = 23;
        //int column_livch = 4;
        //int column_age = 5;
        //int column_urban = 6;
        boolean skipFirstLine = true;
        String columnSplitter = ",";

        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream("bowling_mergedv2_small.csv");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        long startTime = System.nanoTime();
        DataFrame frame = DataQuery.csv(columnSplitter, skipFirstLine)
                .from(inputStream)
                .selectColumn(column_env_HookEnd).asNumeric().asInput("env_HookEnd")
                .selectColumn(36).asOutput("anomaly")
                .build();
        System.out.println("Read small csv file: " + (System.nanoTime() - startTime));

        //
        LOF methodLOF = new LOF();
        methodLOF.setMinPtsLB(3);
        methodLOF.setMinPtsUB(15);
        methodLOF.setThreshold(0.1);

        startTime = System.nanoTime();
        DataFrame resultantTrainedData = methodLOF.fitAndTransform(frame);
        //System.out.println(resultantTrainedData.head(10));
        System.out.println("Fit and Transform: " + TimeUnit.SECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS));

        int truePredicted = 0;
        int falsePredicted = 0;

        List<Integer> inputs = new ArrayList<Integer>();

        for(int i = 0; i < resultantTrainedData.rowCount(); ++i){
            boolean predicted = methodLOF.isAnomaly(resultantTrainedData.row(i));
            inputs.add((int) resultantTrainedData.row(i).getCell("env_HookEnd"));
            //         boolean actual = crossValidationData.row(i).target() > 0.5;
            //         evaluator.evaluate(actual, predicted);
            //        logger.info("predicted: " + predicted + "\texpected: " + actual);
            if (predicted){
                truePredicted++;
                System.out.println(resultantTrainedData.row(i));
            }
            else{
                falsePredicted++;
            }
        }

        Collections.sort(inputs);

        System.out.println(inputs);

        System.out.println("True: " + truePredicted);
        System.out.println("False: " + falsePredicted);

    }
}