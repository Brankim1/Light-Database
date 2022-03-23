package ed.inf.adbs.minibase;

import java.util.ArrayList;
import java.util.List;

import ed.inf.adbs.minibase.base.ComparisonAtom;
import ed.inf.adbs.minibase.base.RelationalAtom;
/**
 * initialize query plan
 * The root operator is Project (I didn't put Group-by as root operator, 
 * 		because I think group-by needs read all line once, so it should split with Iterative models)
 * then if there is only one relational body and zero comparison body, the child of root is scan operator.
 * if there is only one relational body and multiple comparison body, the child of root is select operator, then it has a child is scan operator.
 * if there is multiple relational body, the child of root is select. 
 * 		then select has a child is join. join operator still has select operator child, then select has a scan operator child.  
 * @author pengcheng jin
 *
 */
public class QueryPlan {

	Operator operator;
	int index=0;
	//query tree generate
	public QueryPlan(RelationalAtom head,List<RelationalAtom> relationBody,List<ComparisonAtom> comparisonBody, DatabaseCatalog dbCatalogue) {
		if(relationBody.size()==1) {
			if(comparisonBody.size()==0) {
				operator=new ScanOperator(relationBody.get(0),dbCatalogue);
				
			}else {
				operator=new  ScanOperator(relationBody.get(0),dbCatalogue);
				operator=new SelectOperator(operator,comparisonBody,dbCatalogue);
				
			}
		}else {
			operator=new JoinOperator(relationBody,comparisonBody,dbCatalogue);
			if(comparisonBody.size()!=0) {
				operator=new SelectOperator(operator,comparisonBody,dbCatalogue);
			}
		}
		operator=new ProjectOperator(operator,head,comparisonBody,dbCatalogue);
		
	}
	public Operator getOperator() {
		return operator;
	}
}
