package net.blay09.mods.balm.fabric.world;

import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.api.world.BalmWorldGen;
import net.blay09.mods.balm.api.world.BiomePredicate;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.ConfiguredDecorator;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class FabricBalmWorldGen implements BalmWorldGen {
    @Override
    public <T extends Feature<?>> DeferredObject<T> registerFeature(Supplier<T> supplier, ResourceLocation identifier) {
        return new DeferredObject<>(identifier, () -> {
            T feature = supplier.get();
            Registry.register(Registry.FEATURE, identifier, feature);
            return feature;
        }).resolveImmediately();
    }

    @Override
    public <T extends ConfiguredFeature<?, ?>> DeferredObject<T> registerConfiguredFeature(Supplier<T> supplier, ResourceLocation identifier) {
        return new DeferredObject<>(identifier, () -> {
            T configuredFeature = supplier.get();
            Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, identifier, configuredFeature);
            return configuredFeature;
        }).resolveImmediately();
    }

    @Override
    public <T extends FeatureDecorator<?>> DeferredObject<T> registerDecorator(Supplier<T> supplier, ResourceLocation identifier) {
        return new DeferredObject<>(identifier, () -> {
            T decorator = supplier.get();
            Registry.register(Registry.DECORATOR, identifier, decorator);
            return decorator;
        }).resolveImmediately();
    }

    @Override
    public <T extends FeatureConfiguration> ConfiguredFeature<?, ?> configuredFeature(Feature<T> feature, T config, ConfiguredDecorator<?> configuredDecorator) {
        return feature
                .configured(config)
                .decorated(configuredDecorator);
    }

    @Override
    public void addFeatureToBiomes(BiomePredicate biomePredicate, GenerationStep.Decoration step, ResourceLocation configuredFeatureIdentifier) {
        BiomeModifications.addFeature(it -> biomePredicate.test(
                it.getBiomeKey().location(),
                it.getBiome().getBiomeCategory(),
                it.getBiome().getPrecipitation(),
                it.getBiome().getBaseTemperature(),
                it.getBiome().getDownfall()), step, ResourceKey.create(Registry.CONFIGURED_FEATURE_REGISTRY, configuredFeatureIdentifier));
    }
}
