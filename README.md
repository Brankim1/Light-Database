# Database-CQ-Min-Eva
This is a Java based Light Database, that could minization and evaluation Conjunction Query.
The Conjunction Query look like: Q(x, SUM(t)) :- R(x, y, z), S(x, w, t), x > 5
This program could process Join, Select(=,!=,<,<=,>,>=), Project,GroupBy(SUM & AVG)

Join multiple tables, all table are stored in the List<ScanOperator>, then invoke getNextTuple() once to return one tuple(Iterative model) 
 * The join algorithm:
 * 1. set a tuple list that store each join table first tuple;
 * 2. use one index to mark tables.
 * 3. let index =0 , then multiple run getNextTuple() in last List<ScanOperator>.(ensure it is left join)
 * 4. if first table return null, set first table reset(), then index+1
 * 5. run the Second table getNextTuple(), then index to zero, multiple run getNextTuple() in first table in List<ScanOperator>.
 * 6. then multiple run it, it could return all join tuple.
 * 7. the getNextTuple() could return one tuple once, there is a tupleValid() method could judge whether this tuple is valid(same column name should have same value)

