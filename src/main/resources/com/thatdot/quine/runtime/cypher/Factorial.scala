package com.thatdot.quine.runtime.cypher

import com.thatdot.quine.language.QuineIdProvider

@CypherUDF
final class Factorial extends UserDefinedFunction {

  // Determines what the UDF is called when used in Cypher
  val name = "math.factorial"

  // Used to filter out obviously incorrect uses of the UDF at query compilation
  val signatures = Vector(
    UserDefinedFunctionSignature(
      arguments = Vector("input" -> Type.Integer),
      output = Type.Integer,
      description = "Returns the factorial of a number"
    )
  )

  // Gets called every time the UDF is called
  def call(args: Vector[Value])(implicit idProvider: QuineIdProvider): Value =
    args match {
      case Vector(Expr.Integer(n)) if n < 0L => Expr.Null
      case Vector(Expr.Integer(n)) =>
        // calculate factorial
        var acc: Long = 1L
        for (i <- 1L to n) {
          acc *= i
        }
        Expr.Integer(acc)
      case _ =>
        throw new CypherException.WrongSignature(
          name,
          expectedArguments = Seq(Type.Integer),
          actualArguments = args
        )
    }
}
