package com.celikmustafa.ml.supervised;

import com.github.chen0040.data.evaluators.BinaryClassifierEvaluator;
import com.github.chen0040.data.frame.DataFrame;
import com.github.chen0040.data.frame.DataQuery;
import com.github.chen0040.data.frame.Sampler;
import com.github.chen0040.outliers.guassian.MultiVariateNormalOutliers;

import java.util.*;
import java.util.logging.Logger;

public class ChenSupervisedOutlierDetection {

    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static Random rand = new Random();

    public static void main(String[] args) {
        DataQuery.DataFrameQueryBuilder schema = DataQuery.blank()
                .newInput("c1")
                .newInput("c2")
                .newOutput("anomaly")
                .end();

        Sampler.DataSampleBuilder negativeSampler = new Sampler()
                .forColumn("c1").generate((name, index) -> rand.nextDouble() * 0.3 + (index % 2 == 0 ? -2 : 2))
                .forColumn("c2").generate((name, index) -> rand.nextDouble() * 0.3 + (index % 2 == 0 ? -2 : 2))
                .forColumn("anomaly").generate((name, index) -> 0.0)
                .end();

        Sampler.DataSampleBuilder positiveSampler = new Sampler()
                .forColumn("c1").generate((name, index) -> rand.nextDouble() * (4 - -4))
                .forColumn("c2").generate((name, index) -> rand.nextDouble() * (4 - -4))
                .forColumn("anomaly").generate((name, index) -> 1.0)
                .end();

        DataFrame trainingData = schema.build();

        trainingData = negativeSampler.sample(trainingData, 200);
        trainingData = positiveSampler.sample(trainingData, 200);

        System.out.println(trainingData.head(10));

        DataFrame crossValidationData = schema.build();

        crossValidationData = negativeSampler.sample(crossValidationData, 40);
        crossValidationData = positiveSampler.sample(crossValidationData, 40);

        MultiVariateNormalOutliers method = new MultiVariateNormalOutliers();
        method.fit(trainingData);

        BinaryClassifierEvaluator evaluator = new BinaryClassifierEvaluator();

        for(int i = 0; i < crossValidationData.rowCount(); ++i){
            boolean predicted = method.isAnomaly(crossValidationData.row(i));
            boolean actual = crossValidationData.row(i).target() > 0.5;
            evaluator.evaluate(actual, predicted);
            logger.info("predicted: " + predicted + "\texpected: " + actual);
        }

        evaluator.report();
    }
}
