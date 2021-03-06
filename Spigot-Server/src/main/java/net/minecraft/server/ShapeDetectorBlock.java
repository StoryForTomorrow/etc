package net.minecraft.server;

import com.google.common.base.Predicate;

public class ShapeDetectorBlock {

    private final World a;
    private final BlockPosition b;
    private IBlockData c;
    private TileEntity d;
    private boolean e;

    public ShapeDetectorBlock(World world, BlockPosition blockposition) {
        this.a = world;
        this.b = blockposition;
    }

    public IBlockData a() {
        // CraftBukkit start - fix null pointer
        if (this.c == null) {
            this.c = this.a.isLoaded(this.b) ? this.a.getType(this.b) : Blocks.AIR.getBlockData();
        // CraftBukkit end
        }

        return this.c;
    }

    public TileEntity b() {
        if (this.d == null && !this.e) {
            this.d = this.a.getTileEntity(this.b);
            this.e = true;
        }

        return this.d;
    }

    public BlockPosition d() {
        return this.b;
    }

    public static Predicate a(Predicate predicate) {
        return new ShapeDetectorBlockInnerClass1(predicate);
    }
}
