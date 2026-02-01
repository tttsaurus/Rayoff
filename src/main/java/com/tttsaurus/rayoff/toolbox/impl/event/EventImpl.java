package com.tttsaurus.rayoff.toolbox.impl.event;

import com.cleanroommc.kirino.utils.ReflectionUtils;
import com.google.common.base.Preconditions;
import com.tttsaurus.rayoff.toolbox.api.event.Event;
import org.jspecify.annotations.NonNull;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventImpl<T> implements Event<T> {

    private final List<T> events = new ArrayList<>();
    private final List<Method> methods = new ArrayList<>();
    private final List<MethodHandle> methodHandles = new ArrayList<>();
    private final List<Integer> methodParamCount = new ArrayList<>();

    @Override
    public void register(@NonNull T t) {
        Preconditions.checkNotNull(t);

        events.add(t);

        Method method = t.getClass().getMethods()[0];
        Parameter[] parameters = method.getParameters();
        Class<?>[] params = new Class[parameters.length];
        for (int i = 0; i < params.length; i++) {
            params[i] = parameters[i].getType();
        }

        MethodHandle methodHandle = ReflectionUtils.getMethod(t.getClass(), method.getName(), void.class, params);

        Preconditions.checkNotNull(methodHandle);

        methods.add(method);
        methodHandles.add(methodHandle);
        methodParamCount.add(params.length);
    }

    @Override
    public void invoke(@NonNull Object @NonNull ... params) {
        Preconditions.checkNotNull(params);
        for (Object param : params) {
            Preconditions.checkNotNull(param);
        }

        for (int i = 0; i < events.size(); i++) {
            Preconditions.checkArgument(params.length == methodParamCount.get(i),
                    "The length of argument \"params\" must equal the expected param count=%s.", methodParamCount.get(i));

            MethodHandle handle = methodHandles.get(i);

            Object[] newParams = Arrays.copyOf(params, params.length + 1);
            System.arraycopy(newParams, 0, newParams, 1, params.length);
            newParams[0] = events.get(i);

            try {
                handle.invokeWithArguments(newParams);
            } catch (Throwable e) {
                throw new RuntimeException("Failed to execute \"" +
                                methods.get(i).getName() + "\" in \"" +
                                events.get(i).getClass().getName() + "\".", e);
            }
        }
    }
}
