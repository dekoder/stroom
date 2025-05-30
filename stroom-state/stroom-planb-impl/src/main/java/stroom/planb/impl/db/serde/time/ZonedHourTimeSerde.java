package stroom.planb.impl.db.serde.time;

import stroom.lmdb.serde.UnsignedBytes;
import stroom.lmdb.serde.UnsignedBytesInstances;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class ZonedHourTimeSerde implements TimeSerde {

    private static final UnsignedBytes UNSIGNED_BYTES = UnsignedBytesInstances.ONE;

    private final ZonedDayTimeSerde zonedDayTimeSerde;
    private final ZoneId zone;

    public ZonedHourTimeSerde(final ZoneId zone) {
        this.zone = zone;
        zonedDayTimeSerde = new ZonedDayTimeSerde(zone);
    }

    @Override
    public void write(final ByteBuffer byteBuffer, final Instant instant) {
        final ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, zone);
        // Write date
        zonedDayTimeSerde.writeLocalDate(byteBuffer, zonedDateTime.toLocalDate());
        // Write hour
        UNSIGNED_BYTES.put(byteBuffer, zonedDateTime.getHour());
    }

    @Override
    public Instant read(final ByteBuffer byteBuffer) {
        // Read date
        final LocalDate localDate = zonedDayTimeSerde.readLocalDate(byteBuffer);
        // Read hour
        final long hour = UNSIGNED_BYTES.get(byteBuffer);
        return localDate.atStartOfDay(zone).plusHours(hour).toInstant();
    }

    @Override
    public int getSize() {
        return zonedDayTimeSerde.getSize() + 1;
    }
}
