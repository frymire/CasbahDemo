
package casbahTest

import scala.collection.mutable.ArrayBuffer
import com.mongodb.casbah.Imports._

object Aggregation2 extends App {

  // Connect to the MongoDB server
  val mongoClient = MongoClient("localhost", 27017)	
  val coll = mongoClient(TEST_DB)("Aggregation2")
  coll.drop
  
  // Add records to the collection
  coll += MongoDBObject("foo" -> 5, "bar" -> false)
  coll += MongoDBObject("foo" -> 30, "bar" -> false)
  coll += MongoDBObject("foo" -> 35, "bar" -> true)
  coll += MongoDBObject("foo" -> 50, "bar" -> true)
  coll += MongoDBObject("foo" -> 50, "bar" -> true)
  coll += MongoDBObject("foo" -> 75, "bar" -> true)
  println
  coll.find foreach println

  
  // Here are two ways to just get the first few documents
  
  println("\nFirst 4 documents using a cursor:")
  coll.find.limit(4) foreach println
  
  println("\nFirst 4 documents using aggregate:")
  coll.aggregate( MongoDBObject("$limit" -> 4) ).results foreach println


  // Define a convenience method to create an aggregation pipeline expressed as a single DBObject.  Basically, 
  // it groups them by a key of None, which means they will all fall into the same group with _id = null.  It
  // lets you pass an expression operator such as {$first, $last, $min, $max, $sum, $avg} -> [key or specific value].
  def createGroupPipeline[B](fieldName: String, expressionOperator: (String, B) ): DBObject = 
    MongoDBObject ("$group" -> MongoDBObject("_id" -> None, fieldName -> MongoDBObject(expressionOperator) ) )
  
  // Count the records (elaborately) via the aggregation framework: compute a single "count" field that sums 1 
  // for each document that fell into the group.  
  print("\nCount of coll2: ")
  coll.aggregate( createGroupPipeline("count", "$sum" -> 1) ).results foreach println

  // Get the sum of the foo field.
  print("Sum over foo: ")
  coll.aggregate( createGroupPipeline("sumFoo", "$sum" -> "$foo") ).results foreach println
  
  // Make a set over the foo field.
  print("addToSet over foo:")
  coll.aggregate( createGroupPipeline("setFoo", "$addToSet" -> "$foo") ).results foreach println
  
  // Make a multiset over the foo field.
  print("push over foo:")
  coll.aggregate( createGroupPipeline("pushFoo", "$push" -> "$foo") ).results foreach println
  
  // Demonstrate a projection with a bunch of arithmetic operators. 
  println("\nProjection with foo:")
  val fields = MongoDBObject.newBuilder
  fields += "fooCopy" -> "$foo"
  fields += "add10" -> MongoDBObject("$add" -> ("$foo", 10) )
  fields += "multiply10" -> MongoDBObject("$multiply" -> ("$foo", 10) ) 
  fields += "mod10" -> MongoDBObject("$mod" -> ("$foo", 10) )
  fields += "cmp35" -> MongoDBObject("$cmp" -> ("$foo", 35) )
  fields += "eq35" -> MongoDBObject("$eq" -> ("$foo", 35) )
  coll.aggregate( MongoDBObject("$project" -> fields.result) ).results foreach println
  
  // Demonstrate a projection with a bunch of arithmetic operators. 
  println("\nProjection with foo using JSON:")
  val targetDoc = """{
		  "fooCopy" : "$foo", 
		  "add10" : {"$add" : ["$foo", 10] }, 
		  "multiply10" : {"$multiply" : ["$foo", 10] },
		  "mod10" : { "$mod" : ["$foo", 10] },
		  "cmp35" : { "$cmp" : ["$foo", 35] },
		  "eq35" : { "$eq" : ["$foo", 35] }
      }""".toDBObject
  coll.aggregate( MongoDBObject("$project" -> targetDoc) ).results foreach println
  
  // Count the foo entries that are even multiples of 10 to demonstrate conditionals.
  println("\nSum of foo entries that are divisible by 10:")
  val mod10Equals0 = MongoDBObject("$eq" -> ( MongoDBObject("$mod" -> ("$foo", 10) ), 0) )
  val conditionalSum = MongoDBObject("$sum" -> MongoDBObject("$cond" -> (mod10Equals0, "$foo", 0) ) )
  val sumOfDivisibleBy10 = MongoDBObject("$group" -> MongoDBObject("_id" -> 0, "sum" -> conditionalSum) ) 
  coll.aggregate(sumOfDivisibleBy10).results foreach println

  // Chain operators to query for a range of documents.
  println("\nDocuments with 5 < foo < 50:")
  for (doc <- coll.find("foo" $lt 50 $gt 5) ) println(doc)
  println("\nDocuments with 5 < foo <= 50:")
  for (doc <- coll.find("foo" $lte 50 $gt 5) ) println(doc)
  
  // Use ++ to combine multiple blocks.
  println("\nDocuments with 5 < foo <= 50 and bar = true:")
  for (doc <- coll.find( ("foo" $lte 50 $gt 5) ++ ("bar" $eq true) ) ) println(doc)   
  
  // And we're done.
  coll.drop
  mongoClient.close
  
} // CasbahAggregation
