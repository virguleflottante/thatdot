package com.thatdot.quine.runtime.cypher;

import com.thatdot.quine.language.QuineIdProvider;
import java.util.*;

@CypherUDF
public final class JavaFactorial extends JavaUserDefinedFunction {

    // Determines what the UDF is called when used in Cypher
    private static String name = "math.factorial";

    // Used to filter out obviously incorrect uses of the UDF at query compilation
    private static UserDefinedFunctionSignature signature =
        UserDefinedFunctionSignature.create(
            Arrays.asList(new Argument("input", Type.integer())),
            Type.integer(),
            "Returns the factorial of a number"
        );

    // Gets called every time the UDF is called
    @Override
    public Value call(
        List<Value> args,
        QuineIdProvider idProvider
    ) throws CypherException {
        if (args.size() != 1 || !(args.get(0) instanceof Expr.Integer)) {
            throw CypherException.wrongSignature(
                name,
                Arrays.asList(Type.integer()),
                args
            );
        }

        long n = ((Expr.Integer) args.get(0)).getLong();
        if (n < 0L) return Expr.nullValue();

        // calculate factorial
        long acc = 1L;
        for (long i = 1L; i <= n; i += 1L) {
          acc *= i;
        }

        return new Expr.Integer(acc);
    }

    public JavaFactorial() {
        super(name, Arrays.asList(signature));
    }
}

