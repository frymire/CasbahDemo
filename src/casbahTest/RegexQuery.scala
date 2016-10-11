package casbahTest

import com.mongodb.casbah.Imports._
 
object RegexQuery extends App {

  val mongoClient = MongoConnection() 
  val coll = mongoClient(TEST_DB)("RegexQuery")
    
  coll += MongoDBObject("Romo" -> "sucks")
  coll += MongoDBObject("Romo" -> "douche")
  coll += MongoDBObject("Romo" -> "rocks")
  
  // You can use Scala's native regex format for Casbah queries
  println("\nRegex query:")
  coll.find( MongoDBObject("Romo" -> """s[a-z]+""".r) ) foreach println
  
  println("\nRegex query:")
  coll.find( MongoDBObject("Romo" -> """[a-z]+cks""".r) ) foreach println

  coll.drop
  mongoClient.close 

}