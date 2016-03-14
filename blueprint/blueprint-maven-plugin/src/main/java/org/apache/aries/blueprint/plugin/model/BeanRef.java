package org.apache.aries.blueprint.plugin.model;

import org.springframework.stereotype.Component;

import javax.inject.Named;
import javax.inject.Qualifier;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class BeanRef implements Comparable<BeanRef> {
    public String id;
    public Class<?> clazz;
    public Map<Class<? extends Annotation>, Annotation> qualifiers = new HashMap<>();

    /**
     * @param clazz interface or implementation class
     */
    public BeanRef(Class<?> clazz) {
        this.clazz = clazz;
    }

    public BeanRef(Class<?> clazz, String id) {
        this(clazz);
        this.id = id;
    }

    public BeanRef(Field field) {
        this(field.getType());
        Annotation[] annotations = field.getAnnotations();
        setQualifiersFromAnnotations(annotations);
    }

    protected void setQualifiersFromAnnotations(Annotation[] annotations) {
        for (Annotation ann : annotations) {
            if (isQualifier(ann) != null) {
                this.qualifiers.put(ann.annotationType(), ann);
            }
        }
    }

    private Qualifier isQualifier(Annotation ann) {
        return ann.annotationType().getAnnotation(Qualifier.class);
    }

    public static String getBeanName(Class<?> clazz) {
        Component component = clazz.getAnnotation(Component.class);
        Named named = clazz.getAnnotation(Named.class);
        if (component != null && !"".equals(component.value())) {
            return component.value();
        } else if (named != null && !"".equals(named.value())) {
            return named.value();
        } else {
            String name = clazz.getSimpleName();
            return getBeanNameFromSimpleName(name);
        }
    }

    protected static String getBeanNameFromSimpleName(String name) {
        return name.substring(0, 1).toLowerCase() + name.substring(1, name.length());
    }

    public boolean matches(BeanRef template) {
        boolean assignable = template.clazz.isAssignableFrom(this.clazz);
        return assignable && qualifiers.values().containsAll(template.qualifiers.values());
    }

    @Override
    public int compareTo(BeanRef other) {
        return this.id.compareTo(other.id);
    }

    @Override
    public String toString() {
        return this.clazz.getSimpleName() + "(" + this.id + ")";
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof BeanRef)) return false;
        final BeanRef other = (BeanRef) o;
        if (!other.canEqual(this)) return false;
        if (this.id == null ? other.id != null : !this.id.equals(other.id)) return false;
        if (this.clazz == null ? other.clazz != null : !this.clazz.equals(other.clazz)) return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + (this.id == null ? 0 : this.id.hashCode());
        result = result * PRIME + (this.clazz == null ? 0 : this.clazz.hashCode());
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof BeanRef;
    }
}
