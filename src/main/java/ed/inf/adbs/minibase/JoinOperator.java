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
	
	public JoinOperator(List<RelationalAtom> atomList, DatabaseCatalog dbCatalogue) {
		this.dbCatalogue=dbCatalogue;
		this.atomList=new ArrayList<RelationalAtom>();
		this.atomList=atomList;
		scanOperatorList=new ArrayList<ScanOperator>();
		
		tupleList=new ArrayList<Tuple>();
		
		runIndex=atomList.size()-1;

		for(int i =0;i<atomList.size();i++) {
			ScanOperator scanOperator=new ScanOperator(atomList.get(i),dbCatalogue);

			scanOperatorList.add(scanOperator);
		}	
	}
	
	@Override
	public Tuple getNextTuple() {
		// TODO Auto-generated method stub
		if(atomList.size()==1) {
			tuple=scanOperatorList.get(0).getNextTuple();
			return tuple;
		}else {
			if(firstInvoke==true) {
				firstInvoke=false;
				for(int i =0;i<scanOperatorList.size();i++) {
					tupleList.add(scanOperatorList.get(i).getNextTuple());
					
				}
				tuple=ListToTuple(tupleList);
				if(tupleValid(tuple)) {
					return tuple;
				}else {
					return null;
				}				
			}else {
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
							return null;
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
		
	}

	@Override
	public void dump() {
		// TODO Auto-generated method stub
		
		
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
		
		return true;
	}
	public boolean sameVariable(List<String> oldTupleName,List<String> temTupleName) {
		for(int i=0;i<oldTupleName.size();i++) {
			for(int j=0;j<temTupleName.size();j++) {
				if(oldTupleName.get(i).equals(temTupleName.get(j))) {
					return true;
				}
			}
		}
		return false;
	}
}
