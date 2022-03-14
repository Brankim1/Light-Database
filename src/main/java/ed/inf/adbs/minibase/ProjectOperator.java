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
 * @author pengcheng
 *
 */
public class ProjectOperator extends Operator{
	
	RelationalAtom atom;
	Tuple oldTuple;
	Tuple newTuple;
	List<Term> head;
	int numIndex=0;
	public ProjectOperator(RelationalAtom atom,Tuple tuple) {
		this.atom=atom;
		this.oldTuple=tuple;
		head=new ArrayList<Term>();
		head=atom.getTerms();
		
	}
	@Override
	public Tuple getNextTuple() {
		// TODO Auto-generated method stub
			if(head.size()!=0) {
				numIndex++;
				//delete the column that not selected
				for(int i=0;i<oldTuple.getColumnName().size();i++) {
					int numSame=0;
					for(int j=0;j<head.size();j++) {
						if(oldTuple.getColumnName().get(i).equals(head.get(j).toString().trim())) {
							numSame++;
						}
						if(("SUM"+oldTuple.getColumnName().get(i)).equals(head.get(j).toString().trim())) {
							numSame++;
							
						}
						if(("AVG"+oldTuple.getColumnName().get(i)).equals(head.get(j).toString().trim())) {
							numSame++;
						}
					}
					if(numSame==0) {
						oldTuple.getValue().remove(i);
						oldTuple.getColumnName().remove(i);
						oldTuple.getColumnType().remove(i);
						i--;
					}
				}
				//delete the Duplicate column
				int num1=0;
				while(num1<oldTuple.getColumnName().size()){
					int num2=0;
					while(num2<oldTuple.getColumnName().size()){
						if(num2>num1) {
							if(oldTuple.getColumnName().get(num1).equals(oldTuple.getColumnName().get(num2))) {
								oldTuple.getColumnName().remove(num2);
								oldTuple.getColumnType().remove(num2);
								oldTuple.getValue().remove(num2);
							}else {
								num2++;
							}
						}else {
							num2++;
						}
					}
					num1++;
				}
				
				// order column
				List<String> columnName=new ArrayList<String>();
				List<String> columnType=new ArrayList<String>();
				List<String> value=new ArrayList<String>();
				for(int i=0;i<head.size();i++) {
					for(int j=0;j<oldTuple.getColumnName().size();j++) {
						if(oldTuple.getColumnName().get(j).equals(head.get(i).toString().trim())||
								("SUM"+oldTuple.getColumnName().get(j)).equals(head.get(i).toString().trim())||
								("AVG"+oldTuple.getColumnName().get(j)).equals(head.get(i).toString().trim())) {
							columnName.add(oldTuple.getColumnName().get(j));
							columnType.add(oldTuple.getColumnType().get(j));
							value.add(oldTuple.getValue().get(j));			
						}
					}

				}				
				newTuple=new Tuple(oldTuple.getTableName(),columnName,columnType,value);
				
				columnName.clear();
				columnType.clear();
				value.clear();
					
				return newTuple;
			}else {
				return null;
			}
			
		

	}
	
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		numIndex=0;
	}

	@Override
	public void dump() {
		// TODO Auto-generated method stub
		Tuple tuple = getNextTuple();
        while (tuple!=null) {
            tuple = getNextTuple();
        }
	}

}
