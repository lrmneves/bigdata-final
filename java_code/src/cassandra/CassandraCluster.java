package cassandra;
import static java.lang.System.out;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.QueryBuilder;



public class CassandraCluster
{
	private static Cluster cluster = null;
	private final static String keyspace = "big_data_final";
	private static Session session = null;
	private static Host host = null;
	private final static String table = "doc_word_map";
	public static void connect(final String node, final int port)
	{  if(cluster != null) return;
	cluster = Cluster.builder().addContactPoint(node).withPort(port).build();
	final Metadata metadata = cluster.getMetadata();
	out.printf("Connected to cluster: %s\n", metadata.getClusterName());
	for (final Host host : metadata.getAllHosts())
	{	

		out.printf("Datacenter: %s; Host: %s; Rack: %s\n",
				host.getDatacenter(), host.getAddress(), host.getRack());
		CassandraCluster.host = host;
	}
	session = cluster.connect();
	}

	public static void connect(){
		connect("localhost",9042); 
	}

	public static Session getSession()
	{
		return session;
	}
	public static void createKeyspace(){
		String query = "DROP KEYSPACE " + keyspace+ " ; ";
		try{
			session.execute(query);
		}catch(Exception e){}
		query = "CREATE KEYSPACE " + keyspace+ " WITH replication "
				+ "= {'class':'SimpleStrategy', 'replication_factor':1}; ";

		try{
			session.execute(query);
		}catch(Exception e){}

		session.execute("USE "+keyspace);
	}
	/**
	 * Creates index on word and documents so we can query for the words on a document and 
	 */
	public static void createDocumentTable(){
		
		String query = "CREATE TABLE "+table+" (id text PRIMARY KEY , tfidf double ,document text, word text) with COMPACT STORAGE ;";
		try{
			session.execute(query);
		}catch(Exception e){
			e.printStackTrace();
		}
		String index = "CREATE INDEX ON "+ keyspace +"."+table+" (word);";
		try{
			session.execute(index);
		}catch(Exception e){
			e.printStackTrace();
		}
		index = "CREATE INDEX ON "+ keyspace +"."+table+" (document);";
		try{
			session.execute(index);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void resetDocumentTable(){
		String query = "DROP TABLE "+table+";";
		try{
			session.execute(query);
		}catch(Exception e){
			e.printStackTrace();
		}
		createDocumentTable();
	}
	
	public static void  createDatabase() throws IOException{
		createKeyspace();
		createDocumentTable();
		
	}
	public static String getTable(){
		return table;
	}
	public static String getAddress(){
		return host.getAddress().toString();
	}
	public static void close()
	{
		cluster.close();
		cluster = null;
	}
	public static void startKeyspace() {
		if(keyspace == null){
			session.execute("USE big_data_final;");
			
			return;
		}
		session.execute("USE "+keyspace);

	}
	public static String getKeyspace() {
		// TODO Auto-generated method stub
		return keyspace;
	}
}