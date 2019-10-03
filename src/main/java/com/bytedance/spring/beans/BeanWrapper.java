package com.bytedance.spring.beans;

public class BeanWrapper {

    private Object wrappedInstance;



    public BeanWrapper(Object wrappedInstance) {
        this.wrappedInstance = wrappedInstance;
    }

    /**
     * Return the bean instance wrapped by this object.
     */
    public Object getWrappedInstance() {
        return wrappedInstance;
    }

    /**
     * Return the type of the wrapped bean instance.
     */
    public Class<?> getWrappedClass() {
        return wrappedInstance.getClass();
    }

}
