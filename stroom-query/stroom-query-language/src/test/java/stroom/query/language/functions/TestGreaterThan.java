package stroom.query.language.functions;

import java.util.stream.Stream;

class TestGreaterThan extends AbstractEqualityFunctionTest<GreaterThan> {

    @Override
    Class<GreaterThan> getFunctionType() {
        return GreaterThan.class;
    }

    @Override
    String getOperator() {
        return GreaterThan.NAME;
    }

    @Override
    Stream<Values> getTestCaseValues() {
        return Stream.of(
                Values.of(2, 1, true),
                Values.of(2, 1L, true),
                Values.of(2, 1D, true),
                Values.of(2, 1F, true),
                Values.of(2, 2, false),

                Values.of(2L, 1L, true),
                Values.of(2L, 1, true),
                Values.of(2L, 1D, true),
                Values.of(2L, 1F, true),
                Values.of(2L, 2L, false),

                Values.of(1.2D, 1.1D, true),
                Values.of(1.2D, 1, true),
                Values.of(1.2D, 1L, true),
                Values.of(1.2D, 1.1F, true),
                Values.of(1.1D, 1.1D, false),

                Values.of(1.2F, 1.1F, true),
                Values.of(1.2F, 1, true),
                Values.of(1.2F, 1L, true),
                Values.of(1.2F, 1.1D, true),
                Values.of(1.1F, 1.1F, false),

                Values.of(true, false, true),
                Values.of(true, true, false),

                Values.of(TOMORROW, TODAY, true),
                Values.of(TODAY, TODAY, false),

                Values.of("dog", "cat", true),
                Values.of("cat", "cat", false),
                Values.of("CAT", "cat", false),

                // Comparing as numbers
                Values.of("10", "2", true),
                Values.of("10", "2xx", false),
                Values.of("10x", "2xx", false),
                Values.of("1.1", "1", true)
        );
    }
}
