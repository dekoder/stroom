package stroom.schedule.client;

import stroom.util.shared.scheduler.ScheduleType;

import com.gwtplatform.mvp.client.UiHandlers;

public interface ScheduleUiHandlers extends UiHandlers {

    void onScheduleTypeChange(ScheduleType scheduleType);
}
