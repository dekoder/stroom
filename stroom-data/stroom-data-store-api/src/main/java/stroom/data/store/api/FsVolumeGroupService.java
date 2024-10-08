package stroom.data.store.impl.fs;

import stroom.data.store.impl.fs.shared.FsVolumeGroup;
import stroom.docref.DocRef;

import java.util.List;

public interface FsVolumeGroupService {

    String ENTITY_TYPE = "FS_VOLUME_GROUP";
    DocRef EVENT_DOCREF = new DocRef(ENTITY_TYPE, null, null);

    List<String> getNames();

    List<FsVolumeGroup> getAll();

    FsVolumeGroup create();

    FsVolumeGroup getOrCreate(String name);

    FsVolumeGroup update(FsVolumeGroup indexVolumeGroup);

    FsVolumeGroup get(String name);

    FsVolumeGroup get(int id);

    void delete(int id);

    void ensureDefaultVolumes();
}
