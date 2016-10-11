package casbahTest

import com.mongodb.casbah.Imports._

object PrimaryKey extends App {
  
  // Connect to the "test" database on the MongoDB server.
  val mongoClient = MongoConnection()
  val db = mongoClient(TEST_DB)
  
  // Print the available collections and connect to the "test" collection
  println("\n" + (db.collectionNames mkString "\t") )
  val coll = db("PrimaryKey")
    
  // If you add a record with the same"_id" field as a previous one, it overwrites.
  coll += MongoDBObject("_id" -> 1, "data" -> "first 1")
  coll += MongoDBObject("_id" -> 2, "data" -> "first 2")
  coll += MongoDBObject("_id" -> 1, "data" -> "second 1")	
  println(s"\nAfter adding two documents to coll, the number of documents is ${ coll.count() }")
  coll.find foreach println
  
  // Clean the collection and add a couple of new records.  Mongo automatically adds ObjectIds.
  coll.drop
  coll += MongoDBObject("hello" -> "world")
  coll += MongoDBObject("Romo" -> "sucks")
  
  // Extract timestamps from ObjectIds.
  println("\nUse the ObjectId to extract the timestamp of when each document was created:")
  coll.find foreach { doc =>
    val id = doc.as[ObjectId]("_id")
    val date: java.util.Date = id.getDate
    println(s"$doc\t$date\t${id.getTimeSecond}")
  }
  
  // Create and ObjectId directly.
  println("\nYou can generate BSON ObjectIds like this: " + new ObjectId() )
    
  coll.drop
  mongoClient.close 

}