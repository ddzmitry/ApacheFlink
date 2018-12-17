### Flink

> Download Flink https://flink.apache.org/downloads.html
> tar xzf flink-1.7.0-bin-hadoop28-scala_2.11.tgz
> Start ./bin/start-cluster.sh
> Avaliable at hostname:8081


#### Functions to Read with Apache Flink
```sh
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        env.readTextFile("path")
        env.readCsvFile("path")
        env.readFileOfPrimitives("path",String.class)
        env.readFileOfPrimitives("path","delimiter",String.class)
        env.readHadoopFile("FileInputFormat(avro , parquet)",Key,Value,path)     
        
```
> To run 
```bash
./bin/flink run 
-c WordCount 
~/FlinkProject/target/ApacheFlinkIntro-1.0-SNAPSHOT.jar 
--input ~/FlinkProject/wc.txt 
--output ~/out

root@apache-flink:~# cat out
Nana 2
Nanaho 2
Nate 1
Nicole 7
Nipul 6
Nipun 2
Noel 1


```

### Joins
```
Joint Hints 

Leave Choice To System
DataSet<Tuple3<Integer, String, String>> joined = personSet.join(locationSet, JoinOperatorBase.JoinHint.OPTIMIZER_CHOOSES)
Possible if Datasets on Different Nodes that will help to speed up (If onee file is smal and another is bigger, 
it will broadcast small dataset in memory and will decrease time of code)
DataSet<Tuple3<Integer, String, String>> joined = personSet.join(locationSet, JoinOperatorBase.JoinHint.BROADCAST_HASH_FIRST)
Oposite if second set is bigger then first
DataSet<Tuple3<Integer, String, String>> joined = personSet.join(locationSet, JoinOperatorBase.JoinHint.BROADCAST_HASH_SECOND)
Will hash table from first input will speed up process on first dataset
DataSet<Tuple3<Integer, String, String>> joined = personSet.join(locationSet, JoinOperatorBase.JoinHint.REPARTITION_HASH_FIRST)
If second bifgger then first
DataSet<Tuple3<Integer, String, String>> joined = personSet.join(locationSet, JoinOperatorBase.JoinHint.REPARTITION_HASH_SECOND)
Will Merge Datasets inMemory
DataSet<Tuple3<Integer, String, String>> joined = personSet.join(locationSet, JoinOperatorBase.JoinHint.REPARTITION_SORT_MERGE)

```

#### DatastreamOperations
