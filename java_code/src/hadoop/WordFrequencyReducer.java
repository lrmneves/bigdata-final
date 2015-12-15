package hadoop;
import java.io.IOException;


import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;

/**
 * Reducer for the first step, word frequency counter
 * @author lrmneves
 *
 */
public class WordFrequencyReducer extends Reducer<Text, IntWritable, Text, IntWritable>{
	
	
	public void reduce(Text key,  Iterable<IntWritable> values,Context context) throws IOException, InterruptedException
	{	//Sum up all word@document occurrences
		int sum = 0;
		for(IntWritable v : values){
			sum+=v.get();
		}
		context.write(key, new IntWritable(sum));
	}
}


