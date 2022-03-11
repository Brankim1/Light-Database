/**
 * 
 */
package ed.inf.adbs.minibase;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import ed.inf.adbs.minibase.base.ComparisonAtom;
import ed.inf.adbs.minibase.base.ComparisonOperator;
import ed.inf.adbs.minibase.base.RelationalAtom;
import ed.inf.adbs.minibase.base.Term;

/**
 * @author pengcheng
 *
 */
public class SelectOperator extends Operator {
	ComparisonAtom comparAtom;
	List<Tuple> tupleList;
	int num=0;
	boolean condition=true;
	DatabaseCatalog dbCatalogue;
	
	public SelectOperator(ComparisonAtom comparAtom, DatabaseCatalog dbCatalogue) {
		this.comparAtom=comparAtom;
		this.tupleList=new ArrayList<Tuple>();
		this.tupleList=dbCatalogue.getTupleList();
		this.dbCatalogue=dbCatalogue;
		String atom1=comparAtom.getTerm1().toString().trim();
		String atom2=comparAtom.getTerm2().toString().trim();
		String op=comparAtom.getOp().toString().trim();
		
		//delete the variable is constant in relationalAtom, such as R(8, y, z)
		for (int i=0;i<tupleList.size();i++) {
			for(int j=0;j<tupleList.get(i).getColumnName().size();j++) {
				if(!variable(tupleList.get(i).getColumnName().get(j))&&!tupleList.get(i).getValue().get(j).equals(tupleList.get(i).getColumnName().get(j))) {
					tupleList.remove(i);
					i--;
					break;
				}
			}
		}
		
		
		
		//condition false check
		
		//if comparAtom is not in the RelationalAtom
		int numAtom=0;
		for (int i=0;i<tupleList.size();i++) {
			for(int j=0;j<tupleList.get(i).getColumnName().size();j++) {
				if(tupleList.get(i).getColumnName().get(j).equals(atom1)||tupleList.get(i).getColumnName().get(j).equals(atom2)) {
					numAtom++;
				}
			}
		}
		if(numAtom==0) {
			System.out.println("Condition false, Terminate");
					condition=false;
		}
		
		//if two atoms are same, must equal 
		if(condition) {
			if(atom1.equals(atom2)&&!op.equals("=")) {
			System.out.println("Condition false, Terminate");
			condition=false;
		}
		}
		
		//if one of the comparAtom is string, operator must = or !=
		if(condition) {
			if(string(atom1)||string(atom2)) {
			if(op.equals("=")||op.equals("!=")) {
				
			}else {
				System.out.println("Condition false, Terminate");
				condition=false;
			}
		}		
		}
		
		//if both variable, must same data type
		if(condition) {
			try {
			if(!tupleList.get(0).getColumnType().get(tupleList.get(0).getColumnName().indexOf(atom1)).toString().equals(tupleList.get(0).getColumnType().get(tupleList.get(0).getColumnName().indexOf(atom2)).toString())) {
				System.out.println("Condition false, Terminate");
				condition=false;
			}
		}catch (Exception e){
			
		}
		}
		
		//if one variable, one constant, check data type whether same
		if(condition) {
			try {
			if(tupleList.get(0).getColumnType().get(tupleList.get(0).getColumnName().indexOf(atom1)).toString().equals("int")&&string(atom2)) {
			System.out.println("Condition false, Terminate");
			condition=false;
			}
		}catch (Exception e){
			if(tupleList.get(0).getColumnType().get(tupleList.get(0).getColumnName().indexOf(atom2)).toString().equals("int")&&number(atom1)) {
				System.out.println("Condition false, Terminate");
				condition=false;
		}}
		}
		
		if(condition) {
			try {
			if(tupleList.get(0).getColumnType().get(tupleList.get(0).getColumnName().indexOf(atom1)).toString().equals("string")&&number(atom2)) {
				System.out.println("Condition false, Terminate");
				condition=false;
			}
		}catch (Exception e1){
			if(tupleList.get(0).getColumnType().get(tupleList.get(0).getColumnName().indexOf(atom2)).toString().equals("string")&&number(atom1)) {
				System.out.println("Condition false, Terminate");
				condition=false;
			}
		}
		}
		
		
	}
	@Override
	public void getNextTuple() {
		
		// TODO Auto-generated method stub
		if(num<tupleList.size()) {
			String firstElem=comparAtom.getTerm1().toString().trim();
			String secondElem=comparAtom.getTerm2().toString().trim();
			ComparisonOperator operator=comparAtom.getOp();
			
			if(variable(firstElem)&&variable(secondElem)) {
				for(int j=0;j<tupleList.get(num).getColumnName().size();j++) {
					if(tupleList.get(num).getColumnName().get(j).toString().trim().equals(firstElem)) {
						for(int k=0;k<tupleList.get(num).getColumnName().size();k++) {
							if(tupleList.get(num).getColumnName().get(k).toString().trim().equals(secondElem)) {
								switch(operator) {
									case EQ:
										if(!tupleList.get(num).getValue().get(j).equals(tupleList.get(num).getValue().get(k))){
											tupleList.remove(num);
											num--;
										}		
										break;
									case NEQ:
										if(tupleList.get(num).getValue().get(j).equals(tupleList.get(num).getValue().get(k))){
											tupleList.remove(num);
											num--;		
										}
										break;
									case GT:
										if(Integer.valueOf(tupleList.get(num).getValue().get(j).trim())<=Integer.valueOf(tupleList.get(num).getValue().get(k).trim())){
											tupleList.remove(num);
											num--;		
										}
										break;
									case GEQ:
										if(Integer.valueOf(tupleList.get(num).getValue().get(j))<Integer.valueOf(tupleList.get(num).getValue().get(k))){
											tupleList.remove(num);
											num--;		
										}
										break;
									case LT:
										if(Integer.valueOf(tupleList.get(num).getValue().get(j))>=Integer.valueOf(tupleList.get(num).getValue().get(k))){
											tupleList.remove(num);
											num--;	
										}
										break;
									case LEQ:
										if(Integer.valueOf(tupleList.get(num).getValue().get(j))>Integer.valueOf(tupleList.get(num).getValue().get(k))){
											tupleList.remove(num);
											num--;	
										}
										break;
								}
								break;
							}
						}
						break;
					}					
				}
			}else if(variable(firstElem)){
				for(int j=0;j<tupleList.get(num).getColumnName().size();j++) {
					
					if(tupleList.get(num).getColumnName().get(j).toString().trim().equals(firstElem)) {
						switch(operator) {
							case EQ:
								if(!tupleList.get(num).getValue().get(j).equals(secondElem)){
									tupleList.remove(num);
									num--;			
								}		
								break;
							case NEQ:	
								if(tupleList.get(num).getValue().get(j).equals(secondElem)){
									tupleList.remove(num);
									num--;
			
								}
								break;
							case GT:
								
								if(Integer.valueOf(tupleList.get(num).getValue().get(j))<=Integer.valueOf(secondElem)){
									tupleList.remove(num);
									num--;
									
								}
								break;
							case GEQ:
								if(Integer.valueOf(tupleList.get(num).getValue().get(j))<Integer.valueOf(secondElem)){
									tupleList.remove(num);
									num--;
								}
								break;
							case LT:
								if(Integer.valueOf(tupleList.get(num).getValue().get(j))>=Integer.valueOf(secondElem)){
									tupleList.remove(num);
									num--;
								}
								break;
							case LEQ:
								if(Integer.valueOf(tupleList.get(num).getValue().get(j))>Integer.valueOf(secondElem)){
									tupleList.remove(num);
									num--;	
								}
								break;
						}	
						break;
					}	
				}
			}else if(variable(secondElem)){
				for(int j=0;j<tupleList.get(num).getColumnName().size();j++) {
					if(tupleList.get(num).getColumnName().get(j).toString().trim().equals(secondElem)) {
						switch(operator) {
							case EQ:
								if(!tupleList.get(num).getValue().get(j).equals(firstElem)){
									tupleList.remove(num);
									num--;
									
								}		
								break;
							case NEQ:
								if(tupleList.get(num).getValue().get(j).equals(firstElem)){
									tupleList.remove(num);
									num--;
									
								}
								break;
							case GT:
								if(Integer.valueOf(tupleList.get(num).getValue().get(j))>=Integer.valueOf(firstElem)){
									tupleList.remove(num);
									num--;
									
								}
								break;
							case GEQ:
								if(Integer.valueOf(tupleList.get(num).getValue().get(j))>Integer.valueOf(firstElem)){
									tupleList.remove(num);
									num--;
									
								}
								break;
							case LT:
								if(Integer.valueOf(tupleList.get(num).getValue().get(j))<=Integer.valueOf(firstElem)){
									tupleList.remove(num);
									num--;
									
								}
								break;
							case LEQ:
								if(Integer.valueOf(tupleList.get(num).getValue().get(j))<Integer.valueOf(firstElem)){
									tupleList.remove(num);
									num--;
									
								}
								break;
							
						}
						break;				
					}	
				}
			}
			num++;
//			System.out.println("123");
//			for(Tuple tuple:tupleList) {
//				System.out.println(tuple.getValue().toString());
//			}
		}
		if(num==tupleList.size()) {
			dbCatalogue.setTupleList(tupleList);
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
	
	public static boolean number(String str) {
		str=str.trim();
		//check number
		String bigStr;
		try {
			bigStr=new BigDecimal(str).toString();
			return true;
		}catch(Exception e) {
		}
		return false;
	}
	
	public static boolean string(String str) {
		str=str.trim();
		//check string""
		if (str.startsWith("'")) {
			return true;
		}
		return false;
	}
	
	public static boolean variable(String str) {
		str=str.trim();
		//check string""
		if (string(str)||number(str)) {
			return false;
		}
		return true;
	}
}
