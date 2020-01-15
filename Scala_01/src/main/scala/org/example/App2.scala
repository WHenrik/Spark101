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

case class Person(name:String, surname:String, age:Int)

object SimpleSpark extends App{

  override def main(args: Array[String]): Unit = {

    // Turn off logging
    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)

    //spark uses user defined functions to transform data, lets first look at how functions are defined in scala:
    val smallListOfNumbers = List(1, 2, 3, 4, 5)

    // A Scala map function from int to double
    def squareAndAdd(i: Int): Double = {
      i * 2 + 0.5
    }
    // A Scala map function defined in-line (without curly brackets)
    def squareAndAdd2(i: Int): Double = i * 2 + 0.5
    // A Scala map function inferring the return type
    def squareAndAdd3(i: Int) = i * 2 + 0.5
    // An anonymous Scala map function assigned to a variable
    val squareAndAddFunction = (i: Int) => i * 2 + 0.5


    // Different variants to apply the same function
    println(smallListOfNumbers.map(squareAndAdd))
    println(smallListOfNumbers.map(squareAndAdd2))
    println(smallListOfNumbers.map(squareAndAdd3))
    println(smallListOfNumbers.map(squareAndAddFunction))
    println(smallListOfNumbers.map(i => i * 2 + 0.5)) // anonymous function; compiler can infers types //cool
    println(smallListOfNumbers.map(_ * 2 + 0.5)) // syntactic sugar: '_' maps to first (second, third, ...) parameter  //mega


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
    import spark.implicits._

    val numbers = spark.createDataset((0 until 100).toList)
    val mapped = numbers.map(i => "This is a number: " + i)
    val filtered = mapped.filter(s => s.contains("1"))
    val sorted = filtered.sort()
    //List(numbers, mapped, filtered, sorted).foreach(dataset => println(dataset.getClass))
    //mapped.show()

    println("---------------------------------------------------------------------------------------------------------")

    // Basic terminal operations
    val collected = filtered.collect() // collects the entire dataset to the driver process
    val reduced = filtered.reduce((s1, s2) => s1 + "," + s2) // reduces all values successively to one, bzw. fasst er alle strings zusammen
    filtered.foreach(s => println(s)) // performs an action for each element (take care where the action is evaluated!)
    //println(reduced)
    List(collected, reduced).foreach(result => println(result.getClass))

    println("---------------------------------------------------------------------------------------------------------")

    // DataFrame and Dataset
    val untypedDF = numbers.toDF() // DS to DF
    val stringTypedDS = untypedDF.map(r => r.get(0).toString) // DF to DS via map
    val integerTypedDS = untypedDF.as[Int] // DF to DS via as() function that cast columns to a concrete types
    List(untypedDF, stringTypedDS, integerTypedDS).foreach(result => println(result.head.getClass))
    List(untypedDF, stringTypedDS, integerTypedDS).foreach(result => println(result.head))

    println("---------------------------------------------------------------------------------------------------------")

    // Mapping to tuples
    numbers
      .map(i => (i, "nonce", 3.1415, true))
      .take(10)
      .foreach(println(_))

    println("---------------------------------------------------------------------------------------------------------")

    val persons = spark.createDataset(List(
      Person("Barack", "Obama", 40),
      Person("George", "R.R. Martin", 65),
      Person("Elon", "Musk", 34)))

    persons
      .map(_.surname + " says hello")
      .collect()
      .foreach(println(_))

    println("------------------------------------------")
  }

}
