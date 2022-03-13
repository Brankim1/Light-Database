/**
 * 
 */
package ed.inf.adbs.minibase;

import java.util.ArrayList;
import java.util.List;

import ed.inf.adbs.minibase.base.RelationalAtom;
import ed.inf.adbs.minibase.base.Term;

/**
 * @author 11791
 *
 */
public class GroupByOperator {
	List<Tuple> tupleList;
	DatabaseCatalog dbCatalogue;
	RelationalAtom atom;


	public GroupByOperator(RelationalAtom atom,DatabaseCatalog dbCatalogue) {
		this.dbCatalogue=dbCatalogue;
		this.atom=atom;
		tupleList=new ArrayList<Tuple>();
		tupleList.addAll(dbCatalogue.getTupleList());
		
		//execute SUM &AVG
		for(int i =0;i<atom.getTerms().size();i++) {				
			List<String> value=new ArrayList<String>();
			if(atom.getTerms().get(i).toString().contains("SUM")||atom.getTerms().get(i).toString().contains("AVG")) {
				int sum=0;
				for(int j=0;j<tupleList.size();j++) {
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
		
		//delete Duplicate
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
		dbCatalogue.setTupleList(tupleList);
	}
}
