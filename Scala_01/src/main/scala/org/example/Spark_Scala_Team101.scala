package org.example

import org.apache.spark;
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.sql.{Dataset, Encoder, SparkSession}
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.DecisionTreeClassificationModel
import org.apache.spark.ml.classification.DecisionTreeClassifier
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.{IndexToString, StringIndexer, VectorIndexer}
import org.apache.log4j.Logger
import org.apache.log4j.Level

object Spark_Scala_Team101 extends App{

  override def main(args: Array[String]): Unit = {
    // Turn off logging
    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)

    //------------------------------------------------------------------------------------------------------------------
    // Setting up a Spark Session
    //------------------------------------------------------------------------------------------------------------------

    // Create a SparkSession to work with Spark    --- ETWAS KLAPPT DA NICHT -- GEFIXT: in mvn scala version geändert
    val sparkBuilder = SparkSession
      .builder()
      .appName("SparkTutorial")
      .master("local[4]") // local, with 4 worker cores
    val spark = sparkBuilder.getOrCreate()

    // Set the default number of shuffle partitions (default is 200, which is too high for local deployment)
    spark.conf.set("spark.sql.shuffle.partitions", "8") //

    // Importing implicit encoders for standard library classes and tuples that are used as Dataset types
    import spark.implicits._

    println("---------------------------------------------------------------------------------------------------------")
    //Einlesen von eines der benötigten Files

    val employees = spark.read
      .option("inferSchema", "true")
      .option("header", "true")
      .option("delimiter", ";")
      .csv("./TPCH/tpch_customer.csv") // also text, json, jdbc, parquet

    //employees.show(20)   //zeigt Tabelle (erste 20 Werte)
    //employees.printSchema()  //zeigt die types der columns
    //employees.explain()       //some facts, not interesting
    //employees.map()


    /*
    // Mapping to tuples TODO: Klären welche
    employees
      .map(i => (i, "nonce", 3.1415, true))
      .take(10)
      .foreach(println(_))
    */


    /*
    einlesen aller benötigten files
    val inputs = List("region", "nation", "supplier", "customer", "part", "lineitem", "orders")
      .map(name => s"data/TPCH/tpch_$name.csv")
     */
  }

}

