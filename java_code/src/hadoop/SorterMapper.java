package hadoop;
import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
 

public class SorterMapper extends Mapper<LongWritable, Text, Text, Text> {
 
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
    	String [] values = value.toString().split(",");
    	double tfidf =Double.parseDouble(values[values.length -1].replace(",", ".").replace("]", "").trim()); 
    	String [] docSep = value.toString().split("\t");
    	String wordAtDoc = docSep[0];
    	docSep = docSep[0].split("@");
        context.write(new Text(docSep[1]+"@"+(1-tfidf)), new Text(wordAtDoc + "#"+tfidf));
    }
}