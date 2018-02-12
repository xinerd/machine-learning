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

import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.classification.LogisticRegression;
import org.apache.spark.ml.feature.HashingTF;
import org.apache.spark.ml.feature.Tokenizer;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
// $example off$
import org.apache.spark.sql.SparkSession;

/**
 * Java example for simple text document 'Pipeline'.
 */
public class JavaPipelineExample {
  public static void main(String[] args) {
    SparkSession spark = SparkSession
      .builder()
      .appName("JavaPipelineExample")
      .config("spark.master", "local[2]")
      .getOrCreate();

    // $example on$
    // Prepare training documents, which are labeled.
    Dataset<Row> training = spark.createDataFrame(Arrays.asList(
      new JavaLabeledDocument(0L, "a b c d e spark", 1.0),
      new JavaLabeledDocument(1L, "b d", 0.0),
      new JavaLabeledDocument(2L, "spark f g h", 1.0),
      new JavaLabeledDocument(3L, "hadoop mapreduce", 0.0)
    ), JavaLabeledDocument.class);

    // Configure an ML pipeline, which consists of three stages: tokenizer, hashingTF, and lr.
    Tokenizer tokenizer = new Tokenizer()
      .setInputCol("text")
      .setOutputCol("words");
    /*
     * http://dblab.xmu.edu.cn/blog/1261-2/
     * tf-idf : term frequency–inverse document frequency
     * 先简单地介绍下什么是TF-IDF(词频-逆文档频率)，它可以反映出语料库中某篇文档中某个词的重要性。假设t表示某个词，d表示一篇文档，则词频TF(t,d)是某个词t在文档d中出现的次数，而文档DF(t,D)是包含词t的文档数目。为了过滤掉常用的词组，如"the" "a" "of" "that",我们使用逆文档频率来度量一个词能提供多少信息的数值：
     * IDF(t,D)=log(|D|+1)/(DF(t,D)+1)
     * 这里|D|表示语料库的文档总数，为了不让分母为了0，在此进行了加1平滑操作。而词频-逆文档频率就是TF和IDF的简单相乘：
     * TFIDF(t,d,D)=TF(t,d)*IDF(t,D)
     */
    HashingTF hashingTF = new HashingTF()
      .setNumFeatures(1000)
      .setInputCol(tokenizer.getOutputCol())
      .setOutputCol("features");
    LogisticRegression lr = new LogisticRegression()
      .setMaxIter(10)
      .setRegParam(0.001);
    Pipeline pipeline = new Pipeline()
      .setStages(new PipelineStage[]{tokenizer, hashingTF, lr});

    // Fit the pipeline to training documents.
    PipelineModel model = pipeline.fit(training);

    // Prepare test documents, which are unlabeled.
    Dataset<Row> test = spark.createDataFrame(Arrays.asList(
      new JavaDocument(4L, "spark i j k"),
      new JavaDocument(5L, "l m n"),
      new JavaDocument(6L, "spark hadoop spark"),
      new JavaDocument(7L, "apache hadoop")
    ), JavaDocument.class);

    // Make predictions on test documents.
    Dataset<Row> predictions = model.transform(test);
    System.out.println("Predictions --------> ");
    predictions.show();
    predictions.printSchema();
    for (Row r : predictions.select("id", "text", "probability", "prediction").collectAsList()) {
      System.out.println("(" + r.get(0) + ", " + r.get(1) + ") --> prob=" + r.get(2)
        + ", prediction=" + r.get(3));
    }
    // $example off$

    spark.stop();
  }
}
