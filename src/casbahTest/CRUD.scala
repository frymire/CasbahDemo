
package casbahTest

import com.mongodb.casbah.Imports._

// Demonstrate how to use Casbah to talk to MongDB
object CRUD extends App {
  
  // Connect to the "test" database on the MongoDB server.
  val mongoClient = MongoConnection()
  val coll = mongoClient(TEST_DB)("CRUD")
      
  // Add two documents to the collection, then print out all of the documents.  The method += is the same as insert.
  // You can also use a Map[String, Any] anywhere a MongoDBObject is needed.
  coll insert MongoDBObject("hello" -> "world")	
  coll += Map("language" -> "scala")	
  println(s"\nAfter adding two documents to coll, the number of documents is ${ coll.count() }")
  for(doc <- coll.find) println(doc)
	
  // Find and print a document that does (and doesn't) exist.  Note that find returns a 
  // Cursor and findOne returns an Option.  You can also findAndModify or findAndRemove.
  println(s"\nLooking for hello-world: ${coll.findOne( Map("hello" -> "world") )}")
  println(s"Looking for goodbye-world: ${coll.findOne( MongoDBObject("goodbye" -> "world") )}")
	
  // Find the "language-scala" document and replace it with a "platform-JVM" document 
  val result1 = coll.update( MongoDBObject("language" -> "scala"), MongoDBObject("platform" -> "JVM") )
  println(s"\nAfter replacement, number updated: ${result1.getN}")
  for(doc <- coll.find) println(doc)

  // Casbah provides a rich fluid query syntax (i.e. "$set"), that allows you to construct DBObjects on
  // the fly using MongoDB query operators.  See http://mongodb.github.io/casbah/guide/query_dsl.html
  // Here, instead of replacing the document with "platform-JVM", we add "language-scala" as another key-value pair.
  val result2 = coll.update( MongoDBObject("platform" -> "JVM"), $set("language" -> "scala") )
  println(s"\nAfter $$set, number updated: ${result2.getN}")
  for(doc <- coll.find) println(doc)
  
  // Note: to update all documents, set the multi flag: .update(query, update, multi=true).  Another useful
  // flag is upsert which will insert a document if it doesn't exist and update it if it does.  Here, we 
  // look for the "language-clojure" document, and since it doesn't exist, it adds it (along with a 2nd k-v pair).
  val result3 = coll.update( MongoDBObject("language" -> "clojure"), $set("platform" -> "JVM"), upsert=true )
  println( "\nAfter upsert, number updated: " + result3.getN )
  for(doc <- coll.find) println(doc)
  
  // Here's how to pull out documents with specific keys.
  println(s"\nRecords with platform key:")
  for(doc <- coll.find("platform" $exists true) ) println(doc)
  
  // Here's the same thing, but projecting down to the language field.  You have to explicitly
  // suppress the _id field, since it is provide by default.
  println(s"\nJust the language key for documents with a platform key:")
  for(doc <- coll.find("platform" $exists true, MongoDBObject("_id" -> 0, "language" -> 1)) ) println(doc)
  
  // Here's how to pull out documents with or without specific values.
  println(s"\nRecords with a language key set to clojure:")  
  for(doc <- coll.find("language" $eq "clojure") ) println(doc)
  println(s"\nRecords where there is not a language key set to clojure:")  
  for(doc <- coll.find("language" $ne "clojure") ) println(doc)
  
  // This time, remove the document with "language:clojure"
  val result4 = coll remove MongoDBObject("language" -> "clojure")
  println("\nNumber removed: " + result4.getN)
  for(doc <- coll.find) println(doc)

  // Let's set up and run a bulk operation.  For the last one, we'll also use the builder for MongoDBObjects.  
  val builder = coll.initializeOrderedBulkOperation
  builder insert MongoDBObject("_id" -> 1)
  builder insert MongoDBObject("_id" -> 2)
  builder insert MongoDBObject("_id" -> 3)

  builder find MongoDBObject("_id" -> 1) updateOne $set("x" -> 2)
  builder find MongoDBObject("_id" -> 2) removeOne
  
  val objectBuilder = MongoDBObject.newBuilder
  objectBuilder += "_id" -> 3
  objectBuilder += "x" -> 4  
  builder find MongoDBObject("_id" -> 3) replaceOne objectBuilder.result

  builder.execute  
  println(s"\nAfter bulk operation, the number of documents is ${ coll.count() }")
  for(doc <- coll.find) println(doc)
  
  // Get stats on the collection
  println("\nCollection stats:")
  coll.stats foreach { case(k,v) => println(s"$k:\t$v") }
  
  // Delete the collection for the next run (although typically you might leave the data in place).
  coll.drop
  mongoClient.close
	
} // object CasbahTest
