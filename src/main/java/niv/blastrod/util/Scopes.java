package niv.blastrod.util;

import java.util.Collection;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.util.math.BlockPos;

public class Scopes {

    public static final Int2ObjectMap<Collection<BlockPos>> blastedBlocks = new Int2ObjectOpenHashMap<>();

    public static final ThreadLocal<PistonHandler> localPistonHandler =
            ThreadLocal.withInitial(() -> null);

}
