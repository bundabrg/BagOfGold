package one.lindegaard.MobHunting.rewards;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.compatibility.ProtocolLibCompat;
import one.lindegaard.MobHunting.compatibility.ProtocolLibHelper;

@SuppressWarnings("deprecation")
public class PlayerPickupItemEventListener implements Listener{

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPickupReward(PlayerPickupItemEvent event) {
		// This event is NOT called when the inventory is full.
		if (event.isCancelled())
			return;
		Item item = event.getItem();
		if (Reward.isReward(item)) {
			Reward reward = Reward.getReward(item);
			Player player = event.getPlayer();
			// If not Gringotts
			if (reward.getMoney() != 0)
				if (!MobHunting.getConfigManager().dropMoneyOnGroundUseAsCurrency) {
					MobHunting.getRewardManager().depositPlayer(player, reward.getMoney());
					if (ProtocolLibCompat.isSupported())
						ProtocolLibHelper.pickupMoney(player, item);
					item.remove();
					event.setCancelled(true);
					Messages.playerActionBarMessage(player, Messages.getString("mobhunting.moneypickup", "money",
							MobHunting.getRewardManager().format(reward.getMoney())));
				} else {
					boolean found = false;
					HashMap<Integer, ? extends ItemStack> slots = player.getInventory()
							.all(item.getItemStack().getType());
					for (int slot : slots.keySet()) {
						ItemStack is = player.getInventory().getItem(slot);
						if (Reward.isReward(is)) {
							Reward rewardInSlot = Reward.getReward(is);
							if ((reward.isBagOfGoldReward() || reward.isItemReward())
									&& rewardInSlot.getRewardUUID().equals(reward.getRewardUUID())) {
								ItemMeta im = is.getItemMeta();
								Reward newReward = Reward.getReward(is);
								newReward.setMoney(newReward.getMoney() + reward.getMoney());
								im.setLore(newReward.getHiddenLore());
								String displayName = MobHunting.getConfigManager().dropMoneyOnGroundItemtype
										.equalsIgnoreCase("ITEM")
												? MobHunting.getRewardManager().format(newReward.getMoney())
												: newReward.getDisplayname() + " ("
														+ MobHunting.getRewardManager().format(newReward.getMoney())
														+ ")";
								im.setDisplayName(
										ChatColor.valueOf(MobHunting.getConfigManager().dropMoneyOnGroundTextColor)
												+ displayName);
								is.setItemMeta(im);
								is.setAmount(1);
								event.setCancelled(true);
								if (ProtocolLibCompat.isSupported())
									ProtocolLibHelper.pickupMoney(player, item);
								item.remove();
								Messages.debug("Added %s to item in slot %s, new value is %s",
										MobHunting.getRewardManager().format(reward.getMoney()), slot,
										MobHunting.getRewardManager().format(newReward.getMoney()));
								found = true;
								break;
							}
						}
					}

					if (!found) {
						ItemStack is = item.getItemStack();
						ItemMeta im = is.getItemMeta();
						String displayName = MobHunting.getConfigManager().dropMoneyOnGroundItemtype
								.equalsIgnoreCase("ITEM") ? MobHunting.getRewardManager().format(reward.getMoney())
										: reward.getDisplayname() + " ("
												+ MobHunting.getRewardManager().format(reward.getMoney()) + ")";
						im.setDisplayName(ChatColor.valueOf(MobHunting.getConfigManager().dropMoneyOnGroundTextColor)
								+ displayName);
						im.setLore(reward.getHiddenLore());
						is.setItemMeta(im);
						item.setItemStack(is);
						item.setMetadata(RewardManager.MH_REWARD_DATA,
								new FixedMetadataValue(MobHunting.getInstance(), new Reward(reward)));
					}
				}
			if (RewardManager.getDroppedMoney().containsKey(item.getEntityId()))
				RewardManager.getDroppedMoney().remove(item.getEntityId());
			if (reward.getMoney() == 0)
				Messages.debug("%s picked up a %s (# of rewards left=%s)", player.getName(), reward.getDisplayname(),
						RewardManager.getDroppedMoney().size());
			else
				Messages.debug("%s picked up a %s with a value:%s (# of rewards left=%s)", player.getName(),
						reward.getDisplayname(), MobHunting.getRewardManager().format(reward.getMoney()),
						RewardManager.getDroppedMoney().size());
		}
	}

}
