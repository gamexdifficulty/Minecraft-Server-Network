python3 updater.py
cd "Velocity Proxy"
screen -dmS velocity java -jar velocity.jar
cd ../
cd "Lobby Servers/$(jq -r .lobby_server config.json)"
screen -dmS lobby java -jar server.jar
