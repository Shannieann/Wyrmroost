package com.github.shannieann.wyrmroost.entities.dragon.ai.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class TestClass {
    int numberOfPoints = 40;
    public TestClass(Player player){
        ArrayList<Vec3> listOfVectors=listOfVectors();
        summonEntities(listOfVectors,player);
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



}
