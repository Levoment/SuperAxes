package com.github.levoment.superaxes;

import com.github.levoment.superaxes.Items.ModItems;
import com.github.levoment.superaxes.Items.SuperAxeItem;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class TreeChopper {

    // Variable to tell if the first block was broken. Used to damage the axe for that first block that was broken
    private boolean firstBlockBroken = false;
    // Variable to tell if the axe broke while chopping the tree. Used to keep chopping the tree down even if the axe broke in the process
    // It prevents trees from being left floating in the air
    private boolean axeBroken = false;

    // Variables for creating an initial queue of log blocks to break
    private BlockingQueue<BlockPos> initialQueueOfBlocksToBreak = new LinkedBlockingDeque<>();
    private ConcurrentLinkedQueue<BlockPos> verificationQueueOfBlocksToBreak = new ConcurrentLinkedQueue<>();

    // Variables for creating an initial queue of leaves to break
    private BlockingQueue<BlockPos> initialQueueOfLeavesToBreak = new LinkedBlockingDeque<>();
    private ConcurrentLinkedQueue<BlockPos> verificationQueueOfLeavesToBreak = new ConcurrentLinkedQueue<>();

    // Variables for storing all the logs and leaves that will be attempted to harvest once all positions of the blocks have been gathered
    private BlockingQueue<BlockPos> finalQueueOfBlocksToBreak = new LinkedBlockingDeque<>();
    private BlockingQueue<BlockPos> finalQueueOfLeavesToBreak = new LinkedBlockingDeque<>();


    // The original block position of the initial block that was mined
    private BlockPos originalPosition;

    // The shape size to scan for logs
    private int shapeSize = 1;


    // Assume first block is a log
    boolean CurrentIsLog = true;


    public void cutTree(World world, BlockPos pos, PlayerEntity miner, ItemStack itemStack) {

        // Set the original position
        this.originalPosition = pos;

        // Set the shape size
        shapeSize = Math.abs(SuperAxesMod.shapeScale);

        // Fill a queue with positions in a 9x9 block around the broken block
        fillConcurrentQueue(world, pos);

        // This call traverses the tree in parallel for the current items in the queues initialQueueOfBlocksToBreak and initialQueueOfLeavesToBreak to look for more logs or leaves to break
        // It also breaks the logs first and then traverses through the leaves and breaks them
        parallelTraverseTreeAndBreakBlocks(world, miner, itemStack);

    }

    public void scanTree(World world, BlockPos pos) {
        // Set the original position
        this.originalPosition = pos;

        // Set the shape size
        shapeSize = Math.abs(SuperAxesMod.shapeScale);

        // Fill a queue with positions in a 9x9 block around the broken block
        fillConcurrentQueue(world, pos);

        // This call traverses the tree in parallel for the current items in the queues initialQueueOfBlocksToBreak and initialQueueOfLeavesToBreak to look for more logs or leaves to break
        parallelTraverseTree(world);
    }

    public void parallelTraverseTree(World world) {
        // Fill the list of logs to break
        while (!initialQueueOfBlocksToBreak.isEmpty()) {
            // Scan for adjacent blocks in parallel
            initialQueueOfBlocksToBreak.parallelStream().forEach(blockPos ->
            {
                // For the current block, scan for blocks in a 9x9 area around it
                fillConcurrentQueue(world, blockPos);
                try {
                    // Add the current position to the definitive queue of block positions containing the log blocks to break
                    finalQueueOfBlocksToBreak.put(blockPos);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // Block position was processed, remove it from the queue
                initialQueueOfBlocksToBreak.remove(blockPos);
            });
        }
    }

    public void fillConcurrentQueue(World world, BlockPos pos) {
        for (int y = -(shapeSize); y <= shapeSize; y++) {
            for (int x = -(shapeSize); x <= shapeSize; x++) {
                for (int z = -shapeSize; z <= shapeSize; z++) {
                    BlockPos newPosition = pos.add(x, y, z);
                    // Check if we don't want to harvest leaves
                    if (!SuperAxesMod.harvestLeaves) {
                        if (SuperAxesMod.limitSearch) {
                            if (Math.abs(newPosition.getX() - originalPosition.getX()) < SuperAxesMod.logRadius && Math.abs(newPosition.getZ() - newPosition.getZ()) < SuperAxesMod.logRadius) {
                                // Check if the blockstate is not null, the block is not null, and if the block is a log coming from another log, should alleviate most leaf->2nd tree issues
                                if (world.getBlockState(newPosition) != null && world.getBlockState(newPosition).getBlock() != null && (((world.getBlockState(newPosition).isIn(BlockTags.LOGS))))) {
                                    // Check that the block was not previously added to the stack
                                    if (!verificationQueueOfBlocksToBreak.contains(newPosition)) {
                                        // Add the block to the verification queue of blocks to break and also add it to the queue of blocks to break to continue searching for blocks to break
                                        initialQueueOfBlocksToBreak.add(newPosition);
                                        verificationQueueOfBlocksToBreak.add(newPosition);
                                    }
                                }
                            }
                        } else {
                            // Check if the blockstate is not null, the block is not null, and if the block is a log coming from another log, should alleviate most leaf->2nd tree issues
                            if (world.getBlockState(newPosition) != null && world.getBlockState(newPosition).getBlock() != null && ((world.getBlockState(newPosition).isIn(BlockTags.LOGS)))) {
                                // Check that the block was not previously added to the stack
                                if (!verificationQueueOfBlocksToBreak.contains(newPosition)) {
                                    // Add the block to the verification queue of blocks to break and also add it to the queue of blocks to break to continue searching for blocks to break
                                    initialQueueOfBlocksToBreak.add(newPosition);
                                    verificationQueueOfBlocksToBreak.add(newPosition);
                                }
                            }
                        }
                    } else {
                        // We want to harvest leaves
                        // Since we want to harvest leaves, let's add the leaves and the logs to search for
                        // Check if the block blockstate is not null and if the block is not null
                        if (world.getBlockState(newPosition) != null && world.getBlockState(newPosition).getBlock() != null) {
                            if (SuperAxesMod.limitSearch) {
                                if (Math.abs(newPosition.getX() - originalPosition.getX()) < SuperAxesMod.logRadius && Math.abs(newPosition.getZ() - newPosition.getZ()) < SuperAxesMod.logRadius) {
                                    // If it is a log add it to the list of logs to break
                                    if (world.getBlockState(newPosition).isIn(BlockTags.LOGS)) {
                                        if (!verificationQueueOfBlocksToBreak.contains(newPosition)) {
                                            initialQueueOfBlocksToBreak.add(newPosition);
                                            verificationQueueOfBlocksToBreak.add(newPosition);
                                        }
                                    }
                                    // If it is a leave add it to the list of leaves to break
                                    else if (world.getBlockState(newPosition).isIn(BlockTags.LEAVES)) {
                                        // Check if we are within the configured range
                                        if (Math.abs(newPosition.getX() - originalPosition.getX()) < SuperAxesMod.range && Math.abs(newPosition.getZ() - newPosition.getZ()) < SuperAxesMod.range) {
                                            if (!verificationQueueOfLeavesToBreak.contains(newPosition)) {
                                                verificationQueueOfLeavesToBreak.add(newPosition);
                                                initialQueueOfLeavesToBreak.add(newPosition);
                                            }
                                        }
                                    }
                                }
                            } else {
                                // If it is a log add it to the list of logs to break
                                if (world.getBlockState(newPosition).isIn(BlockTags.LOGS)) {
                                    if (!verificationQueueOfBlocksToBreak.contains(newPosition)) {
                                        initialQueueOfBlocksToBreak.add(newPosition);
                                        verificationQueueOfBlocksToBreak.add(newPosition);
                                    }
                                }
                                // If it is a leave add it to the list of leaves to break
                                else if (world.getBlockState(newPosition).isIn(BlockTags.LEAVES)) {
                                    // Check if we are within the configured range
                                    if (Math.abs(newPosition.getX() - originalPosition.getX()) < SuperAxesMod.range && Math.abs(newPosition.getZ() - newPosition.getZ()) < SuperAxesMod.range) {
                                        if (!verificationQueueOfLeavesToBreak.contains(newPosition)) {
                                            verificationQueueOfLeavesToBreak.add(newPosition);
                                            initialQueueOfLeavesToBreak.add(newPosition);
                                        }
                                    }
                                }
                            }



                        }
                    }
                }
            }
        }
    }



    public void parallelTraverseTreeAndBreakBlocks(World world, PlayerEntity miner, ItemStack itemStack) {
        // Fill the list of logs to break
        while (!initialQueueOfBlocksToBreak.isEmpty()) {
            // Scan for adjacent blocks in parallel
            initialQueueOfBlocksToBreak.parallelStream().forEach(blockPos ->
            {
                // For the current block, scan for blocks in a 9x9 area around it
                fillConcurrentQueue(world, blockPos);
                try {
                    // Add the current position to the definitive queue of block positions containing the log blocks to break
                    finalQueueOfBlocksToBreak.put(blockPos);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // Block position was processed, remove it from the queue
                initialQueueOfBlocksToBreak.remove(blockPos);
            });
        }

        // If the item stack is an instance of SuperAxeItem
        if (itemStack.getItem() instanceof SuperAxeItem) {
            // Disable rendering of blocks to chop down
            ((SuperAxeItem)itemStack.getItem()).setShouldRenderBoxes(false);
        }

        // While the queue of logs to break is not empty
        while (!finalQueueOfBlocksToBreak.isEmpty()) {
            try {
                // Extract one element from the queue containing the log position
                BlockPos positionToBreak = finalQueueOfBlocksToBreak.take();
                // Pass the element to the method handling the logic on whether or not to break the log
                breakLogs(world, positionToBreak, miner, itemStack);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Check if the user has the setting to harvest leaves
        if (SuperAxesMod.harvestLeaves) {
            // Wait half a second after breaking blocks to get updated leaves status
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {

            }

            // While the queue of leaves to break is not empty
            while (!initialQueueOfLeavesToBreak.isEmpty()) {
                // Scans for adjacent blocks in parallel
                initialQueueOfLeavesToBreak.parallelStream().forEach(blockPos -> {
                    // For the current block, scan for blocks in a 9x9 area around it
                    fillConcurrentQueue(world, blockPos);
                    try {
                        // Add the current position to the definitive queue of block positions containing the leaves blocks to break
                        finalQueueOfLeavesToBreak.put(blockPos);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // Block position was processed, remove it from the queue
                    initialQueueOfLeavesToBreak.remove(blockPos);
                });
            }

            // While the queue of leaves to break is not empty
            while (!finalQueueOfLeavesToBreak.isEmpty()) {
                try {
                    // Extract one element from the queue
                    BlockPos blockPos = finalQueueOfLeavesToBreak.take();
                    // Pass the element to the method handling the logic on whether or not to break the leaf
                    breakLeaves(world, blockPos, miner, itemStack);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void breakLogs(World world, BlockPos blockPos, PlayerEntity miner, ItemStack itemStack) {
        // Check if player can edit block
        if (world.canPlayerModifyAt(miner, blockPos)) {
            boolean isItemSuperAxe = miner.getMainHandStack().getItem() instanceof SuperAxeItem;
            // Check if the item is in the player's main hand or if it broke while chopping the tree
            if (isItemSuperAxe || axeBroken) {
                // Check if the item is about to break
                if (miner.getMainHandStack().getMaxDamage() - miner.getMainHandStack().getDamage() == 1) axeBroken = true;
                // Check if the superaxe hasn't broken or if the game mode is creative
                if (miner.getMainHandStack().getDamage() > 0 || Objects.requireNonNull(world.getServer()).getDefaultGameMode().isCreative()) {
                    if (!firstBlockBroken) {
                        // Set that the first block is already broken as it will be broken
                        this.firstBlockBroken = true;
                    }
                }
                BlockState logBlockState = world.getBlockState(blockPos);
                if (logBlockState.isIn(BlockTags.LOGS)) {
                    if (!world.isClient()) {
                        // Harvest the block
                        ((SuperAxeItem) itemStack.getItem()).mineBlockWithLootContext(logBlockState, (ServerWorld) world, blockPos, miner, firstBlockBroken);
                    }
                }
            }
        }
    }

    public void breakLeaves(World world, BlockPos blockPos, PlayerEntity miner, ItemStack itemStack) {
        // Iterate through the list containing the positions of the blocks we want to break
        // Now break all of the leaves
        // Check if player can edit block
        if (world.canPlayerModifyAt(miner, blockPos)) {
            ModItems.mapOfIdentifiers.forEach((identifier, item) -> {
                // Check if the item is in the player's main hand or if it broke while chopping the tree
                if (Registry.ITEM.get(identifier).equals(miner.getMainHandStack().getItem()) || axeBroken) {
                    // Check if the item is about to break
                    if (miner.getMainHandStack().getMaxDamage() - miner.getMainHandStack().getDamage() == 1) axeBroken = true;
                    // Check if the superaxe hasn't broken
                    if (miner.getMainHandStack().getDamage() > 0) {
                        // Damage superaxe for each block that is broken
                        if (!firstBlockBroken) {
                            this.firstBlockBroken = true;
                        }
                    }
                    // Check if leaves are an instance of LeavesBlock
                    if (world.getBlockState(blockPos).isIn(BlockTags.LEAVES)  && world.getBlockState(blockPos).getBlock() instanceof LeavesBlock) {
                        try {
                            BlockState leafBlockState = world.getBlockState(blockPos);
                            // Check if the leaves are a Distance of 7 from a log
                            if (leafBlockState.get(LeavesBlock.DISTANCE) == 7) {
                                // Harvest the block
                                if (!world.isClient())
                                    ((SuperAxeItem) itemStack.getItem()).mineLeaves(leafBlockState, (ServerWorld) world, blockPos, miner, firstBlockBroken);
                            }
                        } catch (IllegalArgumentException illegalArgumentException) {
                            // Don't do anything. Sometimes leaves are dropped before we harvest them
                            // In those cases a block of air is left which doesn't have a DISTANCE property.
                            // That will throw an exception. We can safely ignore the exception and harvest the actual leaves
                        }
                    }
                }
            });
        }
    }

    public BlockingQueue<BlockPos> getFinalQueueOfBlocksToBreak() {
        return finalQueueOfBlocksToBreak;
    }
}
