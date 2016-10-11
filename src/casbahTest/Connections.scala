package casbahTest

import com.mongodb.casbah.Imports._

object Connections extends App {

  // Connect to the MongoDB server (same as MongoClient("localhost", 27017)	).  Print the names of the databases on the server.
  val mongoClient = MongoConnection()
  println
  mongoClient.dbNames foreach println

  // Connect to the testDB database.
  val db = mongoClient(TEST_DB)
  println(s"\nDB Name: ${db.name}")
  
  // Print the available collections and connect to the "test" collection
  println("\n" + (db.collectionNames mkString "\t") )
  println(s"'testCollection' collection exists: ${db.collectionExists("testCollection")}")
  val coll = db("testCollection")
  
  // Note that even though we've created the collection in code, it doesn't yet exist in Mongo.
  println("\n" + (db.collectionNames mkString "\t") )
  println(s"'testCollection' collection exists: ${db.collectionExists("testCollection")}")
  
  // Add a document to the collection, then print out all of the documents.  The method += is the same as insert.
  coll += MongoDBObject("hello" -> "world")	
  println("\n" + (db.collectionNames mkString "\t") )
  println(s"'testCollection' collection exists: ${db.collectionExists("testCollection")}")
  println(s"\nDocuments:")
  for(doc <- coll.find) println(doc)
  
  // If you set a DB as read-only, you can't add anything to it.
//  db.readOnly_=(true)
//  coll += MongoDBObject("read" -> "only") // error
//  for(doc <- coll.find) println(doc)

  
  // NOTE: If you don't need the db instance, you can just get the collection directly like this.  You can 
  // even get it directly from the client connection, but that seems like bad style, since you can't close it.
//  val coll = mongoClient("testDB")("testCollection")
//  val coll = MongoConnection()("testDB")("testCollection")
    
  coll.drop
  mongoClient.close

}