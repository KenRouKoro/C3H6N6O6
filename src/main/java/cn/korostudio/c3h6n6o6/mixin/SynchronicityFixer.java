package cn.korostudio.c3h6n6o6.mixin;

import net.minecraft.entity.ai.pathing.BirdPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import org.spongepowered.asm.mixin.Mixin;

/**
 * 需要加入同步块方法的列表Mixin类，这玩意很玄学，但很好用（（（（
 */
@Mixin(value = {PathNodeNavigator.class,BirdPathNodeMaker.class/*,Object2LongOpenHashMap.class,ReferenceOpenHashSet.class,ReferenceArrayList.class,Int2ObjectOpenHashMap.class, Long2ObjectOpenHashMap.class, LongLinkedOpenHashSet.class, ObjectOpenCustomHashSet.class, Long2LongOpenHashMap.class, Long2ObjectLinkedOpenHashMap.class*/},
        targets = {"it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap$ValueIterator", "it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap$KeySet", "it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap$KeyIterator",
                "it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap$MapEntrySet", "it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap$EntryIterator", "it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap$MapIterator",
                "it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap$MapEntry", "it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap$FastEntryIterator", "it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap$MapIterator",
                "it.unimi.dsi.fastutil.objects.ReferenceArrayList$Spliterator", "it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet$SetIterator", "it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap", "net.minecraft.entity.ai.pathing.BirdPathNodeMaker"
                , "net.minecraft.entity.ai.pathing.PathNodeNavigator", "net.minecraft.server.world.ChunkTicketManager", "it.unimi.dsi.fastutil.ints.IntArrayList", "it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap", "it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap$MapIterator"
                , "net.minecraft.server.world.ThreadedAnvilChunkStorage", "it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap", "it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet", "net.minecraft.world.entity.EntityTrackingSection"//,"it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet"
                , "net.minecraft.world.tick.ChunkTickScheduler", "net.minecraft.world.poi.PointOfInterestStorage", "net.minecraft.entity.ai.pathing.Path", "net.minecraft.entity.ai.pathing.EntityNavigation"
                , "appeng.me.service.EnergyService", "dev.technici4n.moderndynamics.network.NetworkManager", "com.simibubi.create.foundation.advancement.CriterionTriggerBase"
                , "net.minecraft.util.math.random.CheckedRandom", "net.minecraft.world.level.storage.LevelStorage.Session", "com.simibubi.create.content.contraptions.KineticNetwork"
                , "aztech.modern_industrialization.machines.recipe.MachineRecipeType", "aztech.modern_industrialization.machines.multiblocks.world.ChunkPosMultiMap"
                , "com.simibubi.create.content.logistics.RedstoneLinkNetworkHandler", "appeng.helpers.ForgeEnergyAdapter", "net.fabricmc.fabric.impl.transfer.transaction.TransactionManagerImpl$TransactionImpl"
        },priority = 50000)
public class SynchronicityFixer {
}
