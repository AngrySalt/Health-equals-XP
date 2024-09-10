package angrysalt.xphealth.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import static angrysalt.xphealth.XpHealth.*;

@Mixin(ServerPlayerEntity.class)
public abstract class DamageMixin extends PlayerEntity {

	public DamageMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
		super(world, pos, yaw, gameProfile);
	}

	@Inject(at = @At("TAIL"), method = "damage")
	private void onDmg(DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
		ServerPlayerEntity Plr = (ServerPlayerEntity) (Object) this;
		ServerWorld server = Plr.getServerWorld();
		GameRules rules = server.getGameRules();
		if (rules.getBoolean(LOSE_XP)) {
			double newLevel = Math.max(/*Current xp*/ Plr.experienceLevel + Plr.experienceProgress - /*Damage in xp*/ amount * rules.get(XP_TO_HEALTH).get() / 2,0);
			Plr.experienceProgress = (float) newLevel%1;
			Plr.setExperienceLevel((int) newLevel);
		}
	}
}