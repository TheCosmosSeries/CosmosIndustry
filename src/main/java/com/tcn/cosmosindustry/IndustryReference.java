package com.tcn.cosmosindustry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;

public class IndustryReference {
	public static class RESOURCE {
		
		/**
		 * Prefix for all ResourceLocations.
		 */
		public static final String PRE = CosmosIndustry.MOD_ID + ":";
		public static final String RESOURCE = PRE + "textures/";
		
		/**
		 * ResourceLocations for Base Objects.
		 */
		public static class BASE {
			public static final String BLOCKS = RESOURCE + "block/base/";
			public static final String FLUID = BLOCKS + "fluid/";
			public static final String ITEMS = RESOURCE + "item/base/";
			public static final String GUI = RESOURCE + "gui/base/";
			
			public static final ResourceLocation FLUID_COOLANT_STILL = ResourceLocation.parse(FLUID + "coolant/coolant_still");
			public static final ResourceLocation FLUID_COOLANT_FLOWING = ResourceLocation.parse(FLUID + "coolant/coolant_flow");
			
			public static final ResourceLocation FLUID_ENERGIZED_REDSTONE_STILL= ResourceLocation.parse(FLUID + "energized_redstone/energized_redstone_still");
			public static final ResourceLocation FLUID_ENERGIZED_REDSTONE_FLOWING = ResourceLocation.parse(FLUID + "energized_redstone/energized_redstone_flow");
			
			public static final ResourceLocation FLUID_GLOWSTONE_INFUSED_MAGMA_STILL = ResourceLocation.parse(FLUID + "glowstone_infused_magma/glowstone_infused_magma_still");
			public static final ResourceLocation FLUID_GLOWSTONE_INFUSED_MAGMA_FLOWING = ResourceLocation.parse(FLUID + "glowstone_infused_magma/glowstone_infused_magma_flow");
		}
		
		/**
		 * ResourceLocations for Processing Objects.
		 */
		public static class PROCESSING {
			public static final String BLOCKS = RESOURCE + "block/processing/";
			public static final String ITEMS = RESOURCE + "item/processing/";
			public static final String GUI = RESOURCE + "gui/processing/";
			
			public static final String BER = RESOURCE + "model/processing/";
			
			public static final String PREFIX = "gui.processing.";
			public static final String SUFFIX = ".name";
			
			public static final String BLOCK_PREFIX = "block_";
			
			public static final ResourceLocation KILN_LOC_GUI = ResourceLocation.parse(GUI + "kiln/gui.png");
			public static final ResourceLocation KILN_LOC_GUI_JEI = ResourceLocation.parse(GUI + "kiln/jei.png");
			public static final String KILN_NAME = PREFIX + "kiln" + SUFFIX;
			public static final int KILN_INDEX = 1;
			
			public static final ResourceLocation GRINDER_LOC_GUI = ResourceLocation.parse(GUI + "grinder/gui.png");
			public static final ResourceLocation GRINDER_LOC_GUI_JEI = ResourceLocation.parse(GUI + "grinder/jei.png");
			public static final ResourceLocation GRINDER_LOC_BER = ResourceLocation.parse(BER + "grinder.png");
			public static final String GRINDER_NAME = PREFIX + "grinder" + SUFFIX;
			public static final int GRINDER_INDEX = 2;
			
			public static final ResourceLocation COMPACTOR_LOC_GUI = ResourceLocation.parse(GUI + "compactor/gui.png");
			public static final ResourceLocation COMPACTOR_LOC_GUI_JEI = ResourceLocation.parse(GUI + "compactor/jei.png");
			public static final ResourceLocation COMPACTOR_LOC_BER = ResourceLocation.parse(BER + "compactor.png");
			public static final String COMPACTOR_NAME = PREFIX + "compactor" + SUFFIX;
			public static final int COMPACTOR_INDEX = 3;
			
			public static final ResourceLocation SEPARATOR_LOC_GUI = ResourceLocation.parse(GUI + "separator/gui.png");
			public static final ResourceLocation SEPARATOR_LOC_GUI_JEI = ResourceLocation.parse(GUI + "separator/jei.png");
			public static final ResourceLocation SEPARATOR_LOC_BER = ResourceLocation.parse(BER + "separator.png");
			public static final String SEPARATOR_NAME = PREFIX + "seperator" + SUFFIX;
			public static final int SEPARATOR_INDEX = 4;

			public static final ResourceLocation LASER_CUTTER_LOC_GUI = ResourceLocation.parse(GUI + "laser_cutter/gui.png");
			public static final ResourceLocation LASER_CUTTER_LOC_GUI_JEI = ResourceLocation.parse(GUI + "laser_cutter/jei.png");
			public static final ResourceLocation LASER_CUTTER_LOC_BER = ResourceLocation.parse(BER + "laser_cutter.png");
			public static final String LASER_CUTTER_NAME = PREFIX + "laser_cutter" + SUFFIX;
			public static final int LASER_CUTTER_INDEX = 1;
			
			public static final ResourceLocation CHARGER_LOC_GUI = ResourceLocation.parse(GUI + "charger/gui.png");
			public static final ResourceLocation CHARGER_LOC_BER = ResourceLocation.parse(BER + "charger.png");
			public static final String CHARGER_NAME = PREFIX + "charger" + SUFFIX;
			public static final int CHARGER_INDEX = 5;
			
			public static final ResourceLocation ORE_PLANT_LOC_GUI = ResourceLocation.parse(GUI + "ore_plant/gui.png");
			public static final ResourceLocation ORE_PLANT_LOC_BER = ResourceLocation.parse(BER + "ore_plant.png");
			public static final String ORE_PLANT_NAME = PREFIX + "ore_plant" + SUFFIX;
			public static final int ORE_PLANT_INDEX = 6;
			
			public static final ResourceLocation FLUID_CRAFTER_LOC_GUI = ResourceLocation.parse(GUI + "fluid_crafter/gui.png");
			public static final ResourceLocation FLUID_CRAFTER_LOC_BER = ResourceLocation.parse(BER + "fluid_crafter.png");
			public static final String FLUID_CRAFTER_NAME = PREFIX + "fluid_crafter" + SUFFIX;
			public static final int FLUID_CRAFTER_INDEX = 7;
			
			public static final ResourceLocation SYNTHESISER_LOC_GUI = ResourceLocation.parse(GUI + "synthesiser/gui.png");
			public static final ResourceLocation SYNTHESISER_LOC_GUI_JEI = ResourceLocation.parse(GUI + "synthesiser/jei.png");
			public static final ResourceLocation SYNTHESISER_LOC_GUI_JEI_LASER = ResourceLocation.parse(GUI + "synthesiser/laser.png");
			public static final ResourceLocation SYNTHESISER_CONNECTION = ResourceLocation.parse(BLOCKS + "synthesiser/connection.png");
			public static final String SYNTHESISER_NAME = PREFIX + "synthesiser" + SUFFIX;
			public static final int SYNTHESISER_INDEX = 8;
			
			public static final String SYNTHESISER_STAND = PREFIX + "synthesiser_stand" + SUFFIX;
			public static final int SYNTHESISER_STAND_INDEX = 9;

			public static final int[] LASER_JEI_ARRAY_X = new int[] { 0, 0, 0, 0, 60, 60, 60, 60, 120, 120, 120, 120,180, 180, 180, 180 };
			public static final int[] LASER_JEI_ARRAY_Y = new int[] { 0, 60, 120, 180, 0, 60, 120, 180, 0, 60, 120, 180, 0, 60, 120, 180 };
			
			/** Values */
			public static final int[] CAPACITY = new int[] { 200000, 400000, 600000, 800000, 1000000 };
			public static final int[] MAX_INPUT = new int [] { 2000, 4000, 6000, 8000, 10000 };
			
			public static final int[] RF_TICK_RATE = new int[] { 120, 160, 200, 240, 280 };
			public static final int[] RF_EFF_RATE = new int[] { 0, 10, 15, 20, 30, 50 };
			public static final int[] SPEED_RATE = new int[] { 80, 70, 60, 50, 40 };
			
			public static final int[] CAPACITY_U = new int[] { 500000, 700000, 900000, 1100000, 1300000 };
			public static final int[] MAX_INPUT_U = new int[] { 5000, 7000, 9000, 11000, 13000 };
			
			public static final int[] RF_TICK_RATE_U = new int[] { 240, 320, 400, 480, 560 };
			public static final int[] SPEED_RATE_U = new int[] { 35, 30, 25, 20, 15 };
		}
		
		/**
		 * ResourceLocations for Production Objects.
		 */
		public static class PRODUCTION {
			public static final String BLOCKS = RESOURCE + "block/production/";
			public static final String ITEMS = RESOURCE + "item/production/";
			public static final String GUI = RESOURCE + "gui/production/";
			public static final String BER = RESOURCE + "model/production/";
			
			/** Gui */
			public static final ResourceLocation GUI_SCALED_ELEMENTS = ResourceLocation.parse(GUI + "scaled_elements.png");
			
			public static final ResourceLocation GUI_SOLIDFUEL = ResourceLocation.parse(GUI + "solid_fuel/gui.png");
			public static final ResourceLocation GUI_HEATEDFLUID = ResourceLocation.parse(GUI + "heated_fluid/gui.png");
			public static final ResourceLocation GUI_PELTIER = ResourceLocation.parse(GUI + "peltier/gui.png");
			public static final ResourceLocation GUI_SOLARPANEL = ResourceLocation.parse(GUI + "solar/gui.png");
			
			/** Localised Names */
			public static final String SOLIDFUEL = "gui.production.solid_fuel.name";
			public static final String SOLARPANEL = "gui.production.solar.name";
			public static final String HEATEDFLUID = "gui.production.heated_fluid.name";
			public static final String PELTIER = "gui.production.peltier.name";
			
			/** Values */
			public static final int CAPACITY = 60000;
			public static final int MAX_OUTPUT = 500;
			
			public static final int[] RF_TICK_RATE = new int[] { 500, 1000, 1500, 2000 };
			public static final int[] SPEED_RATE = new int[] { 100, 150, 200, 250 };
			
			public static final int CAPACITY_U = 120000;
			public static final int MAX_OUTPUT_U = 1000;
			
			public static final int[] RF_TICK_RATE_U = new int[] { 750, 1250, 1750, 2250 };
			public static final int[] SPEED_RATE_U = new int[] { 100, 150, 200, 250 };
		}
		
		/**
		 * ResourceLocations for Storage Objects.
		 */
		public static class STORAGE {
			public static final String BLOCKS = RESOURCE + "block/storage/";
			public static final String ITEMS = RESOURCE + "item/storage/";
			public static final String GUI = RESOURCE + "gui/storage/";

			/** Gui */
			public static final ResourceLocation GUI_CAPACITOR = ResourceLocation.parse(GUI + "capacitor/gui.png");
			public static final ResourceLocation CAPACITOR_CONNECTION = ResourceLocation.parse(BLOCKS + "capacitor/connection.png");
			
			/** Localised Names */
			public static final String P_CAPACITOR = "gui.storage.powered.capacitor.name";
			public static final String E_CAPACITOR = "gui.storage.energized.capacitor.name";
			
			public static final String MECHANISEDSTORAGESMALL = "gui.storage.mechanisedstoragesmall.name";
			public static final String MECHANISEDSTORAGELARGE = "gui.storage.mechanisedstoragelarge.name";
			
			public static final int[] ENERGY = new int[] { 20000000, 100000 };
			public static final int[] ENERGY_SURGE = new int[] { 50000000, 500000 };
			public static final int[] ENERGY_CREATIVE = new int[] { 200000000, 1000000 };
		}
		
		/**
		 * ResourceLocation for Transport Objects.
		 */
		public static class TRANSPORT {
			
			/** 
			 * Bounding Boxes for "standard" pipes.
			 * 
			 * Order is: [Base - D-U-N-S-W-E]
			 */
			public static VoxelShape[] BOUNDING_BOXES_STANDARD = new VoxelShape[] {
				Block.box(5.00D, 5.00D, 5.00D, 11.0D, 11.0D, 11.0D), // BASE
				Block.box(5.00D, 0.00D, 5.00D, 11.0D, 5.00D, 11.0D), // DOWN
				Block.box(5.00D, 11.0D, 5.00D, 11.0D, 16.0D, 11.0D), // UP
				Block.box(5.00D, 5.00D, 0.00D, 11.0D, 11.0D, 5.00D), // NORTH
				Block.box(5.00D, 5.00D, 11.0D, 11.0D, 11.0D, 16.0D), // SOUTH
				Block.box(0.00D, 5.00D, 5.00D, 5.00D, 11.0D, 11.0D), // WEST
				Block.box(11.0D, 5.00D, 5.00D, 16.0D, 11.0D, 11.0D), // EAST
			};

			/** 
			 * Bounding Boxes for "interface" elements of pipes.
			 * 
			 * Order is: [Base - D-U-N-S-W-E]
			 */
			public static VoxelShape[] BOUNDING_BOXES_INTERFACE = new VoxelShape[] {
				Block.box(4.00F, 0.00F, 4.00F, 12.0F, 3.00F, 12.0F), // DOWN
				Block.box(4.00F, 13.0F, 4.00F, 12.0F, 16.0F, 12.0F), // UP
				
				Block.box(4.00F, 4.00F, 0.00F, 12.0F, 12.0F, 3.00F), // NORTH
				Block.box(4.00F, 4.00F, 13.0F, 12.0F, 12.0F, 16.0F), // SOUTH
				
				Block.box(0.00F, 4.00F, 4.00F, 3.00F, 12.0F, 12.0F), // WEST
				Block.box(13.0F, 4.00F, 4.00F, 16.0F, 12.0F, 12.0F)  // EAST
			};
			
			/** 
			 * Bounding Boxes for "surge" pipes.
			 * 
			 * Order is: [Base - D-U-N-S-W-E]
			 */
			public static VoxelShape[] BOUNDING_BOXES_STANDARD_SURGE = new VoxelShape[] {
				Block.box(4.50D, 4.50D, 4.50D, 11.5D, 11.5D, 11.5D), //BASE
				Block.box(4.50D, 0.00D, 4.50D, 11.5D, 4.50D, 11.5D), // DOWN
				Block.box(4.50D, 11.5D, 4.50D, 11.5D, 16.0D, 11.5D), // UP
				Block.box(4.50D, 4.50D, 0.00D, 11.5D, 11.5D, 4.50D), // NORTH
				Block.box(4.50D, 4.50D, 11.5D, 11.5D, 11.5D, 16.0D), //SOUTH
				Block.box(0.00D, 4.50D, 4.50D, 4.50D, 11.5D, 11.5D), // WEST
				Block.box(11.5D, 4.50D, 4.50D, 16.0D, 11.5D, 11.5D)  // EAST
			};
				
			//[WIP] Bounding Boxes for "thin" pipes.
			public VoxelShape[] BOUNDING_BOXES_THIN = new VoxelShape[] {};
			
			public static final String BLOCKS = RESOURCE + "block/transport/";
			public static final String ITEMS = RESOURCE + "item/transport/";
			public static final String GUI = RESOURCE + "gui/transport/";
			public static final String BER = RESOURCE + "model/transport/";
			
			/** Values */
			public static final int[] ENERGY = new int[] { 100000, 50000 };
			public static final int[] ENERGY_SURGE = new int[] { 200000, 100000 };
			public static final int[] ENERGY_CREATIVE = new int[] { 1000000, 500000 };
			
			public static final ResourceLocation[] ENERGY_TEXTURES = new ResourceLocation[] { 
				ResourceLocation.parse(BLOCKS + "channel/energy/opaque.png"), 
				ResourceLocation.parse(BLOCKS + "channel/energy/transparent.png"), 
				ResourceLocation.parse(BLOCKS + "channel/surge.png"), 
				ResourceLocation.parse(BLOCKS + "channel/creative.png"), 
				ResourceLocation.parse(BLOCKS + "channel/energy/interface.png")
			};

			public static final int[] FLUID = new int[] { 10000, 1000 };
			public static final int[] FLUID_SURGE = new int[] { 20000, 10000 };
			public static final int[] FLUID_CREATIVE = new int[] { 200000, 100000 };
			
			public static final ResourceLocation[] FLUID_TEXTURES = new ResourceLocation[] { 
				ResourceLocation.parse(BLOCKS + "channel/fluid/opaque.png"), 
				ResourceLocation.parse(BLOCKS + "channel/fluid/transparent.png"), 
				ResourceLocation.parse(BLOCKS + "channel/surge.png"), 
				ResourceLocation.parse(BLOCKS + "channel/creative.png"), 
				ResourceLocation.parse(BLOCKS + "channel/fluid/interface.png")
			};
			

			public static final ResourceLocation[] ITEM_TEXTURES = new ResourceLocation[] { 
				ResourceLocation.parse(BLOCKS + "channel/item/opaque.png"), 
				ResourceLocation.parse(BLOCKS + "channel/item/transparent.png"), 
				ResourceLocation.parse(BLOCKS + "channel/surge.png"), 
				ResourceLocation.parse(BLOCKS + "channel/creative.png"), 
				ResourceLocation.parse(BLOCKS + "channel/item/interface.png")
			};
		}
	}
	
	/**
	 * JEI Integration.
	 */
	public static class JEI {
		public static final String KILN_UNLOC = "jei.recipe.kiln";
		public static final String GRINDER_UNLOC = "jei.recipe.grinder";
		public static final String COMPACTOR_UNLOC = "jei.recipe.compactor";
		public static final String SEPARATOR_UNLOC = "jei.recipe.separator";
		public static final String SYNTHESISER_UNLOC = "jei.recipe.synthesiser";
		
		public static final ResourceLocation GRINDER_UID = ResourceLocation.fromNamespaceAndPath(CosmosIndustry.MOD_ID, "grinder_category");
		public static final ResourceLocation SEPARATOR_UID = ResourceLocation.fromNamespaceAndPath(CosmosIndustry.MOD_ID, "separator_category");
		public static final ResourceLocation COMPACTOR_UID = ResourceLocation.fromNamespaceAndPath(CosmosIndustry.MOD_ID, "compactor_category");
		public static final ResourceLocation SYNTHESISER_UID = ResourceLocation.fromNamespaceAndPath(CosmosIndustry.MOD_ID, "synthesiser_category");
	}
}