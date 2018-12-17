import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.UnsortedGrouping;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
// To run
// ./bin/flink run -c WordCount ~/FlinkProject/target/ApacheFlinkIntro-1.0-SNAPSHOT.jar --input ~/FlinkProject/wc.txt --output ~/out
public class WordCount
{
    public static void main(String[] args)
            throws Exception
    {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        ParameterTool params = ParameterTool.fromArgs(args);

        env.getConfig().setGlobalJobParameters(params);
//        params.get("input")
        DataSet<String> text = env.readTextFile(params.get("input"));

        DataSet<String> filtered = text.filter(new FilterFunction<String>()

        {
//            Filter function
            public boolean filter(String value)
            {
                return value.startsWith("N");
            }
        });
        DataSet<Tuple2<String, Integer>> tokenized = filtered.map(new Tokenizer());

//        groupby 0 -> Means group by first firld of tuple
        DataSet<Tuple2<String, Integer>> counts = tokenized.groupBy(new int[] { 0 }).sum(1);
        counts.map(x ->{
            System.out.println(x);
            return x;
        });
        counts.writeAsCsv("C:\\Users\\ddzmi\\Desktop\\out", "\n", " ");
        if (params.has("output"))
        {
            counts.writeAsCsv(params.get("output"), "\n", " ");

            env.execute("WordCount Example");
        }
    }

    public static final class Tokenizer
            implements MapFunction<String, Tuple2<String, Integer>>
    {
        public Tuple2<String, Integer> map(String value)
        {
//            Return Tuple of value, and 1 (Noman,1),(Nipun,1),(Noman,1)
            return new Tuple2(value, Integer.valueOf(1));
        }
    }
}
