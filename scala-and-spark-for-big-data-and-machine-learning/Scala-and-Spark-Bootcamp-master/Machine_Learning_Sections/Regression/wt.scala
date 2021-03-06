import org.apache.spark.ml.evaluation.RegressionEvaluator
import org.apache.spark.ml.regression.LinearRegression
import org.apache.spark.ml.tuning.{ParamGridBuilder, TrainValidationSplit}

// To see less warnings
import org.apache.log4j._
Logger.getLogger("org").setLevel(Level.ERROR)


// Start a simple Spark Session
import org.apache.spark.sql.SparkSession
val spark = SparkSession.builder().getOrCreate()

// Prepare training and test data.
val data = spark.read.option("header","true").option("inferSchema","true").format("csv").load("Clean-USA-Housing.csv")

// Check out the Data
data.printSchema()

// See an example of what the data looks like
// by printing out a Row
val colnames = data.columns
val firstrow = data.head(1)(0)
println("\n")
println("Example Data Row")
for(ind <- Range(0,colnames.length)){
  println(colnames(ind))
  println(firstrow(ind))
  println("\n")
}

// label , features
// This will allow us to join multiple feature columns
// into a single column of an array of feautre values
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.linalg.Vectors

val df = (data.select(data("Price").as("label"),
  $"Avg Area Income", $"Avg Area House Age", $"Avg Area Number of Rooms",
  $"Avg Area Number of Bedrooms", $"Area Population"))
// df.printSchema()

val assembler = (new VectorAssembler().setInputCols(
    Array("Avg Area Income", "Avg Area House Age", "Avg Area Number of Rooms",
    "Avg Area Number of Bedrooms", "Area Population")).setOutputCol("features"))

val output = assembler.transform(df).select($"label",$"features")

val lr = new LinearRegression()

val lrModel = lr.fit(output)


// Print the coefficients and intercept for linear regression
println(s"Coefficients: ${lrModel.coefficients} Intercept: ${lrModel.intercept}")

// Summarize the model over the training set and print out some metrics!
// Explore this in the spark-shell for more methods to call
val trainingSummary = lrModel.summary

println(s"numIterations: ${trainingSummary.totalIterations}")
println(s"objectiveHistory: ${trainingSummary.objectiveHistory.toList}")

trainingSummary.residuals.show()

println(s"RMSE: ${trainingSummary.rootMeanSquaredError}")
println(s"MSE: ${trainingSummary.meanSquaredError}")
println(s"r2: ${trainingSummary.r2}")
