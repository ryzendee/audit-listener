package ryzendee.app.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import ryzendee.app.common.exception.MappingException;
import ryzendee.app.model.MethodLogDocument;
import ryzendee.starter.audit.model.MethodAuditLogEntry;

/**
 * Маппер для преобразования документов логов методов {@link MethodLogDocument}.
 *
 * @author Dmitry Ryazantsev
 */
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class MethodLogDocumentMapper {

    @Autowired
    protected ObjectMapper objectMapper;

    /**
     * Преобразует модель лога в документ.
     *
     * @param entry данные запроса
     * @return документ
     */
    @Mapping(source = "traceId", target = "traceId")
    @Mapping(source = "timestamp", target = "timestamp")
    @Mapping(source = "logLevel", target = "logLevel")
    @Mapping(source = "methodName", target = "methodName", qualifiedByName = "toJson")
    @Mapping(source = "errorMessage", target = "errorMessage", qualifiedByName = "toJson")
    @Mapping(source = "args", target = "args", qualifiedByName = "toJson")
    @Mapping(source = "result", target = "result", qualifiedByName = "toJson")
    public abstract MethodLogDocument toDocument(MethodAuditLogEntry entry);

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
