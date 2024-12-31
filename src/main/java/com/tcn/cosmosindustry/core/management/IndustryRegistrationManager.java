package com.tcn.cosmosindustry.core.management;

import java.util.ArrayList;
import java.util.function.Supplier;

import org.joml.Vector3f;

import com.tcn.cosmosindustry.CosmosIndustry;
import com.tcn.cosmosindustry.IndustryReference;
import com.tcn.cosmosindustry.core.fluid.CoolantFluid;
import com.tcn.cosmosindustry.core.fluid.EnergizedRedstoneFluid;
import com.tcn.cosmosindustry.core.item.IndustryItemRarity;
import com.tcn.cosmosindustry.processing.client.container.ContainerCharger;
import com.tcn.cosmosindustry.processing.client.container.ContainerCompactor;
import com.tcn.cosmosindustry.processing.client.container.ContainerFluidCrafter;
import com.tcn.cosmosindustry.processing.client.container.ContainerGrinder;
import com.tcn.cosmosindustry.processing.client.container.ContainerKiln;
import com.tcn.cosmosindustry.processing.client.container.ContainerLaserCutter;
import com.tcn.cosmosindustry.processing.client.container.ContainerOrePlant;
import com.tcn.cosmosindustry.processing.client.container.ContainerSeparator;
import com.tcn.cosmosindustry.processing.client.container.ContainerSynthesiser;
import com.tcn.cosmosindustry.processing.client.container.ContainerSynthesiserStand;
import com.tcn.cosmosindustry.processing.client.renderer.RendererCompactor;
import com.tcn.cosmosindustry.processing.client.renderer.RendererFluidCrafter;
import com.tcn.cosmosindustry.processing.client.renderer.RendererGrinder;
import com.tcn.cosmosindustry.processing.client.renderer.RendererKiln;
import com.tcn.cosmosindustry.processing.client.renderer.RendererLaserCutter;
import com.tcn.cosmosindustry.processing.client.renderer.RendererOrePlant;
import com.tcn.cosmosindustry.processing.client.renderer.RendererSeparator;
import com.tcn.cosmosindustry.processing.client.renderer.RendererSynthesiser;
import com.tcn.cosmosindustry.processing.client.renderer.RendererSynthesiserStand;
import com.tcn.cosmosindustry.processing.client.screen.ScreenCharger;
import com.tcn.cosmosindustry.processing.client.screen.ScreenCompactor;
import com.tcn.cosmosindustry.processing.client.screen.ScreenFluidCrafter;
import com.tcn.cosmosindustry.processing.client.screen.ScreenGrinder;
import com.tcn.cosmosindustry.processing.client.screen.ScreenKiln;
import com.tcn.cosmosindustry.processing.client.screen.ScreenLaserCutter;
import com.tcn.cosmosindustry.processing.client.screen.ScreenOrePlant;
import com.tcn.cosmosindustry.processing.client.screen.ScreenSeparator;
import com.tcn.cosmosindustry.processing.client.screen.ScreenSynthesiser;
import com.tcn.cosmosindustry.processing.core.block.BlockCharger;
import com.tcn.cosmosindustry.processing.core.block.BlockCompactor;
import com.tcn.cosmosindustry.processing.core.block.BlockFluidCrafter;
import com.tcn.cosmosindustry.processing.core.block.BlockGrinder;
import com.tcn.cosmosindustry.processing.core.block.BlockKiln;
import com.tcn.cosmosindustry.processing.core.block.BlockLaserCutter;
import com.tcn.cosmosindustry.processing.core.block.BlockOrePlant;
import com.tcn.cosmosindustry.processing.core.block.BlockSeparator;
import com.tcn.cosmosindustry.processing.core.block.BlockStructure;
import com.tcn.cosmosindustry.processing.core.block.BlockSynthesiser;
import com.tcn.cosmosindustry.processing.core.block.BlockSynthesiserStand;
import com.tcn.cosmosindustry.processing.core.block.ItemBlockMachine;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntityCharger;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntityCompactor;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntityFluidCrafter;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntityGrinder;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntityKiln;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntityLaserCutter;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntityOrePlant;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntitySeparator;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntitySynthesiser;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntitySynthesiserStand;
import com.tcn.cosmosindustry.production.client.container.ContainerLiquidFuel;
import com.tcn.cosmosindustry.production.client.container.ContainerSolarPanel;
import com.tcn.cosmosindustry.production.client.container.ContainerSolidFuel;
import com.tcn.cosmosindustry.production.client.renderer.RendererLiquidFuel;
import com.tcn.cosmosindustry.production.client.renderer.RendererSolarPanel;
import com.tcn.cosmosindustry.production.client.renderer.RendererSolidFuel;
import com.tcn.cosmosindustry.production.client.screen.ScreenLiquidFuel;
import com.tcn.cosmosindustry.production.client.screen.ScreenSolarPanel;
import com.tcn.cosmosindustry.production.client.screen.ScreenSolidFuel;
import com.tcn.cosmosindustry.production.core.block.BlockLiquidFuel;
import com.tcn.cosmosindustry.production.core.block.BlockSolarPanel;
import com.tcn.cosmosindustry.production.core.block.BlockSolidFuel;
import com.tcn.cosmosindustry.production.core.blockentity.BlockEntityLiquidFuel;
import com.tcn.cosmosindustry.production.core.blockentity.BlockEntitySolarPanel;
import com.tcn.cosmosindustry.production.core.blockentity.BlockEntitySolidFuel;
import com.tcn.cosmosindustry.storage.client.container.ContainerCapacitor;
import com.tcn.cosmosindustry.storage.client.container.ContainerCapacitorCreative;
import com.tcn.cosmosindustry.storage.client.container.ContainerCapacitorSurge;
import com.tcn.cosmosindustry.storage.client.container.ContainerFluidTank;
import com.tcn.cosmosindustry.storage.client.container.ContainerFluidTankCreative;
import com.tcn.cosmosindustry.storage.client.container.ContainerFluidTankSurge;
import com.tcn.cosmosindustry.storage.client.renderer.RendererCapacitor;
import com.tcn.cosmosindustry.storage.client.renderer.RendererFluidTank;
import com.tcn.cosmosindustry.storage.client.screen.ScreenCapacitor;
import com.tcn.cosmosindustry.storage.client.screen.ScreenCapacitorCreative;
import com.tcn.cosmosindustry.storage.client.screen.ScreenCapacitorSurge;
import com.tcn.cosmosindustry.storage.client.screen.ScreenFluidTank;
import com.tcn.cosmosindustry.storage.client.screen.ScreenFluidTankCreative;
import com.tcn.cosmosindustry.storage.client.screen.ScreenFluidTankSurge;
import com.tcn.cosmosindustry.storage.core.block.BlockCapacitor;
import com.tcn.cosmosindustry.storage.core.block.BlockCapacitorCreative;
import com.tcn.cosmosindustry.storage.core.block.BlockCapacitorSurge;
import com.tcn.cosmosindustry.storage.core.block.BlockFluidTank;
import com.tcn.cosmosindustry.storage.core.block.BlockFluidTankCreative;
import com.tcn.cosmosindustry.storage.core.block.BlockFluidTankSurge;
import com.tcn.cosmosindustry.storage.core.block.ItemBlockCapacitor;
import com.tcn.cosmosindustry.storage.core.block.ItemBlockFluidTank;
import com.tcn.cosmosindustry.storage.core.blockentity.BlockEntityCapacitor;
import com.tcn.cosmosindustry.storage.core.blockentity.BlockEntityCapacitorCreative;
import com.tcn.cosmosindustry.storage.core.blockentity.BlockEntityCapacitorSurge;
import com.tcn.cosmosindustry.storage.core.blockentity.BlockEntityFluidTank;
import com.tcn.cosmosindustry.storage.core.blockentity.BlockEntityFluidTankCreative;
import com.tcn.cosmosindustry.storage.core.blockentity.BlockEntityFluidTankSurge;
import com.tcn.cosmosindustry.storage.core.item.ItemEnergyCell;
import com.tcn.cosmosindustry.transport.client.renderer.RendererEnergyChannel;
import com.tcn.cosmosindustry.transport.client.renderer.RendererFluidChannel;
import com.tcn.cosmosindustry.transport.core.energy.block.BlockChannelCreativeEnergy;
import com.tcn.cosmosindustry.transport.core.energy.block.BlockChannelEnergy;
import com.tcn.cosmosindustry.transport.core.energy.block.BlockChannelSurgeEnergy;
import com.tcn.cosmosindustry.transport.core.energy.blockentity.BlockEntityChannelCreativeEnergy;
import com.tcn.cosmosindustry.transport.core.energy.blockentity.BlockEntityChannelEnergy;
import com.tcn.cosmosindustry.transport.core.energy.blockentity.BlockEntityChannelSurgeEnergy;
import com.tcn.cosmosindustry.transport.core.fluid.block.BlockChannelCreativeFluid;
import com.tcn.cosmosindustry.transport.core.fluid.block.BlockChannelFluid;
import com.tcn.cosmosindustry.transport.core.fluid.block.BlockChannelSurgeFluid;
import com.tcn.cosmosindustry.transport.core.fluid.blockentity.BlockEntityChannelCreativeFluid;
import com.tcn.cosmosindustry.transport.core.fluid.blockentity.BlockEntityChannelFluid;
import com.tcn.cosmosindustry.transport.core.fluid.blockentity.BlockEntityChannelSurgeFluid;
import com.tcn.cosmoslibrary.common.block.CosmosBlock;
import com.tcn.cosmoslibrary.common.block.CosmosBlockModelUnplaceable;
import com.tcn.cosmoslibrary.common.block.CosmosFluidBlock;
import com.tcn.cosmoslibrary.common.block.CosmosItemBlock;
import com.tcn.cosmoslibrary.common.enums.EnumIndustryTier;
import com.tcn.cosmoslibrary.common.fluid.CosmosFluidType;
import com.tcn.cosmoslibrary.common.item.CosmosCraftingItem;
import com.tcn.cosmoslibrary.common.item.CosmosItem;
import com.tcn.cosmoslibrary.common.item.CosmosItemTool;
import com.tcn.cosmoslibrary.common.item.CosmosItemUpgradeEnergy;
import com.tcn.cosmoslibrary.common.item.CosmosItemUpgradeFluid;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.runtime.CosmosRuntimeHelper;
import com.tcn.cosmoslibrary.energy.item.CosmosEnergyItem;
import com.tcn.cosmoslibrary.energy.item.CosmosEnergyStorageItem;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.wrappers.FluidBucketWrapper;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

@SuppressWarnings("deprecation")
@EventBusSubscriber(modid = CosmosIndustry.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class IndustryRegistrationManager {

	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CosmosIndustry.MOD_ID);
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CosmosIndustry.MOD_ID);
	public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(BuiltInRegistries.FLUID, CosmosIndustry.MOD_ID);
	public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, CosmosIndustry.MOD_ID);
	
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, CosmosIndustry.MOD_ID);
	public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(BuiltInRegistries.MENU, CosmosIndustry.MOD_ID);

	public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CosmosIndustry.MOD_ID);

	public static final ArrayList<Supplier<? extends ItemLike>> TAB_BLOCKS = new ArrayList<>();
	public static final ArrayList<Supplier<? extends ItemLike>> TAB_DEVICES = new ArrayList<>();
	public static final ArrayList<Supplier<? extends ItemLike>> TAB_ITEMS = new ArrayList<>();
	public static final ArrayList<Supplier<? extends ItemLike>> TAB_TOOLS = new ArrayList<>();

	public static final Supplier<CreativeModeTab> BLOCKS_GROUP = TABS.register("cosmosindustry.blocks", 
		() -> CreativeModeTab.builder()
			.title(ComponentHelper.style(ComponentColour.GRAY, "itemGroup.cosmosindustry.blocks")).icon(() -> { return new ItemStack(IndustryRegistrationManager.BLOCK_ORE_TIN.get()); })
			.displayItems((params, output) -> TAB_BLOCKS.forEach(itemLike -> output.accept(itemLike.get()))).build()
	);

	public static final Supplier<CreativeModeTab> DEVICES_GROUP = TABS.register("cosmosindustry.devices", 
		() -> CreativeModeTab.builder()
			.title(ComponentHelper.style(ComponentColour.GRAY, "itemGroup.cosmosindustry.devices")).icon(() -> { return new ItemStack(IndustryRegistrationManager.BLOCK_STRUCTURE.get()); })
			.displayItems((params, output) -> TAB_DEVICES.forEach(itemLike -> output.accept(itemLike.get()))).build()
	);

	public static final Supplier<CreativeModeTab> ITEMS_GROUP = TABS.register("cosmosindustry.items", 
		() -> CreativeModeTab.builder()
			.title(ComponentHelper.style(ComponentColour.GRAY, "itemGroup.cosmosindustry.items")).icon(() -> { return new ItemStack(IndustryRegistrationManager.IRON_DUST.get()); })
			.displayItems((params, output) -> TAB_ITEMS.forEach(itemLike -> output.accept(itemLike.get()))).build()
	);

	public static final Supplier<CreativeModeTab> TOOLS_GROUP = TABS.register("cosmosindustry.tools", 
		() -> CreativeModeTab.builder()
			.title(ComponentHelper.style(ComponentColour.GRAY, "itemGroup.cosmosindustry.tools")).icon(() -> { return new ItemStack(IndustryRegistrationManager.MACHINE_WRENCH.get()); })
			.displayItems((params, output) -> TAB_TOOLS.forEach(itemLike -> output.accept(itemLike.get()))).build()
	);

	public static final Rarity RARITY_SURGE = IndustryItemRarity.SURGE.getValue();
	public static final Rarity RARITY_CREATIVE = IndustryItemRarity.CREATIVE.getValue();
	
	/** -- ITEM START -- */
	public static final DeferredItem<Item> STONE_DUST = addToItemTab(ITEMS.register("stone_dust", () -> new CosmosItem(new Item.Properties())));
	public static final DeferredItem<Item> COAL_DUST = addToItemTab(ITEMS.register("coal_dust", () -> new CosmosItem(new Item.Properties())));
	
	public static final DeferredItem<Item> IRON_DUST = addToItemTab(ITEMS.register("iron_dust", () -> new CosmosItem(new Item.Properties())));
	public static final DeferredItem<Item> IRON_DUST_CLEAN = addToItemTab(ITEMS.register("iron_dust_clean", () -> new CosmosItem(new Item.Properties())));
	public static final DeferredItem<Item> IRON_DUST_REFINE = addToItemTab(ITEMS.register("iron_dust_refine", () -> new CosmosItem(new Item.Properties())));
	public static final DeferredItem<Item> IRON_PLATE = addToItemTab(ITEMS.register("iron_plate", () -> new CosmosItem(new Item.Properties())));
	
	public static final DeferredItem<Item> COPPER_DUST = addToItemTab(ITEMS.register("copper_dust", () -> new CosmosItem(new Item.Properties())));
	public static final DeferredItem<Item> COPPER_PLATE = addToItemTab(ITEMS.register("copper_plate", () -> new CosmosItem(new Item.Properties())));

	public static final DeferredItem<Item> GOLD_DUST = addToItemTab(ITEMS.register("gold_dust", () -> new CosmosItem(new Item.Properties())));
	public static final DeferredItem<Item> GOLD_DUST_CLEAN = addToItemTab(ITEMS.register("gold_dust_clean", () -> new CosmosItem(new Item.Properties())));
	public static final DeferredItem<Item> GOLD_DUST_REFINE = addToItemTab(ITEMS.register("gold_dust_refine", () -> new CosmosItem(new Item.Properties())));
	public static final DeferredItem<Item> GOLD_PLATE = addToItemTab(ITEMS.register("gold_plate", () -> new CosmosItem(new Item.Properties())));

	public static final DeferredItem<Item> DIAMOND_DUST = addToItemTab(ITEMS.register("diamond_dust", () -> new CosmosItem(new Item.Properties())));
	public static final DeferredItem<Item> DIAMOND_PLATE = addToItemTab(ITEMS.register("diamond_plate", () -> new CosmosItem(new Item.Properties())));
	
	public static final DeferredItem<Item> TIN_INGOT = addToItemTab(ITEMS.register("tin_ingot", () -> new CosmosItem(new Item.Properties())));
	public static final DeferredItem<Item> TIN_DUST = addToItemTab(ITEMS.register("tin_dust", () -> new CosmosItem(new Item.Properties())));
	public static final DeferredItem<Item> TIN_PLATE = addToItemTab(ITEMS.register("tin_plate", () -> new CosmosItem(new Item.Properties())));
	
	public static final DeferredItem<Item> SILVER_INGOT = addToItemTab(ITEMS.register("silver_ingot", () -> new CosmosItem(new Item.Properties())));
	public static final DeferredItem<Item> SILVER_DUST = addToItemTab(ITEMS.register("silver_dust", () -> new CosmosItem(new Item.Properties())));
	public static final DeferredItem<Item> SILVER_PLATE = addToItemTab(ITEMS.register("silver_plate", () -> new CosmosItem(new Item.Properties())));

	public static final DeferredItem<Item> ZINC_INGOT = addToItemTab(ITEMS.register("zinc_ingot", () -> new CosmosItem(new Item.Properties())));
	public static final DeferredItem<Item> ZINC_DUST = addToItemTab(ITEMS.register("zinc_dust", () -> new CosmosItem(new Item.Properties())));
	public static final DeferredItem<Item> ZINC_PLATE = addToItemTab(ITEMS.register("zinc_plate", () -> new CosmosItem(new Item.Properties())));
	
	public static final DeferredItem<Item> BRONZE_INGOT = addToItemTab(ITEMS.register("bronze_ingot", () -> new CosmosItem(new Item.Properties())));
	public static final DeferredItem<Item> BRONZE_DUST = addToItemTab(ITEMS.register("bronze_dust", () -> new CosmosItem(new Item.Properties())));
	public static final DeferredItem<Item> BRONZE_PLATE = addToItemTab(ITEMS.register("bronze_plate", () -> new CosmosItem(new Item.Properties())));
	
	public static final DeferredItem<Item> BRASS_INGOT = addToItemTab(ITEMS.register("brass_ingot", () -> new CosmosItem(new Item.Properties())));
	public static final DeferredItem<Item> BRASS_DUST = addToItemTab(ITEMS.register("brass_dust", () -> new CosmosItem(new Item.Properties())));
	public static final DeferredItem<Item> BRASS_PLATE = addToItemTab(ITEMS.register("brass_plate", () -> new CosmosItem(new Item.Properties())));
	
	public static final DeferredItem<Item> STEEL_INGOT = addToItemTab(ITEMS.register("steel_ingot", () -> new CosmosItem(new Item.Properties())));
	public static final DeferredItem<Item> STEEL_INGOT_UNREFINED = addToItemTab(ITEMS.register("steel_ingot_unrefined", () -> new CosmosItem(new Item.Properties())));
	public static final DeferredItem<Item> STEEL_DUST = addToItemTab(ITEMS.register("steel_dust", () -> new CosmosItem(new Item.Properties())));
	public static final DeferredItem<Item> STEEL_PLATE = addToItemTab(ITEMS.register("steel_plate", () -> new CosmosItem(new Item.Properties())));
	public static final DeferredItem<Item> STEEL_ROD = addToItemTab(ITEMS.register("steel_rod", () -> new CosmosItem(new Item.Properties())));
	
	public static final DeferredItem<Item> ENERGY_INGOT = addToItemTab(ITEMS.register("energy_ingot", () -> new CosmosItem(new Item.Properties())));
	public static final DeferredItem<Item> ENERGY_DUST = addToItemTab(ITEMS.register("energy_dust", () -> new CosmosItem(new Item.Properties())));
	public static final DeferredItem<Item> ENERGY_WAFER = addToItemTab(ITEMS.register("energy_wafer", () -> new CosmosItem(new Item.Properties())));

	public static final DeferredItem<Item> LAPIS_INGOT = addToItemTab(ITEMS.register("lapis_ingot", () -> new CosmosItem(new Item.Properties())));
	
	public static final DeferredItem<Item> SILICON = addToItemTab(ITEMS.register("silicon", () -> new CosmosItem(new Item.Properties())));
	public static final DeferredItem<Item> SILICON_REFINED = addToItemTab(ITEMS.register("silicon_refined", () -> new CosmosItem(new Item.Properties())));
	public static final DeferredItem<Item> SILICON_INGOT = addToItemTab(ITEMS.register("silicon_ingot", () -> new CosmosItem(new Item.Properties())));
	public static final DeferredItem<Item> SILICON_WAFER = addToItemTab(ITEMS.register("silicon_wafer", () -> new CosmosItem(new Item.Properties())));

	public static final DeferredItem<Item> BARK = addToItemTab(ITEMS.register("bark", () -> new CosmosItem(new Item.Properties())));
	
	public static final DeferredItem<Item> RUBBER_RAW = addToItemTab(ITEMS.register("rubber_raw", () -> new CosmosItem(new Item.Properties())));
	public static final DeferredItem<Item> RUBBER = addToItemTab(ITEMS.register("rubber", () -> new CosmosItem(new Item.Properties())));
	public static final DeferredItem<Item> RUBBER_INSULATION = addToItemTab(ITEMS.register("rubber_insulation", () -> new CosmosItem(new Item.Properties())));

	public static final DeferredItem<BucketItem> BUCKET_ENERGIZED_REDSTONE = addToItemTab(ITEMS.register("energized_redstone_bucket", ()-> new BucketItem(IndustryRegistrationManager.FLUID_ENERGIZED_REDSTONE.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1))));
	public static final DeferredItem<BucketItem> BUCKET_COOLANT = addToItemTab(ITEMS.register("coolant_bucket", ()-> new BucketItem(IndustryRegistrationManager.FLUID_COOLANT.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1))));

	public static final DeferredItem<Item> TOOL_ROD = addToItemTab(ITEMS.register("tool_rod", () -> new CosmosItem(new Item.Properties())));

	
	public static final DeferredItem<Item> MACHINE_WRENCH = addToToolsTab(ITEMS.register("machine_wrench", () -> new CosmosItemTool(new Item.Properties())));
	public static final DeferredItem<Item> MACHINE_HAMMER = addToToolsTab(ITEMS.register("machine_hammer", () -> new CosmosCraftingItem(new Item.Properties(), 1, 128, 4, true)));

	public static final DeferredItem<ItemEnergyCell> ENERGY_CELL = addToToolsTab(ITEMS.register("energy_cell", () -> new ItemEnergyCell(new Item.Properties().stacksTo(1), new CosmosEnergyItem.Properties().maxEnergyStored(1000000).maxIO(50000))));
	
	public static final DeferredItem<Item> CIRCUIT_RAW = addToToolsTab(ITEMS.register("circuit_raw", () -> new CosmosItem(new Item.Properties().stacksTo(32))));
	public static final DeferredItem<Item> CIRCUIT = addToToolsTab(ITEMS.register("circuit", () -> new CosmosItem(new Item.Properties().stacksTo(32))));
	public static final DeferredItem<Item> CIRCUIT_ADVANCED_RAW = addToToolsTab(ITEMS.register("circuit_advanced_raw", () -> new CosmosItem(new Item.Properties().stacksTo(8).rarity(RARITY_SURGE))));
	public static final DeferredItem<Item> CIRCUIT_ADVANCED = addToToolsTab(ITEMS.register("circuit_advanced", () -> new CosmosItem(new Item.Properties().stacksTo(8).rarity(RARITY_SURGE))));
	
	public static final DeferredItem<Item> SOLAR_MODULE = addToToolsTab(ITEMS.register("solar_module", () -> new CosmosItem(new Item.Properties().stacksTo(32))));
	
	public static final DeferredItem<Item> UPGRADE_BASE = addToToolsTab(ITEMS.register("upgrade_base", () -> new CosmosItemUpgradeEnergy(new Item.Properties())));
	
	public static final DeferredItem<Item> UPGRADE_SPEED = addToToolsTab(ITEMS.register("upgrade_speed", () -> new CosmosItemUpgradeEnergy(new Item.Properties().stacksTo(16))));
	public static final DeferredItem<Item> UPGRADE_CAPACITY = addToToolsTab(ITEMS.register("upgrade_capacity", () -> new CosmosItemUpgradeEnergy(new Item.Properties().stacksTo(16))));
	public static final DeferredItem<Item> UPGRADE_EFFICIENCY = addToToolsTab(ITEMS.register("upgrade_efficiency", () -> new CosmosItemUpgradeEnergy(new Item.Properties().stacksTo(16))));
	
	public static final DeferredItem<Item> UPGRADE_FLUID_SPEED = addToToolsTab(ITEMS.register("upgrade_fluid_speed", () -> new CosmosItemUpgradeFluid(new Item.Properties().stacksTo(16))));
	public static final DeferredItem<Item> UPGRADE_FLUID_CAPACITY = addToToolsTab(ITEMS.register("upgrade_fluid_capacity", () -> new CosmosItemUpgradeFluid(new Item.Properties().stacksTo(16))));
	public static final DeferredItem<Item> UPGRADE_FLUID_EFFICIENCY = addToToolsTab(ITEMS.register("upgrade_fluid_efficiency", () -> new CosmosItemUpgradeFluid(new Item.Properties().stacksTo(16))));

	
	/** -- BLOCK START -- */
	public static final DeferredBlock<Block> BLOCK_ORE_TIN = BLOCKS.register("block_ore_tin", () -> new CosmosBlock(Block.Properties.of().requiresCorrectToolForDrops().mapColor(MapColor.STONE).strength(3.0F, 3.0F)));
	public static final DeferredItem<Item> ITEM_BLOCK_ORE_TIN = addToBlockTab(ITEMS.register("block_ore_tin", () -> new BlockItem(BLOCK_ORE_TIN.get(), new Item.Properties())));
	public static final DeferredBlock<Block> BLOCK_ORE_SILVER = BLOCKS.register("block_ore_silver", () -> new CosmosBlock(Block.Properties.of().requiresCorrectToolForDrops().mapColor(MapColor.STONE).strength(3.0F, 3.0F)));
	public static final DeferredItem<Item> ITEM_BLOCK_ORE_SILVER = addToBlockTab(ITEMS.register("block_ore_silver", () -> new BlockItem(BLOCK_ORE_SILVER.get(), new Item.Properties())));
	public static final DeferredBlock<Block> BLOCK_ORE_ZINC = BLOCKS.register("block_ore_zinc", () -> new CosmosBlock(Block.Properties.of().requiresCorrectToolForDrops().mapColor(MapColor.STONE).strength(3.0F, 3.0F)));
	public static final DeferredItem<Item> ITEM_BLOCK_ORE_ZINC = addToBlockTab(ITEMS.register("block_ore_zinc", () -> new BlockItem(BLOCK_ORE_ZINC.get(), new Item.Properties())));
	public static final DeferredBlock<Block> BLOCK_ORE_SILICON = BLOCKS.register("block_ore_silicon", () -> new CosmosBlock(Block.Properties.of().requiresCorrectToolForDrops().mapColor(MapColor.STONE).strength(3.0F, 3.0F)));
	public static final DeferredItem<Item> ITEM_BLOCK_ORE_SILICON = addToBlockTab(ITEMS.register("block_ore_silicon", () -> new BlockItem(BLOCK_ORE_SILICON.get(), new Item.Properties())));
	
	public static final DeferredBlock<Block> BLOCK_ORE_TIN_DEEPSLATE = BLOCKS.register("block_ore_tin_deepslate", () -> new CosmosBlock(Block.Properties.of().requiresCorrectToolForDrops().mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE)));
	public static final DeferredItem<Item> ITEM_BLOCK_ORE_TIN_DEEPSLATE = addToBlockTab(ITEMS.register("block_ore_tin_deepslate", () -> new BlockItem(BLOCK_ORE_TIN_DEEPSLATE.get(), new Item.Properties())));
	public static final DeferredBlock<Block> BLOCK_ORE_SILVER_DEEPSLATE = BLOCKS.register("block_ore_silver_deepslate", () -> new CosmosBlock(Block.Properties.of().requiresCorrectToolForDrops().mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE).strength(3.0F, 3.0F)));
	public static final DeferredItem<Item> ITEM_BLOCK_ORE_SILVER_DEEPSLATE = addToBlockTab(ITEMS.register("block_ore_silver_deepslate", () -> new BlockItem(BLOCK_ORE_SILVER_DEEPSLATE.get(), new Item.Properties())));
	public static final DeferredBlock<Block> BLOCK_ORE_ZINC_DEEPSLATE = BLOCKS.register("block_ore_zinc_deepslate", () -> new CosmosBlock(Block.Properties.of().requiresCorrectToolForDrops().mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE).strength(3.0F, 3.0F)));
	public static final DeferredItem<Item> ITEM_BLOCK_ORE_ZINC_DEEPSLATE = addToBlockTab(ITEMS.register("block_ore_zinc_deepslate", () -> new BlockItem(BLOCK_ORE_ZINC_DEEPSLATE.get(), new Item.Properties())));
	public static final DeferredBlock<Block> BLOCK_ORE_SILICON_DEEPSLATE = BLOCKS.register("block_ore_silicon_deepslate", () -> new CosmosBlock(Block.Properties.of().requiresCorrectToolForDrops().mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE).strength(3.0F, 3.0F)));
	public static final DeferredItem<Item> ITEM_BLOCK_ORE_SILICON_DEEPSLATE = addToBlockTab(ITEMS.register("block_ore_silicon_deepslate", () -> new BlockItem(BLOCK_ORE_SILICON_DEEPSLATE.get(), new Item.Properties())));
	
	public static final DeferredBlock<Block> BLOCK_TIN = BLOCKS.register("block_tin", () -> new CosmosBlock(Block.Properties.of().requiresCorrectToolForDrops().strength(5.0F, 6.0F)));
	public static final DeferredItem<Item> ITEM_BLOCK_TIN = addToBlockTab(ITEMS.register("block_tin", () -> new BlockItem(BLOCK_TIN.get(), new Item.Properties())));
	public static final DeferredBlock<Block> BLOCK_SILVER = BLOCKS.register("block_silver", () -> new CosmosBlock(Block.Properties.of().requiresCorrectToolForDrops().strength(5.0F, 6.0F)));
	public static final DeferredItem<Item> ITEM_BLOCK_SILVER = addToBlockTab(ITEMS.register("block_silver", () -> new BlockItem(BLOCK_SILVER.get(), new Item.Properties())));
	public static final DeferredBlock<Block> BLOCK_ZINC = BLOCKS.register("block_zinc", () -> new CosmosBlock(Block.Properties.of().requiresCorrectToolForDrops().strength(5.0F, 6.0F)));
	public static final DeferredItem<Item> ITEM_BLOCK_ZINC = addToBlockTab(ITEMS.register("block_zinc", () -> new BlockItem(BLOCK_ZINC.get(), new Item.Properties())));
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
	
	public static final DeferredBlock<Block> BLOCK_ENERGY_CORE = BLOCKS.register("block_energy_core", () -> new CosmosBlockModelUnplaceable(Block.Properties.of()));
	public static final DeferredItem<Item> ITEM_BLOCK_ENERGY_CORE = addToBlockTab(ITEMS.register("block_energy_core", () -> new BlockItem(BLOCK_ENERGY_CORE.get(), new Item.Properties())));

	public static final DeferredBlock<Block> BLOCK_ENERGIZED_REDSTONE = BLOCKS.register("block_energized_redstone", () -> new CosmosFluidBlock(IndustryRegistrationManager.FLUID_ENERGIZED_REDSTONE.get(), Block.Properties.of().mapColor(MapColor.COLOR_RED).replaceable().noCollission().randomTicks().strength(100.0F).lightLevel(level -> 15).pushReaction(PushReaction.DESTROY).noLootTable().liquid().sound(SoundType.EMPTY), true, DamageTypes.LAVA, 2.0F));
	public static final DeferredHolder<Fluid, EnergizedRedstoneFluid.Flowing> FLOWING_FLUID_ENERGIZED_REDSTONE = FLUIDS.register("flowing_energized_redstone", () -> new EnergizedRedstoneFluid.Flowing());
	public static final DeferredHolder<Fluid, EnergizedRedstoneFluid.Source> FLUID_ENERGIZED_REDSTONE = FLUIDS.register("energized_redstone", () -> new EnergizedRedstoneFluid.Source());

	public static final DeferredHolder<FluidType, FluidType> FLUID_TYPE_ENERGIZED_REDSTONE = FLUID_TYPES.register("energized_redstone", ()-> new CosmosFluidType(
		ResourceLocation.fromNamespaceAndPath(CosmosIndustry.MOD_ID, "block/fluid/energized_redstone_still"),
		ResourceLocation.fromNamespaceAndPath(CosmosIndustry.MOD_ID, "block/fluid/energized_redstone_flow"),
		ResourceLocation.fromNamespaceAndPath(CosmosIndustry.MOD_ID, "block/fluid/overlay"),
		0xFFB60000, new Vector3f(150F / 255F, 0F / 255F, 0F / 255F), FluidType.Properties.create().lightLevel(8).density(15).viscosity(5).canSwim(true).canDrown(true).canConvertToSource(false).canPushEntity(true).temperature(4000))
	);

	public static final DeferredBlock<Block> BLOCK_COOLANT = BLOCKS.register("block_coolant", () -> new CosmosFluidBlock(IndustryRegistrationManager.FLUID_COOLANT.get(), Block.Properties.of().mapColor(MapColor.COLOR_CYAN).replaceable().noCollission().randomTicks().strength(100.0F).lightLevel(level -> 5).pushReaction(PushReaction.DESTROY).noLootTable().liquid().sound(SoundType.EMPTY), true, DamageTypes.FREEZE, 2.0F));
	public static final DeferredHolder<Fluid, CoolantFluid.Flowing> FLOWING_FLUID_COOLANT = FLUIDS.register("flowing_coolant", () -> new CoolantFluid.Flowing());
	public static final DeferredHolder<Fluid, CoolantFluid.Source> FLUID_COOLANT = FLUIDS.register("coolant", () -> new CoolantFluid.Source());

	public static final DeferredHolder<FluidType, FluidType> FLUID_TYPE_COOLANT = FLUID_TYPES.register("coolant", ()-> new CosmosFluidType(
		ResourceLocation.fromNamespaceAndPath(CosmosIndustry.MOD_ID, "block/fluid/coolant_still"),
		ResourceLocation.fromNamespaceAndPath(CosmosIndustry.MOD_ID, "block/fluid/coolant_flow"),
		ResourceLocation.fromNamespaceAndPath(CosmosIndustry.MOD_ID, "block/fluid/overlay"),
		0xFF00C8C8, new Vector3f(0F / 255F, 200F / 255F, 200F / 255F), FluidType.Properties.create().lightLevel(5).density(5).viscosity(1).canSwim(true).canDrown(true).canConvertToSource(false).canPushEntity(true).temperature(-2000).canExtinguish(true).supportsBoating(true))
	);
	
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
	public static final DeferredItem<Item> ITEMBLOCK_SEPARATOR = addToDevicesTab(ITEMS.register("block_separator", () -> new ItemBlockMachine(BLOCK_SEPARATOR.get(), new Item.Properties(), "A machine to separate things.", "Separates items into other items using RF power", "Can be upgraded internally.")));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntitySeparator>> BLOCK_ENTITY_TYPE_SEPARATOR = BLOCK_ENTITY_TYPES.register("block_entity_separator", () -> BlockEntityType.Builder.of(BlockEntitySeparator::new, BLOCK_SEPARATOR.get()).build(null));
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerSeparator>> CONTAINER_TYPE_SEPARATOR = MENU_TYPES.register("container_separator", () -> IMenuTypeExtension.create(ContainerSeparator::new));
	
	public static final DeferredBlock<Block> BLOCK_LASER_CUTTER = BLOCKS.register("block_laser_cutter", () -> new BlockLaserCutter(Block.Properties.of().requiresCorrectToolForDrops().strength(6.0F, 10.0F).noOcclusion().dynamicShape().sound(SoundType.METAL)));
	public static final DeferredItem<Item> ITEMBLOCK_LASER_CUTTER = addToDevicesTab(ITEMS.register("block_laser_cutter", () -> new ItemBlockMachine(BLOCK_LASER_CUTTER.get(), new Item.Properties(), "A machine to cut things with a laser.", "Laser cuts things using RF power.", "Can be upgraded internally.")));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityLaserCutter>> BLOCK_ENTITY_TYPE_LASER_CUTTER = BLOCK_ENTITY_TYPES.register("block_entity_laser_cutter", () -> BlockEntityType.Builder.of(BlockEntityLaserCutter::new, BLOCK_LASER_CUTTER.get()).build(null));
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerLaserCutter>> CONTAINER_TYPE_LASER_CUTTER = MENU_TYPES.register("container_laser_cutter", () -> IMenuTypeExtension.create(ContainerLaserCutter::new));

	public static final DeferredBlock<Block> BLOCK_ORE_PLANT = BLOCKS.register("block_ore_plant", () -> new BlockOrePlant(Block.Properties.of().requiresCorrectToolForDrops().strength(6.0F, 10.0F).noOcclusion().dynamicShape().sound(SoundType.METAL)));
	public static final DeferredItem<Item> ITEMBLOCK_ORE_PLANT = addToDevicesTab(ITEMS.register("block_ore_plant", () -> new ItemBlockMachine(BLOCK_ORE_PLANT.get(), new Item.Properties(), "A block for processing ores.", "Increases ingot yeild from ores.", "Can be upgraded internally.")));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityOrePlant>> BLOCK_ENTITY_TYPE_ORE_PLANT = BLOCK_ENTITY_TYPES.register("block_entity_ore_plant", () -> BlockEntityType.Builder.of(BlockEntityOrePlant::new, BLOCK_ORE_PLANT.get()).build(null));
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerOrePlant>> CONTAINER_TYPE_ORE_PLANT = MENU_TYPES.register("container_ore_plant", () -> IMenuTypeExtension.create(ContainerOrePlant::new));

	public static final DeferredBlock<Block> BLOCK_FLUID_CRAFTER = BLOCKS.register("block_fluid_crafter", () -> new BlockFluidCrafter(Block.Properties.of().requiresCorrectToolForDrops().strength(6.0F, 10.0F).noOcclusion().dynamicShape().sound(SoundType.METAL)));
	public static final DeferredItem<Item> ITEMBLOCK_FLUID_CRAFTER = addToDevicesTab(ITEMS.register("block_fluid_crafter", () -> new ItemBlockMachine(BLOCK_FLUID_CRAFTER.get(), new Item.Properties(), "A block for crafting with fluids.", "Infuses items with fluid, or extracts fluid from items.", "Can be upgraded internally.")));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityFluidCrafter>> BLOCK_ENTITY_TYPE_FLUID_CRAFTER = BLOCK_ENTITY_TYPES.register("block_entity_fluid_crafter", () -> BlockEntityType.Builder.of(BlockEntityFluidCrafter::new, BLOCK_FLUID_CRAFTER.get()).build(null));
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerFluidCrafter>> CONTAINER_TYPE_FLUID_CRAFTER = MENU_TYPES.register("container_fluid_crafer", () -> IMenuTypeExtension.create(ContainerFluidCrafter::new));

	
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
	
	
	
	
	/** - Production - */
	public static final DeferredBlock<Block> BLOCK_GENERATOR_BASE = BLOCKS.register("block_generator_structure", () -> new BlockStructure(Block.Properties.of()));
	public static final DeferredItem<Item> ITEMBLOCK_GENERATOR_BASE = addToDevicesTab(ITEMS.register("block_generator_structure", () -> new CosmosItemBlock(BLOCK_GENERATOR_BASE.get(), new Item.Properties(), "Base block to craft generators.", "", "")));
	
	public static final DeferredBlock<Block> BLOCK_SOLAR_PANEL = BLOCKS.register("block_solar_panel", () -> new BlockSolarPanel(Block.Properties.of().requiresCorrectToolForDrops().strength(6.0F, 10.0F).noOcclusion().dynamicShape().sound(SoundType.METAL)));
	public static final DeferredItem<Item> ITEMBLOCK_SOLAR_PANEL = addToDevicesTab(ITEMS.register("block_solar_panel", () -> new ItemBlockMachine(BLOCK_SOLAR_PANEL.get(), new Item.Properties(), "A block that produces Energy during the day.", "", "")));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntitySolarPanel>> BLOCK_ENTITY_TYPE_SOLAR_PANEL = BLOCK_ENTITY_TYPES.register("block_entity_solar_panel", () -> BlockEntityType.Builder.of(BlockEntitySolarPanel::new, BLOCK_SOLAR_PANEL.get()).build(null));
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerSolarPanel>> CONTAINER_TYPE_SOLAR_PANEL = MENU_TYPES.register("container_solar_panel", () -> IMenuTypeExtension.create(ContainerSolarPanel::new));
	
	public static final DeferredBlock<Block> BLOCK_SOLID_FUEL = BLOCKS.register("block_solid_fuel", () -> new BlockSolidFuel(Block.Properties.of().requiresCorrectToolForDrops().strength(6.0F, 10.0F).noOcclusion().dynamicShape().sound(SoundType.METAL)));
	public static final DeferredItem<Item> ITEMBLOCK_SOLID_FUEL = addToDevicesTab(ITEMS.register("block_solid_fuel", () -> new ItemBlockMachine(BLOCK_SOLID_FUEL.get(), new Item.Properties(), "A block that produces energy by burning Items", "", "")));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntitySolidFuel>> BLOCK_ENTITY_TYPE_SOLID_FUEL = BLOCK_ENTITY_TYPES.register("block_entity_solid_fuel", () -> BlockEntityType.Builder.of(BlockEntitySolidFuel::new, BLOCK_SOLID_FUEL.get()).build(null));
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerSolidFuel>> CONTAINER_TYPE_SOLID_FUEL = MENU_TYPES.register("container_solid_fuel", () -> IMenuTypeExtension.create(ContainerSolidFuel::new));

	public static final DeferredBlock<Block> BLOCK_LIQUID_FUEL = BLOCKS.register("block_liquid_fuel", () -> new BlockLiquidFuel(Block.Properties.of().requiresCorrectToolForDrops().strength(6.0F, 10.0F).noOcclusion().dynamicShape().sound(SoundType.METAL)));
	public static final DeferredItem<Item> ITEMBLOCK_LIQUID_FUEL = addToDevicesTab(ITEMS.register("block_liquid_fuel", () -> new ItemBlockMachine(BLOCK_LIQUID_FUEL.get(), new Item.Properties(), "A block that produces energy by burning Liquids.", "", "")));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityLiquidFuel>> BLOCK_ENTITY_TYPE_LIQUID_FUEL = BLOCK_ENTITY_TYPES.register("block_entity_liquid_fuel", () -> BlockEntityType.Builder.of(BlockEntityLiquidFuel::new, BLOCK_LIQUID_FUEL.get()).build(null));
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerLiquidFuel>> CONTAINER_TYPE_LIQUID_FUEL = MENU_TYPES.register("container_liquid_fuel", () -> IMenuTypeExtension.create(ContainerLiquidFuel::new));
	
	
	
	
	/** - Storage - */
	public static final DeferredBlock<Block> BLOCK_CAPACITOR = BLOCKS.register("block_capacitor", () -> new BlockCapacitor(Block.Properties.of().requiresCorrectToolForDrops().strength(8.0F, 12.0F).noOcclusion().dynamicShape().sound(SoundType.METAL)));
	public static final DeferredItem<ItemBlockCapacitor> ITEMBLOCK_CAPACITOR = addToDevicesTab(ITEMS.register("block_capacitor", () -> new ItemBlockCapacitor(BLOCK_CAPACITOR.get(), new Item.Properties().stacksTo(1), "A storage block that holds 20m FE.", "", "", IndustryRegistrationManager.BLOCK_ENTITY_TYPE_CAPACITOR.getRegisteredName(), EnumIndustryTier.NORMAL, ComponentColour.RED)));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityCapacitor>> BLOCK_ENTITY_TYPE_CAPACITOR = BLOCK_ENTITY_TYPES.register("block_entity_capacitor", () -> BlockEntityType.Builder.of(BlockEntityCapacitor::new, BLOCK_CAPACITOR.get()).build(null));
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerCapacitor>> CONTAINER_TYPE_CAPACITOR = MENU_TYPES.register("container_capacitor", () -> IMenuTypeExtension.create(ContainerCapacitor::new));
	
	public static final DeferredBlock<Block> BLOCK_CAPACITOR_SURGE = BLOCKS.register("block_capacitor_surge", () -> new BlockCapacitorSurge(Block.Properties.of().requiresCorrectToolForDrops().strength(8.0F, 12.0F).noOcclusion().dynamicShape().sound(SoundType.METAL)));
	public static final DeferredItem<ItemBlockCapacitor> ITEMBLOCK_CAPACITOR_SURGE = addToDevicesTab(ITEMS.register("block_capacitor_surge", () -> new ItemBlockCapacitor(BLOCK_CAPACITOR_SURGE.get(), new Item.Properties().stacksTo(1).rarity(RARITY_SURGE), "A storage block that holds 50m FE.", "", "", IndustryRegistrationManager.BLOCK_ENTITY_TYPE_CAPACITOR_SURGE.getRegisteredName(), EnumIndustryTier.SURGE, ComponentColour.RED)));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityCapacitorSurge>> BLOCK_ENTITY_TYPE_CAPACITOR_SURGE = BLOCK_ENTITY_TYPES.register("block_entity_capacitor_surge", () -> BlockEntityType.Builder.of(BlockEntityCapacitorSurge::new, BLOCK_CAPACITOR_SURGE.get()).build(null));
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerCapacitorSurge>> CONTAINER_TYPE_CAPACITOR_SURGE = MENU_TYPES.register("container_capacitor_surge", () -> IMenuTypeExtension.create(ContainerCapacitorSurge::new));
	
	public static final DeferredBlock<Block> BLOCK_CAPACITOR_CREATIVE = BLOCKS.register("block_capacitor_creative", () -> new BlockCapacitorCreative(Block.Properties.of().requiresCorrectToolForDrops().strength(-1, 3600000.0F).noOcclusion().dynamicShape().sound(SoundType.METAL)));
	public static final DeferredItem<ItemBlockCapacitor> ITEMBLOCK_CAPACITOR_CREATIVE = addToDevicesTab(ITEMS.register("block_capacitor_creative", () -> new ItemBlockCapacitor(BLOCK_CAPACITOR_CREATIVE.get(), new Item.Properties().stacksTo(1).rarity(RARITY_CREATIVE), "A storage block that holds unlimited FE.", "", "", IndustryRegistrationManager.BLOCK_ENTITY_TYPE_CAPACITOR_CREATIVE.getRegisteredName(), EnumIndustryTier.CREATIVE, ComponentColour.RED)));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityCapacitorCreative>> BLOCK_ENTITY_TYPE_CAPACITOR_CREATIVE = BLOCK_ENTITY_TYPES.register("block_entity_capacitor_creative", () -> BlockEntityType.Builder.of(BlockEntityCapacitorCreative::new, BLOCK_CAPACITOR_CREATIVE.get()).build(null));
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerCapacitorCreative>> CONTAINER_TYPE_CAPACITOR_CREATIVE = MENU_TYPES.register("container_capacitor_creative", () -> IMenuTypeExtension.create(ContainerCapacitorCreative::new));
	
	
	public static final DeferredBlock<Block> BLOCK_FLUID_TANK = BLOCKS.register("block_fluid_tank", () -> new BlockFluidTank(Block.Properties.of().requiresCorrectToolForDrops().strength(8.0F, 12.0F).noOcclusion().dynamicShape().sound(SoundType.METAL)));
	public static final DeferredItem<ItemBlockFluidTank> ITEMBLOCK_FLUID_TANK = addToDevicesTab(ITEMS.register("block_fluid_tank", () -> new ItemBlockFluidTank(BLOCK_FLUID_TANK.get(), new Item.Properties().stacksTo(1), "A storage block that holds 64 buckets of fluid.", "Can be placed in the world and interacted with.", "Can be used inside Inventories.", IndustryRegistrationManager.BLOCK_ENTITY_TYPE_FLUID_TANK.getRegisteredName(), EnumIndustryTier.NORMAL, ComponentColour.CYAN, IndustryReference.Resource.Storage.FLUID_CAPACITY)));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityFluidTank>> BLOCK_ENTITY_TYPE_FLUID_TANK = BLOCK_ENTITY_TYPES.register("block_entity_fluid_tank", () -> BlockEntityType.Builder.of(BlockEntityFluidTank::new, BLOCK_FLUID_TANK.get()).build(null));
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerFluidTank>> CONTAINER_TYPE_FLUID_TANK = MENU_TYPES.register("container_fluid_tank", () -> IMenuTypeExtension.create(ContainerFluidTank::new));
	
	public static final DeferredBlock<Block> BLOCK_FLUID_TANK_SURGE = BLOCKS.register("block_fluid_tank_surge", () -> new BlockFluidTankSurge(Block.Properties.of().requiresCorrectToolForDrops().strength(8.0F, 12.0F).noOcclusion().dynamicShape().sound(SoundType.METAL)));
	public static final DeferredItem<ItemBlockFluidTank> ITEMBLOCK_FLUID_TANK_SURGE = addToDevicesTab(ITEMS.register("block_fluid_tank_surge", () -> new ItemBlockFluidTank(BLOCK_FLUID_TANK_SURGE.get(), new Item.Properties().stacksTo(1).rarity(RARITY_SURGE), "A storage block that holds 128 buckets of fluid.", "Can be placed in the world and interacted with.", "Can be used inside Inventories.", IndustryRegistrationManager.BLOCK_ENTITY_TYPE_FLUID_TANK_SURGE.getRegisteredName(), EnumIndustryTier.SURGE, ComponentColour.CYAN, IndustryReference.Resource.Storage.FLUID_CAPACITY_S)));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityFluidTankSurge>> BLOCK_ENTITY_TYPE_FLUID_TANK_SURGE = BLOCK_ENTITY_TYPES.register("block_entity_fluid_tank_surge", () -> BlockEntityType.Builder.of(BlockEntityFluidTankSurge::new, BLOCK_FLUID_TANK_SURGE.get()).build(null));
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerFluidTankSurge>> CONTAINER_TYPE_FLUID_TANK_SURGE = MENU_TYPES.register("container_fluid_tank_surge", () -> IMenuTypeExtension.create(ContainerFluidTankSurge::new));

	public static final DeferredBlock<Block> BLOCK_FLUID_TANK_CREATIVE = BLOCKS.register("block_fluid_tank_creative", () -> new BlockFluidTankCreative(Block.Properties.of().requiresCorrectToolForDrops().strength(8.0F, 12.0F).noOcclusion().dynamicShape().sound(SoundType.METAL)));
	public static final DeferredItem<ItemBlockFluidTank> ITEMBLOCK_FLUID_TANK_CREATIVE = addToDevicesTab(ITEMS.register("block_fluid_tank_creative", () -> new ItemBlockFluidTank(BLOCK_FLUID_TANK_CREATIVE.get(), new Item.Properties().stacksTo(1).rarity(RARITY_CREATIVE), "A storage block that holds unlimited fluid.", "Can be placed in the world and interacted with.", "Can be used inside Inventories.", IndustryRegistrationManager.BLOCK_ENTITY_TYPE_FLUID_TANK_CREATIVE.getRegisteredName(), EnumIndustryTier.CREATIVE, ComponentColour.CYAN, IndustryReference.Resource.Storage.FLUID_CAPACITY_C)));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityFluidTankCreative>> BLOCK_ENTITY_TYPE_FLUID_TANK_CREATIVE = BLOCK_ENTITY_TYPES.register("block_entity_fluid_tank_creative", () -> BlockEntityType.Builder.of(BlockEntityFluidTankCreative::new, BLOCK_FLUID_TANK_CREATIVE.get()).build(null));
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerFluidTankCreative>> CONTAINER_TYPE_FLUID_TANK_CREATIVE = MENU_TYPES.register("container_fluid_tank_creative", () -> IMenuTypeExtension.create(ContainerFluidTankCreative::new));
	
	
	
	
	/** - Transport - */
	public static final DeferredBlock<Block> BLOCK_ENERGY_CHANNEL = BLOCKS.register("block_energy_channel", () -> new BlockChannelEnergy(Block.Properties.of().requiresCorrectToolForDrops().strength(8.0F, 12.0F).sound(SoundType.METAL)));
	public static final DeferredItem<Item> ITEMBLOCK_ENERGY_CHANNEL = addToDevicesTab(ITEMS.register("block_energy_channel", () -> new CosmosItemBlock(BLOCK_ENERGY_CHANNEL.get(), new Item.Properties(), "Basic block for transporting energy.", "Transports RF.", "Max transfer rate: " + IndustryReference.Resource.Transport.ENERGY[1] + " RF/t.")));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityChannelEnergy>> BLOCK_ENTITY_TYPE_CHANNEL_ENERGY = BLOCK_ENTITY_TYPES.register("block_entity_channel_energy", () -> BlockEntityType.Builder.of(BlockEntityChannelEnergy::new, BLOCK_ENERGY_CHANNEL.get()).build(null));
	
	public static final DeferredBlock<Block> BLOCK_ENERGY_CHANNEL_SURGE = BLOCKS.register("block_energy_channel_surge", () -> new BlockChannelSurgeEnergy(Block.Properties.of().requiresCorrectToolForDrops().strength(8.0F, 12.0F).sound(SoundType.METAL)));
	public static final DeferredItem<Item> ITEMBLOCK_ENERGY_CHANNEL_SURGE = addToDevicesTab(ITEMS.register("block_energy_channel_surge", () -> new CosmosItemBlock(BLOCK_ENERGY_CHANNEL_SURGE.get(), new Item.Properties().rarity(RARITY_SURGE), "Advanced block for transporting energy.", "Transports RF.", "Max transfer rate: " + IndustryReference.Resource.Transport.ENERGY_SURGE[1] + " RF/t.")));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityChannelSurgeEnergy>> BLOCK_ENTITY_TYPE_CHANNEL_ENERGY_SURGE = BLOCK_ENTITY_TYPES.register("block_entity_channel_energy_surge", () -> BlockEntityType.Builder.of(BlockEntityChannelSurgeEnergy::new, BLOCK_ENERGY_CHANNEL_SURGE.get()).build(null));
	
	public static final DeferredBlock<Block> BLOCK_ENERGY_CHANNEL_CREATIVE = BLOCKS.register("block_energy_channel_creative", () -> new BlockChannelCreativeEnergy(Block.Properties.of().requiresCorrectToolForDrops().strength(8.0F, 12.0F).sound(SoundType.METAL)));
	public static final DeferredItem<Item> ITEMBLOCK_ENERGY_CHANNEL_CREATIVE = addToDevicesTab(ITEMS.register("block_energy_channel_creative", () -> new CosmosItemBlock(BLOCK_ENERGY_CHANNEL_CREATIVE.get(), new Item.Properties().rarity(RARITY_CREATIVE), "Creative block for transporting energy.", "Transports RF.", "Max transfer rate: " + IndustryReference.Resource.Transport.ENERGY_CREATIVE[1] + " RF/t.")));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityChannelCreativeEnergy>> BLOCK_ENTITY_TYPE_CHANNEL_ENERGY_CREATIVE = BLOCK_ENTITY_TYPES.register("block_entity_channel_energy_creative", () -> BlockEntityType.Builder.of(BlockEntityChannelCreativeEnergy::new, BLOCK_ENERGY_CHANNEL_CREATIVE.get()).build(null));
	
	
	
	public static final DeferredBlock<Block> BLOCK_FLUID_CHANNEL = BLOCKS.register("block_fluid_channel", () -> new BlockChannelFluid(Block.Properties.of().requiresCorrectToolForDrops().strength(8.0F, 12.0F).sound(SoundType.METAL)));
	public static final DeferredItem<Item> ITEMBLOCK_FLUID_CHANNEL = addToDevicesTab(ITEMS.register("block_fluid_channel", () -> new CosmosItemBlock(BLOCK_FLUID_CHANNEL.get(), new Item.Properties(), "", "", "")));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityChannelFluid>> BLOCK_ENTITY_TYPE_CHANNEL_FLUID = BLOCK_ENTITY_TYPES.register("block_entity_channel_fluid", () -> BlockEntityType.Builder.of(BlockEntityChannelFluid::new, BLOCK_FLUID_CHANNEL.get()).build(null));
	
	public static final DeferredBlock<Block> BLOCK_FLUID_CHANNEL_SURGE = BLOCKS.register("block_fluid_channel_surge", () -> new BlockChannelSurgeFluid(Block.Properties.of().requiresCorrectToolForDrops().strength(8.0F, 12.0F).sound(SoundType.METAL)));
	public static final DeferredItem<Item> ITEMBLOCK_FLUID_CHANNEL_SURGE = addToDevicesTab(ITEMS.register("block_fluid_channel_surge", () -> new CosmosItemBlock(BLOCK_FLUID_CHANNEL_SURGE.get(), new Item.Properties().rarity(RARITY_SURGE), "", "", "")));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityChannelSurgeFluid>> BLOCK_ENTITY_TYPE_CHANNEL_FLUID_SURGE = BLOCK_ENTITY_TYPES.register("block_entity_channel_fluid_surge", () -> BlockEntityType.Builder.of(BlockEntityChannelSurgeFluid::new, BLOCK_FLUID_CHANNEL_SURGE.get()).build(null));

	public static final DeferredBlock<Block> BLOCK_FLUID_CHANNEL_CREATIVE = BLOCKS.register("block_fluid_channel_creative", () -> new BlockChannelCreativeFluid(Block.Properties.of().requiresCorrectToolForDrops().strength(8.0F, 12.0F).sound(SoundType.METAL)));
	public static final DeferredItem<Item> ITEMBLOCK_FLUID_CHANNEL_CREATIVE = addToDevicesTab(ITEMS.register("block_fluid_channel_creative", () -> new CosmosItemBlock(BLOCK_FLUID_CHANNEL_CREATIVE.get(), new Item.Properties().rarity(RARITY_CREATIVE), "", "", "")));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityChannelCreativeFluid>> BLOCK_ENTITY_TYPE_CHANNEL_FLUID_CREATIVE = BLOCK_ENTITY_TYPES.register("block_entity_channel_fluid_creative", () -> BlockEntityType.Builder.of(BlockEntityChannelCreativeFluid::new, BLOCK_FLUID_CHANNEL_CREATIVE.get()).build(null));
	
	public static void register(IEventBus bus) {
		ITEMS.register(bus);
		BLOCKS.register(bus);
		
		FLUIDS.register(bus);
		FLUID_TYPES.register(bus);
		
		BLOCK_ENTITY_TYPES.register(bus);
		MENU_TYPES.register(bus);
		
		TABS.register(bus);
	}
	
	@SubscribeEvent
	public static void onBlockEntityRendererRegistry(EntityRenderersEvent.RegisterRenderers event) {
		/** -- Processing -- */
		event.registerBlockEntityRenderer(BLOCK_ENTITY_TYPE_KILN.get(), RendererKiln::new);
		event.registerBlockEntityRenderer(BLOCK_ENTITY_TYPE_GRINDER.get(), RendererGrinder::new);
		event.registerBlockEntityRenderer(BLOCK_ENTITY_TYPE_COMPACTOR.get(), RendererCompactor::new);
		event.registerBlockEntityRenderer(BLOCK_ENTITY_TYPE_SEPARATOR.get(), RendererSeparator::new);
		event.registerBlockEntityRenderer(BLOCK_ENTITY_TYPE_LASER_CUTTER.get(), RendererLaserCutter::new);
		event.registerBlockEntityRenderer(BLOCK_ENTITY_TYPE_ORE_PLANT.get(), RendererOrePlant::new);
		event.registerBlockEntityRenderer(BLOCK_ENTITY_TYPE_FLUID_CRAFTER.get(), RendererFluidCrafter::new);
		
		event.registerBlockEntityRenderer(BLOCK_ENTITY_TYPE_SYNTHESISER.get(), RendererSynthesiser::new);
		event.registerBlockEntityRenderer(BLOCK_ENTITY_TYPE_SYNTHESISER_STAND.get(), RendererSynthesiserStand::new);
		
		
		/** -- Production -- */
		event.registerBlockEntityRenderer(BLOCK_ENTITY_TYPE_SOLAR_PANEL.get(), RendererSolarPanel::new);
		event.registerBlockEntityRenderer(BLOCK_ENTITY_TYPE_SOLID_FUEL.get(), RendererSolidFuel::new);
		event.registerBlockEntityRenderer(BLOCK_ENTITY_TYPE_LIQUID_FUEL.get(), RendererLiquidFuel::new);
		

		/** -- Storage -- */
		event.registerBlockEntityRenderer(BLOCK_ENTITY_TYPE_CAPACITOR.get(), RendererCapacitor::new);
		event.registerBlockEntityRenderer(BLOCK_ENTITY_TYPE_CAPACITOR_SURGE.get(), RendererCapacitor::new);
		event.registerBlockEntityRenderer(BLOCK_ENTITY_TYPE_CAPACITOR_CREATIVE.get(), RendererCapacitor::new);

		event.registerBlockEntityRenderer(BLOCK_ENTITY_TYPE_FLUID_TANK.get(), RendererFluidTank::new);
		event.registerBlockEntityRenderer(BLOCK_ENTITY_TYPE_FLUID_TANK_SURGE.get(), RendererFluidTank::new);
		event.registerBlockEntityRenderer(BLOCK_ENTITY_TYPE_FLUID_TANK_CREATIVE.get(), RendererFluidTank::new);
		

		/** -- Transport -- */
		event.registerBlockEntityRenderer(BLOCK_ENTITY_TYPE_CHANNEL_ENERGY.get(), RendererEnergyChannel::new);
		event.registerBlockEntityRenderer(BLOCK_ENTITY_TYPE_CHANNEL_ENERGY_SURGE.get(), RendererEnergyChannel::new);
		event.registerBlockEntityRenderer(BLOCK_ENTITY_TYPE_CHANNEL_ENERGY_CREATIVE.get(), RendererEnergyChannel::new);

		event.registerBlockEntityRenderer(BLOCK_ENTITY_TYPE_CHANNEL_FLUID.get(), RendererFluidChannel::new);
		event.registerBlockEntityRenderer(BLOCK_ENTITY_TYPE_CHANNEL_FLUID_SURGE.get(), RendererFluidChannel::new);
		event.registerBlockEntityRenderer(BLOCK_ENTITY_TYPE_CHANNEL_FLUID_CREATIVE.get(), RendererFluidChannel::new);

		CosmosIndustry.CONSOLE.startup("BlockEntityRenderer Registration complete.");
	}

	@SubscribeEvent
	public static void registerMenuScreensEvent(RegisterMenuScreensEvent event) {
		/** -- Processing -- */
		event.register(CONTAINER_TYPE_KILN.get(), ScreenKiln::new);
		event.register(CONTAINER_TYPE_GRINDER.get(), ScreenGrinder::new);
		event.register(CONTAINER_TYPE_COMPACTOR.get(), ScreenCompactor::new);
		event.register(CONTAINER_TYPE_SEPARATOR.get(), ScreenSeparator::new);
		event.register(CONTAINER_TYPE_LASER_CUTTER.get(), ScreenLaserCutter::new);
		event.register(CONTAINER_TYPE_ORE_PLANT.get(), ScreenOrePlant::new);
		event.register(CONTAINER_TYPE_FLUID_CRAFTER.get(), ScreenFluidCrafter::new);
		
		event.register(CONTAINER_TYPE_CHARGER.get(), ScreenCharger::new);
		event.register(CONTAINER_TYPE_SYNTHESISER.get(), ScreenSynthesiser::new);
		

		/** -- Production -- */
		event.register(CONTAINER_TYPE_SOLAR_PANEL.get(), ScreenSolarPanel::new);
		event.register(CONTAINER_TYPE_SOLID_FUEL.get(), ScreenSolidFuel::new);
		event.register(CONTAINER_TYPE_LIQUID_FUEL.get(), ScreenLiquidFuel::new);
		

		/** -- Storage -- */
		event.register(CONTAINER_TYPE_CAPACITOR.get(), ScreenCapacitor::new);
		event.register(CONTAINER_TYPE_CAPACITOR_SURGE.get(), ScreenCapacitorSurge::new);
		event.register(CONTAINER_TYPE_CAPACITOR_CREATIVE.get(), ScreenCapacitorCreative::new);

		event.register(CONTAINER_TYPE_FLUID_TANK.get(), ScreenFluidTank::new);
		event.register(CONTAINER_TYPE_FLUID_TANK_SURGE.get(), ScreenFluidTankSurge::new);
		event.register(CONTAINER_TYPE_FLUID_TANK_CREATIVE.get(), ScreenFluidTankCreative::new);
	}
	
	@SubscribeEvent
	private static void registerCapabilities(RegisterCapabilitiesEvent event) {
		/** - Energy - */
		/** -- Processing -- */
		event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, BLOCK_ENTITY_TYPE_KILN.get(), (myBlockEntity, side) -> myBlockEntity.createEnergyProxy(side));
		event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, BLOCK_ENTITY_TYPE_GRINDER.get(), (myBlockEntity, side) -> myBlockEntity.createEnergyProxy(side));
		event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, BLOCK_ENTITY_TYPE_COMPACTOR.get(), (myBlockEntity, side) -> myBlockEntity.createEnergyProxy(side));
		event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, BLOCK_ENTITY_TYPE_SEPARATOR.get(), (myBlockEntity, side) -> myBlockEntity.createEnergyProxy(side));
		event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, BLOCK_ENTITY_TYPE_LASER_CUTTER.get(), (myBlockEntity, side) -> myBlockEntity.createEnergyProxy(side));
		event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, BLOCK_ENTITY_TYPE_ORE_PLANT.get(), (myBlockEntity, side) -> myBlockEntity.createEnergyProxy(side));
		event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, BLOCK_ENTITY_TYPE_FLUID_CRAFTER.get(), (myBlockEntity, side) -> myBlockEntity.createEnergyProxy(side));
		
		event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, BLOCK_ENTITY_TYPE_CHARGER.get(), (myBlockEntity, side) -> myBlockEntity.createEnergyProxy(side));
		event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, BLOCK_ENTITY_TYPE_SYNTHESISER.get(), (myBlockEntity, side) -> myBlockEntity.createEnergyProxy(side));


		/** -- Production -- */
		event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, BLOCK_ENTITY_TYPE_SOLAR_PANEL.get(), (myBlockEntity, side) -> myBlockEntity.createEnergyProxy(side));
		event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, BLOCK_ENTITY_TYPE_SOLID_FUEL.get(), (myBlockEntity, side) -> myBlockEntity.createEnergyProxy(side));
		event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, BLOCK_ENTITY_TYPE_LIQUID_FUEL.get(), (myBlockEntity, side) -> myBlockEntity.createEnergyProxy(side));
		

		/** -- Storage -- */
		event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, BLOCK_ENTITY_TYPE_CAPACITOR.get(), (myBlockEntity, side) -> myBlockEntity.createEnergyProxy(side));
		event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, BLOCK_ENTITY_TYPE_CAPACITOR_SURGE.get(), (myBlockEntity, side) -> myBlockEntity.createEnergyProxy(side));
		event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, BLOCK_ENTITY_TYPE_CAPACITOR_CREATIVE.get(), (myBlockEntity, side) -> myBlockEntity.createEnergyProxy(side));

		
		/** -- Transport -- */
		event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, BLOCK_ENTITY_TYPE_CHANNEL_ENERGY.get(), (myBlockEntity, side) -> myBlockEntity.createEnergyProxy(side));
		event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, BLOCK_ENTITY_TYPE_CHANNEL_ENERGY_SURGE.get(), (myBlockEntity, side) -> myBlockEntity.createEnergyProxy(side));
		event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, BLOCK_ENTITY_TYPE_CHANNEL_ENERGY_CREATIVE.get(), (myBlockEntity, side) -> myBlockEntity.createEnergyProxy(side));
		

		/** -- Item -- */
		event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, voidA) -> ENERGY_CELL.get().getEnergyCapability(stack), ENERGY_CELL.get());
		
		event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, voidA) -> ITEMBLOCK_CAPACITOR.get().getEnergyCapability(stack), ITEMBLOCK_CAPACITOR.get());
		event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, voidA) -> ITEMBLOCK_CAPACITOR_SURGE.get().getEnergyCapability(stack), ITEMBLOCK_CAPACITOR_SURGE.get());
		event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, voidA) -> ITEMBLOCK_CAPACITOR_CREATIVE.get().getEnergyCapability(stack), ITEMBLOCK_CAPACITOR_CREATIVE.get());
		
		
		
		/** - Fluid - */
		/** -- Processing -- */
		event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BLOCK_ENTITY_TYPE_ORE_PLANT.get(), (myBlockEntity, side) -> myBlockEntity.createFluidProxy(side));
		event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BLOCK_ENTITY_TYPE_FLUID_CRAFTER.get(), (myBlockEntity, side) -> myBlockEntity.createFluidProxy(side));

		
		/** -- Production -- */
		event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BLOCK_ENTITY_TYPE_LIQUID_FUEL.get(), (myBlockEntity, side) -> myBlockEntity.createFluidProxy(side));
		

		/** -- Storage - */
		event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BLOCK_ENTITY_TYPE_FLUID_TANK.get(), (myBlockEntity, side) -> myBlockEntity.createFluidProxy(side));
		event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BLOCK_ENTITY_TYPE_FLUID_TANK_SURGE.get(), (myBlockEntity, side) -> myBlockEntity.createFluidProxy(side));
		event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BLOCK_ENTITY_TYPE_FLUID_TANK_CREATIVE.get(), (myBlockEntity, side) -> myBlockEntity.createFluidProxy(side));

		
		/** -- Transport -- */
		event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BLOCK_ENTITY_TYPE_CHANNEL_FLUID.get(), (myBlockEntity, side) -> myBlockEntity.createFluidProxy(side));
		event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BLOCK_ENTITY_TYPE_CHANNEL_FLUID_SURGE.get(), (myBlockEntity, side) -> myBlockEntity.createFluidProxy(side));
		event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BLOCK_ENTITY_TYPE_CHANNEL_FLUID_CREATIVE.get(), (myBlockEntity, side) -> myBlockEntity.createFluidProxy(side));

		
		/** -- Item -- */
		event.registerItem(Capabilities.FluidHandler.ITEM, (stack, voidA) -> new FluidBucketWrapper(stack), BUCKET_ENERGIZED_REDSTONE.get());
		event.registerItem(Capabilities.FluidHandler.ITEM, (stack, voidA) -> new FluidBucketWrapper(stack), BUCKET_COOLANT.get());
		
		event.registerItem(Capabilities.FluidHandler.ITEM, (stack, voidA) -> ITEMBLOCK_FLUID_TANK.get().getFluidCapability(stack), ITEMBLOCK_FLUID_TANK.get());
		event.registerItem(Capabilities.FluidHandler.ITEM, (stack, voidA) -> ITEMBLOCK_FLUID_TANK_SURGE.get().getFluidCapability(stack), ITEMBLOCK_FLUID_TANK_SURGE.get());
		event.registerItem(Capabilities.FluidHandler.ITEM, (stack, voidA) -> ITEMBLOCK_FLUID_TANK_CREATIVE.get().getFluidCapability(stack), ITEMBLOCK_FLUID_TANK_CREATIVE.get());
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void onModelRegistryEvent(ModelEvent.RegisterAdditional event) {
		CosmosRuntimeHelper.registerSpecialModels(event, CosmosIndustry.MOD_ID, 
			"item/block_fluid_tank_item",
			"item/block_fluid_tank_surge_item",
			"item/block_fluid_tank_creative_item"
		);
		
		CosmosIndustry.CONSOLE.startup("Model Registration complete..");
	}
	
	@OnlyIn(Dist.CLIENT)
	public static void onFMLClientSetup(FMLClientSetupEvent event) {
		CosmosRuntimeHelper.setRenderLayers(RenderType.cutoutMipped(),
			BLOCK_STRUCTURE.get(), BLOCK_KILN.get(), BLOCK_GRINDER.get(), BLOCK_COMPACTOR.get(), BLOCK_CHARGER.get(), BLOCK_SEPARATOR.get(), BLOCK_LASER_CUTTER.get(), BLOCK_ORE_PLANT.get(), BLOCK_FLUID_CRAFTER.get(),
			BLOCK_SYNTHESISER.get(), BLOCK_SYNTHESISER_STAND.get(),
			
			BLOCK_CAPACITOR.get(), BLOCK_CAPACITOR_SURGE.get(), BLOCK_CAPACITOR_CREATIVE.get(), BLOCK_FLUID_TANK.get(), BLOCK_FLUID_TANK_SURGE.get(), BLOCK_FLUID_TANK_CREATIVE.get(),
			
			BLOCK_ENERGY_CHANNEL.get(), BLOCK_ENERGY_CHANNEL_SURGE.get(), BLOCK_ENERGY_CHANNEL_CREATIVE.get(), 
			BLOCK_FLUID_CHANNEL.get(), BLOCK_FLUID_CHANNEL_SURGE.get(), BLOCK_FLUID_CHANNEL_CREATIVE.get()
		);
		
		CosmosRuntimeHelper.setRenderLayers(RenderType.cutout(),
			BLOCK_LIQUID_FUEL.get()
		);
		
		ItemBlockRenderTypes.setRenderLayer(FLOWING_FLUID_ENERGIZED_REDSTONE.get(), RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(FLUID_ENERGIZED_REDSTONE.get(), RenderType.translucent());

		ItemBlockRenderTypes.setRenderLayer(FLOWING_FLUID_COOLANT.get(), RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(FLUID_COOLANT.get(), RenderType.translucent());
				
		ItemProperties.register(ENERGY_CELL.get(), ResourceLocation.parse("energy"), (stack, level, entity, seed) -> { return stack.getItem() instanceof CosmosEnergyStorageItem item ? (float) item.getScaledEnergy(stack, 8) : 0.0F; });
	}
	
	protected static <T extends Item> DeferredItem<T> addToBlockTab(DeferredItem<T> itemLike) {
		return addToTab(TAB_BLOCKS, itemLike);
    }

    protected static <T extends Item> DeferredItem<T> addToDevicesTab(DeferredItem<T> itemLike) {
        return addToTab(TAB_DEVICES, itemLike);
    }

    protected static <T extends Item> DeferredItem<T> addToItemTab(DeferredItem<T> itemLike) {
    	return addToTab(TAB_ITEMS, itemLike);
    }

    protected static <T extends Item> DeferredItem<T> addToToolsTab(DeferredItem<T> itemLike) {
    	return addToTab(TAB_TOOLS, itemLike);
    }

    private static <T extends Item> DeferredItem<T> addToTab(ArrayList<Supplier<? extends ItemLike>> tab, DeferredItem<T> itemLike) {
    	tab.add(itemLike);
    	return itemLike;
    }
}