
package casbahTest

import java.io.{ByteArrayOutputStream, File, FileInputStream, ObjectInputStream, ObjectOutputStream}

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.gridfs.Imports._
 
object GridFSDemo extends App { 
  
  withMongo { mongoClient =>
  
    val db = mongoClient(TEST_DB)
    
    // Create a GridFS "collection".  Files/Chunks are stored as [bucket name].files and
    // [bucket name].chunks, respectively.  If you don't specify a bucket name, it uses "fs".
    val gridColl = GridFS(db, "TestGrid")    
    println(s"\nCollections after creating grid: ${db.collectionNames mkString " "}")
    
    // Add an image file, along with some metadata, to the GridFS collection using a java.io.File.
    // The call returns an Option[AnyRef] that stores the _id for the MongoDB document.
    val dieID = gridColl( new File("Die.jpg") ) { gridFile => 
      gridFile.filename = "Die.jpg"
      gridFile.contentType = "image/jpg"
      gridFile.metaData = Map("Romo" -> "sucks")  
    }
    println(s"\nDie.jpg document ID in GridFS is $dieID.")
    
    // Add an image file to the GridFS collection using a java.io.InputStream. 
    gridColl( new FileInputStream("Santa.jpg") ) { f => f.filename = "Santa.jpg"; f.contentType = "image/jpg" }
  
    // Add a string to the GridFS collection using an Array[Byte]
    gridColl("Hello, world.".getBytes) { f => f.filename = "Hello.obj"; f.contentType = "text/plain" }
    
    // Add a Scala list to the GridFS collection using an Array[Byte].  Note that you can skip the contentType.
    val listBytes = new ByteArrayOutputStream 
    ( new ObjectOutputStream(listBytes) ) writeObject List(1,2,3) 
    gridColl(listBytes.toByteArray) {_.filename = "AList.obj"}
    
    // You can't use "find" without a parameter.  Use "files" instead.  You also can't access the chunks via gridColl.
    println(s"\nCollection has ${gridColl.files.size} files and ${db("TestGrid.chunks").size} chunks.")
    gridColl.files foreach println
  
    // Query for documents by filename.  The returned object is a Buffer[GridFSDBFile], not a DBObject.
    println
    println( gridColl.find("Santa.jpg") )
    println( gridColl.find( MongoDBObject("filename" -> "Santa.jpg") ) )
    println( gridColl.find( Map("filename" -> "Santa.jpg") ) )
    println( gridColl.find( """{"filename" : "Santa.jpg"}""".toDBObject ) )
    
    // Query by an ObjectId or metadata
    println(s"\nSearching by objectID: ${ gridColl.find( dieID.get.asInstanceOf[ObjectId] ) }")
    println(s"Searching by metadata: ${ gridColl.find( Map("metadata.Romo" -> "sucks") ).head }")
    
    // Since multiple documents can have the same filename, "find" returns a buffer that contains all matches.  Use head 
    // to get the first one.  Use the writeTo method to write the result to a file.  You can also pass a File instance.
    // Querying for one document by filename returns an Option[GridFSDBFile].  (TODO: Why does it print differently?)
    val helloOut = gridColl.find("Hello.obj").head
    helloOut.writeTo("Hello Out.txt")
    // hello.writeTo(new File("Hello Out.txt") )        
    gridColl.findOne("Santa.jpg").get.writeTo("Santa Out.jpg")
        
    // Query by contentType. Note that find( [type query] ) acts like findOne, only returns the first document.
    println("\nJPEG Files:")
    gridColl filter { _.contentType == Some("image/jpg") } foreach println
    
    // Extract the data from a returned GridFSDBFile as a string
    val helloOutBytes = new ByteArrayOutputStream
    helloOut.writeTo(helloOutBytes)
    println("\n" + helloOutBytes)
    
    // Extract something fancier, like a list.
    val listOut = new ObjectInputStream( gridColl.find("AList.obj").head.getInputStream )
    println( listOut.readObject.asInstanceOf[List[Int]] )
    
    
    // Trying to add a 26 MB string to a non-GridFS collection results in an over max BSON 
    // size error.  But you can add it to GridFS, get it back out, and write it to a file.
    val lines = "I will not talk in class.\n" * 1000000
    // val tempColl = db("NonGridFSCollection")    
    // tempColl.drop
    // tempColl += MongoDBObject("lines" -> "talking", "data" -> new org.bson.types.Binary(lines.getBytes) )  
    gridColl(lines.getBytes) {_.filename = "Lines.obj"}
    gridColl.find("Lines.obj").head.writeTo("Lines.txt")
    
    // Since the GridFS class it extends iterable, its "drop" method means to skip over some number 
    // of elements.  To drop the collection, drop each of the buckets explicitly.
    println(s"\TestGrid has ${gridColl.files.size} files and ${db("TestGrid.chunks").size} chunks.")
    println(s"\nCollections before dropping grid: ${db.collectionNames mkString " "}")
    db("TestGrid.files").drop
    db("TestGrid.chunks").drop
    println(s"Collections after dropping grid: ${db.collectionNames mkString " "}")
  
  } // withMongo
  
} // GridFSDemo
