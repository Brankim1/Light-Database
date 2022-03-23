package ed.inf.adbs.minibase;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import ed.inf.adbs.minibase.base.ComparisonAtom;
import ed.inf.adbs.minibase.base.RelationalAtom;
/**
 * Using block nested loops join
 * Join multiple tables, all table are stored in the List<ScanOperator>, then invoke getNextTuple() once to return one tuple(Iterative model) 
 * The join algorithm:
 * 1. set a tuple list that store each join table first tuple;
 * 2. use one index to mark tables.
 * 3. let index =0 , then multiple run getNextTuple() in index List<ScanOperator>.(it could ensure it is left join)
 * 4. ScanOperator getNextTuple() will return a tuple, then invoke Select Operator to compare tuple.
 * 5. if selectOperator return a valid tuple, merge the list tuple to a full tuple
 * 6. check the full tuple is valid, if different table column name is same, the value should same. then return the full tuple.
 * 7. if first table return null, set first table reset(), then index+1
 * 8. run the Second table getNextTuple(), then index to zero, multiple run getNextTuple() in first table in List<ScanOperator>.
 * 9. then multiple run it, it could return all join tuple.
 * 
 * @author Pengcheng Jin
 *
 */
public class JoinOperator extends Operator {
	//DatabaseCatalog instance
	DatabaseCatalog dbCatalogue;
	//all relationalAtom
	List<RelationalAtom> atomList;
	//all ComparisonAtom
	List<ComparisonAtom> comparisonBody;
	//to return tuple
	Tuple tuple;
	//save each table
	List<ScanOperator> scanOperatorList;
	//save temporary tuple
	List<Tuple> tupleList;
	//table index
	int runIndex;
	//if first invoke, Initialization temporary tuple
	boolean firstInvoke=true;
	//if tuple not valid, return it
	List<String> NonValidString=new ArrayList<String>();
	
	/**
	 * Join multiple tables
	 * @param atomList
	 * @param dbCatalogue
	 */
	public JoinOperator(List<RelationalAtom> atomList, List<ComparisonAtom> comparisonBody,DatabaseCatalog dbCatalogue) {
		this.dbCatalogue=dbCatalogue;
		this.atomList=new ArrayList<RelationalAtom>();
		this.atomList=atomList;
		this.comparisonBody=new ArrayList<ComparisonAtom>();
		this.comparisonBody=comparisonBody;
		
		scanOperatorList=new ArrayList<ScanOperator>();
		NonValidString.add("NonValidString");
		tupleList=new ArrayList<Tuple>();
		runIndex=0;
		//init scan operator, and add them to list
		for(int i =0;i<atomList.size();i++) {
			ScanOperator scanOperator=new ScanOperator(atomList.get(i),dbCatalogue);
			scanOperatorList.add(scanOperator);
		}	
	}
	/**
	 * main join process, there are two steps:
	 * 1. get join tuple
	 * 2. return valid tuple(same column should have same value)
	 * @return tuple/Non Valid Tuple
	 */
	@Override
	public Tuple getNextTuple() {
		//if there are more than one table, join it
		//if the getNextTuple is the first invoke, init a tuple list that contains the first tuple in each table
		if(firstInvoke==true) {
			firstInvoke=false;
			for(int i =0;i<scanOperatorList.size();i++) {
				Tuple tuple=scanOperatorList.get(i).getNextTuple();
				tupleList.add(tuple);
			}
			//change tuple list to a list
			tuple=ListToTuple(tupleList);

			//check tuple is valid(such as x should same)
			if(tupleValid(tuple)) {
				return tuple;
			}else {
				return new Tuple("NonVaild",NonValidString,NonValidString,NonValidString);
			}				
		}else {
			//if the getNextTuple is not the first invoke, use one index to loop all table, which are explained detail in README.md file
			while(runIndex>=0&&runIndex<scanOperatorList.size()) {
				Tuple temTuple=scanOperatorList.get(runIndex).getNextTuple();
				if(temTuple!=null) {
					//temTuple check such as x=1 
					
					if(compareTuple(temTuple,comparisonBody)==true) {
						tupleList.set(runIndex, temTuple);
						while(runIndex>0) {
							runIndex--;
						}
						tuple=ListToTuple(tupleList);
						
						if(tupleValid(tuple)) {
							return tuple;
						}else {
							return new Tuple("NonVaild",NonValidString,NonValidString,NonValidString);
						}
					}else {
						return new Tuple("NonVaild",NonValidString,NonValidString,NonValidString);
					}
						
				}else {
					scanOperatorList.get(runIndex).reset();
					temTuple=scanOperatorList.get(runIndex).getNextTuple();
					tupleList.set(runIndex, temTuple);
					runIndex++;
				}
			}
			
		}	
		return null;
	}
	/**
	 * Initialization all tables
	 */
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		//init scan operator, and add them to list
		scanOperatorList.clear();
		tupleList.clear();
		firstInvoke=true;
		for(int i =0;i<atomList.size();i++) {
			ScanOperator scanOperator=new ScanOperator(atomList.get(i),dbCatalogue);
			scanOperatorList.add(scanOperator);
		}
	}
	/**
	 * multiple run getNextTuple()
	 */
	@Override
	public void dump() {
		// TODO Auto-generated method stub
		Tuple dumpTuple=getNextTuple();
		while(dumpTuple!=null) {
			dumpTuple=getNextTuple();
		}
		
	}
	/**
	 * merge tuple list to one tuple
	 * @param tupleList
	 * @return tuple
	 */
	public Tuple ListToTuple(List<Tuple> tupleList) {
		List<String> columnName=new ArrayList<String>();
		List<String> columnType=new ArrayList<String>();
		List<String> value=new ArrayList<String>();
		for(int i=0;i<tupleList.size();i++) {
			columnName.addAll(tupleList.get(i).getColumnName());
			columnType.addAll(tupleList.get(i).getColumnType());
			value.addAll(tupleList.get(i).getValue());
		}
		return new Tuple(tupleList.get(0).getTableName(),columnName,columnType,value);
		
	}
	/**
	 * check tuple valid(same column should have same value),such as R(x,y),S(x,z)
	 * @param tuple
	 * @return true/false
	 */
	public boolean tupleValid(Tuple tuple) {
		for (int i = 0; i< tuple.getColumnName().size();i++) {
			for (int j = i+1; j< tuple.getColumnName().size();j++) {
				if(tuple.getColumnName().get(i).equals(tuple.getColumnName().get(j))) {
					if(!tuple.getValue().get(i).equals(tuple.getValue().get(j))) {
						return false;
					}
				}
			}
		}
		return true;
	}
	/**
	 * single table value compare
	 * @param tuple
	 * @param comparisonBody
	 * @return true/false
	 */
	public boolean compareTuple(Tuple tuple,List<ComparisonAtom> comparisonBody) {
		SelectOperator selectOperator=new SelectOperator(comparisonBody,tuple,true);
		tuple=selectOperator.getNextTuple();
		if(!tuple.getTableName().equals("NonVaild")) {
			return true;
		}
		return false;
	}
	
	
}
