package stroom.legacy.impex_6_1;

import stroom.dashboard.shared.DashboardDoc;
import stroom.dictionary.shared.DictionaryDoc;
import stroom.feed.shared.FeedDoc;
import stroom.importexport.api.ImportConverter;
import stroom.index.shared.LuceneIndexDoc;
import stroom.pipeline.shared.PipelineDoc;
import stroom.pipeline.shared.TextConverterDoc;
import stroom.pipeline.shared.XsltDoc;
import stroom.script.shared.ScriptDoc;
import stroom.search.elastic.shared.ElasticClusterDoc;
import stroom.search.elastic.shared.ElasticIndexDoc;
import stroom.search.solr.shared.SolrIndexDoc;
import stroom.statistics.impl.hbase.shared.StroomStatsStoreDoc;
import stroom.statistics.impl.sql.shared.StatisticStoreDoc;
import stroom.visualisation.shared.VisualisationDoc;
import stroom.xmlschema.shared.XmlSchemaDoc;

import com.google.inject.AbstractModule;

@Deprecated
public class LegacyImpexModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ImportConverter.class).to(ImportConverterImpl.class);
        DataMapConverterBinder.create(binder())
                .bind(DashboardDoc.DOCUMENT_TYPE, DashboardDataMapConverter.class)
                .bind(DictionaryDoc.DOCUMENT_TYPE, DictionaryDataMapConverter.class)
                .bind(ElasticClusterDoc.DOCUMENT_TYPE, ElasticClusterDataMapConverter.class)
                .bind(ElasticIndexDoc.DOCUMENT_TYPE, ElasticIndexDataMapConverter.class)
                .bind(FeedDoc.DOCUMENT_TYPE, FeedDataMapConverter.class)
                .bind(LuceneIndexDoc.DOCUMENT_TYPE, IndexDataMapConverter.class)
                .bind(PipelineDoc.DOCUMENT_TYPE, PipelineDataMapConverter.class)
                .bind(ScriptDoc.DOCUMENT_TYPE, ScriptDataMapConverter.class)
                .bind(SolrIndexDoc.DOCUMENT_TYPE, SolrIndexDataMapConverter.class)
                .bind(StatisticStoreDoc.DOCUMENT_TYPE, StatisticDataMapConverter.class)
                .bind(StroomStatsStoreDoc.DOCUMENT_TYPE, StroomStatsDataMapConverter.class)
                .bind(TextConverterDoc.DOCUMENT_TYPE, TextConverterDataMapConverter.class)
                .bind(XmlSchemaDoc.DOCUMENT_TYPE, XmlSchemaDataMapConverter.class)
                .bind(XsltDoc.DOCUMENT_TYPE, XsltDataMapConverter.class)
                .bind(VisualisationDoc.DOCUMENT_TYPE, VisualisationDataMapConverter.class);
    }
}
