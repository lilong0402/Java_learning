package top.lilong.annotation;


import java.lang.annotation.*;

/**
 * Target 决定了我们这些自定义注解可以用在哪些元素上
 */
@Target(ElementType.METHOD)

/**
 * Retention 决定了我们自定义注解存在什么阶段、一般都是Runtime
 */
@Retention(RetentionPolicy.RUNTIME)

/**
 * Documented 表示我们的自定义注解在我们生成的javadoc文档中是可以存在的
 */
@Documented
public @interface RepeatSubmit {

    /**
     * 防重提交，支持两种，一个是方法参数，一个是令牌
     */
    enum Type { PARAM, TOKEN }
    /**
     * 默认防重提交，是方法参数
     * @return
     */
    Type limitType() default Type.PARAM;
    /**
     * 加锁过期时间，默认是5秒
     * @return
     */
    long lockTime() default 5;

}
