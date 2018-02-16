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
import java.util.List;

import org.apache.spark.ml.feature.HashingTF;
import org.apache.spark.ml.feature.IDF;
import org.apache.spark.ml.feature.IDFModel;
import org.apache.spark.ml.feature.Tokenizer;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
// $example off$

/**
 * Term frequency-inverse document frequency (TF-IDF)
 * is a feature vectorization method widely used in text mining
 * to reflect the importance of a term to a document in the corpus.
 * <p>
 * TF: Both HashingTF and CountVectorizer can be used to generate the term frequency vectors.
 * IDF: IDF is an Estimator which is fit on a dataset and produces an IDFModel.
 * <p>
 * Examples
 * <p>
 * In the following code segment, we start with a set of sentences.
 * We split each sentence into words using Tokenizer.
 * For each sentence (bag of words), we use HashingTF to hash the sentence into a feature vector.
 * We use IDF to rescale the feature vectors; this generally improves performance when using text as features.
 * Our feature vectors could then be passed to a learning algorithm.
 */
public class JavaTfIdfExample {
  public static void main(String[] args) {
    SparkSession spark = SparkSession
      .builder()
      .appName("JavaTfIdfExample")
      .config("spark.master", "local[2]")
      .getOrCreate();

    // $example on$
    List<Row> data = Arrays.asList(
      RowFactory.create(0.0, "Hi I heard about Spark"),
      RowFactory.create(0.0, "I wish Java could use case classes"),
      RowFactory.create(1.0, "Logistic regression models are neat")
    );
    StructType schema = new StructType(new StructField[]{
      new StructField("label", DataTypes.DoubleType, false, Metadata.empty()),
      new StructField("sentence", DataTypes.StringType, false, Metadata.empty())
    });
    Dataset<Row> sentenceData = spark.createDataFrame(data, schema);

    Tokenizer tokenizer = new Tokenizer().setInputCol("sentence").setOutputCol("words");
    Dataset<Row> wordsData = tokenizer.transform(sentenceData);

    int numFeatures = 20;
    HashingTF hashingTF = new HashingTF()
      .setInputCol("words")
      .setOutputCol("rawFeatures")
      .setNumFeatures(numFeatures);

    Dataset<Row> featurizedData = hashingTF.transform(wordsData);
    // alternatively, CountVectorizer can also be used to get term frequency vectors

    IDF idf = new IDF().setInputCol("rawFeatures").setOutputCol("features");
    IDFModel idfModel = idf.fit(featurizedData);

    Dataset<Row> rescaledData = idfModel.transform(featurizedData);
    rescaledData.select("label", "features").show();
    // $example off$

    spark.stop();
  }
}
