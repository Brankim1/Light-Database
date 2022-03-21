package ed.inf.adbs.minibase;

import java.util.ArrayList;
import java.util.List;

import ed.inf.adbs.minibase.base.RelationalAtom;
/**
 * Using block nested loops join
 * Join multiple tables, all table are stored in the List<ScanOperator>, then invoke getNextTuple() once to return one tuple(Iterative model) 
 * The join algorithm:
 * 1. set a tuple list that store each join table first tuple;
 * 2. use one index to mark tables.
 * 3. let index =0 , then multiple run getNextTuple() in index List<ScanOperator>.(it could ensure it is left join)
 * 4. if first table return null, set first table reset(), then index+1
 * 5. run the Second table getNextTuple(), then index to zero, multiple run getNextTuple() in first table in List<ScanOperator>.
 * 6. then multiple run it, it could return all join tuple.
 * The getNextTuple() could return one tuple once, there is a tupleValid() method could judge whether this tuple is valid(do selection), 
 * so this method could ensure do selection in each invoke getNextTuple(). rather than waiting for the cross product.
 * @author Pengcheng Jin
 *
 */
public class JoinOperator extends Operator {
	//DatabaseCatalog instance
	DatabaseCatalog dbCatalogue;
	//all relationalAtom
	List<RelationalAtom> atomList;
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
	public JoinOperator(List<RelationalAtom> atomList, DatabaseCatalog dbCatalogue) {
		this.dbCatalogue=dbCatalogue;
		this.atomList=new ArrayList<RelationalAtom>();
		this.atomList=atomList;
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
		//if there are only one table, no need join, output the table
		if(atomList.size()==1) {
			tuple=scanOperatorList.get(0).getNextTuple();
			return tuple;
		}else {//if there are more than one table, join it
			//if the getNextTuple is the first invoke, init a tuple list that contains the first tuple in each table
			if(firstInvoke==true) {
				firstInvoke=false;
				for(int i =0;i<scanOperatorList.size();i++) {
					tupleList.add(scanOperatorList.get(i).getNextTuple());
					
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
						scanOperatorList.get(runIndex).reset();
						temTuple=scanOperatorList.get(runIndex).getNextTuple();
						tupleList.set(runIndex, temTuple);
						runIndex++;
					}
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
		for(int i =0;i<scanOperatorList.size();i++) {
			scanOperatorList.get(i).reset();
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
	 * check tuple valid(same column should have same value)
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
	
}
