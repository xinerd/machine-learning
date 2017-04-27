import org.apache.spark.sql.SparkSession

val spark = SparkSession.builder().getOrCreate() // create spark session

// create data frame, url relative to root directory of project
val df = spark.read.option("header", "true").option("inferSchema", "true").csv("./Scala-and-Spark-Bootcamp-master/SparkDataFrames/CitiGroup2006_2008")

// df.head(5)

for (row <- df.head(5)) {
  println(row)
}

// Get column names
df.columns

// Find out DataTypes
// Print Schema
df.printSchema()

// Describe DataFrame Numerical Columns
df.describe().show()

// Select columns .transform().action()
df.select("Volume").show()

// Multiple Columns
df.select($"Date",$"Close").show(2)

// Creating New Columns
val df2 = df.withColumn("HighPlusLow",df("High") - df("Low"))
// Show result
df2.columns
df2.printSchema()

// Recheck Head
df2.head(5)

// Renaming Columns (and selecting some more)
df2.select(df2("HighPlusLow").as("HPL"),df2("Close")).show()

// That is it for now! We'll see these basic functions
// a lot more as we go on.
