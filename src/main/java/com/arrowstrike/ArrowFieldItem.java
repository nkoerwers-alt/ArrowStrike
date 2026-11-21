package com.arrowstrike;

import java.util.concurrent.ThreadLocalRandom;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class ArrowFieldItem extends StrikeItem {
    private static final int ARROWS = 250;
    private static final double RADIUS = 15.0;
    private static final double SPEED = 8.0;      // blocks per tick
    private static final double DAMAGE = 8.0;     // about 64 raw damage per arrow
    private static final double HEIGHT = 80.0;

    public ArrowFieldItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    protected void fire(ServerWorld world, PlayerEntity user, Vec3d target) {
        double launchY = StrikeHelper.findLaunchY(world, user, target, HEIGHT);
        ThreadLocalRandom rng = ThreadLocalRandom.current();

        for (int i = 0; i < ARROWS; i++) {
            double angle = rng.nextDouble() * Math.PI * 2.0;
            double dist = Math.sqrt(rng.nextDouble()) * RADIUS;
            double x = target.x + Math.cos(angle) * dist;
            double z = target.z + Math.sin(angle) * dist;
            Vec3d velocity = new Vec3d(
                    (rng.nextDouble() - 0.5) * 0.1,
                    -SPEED,
                    (rng.nextDouble() - 0.5) * 0.1);
            StrikeHelper.spawnArrow(world, user, x, launchY, z, velocity, DAMAGE);
        }
    }
}
// TODO: implement ArrowFieldItem
