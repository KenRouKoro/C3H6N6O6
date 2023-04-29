package cn.korostudio.c3h6n6o6.mixin;

import cn.korostudio.c3h6n6o6.fastutil.ConcurrentCollections;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.AbstractList;

@Mixin(value = DefaultedList.class)
public abstract class DefaultedListMixin<T> extends AbstractList<T> {
    @Inject(method = "of", at = @At("RETURN"), cancellable = true)
    private static <E> void of(CallbackInfoReturnable<DefaultedList<E>> cir) {
        cir.setReturnValue(new DefaultedList(ConcurrentCollections.newLinkedList(), (Object)null));
    }
}
