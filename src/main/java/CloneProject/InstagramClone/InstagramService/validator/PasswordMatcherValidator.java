package CloneProject.InstagramClone.InstagramService.validator;

import CloneProject.InstagramClone.InstagramService.dto.auth.SignUpDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatcherValidator implements ConstraintValidator<PasswordMatcher, SignUpDto> {

    @Override
    public void initialize(PasswordMatcher constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(SignUpDto value, ConstraintValidatorContext context) {
        return value.getPassword()
                .equals(value.getPasswordChecker());
    }
}
