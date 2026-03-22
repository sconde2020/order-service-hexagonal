package com.example.order.infrastructure.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ControllerAspect {

    private static final Logger log = LoggerFactory.getLogger(ControllerAspect.class);

    /**
     * Pointcut pour cibler uniquement le package adapters.in.web
     */
    @Pointcut("within(com.example.order.adapters.in.web..*)")
    public void webAdapterPackage() {}

    /**
     * Pointcut pour les méthodes annotées avec @RequestMapping ou ses variantes
     */
    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PatchMapping)")
    public void requestMappingMethods() {}

    /**
     * Pointcut combiné : méthodes REST dans adapters.in.web uniquement
     */
    @Pointcut("webAdapterPackage() && requestMappingMethods()")
    public void webAdapterEndpoints() {}

    /**
     * Advice Around pour logger les entrées et sorties
     */
    @Around("webAdapterEndpoints()")
    public Object logAroundEndpoint(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        // Log entrée
        log.info("▶ IN  - {}.{}()", className, methodName);

        long startTime = System.currentTimeMillis();

        try {
            // Exécution de la méthode
            Object result = joinPoint.proceed();

            long duration = System.currentTimeMillis() - startTime;

            // Log sortie
            log.info("◀ OUT - {}.{}() | Duration: {}ms",
                    className, methodName, duration);

            return result;
        } catch (Throwable exception) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("✖ ERR - {}.{}() | Duration: {}ms | Exception: {}",
                    className, methodName, duration, exception.getMessage());
            throw exception;
        }
    }

    /**
     * Advice pour logger les exceptions non capturées
     */
    @AfterThrowing(pointcut = "webAdapterEndpoints()", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        log.error("✖ EXCEPTION in {}.{}() | Type: {} | Message: {}",
                className, methodName, ex.getClass().getSimpleName(), ex.getMessage());
    }
}
