package casbahTest

import com.mongodb.casbah.Imports._


object StringToJSON extends App {
  
  // Define a function that needs a DBObject (not a string)
  def printDBObject(obj: DBObject) = obj foreach println

  // Define a string that happens to be JSON-formatted
  val jsonString = """{ "hello" : "world" , "sad" : {"Romo" : "sucks"}, "happy" : ["beer", "pizza", "video games"] } }"""
  println(s"${jsonString.getClass}: $jsonString")
  
  // Here are two easy ways to use the implicit functions in package.scala to convert the string to a Mongo DB Object  
  val obj1 = jsonString.toDBObject
  val obj2: DBObject = jsonString
  println(s"${obj1.getClass}: $obj1")
  println(s"${obj2.getClass}: $obj2")
  
  // Anywhere a DBObject is needed, the conversion is automatic; you can pass a string to a function that needs a DBObject.
  // NOTE: The warning is just Casbah telling you it can't find a logger.
  println("\nCall printDBObject with a string:")
  printDBObject(jsonString)
  
} // StringToJSON
