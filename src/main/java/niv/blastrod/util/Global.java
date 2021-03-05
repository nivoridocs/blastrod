package niv.blastrod.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.util.math.BlockPos;

public class Global {

    public static final Set<BlockPos> blastedBlocks = Collections.synchronizedSet(new HashSet<>());

    public static final synchronized boolean blastedBlocks_removeIfPresent(BlockPos pos) {
        if (Global.blastedBlocks.contains(pos)) {
            Global.blastedBlocks.remove(pos);
            return true;
        } else {
            return false;
        }
    }

    private Global() {
    }

}
