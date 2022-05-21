package net.sharcle.poopymod.mixin;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ComposterBlock;
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

        if(isFirstTickSneaking) {
            isFirstTickSneaking = false;
        } else {
            if (this.isSneaky() && !wasSneaky) {
                isFirstTickSneaking = true;
//                System.out.println("YES");
//                System.out.println(this.getPos().toString());
                state = world.getBlockState(new BlockPos(this.getPos()));

                if (state.isOf(Blocks.COMPOSTER)) {
//                    System.out.println("COMPOSTER");

                    BlockPos pos = new BlockPos(this.getPos());
                    int level = state.get(LEVEL_8);
                    if(level < 7 && !world.isClient && world.getRandom().nextDouble() < 0.3d) {
                        world.setBlockState(pos, state.with(LEVEL_8, level + 1), Block.NOTIFY_ALL);
                        spawnParticles(pos);
                    }
                }
            }
        }

        wasSneaky = this.isSneaky();
    }

    private void spawnParticles(BlockPos pos) {
        for(int i = 0; i < 50; i ++) {
            double g = random.nextGaussian() * 0.02;
            double h = random.nextGaussian() * 0.02;
            double j = random.nextGaussian() * 0.02;
            getWorld().addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, pos.getX() + 0.5d, pos.getY() + 1, pos.getZ() + 0.5d, g, h, j);
        }
    }
}
