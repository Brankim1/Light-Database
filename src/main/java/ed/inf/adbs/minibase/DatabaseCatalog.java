/**
 * 
 */
package ed.inf.adbs.minibase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * This class could read the schema.txt file and store the tuple list.
 * @author Pengcheng Jin
 * 
 */
public class DatabaseCatalog {
	//the schema.txt is saved in this variable
	HashMap<String, List<String>> dbCatalogType;
	//database dictionary
	String databaseDir;
	//save tuple list in it.
	List<Tuple> tupleList;
	/**
	 * read the schema.txt file
	 * @param databaseDir
	 */
	public DatabaseCatalog(String databaseDir) {
		this.databaseDir=databaseDir;
		this.tupleList=new ArrayList<Tuple>();
		//find schema address
		File cataFile=new File(databaseDir+File.separator+"schema.txt");
    	
		dbCatalogType=new HashMap<String, List<String>>();
		//read schema file and save to dbCatalogType
    	try {
	    	BufferedReader bufferTem=new BufferedReader(new FileReader(cataFile));
	        String stringTem;
	        while((stringTem=bufferTem.readLine())!=null){
	            String[] cataArr=stringTem.split("\\s+");
	            List<String> dbCata = new ArrayList<String>(Arrays.asList(cataArr));
	            dbCata.remove(0);
	            dbCatalogType.put(cataArr[0],dbCata);
			}
        }catch(Exception e) {
        	System.err.println("Database Catalogue Load Fail");
            e.printStackTrace();
        }
	}
	/**
	 * It could add tuple to tuple list
	 * @param tuple
	 */
	public void addTupleList(Tuple tuple){
		tupleList.add(tuple);
	}
	
	/**
	 * It could replace tuple list
	 * @param tupleList
	 */
	public void setTupleList(List<Tuple> tupleList){
		this.tupleList=tupleList;
	}
	
	/**
	 * It could get tuple list
	 * @return
	 */
	public List<Tuple> getTupleList(){
		return tupleList;
	}
	
	
	
}
