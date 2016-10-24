# CasbahDemo
Proof that I can use Casbah to talk to MongoDB from Scala.

Import into Eclipse as an Existing Maven Project, then

1) Right-click Project -> Configure -> Add Scala Nature<br>
2) Project Properties -> Java Build Path -> Libraries tab -> Scala Library Container -> Edit -> Latest 2.10 bundle (dynamic)
3) Right-click src/main/scala, then Build Path -> Use as Source Folder.<br>

(After much fuss with m2e and scala-maven-plugin, there doesn't
seem to be a good way to automate these steps.)

Before running, start the MongoDB server

	> mongod -dbpath={path to MongoData}
