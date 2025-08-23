package ryzendee.app.mapper;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import ryzendee.app.common.exception.MappingException;
import ryzendee.app.model.HttpLogDocument;
import ryzendee.starter.audit.model.HttpAuditLogEntry;

/**
 * Маппер для преобразования документов HTTP-логов {@link HttpLogDocument}.
 *
 * @author Dmitry Ryazantsev
 */
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class HttpLogEntryDocumentMapper {

    @Autowired
    protected ObjectMapper objectMapper;

    /**
     * Преобразует модель HTTP лога в документ {@link HttpLogDocument}.
     *
     * @param entry данные запроса
     * @return документ
     */
    @Mapping(source = "traceId", target = "traceId")
    @Mapping(source = "timestamp", target = "timestamp")
    @Mapping(source = "logLevel", target = "logLevel")
    @Mapping(source = "direction", target = "direction", qualifiedByName = "toJson")
    @Mapping(source = "httpMethod", target = "httpMethod", qualifiedByName = "toJson")
    @Mapping(source = "httpStatusCode", target = "httpStatusCode")
    @Mapping(source = "requestPath", target = "requestPath", qualifiedByName = "toJson")
    @Mapping(source = "durationMs", target = "durationMs")
    @Mapping(source = "requestBody", target = "requestBody", qualifiedByName = "toJson")
    @Mapping(source = "responseBody", target = "responseBody", qualifiedByName = "toJson")
    public abstract HttpLogDocument toDocument(HttpAuditLogEntry entry);

    /**
     * Преобразует любой объект в JSON-строку через {@link ObjectMapper}.
     *
     * @param value объект для преобразования
     * @return json-строка
     */
    @Named("toJson")
    protected String toJson(Object value) {
        if (value == null) return null;
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            throw new MappingException("Failed to serialize in json", ex);
        }
    }
}


