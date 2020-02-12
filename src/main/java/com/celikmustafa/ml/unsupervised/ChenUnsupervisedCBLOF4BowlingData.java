package com.celikmustafa.ml.unsupervised;

import com.github.chen0040.data.frame.DataFrame;
import com.github.chen0040.data.frame.DataQuery;
import com.github.chen0040.data.frame.DataRow;
import com.github.chen0040.lof.CBLOF;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Random;
import java.util.logging.Logger;

public class ChenUnsupervisedCBLOF4BowlingData {

    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static Random rand = new Random();

    public static void main(String[] args) {

        int column_shot_RPM = 23;
        int column_id = 0;
        boolean skipFirstLine = true;
        String columnSplitter = ",";

        InputStream inputStream = null;
        InputStream inputStream2 = null;
        try {
            inputStream = new FileInputStream("bowling_mergedv2_small.csv");
            inputStream2 = new FileInputStream("bowling_mergedv2_small.csv");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        DataFrame frame = DataQuery.csv(columnSplitter, skipFirstLine)
                .from(inputStream)
                .selectColumn(column_shot_RPM).asNumeric().asInput("shot_RPM")
                //.selectColumn(column_id).asNumeric().asInput("id")
                //.selectColumn(36).asOutput("anomaly")
                .build();
        DataFrame frame2 = DataQuery.csv(columnSplitter, skipFirstLine)
                .from(inputStream2)
                .selectColumn(column_id).asNumeric().asOutput("id")
                .selectColumn(column_shot_RPM).asNumeric().asInput("shot_RPM")
                //.selectColumn(36).asOutput("anomaly")
                .build();


        CBLOF methodCBLOF = new CBLOF();
        DataFrame resultantTrainedData = methodCBLOF.fitAndTransform(frame2);

        /*int truePredicted = 0;
        int falsePredicted = 0;
        for(int i = 0; i < resultantTrainedData.rowCount(); ++i){
            boolean predicted = methodCBLOF.isAnomaly(resultantTrainedData.row(i));
            //inputs.add((int) resultantTrainedData.row(i).getCell("env_HookEnd"));
            if (predicted){
                truePredicted++;
                System.out.println(resultantTrainedData.row(i));
            }
            else{
                falsePredicted++;
            }
        }
        System.out.println("True: " + truePredicted);
        System.out.println("False: " + falsePredicted);*/
        System.out.println(frame2.getAllColumns());
        System.out.println(frame2.getInputColumns());
        System.out.println(frame2.getOutputColumns());
        System.out.println(frame2.getLevels());


        frame2.unlock();
        int truePredictedFrame = 0;
        int falsePredictedFrame = 0;
        for(int i = 0; i < frame.rowCount(); ++i){

            boolean predicted = methodCBLOF.isAnomaly(frame2.row(i));
            DataRow row = frame2.newRow();
            if (predicted){
                truePredictedFrame++;
                System.out.println(frame2.row(i).getTargetCell("id"));
                row.setTargetCell("anomaly", 1);
            }
            else{
                falsePredictedFrame++;
                row.setTargetCell("anomaly", 0);
            }
            frame2.addRow(row);
        }

        for (int i = 0; i<frame2.rowCount();i++){
            System.out.println(frame2.row(i));
        }


        System.out.println("TrueFrame: " + truePredictedFrame);
        System.out.println("FalseFrame: " + falsePredictedFrame);


    }
}