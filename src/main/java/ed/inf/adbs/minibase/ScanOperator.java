/**
 * 
 */
package ed.inf.adbs.minibase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import ed.inf.adbs.minibase.base.Atom;
import ed.inf.adbs.minibase.base.Query;
import ed.inf.adbs.minibase.base.RelationalAtom;
import ed.inf.adbs.minibase.base.Term;
import ed.inf.adbs.minibase.parser.QueryParser;

/**
 * @author Pengcheng Jin
 *
 */
public class ScanOperator extends Operator{
	Tuple tuple;

	String tableName;
	List<String> columnName;
	List<String> columnType;
	List<String> value;
	RelationalAtom head;
	List<Atom> body;

	BufferedReader bufferTem;
	String stringTem;
	public ScanOperator(RelationalAtom atom, DatabaseCatalog dbCatalogue) {
		this.tableName=atom.getName();
		//build column name and type to tuple list in databaseCatalog.
		columnName=new ArrayList<String>();
		columnType=new ArrayList<String>();
		for(Term i : atom.getTerms()) {
			columnName.add(i.toString().trim());
		}
		columnType=dbCatalogue.dbCatalogType.get(tableName.toString());
		
		File dbFile=new File(dbCatalogue.databaseDir+File.separator+"files"+File.separator+tableName+".csv");
		try {
	    	bufferTem=new BufferedReader(new FileReader(dbFile));
        }catch(Exception e) {
        	System.err.println("Database Catalogue Load Fail");
            e.printStackTrace();
        }
	}

	@Override
	public void getNextTuple() {
		// TODO Auto-generated method stub
		//System.out.println(tupleList.get(num).getTableName());
            String[] cataArr=stringTem.split(",");
            value=new ArrayList<String>();
            for (String i : cataArr) {
            	value.add(i.trim());
            }
            
            tuple= new Tuple(tableName,columnName,columnType,value);
            
		}
		
	

	@Override
	public void reset() {
		// TODO Auto-generated method stub
	
	}

	@Override
	public void dump() {
		// TODO Auto-generated method stub
		try {
			while((stringTem=bufferTem.readLine())!=null){
				getNextTuple();
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
}