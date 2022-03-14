/**
 * 
 */
package ed.inf.adbs.minibase;

import java.util.ArrayList;
import java.util.List;

import ed.inf.adbs.minibase.base.RelationalAtom;
import ed.inf.adbs.minibase.base.Term;

/**
 * @author pengcheng
 *
 */
public class GroupByOperator extends Operator{
	List<Tuple> tupleList;
	DatabaseCatalog dbCatalogue;
	RelationalAtom atom;
	int numIndex=0;
	public GroupByOperator(RelationalAtom atom,DatabaseCatalog dbCatalogue) {
		this.dbCatalogue=dbCatalogue;
		this.atom=atom;
		tupleList=new ArrayList<Tuple>();
		tupleList.addAll(dbCatalogue.getTupleList());
	}

	@Override
	public Tuple getNextTuple() {
		// TODO Auto-generated method stub
		//Execute SUM & AVG
		for(int i =0;i<atom.getTerms().size();i++) {				
			List<String> value=new ArrayList<String>();
			if(atom.getTerms().get(i).toString().contains("SUM")||atom.getTerms().get(i).toString().contains("AVG")) {
				int sum=0;
				for(int j=0;j<tupleList.size();j++) {
					numIndex++;
					sum=sum+Integer.valueOf(tupleList.get(j).getValue().get(i));
				}
				if(atom.getTerms().get(i).toString().contains("AVG")) {
					//average
					sum=sum/tupleList.size();
				}
				for(int j=0;j<tupleList.size();j++) {
					tupleList.get(j).getValue().set(i, Integer.toString(sum));
				}
			}
		}
		
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
		//store the new tuple to dbCatalogue
		dbCatalogue.setTupleList(tupleList);
		return null;
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
