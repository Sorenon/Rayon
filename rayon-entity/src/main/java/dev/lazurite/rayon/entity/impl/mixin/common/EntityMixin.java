package dev.lazurite.rayon.entity.impl.mixin.common;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.impl.RayonCoreCommon;
import dev.lazurite.rayon.core.impl.physics.space.body.ElementRigidBody;
import dev.lazurite.rayon.core.impl.util.math.QuaternionHelper;
import dev.lazurite.rayon.core.impl.util.math.VectorHelper;
import dev.lazurite.rayon.entity.api.EntityPhysicsElement;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Basic changes for {@link EntityPhysicsElement}s.
 */
@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow protected abstract Vec3d adjustMovementForCollisions(Vec3d movement);

    @Inject(method = "getVelocity", at = @At("HEAD"), cancellable = true)
    public void getVelocity(CallbackInfoReturnable<Vec3d> info) {
        if (this instanceof EntityPhysicsElement && RayonCoreCommon.isImmersivePortalsPresent()) {
            info.setReturnValue(VectorHelper.vector3fToVec3d(
                ((EntityPhysicsElement) this).getRigidBody().getLinearVelocity(new Vector3f()).multLocal(0.05f).multLocal(0.2f)
            ));
        }
    }

    @Inject(method = "pushAwayFrom", at = @At("HEAD"), cancellable = true)
    public void pushAwayFrom(Entity entity, CallbackInfo info) {
        if (this instanceof EntityPhysicsElement && entity instanceof EntityPhysicsElement) {
            info.cancel();
        }
    }

    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    public void move(MovementType type, Vec3d movement, CallbackInfo info) {
        if (this instanceof EntityPhysicsElement) {
            this.adjustMovementForCollisions(movement);
            info.cancel();
        }
    }

//    @Inject(method = "getStandingEyeHeight", at = @At("HEAD"), cancellable = true)
//    public void getStandingEyeHeight(CallbackInfoReturnable<Float> info) {
//        if (this instanceof EntityPhysicsElement) {
//            info.setReturnValue(0.0f);
//        }
//    }

    @Inject(method = "toTag", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;writeCustomDataToTag(Lnet/minecraft/nbt/CompoundTag;)V"))
    public void toTag(CompoundTag tag, CallbackInfoReturnable<CompoundTag> info) {
        if (this instanceof EntityPhysicsElement) {
            ElementRigidBody rigidBody = ((EntityPhysicsElement) this).getRigidBody();
            tag.put("orientation", QuaternionHelper.toTag(rigidBody.getPhysicsRotation(new Quaternion())));
            tag.put("linear_velocity", VectorHelper.toTag(rigidBody.getLinearVelocity(new Vector3f())));
            tag.put("angular_velocity", VectorHelper.toTag(rigidBody.getAngularVelocity(new Vector3f())));
        }
    }

    @Inject(method = "fromTag", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;readCustomDataFromTag(Lnet/minecraft/nbt/CompoundTag;)V"))
    public void fromTag(CompoundTag tag, CallbackInfo info) {
        if (this instanceof EntityPhysicsElement) {
            ElementRigidBody rigidBody = ((EntityPhysicsElement) this).getRigidBody();
            rigidBody.setPhysicsRotation(QuaternionHelper.fromTag(tag.getCompound("orientation")));
            rigidBody.setLinearVelocity(VectorHelper.fromTag(tag.getCompound("linear_velocity")));
            rigidBody.setAngularVelocity(VectorHelper.fromTag(tag.getCompound("angular_velocity")));
        }
    }
}
