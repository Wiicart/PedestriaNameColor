package com.pedestriamc.namecolor.nms;

import org.bukkit.entity.Player;

public class Version_1_21 implements PlayerNameTagManager {

    @Override
    public void setOverHeadName(Player player, String name) {
        /*ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        ServerLevel level = serverPlayer.serverLevel();

        //Creating armor stand
        ArmorStand armorStand = new ArmorStand(serverPlayer.serverLevel(), serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ());
        armorStand.setInvisible(true);
        armorStand.setCustomName(Component.literal(name));
        armorStand.setCustomNameVisible(true);

        //chatgpt jargon
        Consumer<Packet<?>> packetConsumer = packet -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                ((CraftPlayer) onlinePlayer).getHandle().connection.send((Packet<ClientGamePacketListener>) packet);
            }
        };
        Set<ServerPlayerConnection> trackedPlayers = new HashSet<>(); // Adjust according to your setup
        ServerEntity armorStandServerEntity = new ServerEntity(level,armorStand,10,true, packetConsumer, trackedPlayers);



        //Creating spawn packet
        ClientboundAddEntityPacket addEntityPacket = new ClientboundAddEntityPacket(armorStand, armorStandServerEntity);
        packetConsumer.accept(addEntityPacket);
        //ClientboundSetPassengersPacket passengersPacket*/

    }

    @Override
    public void removeOverHeadName(Player player) {

    }
}



