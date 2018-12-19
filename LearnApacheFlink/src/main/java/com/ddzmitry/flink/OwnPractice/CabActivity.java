package com.ddzmitry.flink.OwnPractice;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.tuple.*;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class CabActivity {
    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<String> data = env.readTextFile("C:\\Users\\ddzmi\\Desktop\\Tutoring Trilogy\\ApacheFlink\\LearnApacheFlink\\src\\datasets\\cab-flink.txt");
//id_4214,PB7526,Sedan,Wanda,yes,Sector 19,Sector 10,5

        DataStream<Tuple9<String, String, String, String, String, String, String, String, Integer>> SplitedData = data.map(new Splitter());
//        SplitedData.print();

        DataStream<Tuple9<String, String, String, String, String, String, String, Integer, Integer>> DataWithPassangersOnly = SplitedData
                .map(new MapFunction<Tuple9<String, String, String, String, String, String, String, String, Integer>, Tuple9<String, String, String, String, String, String, String, Integer, Integer>>() {
                    @Override
                    public Tuple9<String, String, String, String, String, String, String, Integer, Integer> map(Tuple9<String, String, String, String, String, String, String, String, Integer> dataCabs) throws Exception {
                        System.out.println(dataCabs.f7);
                        if (dataCabs.f7.equals("'null'")) {
                            return new Tuple9<>(dataCabs.f0, dataCabs.f1, dataCabs.f2, dataCabs.f3, dataCabs.f4, dataCabs.f5, dataCabs.f6, 0, dataCabs.f8);

                        } else
                            return new Tuple9<>(dataCabs.f0, dataCabs.f1, dataCabs.f2, dataCabs.f3, dataCabs.f4, dataCabs.f5, dataCabs.f6, Integer.parseInt(dataCabs.f7), dataCabs.f8);
                    }

                    ;
                });
//        DataWithPassangersOnly.keyBy(2).sum(7).writeAsText("C:\\Users\\ddzmi\\Desktop\\Tutoring Trilogy\\ApacheFlink\\LearnApacheFlink\\src\\dataout\\sumofpassangers");
//        DataWithPassangersOnly.keyBy(2).max(7).writeAsText("C:\\Users\\ddzmi\\Desktop\\Tutoring Trilogy\\ApacheFlink\\LearnApacheFlink\\src\\dataout\\maxPassangers");
//        DataWithPassangersOnly.keyBy(6).sum(7).writeAsText("C:\\Users\\ddzmi\\Desktop\\Tutoring Trilogy\\ApacheFlink\\LearnApacheFlink\\src\\dataout\\tripPopularity");
//        DataWithPassangersOnly.keyBy(6).maxBy(7).writeAsText("C:\\Users\\ddzmi\\Desktop\\Tutoring Trilogy\\ApacheFlink\\LearnApacheFlink\\src\\dataout\\myxByPassangerPopularity");

//          DataWithPassangersOnly.keyBy(0).reduce(new  Reduce1()).writeAsText("C:\\Users\\ddzmi\\Desktop\\Tutoring Trilogy\\ApacheFlink\\LearnApacheFlink\\src\\dataout\\totalPassangersPerCab");
        DataStream<Tuple3<String, Integer, Integer>> AngTrips = DataWithPassangersOnly.keyBy(0).reduce(new Reduce1()).map(new MapFunction<Tuple9<String, String, String, String, String, String, String, Integer, Integer>, Tuple3<String, Integer, Integer>>() {
            @Override
            public Tuple3<String, Integer, Integer> map(Tuple9<String, String, String, String, String, String, String, Integer, Integer> avgData) throws Exception {
                return new Tuple3<>(avgData.f1, avgData.f7, avgData.f7 / avgData.f8);
            }
        });


//        AngTrips.writeAsText("C:\\Users\\ddzmi\\Desktop\\Tutoring Trilogy\\ApacheFlink\\LearnApacheFlink\\src\\dataout\\AngTripsPerCabId");

        env.execute("Iteration Demo");

    }


    public static class Reduce1 implements ReduceFunction<Tuple9<String, String, String, String, String, String, String, Integer, Integer>> {
        public Tuple9<String, String, String, String, String, String, String, Integer, Integer> reduce(Tuple9<String, String, String, String, String, String, String, Integer, Integer> current,
                                                                                                       Tuple9<String, String, String, String, String, String, String, Integer, Integer> pre_result) {
            return new Tuple9<String, String, String, String, String, String, String, Integer, Integer>
                    (current.f0, current.f1, current.f2, current.f3, current.f4, current.f5, current.f6, current.f7 + pre_result.f7, current.f8 + pre_result.f8);
        }
    }

    public static class Splitter implements MapFunction<String, Tuple9<String, String, String, String, String, String, String, String, Integer>> {
        public Tuple9<String, String, String, String, String, String, String, String, Integer> map(String value) {
            String[] cabdata = value.split(",");
//            System.out.println(cabdata.length);
//            System.out.println(cabdata.toString());
//            for (int i = 0; i <cabdata.length ; i++) {
//                System.out.println(i);
//                System.out.println(cabdata[i]);
//            }

            return new Tuple9<String, String, String, String, String, String, String, String, Integer>(
                    cabdata[0],
                    cabdata[1],
                    cabdata[2],
                    cabdata[3],
                    cabdata[4],
                    cabdata[5],
                    cabdata[6],
                    cabdata[7],
                    Integer.valueOf(1));

        }


    }

}
