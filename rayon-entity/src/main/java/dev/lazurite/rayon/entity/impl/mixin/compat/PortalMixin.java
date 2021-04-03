package dev.lazurite.rayon.entity.impl.mixin.compat;

import com.jme3.math.Vector3f;
import com.qouteall.immersive_portals.portal.Portal;
import dev.lazurite.rayon.core.impl.physics.space.body.ElementRigidBody;
import dev.lazurite.rayon.core.impl.util.math.VectorHelper;
import dev.lazurite.rayon.entity.api.EntityPhysicsElement;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Quaternion;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Portal.class)
public class PortalMixin {
    @Shadow @Nullable public Quaternion rotation;

    @Inject(method = "transformVelocity", at = @At("HEAD"))
    public void transformVelocity(Entity entity, CallbackInfo info) {
        if (entity instanceof EntityPhysicsElement) {
            ElementRigidBody rigidBody = ((EntityPhysicsElement) entity).getRigidBody();
            net.minecraft.client.util.math.Vector3f velocity = VectorHelper.bulletToMinecraft(rigidBody.getLinearVelocity(new Vector3f()));

            if (rotation != null) {
                velocity.rotate(rotation);
            }

            rigidBody.setLinearVelocity(VectorHelper.minecraftToBullet(velocity));
        }
    }
}
