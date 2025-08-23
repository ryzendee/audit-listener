package ryzendee.app.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.WriteTypeHint;

/**
 * Документ для хранения логов вызовов методов.
 * <p>
 * Расширяет {@link AbstractLogDocument}, добавляя поля,
 * специфичные для логирования выполнения методов
 *
 * @author Dmitry Ryazantsev
*/
@Document(indexName = "audit-methods", createIndex = false, writeTypeHint = WriteTypeHint.FALSE)
@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class MethodLogDocument extends AbstractLogDocument {

    @NotBlank(message = "methodName must not be blank")
    private String methodName;

    @NotBlank(message = "eventType must not be blank")
    private String eventType;

    private String errorMessage;
    private String args;
    private String result;
}
