package dev.lazurite.rayon.core.impl.physics.util.supplier;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ServerWorldSupplier implements WorldSupplier {
    private final MinecraftServer server;

    public ServerWorldSupplier(MinecraftServer server) {
        this.server = server;
    }

    @Override
    public List<World> getWorlds() {
        return new ArrayList<>((Collection<? extends World>) server.getWorlds());
    }

    @Override
    public World getWorld(RegistryKey<World> key) {
        return server.getWorld(key);
    }
}
