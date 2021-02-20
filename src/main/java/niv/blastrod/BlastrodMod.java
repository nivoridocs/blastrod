package niv.blastrod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import niv.blastrod.block.BlastrodBlock;

public class BlastrodMod implements ModInitializer {

	public static final Logger log = LogManager.getLogger();

	public static final String MOD_ID = "blastrod";
	public static final String MOD_NAME = "Blast Rod";

	public static final BlastrodBlock BLASTROD =
			new BlastrodBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f));

	@Override
	public void onInitialize() {
		log.info("[{}] Initializing", MOD_NAME);

		Identifier blastrodId = new Identifier(MOD_ID, "blastrod");
		Registry.register(Registry.BLOCK, blastrodId, BLASTROD);
		Registry.register(Registry.ITEM, blastrodId,
				new BlockItem(BLASTROD, new FabricItemSettings().group(ItemGroup.REDSTONE)));
	}

}
