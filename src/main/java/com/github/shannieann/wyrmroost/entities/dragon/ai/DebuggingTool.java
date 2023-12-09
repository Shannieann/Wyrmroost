package com.github.shannieann.wyrmroost.entities.dragon.ai;

import com.github.shannieann.wyrmroost.entities.dragon.EntityRoyalRed;
import com.github.shannieann.wyrmroost.entities.effect.EffectLightningSphere;
import com.github.shannieann.wyrmroost.registry.WREntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class DebuggingTool {
    int numberOfPoints = 40;
    public DebuggingTool(Player player){

        //ArrayList<Vec3> listOfVectors=listOfVectors();
        //summonEntities(listOfVectors,player);
        summonLightningSphere(player);
        //forceRoyalRedTarget(player);
  }

  public ArrayList<Vec3> listOfVectors() {
      ArrayList<Vec3> list=new ArrayList<Vec3>();
      float goldenRatio = (float) (3-Math.sqrt(5));
      float lambda = (float) Math.PI * goldenRatio;

      for(int i=0; i<numberOfPoints; i++){
          double t = (float)i/numberOfPoints;
          double a1 = Math.acos((double) 1-2*t);
          double a2 = lambda * i;
          double x = (float) (sin(a1) * cos(a2));
          double y = sin(a1) * sin(a2);
          double z = cos(a1);
          Vec3 vec3 = new Vec3(x,y,z).scale(10);
          list.add(vec3);
      }
      return list;
  }

  public void summonEntities(ArrayList<Vec3> listOfVectors, Player player){
        for (int i = 0; i< listOfVectors.size();i++) {
            Vec3 targetVector = listOfVectors.get(i);
            BlockPos targetPosition = new BlockPos(targetVector);
            Chicken chicken = new Chicken(EntityType.CHICKEN,player.level);
            chicken.setNoAi(true);
            chicken.setPos(player.position().add(targetVector));
            player.level.addFreshEntity(chicken);
        }
  }

  public void summonLightningSphere(Player player) {
      EffectLightningSphere lightningSphere = new EffectLightningSphere(WREntityTypes.LIGHTNING_SPHERE.get(),player.level,20);
      lightningSphere.setPos(player.position().add(10,0,0));
      player.level.addFreshEntity(lightningSphere);
  }

  public void forceRoyalRedTarget(Player player) {
        AABB aabb = player.getBoundingBox().inflate(40);
        List<EntityRoyalRed> royalReds = player.level.getEntitiesOfClass(EntityRoyalRed.class,aabb);
        if (!royalReds.isEmpty()) {
            for (int i = 0; i < royalReds.size(); i++) {
                EntityRoyalRed test = royalReds.get(i);
                test.debugTarget = player.position();
                test.setBreathingFire(true);
            }
        }
    }
}
