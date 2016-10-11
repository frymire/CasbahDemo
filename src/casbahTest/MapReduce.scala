package casbahTest

import com.mongodb.casbah.Imports._

object MapReduce extends App {
  
    // Connect to the MongoDB server, and make a collection
  val mongoClient = MongoClient("localhost", 27017)	  
  val coll = mongoClient(TEST_DB)("MapReduce")
  coll.drop()

  // Define a method to make it a little more convenient to make MongoDBObjects.
  def createDoc(id: String, amount: Int, status: String) = MongoDBObject("cust_id" -> id, "amount" -> amount, "status" -> status)

  // Add some sample documents
  coll += createDoc("A123", 500, "A")
  coll += createDoc("A123", 250, "A")
  coll += createDoc("B212", 200, "A")
  coll += createDoc("A123", 300, "D")
  println("\nOriginal documents:")
  coll.find foreach println

  // Specify the parameters for the map-reduce job...
  
  // Define JavaScript functions for the map and reduce steps.
  val map = "function() { emit(this.cust_id, this.amount) }"  
  val reduce = "function(key, values) { return Array.sum(values) }"
  
  // For the output target, either provide the name of a Mongo collection or say to only get the results inline.
  val outputTarget = "mapReduceResults" 
//  val outputTarget = com.mongodb.casbah.map_reduce.MapReduceInlineOutput

  // Limit to records where "status"->"A".  Query is optional, so you could set it to None or just exclude it.
//  val query = None
  val query = Some(DBObject("status" -> "A"))

  // I couldn't get this to work, but sort only works on the *input* documents, anyway.
  val sort = None
//  val sort = Some( DBObject("amount" -> 1) )
  
  // Use this to limit the number of documents to run through the map-reduce job in the first place.
  val limit = None
//  val limit = Some(2)
      
  // Use this to noodle with the value field in each final record.
//  val finalizer = None
  val finalizeFunction = Some("""function(key, value) { return value + 1 }""")
  
  // Run the map-reduce job.  The immediate result is a MapReduceResult (a Casbah trait) instance 
  // which extends Iterator[DBObject], so you can use it to print out the results.
  println("\nInline Map-Reduce results:")
//  coll.mapReduce(map, reduce, outputTarget) foreach println // minimal version
  val result = coll.mapReduce(map, reduce, outputTarget, query, sort, limit, finalizeFunction) 
  result foreach println
  
  // Print run statistics.  (The times in the last print statement don't seem to work, though.)
  println
  result.raw foreach { case(k,v) => println(s"$k: $v") }
  println(s"Emit Time = ${result.emitLoopTime}, Map Time = ${result.mapTime}, Total Time = ${result.totalTime}") 
    
  // Read the results (if any) back in from the collection stored in Mongo.
  println("\nResults stored in test.mapReduceResults collection:")
  val outColl = mongoClient("test")("mapReduceResults") 
  outColl.find foreach println

  // Close the Mongo connection
  coll.drop()
  outColl.drop()
  mongoClient.close  

} // MapReduce
