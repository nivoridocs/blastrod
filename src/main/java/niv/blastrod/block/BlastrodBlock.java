package niv.blastrod.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class BlastrodBlock extends FacingBlock {
	protected static final VoxelShape Y_SHAPE =
			Block.createCuboidShape(6.0D, 0.0D, 6.0D, 10.0D, 16.0D, 10.0D);
	protected static final VoxelShape Z_SHAPE =
			Block.createCuboidShape(6.0D, 6.0D, 0.0D, 10.0D, 10.0D, 16.0D);
	protected static final VoxelShape X_SHAPE =
			Block.createCuboidShape(0.0D, 6.0D, 6.0D, 16.0D, 10.0D, 10.0D);

	public BlastrodBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.UP));
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return (BlockState) state.with(FACING, rotation.rotate((Direction) state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return (BlockState) state.with(FACING, mirror.apply((Direction) state.get(FACING)));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos,
			ShapeContext context) {
		switch (((Direction) state.get(FACING)).getAxis()) {
			case X:
			default:
				return X_SHAPE;
			case Z:
				return Z_SHAPE;
			case Y:
				return Y_SHAPE;
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		Direction direction = ctx.getSide();
		BlockState blockState =
				ctx.getWorld().getBlockState(ctx.getBlockPos().offset(direction.getOpposite()));
		return blockState.isOf(this) && blockState.get(FACING) == direction
				? (BlockState) this.getDefaultState().with(FACING, direction.getOpposite())
				: (BlockState) this.getDefaultState().with(FACING, direction);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public PistonBehavior getPistonBehavior(BlockState state) {
		return PistonBehavior.NORMAL;
	}

	@Override
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos,
			NavigationType type) {
		return false;
	}

}
