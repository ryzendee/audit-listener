package ryzendee.app.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import ryzendee.app.model.HttpLogDocument;

/**
 * Репозиторий для работы с документами http-логов. {@link HttpLogDocument}
 *
 * @author Dmitry Ryazantsev
 */
public interface HttpLogDocumentRepository extends ElasticsearchRepository<HttpLogDocument, String> {
}
