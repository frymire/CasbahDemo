package casbahTest

import collection.mutable.ArrayBuffer

import com.mongodb.casbah.Imports._
 
object DBCommand extends App {
  
  // Have to connect to the "admin" DB to use listDatabases below.
  val mongoClient = MongoConnection() 
  val db = mongoClient("admin")
  
  // Use the command method to list the databases.
  // Other possible commands listed here: http://docs.mongodb.org/manual/reference/command/  
  val cmdResult = db.command("listDatabases")
  cmdResult.as[MongoDBList]("databases") foreach println

  
  
  val db2 = mongoClient(TEST_DB)
  val coll = db2("Command")
  
  // Add records to the collection
  coll += MongoDBObject("foo" -> 5, "bar" -> false)
  coll += MongoDBObject("foo" -> 30, "bar" -> false)
  coll += MongoDBObject("foo" -> 35, "bar" -> true)
  coll += MongoDBObject("foo" -> 50, "bar" -> true)
  coll += MongoDBObject("foo" -> 50, "bar" -> true)
  coll += MongoDBObject("foo" -> 75, "bar" -> true)
  println
  coll.find foreach println

  // Now, define a new pipeline of aggregation operators to apply and view cumulatively in sequence
  val pipeline = new ArrayBuffer[DBObject]
  pipeline += MongoDBObject("$limit" -> 4)

  println("\nFirst 4 documents using aggregate:")  
  val result = db2.command(MongoDBObject("aggregate" -> coll, "pipeline" -> pipeline))  // TODO: Doesn't work.  
  println(result)
  
  coll.drop
  mongoClient.close 

}