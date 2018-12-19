package com.ddzmitry.flink.OwnPractice;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.IterativeStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
public class IterationOwn {

    public static void main(String[] args) throws Exception{

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();


        DataStream<Tuple2<Long,Integer>> data = env.generateSequence(0,25).map(new MapFunction<Long, Tuple2<Long, Integer>>() {
            @Override
            public Tuple2<Long, Integer> map(Long value) throws Exception {
                return new Tuple2<Long,Integer>(value,0);
            }
        });

//        data.print();

        DataStream<Tuple2<Long,Integer>> dataFiltered = data.filter(new FilterFunction<Tuple2<Long, Integer>>() {
            @Override
            public boolean filter(Tuple2<Long, Integer> tupleValue) throws Exception {
                if(tupleValue.f0 %2 ==0){
                    return true;
                }
                else return false;
            }
        });

//        dataFiltered.print();

        final char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();

//        Assign Integer to Alphabet
        DataStream<Tuple3<Long,Integer,Character>> dataWithAssignedLetter = dataFiltered.map(new MapFunction<Tuple2<Long, Integer>, Tuple3<Long, Integer, Character>>() {
            @Override
            public Tuple3<Long, Integer, Character> map(Tuple2<Long, Integer> longIntegerTuple2) throws Exception {
                return new Tuple3<>(longIntegerTuple2.f0,longIntegerTuple2.f1,alphabet[Integer
                        .parseInt(String
                                .valueOf(longIntegerTuple2.f0))]);
            }
        });
//        dataWithAssignedLetter.print();
        IterativeStream<Tuple3<Long, Integer, Character>> IteratedStreamOfTuples = dataWithAssignedLetter.iterate(5000);

//        Make sure that the values only A or otherrwise we will reducing counter of first value to bring it to index of a
        DataStream<Tuple3<Long,Integer,Character>> OnlyAOrReturnBack = IteratedStreamOfTuples.map(new MapFunction<Tuple3<Long, Integer, Character>, Tuple3<Long, Integer, Character>>() {
            @Override
            public Tuple3<Long, Integer, Character> map(Tuple3<Long, Integer, Character> longIntegerCharacterTuple3) throws Exception {
                if (longIntegerCharacterTuple3.f2 == 'a'){
                    return longIntegerCharacterTuple3;
                } else {
                    return new Tuple3<Long, Integer, Character>(longIntegerCharacterTuple3.f0-1,longIntegerCharacterTuple3.f1,alphabet[Integer
                            .parseInt(String
                                    .valueOf(longIntegerCharacterTuple3.f0 -1))]);
                }
            }
        });



//        We filter to find a character
        DataStream<Tuple3<Long,Integer,Character>> FilteredOnA = OnlyAOrReturnBack.filter(new FilterFunction<Tuple3<Long, Integer, Character>>() {
            @Override
            public boolean filter(Tuple3<Long, Integer, Character> longIntegerCharacterTuple3) throws Exception {
               if (longIntegerCharacterTuple3.f2 == 'a'){
//                   if it is A then good
                   return false;
               }
//               else we go back
               else return true;
            }
        });
//        We iterate untill all values are a
        IteratedStreamOfTuples.closeWith(FilteredOnA);

//        IteratedStreamOfTuples.print();
        DataStream <Tuple3<Long,Integer,Character>> equalToA = OnlyAOrReturnBack.filter(new FilterFunction<Tuple3<Long, Integer, Character>>() {
            @Override
            public boolean filter(Tuple3<Long, Integer, Character> longIntegerCharacterTuple3) throws Exception {
                if (longIntegerCharacterTuple3.f2 == 'a') return true;
                else return false;
            };
        });
//        equalToA.print();
        equalToA.writeAsText("C:\\Users\\ddzmi\\Desktop\\Tutoring Trilogy\\ApacheFlink\\LearnApacheFlink\\src\\datasets\\equalToA");

        env.execute("Iteration Demo");


    }
}
