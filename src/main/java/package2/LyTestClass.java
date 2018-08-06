package package2;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;

public class LyTestClass {
        
    public static void main(String[] args) throws Exception {        
                                
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
                                
        //socketTextStream:表示做为客户端连接并接收来自服务端的数据
        //先要打开TCP Server
        //在服务端发送一些空格分隔的单词
        //第3个参数是分隔符.通过分隔符分成多个String元素
        DataStream<String> dataStream = env.socketTextStream("10.100.151.30", 9999,"\n");
        
        //对数据流执行flatMap转换
        //flatMap:读入一个元素，返回转换后的0个、1个或者多个Tuple2<String, Integer>元素
        //例如,读入元素a b c a,返回(a,1),(b,1),(c,1),(a,1)
        //SingleOutputStreamOperator<Tuple2<String, Integer>> singleOutputStreamOperator = dataStream.flatMap(new Splitter());
        
        //将上一步生成的Tuple2<String, Integer>集合进行keyBy操作
        //keyBy:逻辑上将流分区为不相交的分区，每个分区包含相同key的元素
        //注意,只是逻辑上。故keyedStream.print()与singleOutputStreamOperator.print()的效果一样
        //KeyedStream<Tuple2<String, Integer>, Tuple> keyedStream = singleOutputStreamOperator.keyBy(0);
        
        //事件驱动的（比如：每30秒）//数据驱动 （比如：每100个元素）
        //窗口通常被区分为不同的类型，比如 滚动窗口 （没有重叠）， 滑动窗口 （有重叠），以及 会话窗口 （由不活动的间隙所打断）
        //timeWindow定义了滚动窗口
        //WindowedStream<Tuple2<String, Integer>, Tuple, TimeWindow> windowedStream = keyedStream.timeWindow(Time.seconds(5));
        
        //对Tuple2<String, Integer>的Integer相加操作
        //SingleOutputStreamOperator<Tuple2<String, Integer>> result = windowedStream.sum(1);
          
        //连写跟上面的效果一样
        DataStream<Tuple2<String, Integer>> result = dataStream
                .flatMap(new Splitter())
                .keyBy(0)
                .timeWindow(Time.seconds(5))
                .sum(1);
        
        result.print();

        env.execute("jobName2");//*/
    }
    
    public static class Splitter implements FlatMapFunction<String, Tuple2<String, Integer>> {

        private static final long serialVersionUID = 1L;

        @Override
        public void flatMap(String sentence, Collector<Tuple2<String, Integer>> out) throws Exception {
            for (String word: sentence.split(" ")) {
                out.collect(new Tuple2<String, Integer>(word, 1));
            }
        }
    }
}
