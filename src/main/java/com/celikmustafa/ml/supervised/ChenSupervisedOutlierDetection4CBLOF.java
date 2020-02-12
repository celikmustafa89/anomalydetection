package com.celikmustafa.ml.supervised;

import com.github.chen0040.data.evaluators.BinaryClassifierEvaluator;
import com.github.chen0040.data.frame.DataFrame;
import com.github.chen0040.data.frame.DataQuery;
import com.github.chen0040.data.frame.Sampler;
import com.github.chen0040.lof.CBLOF;

import java.util.Random;
import java.util.logging.Logger;

public class ChenSupervisedOutlierDetection4CBLOF {

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
                .forColumn("c2").generate((name, index) -> rand.nextDouble() * 0.3 + (index % 3 == 0 ? -1 : 1))
                .forColumn("c3").generate((name, index) -> rand.nextDouble() * 0.3 + (index % 2 == 0 ? -3 : 3))
                .forColumn("anomaly").generate((name, index) -> 0.0)
                .end();

        Sampler.DataSampleBuilder positiveSampler = new Sampler()
                .forColumn("c1").generate((name, index) -> rand.nextDouble() * (4 - -4))
                .forColumn("c2").generate((name, index) -> rand.nextDouble() * (5 - -4))
                .forColumn("c3").generate((name, index) -> rand.nextDouble() * (4 - -5))
                .forColumn("anomaly").generate((name, index) -> 1.0)
                .end();

        DataFrame data = schema.build();

        data = negativeSampler.sample(data, 200);
        data = positiveSampler.sample(data, 200);

        //System.out.println(data.head(10));


        CBLOF method = new CBLOF();
        method.setParallel(false);
        DataFrame learnedData = method.fitAndTransform(data);

        BinaryClassifierEvaluator evaluator = new BinaryClassifierEvaluator();

        int truePredicted = 0;
        int falsePredicted = 0;
        for(int i = 0; i < learnedData.rowCount(); ++i){
            boolean predicted = learnedData.row(i).categoricalTarget().equals("1");
            boolean actual = data.row(i).target() == 1.0;
            evaluator.evaluate(actual, predicted);
            //logger.info("predicted: " + predicted + "\texpected: " + actual);

            if (predicted){
                System.out.println(learnedData.row(i));
                truePredicted++;
            }
            else{
                System.out.println(learnedData.row(i));
                falsePredicted++;
            }
        }

        System.out.println("True: " + truePredicted);
        System.out.println("False: " + falsePredicted);

        evaluator.report();

    }
}
