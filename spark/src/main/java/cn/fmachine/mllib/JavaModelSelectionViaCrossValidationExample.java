/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.fmachine.mllib;

// $example on$

import java.util.Arrays;
// $example off$

// $example on$
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.classification.LogisticRegression;
import org.apache.spark.ml.evaluation.BinaryClassificationEvaluator;
import org.apache.spark.ml.feature.HashingTF;
import org.apache.spark.ml.feature.Tokenizer;
import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.ml.tuning.CrossValidator;
import org.apache.spark.ml.tuning.CrossValidatorModel;
import org.apache.spark.ml.tuning.ParamGridBuilder;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
// $example off$
import org.apache.spark.sql.SparkSession;

/**
 * Cross-Validation
 * https://spark.apache.org/docs/latest/ml-tuning.html
 * <p>
 * CrossValidator begins by splitting the dataset into a set of folds
 * which are used as separate training and test datasets.
 * E.g., with k=3 folds, CrossValidator will generate 3 (training, test) dataset pairs,
 * each of which uses 2/3 of the data for training and 1/3 for testing.
 * <p>
 * To evaluate a particular ParamMap,
 * CrossValidator computes the average evaluation metric for the 3 Models produced by
 * fitting the Estimator on the 3 different (training, test) dataset pairs.
 * <p>
 * After identifying the best ParamMap,
 * CrossValidator finally re-fits the Estimator using the best ParamMap and the entire dataset.
 * <p>
 * <p>
 * Java example for Model Selection via Cross Validation.
 * <p>
 * The following example demonstrates using CrossValidator to select from a grid of parameters.
 * <p>
 * Note that cross-validation over a grid of parameters is expensive.
 * E.g., in the example below,
 * the parameter grid has 3 values for hashingTF.numFeatures and 2 values for lr.regParam,
 * and CrossValidator uses 2 folds. This multiplies out to (3×2)×2=12 different models being trained.
 * In realistic settings, it can be common to try many more parameters and use more folds (k=3 and k=10 are common).
 * In other words, using CrossValidator can be very expensive.
 * However, it is also a well-established method for choosing parameters
 * which is more statistically sound than heuristic hand-tuning.
 */
public class JavaModelSelectionViaCrossValidationExample {
  public static void main(String[] args) {
    SparkSession spark = SparkSession
      .builder()
      .appName("JavaModelSelectionViaCrossValidationExample")
      .config("spark.master", "local[2]")
      .getOrCreate();

    // $example on$
    // Prepare training documents, which are labeled.
    Dataset<Row> training = spark.createDataFrame(Arrays.asList(
      new JavaLabeledDocument(0L, "a b c d e spark", 1.0),
      new JavaLabeledDocument(1L, "b d", 0.0),
      new JavaLabeledDocument(2L, "spark f g h", 1.0),
      new JavaLabeledDocument(3L, "hadoop mapreduce", 0.0),
      new JavaLabeledDocument(4L, "b spark who", 1.0),
      new JavaLabeledDocument(5L, "g d a y", 0.0),
      new JavaLabeledDocument(6L, "spark fly", 1.0),
      new JavaLabeledDocument(7L, "was mapreduce", 0.0),
      new JavaLabeledDocument(8L, "e spark program", 1.0),
      new JavaLabeledDocument(9L, "a e c l", 0.0),
      new JavaLabeledDocument(10L, "spark compile", 1.0),
      new JavaLabeledDocument(11L, "hadoop software", 0.0)
    ), JavaLabeledDocument.class);

    // Configure an ML pipeline, which consists of three stages: tokenizer, hashingTF, and lr.
    Tokenizer tokenizer = new Tokenizer()
      .setInputCol("text")
      .setOutputCol("words");
    HashingTF hashingTF = new HashingTF()
      .setNumFeatures(1000)
      .setInputCol(tokenizer.getOutputCol())
      .setOutputCol("features");
    LogisticRegression lr = new LogisticRegression()
      .setMaxIter(10)
      .setRegParam(0.01);
    Pipeline pipeline = new Pipeline()
      .setStages(new PipelineStage[]{tokenizer, hashingTF, lr});

    // We use a ParamGridBuilder to construct a grid of parameters to search over.
    // With 3 values for hashingTF.numFeatures and 2 values for lr.regParam,
    // this grid will have 3 x 2 = 6 parameter settings for CrossValidator to choose from.
    ParamMap[] paramGrid = new ParamGridBuilder()
      .addGrid(hashingTF.numFeatures(), new int[]{10, 100, 1000})
      .addGrid(lr.regParam(), new double[]{0.1, 0.01})
      .build();

    // We now treat the Pipeline as an Estimator, wrapping it in a CrossValidator instance.
    // This will allow us to jointly choose parameters for all Pipeline stages.
    // A CrossValidator requires an Estimator, a set of Estimator ParamMaps, and an Evaluator.
    // Note that the evaluator here is a BinaryClassificationEvaluator and its default metric
    // is areaUnderROC.
    CrossValidator cv = new CrossValidator()
      .setEstimator(pipeline)
      .setEvaluator(new BinaryClassificationEvaluator())
      .setEstimatorParamMaps(paramGrid)
      .setNumFolds(2);  // Use 3+ in practice

    // Run cross-validation, and choose the best set of parameters.
    CrossValidatorModel cvModel = cv.fit(training);

    // Prepare test documents, which are unlabeled.
    Dataset<Row> test = spark.createDataFrame(Arrays.asList(
      new JavaDocument(4L, "spark i j k"),
      new JavaDocument(5L, "l m n"),
      new JavaDocument(6L, "mapreduce spark"),
      new JavaDocument(7L, "apache hadoop")
    ), JavaDocument.class);

    // Make predictions on test documents. cvModel uses the best model found (lrModel).
    Dataset<Row> predictions = cvModel.transform(test);

    System.out.println(predictions.schema());
    predictions.show();

    for (Row r : predictions.select("id", "text", "probability", "prediction").collectAsList()) {
      System.out.println("(" + r.get(0) + ", " + r.get(1) + ") --> prob=" + r.get(2)
        + ", prediction=" + r.get(3));
    }
    // $example off$

    spark.stop();
  }
}
