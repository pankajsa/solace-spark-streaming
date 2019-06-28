package streaming

import events.AppSingleton
import org.apache.spark._
import org.apache.spark.streaming._
//import org.slf4j.Logger
//import org.slf4j.LoggerFactory

import org.apache.log4j.Logger
import org.apache.log4j.Level
import events.SolaceReceiver



object SimpleApp{
  val uri = "tcp://vmr-mr3e5sq7dacxp.messaging.solace.cloud:20480"
  val username = "solace-cloud-client"
  val password = "cge4fi7lj67ms6mnn2b4pe76g2"
  val vpn = "msgvpn-8ksiwsp0mtv"
  val queueName = "payment/card"

  val checkpointDirectory = "tmp/mychkpt"

  // Function to create and setup a new StreamingContext
  def functionToCreateContext(): StreamingContext = {
    val conf = new SparkConf()
//        .setMaster("local[2]")
        .setAppName("SimpleApp")
    val ssc = new StreamingContext(conf, Seconds(10))



    val s1 = ssc.receiverStream(new SolaceReceiver(uri, username, password, vpn, queueName))
    val s2 = ssc.receiverStream(new SolaceReceiver(uri, username, password, vpn, queueName))
    val s3 = ssc.receiverStream(new SolaceReceiver(uri, username, password, vpn, queueName))

    val lines = s1.union(s2).union(s3)


    val words = lines.flatMap(_.split(" "))

    //    words.countByValue().print()
    val wordCounts = words.map(x => (x, 1)).reduceByKey(_ + _)


    wordCounts.print()

    ssc.checkpoint(checkpointDirectory)   // set checkpoint directory
    ssc
  }


  def main(args: Array[String]): Unit ={
    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)

    val log = Logger.getLogger("SimpleApp")
    log.info("Application starting")

    // Get StreamingContext from checkpoint data or create a new one



    val context = StreamingContext.getOrCreate(checkpointDirectory, functionToCreateContext _)
//    val context =  functionToCreateContext()




    context.start()
    context.awaitTermination()
    Thread.sleep(100000)

  }
}
