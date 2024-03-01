file:///C:/Users/sreek/Downloads/cinema-world-main/cinema-world-main/src/main/scala/com/example/cinemaworld/Database.scala
### java.lang.NullPointerException

occurred in the presentation compiler.

presentation compiler configuration:
Scala version: 2.13.13
Classpath:
<WORKSPACE>\src\main\resources [exists ], <WORKSPACE>\.bloop\root\bloop-bsp-clients-classes\classes-Metals-p9gkcZ66T9CMPMzhW_qTcA== [missing ], <HOME>\AppData\Local\bloop\cache\semanticdb\com.sourcegraph.semanticdb-javac.0.9.9\semanticdb-javac-0.9.9.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\scala-library\2.13.13\scala-library-2.13.13.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\typesafe\akka\akka-http_2.13\10.2.6\akka-http_2.13-10.2.6.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\typesafe\akka\akka-stream_2.13\2.6.16\akka-stream_2.13-2.6.16.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\typesafe\akka\akka-http-spray-json_2.13\10.2.6\akka-http-spray-json_2.13-10.2.6.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\typesafe\slick\slick_2.13\3.3.3\slick_2.13-3.3.3.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\postgresql\postgresql\42.2.18\postgresql-42.2.18.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\typesafe\slick\slick-hikaricp_2.13\3.3.3\slick-hikaricp_2.13-3.3.3.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\typesafe\akka\akka-http-core_2.13\10.2.6\akka-http-core_2.13-10.2.6.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\typesafe\akka\akka-actor_2.13\2.6.16\akka-actor_2.13-2.6.16.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\typesafe\akka\akka-protobuf-v3_2.13\2.6.16\akka-protobuf-v3_2.13-2.6.16.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\reactivestreams\reactive-streams\1.0.3\reactive-streams-1.0.3.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\typesafe\ssl-config-core_2.13\0.4.2\ssl-config-core_2.13-0.4.2.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\io\spray\spray-json_2.13\1.3.6\spray-json_2.13-1.3.6.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\slf4j\slf4j-api\1.7.25\slf4j-api-1.7.25.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\typesafe\config\1.4.0\config-1.4.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\modules\scala-collection-compat_2.13\2.0.0\scala-collection-compat_2.13-2.0.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\checkerframework\checker-qual\3.5.0\checker-qual-3.5.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\zaxxer\HikariCP\3.2.0\HikariCP-3.2.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\typesafe\akka\akka-parsing_2.13\10.2.6\akka-parsing_2.13-10.2.6.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\modules\scala-java8-compat_2.13\1.0.0\scala-java8-compat_2.13-1.0.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\modules\scala-parser-combinators_2.13\1.1.2\scala-parser-combinators_2.13-1.1.2.jar [exists ]
Options:
-Yrangepos -Xplugin-require:semanticdb


action parameters:
uri: file:///C:/Users/sreek/Downloads/cinema-world-main/cinema-world-main/src/main/scala/com/example/cinemaworld/Database.scala
text:
```scala
import slick.jdbc.PostgresProfile.api._
import slick.lifted.ProvenShape

// Assuming a package structure, adjust the import statements as necessary
// import com.example.cinemaworld._

trait DatabaseSchema {
  // Define a Slick table for movies
  class Movies(tag: Tag) extends Table[Movie](tag, "movies") {
    def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def title: Rep[String] = column[String]("title")
    def duration: Rep[Int] = column[Int]("duration")
    def rating: Rep[String] = column[String]("rating")
    
    // Projection to map columns to the case class
    override def * : ProvenShape[Movie] = (id.?, title, duration, rating) <> (Movie.tupled, Movie.unapply)
  }

  // Define a Slick table for showtimes
  class Showtimes(tag: Tag) extends Table[Showtime](tag, "showtimes") {
    def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def movieId: Rep[Int] = column[Int]("movie_id")
    def startTime: Rep[String] = column[String]("start_time") // Consider changing to Timestamp if needed
    def theater: Rep[String] = column[String]("theater")
    
    def * : ProvenShape[Showtime] = (id.?, movieId, startTime, theater) <> (Showtime.tupled, Showtime.unapply)
    
    // Define a foreign key relationship to movies
    def movie = foreignKey("movie_fk", movieId, TableQuery[Movies])(_.id)
  }

  // Define a Slick table for bookings
  class Bookings(tag: Tag) extends Table[Booking](tag, "bookings") {
    def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def showtimeId: Rep[Int] = column[Int]("showtime_id")
    def quantity: Rep[Int] = column[Int]("quantity")
    
    def * : ProvenShape[Booking] = (id.?, showtimeId, quantity) <> (Booking.tupled, Booking.unapply)
    
    // Define a foreign key relationship to showtimes
    def showtime = foreignKey("showtime_fk", showtimeId, TableQuery[Showtimes])(_.id)
  }
  
  // Instantiate table queries for direct use
  val movies = TableQuery[Movies]
  val showtimes = TableQuery[Showtimes]
  val bookings = TableQuery[Bookings]
}

// Mixin the schema with an object or trait representing your database
object DBSchema extends DatabaseSchema

```



#### Error stacktrace:

```
java.base/java.util.Arrays.sort(Arrays.java:1232)
	scala.tools.nsc.classpath.JFileDirectoryLookup.listChildren(DirectoryClassPath.scala:125)
	scala.tools.nsc.classpath.JFileDirectoryLookup.listChildren$(DirectoryClassPath.scala:109)
	scala.tools.nsc.classpath.DirectoryClassPath.listChildren(DirectoryClassPath.scala:322)
	scala.tools.nsc.classpath.DirectoryClassPath.listChildren(DirectoryClassPath.scala:322)
	scala.tools.nsc.classpath.DirectoryLookup.list(DirectoryClassPath.scala:90)
	scala.tools.nsc.classpath.DirectoryLookup.list$(DirectoryClassPath.scala:84)
	scala.tools.nsc.classpath.DirectoryClassPath.list(DirectoryClassPath.scala:322)
	scala.tools.nsc.classpath.AggregateClassPath.$anonfun$list$3(AggregateClassPath.scala:106)
	scala.collection.immutable.Vector.foreach(Vector.scala:2124)
	scala.tools.nsc.classpath.AggregateClassPath.list(AggregateClassPath.scala:102)
	scala.tools.nsc.util.ClassPath.list(ClassPath.scala:34)
	scala.tools.nsc.util.ClassPath.list$(ClassPath.scala:34)
	scala.tools.nsc.classpath.AggregateClassPath.list(AggregateClassPath.scala:31)
	scala.tools.nsc.symtab.SymbolLoaders$PackageLoader.doComplete(SymbolLoaders.scala:297)
	scala.tools.nsc.symtab.SymbolLoaders$SymbolLoader.$anonfun$complete$2(SymbolLoaders.scala:249)
	scala.tools.nsc.symtab.SymbolLoaders$SymbolLoader.complete(SymbolLoaders.scala:247)
	scala.reflect.internal.Symbols$Symbol.completeInfo(Symbols.scala:1566)
	scala.reflect.internal.Symbols$Symbol.info(Symbols.scala:1538)
	scala.reflect.internal.Types$TypeRef.decls(Types.scala:2608)
	scala.tools.nsc.typechecker.Namers$Namer.enterPackage(Namers.scala:747)
	scala.tools.nsc.typechecker.Namers$Namer.dispatch$1(Namers.scala:297)
	scala.tools.nsc.typechecker.Namers$Namer.standardEnterSym(Namers.scala:310)
	scala.tools.nsc.typechecker.AnalyzerPlugins.pluginsEnterSym(AnalyzerPlugins.scala:496)
	scala.tools.nsc.typechecker.AnalyzerPlugins.pluginsEnterSym$(AnalyzerPlugins.scala:495)
	scala.meta.internal.pc.MetalsGlobal$MetalsInteractiveAnalyzer.pluginsEnterSym(MetalsGlobal.scala:68)
	scala.tools.nsc.typechecker.Namers$Namer.enterSym(Namers.scala:288)
	scala.tools.nsc.typechecker.Analyzer$namerFactory$$anon$1.apply(Analyzer.scala:50)
	scala.tools.nsc.Global$GlobalPhase.applyPhase(Global.scala:481)
	scala.tools.nsc.Global$Run.$anonfun$compileLate$2(Global.scala:1688)
	scala.tools.nsc.Global$Run.$anonfun$compileLate$2$adapted(Global.scala:1687)
	scala.collection.IterableOnceOps.foreach(IterableOnce.scala:619)
	scala.collection.IterableOnceOps.foreach$(IterableOnce.scala:617)
	scala.collection.AbstractIterator.foreach(Iterator.scala:1303)
	scala.tools.nsc.Global$Run.compileLate(Global.scala:1687)
	scala.tools.nsc.interactive.Global.parseAndEnter(Global.scala:668)
	scala.tools.nsc.interactive.Global.typeCheck(Global.scala:678)
	scala.meta.internal.pc.PcCollector.<init>(PcCollector.scala:29)
	scala.meta.internal.pc.PcSemanticTokensProvider$Collector$.<init>(PcSemanticTokensProvider.scala:19)
	scala.meta.internal.pc.PcSemanticTokensProvider.Collector$lzycompute$1(PcSemanticTokensProvider.scala:19)
	scala.meta.internal.pc.PcSemanticTokensProvider.Collector(PcSemanticTokensProvider.scala:19)
	scala.meta.internal.pc.PcSemanticTokensProvider.provide(PcSemanticTokensProvider.scala:73)
	scala.meta.internal.pc.ScalaPresentationCompiler.$anonfun$semanticTokens$1(ScalaPresentationCompiler.scala:169)
```
#### Short summary: 

java.lang.NullPointerException