package casbahTest

import java.io.FileInputStream

import com.mongodb.casbah.Imports._
 
object Scrap extends App {
  
  withMongo { mongo =>   
  
    val mongo = MongoConnection()
    val db = mongo(TEST_DB)

  
    val coll = db("Scrap")
    coll.drop
    
    coll += MongoDBObject("hello" -> "world")
    coll += MongoDBObject("Romo" -> "sucks")
    
    coll.find foreach println
	  
    coll.drop
  
  }
  
}