package me.mioclient.util.baritone;

import me.mioclient.util.client.ChatUtils;

import me.mioclient.api.ClientAccessor;

import me.mioclient.event.EventBus;

import baritone.api.BaritoneAPI;
import baritone.api.behavior.IPathingBehavior;
import baritone.api.pathing.goals.Goal;
import baritone.api.pathing.goals.GoalBlock;
import baritone.api.pathing.goals.GoalXZ;
import baritone.api.process.IBaritoneProcess;
import baritone.api.process.PathingCommand;
import baritone.api.process.PathingCommandType;
import baritone.api.selection.ISelection;
import baritone.api.utils.interfaces.IGoalRenderPos;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import me.mioclient.module.Feature;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/BaritonePathingController.class */
public final class BaritonePathingController implements ClientAccessor, PathingController {
    public final ObjectSet<me.mioclient.module.Feature> objectSet = ObjectSets.synchronize(new ObjectOpenHashSet());

    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/ObjectSetSearchHelper4$BaritoneProcess.class */
    private class BaritoneProcess implements IBaritoneProcess {
        public BaritoneProcess() {
        }

        public boolean isActive() {
            return !BaritonePathingController.this.objectSet.isEmpty();
        }

        public PathingCommand onTick(boolean z, boolean z2) {
            return new PathingCommand((Goal) null, PathingCommandType.REQUEST_PAUSE);
        }

        public boolean isTemporary() {
            return true;
        }

        public void onLostControl() {
        }

        public String displayName0() {
            return "Mio pause service";
        }

        public double priority() {
            return Double.longBitsToDouble(4651998512748167168L);
        }
    }

    public BaritonePathingController() {
        eventBus.register(this);
        BaritoneAPI.getProvider().getPrimaryBaritone().getPathingControlManager().registerProcess(new BaritoneProcess());
        BaritoneAPI.getSettings().useMessageTag.value = false;
        BaritoneAPI.getSettings().logger.value = text -> {
            Text copy = text.copy();
            ((MutableText) copy).getSiblings().add(0, Text.literal(" "));
            ((MutableText) copy).getSiblings().add(0, ChatUtils.getClientPrefix());
            minecraftClient.inGameHud.getChatHud().addMessage(copy, (MessageSignatureData) null, ChatUtils.messageIndicator);
        };
    }

    @Override // me.mioclient.util.baritone.PathingController
    public void addPauseRequest(me.mioclient.module.Feature feature) {
        if (this.objectSet.contains(feature)) {
            return;
        }
        this.objectSet.add(feature);
    }

    @Override // me.mioclient.util.baritone.PathingController
    public void removePauseRequest(me.mioclient.module.Feature feature) {
        if (this.objectSet.contains(feature)) {
            this.objectSet.remove(feature);
        }
    }

    @Override // me.mioclient.util.baritone.PathingController
    public boolean isPaused() {
        return !this.objectSet.isEmpty();
    }

    @Override // me.mioclient.util.baritone.PathingController
    public boolean isPathing() {
        return BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().isPathing();
    }

    @Override // me.mioclient.util.baritone.PathingController
    public void pathForward(double d) {
        BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(GoalXZ.fromDirection(minecraftClient.player.getPos(), minecraftClient.player.getYaw(), Double.longBitsToDouble(4617315517961601024L)));
    }

    @Override // me.mioclient.util.baritone.PathingController
    public void pathTo(BlockPos blockPos) {
        BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(new GoalBlock(blockPos));
    }

    @Override // me.mioclient.util.baritone.PathingController
    public void elytraPathTo(BlockPos blockPos) {
        BaritoneAPI.getProvider().getPrimaryBaritone().getElytraProcess().pathTo(blockPos);
    }

    @Override // me.mioclient.util.baritone.PathingController
    public void setGoal(BlockPos blockPos) {
        BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoal(new GoalBlock(blockPos));
    }

    @Override // me.mioclient.util.baritone.PathingController
    public void cancelAll() {
        IPathingBehavior pathingBehavior = BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior();
        pathingBehavior.cancelEverything();
        pathingBehavior.forceCancel();
    }

    @Override // me.mioclient.util.baritone.PathingController
    public boolean isInSelection(BlockPos blockPos) {
        ISelection lastSelection = BaritoneAPI.getProvider().getPrimaryBaritone().getSelectionManager().getLastSelection();
        if (lastSelection == null) {
            return false;
        }
        return Box.enclosing(lastSelection.min(), lastSelection.max()).contains(blockPos.toCenterPos());
    }

    @Override // me.mioclient.util.baritone.PathingController
    public BlockPos getCurrentGoal() {
        IGoalRenderPos goal = BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().getGoal();
        if (goal instanceof IGoalRenderPos) {
            return goal.getGoalPos();
        }
        if (!(goal instanceof GoalXZ)) {
            return null;
        }
        GoalXZ goalXZ = (GoalXZ) goal;
        return BlockPos.ofFloored(goalXZ.getX(), minecraftClient.player.getY(), goalXZ.getZ());
    }

    @Override // me.mioclient.util.baritone.PathingController
    public boolean isBaritoneLoaded() {
        return true;
    }
}
