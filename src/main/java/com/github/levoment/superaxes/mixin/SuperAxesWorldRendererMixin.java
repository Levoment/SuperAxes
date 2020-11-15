package com.github.levoment.superaxes.mixin;

import com.github.levoment.superaxes.Items.SuperAxeItem;
import com.github.levoment.superaxes.SuperAxesMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**************************************************************************************************************************/
/**                                            Credits to Draylar                                                         **/
/**                                          Base code used as reference                                                  **/
/**                             to achieve an outline box rendering of blocks to break in this mod                        **/
/**     https://github.com/Draylar/magna/blob/master/src/main/java/draylar/magna/mixin/WorldRendererMixin.java            **/
/**                                Code used as reference is under License: CC0-1.0                                       **/
/**                                         as shown on the Readme.md on                                                  **/
/**                   https://github.com/Draylar/magna/tree/619014b7404d7f509f2d0e0aadcf51aadd7a7f1a                      **/
/***************************************************************************************************************************/
@Mixin(WorldRenderer.class)
@Environment(EnvType.CLIENT)
public class SuperAxesWorldRendererMixin {
    @Shadow
    @Final
    private MinecraftClient client;
    @Shadow
    private double lastCameraX;
    @Shadow
    private double lastCameraY;
    @Shadow
    private double lastCameraZ;

    @Shadow
    private ClientWorld world;

    @Inject(at = @At("HEAD"), method = "drawBlockOutline", cancellable = true)
    private void drawBlockOutline(MatrixStack stack, VertexConsumer vertexConsumer, Entity entity, double d, double e, double f, BlockPos blockPos, BlockState blockState, CallbackInfo callbackInfo) {
        // If the player is null, return
        if (this.client.player == null) return;
        // If the world is null, return
        if (this.client.world == null) {
            return;
        }
        // If the show debug lines configuration is not set to true, return
        if (!SuperAxesMod.showDebugLines) {
            return;
        }

        // Get the current block in the crosshair
        boolean currentBlockIsALog = false;
        // If the crosshair target is an instance of BlockHitResult
        if (client.crosshairTarget instanceof BlockHitResult) {
            // If the target block is in the LOGS tags
            if (world.getBlockState(((BlockHitResult) client.crosshairTarget).getBlockPos()).isIn(BlockTags.LOGS)) {
                // Set that the current block is a log
                currentBlockIsALog = true;
            } else {
                // Current block targeted is not in the LOGS tag
                return;
            }
        } else {
            // Current block targeted is not of type BlockHitResult
            return;
        }

        // Get the player item in their main hand
        ItemStack playerMainHandStack = this.client.player.inventory.getMainHandStack();
        // Create a variable to hold the SuperAxeItem
        SuperAxeItem superAxeItem = null;
        // If the player is holding a SuperAxe
        if (playerMainHandStack.getItem() instanceof SuperAxeItem) {
            // Set the SuperAxeItem
            superAxeItem = (SuperAxeItem) playerMainHandStack.getItem();
        }
        // If the player is holding a SuperAxe and  the block they are looking at is a Log
        if (superAxeItem != null && currentBlockIsALog) {
            // If the variable to render boxes is set to true
            if (superAxeItem.shouldRenderBoxes()) {
                // Get the BlockHitResult that the player is targeting
                BlockHitResult crosshairTarget = superAxeItem.getBlockHitResult();
                // Get the position of the block the player is targeting
                BlockPos crosshairPos = crosshairTarget.getBlockPos();
                // Ensure we are not looking at an invalid block
                if (client.world.getWorldBorder().contains(crosshairPos)) {
                    // Get the positions of the blocks that would be broken if the player
                    // uses the SuperAxeItem to mine the log they were looking at when they activated the debug lines to be shown
                    BlockingQueue<BlockPos> positions = superAxeItem.getTreeChopper().getFinalQueueOfBlocksToBreak();
                    List<VoxelShape> outlineShapes = new ArrayList<>();
                    outlineShapes.add(VoxelShapes.empty());

                    // Assemble outline shape
                    for (BlockPos position : positions) {
                        BlockPos diffPos = position.subtract(crosshairPos);
                        BlockState offsetShape = world.getBlockState(position);
                        // If the block is not air
                        if (!offsetShape.isAir()) {
                            outlineShapes.add(offsetShape.getOutlineShape(world, position).offset(diffPos.getX(), diffPos.getY(), diffPos.getZ()));
                        } else {
                            outlineShapes.add(VoxelShapes.fullCube().offset(diffPos.getX(), diffPos.getY(), diffPos.getZ()));
                        }
                    }
                    outlineShapes.forEach(shape -> {
                        // Draw extended hitbox
                        SuperAxesWorldRendererMixin.drawShapeOutline(
                                stack,
                                vertexConsumer,
                                shape,
                                (double) crosshairPos.getX() - lastCameraX,
                                (double) crosshairPos.getY() - lastCameraY,
                                (double) crosshairPos.getZ() - lastCameraZ,
                                255 / 255.0F,
                                0 / 255.0F,
                                0 / 255.0F,
                                1.0F);
                    });
                }
                // Cancel 1x1 hitbox that would normally render
                callbackInfo.cancel();
            }
        }
    }

    @Invoker("drawShapeOutline")
    public static void drawShapeOutline(MatrixStack matrixStack, VertexConsumer vertexConsumer, VoxelShape voxelShape, double d, double e, double f, float g, float h, float i, float j) {
        throw new AssertionError();
    }
}
