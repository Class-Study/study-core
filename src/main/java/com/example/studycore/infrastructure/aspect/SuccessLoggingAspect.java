//package com.example.studycore.infrastructure.aspect;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.extern.slf4j.Slf4j;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Aspect
//@Component
//public class SuccessLoggingAspect {
//
//    private static final ObjectMapper objectMapper = new ObjectMapper();
//
//    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
//    public void restControllerPointcut() {
//    }
//
//    @Around("restControllerPointcut()")
//    public Object logSuccessfulRequests(ProceedingJoinPoint joinPoint) throws Throwable {
//        final var startTime = System.currentTimeMillis();
//        final var methodName = joinPoint.getSignature().getName();
//        final var className = joinPoint.getTarget().getClass().getSimpleName();
//        final var args = joinPoint.getArgs();
//        final var httpMethod = extractHttpMethod(joinPoint);
//
//        // Extrair request
//        String requestData = extractRequestData(args);
//
//        try {
//            // Executar o método
//            final var result = joinPoint.proceed();
//
//            // Logar sucesso
//            if (result instanceof ResponseEntity<?> responseEntity) {
//                final var status = responseEntity.getStatusCode();
//                final var statusCode = status.value();
//                final var duration = System.currentTimeMillis() - startTime;
//
//                // Extrair response
//                String responseData = extractResponseData(responseEntity);
//
//                log.info(
//                        "✓ SUCCESS | http={} | class={} | method={} | status={} | duration={}ms | request={} | response={}",
//                        httpMethod,
//                        className,
//                        methodName,
//                        statusCode,
//                        duration,
//                        requestData,
//                        responseData
//                );
//            } else {
//                final var duration = System.currentTimeMillis() - startTime;
//                log.info(
//                        "✓ SUCCESS | class={} | method={} | duration={}ms | request={}",
//                        className,
//                        methodName,
//                        duration,
//                        requestData
//                );
//            }
//
//            return result;
//        } catch (Exception ex) {
//            // Erros já são tratados pelo GlobalExceptionHandler
//            throw ex;
//        }
//    }
//
//    /**
//     * Extrai o método HTTP da assinatura da anotação @PostMapping, @GetMapping, etc
//     */
//    private String extractHttpMethod(ProceedingJoinPoint joinPoint) {
//        try {
//            final var method = ((org.aspectj.lang.reflect.MethodSignature) joinPoint.getSignature()).getMethod();
//
//            if (method.isAnnotationPresent(org.springframework.web.bind.annotation.PostMapping.class)) {
//                return "POST";
//            } else if (method.isAnnotationPresent(org.springframework.web.bind.annotation.GetMapping.class)) {
//                return "GET";
//            } else if (method.isAnnotationPresent(org.springframework.web.bind.annotation.PatchMapping.class)) {
//                return "PATCH";
//            } else if (method.isAnnotationPresent(org.springframework.web.bind.annotation.PutMapping.class)) {
//                return "PUT";
//            } else if (method.isAnnotationPresent(org.springframework.web.bind.annotation.DeleteMapping.class)) {
//                return "DELETE";
//            }
//            return "UNKNOWN";
//        } catch (Exception e) {
//            return "UNKNOWN";
//        }
//    }
//
//    /**
//     * Extrai dados do request (parâmetros anotados com @RequestBody)
//     */
//    private String extractRequestData(Object[] args) {
//        try {
//            StringBuilder sb = new StringBuilder();
//
//            for (Object arg : args) {
//                if (arg == null) {
//                    continue;
//                }
//
//                final var argClass = arg.getClass();
//                final var className = argClass.getSimpleName();
//
//                // Ignorar tipos do Spring e tipos primitivos/UUID
//                if (argClass.getName().startsWith("org.springframework") ||
//                    argClass.getName().startsWith("java.util.UUID") ||
//                    argClass == boolean.class ||
//                    argClass == int.class ||
//                    argClass == long.class ||
//                    argClass == String.class) {
//                    continue;
//                }
//
//                // Adicionar ao log se for um objeto (Request/DTO)
//                if (className.endsWith("Request") || className.endsWith("Input")) {
//                    try {
//                        sb.append(objectMapper.writeValueAsString(arg));
//                    } catch (Exception e) {
//                        sb.append(arg.toString());
//                    }
//                }
//            }
//
//            return sb.length() > 0 ? sb.toString() : "{}";
//        } catch (Exception e) {
//            log.debug("Erro ao extrair dados do request", e);
//            return "{}";
//        }
//    }
//
//    /**
//     * Extrai dados do response
//     */
//    private String extractResponseData(ResponseEntity<?> responseEntity) {
//        try {
//            if (responseEntity.getBody() == null) {
//                return "{}";
//            }
//
//            final var body = responseEntity.getBody();
//
//            // Tentar serializar com Jackson
//            try {
//                return objectMapper.writeValueAsString(body);
//            } catch (Exception e) {
//                // Se falhar, usar toString
//                return body.toString();
//            }
//        } catch (Exception e) {
//            log.debug("Erro ao extrair dados do response", e);
//            return "{}";
//        }
//    }
//}
//
//
//
