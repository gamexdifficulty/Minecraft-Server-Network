package net.frostlightgames.lobbyCommand;

import com.google.inject.Inject;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import net.frostlightgames.lobbyCommand.Commands.CommandListener;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(id = "lobbycommand", name = "LobbyCommand", version = "1.0.0")
public class LobbyCommand {

    public final ProxyServer server;
    public final Logger logger;
    public final Path dataDirectory;

    @Inject
    public LobbyCommand(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        server.getEventManager().register(this, new CommandLobby(this));
        CommandManager commandManager = server.getCommandManager();
        CommandMeta commandMeta = commandManager.metaBuilder("lobby")
                .aliases("hub")
                .plugin(this)
                .build();
        SimpleCommand commandToRegister = new CommandListener(this);

        commandManager.register(commandMeta, commandToRegister);
    }
}