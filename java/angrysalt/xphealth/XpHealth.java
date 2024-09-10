
package angrysalt.xphealth;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.gamerule.v1.CustomGameRuleCategory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.gamerule.v1.rule.DoubleRule;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;

public class XpHealth implements ModInitializer {
	public static final String MOD_ID = "xphealth";

	public static final CustomGameRuleCategory CATEGORY = new CustomGameRuleCategory(Identifier.of(MOD_ID), Text.literal("Health = XP"));
	public static final GameRules.Key<GameRules.BooleanRule> LOSE_XP = GameRuleRegistry.register("loseXpOnHurt", CATEGORY, GameRuleFactory.createBooleanRule(false));
	public static final GameRules.Key<DoubleRule> XP_TO_HEALTH = GameRuleRegistry.register("levelsPerHeart", CATEGORY, GameRuleFactory.createDoubleRule(2,0.01));
	public static final GameRules.Key<GameRules.IntRule> STARTING_HEALTH = GameRuleRegistry.register("healthOnSpawn", CATEGORY, GameRuleFactory.createIntRule(0,0));
	public static final GameRules.Key<GameRules.IntRule> MAX_HEALTH = GameRuleRegistry.register("maxHealth", CATEGORY, GameRuleFactory.createIntRule(Integer.MAX_VALUE,1));

	@Override
	public void onInitialize() {
		ServerTickEvents.END_SERVER_TICK.register(server->{
			GameRules rules = server.getGameRules();
			double XPtoHealth = rules.get(XP_TO_HEALTH).get() / 2;
			int StartHealth = rules.getInt(STARTING_HEALTH);
			int MaxHealth = rules.getInt(MAX_HEALTH);
			server.getPlayerManager().getPlayerList().forEach(player-> player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue((int) Math.min(Math.floor((player.experienceLevel + player.experienceProgress) / XPtoHealth + StartHealth),MaxHealth)));
		});
	};
}