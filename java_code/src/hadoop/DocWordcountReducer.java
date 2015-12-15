package hadoop;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
 
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
 

public class DocWordcountReducer extends Reducer<Text, Text, Text, Text> {
 
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        int sumOfWordsInDocument = 0;
        Map<String, Integer> tempCounter = new HashMap<String, Integer>();
        for (Text val : values) {
            String[] wordCounter = val.toString().split("=");
            tempCounter.put(wordCounter[0], Integer.valueOf(wordCounter[1]));
            sumOfWordsInDocument += Integer.parseInt(val.toString().split("=")[1]);
        }
        for (String wordKey : tempCounter.keySet()) {
            context.write(new Text(wordKey + "@" + key.toString()), new Text(tempCounter.get(wordKey) + "/"
                    + sumOfWordsInDocument));
        }
    }
}