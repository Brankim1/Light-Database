package ed.inf.adbs.minibase;

import java.util.ArrayList;
import java.util.List;

import ed.inf.adbs.minibase.base.RelationalAtom;

public class JoinOperator extends Operator {
	RelationalAtom atom;
	List<Tuple> oldTupleList;
	List<Tuple> temTupleList;
	List<Tuple> newTupleList;
	String tableName="R";
	List<String> columnName;
	List<String> columnType;
	List<String> value;
	DatabaseCatalog dbCatalogue;
	boolean ScanFirstTable=true;
	
	public JoinOperator(RelationalAtom atom, DatabaseCatalog dbCatalogue) {
		this.dbCatalogue=dbCatalogue;
		this.oldTupleList=new ArrayList<Tuple>();
		this.temTupleList=new ArrayList<Tuple>();
		this.newTupleList=new ArrayList<Tuple>();
		this.columnName=new ArrayList<String>();
		this.columnType=new ArrayList<String>();
		this.value=new ArrayList<String>();
		this.oldTupleList=dbCatalogue.getTupleList();
		this.atom=atom;
		
		if(oldTupleList.size()==0) {
			ScanFirstTable=true;
			ScanOperator scanOperator=new ScanOperator(atom,dbCatalogue);
			scanOperator.dump();
			newTupleList=scanOperator.tupleList;
			dbCatalogue.setTupleList(newTupleList);

		}else {
			ScanFirstTable=false;
			ScanOperator scanOperator=new ScanOperator(atom,dbCatalogue);
			scanOperator.dump();
			this.temTupleList=scanOperator.tupleList;
			
		}
		
	}
	@Override
	public void getNextTuple() {
		// TODO Auto-generated method stub
		
		if(ScanFirstTable==false) {
			//join to big table
			columnName.addAll(oldTupleList.get(0).getColumnName());
			columnType.addAll(oldTupleList.get(0).getColumnType());
			columnName.addAll(temTupleList.get(0).getColumnName());
			columnType.addAll(temTupleList.get(0).getColumnType());
			
			for (int i=0;i<oldTupleList.size();i++) {
				for (int j=0;j<temTupleList.size();j++) {
					value.addAll(oldTupleList.get(i).getValue());
					value.addAll(temTupleList.get(j).getValue());
					Tuple tuple= new Tuple(tableName,columnName,columnType,value);
					newTupleList.add(tuple); 
					value.clear();
				}
			}				
			columnName.clear();
			columnType.clear();
			List<Integer> duplliColum=new ArrayList<Integer>();
			// atom have same variable
			if(sameVariable(oldTupleList,temTupleList)) {
				for(int i =0;i<newTupleList.get(0).getColumnName().size();i++) {
					for(int j =0;j<newTupleList.get(0).getColumnName().size();j++) {
						if(j>i) {
							if(newTupleList.get(0).getColumnName().get(i).equals(newTupleList.get(0).getColumnName().get(j))) {
								duplliColum.add(j);
								int numSame=0;
								while(numSame<newTupleList.size()) {
									if(!newTupleList.get(numSame).getValue().get(i).equals(newTupleList.get(numSame).getValue().get(j))) {
										newTupleList.remove(numSame);
										numSame--;
									}
									numSame++;
								}
							}
						}
						
					}
				}
				
				//delete duplicate column
				for(int i =0;i<newTupleList.size();i++) {
					for(int j =0;j<duplliColum.size();j++) {
						int deleteNum=duplliColum.get(duplliColum.size()-1-j);
						newTupleList.get(i).getColumnName().remove(deleteNum);
						newTupleList.get(i).getColumnType().remove(deleteNum);
						newTupleList.get(i).getValue().remove(deleteNum);
					}
					
					
				}
			}
			
			dbCatalogue.setTupleList(newTupleList);
				
		}
		
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dump() {
		// TODO Auto-generated method stub
		
		
	}
	public boolean sameVariable(List<Tuple> oldTupleList,List<Tuple> temTupleList) {
		for(int i=0;i<oldTupleList.get(0).getColumnName().size();i++) {
			for(int j=0;j<temTupleList.get(0).getColumnName().size();j++) {
				if(oldTupleList.get(0).getColumnName().get(i).equals(temTupleList.get(0).getColumnName().get(j))) {
					return true;
				}
			}
		}
		return false;
	}
}
