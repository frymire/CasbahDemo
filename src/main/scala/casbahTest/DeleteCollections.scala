package casbahTest

import com.mongodb.casbah.Imports._

// Demonstrate how to use Casbah to talk to MongDB
object DeleteCollections extends App {
  
  val mongoClient = MongoConnection()
  mongoClient.dbNames foreach println

//  val db = mongoClient("testDB")  
//  db.dropDatabase
  
//  println("\n" + (db.collectionNames mkString "\t") )
//  
//  for(name <- db.collectionNames) { 
//    if (name != "system.indexes") { val coll = db(name); coll.drop() } 
//  } 
//  
//  println("\n" + (db.collectionNames mkString "\t") )
  
  
  mongoClient.close
	
} // object CasbahTest
