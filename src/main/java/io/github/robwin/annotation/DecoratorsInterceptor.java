package io.github.robwin.annotation;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

@Component
public class DecoratorsInterceptor implements MethodInterceptor, PointcutAdvisor, Ordered {

    private Pointcut pointcut;
    private CircuitBreakerRegistry circuitBreakerRegistry;
    private final RetryRegistry retryRegistry;
    private final TimeLimiterRegistry timeLimiterRegistry;

    public DecoratorsInterceptor(CircuitBreakerRegistry circuitBreakerRegistry,
                                 RetryRegistry retryRegistry,
                                 TimeLimiterRegistry timeLimiterRegistry) {
        this.circuitBreakerRegistry = circuitBreakerRegistry;
        this.retryRegistry = retryRegistry;
        this.timeLimiterRegistry = timeLimiterRegistry;
        this.pointcut = buildPointcut();
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        Class<?> targetClass = (invocation.getThis() != null ? AopUtils.getTargetClass(invocation.getThis()) : null);
        Method method = ClassUtils.getMostSpecificMethod(invocation.getMethod(), targetClass);

        Set<Class<? extends Annotation>> annotationSet = new HashSet<>();
        annotationSet.add(CircuitBreaker.class);
        annotationSet.add(Retry.class);
        annotationSet.add(Bulkhead.class);
        annotationSet.add(TimeLimiter.class);

        Set<Annotation> allMergedAnnotations = AnnotatedElementUtils.getAllMergedAnnotations(method, annotationSet);

        Decorators decoratorsAnnotation = AnnotatedElementUtils.findMergedAnnotation(method, Decorators.class);
        if (decoratorsAnnotation == null) {
            decoratorsAnnotation = AnnotatedElementUtils.findMergedAnnotation(method.getDeclaringClass(), Decorators.class);
        }

        String name = decoratorsAnnotation.name();
        Class<?> returnType = method.getReturnType();

        io.github.resilience4j.decorators.Decorators.DecorateCheckedSupplier decorateCheckedSupplier =
                io.github.resilience4j.decorators.Decorators.ofCheckedSupplier(invocation::proceed);

        for (Annotation annotation: allMergedAnnotations){
            if(annotation.annotationType().equals(CircuitBreaker.class)){
                CircuitBreaker circuitBreakerAnnotation = (CircuitBreaker)annotation;
                decorateCheckedSupplier = decorateCheckedSupplier.withCircuitBreaker(circuitBreakerRegistry.circuitBreaker(circuitBreakerAnnotation.name()));
            }
            else if(annotation.annotationType().equals(Retry.class)){
                Retry retryAnnotation = (Retry) annotation;
                decorateCheckedSupplier = decorateCheckedSupplier.withRetry(retryRegistry.retry(retryAnnotation.name()));
            }
        }

        return decorateCheckedSupplier.get();
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
        Pointcut cpc = new AnnotationMatchingPointcut(Decorators.class, true);
        Pointcut mpc = new AnnotationMatchingPointcut(null, Decorators.class, true);
        return new ComposablePointcut(cpc).union(mpc);
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 3;
    }
}
