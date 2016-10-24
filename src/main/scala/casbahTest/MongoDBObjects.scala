package casbahTest

import com.mongodb.casbah.Imports._

object MongoDBObjects extends App {

  // Make and update a MongoDBObject.  Casbah lets you treat it just like a Scala map.
  val obj1 = MongoDBObject("foo" -> "bar", "pie" -> 3.14, "first3" -> MongoDBList(1,2,3) )
  println(s"\nOriginal: $obj1")
  println(s"\nobj('foo') gets foo as an AnyRef: ${ obj1("foo") }")
  obj1 += "foo" -> "baz"
  println(s"After overwriting foo: $obj1")  
  obj1 += "OMG" -> "Ponies!!!"
  println(s"After adding OMG: $obj1")

  // Use the as[A] and getAs[A] methods to cast it as you get it (the latter returns an option).
  println(s"\nobj.as[Double]('pie'): ${ obj1.as[Double]("pie") }")
  println(s"obj.getAs[Double]('pie'): ${ obj1.getAs[Double]("pie") }")
  
  // Extract a list like this.
  println(s"\nExtract a list -- obj.as[MongoDBList]('first3'): ${obj1.as[MongoDBList]("first3")}")

  // Using the createDBObject function I wrote in package.scala, you can also make objects directly from strings
  val obj2: DBObject = """{ "hello" : "world", "favorites" : ["beer", "pizza", "video games"] }"""
  println(s"\nDBObject from a JSON string: $obj2")

}