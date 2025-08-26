package ryzendee.app.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.WriteTypeHint;

/**
 * Документ для хранения логов HTTP-вызовов.
 * <p>
 * Расширяет {@link AbstractLogDocument}, добавляя поля,
 * специфичные для HTTP-запросов
 *
 * @author Dmitry Ryazantsev
 */
@Document(indexName = "audit-http", createIndex = false, writeTypeHint = WriteTypeHint.FALSE)
@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class HttpLogDocument extends AbstractLogDocument {

    @NotBlank(message = "direction must not be blank")
    private String direction;

    @NotBlank(message = "httpMethod must not be blank (GET/POST/PUT/...)")
    private String httpMethod;

    @Min(value = 100, message = "httpStatusCode must be a valid HTTP status code (>=${value})")
    @Max(value = 599, message = "httpStatusCode must be a valid HTTP status code (<=${value})")
    private int httpStatusCode;

    @NotBlank(message = "requestPath must not be blank")
    private String requestPath;

    @PositiveOrZero(message = "durationMs must be zero or positive")
    private long durationMs;

    private String requestBody;
    private String responseBody;
}
