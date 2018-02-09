package com.tripbux.mllib;

import org.apache.spark.ml.linalg.Vectors;
import org.apache.spark.ml.linalg.VectorUDT;
import org.apache.spark.ml.stat.ChiSquareTest;
import org.apache.spark.ml.stat.Correlation;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.util.Arrays;
import java.util.List;

/**
 * Author: shin
 * Date: 2/9/18
 */
public class BasicStatistics {

  private static SparkConf sparkConf = new SparkConf().setAppName("Spark ML").setMaster("local[2]").set("spark.executor.memory", "1g");
  private static SparkSession spark = SparkSession.builder()
    .appName("ML")
    .config(sparkConf)
    .getOrCreate();


  public static void main(String[] args) {
    test2();
  }

  public static void test1() {
    List<Row> data = Arrays.asList(
      RowFactory.create(Vectors.sparse(4, new int[]{0, 3}, new double[]{1.0, -2.0})),
      RowFactory.create(Vectors.dense(4.0, 5.0, 0.0, 3.0)),
      RowFactory.create(Vectors.dense(6.0, 7.0, 0.0, 8.0)),
      RowFactory.create(Vectors.sparse(4, new int[]{0, 3}, new double[]{9.0, 1.0}))
    );

    StructType schema = new StructType(new StructField[]{
      new StructField("features", new VectorUDT(), false, Metadata.empty()),
    });

    Dataset<Row> df = spark.createDataFrame(data, schema);
    Row r1 = Correlation.corr(df, "features").head();
    System.out.println("Pearson correlation matrix:\n" + r1.get(0).toString());

    Row r2 = Correlation.corr(df, "features", "spearman").head();
    System.out.println("Spearman correlation matrix:\n" + r2.get(0).toString());
    spark.stop();
  }

  private static void test2() {
    List<Row> data = Arrays.asList(
      RowFactory.create(0.0, Vectors.dense(0.5, 10.0)),
      RowFactory.create(0.0, Vectors.dense(1.5, 20.0)),
      RowFactory.create(1.0, Vectors.dense(1.5, 30.0)),
      RowFactory.create(0.0, Vectors.dense(3.5, 30.0)),
      RowFactory.create(0.0, Vectors.dense(3.5, 40.0)),
      RowFactory.create(1.0, Vectors.dense(3.5, 40.0))
    );

    StructType schema = new StructType(new StructField[]{
      new StructField("label", DataTypes.DoubleType, false, Metadata.empty()),
      new StructField("features", new VectorUDT(), false, Metadata.empty()),
    });

    Dataset<Row> df = spark.createDataFrame(data, schema);
    Row r = ChiSquareTest.test(df, "features", "label").head();
    System.out.println("pValues: " + r.get(0).toString());
    System.out.println("degreesOfFreedom: " + r.getList(1).toString());
    System.out.println("statistics: " + r.get(2).toString());
  }

}
