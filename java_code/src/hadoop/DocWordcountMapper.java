package hadoop;
import java.io.IOException;
 
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
 

public class DocWordcountMapper extends Mapper<LongWritable, Text, Text, Text> {
 
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String[] wordAndDocCounter = value.toString().split("\t");
        String[] wordAndDoc = wordAndDocCounter[0].split("@");
        context.write(new Text(wordAndDoc[1]), new Text(wordAndDoc[0] + "=" + wordAndDocCounter[1]));
    }
}