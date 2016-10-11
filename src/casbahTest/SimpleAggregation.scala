
package casbahTest

import com.mongodb.casbah.Imports._

object SimpleAggregation extends App {
  
  // Connect to the MongoDB server, and make a collection to demonstrate Single Purpose Aggregation commands
  val mongoClient = MongoClient("localhost", 27017)	  
  val coll = mongoClient(TEST_DB)("SimpleAggregation")
  coll.drop()
  
  // Define field names as strings, so that it's easy to refactor them
  val a = "a"
  val b = "b"

  // Add some sample documents    
  coll += MongoDBObject(a -> 1, b -> 4)
  coll += MongoDBObject(a -> 1, b -> 2)
  coll += MongoDBObject(a -> 1, b -> 4)
  coll += MongoDBObject(a -> 2, b -> 3)
  coll += MongoDBObject(a -> 2, b -> 1)
  coll += MongoDBObject(a -> 1, b -> 5)
  coll += MongoDBObject(a -> 4, b -> 4)
  println("\nOriginal documents:")
  coll.find foreach println
  
  // Print various document counts
  println(s"\ncoll0 record count: ${coll.count()}")
  val aIs1 = MongoDBObject(a -> 1) 
  println(s"coll0 record count with a -> 1: ${coll.count(aIs1)}")  
  
  // Print the distinct values of each field.
  println(s"Distinct values of a: ${coll.distinct(a) mkString " "}")
  println(s"Distinct values of b: ${coll.distinct(b) mkString " "}")
  println(s"Distinct values of b where a -> 1: ${coll.distinct(b, aIs1) mkString " "}")
  
  // Create a map from the values of the "a" field to the documents with each value.
  println
  coll groupBy {_(a)} foreach { case(aValue, docs) => println(s"$aValue\n\t${docs mkString "\n\t"}") }
  
  // Now use the slightly richer group method to group and sum the b values on the condition that a -> 1.
  // The reduce string below is JavaScript code.
  val key = MongoDBObject(b -> 1)
  val condition = aIs1
  val initial = MongoDBObject("bSum" -> 0)
  val reduce = "function(obj, prev) { prev.bSum += obj.b; }"
  val finalizer = "function(result) { result.count = result.bSum / result.b }"
  println("\nSum of b values over documents with the same unique values of b and where a -> 1:")
  coll.group(key, condition, initial, reduce) foreach println
  println("\nSame thing, with a finalizer:")
  coll.group(key, condition, initial, reduce, finalizer) foreach println
  
  // Get the unique a values elaborately with the group method.
  println("\nUnique a values with group:")
  coll.group( MongoDBObject(a -> 1), MongoDBObject(), MongoDBObject(), "function(obj, prev) {}") foreach println
  
  // Close the Mongo connection
  coll.drop
  mongoClient.close

}