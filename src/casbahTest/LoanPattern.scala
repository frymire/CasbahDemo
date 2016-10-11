
package casbahTest

import java.io.FileInputStream

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.gridfs.Imports._
 
object LoanPattern extends App {
    
  withMongo { mongo =>
    
    val coll = mongo(TEST_DB)("Scrap")
	coll.drop
	   
	coll += MongoDBObject("hello" -> "world")
	coll += MongoDBObject("Romo" -> "sucks")
	
	// Throwing this here shouldn't prevent the Mongo client connection from closing.
	throw new Exception("Something crazy happened.")
	coll.find foreach println
	  
	coll.drop
	
  }
  
} // LoanPattern
