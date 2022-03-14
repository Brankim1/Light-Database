package ed.inf.adbs.minibase;

import java.util.ArrayList;
import java.util.List;

import ed.inf.adbs.minibase.base.RelationalAtom;

public class JoinOperator extends Operator {
	
	DatabaseCatalog dbCatalogue;
	List<RelationalAtom> atomList;
	Tuple tuple;
	List<ScanOperator> scanOperatorList;
	List<Tuple> tupleList;
	int runIndex;
	boolean firstInvoke=true;
	List<String> NonValidString=new ArrayList<String>();
	
	
	public JoinOperator(List<RelationalAtom> atomList, DatabaseCatalog dbCatalogue) {
		this.dbCatalogue=dbCatalogue;
		this.atomList=new ArrayList<RelationalAtom>();
		this.atomList=atomList;
		scanOperatorList=new ArrayList<ScanOperator>();
		NonValidString.add("NonValidString");
		tupleList=new ArrayList<Tuple>();
		runIndex=atomList.size()-1;
		//init scan operator, and add them to list
		for(int i =0;i<atomList.size();i++) {
			ScanOperator scanOperator=new ScanOperator(atomList.get(i),dbCatalogue);
			scanOperatorList.add(scanOperator);
		}	
	}
	
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
				while(runIndex>=0) {
					Tuple temTuple=scanOperatorList.get(runIndex).getNextTuple();
					if(temTuple!=null) {
						tupleList.set(runIndex, temTuple);
						while(runIndex<(atomList.size()-1)) {
							runIndex++;
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
						runIndex--;
					}
				}
			}
		}	
		return null;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		for(int i =0;i<scanOperatorList.size();i++) {
			scanOperatorList.get(i).reset();
		}
	}

	@Override
	public void dump() {
		// TODO Auto-generated method stub
		Tuple dumpTuple=getNextTuple();
		while(dumpTuple!=null) {
			dumpTuple=getNextTuple();
		}
		
	}
	
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
