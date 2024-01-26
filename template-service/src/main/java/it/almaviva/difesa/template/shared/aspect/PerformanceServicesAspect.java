package it.almaviva.difesa.template.shared.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Calendar;

@Aspect
@Slf4j
@Component
public class PerformanceServicesAspect {

    @Around ( "publicServicesMethods()" )
    public Object logServicesPerformance ( ProceedingJoinPoint joinPoint ) throws Throwable {

        long startTime = Calendar.getInstance ().getTimeInMillis ();
        Object result = joinPoint.proceed ();
        long endTime = Calendar.getInstance ().getTimeInMillis ();
        long time = endTime - startTime;
        String info = String.format ( "Performance Service Indicator => SERVICE EXECUTION TIME : %1$s = %2$s " +
                                              "(milliseconds)", joinPoint.getSignature ().toShortString (), time );
        log.info ( info );
        return result;
    }

    @Before ( "publicServicesMethods()" )
    public void logBeforeServices ( JoinPoint joinPoint ) {

        String info = String.format ( "Performance Service Indicator => SERVICE NAME START : %1$s",
                                      joinPoint.getSignature ().toShortString () );
        log.info ( info );
    }

    @After ( "publicServicesMethods()" )
    public void logAfterServices ( JoinPoint joinPoint ) {

        String info = String.format ( "Performance Service Indicator => SERVICE NAME END : %1$s",
                                      joinPoint.getSignature ().toShortString () );
        log.info ( info );
    }

    @Pointcut (
                "execution(public * it.almaviva.difesa.template.placeholder.service..*.*(..)) || " +
                        "execution(public * it.almaviva.difesa.template.templateModel.service..*.*(..)) || " +
                        "execution(public * it.almaviva.difesa.template.templateType.service..*.*(..)))" )
    public void publicServicesMethods () {
        /* Tutti i metodi pubblici di ogni service*/
    }
}
