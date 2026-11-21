package com.arrowstrike;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class StrikeItem extends Item {
    protected StrikeItem(Item.Settings settings) {
        super(settings);
    }

    protected abstract void fire(ServerWorld world, PlayerEntity user, Vec3d target);

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        // Client side: just swing. All real logic runs on the server only.
        if (!(world instanceof ServerWorld serverWorld)) {
            return ActionResult.SUCCESS;
        }

        ItemStack stack = user.getStackInHand(hand);
        if (stack.isEmpty() || !stack.isOf(this)) {
            return ActionResult.PASS;
        }

        Vec3d target = StrikeHelper.findTarget(serverWorld, user);
        if (target == null) {
            user.sendMessage(Text.literal("No target in range."), true);
            return ActionResult.FAIL;
        }

        if (!StrikeHelper.claim(serverWorld, user)) {
            return ActionResult.FAIL;
        }

        stack.decrement(1); // consume before firing so it can never double-trigger
        fire(serverWorld, user, target);
        return ActionResult.SUCCESS;
    }
}
// TODO: implement StrikeItem
