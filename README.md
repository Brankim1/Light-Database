# Database-CQ-Min-Eva
This is a Java based Light Database, that could minization and evaluation Conjunction Query.
The Conjunction Query look like: Q(x, SUM(t)) :- R(x, y, z), S(x, w, t), x > 5
This program could process Join, Select(=,!=,<,<=,>,>=), Project,GroupBy(SUM & AVG)

Using block nested loops join
Join multiple tables, all table are stored in the List<ScanOperator>, then invoke getNextTuple() once to return one tuple(Iterative model) 
The join algorithm:
 * 1. set a tuple list that store each join table first tuple;(Line52)
 * 2. use one index to mark tables.(Line87)
 * 3. let index =0 , then multiple run getNextTuple() in index List<ScanOperator>.(it could ensure it is left join)(Line89)
 * 4. if first table return null, set first table reset(), then index+1(Line103)
 * 5. run the Second table getNextTuple(), then index to zero, multiple run getNextTuple() in first table in List<ScanOperator>.(Line92)
 * 6. then multiple run it, it could return all join tuple.(Line88-106)
 * The getNextTuple() could return one tuple once, there is a tupleValid() method could judge whether this tuple is valid(do selection), 
 * so this method could ensure do selection in each invoke getNextTuple(). rather than waiting for the cross product.(Line97 & Line157-168)