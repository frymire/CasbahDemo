
package casbahTest

import scala.collection.mutable.ArrayBuffer
import com.mongodb.casbah.Imports._

object CasbahAggregation extends App {

  // Connect to the MongoDB server
  val mongoClient = MongoClient("localhost", 27017)	
    
  // Make a collection to demonstrate standard Aggregation commands
  val coll = mongoClient(TEST_DB)("Aggregation1")
  coll.drop

  coll += MongoDBObject(
      "title" -> "Programming in Scala",
      "author" -> "Martin",
      "pageViews" ->  50,
      "tags" ->  ("scala", "functional", "JVM"),
      "body" ->  "...")
      
  coll += MongoDBObject(
      "title" -> "Programming Clojure",
      "author" -> "Stuart",
      "pageViews" ->  35,
      "tags" ->  ("clojure", "functional", "JVM"),
      "body" ->  "Girl, look at that body.")

  // For the last one, use the automatic conversion from strings to DBObjects in the package object.
  coll += """{ 
      "title" : "MongoDB: The Definitive Guide",
      "author" : "Kristina",
      "pageViews" :  90,
      "tags" :  ["databases", "nosql", "future"],
      "body" :  "..." }"""      

  println("\nOriginal documents:")
  for (doc <- coll.find) println(doc)
    
  // Print the records sorted by page views
  println("\nSorted by pageViews:")
  coll.aggregate( MongoDBObject("$sort" -> MongoDBObject("pageViews" -> 1) ) ).results foreach println 

  // Print the records sorted by page views in reverse order
  println("\nSorted by pageViews in reverse order:")
  coll.aggregate( MongoDBObject("$sort" -> MongoDBObject("pageViews" -> -1) ) ).results foreach println 

  // Get the records for which the "body" field is "..."
  println("\nBody is '...'")
  coll.aggregate( MongoDBObject("$match" -> MongoDBObject("body" -> "...") ) ).results foreach println 
  
  // Now, define a new pipeline of aggregation operators to apply and view cumulatively in sequence
  val pipeline = new ArrayBuffer[DBObject]
  
  // First, project down to just the author and tags fields.  Including "_id" -> 0 excludes the _id field.
  pipeline += MongoDBObject("$project" -> MongoDBObject("_id" -> 0, "author" -> 1, "tags" -> 1) )
  println("\nProjections to author and tags without _id:")
  (coll aggregate pipeline).results foreach println
  
  // Next, unwind the tags, which makes a copy of each record for each member of the 
  //  tags field, replacing the latter with the specific tag member.
  pipeline += MongoDBObject("$unwind" -> "$tags")
  println("\nWith tags unwound:")
  (coll aggregate pipeline).results foreach println
  
  // Finally, group the records by tags, and accumulate sets for the authors
  pipeline += MongoDBObject("$group" -> MongoDBObject("_id" -> "$tags", "authors" -> MongoDBObject("$addToSet" -> "$author") ) )
  println("\nGrouped by tags:")
  (coll aggregate pipeline).results foreach println
    
  // As a separate task, let's concatenate the title and author fields from the original records
  val concat = MongoDBObject("$project" -> MongoDBObject("titleAuthor" -> MongoDBObject("$concat" -> ("$title", ", by ", "$author") ) ) )
  println("\nOriginals with title and author fields concatenated:")
  (coll aggregate concat).results foreach println
    
  // And we're done.
  coll.drop
  mongoClient.close
  
} // CasbahAggregation
