package casbahTest

import com.mongodb.casbah.Imports._
 
object Indexes extends App {
  
  // Connect to the "test" database on the MongoDB server.
  val mongoClient = MongoConnection() 
  val db = mongoClient(TEST_DB)  
  val coll1 = db("Indexes1")
  val coll2 = db("Indexes2")
  
  // Add some documents to coll1
  coll1 += """{"hello" : "world"}"""
  coll1 += """{"hello" : "goodbye"}"""
  println
  coll1.find foreach println
  
  // Add some documents to coll1
  coll2 += MongoDBObject("Romo" -> "sucks")
  coll2 += MongoDBObject("Romo" -> "douche")
  coll2 += MongoDBObject("Romo" -> "rocks")
  coll2 += MongoDBObject("Romo" -> "douche")
  println
  coll2.find foreach println
  
  // Make indexes on given fields.
  coll1.ensureIndex("hello")
  coll2.ensureIndex( MongoDBObject("Romo" -> -1) )  // descending order  

  // Print out the infex information.
  println(s"\ncoll1 index info:\n${coll1.getIndexInfo mkString "\n"}")
  println(s"\ncoll2 index info:\n${coll2.getIndexInfo mkString "\n"}")
  println(s"\nAll database indexes:\n${db("system.indexes").find mkString "\n"}")
  
  // Use "hint" to specify a specific index to use for a query.  To force a query to run without indexes, use "$natural".
  println
  val result2n = coll2.find("Romo" $eq "douche").hint( MongoDBObject("$natural" -> 1) )
  println(result2n.explain)
  result2n foreach println

  // With the index, the query only needs to scan 2 objects, rather than all of them.
  println
  val result2 = coll2.find("Romo" $eq "douche")
  println(result2.explain)
  result2 foreach println

  // If we're just looking for the Romo field (i.e. suppress _id), we can just reference the index.  
  // It works even though we sort in the opposite direction of the original index.
  println("\nRomo (without _id) >= 'rocks':")
  val result3 = coll2.find("Romo" $gte "rocks", MongoDBObject("_id" -> 0, "Romo" -> 1) ).sort( MongoDBObject("Romo" -> 1) )
  println(result3.explain)
  println(s"Used indexOnly:\t${result3.explain.get("indexOnly")}")
  result3 foreach println
  
  coll1.drop
  coll2.drop
  mongoClient.close 

}