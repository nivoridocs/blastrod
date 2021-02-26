package niv.blastrod.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public final class QueueItem {
    public final BlockPos blockPos;
    public final Direction motionSource;

    public QueueItem(BlockPos blockPos, Direction motionSource) {
        this.blockPos = blockPos;
        this.motionSource = motionSource;
    }
}
