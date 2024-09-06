package stroom.job.impl;

import stroom.job.shared.BatchScheduleRequest;
import stroom.job.shared.FindJobNodeCriteria;
import stroom.job.shared.JobNode;
import stroom.job.shared.JobNodeListResponse;

import java.util.Optional;

public interface JobNodeDao {

    JobNode create(JobNode jobNode);

    JobNode update(JobNode jobNode);

    void updateSchedule(final BatchScheduleRequest batchScheduleRequest);

    boolean delete(final int id);

    Optional<JobNode> fetch(int id);

    JobNodeListResponse find(FindJobNodeCriteria findJobNodeCriteria);
}
