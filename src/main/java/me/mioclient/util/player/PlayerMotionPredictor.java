package me.mioclient.util.player;

import me.mioclient.api.ClientServices;

import me.mioclient.util.math.BoxMath;
import me.mioclient.util.world.TimedBlockEntry;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;
import me.mioclient.util.world.TimedBlockEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/PlayerMotionPredictor.class */
public class PlayerMotionPredictor implements ClientAccessor {
    public static final float val = Float.intBitsToFloat(-1113550802);
    public static final float val2 = MovementUtils.val;
    public static final float val3 = val2 * Float.intBitsToFloat(1050253722);
    public static final float val4 = (val2 * Float.intBitsToFloat(1068708659)) + Float.intBitsToFloat(1045220557);
    public final List<Box> list = new ArrayList();
    public final PlayerEntity playerEntity;

    public PlayerMotionPredictor(PlayerEntity playerEntity) {
        this.playerEntity = playerEntity;
    }

    public void recalculateLayout() {
        synchronized (this.list) {
            this.list.clear();
            double x = this.playerEntity.getX() - this.playerEntity.prevX;
            double y = this.playerEntity.getY() - this.playerEntity.prevY;
            double z = this.playerEntity.getZ() - this.playerEntity.prevZ;
            double hypot = Math.hypot(x, z);
            double clamp = MathHelper.clamp(hypot, this.playerEntity.isSneaking() ? val3 : val2, val4);
            if (hypot != 0.0d) {
                x = (x / hypot) * clamp;
                z = (z / hypot) * clamp;
            }
            if (EntityUtil.isOnGround(this.playerEntity) && y > 0.0d) {
                y = Double.longBitsToDouble(-4633058300752035840L);
            }
            Box box234 = BoxMath.getServerBox(this.playerEntity);
            if (y > 0.0d) {
                y = applyGravity(y);
            }
            if (hypot < MovementUtils.val2) {
                x = 0.0d;
                z = 0.0d;
            }
            if (hypot < MovementUtils.val2 && Math.abs(y) < Double.longBitsToDouble(4591870180066957722L)) {
                this.list.addAll(Collections.nCopies(20, box234));
                return;
            }
            for (int i = 0; i <= 20; i++) {
                y = !is2711(this.playerEntity, box234.offset(0.0d, Double.longBitsToDouble(-4646453807550688133L), 0.0d)) ? applyGravity(y) : Double.longBitsToDouble(-4633058300752035840L);
                List entityCollisions = minecraftClient.world.getEntityCollisions(this.playerEntity, box234.stretch(x, y, z));
                ArrayList arrayList = new ArrayList();
                synchronized (ClientServices.stashFinderHelper.getTimedBlockMap()) {
                    for (Map.Entry<BlockPos, TimedBlockEntry> entry : ClientServices.stashFinderHelper.getTimedBlockMap().entrySet()) {
                        arrayList.add(entry.getValue().getBlockState().getDefaultState().getOutlineShape(minecraftClient.world, entry.getKey()));
                    }
                }
                Box stretched2711 = box234.stretch(x, y, z);
                arrayList.addAll(entityCollisions);
                net.minecraft.world.border.WorldBorder worldBorder = minecraftClient.world.getWorldBorder();
                if (worldBorder.canCollide(this.playerEntity, stretched2711)) {
                    arrayList.add(worldBorder.asVoxelShape());
                }
                for (VoxelShape voxelShape : minecraftClient.world.getBlockCollisions(this.playerEntity, stretched2711)) {
                    arrayList.add(voxelShape);
                }
                box234 = calculateMovementBox(box234, arrayList, x, y, z);
                this.list.add(box234);
            }
        }
    }

    public static double applyGravity(double d) {
        return (d + Double.longBitsToDouble(4590429028186199163L)) * Double.longBitsToDouble(4607002274814922588L);
    }

    public static Box calculateMovementBox(Box box, List<VoxelShape> list, double d, double d2, double d3) {
        if (list.isEmpty()) {
            return box.offset(d, d2, d3);
        }
        if (d2 != 0.0d) {
            d2 = VoxelShapes.calculateMaxOffset(Direction.Axis.Y, box, list, d2);
            if (d2 != 0.0d) {
                box = box.offset(0.0d, d2, 0.0d);
            }
        }
        boolean z = Math.abs(d) < Math.abs(d3);
        if (z && d3 != 0.0d) {
            d3 = VoxelShapes.calculateMaxOffset(Direction.Axis.Z, box, list, d3);
            if (d3 != 0.0d) {
                box = box.offset(0.0d, 0.0d, d3);
            }
        }
        if (d != 0.0d) {
            d = VoxelShapes.calculateMaxOffset(Direction.Axis.X, box, list, d);
            if (!z && d != 0.0d) {
                box = box.offset(d, 0.0d, 0.0d);
            }
        }
        if (!z && d3 != 0.0d) {
            d3 = VoxelShapes.calculateMaxOffset(Direction.Axis.Z, box, list, d3);
        }
        return box.offset(d, d2, d3);
    }

    public boolean is2711(PlayerEntity playerEntity, Box box) {
        return minecraftClient.world.getBlockCollisions((Entity) playerEntity, box).iterator().hasNext();
    }

    public synchronized List<Box> getList2712() {
        return this.list;
    }
}
