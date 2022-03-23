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

	static DatabaseCatalog dbCatalogue;
	
    public static void main(String[] args) {

        if (args.length != 3) {
            System.err.println("Usage: Minibase database_dir input_file output_file");
            return;
        }

        String databaseDir = args[0];
        String inputFile = args[1];
        String outputFile = args[2];
//    	String queryName="query4";
//        String databaseDir="C:\\Users\\11791\\Desktop\\ADBS CW\\Minibase\\data\\evaluation\\db";
//        String inputFile="C:\\Users\\11791\\Desktop\\ADBS CW\\Minibase\\data\\evaluation\\input\\"+queryName+".txt";
//        String outputFile="C:\\Users\\11791\\Desktop\\ADBS CW\\Minibase\\data\\evaluation\\output\\"+queryName+".csv";
            
        
        evaluateCQ(databaseDir, inputFile, outputFile);
        
    }
    
 
    
    /**
     *  five steps to evaluate CQ
     *  1. process CQ
     *  2. build query plan
     *  3. multiple run root operator, and save the result in DatabaseCatalog
     *  4. run group By operator
     *  5. save tuple list to file
     * @param databaseDir
     * @param inputFile
     * @param outputFile
     */
    public static void evaluateCQ(String databaseDir, String inputFile, String outputFile) {
    	//process atom
    	RelationalAtom head = null;
    	List<Atom> body;
    	//divided body Atom to relational atom and comparison atom
    	List<RelationalAtom> relationBody=new ArrayList<RelationalAtom>();
    	List<ComparisonAtom> comparisonBody=new ArrayList<ComparisonAtom>();
    	try {
            Query query = QueryParser.parse(Paths.get(inputFile));
            head = query.getHead();
//            System.out.println("Head: " + head);
            body = query.getBody();
//            System.out.println("Body: " + body);
            System.out.println("Entire query: " + query);
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
    	
    	//read database schema
        dbCatalogue=new DatabaseCatalog(databaseDir);	
        
        QueryPlan queryPlan=new QueryPlan(head,relationBody,comparisonBody,dbCatalogue);
        Operator operator = queryPlan.getOperator();
        Tuple tuple=operator.getNextTuple();
        while(tuple!=null) {
    		if(!tuple.getTableName().equals("NonVaild")) {
    			//System.out.println(tuple.getValue());
    			dbCatalogue.addTupleList(tuple);
			}
        	tuple=operator.getNextTuple();
        }

    	if(dbCatalogue.getTupleList().size()!=0) {
    		//delete Duplicate tuple and execute SUM and AVG
        	GroupBy groupBy=new GroupBy(head,dbCatalogue);
        	//write to csv file
    		writeToFile(outputFile);
    	}else {
    		System.out.println("No result");
    	}
    	
    }
    
    /**
     * Using fileWriter to write tuple list to csv file
     * @param outputFile
     */
    public static void writeToFile(String outputFile) {
//    	for (int i =0 ;i <dbCatalogue.getTupleList().size();i++) {
//			System.out.println(dbCatalogue.getTupleList().get(i).getValue());
//		}
    	//Create output file dictionary
    	File csvFile = new File(outputFile);
    	if(!csvFile.getParentFile().exists()) {
    		csvFile.getParentFile().mkdirs();
    	}
    	//write to file
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
	        System.out.println("Succesful Evaluation");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("write fail");
		}
    }
    
}
