package rmit.saintgiong.jobpostservice.common.exception;

import org.springframework.validation.ObjectError;
import rmit.saintgiong.jobpostapi.internal.common.type.DomainCode;
import rmit.saintgiong.jobpostapi.internal.common.type.ErrorLocation;

import java.util.Collections;
import java.util.List;

public abstract class BaseExceptionHandler {

    protected rmit.saintgiong.jobpostapi.internal.common.dto.error.ApiError getErrorResponse(DomainCode domainCode, String message, List<rmit.saintgiong.jobpostapi.internal.common.dto.error.ApiErrorDetails> details) {
        String errorId = String.format("API-%d", domainCode.getCode());
        return rmit.saintgiong.jobpostapi.internal.common.dto.error.ApiError.builder()
                .errorId(errorId)
                .message(message)
                .details(details)
                .build();
    }

    protected rmit.saintgiong.jobpostapi.internal.common.dto.error.ApiErrorDetails toErrorDetail(ObjectError error, ErrorLocation location) {
        return rmit.saintgiong.jobpostapi.internal.common.dto.error.ApiErrorDetails.builder()
                .location(location)
                .issue(error == null ? "" : error.getDefaultMessage())
                .build();
    }

    protected rmit.saintgiong.jobpostapi.internal.common.dto.error.ApiError getErrorResponse(DomainCode code, String message) {
        return getErrorResponse(code, message, Collections.emptyList());
    }

}
