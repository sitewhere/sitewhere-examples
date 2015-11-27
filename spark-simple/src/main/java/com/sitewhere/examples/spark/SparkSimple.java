/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.examples.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import scala.Tuple2;

import com.sitewhere.spark.SiteWhereReceiver;
import com.sitewhere.spi.device.event.IDeviceEvent;

/**
 * Simple example of ingesting SiteWhere events into Spark stream processing. The example
 * gives a count of how many events are processed for each unique assignment.
 * 
 * @author Derek
 */
public class SparkSimple {

	/** Address Hazelcast client will use for connection */
	public static final String HAZELCAST_ADDRESS = "localhost:5701";

	/** Hazelcast group name */
	public static final String HAZELCAST_USERNAME = "sitewhere";

	/** Hazelcast group password */
	public static final String HAZELCAST_PASSWORD = "sitewhere";

	/** Hazelcast group password */
	public static final String TENANT_ID = "default";

	@SuppressWarnings("serial")
	public static void main(String[] args) {
		SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("SparkSimple");
		JavaStreamingContext context = null;
		try {
			context = new JavaStreamingContext(conf, Durations.seconds(1));
			JavaReceiverInputDStream<IDeviceEvent> sitewhere =
					context.receiverStream(new SiteWhereReceiver(HAZELCAST_ADDRESS, HAZELCAST_USERNAME,
							HAZELCAST_PASSWORD, TENANT_ID));
			JavaPairDStream<String, Integer> pairs =
					sitewhere.mapToPair(new PairFunction<IDeviceEvent, String, Integer>() {
						@Override
						public Tuple2<String, Integer> call(IDeviceEvent s) {
							return new Tuple2<String, Integer>(s.getDeviceAssignmentToken(), 1);
						}
					});
			JavaPairDStream<String, Integer> assignmentCounts =
					pairs.reduceByKey(new Function2<Integer, Integer, Integer>() {
						@Override
						public Integer call(Integer i1, Integer i2) {
							return i1 + i2;
						}
					});
			assignmentCounts.print();
		} finally {
			if (context != null) {
				context.close();
			}
		}
	}
}