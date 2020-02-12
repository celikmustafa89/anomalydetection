package com.celikmustafa.ml.unsupervised;

import com.github.chen0040.data.frame.DataFrame;
import com.github.chen0040.data.frame.DataQuery;
import com.github.chen0040.data.frame.Sampler;
import com.github.chen0040.lof.LOF;

import java.util.Random;
import java.util.logging.Logger;

public class ChenUnsupervisedOutlierDetection {

    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static Random rand = new Random();

    public static void main(String[] args) {
        DataQuery.DataFrameQueryBuilder schema = DataQuery.blank()
                .newInput("c1")
                .newInput("c2")
                .newOutput("anomaly")
                .end();



        Sampler.DataSampleBuilder positiveSampler = new Sampler()
                .forColumn("c1").generate((name, index) -> rand.nextDouble() * (4 - -4))
                .forColumn("c2").generate((name, index) -> rand.nextDouble() * (4 - -4))
                //.forColumn("anomaly").generate((name, index) -> 1.0)
                .end();

        DataFrame trainingData = schema.build();

        trainingData = positiveSampler.sample(trainingData, 100);



        LOF methodLOF = new LOF();
        methodLOF.setMinPtsLB(3);
        methodLOF.setMinPtsUB(15);
        methodLOF.setThreshold(0.2);
        DataFrame resultantTrainedData = methodLOF.fitAndTransform(trainingData);
        System.out.println(resultantTrainedData.head(10));


        int truePredicted = 0;
        int falsePredicted = 0;

        for(int i = 0; i < resultantTrainedData.rowCount(); ++i){
            boolean predicted = methodLOF.isAnomaly(resultantTrainedData.row(i));
            //         boolean actual = crossValidationData.row(i).target() > 0.5;
            //         evaluator.evaluate(actual, predicted);
            //        logger.info("predicted: " + predicted + "\texpected: " + actual);
            if (predicted)
                truePredicted++;
            else
                falsePredicted++;
        }

        System.out.println("True: " + truePredicted);
        System.out.println("False: " + falsePredicted);

    }
}
