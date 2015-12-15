package hadoop;
import java.io.IOException;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
/**
 * Mapper for the first step, word frequency count
 * @author lrmneves
 *
 */
public class WordFrequencyMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
	HashSet<String> set = new HashSet<String>();
	
	public void map (LongWritable key , Text value, Context context) throws IOException, InterruptedException
	{	
		
		//Regex to find the words in the text
		Pattern p = Pattern.compile("\\w+");
		Matcher m = p.matcher(value.toString());
		//Reads the file name
		String fileName = ((FileSplit) context.getInputSplit()).getPath().getName();
		if(set.add(fileName) == true){
			context.getCounter(DocumentCounter.DOCUMENTS).increment(1);
		}
		
		StringBuilder valueBuilder = new StringBuilder();
		while (m.find()) {
			String matchedKey = m.group().toLowerCase();
			//Ignores single digits, special chars and words with _ 
			if (!Character.isLetter(matchedKey.charAt(0)) || Character.isDigit(matchedKey.charAt(0))
					||  matchedKey.contains("_") || matchedKey.length() < 2) {
				continue;
			}
			//Creates key word@document
			valueBuilder.append(matchedKey);
			valueBuilder.append("@");
			valueBuilder.append(fileName);
			//Copies the wordcount example, sending our key and 1 as the value, so we can sum up the counts
			context.write(new Text(valueBuilder.toString()), new IntWritable(1));
			valueBuilder.delete(0, valueBuilder.length());
		}
	}

}