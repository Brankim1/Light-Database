package ed.inf.adbs.minibase;

import java.util.ArrayList;
import java.util.List;

import ed.inf.adbs.minibase.base.ComparisonAtom;
import ed.inf.adbs.minibase.base.RelationalAtom;

public class QueryPlan {

	Operator operator;
	int index=0;
	
	public QueryPlan(RelationalAtom head,List<RelationalAtom> relationBody,List<ComparisonAtom> comparisonBody, DatabaseCatalog dbCatalogue) {
		if(relationBody.size()==1) {
			if(comparisonBody.size()==0) {
				operator=new ScanOperator(relationBody.get(0),dbCatalogue);
				
			}else {
				operator=new  ScanOperator(relationBody.get(0),dbCatalogue);
				operator=new SelectOperator(operator,comparisonBody,head,dbCatalogue);
				
			}
		}else {
			operator=new JoinOperator(relationBody,dbCatalogue);
			if(comparisonBody.size()!=0) {
				operator=new SelectOperator(operator,comparisonBody,head,dbCatalogue);
			}
		}
		operator=new ProjectOperator(operator,head,comparisonBody,dbCatalogue);
		
	}
	public Operator getOperator() {
		return operator;
	}
}
