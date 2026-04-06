package com.example.order.infrastructure.aspect;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Parameter;
import java.util.StringJoiner;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);
    private static final String CORRELATION_ID_HEADER = "correlationId";

    // ==================== POINTCUTS ====================

    // ----- CONTROLLER LAYER -----
    @Pointcut("within(com.example.order.adapters.in.web..*)")
    public void webAdapterPackage() {}

    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PatchMapping)")
    public void requestMappingMethods() {}

    @Pointcut("webAdapterPackage() && requestMappingMethods()")
    public void controllerLayer() {}

    // ----- SERVICE LAYER -----
    @Pointcut("within(com.example.order.application.service..*)")
    public void applicationServicePackage() {}

    @Pointcut("@within(org.springframework.stereotype.Service)")
    public void serviceAnnotation() {}

    @Pointcut("applicationServicePackage() && serviceAnnotation()")
    public void serviceLayer() {}

    // ----- PERSISTENCE LAYER -----
    @Pointcut("within(com.example.order.adapters.out.persistence..*)")
    public void persistencePackage() {}

    @Pointcut("@within(org.springframework.stereotype.Repository)")
    public void repositoryAnnotation() {}

    @Pointcut("persistencePackage() && repositoryAnnotation()")
    public void persistenceLayer() {}

    // ----- MESSAGING LAYER -----
    @Pointcut("within(com.example.order.adapters.out.messaging..*)")
    public void messagingPackage() {}

    @Pointcut("execution(* com.example.order.adapters.out.messaging.*Adapter.*(..))")
    public void messagingAdapterMethods() {}

    @Pointcut("messagingPackage() && messagingAdapterMethods()")
    public void messagingLayer() {}

    // ----- ALL LAYERS -----
    @Pointcut("controllerLayer() || serviceLayer() || persistenceLayer() || messagingLayer()")
    public void allLayers() {}

    // ==================== ADVICES ====================

    @Around("allLayers()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String layer = determineLayer(joinPoint);
        String className = getSimpleClassName(joinPoint);
        String methodName = joinPoint.getSignature().getName();
        String correlationId = getCorrelationId(joinPoint);
        String params = extractSimpleParams(joinPoint);

        // Log entrée
        if (params.isEmpty()) {
            log.info("[{}] ▶ IN  - {}.{}() | correlationId: {}",
                    layer, className, methodName, correlationId);
        } else {
            log.info("[{}] ▶ IN  - {}.{}() | correlationId: {} | params: [{}]",
                    layer, className, methodName, correlationId, params);
        }

        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;

            log.info("[{}] ◀ OUT - {}.{}() | correlationId: {} | Duration: {}ms",
                    layer, className, methodName, correlationId, duration);

            return result;
        } catch (Throwable exception) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("[{}] ✖ ERR - {}.{}() | correlationId: {} | Duration: {}ms | Exception: {}",
                    layer, className, methodName, correlationId, duration, exception.getMessage());
            throw exception;
        }
    }

    @AfterThrowing(pointcut = "allLayers()", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        String layer = determineLayer(joinPoint);
        String className = getSimpleClassName(joinPoint);
        String methodName = joinPoint.getSignature().getName();
        String correlationId = getCorrelationId(joinPoint);

        log.error("[{}] ✖ EXCEPTION in {}.{}() | correlationId: {} | Type: {} | Message: {}",
                layer, className, methodName, correlationId, ex.getClass().getSimpleName(), ex.getMessage());
    }

    // ==================== HELPER METHODS ====================

    private String determineLayer(JoinPoint joinPoint) {
        String packageName = joinPoint.getSignature().getDeclaringTypeName();
        if (packageName.contains("adapters.in.web")) {
            return "CONTROLLER";
        } else if (packageName.contains("application.service")) {
            return "SERVICE";
        } else if (packageName.contains("adapters.out.persistence")) {
            return "PERSISTENCE";
        } else if (packageName.contains("adapters.out.messaging")) {
            return "MESSAGING";
        }
        return "UNKNOWN";
    }

    private String getSimpleClassName(JoinPoint joinPoint) {
        return joinPoint.getSignature().getDeclaringType().getSimpleName();
    }

    private String getCorrelationId(JoinPoint joinPoint) {
        // D'abord header HTTP (controllers)
        String correlationId = getCorrelationIdFromHeader();
        if (correlationId != null) {
            return correlationId;
        }
        // Sinon paramètres de méthode (services)
        return getCorrelationIdFromParams(joinPoint);
    }

    private String getCorrelationIdFromHeader() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            return request.getHeader(CORRELATION_ID_HEADER);
        }
        return null;
    }

    private String getCorrelationIdFromParams(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        if (paramNames != null) {
            for (int i = 0; i < paramNames.length; i++) {
                if ("correlationId".equals(paramNames[i]) && args[i] instanceof String) {
                    return (String) args[i];
                }
            }
        }
        return "N/A";
    }

    private String extractSimpleParams(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        Parameter[] parameters = signature.getMethod().getParameters();

        if (paramNames == null || args == null) {
            return "";
        }

        StringJoiner joiner = new StringJoiner(", ");

        for (int i = 0; i < paramNames.length; i++) {
            String paramName = paramNames[i];
            Object arg = args[i];
            Class<?> paramType = parameters[i].getType();

            if (isSimpleType(paramType) && !"correlationId".equals(paramName)) {
                joiner.add(paramName + "=" + arg);
            }
        }

        return joiner.toString();
    }

    private boolean isSimpleType(Class<?> type) {
        return type.isPrimitive() ||
                type == String.class ||
                Number.class.isAssignableFrom(type) ||
                type == Boolean.class ||
                type == Character.class;
    }
}
