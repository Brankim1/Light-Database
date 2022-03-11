package ed.inf.adbs.minibase;

import ed.inf.adbs.minibase.base.Atom;
import ed.inf.adbs.minibase.base.Term;
import ed.inf.adbs.minibase.base.Variable;
import ed.inf.adbs.minibase.base.Query;
import ed.inf.adbs.minibase.base.RelationalAtom;
import ed.inf.adbs.minibase.parser.QueryParser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Minimization of conjunctive queries
 *
 */
public class CQMinimizer {

    public static void main(String[] args) {

//        if (args.length != 2) {
//            System.err.println("Usage: CQMinimizer input_file output_file");
//            return;
//        }

        //String inputFile = args[0];
        //String outputFile = args[1];
    	String queryName="query4";
        String inputFile = "C:\\Users\\11791\\Desktop\\ADBS CW\\Minibase\\data\\minimization\\input\\"+queryName+".txt";
        String outputFile = "C:\\Users\\11791\\Desktop\\ADBS CW\\Minibase\\data\\minimization\\output\\"+queryName+".txt";
        minimizeCQ(inputFile, outputFile);
        
        //parsingExample(inputFile);
    }

    /**
     * CQ minimization procedure
     *
     * Assume the body of the query from inputFile has no comparison atoms
     * but could potentially have constants in its relational atoms.
     *
     */
    public static void minimizeCQ(String inputFile, String outputFile) {
        // TODO: add your implementation
    	try {
            Query query = QueryParser.parse(Paths.get(inputFile));

            System.out.println("Entire query: " + query);
            RelationalAtom head = query.getHead();
            System.out.println("Head: " + head);
            
            List<Atom> body = query.getBody();
            System.out.println("Body: " + body);
            
            // change body(atom list) to relational atom list
            List<RelationalAtom> atomBody = new ArrayList<RelationalAtom>();
            for(int i = 0;i<body.size(); i++) {
            	atomBody.add((RelationalAtom)body.get(i));
            }
            
            //main process of Minimize CQ
            //Loop each body atom, assume delete one atom, then judge whether the original CQ and this one are homomorphism
            //if homomorphism, delete this atom; otherwise, keep it.
            int i = 0;
            int index=0;
            int k=atomBody.size();
            while(i<k) {
            	List<RelationalAtom> atomBodyTem=new ArrayList<>();
            	atomBodyTem.addAll(atomBody);
            	atomBodyTem.remove(index);
            	if(exist_homomo(head,atomBody.get(index),atomBody,atomBodyTem)) {
            		atomBody.remove(index);
            	}else {
            		index++;
            	}
            	i++;
            }
            
            System.out.println("minimizeCQ is "+head+" :- "+atomBody.toString().substring(1,atomBody.toString().length()-1));	
            
            //write to the file
        	File file = new File(outputFile);
        	//creat output file
        	if(!file.getParentFile().exists()) {
        		file.getParentFile().mkdirs();
        	}
        	FileWriter fileWriter;
     		try {
     			fileWriter = new FileWriter(file);
	            StringBuilder line = new StringBuilder();
	            
	                
	            line.append(head+" :- "+atomBody.toString().substring(1,atomBody.toString().length()-1));
	                
	              
	            fileWriter.write(line.toString());
     	        
     	        fileWriter.close();
     		} catch (IOException e) {
     			// TODO Auto-generated catch block
     			e.printStackTrace();
     			System.out.println("write fail");
     		}
        	
            
        }
        catch (Exception e)
        {
            System.err.println("Exception occurred during parsing");
            e.printStackTrace();
        }
    	
    	
    }
    
/*
 * homomorphism algorithm: 
 * 1. build the constant and distinguished variables dictionary(hash map)
 * 2. build the variable dictionary by find the deleted atom homomorphism atoms.
 * 3. check the original atom list and remaining atom list homomorphism
 * 
 * */    
    public static boolean exist_homomo(RelationalAtom head,RelationalAtom atom,List<RelationalAtom> originBody,List<RelationalAtom> temBody) {
    	//build constant and distinguished variables dictionary(hash map)
    	Map<String, String> dictionary = new HashMap<String, String>();
    	for(int i=0;i<head.getTerms().size();i++) {
    		dictionary.put(head.getTerms().get(i).toString().trim(), head.getTerms().get(i).toString().trim());
    	}

    	for(int i=0;i<originBody.size();i++) {
    		for(int j=0;j<originBody.get(i).getTerms().size();j++) {
    			if(constant(head,originBody.get(i).getTerms().get(j).toString())) {
    				dictionary.put(originBody.get(i).getTerms().get(j).toString().trim(), originBody.get(i).getTerms().get(j).toString().trim());
    			}
    		}
    	}
    	
    	//find the deleted atom homomorphism atoms. if has homomorphism atom, build the dictionary for the body variable
    	//The homomorphism algorithm: judge the deleted atom terms, loop all of the remaining atom list(temple atom list)
    	//if both deleted atom term and loop atom are same, homomorphism in this term;
    	//if both deleted atom term and loop atom are not constant, homomorphism in this term;
    	//if deleted atom term are constant and loop atom are not constant, homomorphism in this term;
    	//if all of the term in deleted atom have homomorphism, the deleted atom is homomorphism for this atom list.
    	for (int i=0; i<temBody.size();i++) {
    		if(atom.getName().toString().equals(temBody.get(i).getName().toString())&&atom.getTerms().size()==temBody.get(i).getTerms().size()) {
    			int sameVar=0;
    			for (int j=0; j<atom.getTerms().size();j++) {
    				if(atom.getTerms().get(j).toString().equals(temBody.get(i).getTerms().get(j).toString())) {
    					sameVar++;
    					//System.out.println("homomo is 1");	
    				}else {
    					if(!constant(head,atom.getTerms().get(j).toString())&&!constant(head,temBody.get(i).getTerms().get(j).toString())) {
    						sameVar++;    
    						//System.out.println("homomo is 2");	
    					}
    					if(!constant(head,atom.getTerms().get(j).toString())&&constant(head,temBody.get(i).getTerms().get(j).toString())) {
    						sameVar++;    	
    						//System.out.println("homomo is 3");	
    					}
    				}
    			}
    			
    			
    			//if the deleted atom has homomorphism with the remaining atom list, build the deleted atom variable dictionary
    			if(sameVar==atom.getTerms().size()) {
    				for(int j=0;j<atom.getTerms().size();j++) {
    					String term=atom.getTerms().get(j).toString().trim();
    					if(!constant(head,term)) {
    						dictionary.put(term, temBody.get(i).getTerms().get(j).toString().trim());
    					}
    				}
    				
    				//According the exist dictionary, double check whether the original atom list is homomorphism with the remaining atom list.
    				if (Listhomomorphism(dictionary,head,originBody, temBody)) {
    					return true;
    				} 				
    			}
			}
    	}
    	
    	return false;
    }


    public static boolean Listhomomorphism(Map<String, String> dictionary,RelationalAtom head,List<RelationalAtom> originBody, List<RelationalAtom> temBody) {
    	List<RelationalAtom> atomBodyTem2=new ArrayList<>();
    	//replace variable by dictionary
    	for(int i=0;i<originBody.size();i++) {
    		List<Term> term=new ArrayList<>();
    		term.clear();
    		String name=originBody.get(i).getName().toString();
    		for(int j=0;j<originBody.get(i).getTerms().size();j++) {
    			if(dictionary.get(originBody.get(i).getTerms().get(j).toString().replaceAll("\\s",""))!=null) {
    				term.add((Term)new Variable(dictionary.get(originBody.get(i).getTerms().get(j).toString().trim())));
    			}else {
    				term.add((Term)new Variable(originBody.get(i).getTerms().get(j).toString().trim()));
    			}
    		}
    		atomBodyTem2.add((RelationalAtom)new RelationalAtom(name,term));
    	}
    	
    	//let all dictionary value as distinguished variables(I can simple add these to the head)
    	List<Term> term2=new ArrayList<>();
    	term2.addAll(head.getTerms());
    	for (String key: dictionary.keySet()) {
    		term2.add(new Variable(dictionary.get(key)));
    	}
    	RelationalAtom head2=new RelationalAtom(head.getName().toString(),term2);
    	
	
    	//The homomorphism algorithm: loop all original atom list(already replaced by dictionary),assume delete one atom, then loop all of the remaining atom list(temple atom list)
    	//if both deleted atom term and loop atom are same, homomorphism in this term;
    	//if both deleted atom term and loop atom are not constant, homomorphism in this term;
    	//if deleted atom term are constant and loop atom are not constant, homomorphism in this term;
    	//if all of the term in deleted atom have homomorphism, the deleted atom is homomorphism for this atom list;
    	//if all original list atoms have homomorphism with remaining atom list,two list are homomorphism.
    	int numAtom=0;
    	for(int i=0;i<atomBodyTem2.size();i++) {
    		int num=0;
    		for(int j=0;j<temBody.size();j++) {
    			if(atomBodyTem2.get(i).getName().toString().equals(temBody.get(j).getName().toString())&&atomBodyTem2.get(i).getTerms().size()==temBody.get(j).getTerms().size()) {
    				int sameVar=0;
    				for(int k=0;k<atomBodyTem2.get(i).getTerms().size();k++) {
    					if(atomBodyTem2.get(i).getTerms().get(k).toString().equals(temBody.get(j).getTerms().get(k).toString())) {
        					sameVar++;
        					//System.out.println("homomo is 1");	
        				}else {
        					if(!constant(head2,atomBodyTem2.get(i).getTerms().get(k).toString())&&!constant(head2,temBody.get(j).getTerms().get(k).toString())) {
        						sameVar++;    
        						//System.out.println("homomo is 2");	
        					}
        					if(!constant(head2,atomBodyTem2.get(i).getTerms().get(k).toString())&&constant(head2,temBody.get(j).getTerms().get(k).toString())) {
        						sameVar++;    	
        						//System.out.println("homomo is 3");	
        					}
        				}
    				}
    				if(sameVar==atomBodyTem2.get(i).getTerms().size()) {
    					num++;
    					break;
    				}
    			}	
        	}
    		if(num>=1) {
    			numAtom++;
    		}
    	}
    	if(numAtom==atomBodyTem2.size()) {
    		return true;
    	}
    	return false;
    
    }
    /**
     * Judge whether the string are constant or distinguished variables
     * 
     * 
     */
	public static boolean constant(RelationalAtom head, String str) {
		str=str.trim();
		boolean cons=false;
		//check number
		String bigStr;
		try {
			bigStr=new BigDecimal(str).toString();
			cons=true;
		}catch(Exception e) {
			cons= false;
		}
		//check string""
		if (str.startsWith("'")) {
			cons=true;
		}
		//check distinguished
		int k=0;
		for (int i=0;i<head.getTerms().size();i++) {
			if (!str.equals(head.getTerms().get(i).toString().trim())) {
				k++;
			}
		}
			
		if(k!=head.getTerms().size()) {
			cons=true;
		}
		if(cons==true) {
			return true;
		}else {
			return false;
		}
			
	}

}
