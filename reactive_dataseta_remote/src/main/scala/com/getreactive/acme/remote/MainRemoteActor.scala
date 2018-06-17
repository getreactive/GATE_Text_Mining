package com.getreactive.acme.remote

import java.util.Calendar
import akka.routing.Broadcast
import com.sigmoid.kryptos.Gate.RequestHandler
import com.sigmoid.kryptos.Gate.GateInitListener
import net.liftweb.json.DefaultFormats
import net.liftweb.json._
import scala.io.Source
import akka.actor._
import scala.concurrent.duration._
import akka.routing.{Broadcast, RoundRobinPool, RoundRobinGroup}
import akka.actor.PoisonPill

class BannerProcess extends Actor with ActorLogging
{
  /*
     This class recieves input from local actor and sends each string to a router for processing
   */
  var counter=0
  val router2: ActorRef =
  context.actorOf(RoundRobinPool(1).props(Props[RoutedActor]), "router2")
  context.watch(router2)
  def receive = {

    case Terminated(corpse) =>

      if (corpse == router2) {
        log.warning("Received termination notification for '" + corpse + "'," +
          "is in our watch list. Terminating ActorSystem.")

      } else {
        log.info("Received termination notification for '" + corpse + "'," +
          "which is not in our deathwatch list.".format(corpse))
      }


    case list:List[String] =>
    {

        println("Length of list is***************"+list.length);
        if(list.length%1000!=0)
         {
            println("Length of list is******sending null**********"+list.length);
              router2 ! ""
              for(i <- 0 until list.length) {
                router2 ! list(i)
              }
         }
        else
        {
            println("Length of list is*********in else&&&******"+list.length);
            for(i <- 0 until list.length) {
                router2 ! list(i)
              }
            }
   }
    case msg:String => {

     import context._
      println("____default case____")
      router2 ! Broadcast(PoisonPill)
      router2 ! "Check******"
     }
  }
 }

class RoutedActor extends Actor with ActorLogging
{
  /*
  This class sends the string to requesthandler
   */
  val request_class: RequestHandler = new RequestHandler()

  def receive = {
  case json_str:String =>  {

       request_class.process_request(json_str)

    }
   }
 }


object ReadInputFile {

  /*

  This class creates actor system

 */

  implicit val formats = DefaultFormats

  val processingsystem = ActorSystem("JsonActors")

  def main(args: Array[String]) {

    //Calling code for initializing Gate Plugins and controlling
    GateInitListener.context()
    val simpleRouted = processingsystem.actorOf(Props[BannerProcess], "BannerProcess")

  

  }

}

