package hadoop;
import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
 

public class TFIDFCalculatorMapper extends Mapper<LongWritable, Text, Text, Text> {
 
   
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String[] wordAndCounters = value.toString().split("\t");
        String[] wordAndDoc = wordAndCounters[0].split("@");                
        context.write(new Text(wordAndDoc[0]), new Text(wordAndDoc[1] + "=" + wordAndCounters[1]));
    }
}
