package rmit.saintgiong.jobpostservice.domain.validators;

import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.AccessLevel;
import lombok.Builder;
import rmit.saintgiong.jobpostservice.common.exception.domain.DomainException;

import java.util.ArrayList;
import java.util.List;

import static rmit.saintgiong.jobpostapi.internal.common.type.DomainCode.INVALID_BUSINESS_LOGIC;

@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseValidator<T> {

    @Builder.Default
    protected List<String> errors = new ArrayList<>();

    protected void reject(String message) {
        errors.add(message);
    }

    protected boolean hasErrors() {
        return !errors.isEmpty();
    }

    protected void throwIfErrors() {
        if (hasErrors()) {
            throw new DomainException(INVALID_BUSINESS_LOGIC, String.join("; ", errors));
        }
    }

    public abstract void validate(T target);
}
