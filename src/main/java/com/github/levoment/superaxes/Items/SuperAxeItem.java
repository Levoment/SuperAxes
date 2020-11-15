package com.github.levoment.superaxes.Items;

import com.github.levoment.superaxes.SuperAxesMaterialGenerator;
import com.github.levoment.superaxes.SuperAxesMod;
import com.github.levoment.superaxes.TreeChopper;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.ToolMaterial;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;

public class SuperAxeItem extends AxeItem {

    private boolean renderBoxes = false;

    // Create a new TreeChopper instance
    private TreeChopper treeChopper;

    // Target block
    private BlockHitResult blockHitResult;

    // Constructor
    public SuperAxeItem(ToolMaterial material, Settings settings) {
        super(material, ((SuperAxesMaterialGenerator)material).getAxeAttackDamage(), ((SuperAxesMaterialGenerator) material).getAxeAttackSpeed(), settings);
    }


    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        if(!world.isClient()) {
            // Check if the player is sneaking. If Sneaking, mine as normal
            if (miner.isSneaking()) return super.canMine(state, world, pos, miner);
            // Check if the tool is effective on the block and check for the LOGS tag
            if (state.isIn(BlockTags.LOGS)) {
                // Create an instance of TreeChopper
                TreeChopper treeChopper = new TreeChopper();
                // Create a new thread for chopping the tree
                new Thread(() -> treeChopper.cutTree(world, pos, miner, miner.getMainHandStack())).start();
            }
        }
        return true;
    }

    public void mineLeaves(BlockState leafBlockState, ServerWorld serverWorld, BlockPos pos, PlayerEntity miner, boolean firstBlockBroken)
    {
        if (leafBlockState.isIn(BlockTags.LEAVES))
        {
            // Set the loot context for mining the leaf block
            LootContext.Builder builder = (new LootContext.Builder(serverWorld)).random(serverWorld.random).luck(miner.getLuck()).optionalParameter(LootContextParameters.ORIGIN, new Vec3d(pos.getX(), pos.getY(), pos.getZ())).optionalParameter(LootContextParameters.TOOL, miner.getMainHandStack()).optionalParameter(LootContextParameters.THIS_ENTITY, miner);
            // Get a list of drops if the tool is used to harvest the block
            List<ItemStack> listOfDroppedStacks = leafBlockState.getDroppedStacks(builder);
            listOfDroppedStacks.forEach(itemStack -> {
                // Drop the item on the world
                ItemScatterer.spawn(serverWorld, pos.getX(), pos.getY(), pos.getZ(), itemStack);
            });
            if (miner.getMainHandStack().getItem() instanceof SuperAxeItem) {
                // Damage superaxe for each block that is broken
                if (firstBlockBroken) {
                    miner.getMainHandStack().postMine(serverWorld, serverWorld.getBlockState(pos), pos, miner);
                    // Break the block
                    serverWorld.breakBlock(pos, false, miner);
                }
            }
        }
    }

    public void mineBlockWithLootContext(BlockState leafBlockState, ServerWorld serverWorld, BlockPos pos, PlayerEntity miner, boolean firstBlockBroken)
    {
            // Set the loot context for mining the leaf block
            LootContext.Builder builder = (new LootContext.Builder(serverWorld)).random(serverWorld.random).luck(miner.getLuck()).optionalParameter(LootContextParameters.ORIGIN, new Vec3d(pos.getX(), pos.getY(), pos.getZ())).optionalParameter(LootContextParameters.TOOL, miner.getMainHandStack()).optionalParameter(LootContextParameters.THIS_ENTITY, miner);
            // Get a list of drops if the tool is used to harvest the block
            List<ItemStack> listOfDroppedStacks = leafBlockState.getDroppedStacks(builder);
            listOfDroppedStacks.forEach(itemStack -> {
                // Drop the item on the world
                ItemScatterer.spawn(serverWorld, pos.getX(), pos.getY(), pos.getZ(), itemStack);
            });

        if (miner.getMainHandStack().getItem() instanceof SuperAxeItem) {
            // Damage superaxe for each block that is broken
            if (firstBlockBroken) {
                miner.getMainHandStack().postMine(serverWorld, serverWorld.getBlockState(pos), pos, miner);
                // Break the block
                serverWorld.breakBlock(pos, false, miner);
            }
        }

    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        // Get whether the player is sneaking or not
        boolean isPlayerSneaking;
        isPlayerSneaking = Objects.requireNonNull(context.getPlayer()).isSneaking();
        if (context.getWorld().isClient() && context.getWorld().getBlockState(context.getBlockPos()).isIn(BlockTags.LOGS)) {
            // If the player is sneaking and the render boxes has not been set and the configuration has show debug lines set
            if (isPlayerSneaking && !this.renderBoxes && SuperAxesMod.showDebugLines) {
                if (MinecraftClient.getInstance().crosshairTarget instanceof BlockHitResult) {
                    // Create the tree chopper instance
                    this.treeChopper = new TreeChopper();
                    // Scan for the tree
                    this.treeChopper.scanTree(context.getWorld(), context.getBlockPos());
                    // Set a variable to render boxes
                    this.renderBoxes = true;
                    this.blockHitResult = (BlockHitResult) MinecraftClient.getInstance().crosshairTarget;
                    return ActionResult.CONSUME;
                }
            } else if (context.getPlayer().isSneaking() && this.renderBoxes) {
                // Set a variable to stop rendering boxes
                this.renderBoxes = false;
                return ActionResult.CONSUME;
            }
        } else if (context.getWorld().isClient() && !context.getWorld().getBlockState(context.getBlockPos()).isIn(BlockTags.LOGS)) {
            this.renderBoxes = false;
        }

        // If the player is sneaking and this part of the code was reached
        if (isPlayerSneaking) {
            return ActionResult.SUCCESS;
        } else {
            return super.useOnBlock(context);
        }
    }

    public BlockHitResult getBlockHitResult() {
        return blockHitResult;
    }

    public TreeChopper getTreeChopper() {
        return treeChopper;
    }

    public boolean shouldRenderBoxes() {
        return renderBoxes;
    }

    public void setShouldRenderBoxes(boolean renderBoxes) {
        this.renderBoxes = renderBoxes;
    }
}
