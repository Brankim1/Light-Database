/**
 * 
 */
package ed.inf.adbs.minibase;

import java.util.ArrayList;
import java.util.List;

import ed.inf.adbs.minibase.base.ComparisonOperator;
import ed.inf.adbs.minibase.base.Term;

/**
 * @author pengcheng jin
 *
 */
public class Tuple {
	String tableName;
	List<String> columnName;
	List<String> columnType;
	List<String> value;
	/**
	 * 
	 * @param tableName
	 * @param columnName
	 * @param columnType
	 * @param value
	 */
	public Tuple(String tableName, List<String> columnName, List<String> columnType, List<String> value) {
		this.tableName=tableName;
		this.columnName=new ArrayList<String>(columnName);
		this.columnType=new ArrayList<String>(columnType);
		this.value=new ArrayList<String>(value);
    }
	/**
	 * 
	 * @return
	 */
	 public String getTableName() {
	        return tableName;
	    }
	 /**
	  * 
	  * @return
	  */
	 public List<String> getColumnName() {
	        return columnName;
	    } 
	 /**
	  * 
	  * @return
	  */
	 public List<String> getColumnType() {
	        return columnType;
	    } 
	 /**
	  * 
	  * @return
	  */
	 public List<String> getValue() {
	        return value;
	    }
}
