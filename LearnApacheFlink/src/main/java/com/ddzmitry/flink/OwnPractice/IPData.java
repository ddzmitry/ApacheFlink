package com.ddzmitry.flink.OwnPractice;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

public class IPData {

    public static void main(String[] args) throws Exception {


        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStream<String> data = env.readTextFile("C:\\Users\\ddzmi\\Desktop\\Tutoring Trilogy\\ApacheFlink\\LearnApacheFlink\\src\\datasets\\ip-data.txt");

        DataStream<Tuple2<String, String>> WebSiteAndAllData = data.map(new MapFunction<String, Tuple2<String, String>>() {
            @Override
            public Tuple2<String, String> map(String value) throws Exception {
//                id_9328,Cox Communications,US,932291609761649,www.americanowl.com,88
//                user_id,network_name,user_IP,user_country,website, Time spent before next click
                String[] all_data = value.split(",");
//                (website,| user_id,network_name,user_IP,user_country,website, Time spent before next click)
                return new Tuple2<>(all_data[4], value);
            }
        });
//        WebSiteAndAllData.print();


        DataStream<Tuple2<String, String>> UsDataOnly = WebSiteAndAllData.filter(new FilterFunction<Tuple2<String, String>>() {
            @Override
            public boolean filter(Tuple2<String, String> stringStringTuple2) throws Exception {
//                Get only data thats from US
                String country = stringStringTuple2.f1.split(",")[3];

                return !country.equals("US");
            }
        });


        DataStream<Tuple2<String, Integer>> cllicksOnUSWebsites = UsDataOnly.map(new MapFunction<Tuple2<String, String>, Tuple3<String, String, Integer>>() {
            public Tuple3<String, String, Integer> map(Tuple2<String, String> value) {
                return new Tuple3<String, String, Integer>(value.f0, value.f1, 1);
            }
        })
                .keyBy(0)
                .window(TumblingProcessingTimeWindows.of(Time.milliseconds(5)))
                .sum(2)
                .map(new MapFunction<Tuple3<String, String, Integer>, Tuple2<String, Integer>>() {
                    public Tuple2<String, Integer> map(Tuple3<String, String, Integer> value) {
                        return new Tuple2<String, Integer>(value.f0, value.f2);
                    }
                });
//        cllicksOnUSWebsites.print();
        cllicksOnUSWebsites.writeAsText("C:\\Users\\ddzmi\\Desktop\\Tutoring Trilogy\\ApacheFlink\\LearnApacheFlink\\src\\dataout\\UsClicksCountWebsite");

        // website with max clicks
        DataStream<Tuple2<String, Integer>> maxClicks = cllicksOnUSWebsites
                .keyBy(0)
                .window(TumblingProcessingTimeWindows.of(Time.milliseconds(5)))
                .maxBy(1);

        // website with max clicks
        DataStream<Tuple2<String, Integer>> minClicks = cllicksOnUSWebsites
                .keyBy(0)
                .window(TumblingProcessingTimeWindows.of(Time.milliseconds(5)))
                .minBy(1);

        DataStream<Tuple2<String, Integer>> avgTimeWebsite =
                UsDataOnly
                        .map(new MapFunction<Tuple2<String, String>, Tuple3<String, Integer, Integer>>() {
                            public Tuple3<String, Integer, Integer> map(Tuple2<String, String> value) {
                                int timeSpent = Integer.parseInt(value.f1.split(",")[5]);
                                return new Tuple3<String, Integer, Integer>(value.f0, 1, timeSpent);
                            }
                        })
                        .keyBy(0)
                        .window(TumblingProcessingTimeWindows.of(Time.milliseconds(5)))
                        .reduce(new ReduceFunction<Tuple3<String, Integer, Integer>>() {
                            public Tuple3<String, Integer, Integer> reduce(Tuple3<String, Integer, Integer> v1,
                                                                           Tuple3<String, Integer, Integer> v2) {
                                return new Tuple3<String, Integer, Integer>(v1.f0, v1.f1 + v2.f1, v1.f2 + v2.f2);
                            }
                        })
                        .map(new MapFunction<Tuple3<String, Integer, Integer>, Tuple2<String, Integer>>() {
                            public Tuple2<String, Integer> map(Tuple3<String, Integer, Integer> value) {
                                return new Tuple2<String, Integer>(value.f0, (value.f2 / value.f1));
                            }
                        });
        avgTimeWebsite.print();

        DataStream<Tuple2<String, Integer>> usersPerWebsite =	 UsDataOnly
                .keyBy(0)
                .flatMap(new Click.DistinctUsers());

        env.execute("MappingIpData");
    }

}
