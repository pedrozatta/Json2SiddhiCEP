package br.produban.bdm.ceprule.commons;

import org.apache.commons.lang3.time.StopWatch;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Aspect
@Component
public class AOPLogger {

	Logger logger = LoggerFactory.getLogger(AOPLogger.class);

	public AOPLogger() {
	}

	@Around("execution(* br.produban.bdm.ceprule.service.*.*(..))")
	public Object profile(ProceedingJoinPoint pjp) throws Throwable {
		StopWatch aopsw = new StopWatch();
		aopsw.start();
		ObjectMapper mapper = new ObjectMapper();
		Logger targetLogger = LoggerFactory.getLogger(pjp.getTarget().getClass());

		StopWatch sw = new StopWatch();
		String signatureName = pjp.getSignature().getName();
		String userName = (new UserUtil()).getAuthenticatedUserName();
		if (targetLogger.isInfoEnabled()) {
			targetLogger.info("AOPLogger {} {} start", userName, signatureName);
		}
		if (targetLogger.isTraceEnabled()) {
			targetLogger.trace("AOPLogger {} {} args {}", userName, signatureName,
					mapper.writeValueAsString(pjp.getArgs()));
		}
		sw.start();
		Object result = null;
		Exception exception = null;
		try {
			result = pjp.proceed();
			return result;
		} catch (Exception e) {
			exception = e;
			return result;
		} finally {
			sw.stop();
			if (exception != null) {
				if (targetLogger.isWarnEnabled()) {
					targetLogger.warn("AOPLogger {} {} end ({} ms) with erros {}", userName, signatureName,
							sw.getTime(), exception.getMessage());
				}
			} else {
				if (targetLogger.isTraceEnabled()) {
					targetLogger.trace("AOPLogger {} {} result {}", userName, signatureName,
							mapper.writeValueAsString(result));
				}
				if (targetLogger.isInfoEnabled()) {
					targetLogger.info("AOPLogger {} {} end ({} ms)", userName, signatureName, sw.getTime());
				}
			}
			aopsw.stop();
			logger.trace("Service:{} Total:{}", sw.getTime(), aopsw.getTime());
		}
	}
}