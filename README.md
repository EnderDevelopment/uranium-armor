# Minecraft Plugin: Uranium Armor

Uranium Armor is a Minecraft plugin that adds a complete Uranium-234 armor set with glowing green leather armor, extremely high armor and toughness values, knockback resistance, proximity poison radiation, and damage reflection.

## Install

1. Use a Paper server running Minecraft 1.21.11 or a compatible 1.21 server.
2. Install Java 21.
3. Build the plugin with Maven:
   ```bash
   mvn package
   ```
4. Copy the generated `target/plugin.jar` file into the server's `plugins` folder.
5. Start or restart the server.
6. Grant the `uraniumarmor.give` permission to players who should be able to use the command. This permission is granted to operators by default.

## Use

1. Join the server as a player with the `uraniumarmor.give` permission.
2. Run:
   ```text
   /uraniumarmor
   ```
3. The command equips a complete Uranium-234 helmet, chestplate, leggings, and boots.
4. Wearing the complete set gives each armor piece 25 armor, 12 armor toughness, and 0.25 knockback resistance.
5. While wearing the complete set, nearby living entities within 4 blocks receive Poison II effects every second.
6. When another living entity damages a full-set wearer, 5 damage is reflected back to the attacker. Projectile damage is reflected to the projectile's living shooter.
7. A reflected attacker cannot receive another reflected hit from the same armor effect for 100 milliseconds.

---

More Minecraft plugin projects by EnderDevelopment: https://github.com/EnderDevelopment

Project page: https://dash.enderdevelopment.com/community/13b18a49-28be-4247-b3d4-8bed14de7d8d?utm_source=github&utm_medium=readme&utm_campaign=auto_publish&utm_content=13b18a49-28be-4247-b3d4-8bed14de7d8d
