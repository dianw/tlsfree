package org.tlsfree.core.persistence;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.transaction.annotation.Transactional;

@Documented
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
@Transactional(readOnly = true)
public @interface ReadOnlyTransactional {
}
