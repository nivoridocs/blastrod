package niv.blastrod.mixin;

import static niv.blastrod.BlastrodMod.BLASTROD;
import java.util.List;
import java.util.Set;
import com.google.common.collect.Sets;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.block.BlockState;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import niv.blastrod.block.BlastrodBlock;

@Mixin(PistonHandler.class)
public class PistonHandlerMixin {

	@Shadow
	private World world;

	@Shadow
	private Direction motionDirection;

	@Shadow
	private List<BlockPos> movedBlocks;

	@Shadow
	private List<BlockPos> brokenBlocks;

	private Set<BlockPos> blastedBlocks = Sets.newHashSet();

	private final ThreadLocal<BlockPos> localBlockPos = ThreadLocal.withInitial(() -> null);

	@Inject(method = "calculatePush()Z", at = @At("HEAD"))
	public void calculatePushHead(CallbackInfoReturnable<Boolean> info) {
		this.blastedBlocks.clear();
	}

	@Redirect(
			method = "tryMove(" + "Lnet/minecraft/util/math/BlockPos;"
					+ "Lnet/minecraft/util/math/Direction;" + ")Z",
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/world/World;" + "getBlockState("
							+ "Lnet/minecraft/util/math/BlockPos;" + ")"
							+ "Lnet/minecraft/block/BlockState;",
					ordinal = 3))
	public BlockState getBlockStateProxy(World world, BlockPos pos) {
		localBlockPos.set(pos);
		return world.getBlockState(pos);
	}

	@Inject(method = "tryMove(" + "Lnet/minecraft/util/math/BlockPos;"
			+ "Lnet/minecraft/util/math/Direction;" + ")Z",
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/world/World;" + "getBlockState("
							+ "Lnet/minecraft/util/math/BlockPos;" + ")"
							+ "Lnet/minecraft/block/BlockState;",
					ordinal = 3, shift = At.Shift.AFTER),
			cancellable = true)
	public void addMovedBlockAfter(CallbackInfoReturnable<Boolean> info) {
		BlockPos pos = localBlockPos.get();
		localBlockPos.remove();

		if (pos != null) {
			BlockState state = this.world.getBlockState(pos);
			if (state.isOf(BLASTROD)) {
				Direction direction = state.get(BlastrodBlock.FACING);
				if (direction == this.motionDirection) {
					BlockPos targetPos = pos.offset(direction);
					if (PistonBlock.isMovable(state, this.world, targetPos, this.motionDirection,
							true, this.motionDirection)) {
						this.movedBlocks.add(pos);
						this.blastedBlocks.add(targetPos);
						info.setReturnValue(true);
					}
				}
			}
		}
	}

	@Inject(method = "calculatePush()Z", at = @At("RETURN"))
	public void calculatePushReturn(CallbackInfoReturnable<Boolean> info) {
		if (!info.getReturnValueZ())
			return;
		this.blastedBlocks.removeAll(movedBlocks);
		this.brokenBlocks.addAll(blastedBlocks);
	}

}
