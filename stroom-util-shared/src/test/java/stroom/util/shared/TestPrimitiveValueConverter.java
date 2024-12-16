package stroom.util.shared;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TestPrimitiveValueConverter {

    @Test
    void testTypeArray_small() {
        final PrimitiveValueConverter<Small> converter = PrimitiveValueConverter.create(
                Small.class, Small.values());

        assertThat(converter)
                .isInstanceOf(PrimitiveValueConverterArrayImpl.class);

        assertThat(converter.fromPrimitiveValue((byte) 25))
                .isEqualTo(Small.DOG);
    }

    @Test
    void testTypeArray_large() {

        final PrimitiveValueConverter<Large> converter = PrimitiveValueConverter.create(
                Large.class, Large.values());

        assertThat(converter)
                .isInstanceOf(PrimitiveValueConverterMapImpl.class);

        assertThat(converter.fromPrimitiveValue((byte) 250))
                .isEqualTo(Large.DOG);
    }

    @Test
    void testTypeArray_belowZero() {

        final PrimitiveValueConverter<BelowZero> converter = PrimitiveValueConverter.create(
                BelowZero.class, BelowZero.values());

        assertThat(converter)
                .isInstanceOf(PrimitiveValueConverterMapImpl.class);

        assertThat(converter.fromPrimitiveValue((byte) 25))
                .isEqualTo(BelowZero.DOG);
    }


    @Test
    void testTypeArray_empty() {
        Assertions.assertThatThrownBy(
                        () -> {
                            PrimitiveValueConverter.create(Large.class, new Large[0]);
                        })
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testTypeArray_null() {
        Assertions.assertThatThrownBy(
                        () -> {
                            PrimitiveValueConverter.create(Large.class, null);
                        })
                .isInstanceOf(NullPointerException.class);
    }


    // --------------------------------------------------------------------------------


    private enum Small implements HasPrimitiveValue {
        CAT((byte) 5),
        HAMSTER((byte) 10),
        GERBIL((byte) 15),
        RAT((byte) 20),
        DOG((byte) 25),
        ;

        private final byte primitiveValue;

        Small(final byte primitiveValue) {
            this.primitiveValue = primitiveValue;
        }

        @Override
        public byte getPrimitiveValue() {
            return primitiveValue;
        }
    }


    // --------------------------------------------------------------------------------


    private enum Large implements HasPrimitiveValue {
        CAT((byte) 0),
        HAMSTER((byte) 100),
        GERBIL((byte) 150),
        RAT((byte) 200),
        DOG((byte) 250),
        ;

        private final byte primitiveValue;

        Large(final byte primitiveValue) {
            this.primitiveValue = primitiveValue;
        }

        @Override
        public byte getPrimitiveValue() {
            return primitiveValue;
        }
    }


    // --------------------------------------------------------------------------------


    private enum BelowZero implements HasPrimitiveValue {
        CAT((byte) -10),
        HAMSTER((byte) 10),
        GERBIL((byte) 15),
        RAT((byte) 20),
        DOG((byte) 25),
        ;

        private final byte primitiveValue;

        BelowZero(final byte primitiveValue) {
            this.primitiveValue = primitiveValue;
        }

        @Override
        public byte getPrimitiveValue() {
            return primitiveValue;
        }
    }
}
