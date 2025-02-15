package net.blay09.mods.balm.api.block;

import net.blay09.mods.balm.api.block.entity.BalmBlockEntityFactory;
import net.blay09.mods.balm.api.DeferredObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public interface BalmBlockEntities {
    <T extends BlockEntity> DeferredObject<BlockEntityType<T>> registerBlockEntity(ResourceLocation identifier, BalmBlockEntityFactory<T> factory, Block... blocks);

    <T extends BlockEntity> DeferredObject<BlockEntityType<T>> registerBlockEntity(ResourceLocation identifier, BalmBlockEntityFactory<T> factory, DeferredObject<Block>... blocks);
}
