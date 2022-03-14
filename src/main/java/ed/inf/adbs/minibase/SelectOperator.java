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
 * process ComparisonAtom in each tuple
 * @author Pengcheng Jin
 *
 */
public class SelectOperator extends Operator {
	//ComparisonAtom list
	List<ComparisonAtom> comparisonList;
	//return tuple
	Tuple tuple;
	//check ComparisonAtom is valid
	boolean condition=true;
	//run index
	int numIndex=0;
	/**
	 * check ComparisonAtom is valid
	 * @param comparisonList
	 * @param tuple
	 */
	public SelectOperator(List<ComparisonAtom> comparisonList,Tuple tuple) {
		this.comparisonList=new ArrayList<ComparisonAtom>();
		this.comparisonList=comparisonList;
		this.tuple=tuple;
		
		//condition false checking
		for(ComparisonAtom comparAtom:comparisonList) {
			String atom1=comparAtom.getTerm1().toString().trim();
			String atom2=comparAtom.getTerm2().toString().trim();
			String op=comparAtom.getOp().toString().trim();	
			
			//if comparAtom variable is not in the RelationalAtom column name
			int numAtom1=0;
			int numAtom2=0;
			for(int j=0;j<tuple.getColumnName().size();j++) {
				if(!variable(atom1)) {
					numAtom1=-1;
				}
				if(!variable(atom2)) {
					numAtom2=-1;
				}
				if(variable(atom1)&&tuple.getColumnName().get(j).equals(atom1)) {
					numAtom1++;
				}		
				if(variable(atom2)&&tuple.getColumnName().get(j).equals(atom2)) {
					numAtom2++;
				}
			}
			if(numAtom1==0) {
				condition=false;
				//System.out.println("Condition false1, Terminate");
			}
			if(numAtom2==0) {
				condition=false;
				//System.out.println("Condition false2, Terminate");
			}
			
			//if two atoms are same, must equal 
			if(condition) {
				if(string(atom1)||string(atom2)) {
					if(atom1.equals(atom2)&&!op.equals("=")) {
					condition=false;
					//System.out.println("Condition false3, Terminate");
					}
				}
				
			}
			
			//if one of the comparAtom is string, operator must = or !=
			if(condition) {
				if(string(atom1)||string(atom2)) {
					if(op.equals("=")||op.equals("!=")) {
						
					}else {
						condition=false;
						//System.out.println("Condition false4, Terminate");
					}
				}		
			}
			
			//if both variable, must same data type
			if(condition) {
				try {
					if(!tuple.getColumnType().get(tuple.getColumnName().indexOf(atom1)).toString().equals(tuple.getColumnType().get(tuple.getColumnName().indexOf(atom2)).toString())) {
						condition=false;
						//System.out.println("Condition false5, Terminate");
					}
				}catch (Exception e){
					
				}
			}
			
			//if one variable, one constant, check data type whether same
			if(condition) {
				if(!(!variable(atom1)&&!variable(atom2))) {
					try {
					if(tuple.getColumnType().get(tuple.getColumnName().indexOf(atom1)).toString().equals("int")&&string(atom2)) {
					condition=false;
					}
					}catch (Exception e){
						if(tuple.getColumnType().get(tuple.getColumnName().indexOf(atom2)).toString().equals("int")&&string(atom1)) {
							
							condition=false;
						}
					}
				}
				
			}
			
			if(condition) {
				if(!(!variable(atom1)&&!variable(atom2))) {
					try {
						if(tuple.getColumnType().get(tuple.getColumnName().indexOf(atom1)).toString().equals("string")&&number(atom2)) {
							
							condition=false;
						}
					}catch (Exception e1){
						if(tuple.getColumnType().get(tuple.getColumnName().indexOf(atom2)).toString().equals("string")&&number(atom1)) {
							
							condition=false;
						}
					}
				}
			}
			//if both constant
			if(condition) {
				if(!variable(atom1)&&!variable(atom2)) {
					if(op.equals("=")&&!atom1.equals(atom2)) {
						condition=false;
					}
					if(op.equals("!=")&&atom1.equals(atom2)) {
						condition=false;
					}
					if(number(atom1)&&number(atom2)) {
						if(op.equals(">")&&Integer.valueOf(atom1)<=Integer.valueOf(atom2)) {
							condition=false;
						}else if(op.equals(">=")&&Integer.valueOf(atom1)<Integer.valueOf(atom2)) {
							condition=false;
						}else if(op.equals("<")&&Integer.valueOf(atom1)>=Integer.valueOf(atom2)) {
							condition=false;
						}else if(op.equals("<=")&&Integer.valueOf(atom1)>Integer.valueOf(atom2)) {
							condition=false;
							System.out.println("123456");
						}
						
					}
				}
			}
		}
	}
	/**
	 * two main steps to run select operator
	 * 1. delete the variable is constant in relationalAtom, such as R(8, y, z)
	 * 2. check =,!=,<,<=,>,>= operator, if not accept ,return null
	 * @return tuple
	 */
	@Override
	public Tuple getNextTuple() {
		// TODO Auto-generated method stub
		if(condition!=true) {
			return null;
		}

		//delete the variable is constant in relationalAtom, such as R(8, y, z)
		for(int j=0;j<tuple.getColumnName().size();j++) {
			if(!variable(tuple.getColumnName().get(j))&&!tuple.getValue().get(j).equals(tuple.getColumnName().get(j))) {
				return null;
			}
		}
		
		for(ComparisonAtom comparAtom:comparisonList) {
			numIndex++;
			String firstElem=comparAtom.getTerm1().toString().trim();
			String secondElem=comparAtom.getTerm2().toString().trim();
			ComparisonOperator operator=comparAtom.getOp();	
			
			//if both are variable, compare two column
			if(variable(firstElem)&&variable(secondElem)) {
				for(int j=0;j<tuple.getColumnName().size();j++) {
					if(tuple.getColumnName().get(j).toString().trim().equals(firstElem)) {
						for(int k=0;k<tuple.getColumnName().size();k++) {
							if(tuple.getColumnName().get(k).toString().trim().equals(secondElem)) {
								switch(operator) {
									case EQ:
										if(!tuple.getValue().get(j).equals(tuple.getValue().get(k))){
											return null;
										}		
										break;
									case NEQ:
										if(tuple.getValue().get(j).equals(tuple.getValue().get(k))){
											return null;	
										}
										break;
									case GT:
										if(Integer.valueOf(tuple.getValue().get(j).trim())<=Integer.valueOf(tuple.getValue().get(k).trim())){
											return null;	
										}
										break;
									case GEQ:
										if(Integer.valueOf(tuple.getValue().get(j))<Integer.valueOf(tuple.getValue().get(k))){
											return null;	
										}
										break;
									case LT:
										if(Integer.valueOf(tuple.getValue().get(j))>=Integer.valueOf(tuple.getValue().get(k))){
											return null;	
										}
										break;
									case LEQ:
										if(Integer.valueOf(tuple.getValue().get(j))>Integer.valueOf(tuple.getValue().get(k))){
											return null;	
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
				//if first is variable, second is constant
				for(int j=0;j<tuple.getColumnName().size();j++) {
					
					if(tuple.getColumnName().get(j).toString().trim().equals(firstElem)) {
						switch(operator) {
							case EQ:
								if(!tuple.getValue().get(j).equals(secondElem)){
									return null;			
								}		
								break;
							case NEQ:	
								if(tuple.getValue().get(j).equals(secondElem)){
									return null;
			
								}
								break;
							case GT:
								
								if(Integer.valueOf(tuple.getValue().get(j))<=Integer.valueOf(secondElem)){
									return null;
									
								}
								break;
							case GEQ:
								if(Integer.valueOf(tuple.getValue().get(j))<Integer.valueOf(secondElem)){
									return null;
								}
								break;
							case LT:
								if(Integer.valueOf(tuple.getValue().get(j))>=Integer.valueOf(secondElem)){
									return null;
								}
								break;
							case LEQ:
								if(Integer.valueOf(tuple.getValue().get(j))>Integer.valueOf(secondElem)){
									return null;	
								}
								break;
						}	
						break;
					}	
				}
			}else if(variable(secondElem)){
				//if first is constant, second is variable
				for(int j=0;j<tuple.getColumnName().size();j++) {
					if(tuple.getColumnName().get(j).toString().trim().equals(secondElem)) {
						switch(operator) {
							case EQ:
								if(!tuple.getValue().get(j).equals(firstElem)){
									return null;
								}		
								break;
							case NEQ:
								if(tuple.getValue().get(j).equals(firstElem)){
									return null;
									
								}
								break;
							case GT:
								if(Integer.valueOf(tuple.getValue().get(j))>=Integer.valueOf(firstElem)){
									return null;
									
								}
								break;
							case GEQ:
								if(Integer.valueOf(tuple.getValue().get(j))>Integer.valueOf(firstElem)){
									return null;
									
								}
								break;
							case LT:
								if(Integer.valueOf(tuple.getValue().get(j))<=Integer.valueOf(firstElem)){
									return null;
									
								}
								break;
							case LEQ:
								if(Integer.valueOf(tuple.getValue().get(j))<Integer.valueOf(firstElem)){
									return null;
									
								}
								break;
							
						}
						break;				
					}	
				}
			}
		}
		return tuple;
	}
	
	/**
	 * set numIndex=0 to restart
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
	
	/**
	 * check string could be transfer to int
	 * @param str
	 * @return true/false
	 */
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
	/**
	 * check string are start with "
	 * @param str
	 * @return true/false
	 */
	public static boolean string(String str) {
		str=str.trim();
		//check string""
		if (str.startsWith("'")) {
			return true;
		}
		return false;
	}
	/**
	 * check it whether is not string and number
	 * @param str
	 * @return true/false
	 */
	public static boolean variable(String str) {
		str=str.trim();
		//check variable
		if (string(str)||number(str)) {
			return false;
		}
		return true;
	}
}
