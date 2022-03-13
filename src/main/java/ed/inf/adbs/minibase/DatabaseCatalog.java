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
 * @author 11791
 *
 */
public class DatabaseCatalog {
	
	HashMap<String, List<String>> dbCatalogType;
	HashMap<String, List<String>> dbCatalog;
	String databaseDir;

	List<Tuple> tupleList;
	
	public DatabaseCatalog(String databaseDir) {
		this.databaseDir=databaseDir;
		this.tupleList=new ArrayList<Tuple>();
		File cataFile=new File(databaseDir+File.separator+"schema.txt");
    	
		dbCatalogType=new HashMap<String, List<String>>();
		dbCatalog=new HashMap<String, List<String>>();
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
	
	public void addTupleList(Tuple tuple){
		tupleList.add(tuple);
	}
	public void setTupleList(List<Tuple> tupleList){
		this.tupleList=tupleList;
	}
	public List<Tuple> getTupleList(){
		return tupleList;
	}
	
	
	
}
