package ed.inf.adbs.minibase;

import ed.inf.adbs.minibase.base.Atom;
import ed.inf.adbs.minibase.base.ComparisonAtom;
import ed.inf.adbs.minibase.base.Query;
import ed.inf.adbs.minibase.base.RelationalAtom;
import ed.inf.adbs.minibase.parser.QueryParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * In-memory database system
 *
 */
public class Minibase {
	static HashMap<String, List<String>> dbCatalogType;
	static DatabaseCatalog dbCatalogue;
	
    public static void main(String[] args) {

//        if (args.length != 3) {
//            System.err.println("Usage: Minibase database_dir input_file output_file");
//            return;
//        }

//        String databaseDir = args[0];
//        String inputFile = args[1];
//        String outputFile = args[2];
    	String queryName="query6";
        String databaseDir="C:\\Users\\11791\\Desktop\\ADBS CW\\Database-CQ-Min-Eva\\data\\evaluation\\db";
        String inputFile="C:\\Users\\11791\\Desktop\\ADBS CW\\Database-CQ-Min-Eva\\data\\evaluation\\input\\"+queryName+".txt";
        String outputFile="C:\\Users\\11791\\Desktop\\ADBS CW\\Database-CQ-Min-Eva\\data\\evaluation\\output\\"+queryName+".csv";
        
        dbCatalogType = new HashMap<String, List<String>>();
        
        readDd(databaseDir);	
        
        evaluateCQ(databaseDir, inputFile, outputFile);
        
    }
    
    public static void readDd(String databaseDir){
    	dbCatalogue=new DatabaseCatalog(databaseDir);
    	dbCatalogType=dbCatalogue.dbCatalogType;
    	
    }
    
    /**
     * Example method for getting started with the parser.
     * Reads CQ from a file and prints it to screen, then extracts Head and Body
     * from the query and prints them to screen.
     */
    public static void evaluateCQ(String databaseDir, String inputFile, String outputFile) {
        // TODO: add your implementation
    	//process atom
    	RelationalAtom head = null;
    	List<Atom> body;
    	List<RelationalAtom> relationBody=new ArrayList<RelationalAtom>();
    	List<ComparisonAtom> comparisonBody=new ArrayList<ComparisonAtom>();
    	try {
            Query query = QueryParser.parse(Paths.get(inputFile));
            System.out.println("Entire query: " + query);
            head = query.getHead();
            System.out.println("Head: " + head);
            body = query.getBody();
            System.out.println("Body: " + body);
            //Separate the relational atom and comparison atom
            for (Atom i:body) {
            	try {
            		relationBody.add((RelationalAtom)i);
            	}catch(Exception e) {
            		comparisonBody.add((ComparisonAtom)i);
            	}            	
            }
        }
        catch (Exception e)
        {
            System.err.println("Exception occurred during parsing");
            e.printStackTrace();
        }
    	
    	Tuple tuple;
    	
    	JoinOperator joinOperator=new JoinOperator(relationBody,dbCatalogue);
    	tuple=joinOperator.getNextTuple();

    	while(tuple!=null) {
    		
//    		SelectOperator selectOperator=new SelectOperator(comparisonBody,tuple);
//    		tuple=selectOperator.getNextTuple();
//    		
//    		if(tuple!=null) {
//    			ProjectOperator projectOperator=new ProjectOperator(head,tuple);
//    			tuple=projectOperator.getNextTuple();
//    			dbCatalogue.addTupleList(tuple);
//    		}
    		
    		System.out.println(tuple.getValue());
    		tuple=joinOperator.getNextTuple();
    	}
    	
    	
    	GroupByOperator groupByOperator=new GroupByOperator(head,dbCatalogue);
		writeToFile(outputFile);
		
		
    }
    public static void writeToFile(String outputFile) {
    	
//    	for (int i =0 ;i <dbCatalogue.getTupleList().size();i++) {
//			System.out.println(dbCatalogue.getTupleList().get(i).getValue());
//		}
    	
    	File csvFile = new File(outputFile);
    	//creat output file
    	if(!csvFile.getParentFile().exists()) {
    		csvFile.getParentFile().mkdirs();
    	}
    	
    	
        FileWriter fileWriter;
		try {
			fileWriter = new FileWriter(csvFile);
			for (int j =0;j<dbCatalogue.getTupleList().size();j++) {
	            StringBuilder line = new StringBuilder();
	            for (int i = 0; i < dbCatalogue.getTupleList().get(j).getValue().size(); i++) {
	                
	                line.append(dbCatalogue.getTupleList().get(j).getValue().get(i));
	                
	                if (i != dbCatalogue.getTupleList().get(j).getValue().size() - 1) {
	                    line.append(", ");
	                }
	            }
	            if(j<dbCatalogue.getTupleList().size()-1) {
	            	line.append("\n");
	            }
	            
	            fileWriter.write(line.toString());
	        }
	        fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("write fail");
		}
        
    }
}
