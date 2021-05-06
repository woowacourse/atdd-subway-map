package wooteco.subway.aop.logger;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DaoAspectLogger {
    private static final Logger CONSOLE_LOGGER = LoggerFactory.getLogger(DaoAspectLogger.class);

    @AfterReturning(pointcut = "execution(!void wooteco.subway..*Dao.*(..))", returning = "result")
    public void AfterReturning(JoinPoint joinPoint, Object result) {
        CONSOLE_LOGGER.debug("=====AfterExecutionDao=====");
        CONSOLE_LOGGER.debug(joinPoint.toString());
        CONSOLE_LOGGER.debug(result.toString());
        CONSOLE_LOGGER.debug("===========================");

    }
}
