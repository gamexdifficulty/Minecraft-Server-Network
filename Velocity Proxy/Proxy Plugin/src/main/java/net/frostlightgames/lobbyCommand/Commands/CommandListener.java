package net.frostlightgames.lobbyCommand.Commands;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.frostlightgames.lobbyCommand.LobbyCommand;

import java.lang.reflect.Type;
import java.util.Optional;

public final class CommandListener implements SimpleCommand {

    private final LobbyCommand server;

    public CommandListener(LobbyCommand server) {this.server = server;}

    @Override
    public void execute(final Invocation invocation){
        CommandSource source = invocation.source();

        server.logger.info(source.toString());
        server.logger.info(source.getClass().toString());

        if (source instanceof Player) {
            Player player = (Player) source;
            Optional<RegisteredServer> lobbyServer = this.server.server.getServer("lobby");
            lobbyServer.ifPresent((target) -> player.createConnectionRequest(target).connect());
        }
    }
}