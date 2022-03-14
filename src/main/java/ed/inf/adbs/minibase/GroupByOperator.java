/**
 * 
 */
package ed.inf.adbs.minibase;

import java.util.ArrayList;
import java.util.List;

import ed.inf.adbs.minibase.base.RelationalAtom;
import ed.inf.adbs.minibase.base.Term;

/**
 * It could Execute SUM & AVG and Delete Duplicate tuple
 * @author Pengcheng Jin
 * 
 */
public class GroupByOperator extends Operator{
	//buffer all tuple
	List<Tuple> tupleList;
	//DatabaseCatalog instance
	DatabaseCatalog dbCatalogue;
	//head atom
	RelationalAtom atom;
	//run index
	int numIndex=0;
	
	/**
	 * Initialization instance 
	 * @param atom
	 * @param dbCatalogue
	 */
	public GroupByOperator(RelationalAtom atom,DatabaseCatalog dbCatalogue) {
		this.dbCatalogue=dbCatalogue;
		this.atom=atom;
		tupleList=new ArrayList<Tuple>();
		tupleList.addAll(dbCatalogue.getTupleList());
	}
	/**
	 * execute SUM & AVG, Delete Duplicate tuple
	 * @return tupleList, but it saved in DatabaseCatalog instance
	 */
	@Override
	public Tuple getNextTuple() {
		// TODO Auto-generated method stub
		
		//Execute SUM & AVG
		for(int i =0;i<atom.getTerms().size();i++) {				
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

	/**
	 * let numIndex=0 to restart getNextTuple()
	 */
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		numIndex=0;
	}

	/**
	 * multiple run getNextTuple()
	 */
	@Override
	public void dump() {
		// TODO Auto-generated method stub
		Tuple tuple = getNextTuple();
        while (tuple!=null) {
            tuple = getNextTuple();
        }
	}
}
