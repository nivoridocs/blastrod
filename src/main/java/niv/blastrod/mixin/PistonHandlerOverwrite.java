package niv.blastrod.mixin;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import com.google.common.collect.Lists;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import niv.blastrod.BlastrodMod;
import niv.blastrod.block.BlastrodBlock;
import niv.blastrod.util.QueueItem;

@Mixin(PistonHandler.class)
public abstract class PistonHandlerOverwrite {

	@Shadow
	private World world;

	@Shadow
	private BlockPos posFrom;

	@Shadow
	private BlockPos posTo;

	@Shadow
	private Direction motionDirection;

	@Shadow
	private List<BlockPos> movedBlocks;

	@Shadow
	private List<BlockPos> brokenBlocks;

	private final List<BlockPos> blastedBlocks = Lists.newArrayList();

	@Shadow
	private Direction pistonDirection;

	@Invoker("isBlockSticky")
	private static boolean isBlockSticky(Block block) {
		throw new AssertionError();
	}

	@Invoker("isAdjacentBlockStuck")
	private static boolean isAdjacentBlockStuck(Block block, Block block2) {
		throw new AssertionError();
	}

	@Overwrite
	public boolean calculatePush() {
		this.movedBlocks.clear();
		this.brokenBlocks.clear();
		this.blastedBlocks.clear();

		BlockState blockState;
		boolean result = true;

		// Breadth-first search
		Queue<QueueItem> queue = new LinkedList<>();
		QueueItem item = new QueueItem(this.posTo, this.pistonDirection);
		queue.offer(item);
		while (!queue.isEmpty()) {
			item = queue.poll();
			blockState = this.world.getBlockState(item.blockPos);

			if (blockState.isAir() || this.posFrom.equals(item.blockPos)
					|| this.movedBlocks.contains(item.blockPos)) {
				continue;

			} else if (tryMove(item.blockPos, blockState, item.motionSource)) {
				if (this.movedBlocks.size() + 1 > 12) {
					result = false;
					break;
				} else {
					this.movedBlocks.add(item.blockPos);
					onMove(queue, item.blockPos, blockState, item.motionSource);
				}

			} else if (tryBreak(item.blockPos, blockState, item.motionSource)) {
				this.brokenBlocks.add(item.blockPos);

			} else if (this.motionDirection == item.motionSource) {
				result = false;
				break;
			}
		}

		this.blastedBlocks.removeAll(this.movedBlocks);
		this.brokenBlocks.addAll(this.blastedBlocks);

		return result;
	}

	private boolean tryBreak(BlockPos blockPos, BlockState blockState, Direction motionSource) {
		return PistonBlock.isMovable(blockState, this.world, blockPos, this.motionDirection, true,
				motionSource);
	}

	private boolean tryMove(BlockPos blockPos, BlockState blockState, Direction motionSource) {
		return PistonBlock.isMovable(blockState, this.world, blockPos, this.motionDirection, false,
				motionSource);
	}

	private void onMove(Queue<QueueItem> queue, BlockPos blockPos, BlockState blockState,
			Direction motionSource) {

		if (onStickyMove(queue, blockPos, blockState))
			return;

		if (onBlastMove(blockPos, blockState, motionSource))
			return;

		if (onDefaultMove(queue, blockPos, blockState))
			return;
	}

	private boolean onStickyMove(Queue<QueueItem> queue, BlockPos blockPos, BlockState blockState) {
		if (isBlockSticky(blockState.getBlock())) {
			BlockPos targetPos;
			BlockState targetState;
			for (Direction direction : Direction.values()) {
				targetPos = blockPos.offset(direction);
				if (direction != this.motionDirection) {
					targetState = this.world.getBlockState(targetPos);
					if (isAdjacentBlockStuck(blockState.getBlock(), targetState.getBlock())) {
						queue.offer(new QueueItem(targetPos, direction));
					}
				} else {
					queue.offer(new QueueItem(targetPos, direction));
				}
			}
			return true;
		} else {
			return false;
		}
	}

	private boolean onBlastMove(BlockPos blockPos, BlockState blockState, Direction motionSource) {
		if (blockState.isOf(BlastrodMod.BLASTROD)) {
			BlockPos targetPos;
			BlockState targetState;
			if (blockState.get(BlastrodBlock.FACING) == this.motionDirection) {
				targetPos = blockPos.offset(this.motionDirection);
				targetState = this.world.getBlockState(targetPos);
				if (PistonBlock.isMovable(targetState, this.world, targetPos, this.motionDirection,
						true, motionSource)) {
					this.blastedBlocks.add(targetPos);
					return true;
				}
			}
		}
		return false;
	}

	private boolean onDefaultMove(Queue<QueueItem> queue, BlockPos blockPos,
			BlockState blockState) {
		queue.offer(new QueueItem(blockPos.offset(this.motionDirection), this.motionDirection));
		return true;
	}

}
