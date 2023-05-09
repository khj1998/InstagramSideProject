package CloneProject.InstagramClone.InstagramService.validator;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE,ElementType.ANNOTATION_TYPE})
@Constraint(validatedBy = PasswordLengthValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PasswordLengthChecker {
    String message() default "비밀번호는 최소 8자리 이상 작성해주세요.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
