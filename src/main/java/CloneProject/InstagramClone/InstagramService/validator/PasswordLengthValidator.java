package CloneProject.InstagramClone.InstagramService.validator;

import CloneProject.InstagramClone.InstagramService.dto.auth.SignUpDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordLengthValidator implements ConstraintValidator<PasswordLengthChecker, SignUpDto> {

    @Override
    public void initialize(PasswordLengthChecker constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(SignUpDto value, ConstraintValidatorContext context) {
        return value.getPassword().length() >= 8;
    }
}
