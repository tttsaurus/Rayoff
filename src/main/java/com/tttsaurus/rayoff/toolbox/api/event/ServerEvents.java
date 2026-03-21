package com.tttsaurus.rayoff.toolbox.api.event;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public final class ServerEvents {
    private ServerEvents() {
    }

    public static final class Lifecycle {
        public static final Event<LoadLevel> LOAD_LEVEL = Event.create();
        public static final Event<UnloadLevel> UNLOAD_LEVEL = Event.create();
        public static final Event<ServerLoad> LOAD_SERVER = Event.create();
        public static final Event<ServerUnload> UNLOAD_SERVER = Event.create();
        public static final Event<Join> JOIN = Event.create();

        private Lifecycle() {
        }

        @FunctionalInterface
        public interface LoadLevel {
            void onLoadLevel(MinecraftServer server, WorldServer level);
        }

        @FunctionalInterface
        public interface UnloadLevel {
            void onUnloadLevel(MinecraftServer server, WorldServer level);
        }

        @FunctionalInterface
        public interface ServerLoad {
            void onServerLoad(MinecraftServer server);
        }

        @FunctionalInterface
        public interface ServerUnload {
            void onServerUnload(MinecraftServer server);
        }

        @FunctionalInterface
        public interface Join {
            void onJoin(EntityPlayerMP player);
        }
    }

    public static final class Tick {
        private Tick() {
        }

        public static final Event<StartLevelTick> START_LEVEL_TICK = Event.create();
        public static final Event<EndLevelTick> END_LEVEL_TICK = Event.create();
        public static final Event<StartServerTick> START_SERVER_TICK = Event.create();
        public static final Event<EndServerTick> END_SERVER_TICK = Event.create();

        @FunctionalInterface
        public interface StartLevelTick {
            void onStartLevelTick(WorldServer level);
        }

        @FunctionalInterface
        public interface EndLevelTick {
            void onEndLevelTick(WorldServer level);
        }

        @FunctionalInterface
        public interface StartServerTick {
            void onStartServerTick(MinecraftServer server);
        }

        @FunctionalInterface
        public interface EndServerTick {
            void onEndServerTick(MinecraftServer server);
        }
    }

    public static final class Block {
        private Block() {
        }

        public static final Event<BlockUpdate> BLOCK_UPDATE = Event.create();

        @FunctionalInterface
        public interface BlockUpdate {
            void onBlockUpdate(World level, IBlockState blockState, BlockPos blockPos);
        }
    }

    public static final class Entity {
        private Entity() {
        }

        public static final Event<Load> LOAD = Event.create();
        public static final Event<Unload> UNLOAD = Event.create();
        public static final Event<StartTracking> START_TRACKING = Event.create();
        public static final Event<StopTracking> STOP_TRACKING = Event.create();

        @FunctionalInterface
        public interface Load {
            void onLoad(net.minecraft.entity.Entity entity);
        }

        @FunctionalInterface
        public interface Unload {
            void onUnload(net.minecraft.entity.Entity entity);
        }

        @FunctionalInterface
        public interface StartTracking {
            void onStartTracking(net.minecraft.entity.Entity entity, EntityPlayerMP player);
        }

        @FunctionalInterface
        public interface StopTracking {
            void onStopTracking(net.minecraft.entity.Entity entity, EntityPlayerMP player);
        }
    }
}
