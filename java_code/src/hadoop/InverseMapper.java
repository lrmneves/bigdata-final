package hadoop;
import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
 

public class InverseMapper extends Mapper<LongWritable, Text, Text, Text> {
 
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
    	String [] docSep = value.toString().split("\t");
    	docSep = docSep[0].split("@");

        context.write(new Text(docSep[0]), new Text(docSep[1]));
    }
}