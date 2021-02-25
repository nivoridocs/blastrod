package niv.blastrod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.piston.PistonHandler;
import niv.blastrod.util.Scopes;

@Mixin(PistonBlock.class)
public class PistonBlockMixin {

	@Redirect(
			method = "move(" + "Lnet/minecraft/world/World;" + "Lnet/minecraft/util/math/BlockPos;"
					+ "Lnet/minecraft/util/math/Direction;" + "Z" + ")Z",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/block/piston/PistonHandler;"
					+ "calculatePush(" + ")Z"))
	public boolean calculatePushProxy(PistonHandler handler) {
		Scopes.localPistonHandler.set(handler);
		return handler.calculatePush();
	}

}
