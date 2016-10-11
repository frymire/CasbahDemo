
// Note that Scala's built-in JSON parser is reported to be relatively slow.
import util.parsing.json.{JSON, JSONObject, JSONArray}

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.gridfs.Imports._


package object casbahTest {
  
  // Save the name of the database to use for testing.
  val TEST_DB = "testDB"   
    
  // Set up a Loan Pattern to handle MongoDB connections.
  def withMongo[ReturnType](f: MongoConnection => ReturnType) = {
    
    // TODO: Merge this with the try once Scala gets try-with-resource (see scala-arm incubator)
    var mongoClient: MongoConnection = null
    
    try {      
      mongoClient = MongoConnection()
      f(mongoClient)      
    } catch {      
      case e: Exception => { /* Do something with the exception. */ throw e }      
    } finally {      
      mongoClient.close      
    }
    
  } // doMongoCallOnLoan
   
  
  // Parse a JSON string as a Mongo DBObject.  By making this implicit, clients can use strings anywhere a DBObject 
  // is needed.  The second version lets you convert to a DBObject explicitly, as in """{"Hello" : "World"}""".json
  implicit def jsonToDBObject(json: String): DBObject = com.mongodb.util.JSON.parse(json).asInstanceOf[DBObject]
  implicit def jsonToDBObjectExplicit(string: String) = new { def toDBObject = jsonToDBObject(string) }

  // Parse JSON strings directly to Scala's representation of JSON objects or directly to Scala objects.
  def jsonToJSONObject(json: String) = JSON.parseRaw(json).get.asInstanceOf[JSONObject]
  def jsonToJSONArray(json: String) = JSON.parseRaw(json).get.asInstanceOf[JSONArray] 
  def jsonToMap(json: String) = JSON.parseFull(json).asInstanceOf[ Option[Map[String, Any]] ].get
  def jsonToList(json: String) = JSON.parseFull(json).asInstanceOf[ Option[List[Any]] ].get

} // package object casbahTest
