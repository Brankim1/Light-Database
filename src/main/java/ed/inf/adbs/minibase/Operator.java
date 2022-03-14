package ed.inf.adbs.minibase;
/**
 * abstract class Operator, each operator extend it
 * @author Pengcheng Jin
 *
 */
public abstract class Operator {

	/**
	 * multiple run it to get multiple tuple
	 * @return tuple
	 */
	public abstract Tuple getNextTuple(); // implemented by subclasses
	/**
	 * set the index to first
	 */
	public abstract void reset(); // implemented by subclasses
	/**
	 * multiple run getNextTuple() method
	 */
	public abstract void dump(); // implemented by subclasses

}
