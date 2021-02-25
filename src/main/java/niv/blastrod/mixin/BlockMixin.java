package niv.blastrod.mixin;

import static niv.blastrod.BlastrodMod.BLASTROD_ITEM;
import static niv.blastrod.BlastrodMod.MOD_NAME;
import static niv.blastrod.BlastrodMod.log;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import niv.blastrod.util.Scopes;

@Mixin(Block.class)
public class BlockMixin {

	@Inject(method = "dropStacks(" + "Lnet/minecraft/block/BlockState;"
			+ "Lnet/minecraft/world/WorldAccess;" + "Lnet/minecraft/util/math/BlockPos;"
			+ "Lnet/minecraft/block/entity/BlockEntity;" + ")V", at = @At("HEAD"),
			cancellable = true)
	private static void dropStackHead(BlockState state, WorldAccess world, BlockPos pos,
			BlockEntity blockEntity, CallbackInfo info) {
		PistonHandler handler = Scopes.localPistonHandler.get();
		Scopes.localPistonHandler.remove();

		if (handler != null) {
			if (world instanceof World && Scopes.blastedBlocks.containsKey(handler.hashCode())
					&& Scopes.blastedBlocks.get(handler.hashCode()).contains(pos)) {
				Block.dropStacks(state, (World) world, pos, blockEntity, null,
						BLASTROD_ITEM.getDefaultStack());
				info.cancel();
			}
		}
	}

}
