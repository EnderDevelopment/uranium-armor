package com.enderdevelopment.plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.EquipmentSlotGroup;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

public final class Main extends JavaPlugin implements Listener {
    private static final double RADIATION_RADIUS = 4.0;
    private static final double REFLECTED_DAMAGE = 5.0;
    private static final double ARMOR_PER_PIECE = 25.0;
    private static final double TOUGHNESS_PER_PIECE = 12.0;
    private static final double KNOCKBACK_RESISTANCE_PER_PIECE = 0.25;
    private static final String ARMOR_ID = "uranium_234";
    private final Map<UUID, Long> reflectedHits = new HashMap<>();
    private NamespacedKey armorKey;
    private BukkitTask radiationTask;

    @Override
    public void onEnable() {
        armorKey = new NamespacedKey(this, ARMOR_ID);
        Bukkit.getPluginManager().registerEvents(this, this);
        radiationTask = Bukkit.getScheduler().runTaskTimer(this, this::applyRadiation, 20L, 20L);
    }

    @Override
    public void onDisable() {
        if (radiationTask != null) {
            radiationTask.cancel();
        }
        reflectedHits.clear();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        giveArmor(player);
        player.sendMessage(Component.text("Uranium-234 armor equipped.", NamedTextColor.GREEN));
        return true;
    }

    private void giveArmor(Player player) {
        PlayerInventory inventory = player.getInventory();
        inventory.setHelmet(createArmor(Material.LEATHER_HELMET, "Uranium-234 Helmet", EquipmentSlotGroup.HEAD));
        inventory.setChestplate(createArmor(Material.LEATHER_CHESTPLATE, "Uranium-234 Chestplate", EquipmentSlotGroup.CHEST));
        inventory.setLeggings(createArmor(Material.LEATHER_LEGGINGS, "Uranium-234 Leggings", EquipmentSlotGroup.LEGS));
        inventory.setBoots(createArmor(Material.LEATHER_BOOTS, "Uranium-234 Boots", EquipmentSlotGroup.FEET));
    }

    private ItemStack createArmor(Material material, String name, EquipmentSlotGroup slotGroup) {
        ItemStack item = new ItemStack(material);
        ItemMeta baseMeta = item.getItemMeta();
        if (!(baseMeta instanceof LeatherArmorMeta meta)) {
            return item;
        }
        meta.setColor(Color.fromRGB(40, 255, 70));
        meta.displayName(Component.text(name, NamedTextColor.GREEN));
        meta.getPersistentDataContainer().set(armorKey, PersistentDataType.STRING, ARMOR_ID);
        meta.addAttributeModifier(Attribute.ARMOR, new AttributeModifier(
                new NamespacedKey(this, "uranium_armor"), ARMOR_PER_PIECE,
                AttributeModifier.Operation.ADD_NUMBER, slotGroup));
        meta.addAttributeModifier(Attribute.ARMOR_TOUGHNESS, new AttributeModifier(
                new NamespacedKey(this, "uranium_toughness"), TOUGHNESS_PER_PIECE,
                AttributeModifier.Operation.ADD_NUMBER, slotGroup));
        meta.addAttributeModifier(Attribute.KNOCKBACK_RESISTANCE, new AttributeModifier(
                new NamespacedKey(this, "uranium_knockback"), KNOCKBACK_RESISTANCE_PER_PIECE,
                AttributeModifier.Operation.ADD_NUMBER, slotGroup));
        item.setItemMeta(meta);
        return item;
    }

    private void applyRadiation() {
        for (Player wearer : Bukkit.getOnlinePlayers()) {
            if (!hasFullSet(wearer)) {
                continue;
            }
            for (Entity nearby : wearer.getNearbyEntities(RADIATION_RADIUS, RADIATION_RADIUS, RADIATION_RADIUS)) {
                if (nearby instanceof LivingEntity target && target != wearer) {
                    target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 60, 1, true, true, true));
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player wearer) || !hasFullSet(wearer)) {
            return;
        }
        Entity attacker = event.getDamager();
        if (attacker instanceof Projectile projectile && projectile.getShooter() instanceof LivingEntity shooter) {
            attacker = shooter;
        }
        if (!(attacker instanceof LivingEntity livingAttacker) || livingAttacker == wearer
                || livingAttacker instanceof Player attackerPlayer && hasFullSet(attackerPlayer)) {
            return;
        }
        long now = System.currentTimeMillis();
        long previous = reflectedHits.getOrDefault(livingAttacker.getUniqueId(), 0L);
        if (now - previous < 100L) {
            return;
        }
        reflectedHits.put(livingAttacker.getUniqueId(), now);
        livingAttacker.damage(REFLECTED_DAMAGE, wearer);
    }

    private boolean hasFullSet(Player player) {
        PlayerInventory inventory = player.getInventory();
        return isUranium(inventory.getHelmet())
                && isUranium(inventory.getChestplate())
                && isUranium(inventory.getLeggings())
                && isUranium(inventory.getBoots());
    }

    private boolean isUranium(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        return ARMOR_ID.equals(meta.getPersistentDataContainer().get(armorKey, PersistentDataType.STRING));
    }
}
