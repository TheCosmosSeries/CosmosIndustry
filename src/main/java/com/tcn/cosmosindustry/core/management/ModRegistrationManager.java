package com.tcn.cosmosindustry.core.management;

import java.util.ArrayList;
import java.util.function.Supplier;

import com.tcn.cosmosindustry.CosmosIndustry;
import com.tcn.cosmosindustry.IndustryReference;
import com.tcn.cosmosindustry.processing.client.container.ContainerCharger;
import com.tcn.cosmosindustry.processing.client.container.ContainerCompactor;
import com.tcn.cosmosindustry.processing.client.container.ContainerGrinder;
import com.tcn.cosmosindustry.processing.client.container.ContainerKiln;
import com.tcn.cosmosindustry.processing.client.container.ContainerLaserCutter;
import com.tcn.cosmosindustry.processing.client.container.ContainerSeparator;
import com.tcn.cosmosindustry.processing.client.container.ContainerSynthesiser;
import com.tcn.cosmosindustry.processing.client.container.ContainerSynthesiserStand;
import com.tcn.cosmosindustry.processing.client.renderer.RendererCompactor;
import com.tcn.cosmosindustry.processing.client.renderer.RendererGrinder;
import com.tcn.cosmosindustry.processing.client.renderer.RendererKiln;
import com.tcn.cosmosindustry.processing.client.renderer.RendererLaserCutter;
import com.tcn.cosmosindustry.processing.client.renderer.RendererSeparator;
import com.tcn.cosmosindustry.processing.client.renderer.RendererSynthesiser;
import com.tcn.cosmosindustry.processing.client.renderer.RendererSynthesiserStand;
import com.tcn.cosmosindustry.processing.client.screen.ScreenCharger;
import com.tcn.cosmosindustry.processing.client.screen.ScreenCompactor;
import com.tcn.cosmosindustry.processing.client.screen.ScreenGrinder;
import com.tcn.cosmosindustry.processing.client.screen.ScreenKiln;
import com.tcn.cosmosindustry.processing.client.screen.ScreenLaserCutter;
import com.tcn.cosmosindustry.processing.client.screen.ScreenSeparator;
import com.tcn.cosmosindustry.processing.client.screen.ScreenSynthesiser;
import com.tcn.cosmosindustry.processing.core.block.BlockCharger;
import com.tcn.cosmosindustry.processing.core.block.BlockCompactor;
import com.tcn.cosmosindustry.processing.core.block.BlockGrinder;
import com.tcn.cosmosindustry.processing.core.block.BlockKiln;
import com.tcn.cosmosindustry.processing.core.block.BlockLaserCutter;
import com.tcn.cosmosindustry.processing.core.block.BlockSeparator;
import com.tcn.cosmosindustry.processing.core.block.BlockStructure;
import com.tcn.cosmosindustry.processing.core.block.BlockSynthesiser;
import com.tcn.cosmosindustry.processing.core.block.BlockSynthesiserStand;
import com.tcn.cosmosindustry.processing.core.block.ItemBlockMachine;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntityCharger;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntityCompactor;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntityGrinder;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntityKiln;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntityLaserCutter;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntitySeparator;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntitySynthesiser;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntitySynthesiserStand;
import com.tcn.cosmosindustry.storage.client.container.ContainerCapacitor;
import com.tcn.cosmosindustry.storage.client.container.ContainerCapacitorCreative;
import com.tcn.cosmosindustry.storage.client.container.ContainerCapacitorSurge;
import com.tcn.cosmosindustry.storage.client.renderer.RendererCapacitor;
import com.tcn.cosmosindustry.storage.client.screen.ScreenCapacitor;
import com.tcn.cosmosindustry.storage.core.block.BlockCapacitor;
import com.tcn.cosmosindustry.storage.core.block.BlockCapacitorCreative;
import com.tcn.cosmosindustry.storage.core.block.BlockCapacitorSurge;
import com.tcn.cosmosindustry.storage.core.blockentity.BlockEntityCapacitor;
import com.tcn.cosmosindustry.storage.core.blockentity.BlockEntityCapacitorCreative;
import com.tcn.cosmosindustry.storage.core.blockentity.BlockEntityCapacitorSurge;
import com.tcn.cosmosindustry.transport.client.renderer.RendererEnergyChannel;
import com.tcn.cosmosindustry.transport.client.renderer.RendererEnergyChannelTransparent;
import com.tcn.cosmosindustry.transport.client.renderer.RendererFluidChannel;
import com.tcn.cosmosindustry.transport.client.renderer.RendererFluidChannelTransparent;
import com.tcn.cosmosindustry.transport.core.energy.block.BlockChannelCreativeEnergy;
import com.tcn.cosmosindustry.transport.core.energy.block.BlockChannelEnergy;
import com.tcn.cosmosindustry.transport.core.energy.block.BlockChannelSurgeEnergy;
import com.tcn.cosmosindustry.transport.core.energy.block.BlockChannelTransparentCreativeEnergy;
import com.tcn.cosmosindustry.transport.core.energy.block.BlockChannelTransparentEnergy;
import com.tcn.cosmosindustry.transport.core.energy.block.BlockChannelTransparentSurgeEnergy;
import com.tcn.cosmosindustry.transport.core.energy.blockentity.BlockEntityChannelCreativeEnergy;
import com.tcn.cosmosindustry.transport.core.energy.blockentity.BlockEntityChannelEnergy;
import com.tcn.cosmosindustry.transport.core.energy.blockentity.BlockEntityChannelSurgeEnergy;
import com.tcn.cosmosindustry.transport.core.energy.blockentity.BlockEntityChannelTransparentCreativeEnergy;
import com.tcn.cosmosindustry.transport.core.energy.blockentity.BlockEntityChannelTransparentEnergy;
import com.tcn.cosmosindustry.transport.core.energy.blockentity.BlockEntityChannelTransparentSurgeEnergy;
import com.tcn.cosmosindustry.transport.core.fluid.block.BlockChannelCreativeFluid;
import com.tcn.cosmosindustry.transport.core.fluid.block.BlockChannelFluid;
import com.tcn.cosmosindustry.transport.core.fluid.block.BlockChannelSurgeFluid;
import com.tcn.cosmosindustry.transport.core.fluid.block.BlockChannelTransparentCreativeFluid;
import com.tcn.cosmosindustry.transport.core.fluid.block.BlockChannelTransparentFluid;
import com.tcn.cosmosindustry.transport.core.fluid.block.BlockChannelTransparentSurgeFluid;
import com.tcn.cosmosindustry.transport.core.fluid.blockentity.BlockEntityChannelCreativeFluid;
import com.tcn.cosmosindustry.transport.core.fluid.blockentity.BlockEntityChannelFluid;
import com.tcn.cosmosindustry.transport.core.fluid.blockentity.BlockEntityChannelSurgeFluid;
import com.tcn.cosmosindustry.transport.core.fluid.blockentity.BlockEntityChannelTransparentCreativeFluid;
import com.tcn.cosmosindustry.transport.core.fluid.blockentity.BlockEntityChannelTransparentFluid;
import com.tcn.cosmosindustry.transport.core.fluid.blockentity.BlockEntityChannelTransparentSurgeFluid;
import com.tcn.cosmoslibrary.common.block.CosmosBlock;
import com.tcn.cosmoslibrary.common.block.CosmosItemBlock;
import com.tcn.cosmoslibrary.common.item.CosmosCraftingItem;
import com.tcn.cosmoslibrary.common.item.CosmosItem;
import com.tcn.cosmoslibrary.common.item.CosmosItemTool;
import com.tcn.cosmoslibrary.common.item.CosmosItemUpgradeEnergy;
import com.tcn.cosmoslibrary.common.item.CosmosItemUpgradeFluid;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.runtime.CosmosRuntimeHelper;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("deprecation")
@EventBusSubscriber(modid = CosmosIndustry.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModRegistrationManager {

	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CosmosIndustry.MOD_ID);
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CosmosIndustry.MOD_ID);
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, CosmosIndustry.MOD_ID);
	public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(BuiltInRegistries.MENU, CosmosIndustry.MOD_ID);

	public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CosmosIndustry.MOD_ID);

	public static final ArrayList<Supplier<? extends ItemLike>> TAB_BLOCKS = new ArrayList<>();
	public static final ArrayList<Supplier<? extends ItemLike>> TAB_ITEMS = new ArrayList<>();
	public static final ArrayList<Supplier<? extends ItemLike>> TAB_DEVICES = new ArrayList<>();

	public static final Supplier<CreativeModeTab> BLOCKS_GROUP = TABS.register("cosmosindustry.blocks", 
		() -> CreativeModeTab.builder()
			.title(ComponentHelper.style(ComponentColour.GRAY, "Cosmos Industry Blocks")).icon(() -> { return new ItemStack(ModRegistrationManager.BLOCK_ORE_TIN.get()); })
			.displayItems((params, output) -> TAB_BLOCKS.forEach(itemLike -> output.accept(itemLike.get())))
			.build()
	);

	public static final Supplier<CreativeModeTab> ITEMS_GROUP = TABS.register("cosmosindustry.items", 
		() -> CreativeModeTab.builder()
			.title(ComponentHelper.style(ComponentColour.GRAY, "Cosmos Industry Items")).icon(() -> { return new ItemStack(ModRegistrationManager.MACHINE_WRENCH.get()); })
			.displayItems((params, output) -> TAB_ITEMS.forEach(itemLike -> output.accept(itemLike.get())))
			.build()
	);

	public static final Supplier<CreativeModeTab> DEVICES_GROUP = TABS.register("cosmosindustry.tools", 
		() -> CreativeModeTab.builder()
			.title(ComponentHelper.style(ComponentColour.GRAY, "Cosmos Industry Tools")).icon(() -> { return new ItemStack(ModRegistrationManager.BLOCK_STRUCTURE.get()); })
			.displayItems((params, output) -> TAB_DEVICES.forEach(itemLike -> output.accept(itemLike.get())))
			.build()
	);
	
	/** -- ITEM START -- */
	public static final DeferredItem<Item> COPPER_DUST = addToItemTab(ITEMS.register("copper_dust", () -> new CosmosItem(new Item.Properties())));
	public static final DeferredItem<Item> COPPER_PLATE = addToItemTab(ITEMS.register("copper_plate", () -> new CosmosItem(new Item.Properties())));
	
	public static final DeferredItem<Item> TIN_INGOT = addToItemTab(ITEMS.register("tin_ingot", () -> new CosmosItem(new Item.Properties())));
	public static final DeferredItem<Item> TIN_DUST = addToItemTab(ITEMS.register("tin_dust", () -> new CosmosItem(new Item.Properties())));
	public static final DeferredItem<Item> TIN_PLATE = addToItemTab(ITEMS.register("tin_plate", () -> new CosmosItem(new Item.Properties())));
	
	public static final DeferredItem<Item> SILVER_INGOT = addToItemTab(ITEMS.register("silver_ingot", () -> new CosmosItem(new Item.Properties())));
	public static final DeferredItem<Item> SILVER_DUST = addToItemTab(ITEMS.register("silver_dust", () -> new CosmosItem(new Item.Properties())));
	public static final DeferredItem<Item> SILVER_PLATE = addToItemTab(ITEMS.register("silver_plate", () -> new CosmosItem(new Item.Properties())));
	
	public static final DeferredItem<Item> BRONZE_INGOT = addToItemTab(ITEMS.register("bronze_ingot", () -> new CosmosItem(new Item.Properties())));
	public static final DeferredItem<Item> BRONZE_DUST = addToItemTab(ITEMS.register("bronze_dust", () -> new CosmosItem(new Item.Properties())));
	public static final DeferredItem<Item> BRONZE_PLATE = addToItemTab(ITEMS.register("bronze_plate", () -> new CosmosItem(new Item.Properties())));
	
	public static final DeferredItem<Item> BRASS_INGOT = addToItemTab(ITEMS.register("brass_ingot", () -> new CosmosItem(new Item.Properties())));
	public static final DeferredItem<Item> BRASS_DUST = addToItemTab(ITEMS.register("brass_dust", () -> new CosmosItem(new Item.Properties())));
	public static final DeferredItem<Item> BRASS_PLATE = addToItemTab(ITEMS.register("brass_plate", () -> new CosmosItem(new Item.Properties())));
	
	public static final DeferredItem<Item> LAPIS_INGOT = addToItemTab(ITEMS.register("lapis_ingot", () -> new CosmosItem(new Item.Properties())));
	public static final DeferredItem<Item> SILICON_INGOT = addToItemTab(ITEMS.register("silicon_ingot", () -> new CosmosItem(new Item.Properties())));
	
	public static final DeferredItem<Item> STEEL_INGOT = addToItemTab(ITEMS.register("steel_ingot", () -> new CosmosItem(new Item.Properties())));
	public static final DeferredItem<Item> STEEL_INGOT_UNREFINED = addToItemTab(ITEMS.register("steel_ingot_unrefined", () -> new CosmosItem(new Item.Properties())));
	public static final DeferredItem<Item> STEEL_DUST = addToItemTab(ITEMS.register("steel_dust", () -> new CosmosItem(new Item.Properties())));
	public static final DeferredItem<Item> STEEL_PLATE = addToItemTab(ITEMS.register("steel_plate", () -> new CosmosItem(new Item.Properties())));
	public static final DeferredItem<Item> STEEL_ROD = addToItemTab(ITEMS.register("steel_rod", () -> new CosmosItem(new Item.Properties())));
	
	public static final DeferredItem<Item> IRON_DUST = addToItemTab(ITEMS.register("iron_dust", () -> new CosmosItem(new Item.Properties())));
	public static final DeferredItem<Item> IRON_DUST_REFINE = addToItemTab(ITEMS.register("iron_dust_refine", () -> new CosmosItem(new Item.Properties())));
	public static final DeferredItem<Item> IRON_PLATE = addToItemTab(ITEMS.register("iron_plate", () -> new CosmosItem(new Item.Properties())));
	
	public static final DeferredItem<Item> GOLD_DUST = addToItemTab(ITEMS.register("gold_dust", () -> new CosmosItem(new Item.Properties())));
	public static final DeferredItem<Item> GOLD_DUST_REFINE = addToItemTab(ITEMS.register("gold_dust_refine", () -> new CosmosItem(new Item.Properties())));
	public static final DeferredItem<Item> GOLD_PLATE = addToItemTab(ITEMS.register("gold_plate", () -> new CosmosItem(new Item.Properties())));
	
	public static final DeferredItem<Item> DIAMOND_DUST = addToItemTab(ITEMS.register("diamond_dust", () -> new CosmosItem(new Item.Properties())));
	public static final DeferredItem<Item> DIAMOND_PLATE = addToItemTab(ITEMS.register("diamond_plate", () -> new CosmosItem(new Item.Properties())));
	
	public static final DeferredItem<Item> STONE_DUST = addToItemTab(ITEMS.register("stone_dust", () -> new CosmosItem(new Item.Properties())));
	public static final DeferredItem<Item> COAL_DUST = addToItemTab(ITEMS.register("coal_dust", () -> new CosmosItem(new Item.Properties())));
	
	public static final DeferredItem<Item> ENERGY_INGOT = addToItemTab(ITEMS.register("energy_ingot", () -> new CosmosItem(new Item.Properties())));
	public static final DeferredItem<Item> ENERGY_DUST = addToItemTab(ITEMS.register("energy_dust", () -> new CosmosItem(new Item.Properties())));
	public static final DeferredItem<Item> ENERGY_WAFER = addToItemTab(ITEMS.register("energy_wafer", () -> new CosmosItem(new Item.Properties())));
	
	public static final DeferredItem<Item> CIRCUIT_RAW = addToItemTab(ITEMS.register("circuit_raw", () -> new CosmosItem(new Item.Properties().stacksTo(32))));
	public static final DeferredItem<Item> CIRCUIT = addToItemTab(ITEMS.register("circuit", () -> new CosmosItem(new Item.Properties().stacksTo(32))));
	public static final DeferredItem<Item> CIRCUIT_ADVANCED_RAW = addToItemTab(ITEMS.register("circuit_advanced_raw", () -> new CosmosItem(new Item.Properties().stacksTo(8))));
	public static final DeferredItem<Item> CIRCUIT_ADVANCED = addToItemTab(ITEMS.register("circuit_advanced", () -> new CosmosItem(new Item.Properties().stacksTo(8))));
	
	public static final DeferredItem<Item> SILICON = addToItemTab(ITEMS.register("silicon", () -> new CosmosItem(new Item.Properties())));
	public static final DeferredItem<Item> SILICON_REFINED = addToItemTab(ITEMS.register("silicon_refined", () -> new CosmosItem(new Item.Properties())));
	public static final DeferredItem<Item> SILICON_WAFER = addToItemTab(ITEMS.register("silicon_wafer", () -> new CosmosItem(new Item.Properties())));
	
	public static final DeferredItem<Item> UPGRADE_BASE = addToItemTab(ITEMS.register("upgrade_base", () -> new CosmosItemUpgradeEnergy(new Item.Properties())));
	
	public static final DeferredItem<Item> UPGRADE_SPEED = addToItemTab(ITEMS.register("upgrade_speed", () -> new CosmosItemUpgradeEnergy(new Item.Properties().stacksTo(16))));
	public static final DeferredItem<Item> UPGRADE_CAPACITY = addToItemTab(ITEMS.register("upgrade_capacity", () -> new CosmosItemUpgradeEnergy(new Item.Properties().stacksTo(16))));
	public static final DeferredItem<Item> UPGRADE_EFFICIENCY = addToItemTab(ITEMS.register("upgrade_efficiency", () -> new CosmosItemUpgradeEnergy(new Item.Properties().stacksTo(16))));
	
	public static final DeferredItem<Item> UPGRADE_FLUID_SPEED = addToItemTab(ITEMS.register("upgrade_fluid_speed", () -> new CosmosItemUpgradeFluid(new Item.Properties().stacksTo(16))));
	public static final DeferredItem<Item> UPGRADE_FLUID_CAPACITY = addToItemTab(ITEMS.register("upgrade_fluid_capacity", () -> new CosmosItemUpgradeFluid(new Item.Properties().stacksTo(16))));
	public static final DeferredItem<Item> UPGRADE_FLUID_EFFICIENCY = addToItemTab(ITEMS.register("upgrade_fluid_efficiency", () -> new CosmosItemUpgradeFluid(new Item.Properties().stacksTo(16))));
	
	public static final DeferredItem<Item> RUBBER = addToItemTab(ITEMS.register("rubber", () -> new CosmosItem(new Item.Properties())));
	public static final DeferredItem<Item> RUBBER_INSULATION = addToItemTab(ITEMS.register("rubber_insulation", () -> new CosmosItem(new Item.Properties())));
	public static final DeferredItem<Item> TOOL_ROD = addToItemTab(ITEMS.register("tool_rod", () -> new CosmosItem(new Item.Properties())));
	
	public static final DeferredItem<Item> MACHINE_WRENCH = addToItemTab(ITEMS.register("machine_wrench", () -> new CosmosItemTool(new Item.Properties())));
	public static final DeferredItem<Item> MACHINE_HAMMER = addToItemTab(ITEMS.register("machine_hammer", () -> new CosmosCraftingItem(new Item.Properties(), 1, 100, 2)));
	
	/** -- BLOCK START -- */
	public static final DeferredBlock<Block> BLOCK_ORE_TIN = BLOCKS.register("block_ore_tin", () -> new CosmosBlock(Block.Properties.of().requiresCorrectToolForDrops().strength(3.0F, 3.0F)));
	public static final DeferredItem<Item> ITEM_BLOCK_ORE_TIN = addToBlockTab(ITEMS.register("block_ore_tin", () -> new BlockItem(BLOCK_ORE_TIN.get(), new Item.Properties())));
	public static final DeferredBlock<Block> BLOCK_ORE_SILVER = BLOCKS.register("block_ore_silver", () -> new CosmosBlock(Block.Properties.of().requiresCorrectToolForDrops().strength(3.0F, 3.0F)));
	public static final DeferredItem<Item> ITEM_BLOCK_ORE_SILVER = addToBlockTab(ITEMS.register("block_ore_silver", () -> new BlockItem(BLOCK_ORE_SILVER.get(), new Item.Properties())));
	public static final DeferredBlock<Block> BLOCK_ORE_SILICON = BLOCKS.register("block_ore_silicon", () -> new CosmosBlock(Block.Properties.of().requiresCorrectToolForDrops().strength(3.0F, 3.0F)));
	public static final DeferredItem<Item> ITEM_BLOCK_ORE_SILICON = addToBlockTab(ITEMS.register("block_ore_silicon", () -> new BlockItem(BLOCK_ORE_SILICON.get(), new Item.Properties())));
	
	public static final DeferredBlock<Block> BLOCK_TIN = BLOCKS.register("block_tin", () -> new CosmosBlock(Block.Properties.of().requiresCorrectToolForDrops().strength(5.0F, 6.0F)));
	public static final DeferredItem<Item> ITEM_BLOCK_TIN = addToBlockTab(ITEMS.register("block_tin", () -> new BlockItem(BLOCK_TIN.get(), new Item.Properties())));
	public static final DeferredBlock<Block> BLOCK_SILVER = BLOCKS.register("block_silver", () -> new CosmosBlock(Block.Properties.of().requiresCorrectToolForDrops().strength(5.0F, 6.0F)));
	public static final DeferredItem<Item> ITEM_BLOCK_SILVER = addToBlockTab(ITEMS.register("block_silver", () -> new BlockItem(BLOCK_SILVER.get(), new Item.Properties())));
	public static final DeferredBlock<Block> BLOCK_SILICON = BLOCKS.register("block_silicon", () -> new CosmosBlock(Block.Properties.of().requiresCorrectToolForDrops().strength(5.0F, 6.0F)));
	public static final DeferredItem<Item> ITEM_BLOCK_SILICON = addToBlockTab(ITEMS.register("block_silicon", () -> new BlockItem(BLOCK_SILICON.get(), new Item.Properties())));
	
	public static final DeferredBlock<Block> BLOCK_STEEL = BLOCKS.register("block_steel", () -> new CosmosBlock(Block.Properties.of().requiresCorrectToolForDrops().strength(8.0F, 10.0F)));
	public static final DeferredItem<Item> ITEM_BLOCK_STEEL = addToBlockTab(ITEMS.register("block_steel", () -> new BlockItem(BLOCK_STEEL.get(), new Item.Properties())));
	public static final DeferredBlock<Block> BLOCK_BRONZE = BLOCKS.register("block_bronze", () -> new CosmosBlock(Block.Properties.of().requiresCorrectToolForDrops().strength(6.0F, 8.0F)));
	public static final DeferredItem<Item> ITEM_BLOCK_BRONZE = addToBlockTab(ITEMS.register("block_bronze", () -> new BlockItem(BLOCK_BRONZE.get(), new Item.Properties())));
	public static final DeferredBlock<Block> BLOCK_BRASS = BLOCKS.register("block_brass", () -> new CosmosBlock(Block.Properties.of().requiresCorrectToolForDrops().strength(6.0F, 8.0F)));
	public static final DeferredItem<Item> ITEM_BLOCK_BRASS = addToBlockTab(ITEMS.register("block_brass", () -> new BlockItem(BLOCK_BRASS.get(), new Item.Properties())));
	
	public static final DeferredBlock<Block> BLOCK_ENERGY = BLOCKS.register("block_energy", () -> new CosmosBlock(Block.Properties.of().requiresCorrectToolForDrops().strength(6.0F, 8.0F)));
	public static final DeferredItem<Item> ITEM_BLOCK_ENERGY = addToBlockTab(ITEMS.register("block_energy", () -> new BlockItem(BLOCK_ENERGY.get(), new Item.Properties())));
	
	/** - Processing - */
	public static final DeferredBlock<Block> BLOCK_STRUCTURE = BLOCKS.register("block_structure", () -> new BlockStructure(Block.Properties.of()));
	public static final DeferredItem<Item> ITEMBLOCK_STRUCTURE = addToDevicesTab(ITEMS.register("block_structure", () -> new CosmosItemBlock(BLOCK_STRUCTURE.get(), new Item.Properties(), "Base block to craft machines.", "", "")));
	
	public static final DeferredBlock<Block> BLOCK_KILN = BLOCKS.register("block_kiln", () -> new BlockKiln(Block.Properties.of().requiresCorrectToolForDrops().strength(6.0F, 10.0F).noOcclusion().dynamicShape().sound(SoundType.METAL)));
	public static final DeferredItem<Item> ITEMBLOCK_KILN = addToDevicesTab(ITEMS.register("block_kiln", () -> new ItemBlockMachine(BLOCK_KILN.get(), new Item.Properties(), "A machine to smelt things.", "Smelts things using RF power.", "Can be upgraded internally.")));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityKiln>> BLOCK_ENTITY_TYPE_KILN = BLOCK_ENTITY_TYPES.register("block_entity_kiln", () -> BlockEntityType.Builder.of(BlockEntityKiln::new, BLOCK_KILN.get()).build(null));
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerKiln>> CONTAINER_TYPE_KILN = MENU_TYPES.register("container_kiln", () -> IMenuTypeExtension.create(ContainerKiln::new));
	
	public static final DeferredBlock<Block> BLOCK_GRINDER = BLOCKS.register("block_grinder", () -> new BlockGrinder(Block.Properties.of().requiresCorrectToolForDrops().strength(6.0F, 10.0F).noOcclusion().dynamicShape().sound(SoundType.METAL)));
	public static final DeferredItem<Item> ITEMBLOCK_GRINDER = addToDevicesTab(ITEMS.register("block_grinder", () -> new ItemBlockMachine(BLOCK_GRINDER.get(), new Item.Properties(), "A machine to grind things.", "Grinds things using RF power.", "Can be upgraded internally.")));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityGrinder>> BLOCK_ENTITY_TYPE_GRINDER = BLOCK_ENTITY_TYPES.register("block_entity_grinder", () -> BlockEntityType.Builder.of(BlockEntityGrinder::new, BLOCK_GRINDER.get()).build(null));
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerGrinder>> CONTAINER_TYPE_GRINDER = MENU_TYPES.register("container_grinder", () -> IMenuTypeExtension.create(ContainerGrinder::new));
	
	public static final DeferredBlock<Block> BLOCK_COMPACTOR = BLOCKS.register("block_compactor", () -> new BlockCompactor(Block.Properties.of().requiresCorrectToolForDrops().strength(6.0F, 10.0F).noOcclusion().dynamicShape().sound(SoundType.METAL)));
	public static final DeferredItem<Item> ITEMBLOCK_COMPACTOR = addToDevicesTab(ITEMS.register("block_compactor", () -> new ItemBlockMachine(BLOCK_COMPACTOR.get(), new Item.Properties(), "A machine to compact things.", "Compacts things using RF power.", "Can be upgraded internally.")));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityCompactor>> BLOCK_ENTITY_TYPE_COMPACTOR = BLOCK_ENTITY_TYPES.register("block_entity_compactor", () -> BlockEntityType.Builder.of(BlockEntityCompactor::new, BLOCK_COMPACTOR.get()).build(null));
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerCompactor>> CONTAINER_TYPE_COMPACTOR = MENU_TYPES.register("container_compactor", () -> IMenuTypeExtension.create(ContainerCompactor::new));
	
	public static final DeferredBlock<Block> BLOCK_SEPARATOR = BLOCKS.register("block_separator", () -> new BlockSeparator(Block.Properties.of().requiresCorrectToolForDrops().strength(6.0F, 10.0F).noOcclusion().dynamicShape().sound(SoundType.METAL)));
	public static final DeferredItem<Item> ITEMBLOCK_SEPARATOR = addToDevicesTab(ITEMS.register("block_separator", () -> new ItemBlockMachine(BLOCK_SEPARATOR.get(), new Item.Properties(), "Separates Items.", "Separates items into other items using RF power", "Can be upgraded internally.")));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntitySeparator>> BLOCK_ENTITY_TYPE_SEPARATOR = BLOCK_ENTITY_TYPES.register("block_entity_separator", () -> BlockEntityType.Builder.of(BlockEntitySeparator::new, BLOCK_SEPARATOR.get()).build(null));
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerSeparator>> CONTAINER_TYPE_SEPARATOR = MENU_TYPES.register("container_separator", () -> IMenuTypeExtension.create(ContainerSeparator::new));
	
	public static final DeferredBlock<Block> BLOCK_LASER_CUTTER = BLOCKS.register("block_laser_cutter", () -> new BlockLaserCutter(Block.Properties.of().requiresCorrectToolForDrops().strength(6.0F, 10.0F).noOcclusion().dynamicShape().sound(SoundType.METAL)));
	public static final DeferredItem<Item> ITEMBLOCK_LASER_CUTTER = addToDevicesTab(ITEMS.register("block_laser_cutter", () -> new ItemBlockMachine(BLOCK_LASER_CUTTER.get(), new Item.Properties(), "A machine to cut things with a laser.", "Laser cuts things using RF power.", "Can be upgraded internally.")));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityLaserCutter>> BLOCK_ENTITY_TYPE_LASER_CUTTER = BLOCK_ENTITY_TYPES.register("block_entity_laser_cutter", () -> BlockEntityType.Builder.of(BlockEntityLaserCutter::new, BLOCK_LASER_CUTTER.get()).build(null));
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerLaserCutter>> CONTAINER_TYPE_LASER_CUTTER = MENU_TYPES.register("container_laser_cutter", () -> IMenuTypeExtension.create(ContainerLaserCutter::new));

	public static final DeferredBlock<Block> BLOCK_CHARGER = BLOCKS.register("block_charger", () -> new BlockCharger(Block.Properties.of().requiresCorrectToolForDrops().strength(6.0F, 10.0F).noOcclusion().dynamicShape().sound(SoundType.METAL)));
	public static final DeferredItem<Item> ITEMBLOCK_CHARGER = addToDevicesTab(ITEMS.register("block_charger", () -> new ItemBlockMachine(BLOCK_CHARGER.get(), new Item.Properties(), "Charges Items.", "Charges any RF enabled Item.", "Can be upgraded internally.")));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityCharger>> BLOCK_ENTITY_TYPE_CHARGER = BLOCK_ENTITY_TYPES.register("block_entity_charger", () -> BlockEntityType.Builder.of(BlockEntityCharger::new, BLOCK_CHARGER.get()).build(null));
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerCharger>> CONTAINER_TYPE_CHARGER = MENU_TYPES.register("container_charger", () -> IMenuTypeExtension.create(ContainerCharger::new));
	
	
	public static final DeferredBlock<Block> BLOCK_SYNTHESISER = BLOCKS.register("block_synthesiser", () -> new BlockSynthesiser(Block.Properties.of().requiresCorrectToolForDrops().strength(6.0F, 10.0F).noOcclusion().dynamicShape().sound(SoundType.METAL)));
	public static final DeferredItem<Item> ITEMBLOCK_SYNTHESISER = addToDevicesTab(ITEMS.register("block_synthesiser", () -> new ItemBlockMachine(BLOCK_SYNTHESISER.get(), new Item.Properties(), "Base block of the Synthesiser multiblock.", "Used in complex crafting.", "Requires Synthesiser Stands.")));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntitySynthesiser>> BLOCK_ENTITY_TYPE_SYNTHESISER = BLOCK_ENTITY_TYPES.register("block_entity_synthesiser", () -> BlockEntityType.Builder.of(BlockEntitySynthesiser::new, BLOCK_SYNTHESISER.get()).build(null));
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerSynthesiser>> CONTAINER_TYPE_SYNTHESISER = MENU_TYPES.register("container_synthesiser", () -> IMenuTypeExtension.create(ContainerSynthesiser::new));
	
	public static final DeferredBlock<Block> BLOCK_SYNTHESISER_STAND = BLOCKS.register("block_synthesiser_stand", () -> new BlockSynthesiserStand(Block.Properties.of().requiresCorrectToolForDrops().strength(6.0F, 10.0F).noOcclusion().noOcclusion().dynamicShape().sound(SoundType.METAL)));
	public static final DeferredItem<Item> ITEMBLOCK_SYNTHESISER_STAND = addToDevicesTab(ITEMS.register("block_synthesiser_stand", () -> new CosmosItemBlock(BLOCK_SYNTHESISER_STAND.get(), new Item.Properties(), "Support block for the Synthesiser multiblock.", "Used in complex crafting.", "Requires a Synthesiser to use.")));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntitySynthesiserStand>> BLOCK_ENTITY_TYPE_SYNTHESISER_STAND = BLOCK_ENTITY_TYPES.register("block_entity_synthesiser_stand", () -> BlockEntityType.Builder.of(BlockEntitySynthesiserStand::new, BLOCK_SYNTHESISER_STAND.get()).build(null));
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerSynthesiserStand>> CONTAINER_TYPE_SYNTHESISER_STAND = MENU_TYPES.register("container_synthesiser_stand", () -> IMenuTypeExtension.create(ContainerSynthesiserStand::new));
	
	
	
	/** - Storage - */
	public static final DeferredBlock<Block> BLOCK_CAPACITOR = BLOCKS.register("block_capacitor", () -> new BlockCapacitor(Block.Properties.of().requiresCorrectToolForDrops().strength(8.0F, 12.0F).noOcclusion().dynamicShape().sound(SoundType.METAL)));
	public static final DeferredItem<Item> ITEMBLOCK_CAPACITOR = addToDevicesTab(ITEMS.register("block_capacitor", () -> new CosmosItemBlock(BLOCK_CAPACITOR.get(), new Item.Properties(), "", "", "")));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityCapacitor>> BLOCK_ENTITY_TYPE_CAPACITOR = BLOCK_ENTITY_TYPES.register("block_entity_capacitor", () -> BlockEntityType.Builder.of(BlockEntityCapacitor::new, BLOCK_CAPACITOR.get()).build(null));
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerCapacitor>> CONTAINER_TYPE_CAPACITOR = MENU_TYPES.register("container_capacitor", () -> IMenuTypeExtension.create(ContainerCapacitor::new));
	
	public static final DeferredBlock<Block> BLOCK_CAPACITOR_SURGE = BLOCKS.register("block_capacitor_surge", () -> new BlockCapacitorSurge(Block.Properties.of().requiresCorrectToolForDrops().strength(8.0F, 12.0F).noOcclusion().dynamicShape().sound(SoundType.METAL)));
	public static final DeferredItem<Item> ITEMBLOCK_CAPACITOR_SURGE = addToDevicesTab(ITEMS.register("block_capacitor_surge", () -> new CosmosItemBlock(BLOCK_CAPACITOR_SURGE.get(), new Item.Properties(), "", "", "")));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityCapacitorSurge>> BLOCK_ENTITY_TYPE_CAPACITOR_SURGE = BLOCK_ENTITY_TYPES.register("block_entity_capacitor_surge", () -> BlockEntityType.Builder.of(BlockEntityCapacitorSurge::new, BLOCK_CAPACITOR_SURGE.get()).build(null));
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerCapacitorSurge>> CONTAINER_TYPE_CAPACITOR_SURGE = MENU_TYPES.register("container_capacitor_surge", () -> IMenuTypeExtension.create(ContainerCapacitorSurge::new));
	
	public static final DeferredBlock<Block> BLOCK_CAPACITOR_CREATIVE = BLOCKS.register("block_capacitor_creative", () -> new BlockCapacitorCreative(Block.Properties.of().requiresCorrectToolForDrops().strength(-1, 3600000.0F).noOcclusion().dynamicShape().sound(SoundType.METAL)));
	public static final DeferredItem<Item> ITEMBLOCK_CAPACITOR_CREATIVE = addToDevicesTab(ITEMS.register("block_capacitor_creative", () -> new CosmosItemBlock(BLOCK_CAPACITOR_CREATIVE.get(), new Item.Properties(), "", "", "")));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityCapacitorCreative>> BLOCK_ENTITY_TYPE_CAPACITOR_CREATIVE = BLOCK_ENTITY_TYPES.register("block_entity_capacitor_creative", () -> BlockEntityType.Builder.of(BlockEntityCapacitorCreative::new, BLOCK_CAPACITOR_CREATIVE.get()).build(null));
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerCapacitorCreative>> CONTAINER_TYPE_CAPACITOR_CREATIVE = MENU_TYPES.register("container_capacitor_creative", () -> IMenuTypeExtension.create(ContainerCapacitorCreative::new));
	
	
	
	/** - Transport - */
	public static final DeferredBlock<Block> BLOCK_ENERGY_CHANNEL = BLOCKS.register("block_energy_channel", () -> new BlockChannelEnergy(Block.Properties.of().requiresCorrectToolForDrops().strength(8.0F, 12.0F).sound(SoundType.METAL)));
	public static final DeferredItem<Item> ITEMBLOCK_ENERGY_CHANNEL = addToDevicesTab(ITEMS.register("block_energy_channel", () -> new CosmosItemBlock(BLOCK_ENERGY_CHANNEL.get(), new Item.Properties(), "Basic block for transporting energy.", "Transports RF.", "Max transfer rate: " + IndustryReference.RESOURCE.TRANSPORT.ENERGY[1] + " RF/t.")));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityChannelEnergy>> BLOCK_ENTITY_TYPE_CHANNEL_ENERGY = BLOCK_ENTITY_TYPES.register("block_entity_channel_energy", () -> BlockEntityType.Builder.of(BlockEntityChannelEnergy::new, BLOCK_ENERGY_CHANNEL.get()).build(null));
	
	public static final DeferredBlock<Block> BLOCK_ENERGY_CHANNEL_SURGE = BLOCKS.register("block_energy_channel_surge", () -> new BlockChannelSurgeEnergy(Block.Properties.of().requiresCorrectToolForDrops().strength(8.0F, 12.0F).sound(SoundType.METAL)));
	public static final DeferredItem<Item> ITEMBLOCK_ENERGY_CHANNEL_SURGE = addToDevicesTab(ITEMS.register("block_energy_channel_surge", () -> new CosmosItemBlock(BLOCK_ENERGY_CHANNEL_SURGE.get(), new Item.Properties(), "Advanced block for transporting energy.", "Transports RF.", "Max transfer rate: " + IndustryReference.RESOURCE.TRANSPORT.ENERGY_SURGE[1] + " RF/t.")));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityChannelSurgeEnergy>> BLOCK_ENTITY_TYPE_CHANNEL_ENERGY_SURGE = BLOCK_ENTITY_TYPES.register("block_entity_channel_energy_surge", () -> BlockEntityType.Builder.of(BlockEntityChannelSurgeEnergy::new, BLOCK_ENERGY_CHANNEL_SURGE.get()).build(null));
	
	public static final DeferredBlock<Block> BLOCK_ENERGY_CHANNEL_CREATIVE = BLOCKS.register("block_energy_channel_creative", () -> new BlockChannelCreativeEnergy(Block.Properties.of().requiresCorrectToolForDrops().strength(8.0F, 12.0F).sound(SoundType.METAL)));
	public static final DeferredItem<Item> ITEMBLOCK_ENERGY_CHANNEL_CREATIVE = addToDevicesTab(ITEMS.register("block_energy_channel_creative", () -> new CosmosItemBlock(BLOCK_ENERGY_CHANNEL_CREATIVE.get(), new Item.Properties(), "Creative block for transporting energy.", "Transports RF.", "Max transfer rate: " + IndustryReference.RESOURCE.TRANSPORT.ENERGY_CREATIVE[1] + " RF/t.")));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityChannelCreativeEnergy>> BLOCK_ENTITY_TYPE_CHANNEL_ENERGY_CREATIVE = BLOCK_ENTITY_TYPES.register("block_entity_channel_energy_creative", () -> BlockEntityType.Builder.of(BlockEntityChannelCreativeEnergy::new, BLOCK_ENERGY_CHANNEL_CREATIVE.get()).build(null));
	
	public static final DeferredBlock<Block> BLOCK_ENERGY_CHANNEL_TRANSPARENT = BLOCKS.register("block_energy_channel_transparent", () -> new BlockChannelTransparentEnergy(Block.Properties.of().requiresCorrectToolForDrops().strength(8.0F, 12.0F).sound(SoundType.METAL)));
	public static final DeferredItem<Item> ITEMBLOCK_ENERGY_CHANNEL_TRANSPARENT = addToDevicesTab(ITEMS.register("block_energy_channel_transparent", () -> new CosmosItemBlock(BLOCK_ENERGY_CHANNEL_TRANSPARENT.get(), new Item.Properties(), "Basic block for transporting energy.", "Transports RF. Clear to be able to see energy transfer.", "Max transfer rate: " + IndustryReference.RESOURCE.TRANSPORT.ENERGY[1] + " RF/t.")));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityChannelTransparentEnergy>> BLOCK_ENTITY_TYPE_CHANNEL_ENERGY_TRANSPARENT = BLOCK_ENTITY_TYPES.register("block_entity_channel_energy_transparent", () -> BlockEntityType.Builder.of(BlockEntityChannelTransparentEnergy::new, BLOCK_ENERGY_CHANNEL_TRANSPARENT.get()).build(null));
	
	public static final DeferredBlock<Block> BLOCK_ENERGY_CHANNEL_TRANSPARENT_SURGE = BLOCKS.register("block_energy_channel_transparent_surge", () -> new BlockChannelTransparentSurgeEnergy(Block.Properties.of().requiresCorrectToolForDrops().strength(8.0F, 12.0F).sound(SoundType.METAL)));
	public static final DeferredItem<Item> ITEMBLOCK_ENERGY_CHANNEL_TRANSPARENT_SURGE = addToDevicesTab(ITEMS.register("block_energy_channel_transparent_surge", () -> new CosmosItemBlock(BLOCK_ENERGY_CHANNEL_TRANSPARENT_SURGE.get(), new Item.Properties(), "Advanced block for transporting energy.", "Transports RF. Clear to be able to see energy transfer.", "Max transfer rate: " + IndustryReference.RESOURCE.TRANSPORT.ENERGY_SURGE[1] + " RF/t.")));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityChannelTransparentSurgeEnergy>> BLOCK_ENTITY_TYPE_CHANNEL_ENERGY_TRANSPARENT_SURGE = BLOCK_ENTITY_TYPES.register("block_entity_channel_energy_transparent_surge", () -> BlockEntityType.Builder.of(BlockEntityChannelTransparentSurgeEnergy::new, BLOCK_ENERGY_CHANNEL_TRANSPARENT_SURGE.get()).build(null));
	
	public static final DeferredBlock<Block> BLOCK_ENERGY_CHANNEL_TRANSPARENT_CREATIVE = BLOCKS.register("block_energy_channel_transparent_creative", () -> new BlockChannelTransparentCreativeEnergy(Block.Properties.of().requiresCorrectToolForDrops().strength(8.0F, 12.0F).sound(SoundType.METAL)));
	public static final DeferredItem<Item> ITEMBLOCK_ENERGY_CHANNEL_TRANSPARENT_CREATIVE = addToDevicesTab(ITEMS.register("block_energy_channel_transparent_creative", () -> new CosmosItemBlock(BLOCK_ENERGY_CHANNEL_TRANSPARENT_CREATIVE.get(), new Item.Properties(), "Creative block for transporting energy.", "Transports RF. Clear to be able to see energy transfer.", "Max transfer rate: " + IndustryReference.RESOURCE.TRANSPORT.ENERGY_CREATIVE[1] + " RF/t.")));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityChannelTransparentCreativeEnergy>> BLOCK_ENTITY_TYPE_CHANNEL_ENERGY_TRANSPARENT_CREATIVE = BLOCK_ENTITY_TYPES.register("block_entity_channel_energy_transparent_creative", () -> BlockEntityType.Builder.of(BlockEntityChannelTransparentCreativeEnergy::new, BLOCK_ENERGY_CHANNEL_TRANSPARENT_CREATIVE.get()).build(null));
	
	
	
	public static final DeferredBlock<Block> BLOCK_FLUID_CHANNEL = BLOCKS.register("block_fluid_channel", () -> new BlockChannelFluid(Block.Properties.of().requiresCorrectToolForDrops().strength(8.0F, 12.0F).sound(SoundType.METAL)));
	public static final DeferredItem<Item> ITEMBLOCK_FLUID_CHANNEL = addToDevicesTab(ITEMS.register("block_fluid_channel", () -> new CosmosItemBlock(BLOCK_FLUID_CHANNEL.get(), new Item.Properties(), "", "", "")));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityChannelFluid>> BLOCK_ENTITY_TYPE_CHANNEL_FLUID = BLOCK_ENTITY_TYPES.register("block_entity_channel_fluid", () -> BlockEntityType.Builder.of(BlockEntityChannelFluid::new, BLOCK_FLUID_CHANNEL.get()).build(null));
	
	public static final DeferredBlock<Block> BLOCK_FLUID_CHANNEL_SURGE = BLOCKS.register("block_fluid_channel_surge", () -> new BlockChannelSurgeFluid(Block.Properties.of().requiresCorrectToolForDrops().strength(8.0F, 12.0F).sound(SoundType.METAL)));
	public static final DeferredItem<Item> ITEMBLOCK_FLUID_CHANNEL_SURGE = addToDevicesTab(ITEMS.register("block_fluid_channel_surge", () -> new CosmosItemBlock(BLOCK_FLUID_CHANNEL_SURGE.get(), new Item.Properties(), "", "", "")));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityChannelSurgeFluid>> BLOCK_ENTITY_TYPE_CHANNEL_FLUID_SURGE = BLOCK_ENTITY_TYPES.register("block_entity_channel_fluid_surge", () -> BlockEntityType.Builder.of(BlockEntityChannelSurgeFluid::new, BLOCK_FLUID_CHANNEL_SURGE.get()).build(null));

	public static final DeferredBlock<Block> BLOCK_FLUID_CHANNEL_CREATIVE = BLOCKS.register("block_fluid_channel_creative", () -> new BlockChannelCreativeFluid(Block.Properties.of().requiresCorrectToolForDrops().strength(8.0F, 12.0F).sound(SoundType.METAL)));
	public static final DeferredItem<Item> ITEMBLOCK_FLUID_CHANNEL_CREATIVE = addToDevicesTab(ITEMS.register("block_fluid_channel_creative", () -> new CosmosItemBlock(BLOCK_FLUID_CHANNEL_CREATIVE.get(), new Item.Properties(), "", "", "")));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityChannelCreativeFluid>> BLOCK_ENTITY_TYPE_CHANNEL_FLUID_CREATIVE = BLOCK_ENTITY_TYPES.register("block_entity_channel_fluid_creative", () -> BlockEntityType.Builder.of(BlockEntityChannelCreativeFluid::new, BLOCK_FLUID_CHANNEL_CREATIVE.get()).build(null));
	
	public static final DeferredBlock<Block> BLOCK_FLUID_CHANNEL_TRANSPARENT = BLOCKS.register("block_fluid_channel_transparent", () -> new BlockChannelTransparentFluid(Block.Properties.of().requiresCorrectToolForDrops().strength(8.0F, 12.0F).sound(SoundType.METAL)));
	public static final DeferredItem<Item> ITEMBLOCK_FLUID_CHANNEL_TRANSPARENT = addToDevicesTab(ITEMS.register("block_fluid_channel_transparent", () -> new CosmosItemBlock(BLOCK_FLUID_CHANNEL_TRANSPARENT.get(), new Item.Properties(), "", "", "")));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityChannelTransparentFluid>> BLOCK_ENTITY_TYPE_CHANNEL_FLUID_TRANSPARENT = BLOCK_ENTITY_TYPES.register("block_entity_channel_fluid_transparent", () -> BlockEntityType.Builder.of(BlockEntityChannelTransparentFluid::new, BLOCK_FLUID_CHANNEL_TRANSPARENT.get()).build(null));
	
	public static final DeferredBlock<Block> BLOCK_FLUID_CHANNEL_TRANSPARENT_SURGE = BLOCKS.register("block_fluid_channel_transparent_surge", () -> new BlockChannelTransparentSurgeFluid(Block.Properties.of().requiresCorrectToolForDrops().strength(8.0F, 12.0F).sound(SoundType.METAL)));
	public static final DeferredItem<Item> ITEMBLOCK_FLUID_CHANNEL_TRANSPARENT_SURGE = addToDevicesTab(ITEMS.register("block_fluid_channel_transparent_surge", () -> new CosmosItemBlock(BLOCK_FLUID_CHANNEL_TRANSPARENT_SURGE.get(), new Item.Properties(), "", "", "")));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityChannelTransparentSurgeFluid>> BLOCK_ENTITY_TYPE_CHANNEL_FLUID_TRANSPARENT_SURGE = BLOCK_ENTITY_TYPES.register("block_entity_channel_fluid_transparent_surge", () -> BlockEntityType.Builder.of(BlockEntityChannelTransparentSurgeFluid::new, BLOCK_FLUID_CHANNEL_TRANSPARENT_SURGE.get()).build(null));
	
	public static final DeferredBlock<Block> BLOCK_FLUID_CHANNEL_TRANSPARENT_CREATIVE = BLOCKS.register("block_fluid_channel_transparent_creative", () -> new BlockChannelTransparentCreativeFluid(Block.Properties.of().requiresCorrectToolForDrops().strength(8.0F, 12.0F).sound(SoundType.METAL)));
	public static final DeferredItem<Item> ITEMBLOCK_FLUID_CHANNEL_TRANSPARENT_CREATIVE = addToDevicesTab(ITEMS.register("block_fluid_channel_transparent_creative", () -> new CosmosItemBlock(BLOCK_FLUID_CHANNEL_TRANSPARENT_CREATIVE.get(), new Item.Properties(), "", "", "")));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityChannelTransparentCreativeFluid>> BLOCK_ENTITY_TYPE_CHANNEL_FLUID_TRANSPARENT_CREATIVE = BLOCK_ENTITY_TYPES.register("block_entity_channel_fluid_transparent_creative", () -> BlockEntityType.Builder.of(BlockEntityChannelTransparentCreativeFluid::new, BLOCK_FLUID_CHANNEL_TRANSPARENT_CREATIVE.get()).build(null));

	public static void register(IEventBus bus) {
		ITEMS.register(bus);
		BLOCKS.register(bus);
		BLOCK_ENTITY_TYPES.register(bus);
		MENU_TYPES.register(bus);
		TABS.register(bus);
	}
	
	@SubscribeEvent
	public static void onBlockEntityRendererRegistry(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(BLOCK_ENTITY_TYPE_CHANNEL_ENERGY.get(), RendererEnergyChannel::new);
		event.registerBlockEntityRenderer(BLOCK_ENTITY_TYPE_CHANNEL_ENERGY_SURGE.get(), RendererEnergyChannel::new);
		event.registerBlockEntityRenderer(BLOCK_ENTITY_TYPE_CHANNEL_ENERGY_CREATIVE.get(), RendererEnergyChannel::new);
		event.registerBlockEntityRenderer(BLOCK_ENTITY_TYPE_CHANNEL_ENERGY_TRANSPARENT.get(), RendererEnergyChannelTransparent::new);
		event.registerBlockEntityRenderer(BLOCK_ENTITY_TYPE_CHANNEL_ENERGY_TRANSPARENT_SURGE.get(), RendererEnergyChannelTransparent::new);
		event.registerBlockEntityRenderer(BLOCK_ENTITY_TYPE_CHANNEL_ENERGY_TRANSPARENT_CREATIVE.get(), RendererEnergyChannelTransparent::new);

		event.registerBlockEntityRenderer(BLOCK_ENTITY_TYPE_CHANNEL_FLUID.get(), RendererFluidChannel::new);
		event.registerBlockEntityRenderer(BLOCK_ENTITY_TYPE_CHANNEL_FLUID_SURGE.get(), RendererFluidChannel::new);
		event.registerBlockEntityRenderer(BLOCK_ENTITY_TYPE_CHANNEL_FLUID_CREATIVE.get(), RendererFluidChannel::new);
		event.registerBlockEntityRenderer(BLOCK_ENTITY_TYPE_CHANNEL_FLUID_TRANSPARENT.get(), RendererFluidChannelTransparent::new);
		event.registerBlockEntityRenderer(BLOCK_ENTITY_TYPE_CHANNEL_FLUID_TRANSPARENT_SURGE.get(), RendererFluidChannelTransparent::new);
		event.registerBlockEntityRenderer(BLOCK_ENTITY_TYPE_CHANNEL_FLUID_TRANSPARENT_CREATIVE.get(), RendererFluidChannelTransparent::new);

		event.registerBlockEntityRenderer(BLOCK_ENTITY_TYPE_KILN.get(), RendererKiln::new);
		event.registerBlockEntityRenderer(BLOCK_ENTITY_TYPE_GRINDER.get(), RendererGrinder::new);
		event.registerBlockEntityRenderer(BLOCK_ENTITY_TYPE_COMPACTOR.get(), RendererCompactor::new);
		event.registerBlockEntityRenderer(BLOCK_ENTITY_TYPE_SEPARATOR.get(), RendererSeparator::new);
		event.registerBlockEntityRenderer(BLOCK_ENTITY_TYPE_LASER_CUTTER.get(), RendererLaserCutter::new);
		
		event.registerBlockEntityRenderer(BLOCK_ENTITY_TYPE_SYNTHESISER.get(), RendererSynthesiser::new);
		event.registerBlockEntityRenderer(BLOCK_ENTITY_TYPE_SYNTHESISER_STAND.get(), RendererSynthesiserStand::new);

		event.registerBlockEntityRenderer(BLOCK_ENTITY_TYPE_CAPACITOR.get(), RendererCapacitor::new);
		event.registerBlockEntityRenderer(BLOCK_ENTITY_TYPE_CAPACITOR_SURGE.get(), RendererCapacitor::new);
		event.registerBlockEntityRenderer(BLOCK_ENTITY_TYPE_CAPACITOR_CREATIVE.get(), RendererCapacitor::new);

		CosmosIndustry.CONSOLE.startup("BlockEntityRenderer Registration complete.");
	}

	@SubscribeEvent
	public static void registerMenuScreensEvent(RegisterMenuScreensEvent event) {
		event.register(CONTAINER_TYPE_KILN.get(), ScreenKiln::new);
		event.register(CONTAINER_TYPE_GRINDER.get(), ScreenGrinder::new);
		event.register(CONTAINER_TYPE_COMPACTOR.get(), ScreenCompactor::new);
		event.register(CONTAINER_TYPE_CHARGER.get(), ScreenCharger::new);
		event.register(CONTAINER_TYPE_SEPARATOR.get(), ScreenSeparator::new);
		event.register(CONTAINER_TYPE_LASER_CUTTER.get(), ScreenLaserCutter::new);
		event.register(CONTAINER_TYPE_SYNTHESISER.get(), ScreenSynthesiser::new);
		
		event.register(CONTAINER_TYPE_CAPACITOR.get(), ScreenCapacitor::new);
		event.register(CONTAINER_TYPE_CAPACITOR_SURGE.get(), ScreenCapacitor::new);
		event.register(CONTAINER_TYPE_CAPACITOR_CREATIVE.get(), ScreenCapacitor::new);
	}
	
	@SubscribeEvent
	private static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, BLOCK_ENTITY_TYPE_CHARGER.get(), (myBlockEntity, side) -> myBlockEntity.createEnergyProxy(side));
		event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, BLOCK_ENTITY_TYPE_COMPACTOR.get(), (myBlockEntity, side) -> myBlockEntity.createEnergyProxy(side));
		event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, BLOCK_ENTITY_TYPE_GRINDER.get(), (myBlockEntity, side) -> myBlockEntity.createEnergyProxy(side));
		event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, BLOCK_ENTITY_TYPE_KILN.get(), (myBlockEntity, side) -> myBlockEntity.createEnergyProxy(side));
		event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, BLOCK_ENTITY_TYPE_LASER_CUTTER.get(), (myBlockEntity, side) -> myBlockEntity.createEnergyProxy(side));
		event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, BLOCK_ENTITY_TYPE_SEPARATOR.get(), (myBlockEntity, side) -> myBlockEntity.createEnergyProxy(side));
		event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, BLOCK_ENTITY_TYPE_SYNTHESISER.get(), (myBlockEntity, side) -> myBlockEntity.createEnergyProxy(side));
	
		event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, BLOCK_ENTITY_TYPE_CAPACITOR.get(), (myBlockEntity, side) -> myBlockEntity.createEnergyProxy(side));
		event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, BLOCK_ENTITY_TYPE_CAPACITOR_SURGE.get(), (myBlockEntity, side) -> myBlockEntity.createEnergyProxy(side));
		event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, BLOCK_ENTITY_TYPE_CAPACITOR_CREATIVE.get(), (myBlockEntity, side) -> myBlockEntity.createEnergyProxy(side));

		event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, BLOCK_ENTITY_TYPE_CHANNEL_ENERGY.get(), (myBlockEntity, side) -> myBlockEntity.createEnergyProxy(side));
		event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, BLOCK_ENTITY_TYPE_CHANNEL_ENERGY_SURGE.get(), (myBlockEntity, side) -> myBlockEntity.createEnergyProxy(side));
		event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, BLOCK_ENTITY_TYPE_CHANNEL_ENERGY_CREATIVE.get(), (myBlockEntity, side) -> myBlockEntity.createEnergyProxy(side));
		event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, BLOCK_ENTITY_TYPE_CHANNEL_ENERGY_TRANSPARENT.get(), (myBlockEntity, side) -> myBlockEntity.createEnergyProxy(side));
		event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, BLOCK_ENTITY_TYPE_CHANNEL_ENERGY_TRANSPARENT_SURGE.get(), (myBlockEntity, side) -> myBlockEntity.createEnergyProxy(side));
		event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, BLOCK_ENTITY_TYPE_CHANNEL_ENERGY_TRANSPARENT_CREATIVE.get(), (myBlockEntity, side) -> myBlockEntity.createEnergyProxy(side));
	}

	@OnlyIn(Dist.CLIENT)
	public static void onFMLClientSetup(FMLClientSetupEvent event) {		
		CosmosRuntimeHelper.setRenderLayers(RenderType.cutoutMipped(),
			BLOCK_STRUCTURE.get(), BLOCK_KILN.get(), BLOCK_GRINDER.get(), BLOCK_COMPACTOR.get(), BLOCK_CHARGER.get(), BLOCK_SEPARATOR.get(),
			BLOCK_SYNTHESISER.get(), BLOCK_SYNTHESISER_STAND.get(),
			BLOCK_ENERGY_CHANNEL.get(), BLOCK_ENERGY_CHANNEL_SURGE.get(), BLOCK_ENERGY_CHANNEL_CREATIVE.get(), 
			BLOCK_FLUID_CHANNEL.get(), BLOCK_FLUID_CHANNEL_SURGE.get(), BLOCK_FLUID_CHANNEL_CREATIVE.get(), 
			BLOCK_CAPACITOR.get(), BLOCK_CAPACITOR_SURGE.get(), BLOCK_CAPACITOR_CREATIVE.get()
		);
		
		CosmosRuntimeHelper.setRenderLayers(RenderType.translucent(),
			BLOCK_ENERGY_CHANNEL_TRANSPARENT.get(), BLOCK_ENERGY_CHANNEL_TRANSPARENT_SURGE.get(), BLOCK_ENERGY_CHANNEL_TRANSPARENT_CREATIVE.get(),
			BLOCK_FLUID_CHANNEL_TRANSPARENT.get(), BLOCK_FLUID_CHANNEL_TRANSPARENT_SURGE.get(), BLOCK_FLUID_CHANNEL_TRANSPARENT_CREATIVE.get(),
			BLOCK_LASER_CUTTER.get()
		);
	}
	
    public static <T extends Item> DeferredItem<T> addToBlockTab(DeferredItem<T> itemLike) {
        TAB_BLOCKS.add(itemLike);
        return itemLike;
    }

    public static <A extends Block> DeferredBlock<A> addToBlockTab_(DeferredBlock<A> itemLike) {
        TAB_BLOCKS.add(itemLike);
        return itemLike;
    }
    

    public static <T extends Item> DeferredItem<T> addToItemTab(DeferredItem<T> itemLike) {
        TAB_ITEMS.add(itemLike);
        return itemLike;
    }

    public static <T extends Item> DeferredItem<T> addToDevicesTab(DeferredItem<T> itemLike) {
        TAB_DEVICES.add(itemLike);
        return itemLike;
    }

    public static <T extends Block> DeferredBlock<T> addToDevicesTabA(DeferredBlock<T> itemLike) {
        TAB_DEVICES.add(itemLike);
        return itemLike;
    }
}