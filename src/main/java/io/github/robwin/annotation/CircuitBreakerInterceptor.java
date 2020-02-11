package io.github.robwin.annotation;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.Pointcut;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;

@Component
public class CircuitBreakerInterceptor implements MethodInterceptor, PointcutAdvisor, Ordered {

    private Pointcut pointcut;
    private CircuitBreakerRegistry circuitBreakerRegistry;

    public CircuitBreakerInterceptor(CircuitBreakerRegistry circuitBreakerRegistry) {
        this.circuitBreakerRegistry = circuitBreakerRegistry;
        this.pointcut = buildPointcut();
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        Class<?> targetClass = (invocation.getThis() != null ? AopUtils.getTargetClass(invocation.getThis()) : null);
        Method method = ClassUtils.getMostSpecificMethod(invocation.getMethod(), targetClass);

        CircuitBreaker circuitBreakerAnnotation = AnnotatedElementUtils.findMergedAnnotation(method, CircuitBreaker.class);
        if (circuitBreakerAnnotation == null) {
            circuitBreakerAnnotation = AnnotatedElementUtils.findMergedAnnotation(method.getDeclaringClass(), CircuitBreaker.class);
        }

        String backend = circuitBreakerAnnotation.name();
        io.github.resilience4j.circuitbreaker.CircuitBreaker circuitBreaker = circuitBreakerRegistry
                .circuitBreaker(backend);
        Class<?> returnType = method.getReturnType();

        return circuitBreaker.executeCheckedSupplier(invocation::proceed);
    }

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    @Override
    public Advice getAdvice() {
        return this;
    }

    @Override
    public boolean isPerInstance() {
        return true;
    }

    /**
     * Calculate a pointcut for the given async annotation types, if any.
     * @return the applicable Pointcut object, or {@code null} if none
     */
    private Pointcut buildPointcut() {
        Pointcut cpc = new AnnotationMatchingPointcut(CircuitBreaker.class, true);
        Pointcut mpc = new AnnotationMatchingPointcut(null, CircuitBreaker.class, true);
        return new ComposablePointcut(cpc).union(mpc);
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 3;
    }
}
