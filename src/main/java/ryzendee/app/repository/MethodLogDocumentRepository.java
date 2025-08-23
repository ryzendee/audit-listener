package ryzendee.app.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import ryzendee.app.model.MethodLogDocument;

/**
 * Репозиторий для работы с документами логов методов. {@link MethodLogDocument}
 *
 * @author Dmitry Ryazantsev
 */
public interface MethodLogDocumentRepository extends ElasticsearchRepository<MethodLogDocument, String> {
}
