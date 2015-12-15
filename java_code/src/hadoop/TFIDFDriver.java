package hadoop;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import org.apache.cassandra.hadoop.ColumnFamilyOutputFormat;
import org.apache.cassandra.hadoop.ConfigHelper;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cassandra.CassandraCluster;



public class TFIDFDriver {
	private static final Logger logger = LoggerFactory.getLogger(TFIDFDriver.class);

	public static String returnLastFolder(String fullFolder){
		String [] list = fullFolder.split("/");
		return list[list.length-1];
	}
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		BasicConfigurator.configure();

		int numRepetitions = 4; 
		long documents = 0;
		Path input = new Path("input");
		Configuration conf = new Configuration();
		FileSystem fs = input.getFileSystem(conf);
		CassandraCluster.connect();
		CassandraCluster.createDatabase();

		for(FileStatus f : fs.listStatus(input)){
			if (f.isDirectory()){
				String dirInput = f.getPath().toString().replace("file:", "");
				
				for(int i = 3; i < numRepetitions; i++) {
					conf = new Configuration();
					Job job = Job.getInstance(conf, "tfidfPhase" + i);
					if (i == 0){      
						System.out.println("Step 1: Frequency Count");

						job.setJarByClass(TFIDFDriver.class);
						job.setMapperClass(WordFrequencyMapper.class);
						job.setReducerClass(WordFrequencyReducer.class);
						job.setCombinerClass(WordFrequencyReducer.class);
						job.setOutputKeyClass(Text.class);
				        job.setOutputValueClass(IntWritable.class);
				 

					}
					else if (i == 1){
						System.out.println("Step 2: Document WordCount");
						job.setJarByClass(TFIDFDriver.class);
						job.setMapperClass(DocWordcountMapper.class);
						job.setReducerClass(DocWordcountReducer.class);
						job.setOutputKeyClass(Text.class);
				        job.setOutputValueClass(Text.class);
					}
					else if (i == 2){
						System.out.println("Step 3: TF-IDF");
						job.setJarByClass(TFIDFDriver.class);
						job.setMapperClass(TFIDFCalculatorMapper.class);
						job.setReducerClass(TFIDFCalculatorReducer.class);

						 job.setOutputKeyClass(Text.class);
					     job.setOutputValueClass(Text.class);
					}
					else if (i == 3){
						System.out.println("Step 4: TF-IDF Sort");


						job.setJarByClass(TFIDFDriver.class);


						job.setMapperClass(SorterMapper.class);
						job.setReducerClass(SorterReducer.class);
						
						job.setMapOutputKeyClass(Text.class);
						job.setMapOutputValueClass(Text.class);

						job.setOutputKeyClass(ByteBuffer.class);
						job.setOutputValueClass(List.class);


					}
					//"Deprecated". This would be my first attempt of handling the documents, before
					//using cassandra. It is only here for evaluation, it would have been removed on
					//production code
//					else if (i == 4){
//						System.out.println("Step 5: InverseDocument");
//
//
//						job.setJarByClass(TFIDFDriver.class);
//
//
//						job.setMapperClass(InverseMapper.class);
//						job.setReducerClass(InverseReducer.class);
//
//						 job.setOutputKeyClass(Text.class);
//					     job.setOutputValueClass(Text.class);
//					}
					
					String inputPath = i == 0?dirInput: "results/"+ returnLastFolder(dirInput) + (i-1);
					String outputPath = "results/"+returnLastFolder(dirInput) + i;
					FileInputFormat.setInputDirRecursive(job, true);
					FileInputFormat.addInputPath(job, new Path(inputPath));
					if (i == 3){
						job.setOutputFormatClass(ColumnFamilyOutputFormat.class);
						ConfigHelper.setOutputColumnFamily(job.getConfiguration(),
								CassandraCluster.getKeyspace(), CassandraCluster.getTable());
						ConfigHelper.setOutputPartitioner(job.getConfiguration(), "Murmur3Partitioner");
						ConfigHelper.setOutputInitialAddress(job.getConfiguration(), "localhost");

					}else{
						FileOutputFormat.setOutputPath(job, new Path(outputPath));
					}
				    if(i != 0) {
				    	job.setJobName(""+documents);
				    }
					
				    try { 
						if(!job.waitForCompletion(true)) throw new Exception("Job didn't complete"); //run the job
					} catch(Exception e) {
						System.err.println("ERROR IN JOB: " + e);
						e.printStackTrace();
						return;
					}
				    if(i == 0) {
				    	Counters jobCntrs = job.getCounters();
				    	documents = jobCntrs.findCounter(DocumentCounter.DOCUMENTS).getValue();
				    	jobCntrs.findCounter(DocumentCounter.DOCUMENTS).setValue(0);
				    }
				}
			}
			}
		CassandraCluster.close();
		}
		
}


