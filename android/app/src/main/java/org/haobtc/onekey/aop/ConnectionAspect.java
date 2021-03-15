package org.haobtc.onekey.aop;

import java.lang.reflect.Method;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * @author liyan
 * @date 12/6/20
 */
@Aspect
public class ConnectionAspect {
    /** define the method that was annotated with @CheckConnection as joinPoint */
    @Pointcut("execution(@org.haobtc.onekey.aop.CheckConnection * *(..))")
    public void methodAnnotated() {}

    @Around("methodAnnotated()")
    public void aroundJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
        // retrieve the method args

        // retrieve the annotated of the method
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        if (!method.isAnnotationPresent(CheckConnection.class)) {
            return;
        }
        CheckConnection checkConnection = method.getAnnotation(CheckConnection.class);
        // adjust is fast click or not
        assert checkConnection != null;
        //        if (ConnectionCheckUtils.requireConnection()) {
        //            joinPoint.proceed();
        //        }
    }
}
