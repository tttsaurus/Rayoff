package com.tttsaurus.rayoff.impl.bullet.collision.body;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Quaternion;
import com.tttsaurus.rayoff.api.PhysicsElement;
import com.tttsaurus.rayoff.toolbox.api.math.AABBUtils;
import com.tttsaurus.rayoff.impl.bullet.collision.body.shape.MinecraftShape;
import com.tttsaurus.rayoff.impl.bullet.collision.space.MinecraftSpace;
import com.tttsaurus.rayoff.toolbox.api.compat.Convert;
import com.tttsaurus.rayoff.impl.bullet.thread.util.Clock;
import com.tttsaurus.rayoff.impl.util.Frame;
import com.jme3.math.Vector3f;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.security.InvalidParameterException;

public abstract class ElementRigidBody extends MinecraftRigidBody {

    public enum BuoyancyType {
        NONE,
        AIR,
        WATER,
        ALL
    }

    public enum DragType {
        NONE,
        AIR,
        WATER,
        SIMPLE,
        ALL
    }

    public static final float SLEEP_TIME_IN_SECONDS = 2.0f;

    protected final PhysicsElement element;

    private final Frame frame;
    private final Clock sleepTimer;
    private boolean terrainLoading;
    private float dragCoefficient;
    private BuoyancyType buoyancyType;
    private DragType dragType;
    private BoundingBox currentBoundingBox = new BoundingBox();
    private AxisAlignedBB currentMinecraftBoundingBox = new AxisAlignedBB(0, 0, 0, 0, 0 ,0);

    public ElementRigidBody(PhysicsElement element, MinecraftSpace space, MinecraftShape shape, float mass, float dragCoefficient, float friction, float restitution) {
        super(space, shape, mass);

        if (shape instanceof MinecraftShape.Concave) {
            throw new InvalidParameterException("Only massless rigid bodies can use concave shapes.");
        }

        this.element = element;
        this.frame = new Frame();
        this.sleepTimer = new Clock();

        this.setTerrainLoadingEnabled(!this.isStatic());
        this.setDragCoefficient(dragCoefficient);
        this.setFriction(friction);
        this.setRestitution(restitution);
        this.setBuoyancyType(BuoyancyType.WATER);
        this.setDragType(DragType.SIMPLE);
    }

    public PhysicsElement getElement() {
        return this.element;
    }

    public void readTagInfo(NBTTagCompound tag) {
        if (tag.hasKey("orientation")) {
            this.setPhysicsRotation(Convert.fromTagBulletQuat(tag.getCompoundTag("orientation")));
        }
        if (tag.hasKey("linearVelocity")) {
            this.setLinearVelocity(Convert.fromTagBulletVec3(tag.getCompoundTag("linearVelocity")));
        }
        if (tag.hasKey("angularVelocity")) {
            this.setAngularVelocity(Convert.fromTagBulletVec3(tag.getCompoundTag("angularVelocity")));
        }
//        this.setMass(tag.getFloat("mass"));
//        this.setDragCoefficient(tag.getFloat("dragCoefficient"));
//        this.setFriction(tag.getFloat("friction"));
//        this.setRestitution(tag.getFloat("restitution"));
//        this.setBuoyancyType(ElementRigidBody.BuoyancyType.values()[tag.getInt("buoyancyType")]);
//        this.setDragType(ElementRigidBody.DragType.values()[tag.getInt("dragType")]);
    }

    public boolean terrainLoadingEnabled() {
        return this.terrainLoading && !this.isStatic();
    }

    public void setTerrainLoadingEnabled(boolean terrainLoading) {
        this.terrainLoading = terrainLoading;
    }

    public float getDragCoefficient() {
        return dragCoefficient;
    }

    public void setDragCoefficient(float dragCoefficient) {
        this.dragCoefficient = dragCoefficient;
    }

    public BuoyancyType getBuoyancyType() {
        return this.buoyancyType;
    }

    public void setBuoyancyType(BuoyancyType buoyancyType) {
        this.buoyancyType = buoyancyType;
    }

    public DragType getDragType() {
        return this.dragType;
    }

    public void setDragType(DragType dragType) {
        this.dragType = dragType;
    }

    public Frame getFrame() {
        return this.frame;
    }

    public Clock getSleepTimer() {
        return this.sleepTimer;
    }

    @Override
    public Vector3f getOutlineColor() {
        return this.isActive() ? new Vector3f(1.0f, 1.0f, 1.0f) : new Vector3f(1.0f, 0.0f, 0.0f);
    }

    public void updateFrame() {
        getFrame().from(getFrame(), getPhysicsLocation(new Vector3f()), getPhysicsRotation(new Quaternion()));
        this.updateBoundingBox();
    }

    public boolean isNear(BlockPos blockPos) {
        return this.currentMinecraftBoundingBox.intersects(AABBUtils.inflate(new AxisAlignedBB(blockPos), 0.5f));
    }

    public boolean isWaterBuoyancyEnabled() {
        return buoyancyType == BuoyancyType.WATER || buoyancyType == BuoyancyType.ALL;
    }

    public boolean isAirBuoyancyEnabled() {
        return buoyancyType == BuoyancyType.AIR || buoyancyType == BuoyancyType.ALL;
    }

    public boolean isWaterDragEnabled() {
        // We check for simple drag here, but complex drag is always used for water buoyancy.
        return dragType == DragType.WATER || dragType == DragType.ALL || dragType == DragType.SIMPLE;
    }

    public boolean isAirDragEnabled() {
        return dragType == DragType.AIR || dragType == DragType.ALL;
    }

    public void updateBoundingBox() {
        this.currentBoundingBox = this.boundingBox(this.currentBoundingBox);
        this.currentMinecraftBoundingBox = Convert.toMcAABB(this.currentBoundingBox);
    }

    public AxisAlignedBB getCurrentMinecraftBoundingBox() {
        return currentMinecraftBoundingBox;
    }

    public BoundingBox getCurrentBoundingBox() {
        return currentBoundingBox;
    }
}