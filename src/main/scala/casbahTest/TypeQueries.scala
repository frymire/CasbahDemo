package casbahTest

import com.mongodb.casbah.Imports._
 
object TypeQueries extends App {
  
  // Connect to the "test" database on the MongoDB server.
  val mongoClient = MongoConnection() 
  val db = mongoClient(TEST_DB)
  val coll = db("TypeQueries")
    
  // Add records of various types for the "hello" field.
  
  // Double -> 1
  coll += MongoDBObject("hello" -> 5.0)
  coll += MongoDBObject("hello" -> 6.0)
  
  // String -> 2
  coll += MongoDBObject("hello" -> "world")
  coll += MongoDBObject("hello" -> "goodbye")
  
  // Object -> 3
  coll += MongoDBObject("hello" -> MongoDBObject("Romo" -> "sucks") )
  coll += MongoDBObject("hello" -> MongoDBObject("OMG" -> "Ponies!!!") )
  
  // Array -> 4, but this doesn't work.
  coll += MongoDBObject("hello" -> MongoDBList(1,2,3) )
  coll += MongoDBObject("hello" -> MongoDBList(4,5,6) )
  
  // Binary Data -> 5
  coll += MongoDBObject("hello" -> new org.bson.types.Binary("world".getBytes) )
  coll += MongoDBObject("hello" -> new org.bson.types.Binary( Array(42.toByte) ) )
  
  // ObjectId -> 7
  coll += MongoDBObject("hello" -> new ObjectId )
  coll += MongoDBObject("hello" -> new ObjectId )
  
  // Boolean -> 8
  coll += MongoDBObject("hello" -> true )
  coll += MongoDBObject("hello" -> false )
  
  // Date -> 9
  coll += MongoDBObject("hello" -> (new ObjectId).getDate )
  coll += MongoDBObject("hello" -> new java.util.Date() )
  
  // Null -> 10.  Both of these work.
  coll += MongoDBObject("hello" -> null )
  coll += MongoDBObject("hello" -> None )  
  
  // TODO: Regex -> 11. 
  
  // JavaScript -> 13, but this doesn't work.
  coll += MongoDBObject("hello" -> new JSFunction("""function(value) { return value + 1 }""") )
  
  // 32-bit Integer -> 16.  The type match includes integers within Arrays.
  coll += MongoDBObject("hello" -> 10)
  coll += MongoDBObject("hello" -> 11)
  
  // Date -> 17, but this doesn't work.
  coll += MongoDBObject("hello" -> (new ObjectId).getTimestamp() )
  coll += MongoDBObject("hello" -> (new ObjectId).getTimestamp() )
  
  // Print out all of the documents
  println("\nAll documents:")
  coll.find foreach println
  
  // Print documents where the hello field matches a specific BSON type.  
  for (bsonType <- 1 to 17) {
    
    println(s"\nBSON Type = $bsonType:")    
    coll.find( MongoDBObject("hello" -> MongoDBObject("$type" -> bsonType) ) ) foreach println 
    
  }
  
  // Just to prove it works, let's read out the data stored as bytes.
  println("\nReading bytes:")
  val cursor = coll.find( MongoDBObject("hello" -> MongoDBObject("$type" -> 5) ) )
  val bytes1 = cursor.next.as[Array[Byte]]("hello")
  val bytes2 = cursor.next.as[Array[Byte]]("hello")
  println(bytes1 mkString " ")
  println(bytes1 map { _.asInstanceOf[Char] } mkString)
  println(bytes2 mkString " ")
  println(bytes2(0).asInstanceOf[Int])

  coll.drop
  mongoClient.close 

}