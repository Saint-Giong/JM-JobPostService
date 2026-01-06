package rmit.saintgiong.jobpostapi.internal.common.dto.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Builder;
import lombok.Getter;
import rmit.saintgiong.jobpostapi.internal.common.type.ErrorLocation;

@Builder
@Getter
@JsonInclude(Include.NON_EMPTY)
public class ApiErrorDetails {
    private ErrorLocation location;
    private String field;
    private Object value;
    private String issue;
}
