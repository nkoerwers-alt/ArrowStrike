package com.arrowstrike;

import java.util.concurrent.ThreadLocalRandom;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class ArrowStabItem extends StrikeItem {
    private static final int ARROWS = 20;
    private static final double SPEED = 10.0;     // blocks per tick
    private static final double DAMAGE = 500.0;   // arrow damage = speed * this
    private static final double HEIGHT = 120.0;
    private static final double SPREAD = 0.15;

    public ArrowStabItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    protected void fire(ServerWorld world, PlayerEntity user, Vec3d target) {
        double launchY = StrikeHelper.findLaunchY(world, user, target, HEIGHT);
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        Vec3d velocity = new Vec3d(0.0, -SPEED, 0.0);

        for (int i = 0; i < ARROWS; i++) {
            double x = target.x + (rng.nextDouble() * 2.0 - 1.0) * SPREAD;
            double z = target.z + (rng.nextDouble() * 2.0 - 1.0) * SPREAD;
            StrikeHelper.spawnArrow(world, user, x, launchY, z, velocity, DAMAGE);
        }
    }
}
// TODO: implement ArrowStabItem
