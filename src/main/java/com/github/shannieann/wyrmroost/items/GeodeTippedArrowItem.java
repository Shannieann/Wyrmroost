package com.github.shannieann.wyrmroost.items;

import com.github.shannieann.wyrmroost.entity.projectile.GeodeTippedArrowEntity;
import com.github.shannieann.wyrmroost.registry.WRItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class GeodeTippedArrowItem extends ArrowItem
{
    private final double damage;

    public GeodeTippedArrowItem(double damage)
    {
        super(WRItems.builder());
        this.damage = damage;
    }
    @Override
    public AbstractArrow createArrow(Level level, ItemStack stack, LivingEntity shooter)
    {
        GeodeTippedArrowEntity arrow = new GeodeTippedArrowEntity(level, this);
        arrow.absMoveTo(shooter.getX(), shooter.getEyeY() - 0.1d, shooter.getZ());
        arrow.setBaseDamage(damage);
        return arrow;
    }
}
