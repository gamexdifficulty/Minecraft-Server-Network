package net.frostlightgames.lobbyCommand;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.ConnectionRequestBuilder;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class CommandLobby {
    private final LobbyCommand server;

        public CommandLobby(LobbyCommand server) {this.server = server;}

    @Subscribe
    public void onPlayerChat(PlayerChatEvent event) {
        if (Objects.equals(event.getMessage(), "lobby")){
            Player player = event.getPlayer();

            Optional<RegisteredServer> lobbyServer = this.server.server.getServer("lobby");
            lobbyServer.ifPresent((target) -> player.createConnectionRequest(target).connect());
        }
    }
}
