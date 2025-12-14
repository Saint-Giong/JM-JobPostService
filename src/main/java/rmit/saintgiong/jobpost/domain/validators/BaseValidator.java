package rmit.saintgiong.jobpost.domain.validators;

import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.AccessLevel;
import lombok.Builder;
import rmit.saintgiong.jobpost.common.exception.DomainCode;
import rmit.saintgiong.jobpost.common.exception.DomainException;

import java.util.ArrayList;
import java.util.List;

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
            throw new DomainException(DomainCode.INVALID_BUSINESS_LOGIC, String.join("; ", errors));
        }
    }

    public abstract void validate(T target);
}
