/**
 * 
 */
package ed.inf.adbs.minibase;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import ed.inf.adbs.minibase.base.RelationalAtom;
import ed.inf.adbs.minibase.base.Term;

/**
 * @author 11791
 *
 */
public class ProjectOperator extends Operator{
	
	RelationalAtom atom;
	List<Tuple> tupleList;
	List<Tuple> newTupleList;
	DatabaseCatalog dbCatalogue;
	List<Term> head;
	int num=0;
	public ProjectOperator(RelationalAtom atom,DatabaseCatalog dbCatalogue) {
		this.atom=atom;
		this.tupleList=new ArrayList<Tuple>();
		this.dbCatalogue=dbCatalogue;
		this.tupleList=dbCatalogue.getTupleList();
		this.newTupleList=new ArrayList<Tuple>();
		head=new ArrayList<Term>();
		head=atom.getTerms();
	}
	@Override
	public void getNextTuple() {
		// TODO Auto-generated method stub
		if(num<tupleList.size()) {
			if(head.size()!=0) {
				for(int i=0;i<tupleList.get(num).getColumnName().size();i++) {
					int numSame=0;
					for(int j=0;j<head.size();j++) {
						if(tupleList.get(num).getColumnName().get(i).equals(head.get(j).toString().trim())) {
							numSame++;
						}
						if(("SUM"+tupleList.get(num).getColumnName().get(i)).equals(head.get(j).toString().trim())) {
							numSame++;
							
						}
						if(("AVG"+tupleList.get(num).getColumnName().get(i)).equals(head.get(j).toString().trim())) {
							numSame++;
						}
					}
					if(numSame==0) {
						tupleList.get(num).getValue().remove(i);
						tupleList.get(num).getColumnName().remove(i);
						tupleList.get(num).getColumnType().remove(i);
						i--;
					}
				}
				// order
				List<String> columnName=new ArrayList<String>();
				List<String> columnType=new ArrayList<String>();
				List<String> value=new ArrayList<String>();
				for(int i=0;i<head.size();i++) {
					for(int j=0;j<tupleList.get(num).getColumnName().size();j++) {
						if(tupleList.get(num).getColumnName().get(j).equals(head.get(i).toString().trim())||
								("SUM"+tupleList.get(num).getColumnName().get(j)).equals(head.get(i).toString().trim())||
								("AVG"+tupleList.get(num).getColumnName().get(j)).equals(head.get(i).toString().trim())) {
							columnName.add(tupleList.get(num).getColumnName().get(j));
							columnType.add(tupleList.get(num).getColumnType().get(j));
							value.add(tupleList.get(num).getValue().get(j));			
						}
					}

				}				
					newTupleList.add(new Tuple(tupleList.get(num).getTableName(),columnName,columnType,value));
					columnName.clear();
					columnType.clear();
					value.clear();
			}
			num++;
		}
		
		if(num==tupleList.size()) {
			//execute SUM &AVG
			for(int i =0;i<atom.getTerms().size();i++) {				
				List<String> value=new ArrayList<String>();
				if(atom.getTerms().get(i).toString().contains("SUM")||atom.getTerms().get(i).toString().contains("AVG")) {
					int sum=0;
					for(int j=0;j<newTupleList.size();j++) {
						sum=sum+Integer.valueOf(newTupleList.get(j).getValue().get(i));
					}
					if(atom.getTerms().get(i).toString().contains("AVG")) {
						//average
						sum=sum/newTupleList.size();
					}
					for(int j=0;j<newTupleList.size();j++) {
						newTupleList.get(j).getValue().set(i, Integer.toString(sum));
					}
				}
			
			}
			//delete Duplicate
			int numk=0;
			int numm=0;
			while(numk<newTupleList.size()) {
				numm=0;
				while(numm<newTupleList.size()) {
					if(numm>numk) {
						if(newTupleList.get(numk).getValue().toString().equals(newTupleList.get(numm).getValue().toString())) {
							newTupleList.remove(numm);
							numm--;
						}
					}
					numm++;
				}
				numk++;
			}
			dbCatalogue.setTupleList(newTupleList);
		}
	}
	
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		num=0;
	}

	@Override
	public void dump() {
		// TODO Auto-generated method stub
		while(num<tupleList.size()) {
			getNextTuple();
		}
	}

}
