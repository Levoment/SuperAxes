package com.github.levoment.superaxes.mixin;

import com.github.levoment.superaxes.Items.SuperAxeItem;
import com.github.levoment.superaxes.SuperAxesMod;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.lwjgl.opengl.GL11;
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

/**************************************************************************************************************************/
/**                                            Credits to Pepper                                                         **/
/**                                 For providing the code that renders a box over                                       **/
/**                                            the passed blocks                                                         **/
/**                                      https://github.com/PepperCode1                                                  **/
/**************************************************************************************************************************/

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

    private static BufferBuilder buffer = new BufferBuilder(256);

    @Inject(at = @At("HEAD"), method = "drawBlockOutline", cancellable = true)
    private void drawBlockOutline(MatrixStack stack, VertexConsumer vertexConsumer, Entity entity, double d, double e, double f, BlockPos blockPos, BlockState blockState, CallbackInfo callbackInfo) {

        // If the player is null, return
        if (this.client.player == null) return;
        // If the world is null, return
        if (this.client.world == null) {
            return;
        }
        // If the show debug lines or show debug highlight configuration is not set to true, return
//        if (!(SuperAxesMod.showDebugLines || SuperAxesMod.showDebugHighlight)) {
//            return;
//        }

        // Get the player item in their main hand
        ItemStack playerMainHandStack = ((ClientPlayerEntity)entity).getMainHandStack();
        // Create a variable to hold the SuperAxeItem
        SuperAxeItem superAxeItem = null;
        // If the player is holding a SuperAxe
        if (playerMainHandStack.getItem() instanceof SuperAxeItem) {
            // Set the SuperAxeItem
            superAxeItem = (SuperAxeItem) playerMainHandStack.getItem();
        }

        // If the player is holding a SuperAxe
        if (superAxeItem != null) {
            // If the variable to render boxes is set to true
            if (superAxeItem.shouldRenderBoxes()) {
                // Get the block position that was originally targeted
                BlockPos crosshairPos = superAxeItem.getTargetBlockPos();
                // Ensure we are not looking at an invalid block
                if (client.world.getWorldBorder().contains(crosshairPos)) {
                    // Get the positions of the blocks that would be broken if the player
                    // uses the SuperAxeItem to mine the log they were looking at when they activated the debug lines to be shown
                    BlockingQueue<BlockPos> positions = superAxeItem.getTreeChopper().getFinalQueueOfBlocksToBreak();

//                    if (SuperAxesMod.showDebugHighlight) {
//                        float newRed = 255 / 255.0F;
//                        float newGreen = 215 / 255.0F;
//                        float newBlue = 0 / 255.0F;
//                        float newAlpha = 0.4F;
//
//                        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
//
//                        stack.push();
//                        stack.translate(-d, -e, -f);
//                        Matrix4f matrix = stack.peek().getModel();
//                        // Assemble outline shape
//                        for (BlockPos position : positions) {
//                            drawVertices(buffer, matrix, client.world, position, newRed, newGreen, newBlue, newAlpha);
//                        }
//                        stack.pop();
//                        buffer.end();
//                        RenderSystem.disableTexture();
//                        RenderSystem.enableBlend();
//                        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
//                        RenderSystem.enableDepthTest();
//                        RenderSystem.depthFunc(GL11.GL_LEQUAL);
//                        RenderSystem.polygonOffset(0.0F, 0.0F);
//                        RenderSystem.enablePolygonOffset();
//                        RenderSystem.disableCull();
//                        BufferRenderer.draw(buffer);
//                    }

                    if (SuperAxesMod.showDebugLines) {
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
                                    (double) crosshairPos.getX() - d,
                                    (double) crosshairPos.getY() - e,
                                    (double) crosshairPos.getZ() - f,
                                    255 / 255.0F,
                                    0 / 255.0F,
                                    0 / 255.0F,
                                    1.0F);
                        });
                    }

                    //}
                    // Cancel 1x1 hitbox that would normally render
                    callbackInfo.cancel();
                }
            }
        }
    }

    @Invoker("drawShapeOutline")
    public static void drawShapeOutline(MatrixStack matrixStack, VertexConsumer vertexConsumer, VoxelShape voxelShape, double d, double e, double f, float g, float h, float i, float j) {
        throw new AssertionError();
    }

/**************************************************************************************************************************/
/**                                            Credits to Pepper                                                         **/
/**                                    For this code to render a box over                                                **/
/**                                            the passed blocks                                                         **/
/**                                      https://github.com/PepperCode1                                                  **/
/**************************************************************************************************************************/
    private static void drawVertices(BufferBuilder buffer, Matrix4f matrix, BlockView world, BlockPos pos, float red, float green, float blue, float alpha) {
        BlockState state = world.getBlockState(pos);
        try {
            Box box = state.getOutlineShape(world, pos).getBoundingBox();
            box = box.expand(0.01, 0.01, 0.01);

            float minX = (float) box.minX + pos.getX();
            float minY = (float) box.minY + pos.getY();
            float minZ = (float) box.minZ + pos.getZ();
            float maxX = (float) box.maxX + pos.getX();
            float maxY = (float) box.maxY + pos.getY();
            float maxZ = (float) box.maxZ + pos.getZ();

            if (Block.shouldDrawSide(state, world, pos, Direction.DOWN, pos)) {
                buffer.vertex(matrix, maxX, minY, minZ).color(red, green, blue, alpha).next();
                buffer.vertex(matrix, maxX, minY, maxZ).color(red, green, blue, alpha).next();
                buffer.vertex(matrix, minX, minY, maxZ).color(red, green, blue, alpha).next();
                buffer.vertex(matrix, minX, minY, minZ).color(red, green, blue, alpha).next();
            }
            if (Block.shouldDrawSide(state, world, pos, Direction.UP, pos)) {
                buffer.vertex(matrix, minX, maxY, maxZ).color(red, green, blue, alpha).next();
                buffer.vertex(matrix, maxX, maxY, maxZ).color(red, green, blue, alpha).next();
                buffer.vertex(matrix, maxX, maxY, minZ).color(red, green, blue, alpha).next();
                buffer.vertex(matrix, minX, maxY, minZ).color(red, green, blue, alpha).next();
            }
            if (Block.shouldDrawSide(state, world, pos, Direction.NORTH, pos)) {
                buffer.vertex(matrix, minX, maxY, minZ).color(red, green, blue, alpha).next();
                buffer.vertex(matrix, maxX, maxY, minZ).color(red, green, blue, alpha).next();
                buffer.vertex(matrix, maxX, minY, minZ).color(red, green, blue, alpha).next();
                buffer.vertex(matrix, minX, minY, minZ).color(red, green, blue, alpha).next();
            }
            if (Block.shouldDrawSide(state, world, pos, Direction.SOUTH, pos)) {
                buffer.vertex(matrix, minX, minY, maxZ).color(red, green, blue, alpha).next();
                buffer.vertex(matrix, maxX, minY, maxZ).color(red, green, blue, alpha).next();
                buffer.vertex(matrix, maxX, maxY, maxZ).color(red, green, blue,alpha).next();
                buffer.vertex(matrix, minX, maxY, maxZ).color(red, green, blue, alpha).next();
            }
            if (Block.shouldDrawSide(state, world, pos, Direction.WEST, pos)) {
                buffer.vertex(matrix, minX, minY, minZ).color(red, green, blue, alpha).next();
                buffer.vertex(matrix, minX, minY, maxZ).color(red, green, blue, alpha).next();
                buffer.vertex(matrix, minX, maxY, maxZ).color(red, green, blue, alpha).next();
                buffer.vertex(matrix, minX, maxY, minZ).color(red, green, blue, alpha).next();
            }
            if (Block.shouldDrawSide(state, world, pos, Direction.EAST, pos)) {
                buffer.vertex(matrix, maxX, maxY, minZ).color(red, green, blue, alpha).next();
                buffer.vertex(matrix, maxX, maxY, maxZ).color(red, green, blue, alpha).next();
                buffer.vertex(matrix, maxX, minY, maxZ).color(red, green, blue, alpha).next();
                buffer.vertex(matrix, maxX, minY, minZ).color(red, green, blue, alpha).next();
            }
        } catch (UnsupportedOperationException unsupportedOperationException) {

        }
    }
}