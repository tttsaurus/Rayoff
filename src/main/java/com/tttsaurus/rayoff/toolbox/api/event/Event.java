package com.tttsaurus.rayoff.toolbox.api.event;

import com.tttsaurus.rayoff.toolbox.impl.event.EventImpl;
import org.jspecify.annotations.NonNull;

/**
 * @param <T> T must be a functional interface that only has one method
 *           with <code>void</code> return type. Generic parameters are not allowed
 *           in the method signature. It must be a simple <code>void</code> function with
 *           arbitrary many parameters.
 */
public interface Event<T> {
    @NonNull
    static <T> Event<T> create() {
        return new EventImpl<>();
    }

    void register(@NonNull T t);
    void invoke(@NonNull Object @NonNull ... params);
}
