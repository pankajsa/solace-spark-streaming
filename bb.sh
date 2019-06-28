gradle clean && gradle shadowJar && spark-submit --name "App1" --master local[4] --class streaming.SimpleApp build/libs/solace-spark-streaming-all.jar 
