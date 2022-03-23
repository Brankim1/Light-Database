# Minibase

The Join Algorithm:
Using block nested loops join
Join multiple tables, all table are stored in the List<ScanOperator>(Line63), then invoke getNextTuple() once to return one tuple(Iterative model) 
 * 1. set a tuple list that store each join table first tuple;(Line81)
 * 2. use one index to mark tables.(Line97-123)
 * 3. let index =0 , then multiple run getNextTuple() in index List<ScanOperator>.(it could ensure it is left join)(Line105&122)
 * 4. ScanOperator getNextTuple() will return a tuple, then invoke Select Operator to compare tuple.(Line102)
 * 5. if selectOperator return a valid tuple, merge the list tuple to a full tuple.(Line107)
 * 6. check the full tuple is valid, if different table column name is same, the value should same. then return the full tuple.(Line109)
 * 7. if first table return null, set first table reset(), then index+1;(Line119-123)
 * 8. run the Second table getNextTuple(), then index to zero, multiple run getNextTuple() in first table in List<ScanOperator>.(Line104-106)
 * 9. then multiple run it, it could return all join tuple.
 
 In summary, the join operator has a select operator child , then the select operator has a scan operator child . It could ensure the join algorithm avoid cross product. 
 

The Query Plan:
The root operator is Project (I didn't put Group-by as root operator, because I think group-by needs read all line once, so it should split with Iterative models)
then if there is only one relational body and zero comparison body, the child of root is scan operator.
if there is only one relational body and multiple comparison body, the child of root is select operator, then it has a child is scan operator.
if there is multiple relational body, the child of root is select(for multiple table value compare). then select has a child is join. join operator still has select operator(for single table value compare) child, then select has a scan operator child.  