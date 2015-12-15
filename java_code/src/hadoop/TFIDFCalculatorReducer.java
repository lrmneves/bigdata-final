package hadoop;
 
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
 
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
 

public class TFIDFCalculatorReducer extends Reducer<Text, Text, Text, Text> {
 
    private static final DecimalFormat DF = new DecimalFormat("###.########");
 
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        
    	int totalDocs = Integer.parseInt(context.getJobName());
        
    	int totalDocsWhereWeCanFindTerm = 0;
        
    	Map<String, String> tempFrequencies = new HashMap<String, String>();
        for (Text val : values) {
            String[] documentAndFrequencies = val.toString().split("=");
            totalDocsWhereWeCanFindTerm++;
            tempFrequencies.put(documentAndFrequencies[0], documentAndFrequencies[1]);
        }
        for (String document : tempFrequencies.keySet()) {
            String[] wordFrequenceAndTotalWords = tempFrequencies.get(document).split("/");
 
            double tf = Double.valueOf(Double.valueOf(wordFrequenceAndTotalWords[0])
                    / Double.valueOf(wordFrequenceAndTotalWords[1]));
 
            double idf = (double) totalDocs / (double) totalDocsWhereWeCanFindTerm;
 
            double tfIdf = totalDocs == totalDocsWhereWeCanFindTerm ?
                    tf : tf * Math.log10(idf);
 
            context.write(new Text(key + "@" + document), new Text("[" + totalDocsWhereWeCanFindTerm + "/"
                    + totalDocs + " , " + wordFrequenceAndTotalWords[0] + "/"
                    + wordFrequenceAndTotalWords[1] + " , " + DF.format(tfIdf) + "]"));
        }
    }
}