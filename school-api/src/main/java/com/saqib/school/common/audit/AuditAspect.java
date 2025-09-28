package com.saqib.school.common.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saqib.school.user.entity.AuditLog;
import com.saqib.school.user.entity.User;
import com.saqib.school.user.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class AuditAspect {

  private final AuditLogRepository auditLogRepository;
  private final ObjectMapper objectMapper;

  @AfterReturning(value = "@annotation(auditable)", returning = "result")
  public void auditAfterReturning(JoinPoint joinPoint, Auditable auditable, Object result) {
    try {
      createAuditLog(joinPoint, auditable, result, null);
    } catch (Exception e) {
      log.error("Error creating audit log", e);
    }
  }

  @AfterThrowing(value = "@annotation(auditable)", throwing = "exception")
  public void auditAfterThrowing(JoinPoint joinPoint, Auditable auditable, Exception exception) {
    try {
      createAuditLog(joinPoint, auditable, null, exception);
    } catch (Exception e) {
      log.error("Error creating audit log for exception", e);
    }
  }

  private void createAuditLog(JoinPoint joinPoint, Auditable auditable, Object result, Exception exception) {
    try {
      User currentUser = getCurrentUser();
      HttpServletRequest request = getCurrentRequest();

      AuditLog auditLog = AuditLog.builder()
        .user(currentUser)
        .action(buildActionDescription(auditable, joinPoint, exception))
        .entityType(auditable.entityType().isEmpty() ?
          joinPoint.getTarget().getClass().getSimpleName() :
          auditable.entityType())
        .entityId(extractEntityId(joinPoint, result))
        .oldValues(auditable.logParameters() ? serializeParameters(joinPoint) : null)
        .newValues(auditable.logResult() && result != null ? serializeObject(result) : null)
        .ipAddress(request != null ? getClientIpAddress(request) : null)
        .userAgent(request != null ? request.getHeader("User-Agent") : null)
        .build();

      auditLogRepository.save(auditLog);

    } catch (Exception e) {
      log.error("Failed to create audit log", e);
    }
  }

  private String buildActionDescription(Auditable auditable, JoinPoint joinPoint, Exception exception) {
    String baseAction = auditable.action();
    String methodName = joinPoint.getSignature().getName();

    if (exception != null) {
      return baseAction + " - FAILED (" + exception.getClass().getSimpleName() + ")";
    }

    return baseAction.isEmpty() ? methodName : baseAction;
  }

  private Long extractEntityId(JoinPoint joinPoint, Object result) {
    // Try to extract ID from method parameters
    Object[] args = joinPoint.getArgs();
    for (Object arg : args) {
      if (arg instanceof Long) {
        return (Long) arg;
      }
      if (arg != null && hasIdField(arg)) {
        return getIdFromObject(arg);
      }
    }

    // Try to extract ID from result
    if (result != null && hasIdField(result)) {
      return getIdFromObject(result);
    }

    return null;
  }

  private boolean hasIdField(Object obj) {
    try {
      obj.getClass().getDeclaredField("id");
      return true;
    } catch (NoSuchFieldException e) {
      return false;
    }
  }

  private Long getIdFromObject(Object obj) {
    try {
      java.lang.reflect.Field idField = obj.getClass().getDeclaredField("id");
      idField.setAccessible(true);
      Object idValue = idField.get(obj);
      return idValue instanceof Long ? (Long) idValue : null;
    } catch (Exception e) {
      return null;
    }
  }

  private User getCurrentUser() {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication != null && authentication.isAuthenticated() &&
        !authentication.getName().equals("anonymousUser")) {

        if (authentication.getPrincipal() instanceof User) {
          return (User) authentication.getPrincipal();
        }
      }
    } catch (Exception e) {
      log.debug("Could not get current user for audit", e);
    }
    return null;
  }

  private HttpServletRequest getCurrentRequest() {
    try {
      ServletRequestAttributes attributes =
        (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
      return attributes.getRequest();
    } catch (Exception e) {
      return null;
    }
  }

  private String serializeParameters(JoinPoint joinPoint) {
    try {
      Object[] args = joinPoint.getArgs();
      if (args.length == 0) return null;

      Map<String, Object> parameters = new HashMap<>();
      Method method = ((org.aspectj.lang.reflect.MethodSignature) joinPoint.getSignature()).getMethod();
      String[] paramNames = getParameterNames(method);

      for (int i = 0; i < args.length && i < paramNames.length; i++) {
        if (args[i] != null && !isPasswordField(paramNames[i])) {
          parameters.put(paramNames[i], args[i]);
        }
      }

      return parameters.isEmpty() ? null : objectMapper.writeValueAsString(parameters);
    } catch (JsonProcessingException e) {
      log.warn("Could not serialize parameters for audit", e);
      return null;
    }
  }

  private String[] getParameterNames(Method method) {
    // This is a simplified approach. In production, you might want to use
    // Spring's parameter name discovery or compile with -parameters flag
    String[] names = new String[method.getParameterCount()];
    for (int i = 0; i < names.length; i++) {
      names[i] = "param" + i;
    }
    return names;
  }

  private boolean isPasswordField(String paramName) {
    return paramName != null &&
      (paramName.toLowerCase().contains("password") ||
        paramName.toLowerCase().contains("token"));
  }

  private String serializeObject(Object obj) {
    try {
      return objectMapper.writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      log.warn("Could not serialize object for audit", e);
      return obj.toString();
    }
  }

  private String getClientIpAddress(HttpServletRequest request) {
    String[] headerNames = {
      "X-Forwarded-For",
      "X-Real-IP",
      "Proxy-Client-IP",
      "WL-Proxy-Client-IP",
      "HTTP_X_FORWARDED_FOR",
      "HTTP_X_FORWARDED",
      "HTTP_X_CLUSTER_CLIENT_IP",
      "HTTP_CLIENT_IP",
      "HTTP_FORWARDED_FOR",
      "HTTP_FORWARDED",
      "HTTP_VIA",
      "REMOTE_ADDR"
    };

    for (String header : headerNames) {
      String ip = request.getHeader(header);
      if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
        return ip.split(",")[0].trim();
      }
    }

    return request.getRemoteAddr();
  }
}
