package ryzendee.app.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

/**
 * Абстрактный базовый класс для лог-документов.
 * <p>
 * Содержит общие поля, характерные для любого типа логов,
 *
 * @author Dmitry Ryazantsev
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractLogDocument {

    @Id
    protected String id;

    @NotBlank(message = "traceId must not be blank")
    protected String traceId;

    @NotNull(message = "timestamp must not be null")
    @Field(type = FieldType.Date, format = {}, pattern = "uuuu-MM-dd'T'HH:mm:ss")
    protected LocalDateTime timestamp;

    @NotBlank(message = "timestamp must not be blank")
    protected String logLevel;

}
