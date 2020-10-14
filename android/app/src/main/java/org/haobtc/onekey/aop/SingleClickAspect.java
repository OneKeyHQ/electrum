package org.haobtc.onekey.aop;

import android.view.View;
import android.widget.Toast;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.haobtc.onekey.activities.base.MyApplication;

import java.lang.reflect.Method;

@Aspect
public class SingleClickAspect {

    /**
     * define the method that was annotated with @SingleClick as joinPoint
     * */
    @Pointcut("execution(@org.haobtc.onekey.aop.SingleClick * *(..))")
    public void methodAnnotated() {}

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
