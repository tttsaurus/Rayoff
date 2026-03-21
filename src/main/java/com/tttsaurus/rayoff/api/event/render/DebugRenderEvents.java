package com.tttsaurus.rayoff.api.event.render;

import com.tttsaurus.rayoff.impl.util.debug.CollisionObjectDebugger;
import com.tttsaurus.rayoff.impl.bullet.collision.space.MinecraftSpace;
import com.tttsaurus.rayoff.toolbox.api.event.Event;

/**
 * The events available through this class are:
 * <ul>
 *     <li><b>Before Render:</b> Called before each frame of the {@link CollisionObjectDebugger}</li>
 * </ul>
 * @since 1.3.0
 */
public final class DebugRenderEvents {
    private DebugRenderEvents() {
    }

    public static final Event<BeforeRender> BEFORE_RENDER = Event.create();

    @FunctionalInterface
    public interface BeforeRender {
        void onRender(Context context);
    }

    public record Context(MinecraftSpace space) {
    }
}
