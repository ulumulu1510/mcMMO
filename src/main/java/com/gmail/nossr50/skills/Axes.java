/*
	This file is part of mcMMO.

    mcMMO is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    mcMMO is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with mcMMO.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.gmail.nossr50.skills;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.gmail.nossr50.Combat;
import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.party.Party;

public class Axes {
	
	public static void axeCriticalCheck(Player attacker, EntityDamageByEntityEvent event, Plugin pluginx)
	{
    	Entity x = event.getEntity();
    	
    	if(x instanceof Wolf){
    		Wolf wolf = (Wolf)x;
    		if(Taming.getOwner(wolf, pluginx) != null)
    		{
	    		if(Taming.getOwner(wolf, pluginx) == attacker)
	    			return;
	    		if(Party.getInstance().inSameParty(attacker, Taming.getOwner(wolf, pluginx)))
	    			return;
    		}
    	}
    	PlayerProfile PPa = Users.getProfile(attacker);
    	if(m.isAxes(attacker.getItemInHand()) && mcPermissions.getInstance().axes(attacker)){
    		if(PPa.getSkillLevel(SkillType.AXES) >= 750){
    			if(Math.random() * 1000 <= 750 && !x.isDead()){
    				if(x instanceof Player){
    					int damage = (event.getDamage() * 2) - (event.getDamage() / 2);
    					event.setDamage(damage);
    					Player player = (Player)x;
    					player.sendMessage(ChatColor.DARK_RED + "You were CRITICALLY hit!");
    				}
    				else {
    					int damage = event.getDamage() * 2;
        				event.setDamage(damage);
        			}
    				attacker.sendMessage(ChatColor.RED+"CRITICAL HIT!");
    			}
    		} else if(Math.random() * 1000 <= PPa.getSkillLevel(SkillType.AXES) && !x.isDead()){
    			if(x instanceof Player){
    				int damage = (event.getDamage() * 2) - (event.getDamage() / 2);
					event.setDamage(damage);
    				Player player = (Player)x;
    				player.sendMessage(ChatColor.DARK_RED + "You were CRITICALLY hit!");
    			}
    			else {
    				int damage = event.getDamage() * 2;
    				event.setDamage(damage);
    			}
				attacker.sendMessage(ChatColor.RED+"CRITICAL HIT!");
    		}
    	}
    }
	
	public static void impact(Player attacker, LivingEntity target)
	{
	    boolean didImpact = false;
	    
	    if(target instanceof Player)
	    {
	        Player targetPlayer = (Player) target;
	        int emptySlots = 0;
	        
	        for(ItemStack x : targetPlayer.getInventory().getArmorContents())
	        {
	            System.out.println("[mcMMO] DEBUG: "+x.getType().toString());
	            if(x.getType() == Material.AIR)
	            {
	                emptySlots++;
	            } else {
	                x.setDurability((short) (x.getDurability()+30)); //Damage armor piece
	            }
	        }
	        
	        if(emptySlots == 4)
	        {
	            targetPlayer.sendMessage("**HIT BY IMPACT**");
	            didImpact = applyImpact(target);
	        }
	    } else {
	        //Since mobs are technically unarmored this will always trigger
	        didImpact = applyImpact(target);
	    }
	    
	    if(didImpact)
	    {
	        attacker.sendMessage("STRUCK WITH GREAT FORCE!");
	    }
	}
	
	public static boolean applyImpact(LivingEntity target)
	{
	    if(Math.random() * 100 > 75)
        {
            target.teleport(target.getLocation());
            target.damage(2);
            return true;
        }
	    return false;
	}
	
	public static void applyAoeDamage(Player attacker, EntityDamageByEntityEvent event, Plugin pluginx)
	{
		int targets = 0;
		
		int dmgAmount = (event.getDamage()/2);
        
        //Setup minimum damage
        if(dmgAmount < 1)
            dmgAmount = 1;
    	
    	if(event.getEntity() instanceof LivingEntity)
    	{
    		LivingEntity x = (LivingEntity) event.getEntity();
	    	targets = m.getTier(attacker);
	    	
    	for(Entity derp : x.getNearbyEntities(2.5, 2.5, 2.5))
    	{
    			//Make sure the Wolf is not friendly
    			if(derp instanceof Wolf)
    			{
					Wolf hurrDurr = (Wolf)derp;
					if(Taming.getOwner(hurrDurr, pluginx) == attacker)
						continue;
					if(Party.getInstance().inSameParty(attacker, Taming.getOwner(hurrDurr, pluginx)))
						continue;
				}
    			
    			//Damage nearby LivingEntities
    			if(derp instanceof LivingEntity && targets >= 1)
    			{
    				if(derp instanceof Player)
	    			{
	    				Player target = (Player)derp;
	    				
	    				if(Users.getProfile(target).getGodMode())
	    					continue;

	    				if(target.getName().equals(attacker.getName()))
	    					continue;
	    				
	    				if(Party.getInstance().inSameParty(attacker, target))
	    					continue;
	    				
	    				if(target.isDead())
	    					continue;
	    				
	    				if(targets >= 1 && derp.getWorld().getPVP())
	    				{
	    				    Combat.dealDamage(target, dmgAmount, attacker);
	    					target.sendMessage(ChatColor.DARK_RED+"Struck by CLEAVE!");
	    					targets--;
	    					continue;
	    				}
	    			}
    				else
	    			{			
	    				LivingEntity target = (LivingEntity)derp;
    					Combat.dealDamage(target, dmgAmount, attacker);
	    				targets--;
	    			}
    			}
    		}
    	}
	}
}
