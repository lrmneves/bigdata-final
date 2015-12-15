package hadoop;
import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
 

public class InverseReducer extends Reducer<Text, Text, Text, Text> {
 
	 protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
	        StringBuilder builder = new StringBuilder();
	        for (Text val : values) {
	        	builder.append(val.toString()).append(",");
	        }
	        context.write(key, new Text( builder.substring(0, builder.length()-1)));
	        
	    }
}