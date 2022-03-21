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
 * ScanOperator that could scan the table tuple
 * @author Pengcheng Jin
 *
 */
public class ScanOperator extends Operator{
	//tuple
	Tuple tuple;
	//tuple Components(such as R)
	String tableName;
	//tuple Components(such as x)
	List<String> columnName;
	//tuple Components(such as int)
	List<String> columnType;
	//tuple Components(such as 9)
	List<String> value;
	//read file buffer
	BufferedReader bufferTem;
	String stringTem;
	//DatabaseCatalog instance
	DatabaseCatalog dbCatalogue;
	 /**
	  * initialize tableName, columnName, columnType and bufferTem
	  * @param atom
	  * @param dbCatalogue
	  */
	public ScanOperator(RelationalAtom atom, DatabaseCatalog dbCatalogue) {
		this.tableName=atom.getName();
		this.dbCatalogue=dbCatalogue;
		//build column name and type to tuple list in databaseCatalog.
		columnName=new ArrayList<String>();
		columnType=new ArrayList<String>();
		for(Term i : atom.getTerms()) {
			columnName.add(i.toString().trim());
		}
		columnType=dbCatalogue.dbCatalogType.get(tableName.toString());
		//set up dictionary to read file
		File dbFile=new File(dbCatalogue.databaseDir+File.separator+"files"+File.separator+tableName+".csv");
		try {
	    	bufferTem=new BufferedReader(new FileReader(dbFile));
        }catch(Exception e) {
        	System.err.println("Database Catalogue Load Fail");
            e.printStackTrace();
        }
	}
	/**
	 * read tuple from csv file
	 * @return tuple
	 */
	@Override
	public Tuple getNextTuple() {
		// TODO Auto-generated method stub
		//read database to get tuple
		try {
			stringTem=bufferTem.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(stringTem==null) {
			return null;
		}else {
			boolean quotation=false;
			//handle comma in string,such as tuple is (a,'pengcheng,jin,edinburgh',4)
			//change comma than in the string to `, then split by comma
			for(int i =0;i<stringTem.length();i++) {
				String singleStr=Character.toString(stringTem.charAt(i));
				if(quotation==true) {
					if(singleStr.equals(",")) {
						char[] temChars = stringTem.toCharArray();
						temChars[i] = '`';
						stringTem = String.valueOf(temChars);
					}
				}
				if(singleStr.equals("'")) {
					if(quotation==false) {
						quotation=true;
					}else {
						quotation=false;
					}
					
				}
				
			}
			
			String[] cataArr=stringTem.split(",");
            value=new ArrayList<String>();
            for (int i=0;i<cataArr.length;i++) {
            	String finalString=cataArr[i];
            	for(int j=0;j<finalString.length();j++) {
            		if(Character.toString(finalString.charAt(j)).equals("`")) {
						char[] temChars = finalString.toCharArray();
						temChars[j] = ',';
						finalString = String.valueOf(temChars);
					}
            	}
            	value.add(finalString.trim());
            }
            
            tuple= new Tuple(tableName,columnName,columnType,value);
            return tuple;
		}
	}
		
	
	/**
	 * reset bufferTem to restart
	 */
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		File dbFile=new File(dbCatalogue.databaseDir+File.separator+"files"+File.separator+tableName+".csv");
		try {
	    	bufferTem=new BufferedReader(new FileReader(dbFile));
        }catch(Exception e) {
        	System.err.println("Database Catalogue Load Fail");
            e.printStackTrace();
        }
	}
	/**
	 * multiple run getNextTuple()
	 */
	@Override
	public void dump() {
		// TODO Auto-generated method stub
		Tuple tupleDump=getNextTuple();
		while(tupleDump!=null) {
			tupleDump=getNextTuple();
		}
		
	}

	
}
