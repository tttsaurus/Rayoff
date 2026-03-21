package com.tttsaurus.rayoff.toolbox.api.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;

public final class ClientEvents {
    private ClientEvents() {
    }

    public static final class Lifecycle {
        private Lifecycle() {
        }

        public static final Event<LoadLevel> LOAD_LEVEL = Event.create();
        public static final Event<PreLogin> PRE_LOGIN = Event.create();
        public static final Event<PostLogin> POST_LOGIN = Event.create();
        public static final Event<Disconnect> DISCONNECT = Event.create();

        @FunctionalInterface
        public interface LoadLevel {
            void onLoadLevel(Minecraft minecraft, WorldClient level);
        }

        @FunctionalInterface
        public interface PreLogin {
            void onPreLogin(Minecraft minecraft);
        }

        @FunctionalInterface
        public interface PostLogin {
            void onPostLogin(Minecraft minecraft, WorldClient level, EntityPlayerSP player);
        }

        @FunctionalInterface
        public interface Disconnect {
            void onDisconnect(Minecraft minecraft, WorldClient level);
        }
    }

    public static final class Tick {
        private Tick() {
        }

        public static final Event<StartLevelTick> START_LEVEL_TICK = Event.create();
        public static final Event<EndLevelTick> END_LEVEL_TICK = Event.create();
        public static final Event<StartClientTick> START_CLIENT_TICK = Event.create();
        public static final Event<EndClientTick> END_CLIENT_TICK = Event.create();

        @FunctionalInterface
        public interface StartLevelTick {
            void onStartTick(WorldClient level);
        }

        @FunctionalInterface
        public interface EndLevelTick {
            void onEndTick(WorldClient level);
        }

        @FunctionalInterface
        public interface StartClientTick {
            void onStartTick(Minecraft minecraft);
        }

        @FunctionalInterface
        public interface EndClientTick {
            void onEndTick(Minecraft minecraft);
        }
    }

    public static final class Entity {
        private Entity() {
        }

        public static final Event<EntityLoad> LOAD = Event.create();
        public static final Event<EntityUnload> UNLOAD = Event.create();

        @FunctionalInterface
        public interface EntityLoad {
            void onLoad(net.minecraft.entity.Entity entity);
        }

        @FunctionalInterface
        public interface EntityUnload {
            void onUnload(net.minecraft.entity.Entity entity);
        }
    }

    public static final class Player {
        private Player() {
        }

        public static final Event<PlayerAdd> ADD = Event.create();

        @FunctionalInterface
        public interface PlayerAdd {
            void onAdd(AbstractClientPlayer abstractClientPlayer, boolean isLocalPlayer);
        }
    }

    public static final class Render {
        private Render() {
        }

        public static final Event<BeforeDebug> BEFORE_DEBUG = Event.create();

        @FunctionalInterface
        public interface BeforeDebug {
            void onBeforeDebug(float tickDelta, WorldClient level);
        }
    }
}
