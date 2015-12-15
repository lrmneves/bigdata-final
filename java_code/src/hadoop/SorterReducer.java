package hadoop;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.Mutation;
import org.apache.cassandra.utils.ByteBufferUtil;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.log4j.Logger;
 

public class SorterReducer extends Reducer<Text, Text, ByteBuffer, List<Mutation>> {
	Logger logger = Logger.getLogger(SorterReducer.class);
	
	 protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
	        String [] keyval;
	        for (Text val : values) {
	        	keyval = val.toString().split("#");
	        	
	    		List<Mutation> v = getMutationList(keyval);
	    		Text k = new Text(keyval[0].replace("process_", "").replace("_", "").replace(".txt", ""));
	    		
	        	context.write(ByteBufferUtil.bytes(String.valueOf(k.toString().hashCode())), v);
	        }
	        
	    }
	 

	 private static List<Mutation> getMutationList(String [] keyval)
     {	  
		 List<Mutation> list = new ArrayList<>();
		 double tfidf = Double.parseDouble(keyval[1]);
		 keyval = keyval[0].split("@");
		 Text document = new Text(keyval[1].replace("process_", "").replace("_", "").replace(".txt", ""));
		 Text word = new Text(keyval[0]);
		 
		 Text col1 = new Text("tfidf");
		 Text col2 = new Text("document");
		 Text col3 = new Text("word");
		 org.apache.cassandra.thrift.Column c1 = new org.apache.cassandra.thrift.Column();
		 org.apache.cassandra.thrift.Column c2 = new org.apache.cassandra.thrift.Column();
		 org.apache.cassandra.thrift.Column c3 = new org.apache.cassandra.thrift.Column();
		
		 long timestamp = System.currentTimeMillis();
         c1.setName(Arrays.copyOf(col1.getBytes(), col1.getLength()));
         c1.setValue(ByteBufferUtil.bytes(tfidf));
         c1.setTimestamp(timestamp);
         Mutation m = new Mutation();
         
         m.setColumn_or_supercolumn(new ColumnOrSuperColumn());
         m.column_or_supercolumn.setColumn(c1);
         list.add(m);
         
         
         c2.setName(Arrays.copyOf(col2.getBytes(), col2.getLength()));
         c2.setValue(Arrays.copyOf(document.getBytes(), document.getLength()));
         c2.setTimestamp(timestamp);
         m = new Mutation();
         
         m.setColumn_or_supercolumn(new ColumnOrSuperColumn());
         m.column_or_supercolumn.setColumn(c2);
         list.add(m);
         
         c3.setName(Arrays.copyOf(col3.getBytes(), col3.getLength()));
         c3.setValue(Arrays.copyOf(word.getBytes(), word.getLength()));
         c3.setTimestamp(timestamp);
         m = new Mutation();
         
         m.setColumn_or_supercolumn(new ColumnOrSuperColumn());
         m.column_or_supercolumn.setColumn(c3);
         list.add(m);
         return list;
     }
}