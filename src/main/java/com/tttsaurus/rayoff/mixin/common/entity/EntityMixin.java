package com.tttsaurus.rayoff.mixin.common.entity;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.tttsaurus.rayoff.api.EntityPhysicsElement;
import com.tttsaurus.rayoff.toolbox.api.compat.Convert;
import com.tttsaurus.rayoff.toolbox.api.math.QuaternionHelper;
import com.tttsaurus.rayoff.toolbox.api.math.VectorHelper;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Basic changes for {@link EntityPhysicsElement}s. ({@link CallbackInfo#cancel()} go brrr)
 */
@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "applyEntityCollision", at = @At("HEAD"), cancellable = true)
    public void pushAwayFrom(Entity entity, CallbackInfo info) {
        if (EntityPhysicsElement.is((Entity) (Object) this) && EntityPhysicsElement.is(entity)) {
            info.cancel();
        }
    }

    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    public void move(CallbackInfo info) {
        if (EntityPhysicsElement.is((Entity) (Object) this)) {
            info.cancel();
        }
    }

    @Inject(method = "writeToNBT", at = @At("HEAD"))
    public void saveWithoutId(NBTTagCompound tag, CallbackInfoReturnable<NBTTagCompound> info) {
        if (EntityPhysicsElement.is((Entity) (Object) this)) {
            var rigidBody = EntityPhysicsElement.get((Entity) (Object) this).getRigidBody();
            tag.setTag("orientation", QuaternionHelper.toTag(Convert.toJomlQuat(rigidBody.getPhysicsRotation(new Quaternion()))));
            tag.setTag("linearVelocity", VectorHelper.toTag(Convert.toJomlVec3(rigidBody.getLinearVelocity(new Vector3f()))));
            tag.setTag("angularVelocity", VectorHelper.toTag(Convert.toJomlVec3(rigidBody.getAngularVelocity(new Vector3f()))));
            tag.setFloat("mass", rigidBody.getMass());
            tag.setFloat("dragCoefficient", rigidBody.getDragCoefficient());
            tag.setFloat("friction", rigidBody.getFriction());
            tag.setFloat("restitution", rigidBody.getRestitution());
            tag.setBoolean("terrainLoadingEnabled", rigidBody.terrainLoadingEnabled());
            tag.setInteger("buoyancyType", rigidBody.getBuoyancyType().ordinal());
            tag.setInteger("dragType", rigidBody.getDragType().ordinal());
        }
    }

    @Inject(method = "readFromNBT", at = @At("HEAD"))
    public void load(NBTTagCompound tag, CallbackInfo info) {
        if (EntityPhysicsElement.is((Entity) (Object) this)) {
            EntityPhysicsElement.get((Entity) (Object) this).getRigidBody().readTagInfo(tag);
        }
    }
}
