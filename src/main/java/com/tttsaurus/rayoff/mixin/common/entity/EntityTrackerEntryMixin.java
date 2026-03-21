package com.tttsaurus.rayoff.mixin.common.entity;

import com.tttsaurus.rayoff.api.EntityPhysicsElement;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTrackerEntry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Consumer;

/**
 * Prevents certain packets from being sent for {@link EntityPhysicsElement}s.
 */
@Mixin(EntityTrackerEntry.class)
public class EntityTrackerEntryMixin {
    @Shadow @Final public Entity trackedEntity;

    @Redirect(
            method = "sendChanges",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V",
                    ordinal = 1
            )
    )
    public void rotate(Consumer consumer, Object object) {
        if (!EntityPhysicsElement.is(trackedEntity)) {
            consumer.accept(object);
        }
    }

    @Redirect(
            method = "sendChanges",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V",
                    ordinal = 2
            )
    )
    public void velocity(Consumer consumer, Object object) {
        if (!EntityPhysicsElement.is(trackedEntity)) {
            consumer.accept(object);
        }
    }

    @Redirect(
            method = "sendChanges",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V",
                    ordinal = 3
            )
    )
    public void multiple(Consumer consumer, Object object) {
        if (!EntityPhysicsElement.is(trackedEntity)) {
            consumer.accept(object);
        }
    }
}
