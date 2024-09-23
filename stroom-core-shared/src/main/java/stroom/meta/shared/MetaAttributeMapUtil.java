package stroom.meta.shared;

import stroom.docref.DocRef;

import java.util.HashMap;
import java.util.Map;

public class MetaAttributeMapUtil {

    private MetaAttributeMapUtil() {
        // Utility.
    }

    /**
     * Turns a stream attribute map object into a generic map of attributes for use by an expression filter.
     */
    public static Map<String, Object> createAttributeMap(final Meta meta) {
        final Map<String, Object> map = new HashMap<>();

        if (meta != null) {
            // Non grouped fields
            final String feedName = meta.getFeedName();
            if (feedName != null) {
                map.put(MetaFields.FEED.getFldName(), feedName);
            }
            final String pipelineUuid = meta.getPipelineUuid();
            if (pipelineUuid != null) {
                map.put(MetaFields.PIPELINE.getFldName(), new DocRef("Pipeline", pipelineUuid));
            }
            if (meta.getStatus() != null) {
                map.put(MetaFields.STATUS.getFldName(), meta.getStatus().getDisplayValue());
            }
            if (meta.getTypeName() != null) {
                map.put(MetaFields.TYPE.getFldName(), meta.getTypeName());
            }

            // Id's
            map.put(MetaFields.ID.getFldName(), meta.getId());
            if (meta.getParentMetaId() != null) {
                map.put(MetaFields.PARENT_ID.getFldName(), meta.getParentMetaId());
            }

            // Times
            map.put(MetaFields.CREATE_TIME.getFldName(), meta.getCreateMs());
            map.put(MetaFields.EFFECTIVE_TIME.getFldName(), meta.getEffectiveMs());
            map.put(MetaFields.STATUS_TIME.getFldName(), meta.getStatusMs());

//            FIELDS.add(META_INTERNAL_PROCESSOR_ID);
//            FIELDS.add(META_PROCESSOR_FILTER_ID);
//            FIELDS.add(META_PROCESSOR_TASK_ID);


//            if (streamProcessor != null) {
//                final String pipelineUuid = streamProcessor.getPipelineUuid();
//                if (pipelineUuid != null) {
//                    attributeMap.put(StreamDataSource.PIPELINE, pipelineUuid);
//                }
//            }
        }

//        MetaFields.getExtendedFields().forEach(field -> {
//            final String value = attributeMap.get(field.getName());
//            if (value != null) {
//                try {
//                    switch (field.getFieldType()) {
//                        case TEXT:
//                            map.put(field.getName(), value);
//                            break;
//                        case DATE:
//                            map.put(field.getName(), DateUtil.parseNormalDateTimeString(value));
//                            break;
//                        case DOC_REF:
//                            attributeMap.put(field.getName(), value);
//                            break;
//                        case ID:
//                        case LONG:
//                            map.put(field.getName(), Long.valueOf(value));
//                            break;
//                    }
//                } catch (final RuntimeException e) {
//                    LOGGER.error(e.getMessage(), e);
//                }
//            }
//        });
        return map;
    }
}
