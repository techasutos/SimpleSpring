package com.asu.reactive;

import java.lang.reflect.Method;

public class ReactiveDispatcher {

    public void dispatch(Method method, Object bean) throws Exception {

        Object result = method.invoke(bean);

        if (result instanceof Mono) {
            ((Mono<?>) result).subscribe(res ->
                    System.out.println("Async Response: " + res)
            );
        }
    }
}
