/**
 * 
 */
package ed.inf.adbs.minibase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import ed.inf.adbs.minibase.base.RelationalAtom;
import ed.inf.adbs.minibase.base.Term;

/**
 * It could Execute SUM & AVG and Delete Duplicate tuple
 * @author Pengcheng Jin
 * 
 */
public class GroupBy{
	//buffer all tuple
	List<Tuple> tupleList;
	//DatabaseCatalog instance
	DatabaseCatalog dbCatalogue;
	//head atom
	RelationalAtom atom;

	HashMap<String, List<Integer>> map;
	
	/**
	 * Initialization instance 
	 * @param atom
	 * @param dbCatalogue
	 */
	public GroupBy(RelationalAtom atom,DatabaseCatalog dbCatalogue) {
		this.dbCatalogue=dbCatalogue;
		this.atom=atom;
		List<Tuple> newTupleList=new ArrayList<Tuple>();;
		map = new HashMap<String, List<Integer>>();
		tupleList=new ArrayList<Tuple>();
		tupleList.addAll(dbCatalogue.getTupleList());
		//Execute SUM & AVG, Delete Duplicate tuple
						
		if(atom.getTerms().get(atom.getTerms().size()-1).toString().contains("SUM")||atom.getTerms().get(atom.getTerms().size()-1).toString().contains("AVG")) {
			int sum=0;
			for(int j=0;j<tupleList.size();j++) {
				if(map.get(getKey(tupleList.get(j)))==null) {
					List<Integer> initInt=new ArrayList<Integer>();
					initInt.add(0);
					initInt.add(0);
					map.put(getKey(tupleList.get(j)), initInt);
				}
				List<Integer> intList=new ArrayList<Integer>();
				intList=map.get(getKey(tupleList.get(j)));
				intList.set(0, intList.get(0)+Integer.valueOf(tupleList.get(j).getValue().get(atom.getTerms().size()-1)));
				intList.set(1, intList.get(1)+1);
				map.put(getKey(tupleList.get(j)), intList);
				
			}
			if(atom.getTerms().get(atom.getTerms().size()-1).toString().contains("AVG")) {
				//average
				for (String key : map.keySet()) {
					List<Integer> intList=new ArrayList<Integer>();
					intList=map.get(key);
					intList.set(0, intList.get(0)/intList.get(1));
				}
			}
					
			for (String key : map.keySet()) {
				String[] cataArr=key.split(",");
				List<String> newString=new ArrayList<String>();
				if(cataArr.length==0) {
					for(int i=0;i<cataArr.length;i++) {
						newString.add(cataArr[i]);
					}
				}				
				newString.add(map.get(key).get(0).toString());
				Tuple tuple=new Tuple(tupleList.get(0).getTableName(),tupleList.get(0).getColumnType(),tupleList.get(0).getColumnName(),newString);
				newTupleList.add(tuple);
			}
		}else {
			//Delete Duplicate tuple
			int numk=0;
			int numm=0;
			while(numk<tupleList.size()) {
				numm=0;
				while(numm<tupleList.size()) {
					if(numm>numk) {
						if(tupleList.get(numk).getValue().toString().equals(tupleList.get(numm).getValue().toString())) {
							tupleList.remove(numm);
							numm--;
						}
					}
					numm++;
				}
				numk++;
			}
			newTupleList=tupleList;
		}
		
		
		//store the new tuple to dbCatalogue
		dbCatalogue.setTupleList(newTupleList);
	}
	public String getKey(Tuple tuple) {
		String key="";
		
		for(int i =0;i<tuple.getValue().size()-1;i++) {
			key=key+tuple.getValue().get(i);
			if(i<tuple.getValue().size()-2) {
						key=key+",";	
			}
		}
		return key;
	}
}
