package ed.inf.adbs.minibase;

public abstract class Operator {


	public abstract Tuple getNextTuple(); // implemented by subclasses
	public abstract void reset(); // implemented by subclasses
	public abstract void dump(); // implemented by subclasses

}
