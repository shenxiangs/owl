package com.databricks.logs.streaming;

import java.io.Serializable;
import java.util.Comparator;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import scala.Tuple2;

import com.databricks.logs.ApacheAccessLog;
import java.util.List;

public class LogAnalyzerStreaming {
	  private static final Duration WINDOW_LENGTH = new Duration(30 * 1000);
	  private static final Duration SLIDE_INTERVAL = new Duration(10 * 1000);
	private static class ValueComparator<K, V> implements Comparator<Tuple2<K, V>>, Serializable {
	    private Comparator<V> comparator;

	    public ValueComparator(Comparator<V> comparator) {
	      this.comparator = comparator;
	    }

	    @Override
	    public int compare(Tuple2<K, V> o1, Tuple2<K, V> o2) {
	      return comparator.compare(o1._2(), o2._2());
	    }
	  }
	private static final Function2<Long, Long, Long> SUM_REDUCEE = (a,b)->a+b;
	public static void main(String[] args) throws InterruptedException {
		SparkConf conf = new SparkConf().setMaster("local[4]").setAppName("Log Analyzer Streaming");
	    JavaSparkContext sc = new JavaSparkContext(conf);
	    JavaStreamingContext jssc = new JavaStreamingContext(sc,
	        SLIDE_INTERVAL);
	    JavaReceiverInputDStream<String> logDataDStream =
	        jssc.socketTextStream("192.168.10.128", 9999);
	    JavaDStream<ApacheAccessLog> accessLogDStream =
	        logDataDStream.map(ApacheAccessLog::parseFromLogLine);
	    JavaDStream<ApacheAccessLog> windowDStream =
	        accessLogDStream.window(WINDOW_LENGTH, SLIDE_INTERVAL);
	    windowDStream.foreachRDD(accessLogs ->{
	    	if (accessLogs.count() == 0) {
	            System.out.println("No access logs in this time interval");
	            return;
	          }
	    	JavaRDD<Long> contentSize = accessLogs.map(ApacheAccessLog::getContentSize).cache();
	    	System.out.println(String.format("Content Size Avg: %s, Min: %s, Max: %s", 
	    			contentSize.reduce(SUM_REDUCEE)/ contentSize.count(),
	    			contentSize.min(Comparator.naturalOrder()),
	    			contentSize.max(Comparator.naturalOrder())));
	    	List<Tuple2<Integer, Long>> responseCodeToCount =
	    	          accessLogs.mapToPair(log -> new Tuple2<>(log.getResponseCode(), 1L))
	    	              .reduceByKey(SUM_REDUCEE)
	    	              .take(100);
	    	System.out.println(String.format("Response code counts: %s", responseCodeToCount));
	    	List<String> ipAddresses =
	    	          accessLogs.mapToPair(log -> new Tuple2<>(log.getIpAddress(), 1L))
	    	              .reduceByKey(SUM_REDUCEE)
	    	              .filter(tuple -> tuple._2() > 10)
	    	              .map(Tuple2::_1)
	    	              .take(100);
	    	      System.out.println(String.format("IPAddresses > 10 times: %s", ipAddresses));
	    	      List<Tuple2<String, Long>> topEndpoints = accessLogs
	    	              .mapToPair(log -> new Tuple2<>(log.getEndpoint(), 1L))
	    	              .reduceByKey(SUM_REDUCEE)
	    	              .top(10, new ValueComparator<>(Comparator.<Long>naturalOrder()));
	    	          System.out.println(String.format("Top Endpoints: %s", topEndpoints));
	    	          return;
	    });
	    jssc.start();              // Start the computation
	    jssc.awaitTermination();
	}
}