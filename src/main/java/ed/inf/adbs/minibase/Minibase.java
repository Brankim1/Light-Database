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
	static DatabaseCatalog dbCata;
	
    public static void main(String[] args) {

//        if (args.length != 3) {
//            System.err.println("Usage: Minibase database_dir input_file output_file");
//            return;
//        }

//        String databaseDir = args[0];
//        String inputFile = args[1];
//        String outputFile = args[2];
    	String queryName="query9";
        String databaseDir="C:\\Users\\11791\\Desktop\\ADBS CW\\Minibase\\data\\evaluation\\db";
        String inputFile="C:\\Users\\11791\\Desktop\\ADBS CW\\Minibase\\data\\evaluation\\input\\"+queryName+".txt";
        String outputFile="C:\\Users\\11791\\Desktop\\ADBS CW\\Minibase\\data\\evaluation\\output\\"+queryName+".csv";
        dbCatalogType = new HashMap<String, List<String>>();
        
        readDd(databaseDir);	
        
        evaluateCQ(databaseDir, inputFile, outputFile);
        
    }
    
    public static void readDd(String databaseDir){
    	dbCata=new DatabaseCatalog(databaseDir);
    	dbCatalogType=dbCata.dbCatalogType;
    	
    }
    
    /**
     * Example method for getting started with the parser.
     * Reads CQ from a file and prints it to screen, then extracts Head and Body
     * from the query and prints them to screen.
     */
    public static void evaluateCQ(String databaseDir, String inputFile, String outputFile) {
        // TODO: add your implementation
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
    	
//    	用在join里面
//    	for(RelationalAtom i :relationBody) {
//    		ScanOperator scanOperator=new ScanOperator(i,dbCata);
//			int num=0;
//			while(num<scanOperator.tupleList.size()) {
//				scanOperator.getNextTuple();
//				num++;
//			}
//    	}

    	for(RelationalAtom relaAtom:relationBody) {
    		ScanOperator scanOperator=new ScanOperator(relaAtom,dbCata);
    		scanOperator.dump();
    	}
    	
    	
    	
    	
    	
//    	for(RelationalAtom relaAtom:relationBody) {
//    		JoinOperator joinOperator=new JoinOperator(relaAtom,dbCata);
//    		joinOperator.getNextTuple();
//    	}
//    	
//		for(int i =0;i<comparisonBody.size();i++) {
//			SelectOperator selectOperator=new SelectOperator(comparisonBody.get(i),dbCata);
//			if(selectOperator.condition) {
//				selectOperator.dump();
//			}
//		}
		
		
		
		
//		ProjectOperator projectOperator=new ProjectOperator(head, dbCata);
//		projectOperator.dump();

		writeToFile(outputFile);
		
		
    }
    public static void writeToFile(String outputFile) {
    	
    	for (int i =0 ;i <dbCata.getTupleList().size();i++) {
			System.out.println(dbCata.getTupleList().get(i).getValue());
		}
    	
    	File csvFile = new File(outputFile);
    	//creat output file
    	if(!csvFile.getParentFile().exists()) {
    		csvFile.getParentFile().mkdirs();
    	}
    	
    	
        FileWriter fileWriter;
		try {
			fileWriter = new FileWriter(csvFile);
			for (int j =0;j<dbCata.getTupleList().size();j++) {
	            StringBuilder line = new StringBuilder();
	            for (int i = 0; i < dbCata.getTupleList().get(j).getValue().size(); i++) {
	                
	                line.append(dbCata.getTupleList().get(j).getValue().get(i));
	                
	                if (i != dbCata.getTupleList().get(j).getValue().size() - 1) {
	                    line.append(", ");
	                }
	            }
	            if(j<dbCata.getTupleList().size()-1) {
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
