package it.almaviva.difesa.template.shared.aspect;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Optional;

@Aspect
@Slf4j
@Component
public class PerformanceControllersAspect {

    @Around ( "publicControllersMethods()" )
    public Object logControllersPerformance ( ProceedingJoinPoint joinPoint ) throws Throwable {

        Object result = joinPoint.proceed ();

        if(log.isDebugEnabled()) {
            long startTime = Calendar.getInstance().getTimeInMillis();
            long endTime = Calendar.getInstance().getTimeInMillis();

            long time = endTime - startTime;

            String info = String.format("Performance Controller Indicator => CONTROLLER EXECUTION TIME : %1$s = %2$s " +
                    "(milliseconds)", joinPoint.getSignature().toShortString(), time);
            log.debug(info);
        }
        return result;
    }

    /***
     * Logging before execution of one of the list of public methods
     * @param joinPoint
     */
    @Before ( "publicControllersMethods() )" )
    public void logBeforeControllers ( JoinPoint joinPoint ) {

        if(log.isDebugEnabled()) {
            HttpServletRequest request = currentRequest();

            if (null != request) {
                log.debug("Performance Controller Indicator => CONTROLLER PATH INFO : " + request.getServletPath());
            }
            String info = String.format("Performance Controller Indicator => CONTROLLER NAME START : %1$s ",
                    joinPoint.getSignature().toShortString());

            CodeSignature signature = (CodeSignature) joinPoint.getSignature();
            StringBuilder payload = new StringBuilder();
            for (int i = 0; i < joinPoint.getArgs().length; i++) {
                String parameterName = signature.getParameterNames()[i];
                payload.append(parameterName);
                payload.append(": ");
                payload.append(joinPoint.getArgs()[i] != null ? joinPoint.getArgs()[i].toString() : null);
                payload.append(", ");
            }

            log.debug(info);

            log.debug(String.format("Request Payload => %1$s ", payload));

            if (null != request) {
                log.debug("Start Header Section of request ");
                log.debug("Method Type : " + request.getMethod());
                Enumeration<?> headerNames = request.getHeaderNames();
                while (headerNames.hasMoreElements()) {
                    String headerName = (String) headerNames.nextElement();
                    String headerValue = request.getHeader(headerName);
                    log.debug("Header Name: " + headerName + " Header Value : " + headerValue);
                }
                log.debug("End Header Section of request ");
            }
        }
    }

    /***
     * Logging after execution of one of the list of public methods
     * @param joinPoint
     */
    @After ( "publicControllersMethods()" )
    public void logAfterControllers ( JoinPoint joinPoint ) {

        if(log.isDebugEnabled()) {
            String info = String.format("Performance Service Indicator => CONTROLLER NAME END : %1$s",
                    joinPoint.getSignature().toShortString());
            log.debug(info);
        }
    }

    /**
     * List of all public methods of all controllers
     */
    @Pointcut (
                "execution(public * it.almaviva.difesa.template.placeholder..*.*(..)) || " +
                        "execution(public * it.almaviva.difesa.template.templateModel..*.*(..)) || " +
                        "execution(public * it.almaviva.difesa.template.shared..*.*(..)) || " +
                        "execution(public * it.almaviva.difesa.template.templateType..*.*(..))" )
    public void publicControllersMethods () {
    }

    /**
     * Return request current thread bound or null if none bound.
     * @return Current request or null
     */
    private HttpServletRequest currentRequest() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return Optional.ofNullable(servletRequestAttributes).map(ServletRequestAttributes::getRequest).orElse(null);
    }
}