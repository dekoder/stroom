package stroom.query.language.functions;

import java.util.stream.Stream;

class TestGreaterThanEqualTo extends AbstractEqualityFunctionTest<GreaterThanOrEqualTo> {

    @Override
    Class<GreaterThanOrEqualTo> getFunctionType() {
        return GreaterThanOrEqualTo.class;
    }

    @Override
    String getOperator() {
        return GreaterThanOrEqualTo.NAME;
    }

    @Override
    Stream<Values> getTestCaseValues() {
        return Stream.of(
                Values.of(2, 1, true),
                Values.of(2, 1L, true),
                Values.of(2, 1D, true),
                Values.of(2, 1F, true),
                Values.of(2, 2, true),
                Values.of(2, 2L, true),
                Values.of(2, 2D, true),
                Values.of(2, 2F, true),
                Values.of(2, null, false),

                Values.of(2L, 1L, true),
                Values.of(2L, 1, true),
                Values.of(2L, 1D, true),
                Values.of(2L, 1F, true),
                Values.of(2L, 2L, true),
                Values.of(2L, 2, true),
                Values.of(2L, 2D, true),
                Values.of(2L, 2F, true),
                Values.of(2L, null, false),

                Values.of(1.2D, 1.1D, true),
                Values.of(1.2D, 1, true),
                Values.of(1.2D, 1L, true),
                Values.of(1.2D, 1.1F, true),
                Values.of(1.1D, 1.1D, true),
                Values.of(1D, 1D, true),
                Values.of(1D, 1, true),
                Values.of(1D, 1L, true),
                Values.of(1.1D, 1.1F, true),
                Values.of(1.1D, null, false),

                Values.of(1.2F, 1.1F, true),
                Values.of(1.2F, 1, true),
                Values.of(1.2F, 1L, true),
                Values.of(1.2F, 1.1D, true),
                Values.of(1.1F, 1.1F, true),
                Values.of(1F, 1, true),
                Values.of(1F, 1L, true),
                Values.of(1.1F, 1.1D, true),
                Values.of(1.1F, null, false),

                Values.of(true, false, true),
                Values.of(true, true, true),
                Values.of(true, null, false),

                Values.of(TOMORROW, TODAY, true),
                Values.of(TODAY, TODAY, true),
                Values.of(TODAY, null, false),

                Values.of("dog", "cat", true),
                Values.of("CAT", "cat", false),
                Values.of("cat", "cat", true),
                Values.of("cat", null, false),

                Values.of(null, null, true)
        );
    }
}
