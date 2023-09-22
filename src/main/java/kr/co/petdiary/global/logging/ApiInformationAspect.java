package kr.co.petdiary.global.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class ApiInformationAspect {
    @Pointcut("execution(* kr.co.petdiary.owner.presentation..*.*(..))")
    private void ownerController() {
    }

    @Before("ownerController()")
    public void beforeLogApiInformation(JoinPoint joinPoint) {
        threadAndMethodInformation(Thread.currentThread(), joinPoint.getSignature());
        Object[] args = joinPoint.getArgs();

        if (args.length == 0) {
            log.info("[ Parameter : NO ]");
        }

        Arrays.stream(args)
                .forEach(arg -> {
                    log.info("[ Arg Type - {} ]", arg.getClass().getSimpleName());
                    log.info("[ Arg Value - {} ]", arg);
                });
    }

    @AfterReturning(value = "ownerController()", returning = "returnObject")
    public void afterReturnLogApiInfo(JoinPoint joinPoint, Object returnObject) {
        threadAndMethodInformation(Thread.currentThread(), joinPoint.getSignature());
        log.info("[ Return Type : {} ]", returnObject.getClass().getSimpleName());
        log.info("[ Return Value : {} ]", returnObject);
    }

    private void threadAndMethodInformation(Thread thread, Signature signature) {
        log.info("======= Current Thread Name {} =======", thread);
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        log.info("======= Method Name : {} =======", method.getName());
    }
}
