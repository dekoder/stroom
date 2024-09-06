package stroom.query.language.functions;

import java.util.stream.Stream;

class TestLessThanEqualTo extends AbstractEqualityFunctionTest<LessThanOrEqualTo> {

    @Override
    Class<LessThanOrEqualTo> getFunctionType() {
        return LessThanOrEqualTo.class;
    }

    @Override
    String getOperator() {
        return LessThanOrEqualTo.NAME;
    }

    @Override
    Stream<Values> getTestCaseValues() {
        return Stream.of(
                Values.of(2, 1, false),
                Values.of(2, 1L, false),
                Values.of(2, 1D, false),
                Values.of(2, 1F, false),
                Values.of(2, 2, true),
                Values.of(2, 2L, true),
                Values.of(2, 2D, true),
                Values.of(2, 2F, true),

                Values.of(2L, 1L, false),
                Values.of(2L, 1, false),
                Values.of(2L, 1D, false),
                Values.of(2L, 1F, false),
                Values.of(2L, 2L, true),
                Values.of(2L, 2, true),
                Values.of(2L, 2D, true),
                Values.of(2L, 2F, true),

                Values.of(1.2D, 1.1D, false),
                Values.of(1.2D, 1, false),
                Values.of(1.2D, 1L, false),
                Values.of(1.2D, 1.1F, false),
                Values.of(1.1D, 1.1D, true),
                Values.of(1D, 1D, true),
                Values.of(1D, 1, true),
                Values.of(1D, 1L, true),
                Values.of(1.1D, 1.1F, true),

                Values.of(1.2F, 1.1F, false),
                Values.of(1.2F, 1, false),
                Values.of(1.2F, 1L, false),
                Values.of(1.2F, 1.1D, false),
                Values.of(1.1F, 1.1F, true),
                Values.of(1F, 1, true),
                Values.of(1F, 1L, true),
                Values.of(1.1F, 1.1D, true),

                Values.of(true, false, false),
                Values.of(true, true, true),

                Values.of(TOMORROW, TODAY, false),
                Values.of(TODAY, TODAY, true),

                Values.of("dog", "cat", false),
                Values.of("CAT", "cat", true),
                Values.of("cat", "cat", true)
        );
    }
}
