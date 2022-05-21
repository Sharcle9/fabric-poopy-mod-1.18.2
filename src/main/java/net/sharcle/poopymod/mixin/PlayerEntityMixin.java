package net.sharcle.poopymod.mixin;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.mixin.client.particle.ParticleManagerAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ComposterBlock;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.state.property.Properties.LEVEL_8;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    public boolean isFirstTickSneaking;
    public boolean wasSneaky = false;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at = @At("Head"), method = "tick")
    private void tick(CallbackInfo info) {

        BlockState state;
        World world = this.getWorld();

        if (isFirstTickSneaking) {
            isFirstTickSneaking = false;
        } else {
            if (this.isSneaky() && !wasSneaky) {
                isFirstTickSneaking = true;
                state = world.getBlockState(new BlockPos(this.getPos()));

                if (state.isOf(Blocks.COMPOSTER)) {

                    BlockPos pos = new BlockPos(this.getPos());
                    int level = state.get(LEVEL_8);
                    if (level < 7 && !world.isClient && world.getRandom().nextDouble() < 0.24d) {
                        world.setBlockState(pos, state.with(LEVEL_8, level + 1), Block.NOTIFY_ALL);
                        double g = random.nextGaussian() * 0.02;
                        world.playSound(null, pos, SoundEvents.BLOCK_POINTED_DRIPSTONE_DRIP_LAVA, SoundCategory.PLAYERS, 2f, 0.4f + (float) g);
                        world.playSound(null, pos, SoundEvents.BLOCK_POINTED_DRIPSTONE_DRIP_WATER, SoundCategory.PLAYERS, 1f, 0.2f + (float) g);

                        if (world.getRandom().nextDouble() < 0.3d) {
                            double h = random.nextGaussian() * 0.02;
                            boolean j = random.nextDouble() < 0.3;
                            world.playSound(null, pos, j ? SoundEvents.ENTITY_BEE_DEATH : SoundEvents.ENTITY_BEE_HURT, SoundCategory.PLAYERS, 0.25f, 0.2f + (float) h);
                            world.playSound(null, pos, SoundEvents.ENTITY_BEE_POLLINATE, SoundCategory.PLAYERS, 1f, 0.5f + (float) h);
                        }
                        spawnParticles(pos, world);
                    } else if (world.isClient && world.getRandom().nextDouble() < 0.1d) {
                        spawnParticles(pos, world);
                    }
                }
            }
        }

        wasSneaky = this.isSneaky();
    }

    private void spawnParticles(BlockPos pos, World world) {

        float h = MathHelper.cos(this.getPitch() * ((float) Math.PI / 180));
        float i = MathHelper.sin(this.getYaw() * ((float) Math.PI / 180));
        float j = MathHelper.cos(this.getYaw() * ((float) Math.PI / 180));
        float k = this.random.nextFloat() * ((float) Math.PI * 2);
        float l = 0.003f * this.random.nextFloat();
        world.addParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, pos.getX() + 0.5d, pos.getY() + 1, pos.getZ() + 0.5d, -((double) (-i * h * 0.3f) + Math.cos(k) * (double) l), -0.02f, -((double) (j * h * 0.3f) + Math.sin(k) * (double) l));


    }
}
