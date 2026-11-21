package com.arrowstrike;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public final class StrikeHelper {
    public static final double RANGE = 160.0;
    private static final long MIN_TICKS_BETWEEN_USES = 20L;
    private static final Map<UUID, Long> LAST_USE = new HashMap<>();

    private StrikeHelper() {
    }

    /** Returns false if this player already activated a strike in the last second (duplicate guard). */
    public static boolean claim(ServerWorld world, PlayerEntity player) {
        long now = world.getTime();
        Long last = LAST_USE.get(player.getUuid());
        if (last != null && now >= last && now - last < MIN_TICKS_BETWEEN_USES) {
            return false;
        }
        LAST_USE.put(player.getUuid(), now);
        return true;
    }

    /** Finds the point the player is aiming at (block or entity). Null if nothing within range. */
    public static Vec3d findTarget(ServerWorld world, PlayerEntity player) {
        Vec3d start = player.getEyePos();
        Vec3d look = player.getRotationVector();
        Vec3d end = start.add(look.multiply(RANGE));

        Vec3d best = null;
        double bestDist = RANGE * RANGE;

        BlockHitResult blockHit = world.raycast(new RaycastContext(
                start, end,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                player));
        if (blockHit.getType() != HitResult.Type.MISS) {
            best = blockHit.getPos();
            bestDist = start.squaredDistanceTo(best);
        }

        Box search = player.getBoundingBox().stretch(look.multiply(RANGE)).expand(1.0);
        for (Entity entity : world.getOtherEntities(player, search,
                e -> e instanceof LivingEntity && e.isAlive() && !e.isSpectator())) {
            Optional<Vec3d> hit = entity.getBoundingBox().expand(0.3).raycast(start, end);
            if (hit.isPresent()) {
                double d = start.squaredDistanceTo(hit.get());
                if (d < bestDist) {
                    bestDist = d;
                    best = hit.get();
                }
            }
        }
        return best;
    }

    /** Highest open Y above the target (up to desiredHeight above it, never above the world limit). */
    public static double findLaunchY(ServerWorld world, PlayerEntity player, Vec3d target, double desiredHeight) {
        double worldTop = world.getBottomY() + world.getHeight() - 1;
        double maxY = Math.min(target.y + desiredHeight, worldTop);
        Vec3d from = new Vec3d(target.x, target.y + 0.6, target.z);
        if (maxY - from.y < 2.0) {
            return from.y + 1.0;
        }
        Vec3d to = new Vec3d(target.x, maxY, target.z);
        BlockHitResult up = world.raycast(new RaycastContext(
                from, to,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                player));
        if (up.getType() != HitResult.Type.MISS) {
            return Math.max(from.y + 1.0, up.getPos().y - 1.0);
        }
        return maxY;
    }

    /** Spawns one real arrow entity with the given velocity and damage multiplier. */
    public static void spawnArrow(ServerWorld world, PlayerEntity owner,
                                  double x, double y, double z,
                                  Vec3d velocity, double damage) {
        ArrowEntity arrow = new ArrowEntity(world, x, y, z, new ItemStack(Items.ARROW), null);
        arrow.setOwner(owner);
        arrow.setNoGravity(true);
        arrow.setDamage(damage);
        arrow.pickupType = PersistentProjectileEntity.PickupPermission.DISALLOWED;
        arrow.setVelocity(velocity);
        world.spawnEntity(arrow);
    }
}
