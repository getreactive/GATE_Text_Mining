import akka.actor._
import scala.collection.mutable.{ArrayBuffer,ListBuffer,ArrayBuilder}
import akka.routing.RoundRobinGroup
import java.util.Calendar
import scala.io.Source
import akka.actor.Scheduler;
import scala.concurrent.duration._
import akka.actor.PoisonPill
import scala.collection.JavaConversions._
import scala.util.control.Breaks._


object JsonSample extends App {


  /*
  This main class creates actors and checks for termination

   */

var Count_For_Termination:Int=0
var i=0

  //Creating actors within an local actorsystem

implicit val system = ActorSystem("LocalSystem")
val localActor1 = system.actorOf(Props[LocalActor1], name = "LocalActor1")  // the local actor
val localActor2 = system.actorOf(Props[LocalActor2], name = "LocalActor2")  
val localActor3 = system.actorOf(Props[LocalActor3], name = "LocalActor3")
val localActor4 = system.actorOf(Props[LocalActor4], name = "LocalActor4")
val localActor5 = system.actorOf(Props[LocalActor5], name = "LocalActor5")
val localActor6 = system.actorOf(Props[LocalActor6], name = "LocalActor6")

 //Subscribing actors for deadletters

system.eventStream.subscribe(localActor1, classOf[DeadLetter])
system.eventStream.subscribe(localActor2, classOf[DeadLetter])
system.eventStream.subscribe(localActor3, classOf[DeadLetter])
system.eventStream.subscribe(localActor4, classOf[DeadLetter])
system.eventStream.subscribe(localActor5, classOf[DeadLetter])
system.eventStream.subscribe(localActor6, classOf[DeadLetter])

//Starting localactors

localActor1 ! "START PROCESS"
localActor2 ! "START PROCESS"
localActor3 ! "START PROCESS"
localActor4 ! "START PROCESS" 
localActor5 ! "START PROCESS"  
localActor6 ! "START PROCESS"

//Checking for termination of all actors and launch a script

  while(true){

    if(Count_For_Termination>6){
      println("Yeah!!!!!!!!")
      break
    }

  }

}

//Local actors

class LocalActor1 extends Actor with ActorLogging {

  /*
  Each actor is registered for logging and deadletters.This reads input file and sends to remote actors
   */

  import context._
  var ip1=""
  var ip2=""
  var paths1 = List("akka.tcp://JsonActors@"+ip1+"/user/BannerProcess","akka.tcp://JsonActors@"+ip2+"/user/BannerProcess")
  val filename1 = "/home/ubuntu/sbt/Files/Third_5cr_1.json"
  val router1: ActorRef = context.actorOf(RoundRobinGroup(paths1.toList).props(), "router1")
  context.watch(router1)   //Watching each router for termination
  var counter = 0
  var total_counter =0
  var i=0
  var j=0
  def receive = {
   case Terminated(corpse) =>

      if (corpse == router1) {
        log.warning("Received termination notification for '" + corpse + "'," +
          "is in our watch list. Terminating ActorSystem.")

      } else {
        log.info("Received termination notification for '" + corpse + "'," +
          "which is not in our deathwatch list.".format(corpse))
      }
   case d : DeadLetter => {
     JsonSample.Count_For_Termination+=1    //Incrementing counter value when it is terminated
     println("Count value is...."+JsonSample.Count_For_Termination)
   }

    case "START PROCESS" => {
      val line_count = Source.fromFile(filename1).getLines.size

      val chunk_size: Int = 3500 //Chunk size that is to be sent

      val no_of_chunks = line_count / chunk_size

      for (k <- 0 until no_of_chunks + 1)  //Send all chunks
      {
        var file_content = Source.fromFile(filename1)
        val total_lines = file_content.getLines()
        file_content = Source.fromFile(filename1)

        if ((k + 1) * chunk_size > file_content.getLines().length) {

          file_content = Source.fromFile(filename1)
          router1 ! total_lines.slice(k * chunk_size, file_content.getLines().length).toList
          file_content.close()
        }

        else {

          val int_val: Int = k * chunk_size
          router1 ! total_lines.slice(k * chunk_size, (k + 1) * chunk_size).toList
          file_content.close()
        }
      }
    }
   }
} 

class LocalActor2 extends Actor {
  import context._
  var ip1=""
  var ip2=""
  var paths2 =List("akka.tcp://JsonActors@"+ip1+"/user/BannerProcess","akka.tcp://JsonActors@"+ip2+"/user/BannerProcess")
  val filename2 = "/home/ubuntu/sbt/Files/Third_5cr_2.json"
  val router2: ActorRef = context.actorOf(RoundRobinGroup(paths2.toList).props(), "router2")
  var counter = 0
  var total_counter =0
  var i=0
  var j=0
  def receive = {
    case "START" =>
      router2 ! "Hello from the LocalActor"
    case "START PROCESS" =>{
    val line_count2 = Source.fromFile(filename2).getLines.size
    val chunk_size2:Int = 3500
    val no_of_chunks2 = line_count2/chunk_size2
    for(k2  <- 0 until no_of_chunks2+1)
     {
        var file_content2 = Source.fromFile(filename2)
        val total_lines2 = file_content2.getLines()
        file_content2 = Source.fromFile(filename2)
        if((k2+1)*chunk_size2 > file_content2.getLines().length) 
        {
           file_content2 = Source.fromFile(filename2)
           router2 ! total_lines2.slice(k2 * chunk_size2,file_content2.getLines().length).toList
           file_content2.close()
        }
        else
        {
          router2 ! total_lines2.slice(k2 * chunk_size2, (k2+1) * chunk_size2).toList
          file_content2.close()
       }      
    }
   }
 }
} 

class LocalActor3 extends Actor {
  import context._
  var ip1=""
  var ip2=""
  var paths3 = List("akka.tcp://JsonActors@"+ip1+"/user/BannerProcess","akka.tcp://JsonActors@"+ip2+"/user/BannerProcess")
  val filename3 = "/home/ubuntu/sbt/Files/Third_5cr_5.json"
  val router3: ActorRef = context.actorOf(RoundRobinGroup(paths3.toList).props(), "router3")
  var counter = 0
  var total_counter =0
  var i=0
  var j=0
  def receive = {
    case "START" =>
      router3 ! "Hello from the LocalActor"
    case "START PROCESS" =>{
    val line_count3 = Source.fromFile(filename3).getLines.size
    val chunk_size3:Int = 3500
    val no_of_chunks3 = line_count3/chunk_size3
    for(k3  <- 0 until no_of_chunks3+1)
     {
        var file_content3 = Source.fromFile(filename3)
        val total_lines3 = file_content3.getLines()
        file_content3 = Source.fromFile(filename3)
        if((k3+1)*chunk_size3 > file_content3.getLines().length) 
        {
           file_content3 = Source.fromFile(filename3)
           router3 ! total_lines3.slice(k3 * chunk_size3,file_content3.getLines().length).toList
           file_content3.close()
        }
        else
        {
           router3 ! total_lines3.slice(k3 * chunk_size3, (k3+1) * chunk_size3).toList
          file_content3.close()
       }      
    }
   }
 }
} 





class LocalActor4 extends Actor {
  import context._
  var ip1=""
  var ip2=""
  var paths4 = List("akka.tcp://JsonActors@"+ip1+"/user/BannerProcess","akka.tcp://JsonActors@"+ip2+"/user/BannerProcess")
  val filename4 = "/home/ubuntu/sbt/Files/Third_5cr_3.json"
  val router4: ActorRef = context.actorOf(RoundRobinGroup(paths4.toList).props(), "router4")
  var counter = 0
  var total_counter =0
  var i=0
  var j=0
  def receive = {
    case "START" =>
      router4 ! "Hello from the LocalActor"
    case "START PROCESS" =>{
    val line_count4 = Source.fromFile(filename4).getLines.size
    val chunk_size4:Int = 3500
    val no_of_chunks4 = line_count4/chunk_size4
    for(k4  <- 0 until no_of_chunks4+1)
     {
        var file_content4 = Source.fromFile(filename4)
        val total_lines4 = file_content4.getLines()
         file_content4 = Source.fromFile(filename4)
        if((k4+1)*chunk_size4 > file_content4.getLines().length) 
        {
           file_content4 = Source.fromFile(filename4)
           router4 ! total_lines4.slice(k4 * chunk_size4,file_content4.getLines().length).toList
           file_content4.close()
        }
        else
        {
           router4 ! total_lines4.slice(k4 * chunk_size4, (k4+1) * chunk_size4).toList
          file_content4.close()
       }      
    }
   }
 }
} 



class LocalActor5 extends Actor {
  import context._
  var ip1=""
  var ip2=""
  var paths5 = List("akka.tcp://JsonActors@"+ip1+"/user/BannerProcess","akka.tcp://JsonActors@"+ip2+"/user/BannerProcess")
  val filename5 = "/home/ubuntu/sbt/Files/Third_5cr_4.json"
  val router5: ActorRef = context.actorOf(RoundRobinGroup(paths5.toList).props(), "router5")
  var counter = 0
  var total_counter =0
  var i=0
  var j=0
  def receive = {
    case "START" =>
      router5 ! "Hello from the LocalActor"
    case "START PROCESS" =>{
    val line_count5 = Source.fromFile(filename5).getLines.size
    val chunk_size5:Int = 3500
    val no_of_chunks5 = line_count5/chunk_size5
    for(k5  <- 0 until no_of_chunks5+1)
     {
        var file_content5 = Source.fromFile(filename5)
        val total_lines5 = file_content5.getLines()
         file_content5 = Source.fromFile(filename5)
        if((k5+1)*chunk_size5 > file_content5.getLines().length) 
        {
           file_content5 = Source.fromFile(filename5)
           router5 ! total_lines5.slice(k5 * chunk_size5,file_content5.getLines().length).toList
           file_content5.close()
        }
        else
        {
           router5 ! total_lines5.slice(k5 * chunk_size5, (k5+1) * chunk_size5).toList
          file_content5.close()
       }      
    }
   }
 }
} 




class LocalActor6 extends Actor {
  import context._
  var ip1=""
  var ip2=""
  var paths6 = List("akka.tcp://JsonActors@"+ip1+"/user/BannerProcess","akka.tcp://JsonActors@"+ip2+"/user/BannerProcess")
  val filename6 = "/home/ubuntu/sbt/Files/Second_5cr_6.json"
  val router6: ActorRef = context.actorOf(RoundRobinGroup(paths6.toList).props(), "router6")
  var counter = 0
  var total_counter =0
  var i=0
  var j=0
  def receive = {
    case "START" =>
      router6 ! "Hello from the LocalActor"
    case "START PROCESS" =>{
    val line_count6 = Source.fromFile(filename6).getLines.size
    val chunk_size6:Int = 3000
    val no_of_chunks6 = line_count6/chunk_size6
    for(k6  <- 0 until no_of_chunks6+1)
     {
        var file_content6 = Source.fromFile(filename6)
        val total_lines6 = file_content6.getLines()
         file_content6 = Source.fromFile(filename6)
        if((k6+1)*chunk_size6 > file_content6.getLines().length) 
        {
           file_content6 = Source.fromFile(filename6)
           router6 ! total_lines6.slice(k6 * chunk_size6,file_content6.getLines().length).toList
           file_content6.close()
        }
        else
        {
          router6 ! total_lines6.slice(k6 * chunk_size6, (k6+1) * chunk_size6).toList
          file_content6.close()
       }      
    }
   }
 }
} 
