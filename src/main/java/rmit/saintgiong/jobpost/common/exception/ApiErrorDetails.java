package rmit.saintgiong.jobpost.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@JsonInclude(Include.NON_EMPTY)
public class ApiErrorDetails {
    private ErrorLocation location;
    private String field;
    private Object value;
    private String issue;
}
