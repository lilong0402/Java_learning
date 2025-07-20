package top.lilong.handle;


import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import top.lilong.annotation.RepeatSubmit;

@Component
@Aspect
/**
 * Aspect：切面，即一个横跨多个核心逻辑的功能，或者称之为系统关注点；
 * Joinpoint：连接点，即定义在应用程序流程的何处插入切面的执行；
 * Pointcut：切入点，即一组连接点的集合；
 * Advice：增强，指特定连接点上执行的动作；
 * Introduction：引介，指为一个已有的Java对象动态地增加新的接口；
 * Weaving：织入，指将切面整合到程序的执行流程中；
 * Interceptor：拦截器，是一种实现增强的方式；
 * Target Object：目标对象，即真正执行业务的核心逻辑对象；
 * AOP Proxy：AOP代理，是客户端持有的增强后的对象引用
 */
@Slf4j
public class PreventRepeatSubmitAspect {

    @Pointcut(value = "@annotation(repeatSubmit))")
    public void pointCutNoRepeatSubmit(RepeatSubmit repeatSubmit) {}

    /**
     * 环绕通知, 围绕着方法执行
     * @param joinPoint
     * @param repeatSubmit
     * @return
     * @throws Throwable
     * @Around 可以用来在调用一个具体方法前和调用后来完成一些具体的任务。
     * <p>
     * 方式一：单用 @Around("execution(* net.wnn.controller.*.*(..))")可以
     * 方式二：用@Pointcut和@Around联合注解也可以（本博客采用这个）
     * <p>
     * <p>
     * 两种方式
     * 方式一：加锁 固定时间内不能重复提交
     * <p>
     * 方式二：先请求获取token，这边再删除token,删除成功则是第一次提交
     */
    @Around("pointCutNoRepeatSubmit(repeatSubmit)")
    public Object around(ProceedingJoinPoint joinPoint, RepeatSubmit repeatSubmit) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        boolean res = false;
        // 防止重复提交
        String type = repeatSubmit.limitType().name();

        /**
         * 通过令牌方式防止重复提交
         */
        String requestToken = request.getHeader("request-token");
        if (StringUtils.isBlank(requestToken)) {
            throw new Exception("token为空");
        }
        // 在redis 中查询这个token 如果存在 就让 res = true

        if (!res) {
            log.error("请求重复提交");
            log.info("环绕通知中");
            return null;
        }
        log.info("环绕通知执行前");
        Object obj = joinPoint.proceed();
        log.info("环绕通知执行后");
        return obj;

    }

}
