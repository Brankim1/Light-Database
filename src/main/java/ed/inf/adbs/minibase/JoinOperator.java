package ed.inf.adbs.minibase;

import java.util.ArrayList;
import java.util.List;

import ed.inf.adbs.minibase.base.RelationalAtom;

public class JoinOperator extends Operator {
	
	RelationalAtom atom;
	DatabaseCatalog dbCatalogue;
	Tuple oldTuple;
	Tuple temTuple;
	Tuple newTuple;
	String tableName;
	List<String> columnName;
	List<String> columnType;
	List<String> value;
	ScanOperator scanOperator;
	Boolean sameVariable;
	
	public JoinOperator(RelationalAtom atom, DatabaseCatalog dbCatalogue) {
		this.dbCatalogue=dbCatalogue;
		this.atom=atom;
		this.oldTuple=dbCatalogue.tuple;
		tableName=dbCatalogue.tuple.getTableName();
		columnName=new ArrayList<String>();
		columnType=new ArrayList<String>();
		value=new ArrayList<String>();
		columnName.addAll(oldTuple.getColumnName());
		columnType.addAll(oldTuple.getColumnType());
		scanOperator=new ScanOperator(atom,dbCatalogue);
		sameVariable=sameVariable(oldTuple.getColumnName(),scanOperator.columnName);
		if(sameVariable==false) {
			columnName.addAll(scanOperator.columnName);
			columnType.addAll(scanOperator.columnType);
		}else {
			
		}
	}
	@Override
	public void getNextTuple() {
		// TODO Auto-generated method stub
		scanOperator.getNextTuple();
		temTuple=scanOperator.tuple;
		if(sameVariable==false) {
			value.addAll(oldTuple.getValue());
			value.addAll(temTuple.getValue());
			newTuple= new Tuple(tableName,columnName,columnType,value);
			dbCatalogue.setTuple(newTuple);
			value.clear();
			
			System.out.println(dbCatalogue.getTuple().getValue());
		}else {
			
		}
		
//		if(ScanFirstTable==false) {
//			//join to big table
//			columnName.addAll(oldTupleList.get(0).getColumnName());
//			columnType.addAll(oldTupleList.get(0).getColumnType());
//			columnName.addAll(temTupleList.get(0).getColumnName());
//			columnType.addAll(temTupleList.get(0).getColumnType());
//			
//			for (int i=0;i<oldTupleList.size();i++) {
//				for (int j=0;j<temTupleList.size();j++) {
//					value.addAll(oldTupleList.get(i).getValue());
//					value.addAll(temTupleList.get(j).getValue());
//					Tuple tuple= new Tuple(tableName,columnName,columnType,value);
//					newTupleList.add(tuple); 
//					value.clear();
//				}
//			}				
//			columnName.clear();
//			columnType.clear();
//			List<Integer> duplliColum=new ArrayList<Integer>();
//			// atom have same variable
//			if(sameVariable(oldTupleList,temTupleList)) {
//				for(int i =0;i<newTupleList.get(0).getColumnName().size();i++) {
//					for(int j =0;j<newTupleList.get(0).getColumnName().size();j++) {
//						if(j>i) {
//							if(newTupleList.get(0).getColumnName().get(i).equals(newTupleList.get(0).getColumnName().get(j))) {
//								duplliColum.add(j);
//								int numSame=0;
//								while(numSame<newTupleList.size()) {
//									if(!newTupleList.get(numSame).getValue().get(i).equals(newTupleList.get(numSame).getValue().get(j))) {
//										newTupleList.remove(numSame);
//										numSame--;
//									}
//									numSame++;
//								}
//							}
//						}
//						
//					}
//				}
//				
//				//delete duplicate column
//				for(int i =0;i<newTupleList.size();i++) {
//					for(int j =0;j<duplliColum.size();j++) {
//						int deleteNum=duplliColum.get(duplliColum.size()-1-j);
//						newTupleList.get(i).getColumnName().remove(deleteNum);
//						newTupleList.get(i).getColumnType().remove(deleteNum);
//						newTupleList.get(i).getValue().remove(deleteNum);
//					}
//					
//					
//				}
//			}
//			
//			dbCatalogue.setTupleList(newTupleList);
//				
//		}
		
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dump() {
		// TODO Auto-generated method stub
		
		
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
