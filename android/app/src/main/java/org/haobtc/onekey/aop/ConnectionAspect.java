package org.haobtc.onekey.aop;

import android.view.View;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * @author liyan
 * @date 12/6/20
 */
@Aspect
public class ConnectionAspect {
    /**
     * define the method that was annotated with @CheckConnection as joinPoint
     * */
    @Pointcut("execution(@org.haobtc.onekey.aop.CheckConnection * *(..))")
    public void methodAnnotated() {

    }
    @Around("methodAnnotated()")
    public void aroundJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
        // retrieve the method args
        View view = null;
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof View) {
                view = (View) arg;
                break;
            }
        }
        if (view == null) {
            return;
        }
        // retrieve the annotated of the method
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        if (!method.isAnnotationPresent(SingleClick.class)) {
            return;
        }
        SingleClick singleClick = method.getAnnotation(SingleClick.class);
        // adjust is fast click or not
        assert singleClick != null;
        if (!ClickUtil.isFastDoubleClick(view, singleClick.value())) {
            // do original logic
            joinPoint.proceed();
        }
    }
}
