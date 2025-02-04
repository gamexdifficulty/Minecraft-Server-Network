import os
import json
import time
import shutil
import datetime
import requests
import urllib.request

class Updater:
    def __init__(self):
        self.downloaded_paper = False
        self.downloaded_velocity = False

        self.latest_minecraft_version = ""
        self.latest_paper_version = ""
        self.latest_velocity_version = ""
        
        self.log_filename = ""
        self.start_time = datetime.datetime.now()
        self.time_format = "%d.%m.%Y %H:%M:%S"

        self.minecraft_request_url = "https://launchermeta.mojang.com/mc/game/version_manifest.json"
        self.velocity_request_url = "https://api.papermc.io/v2/projects/velocity/"

    def log(self,message:str):
        try:
            if not os.path.exists("logs"):
                print(f"|{datetime.datetime.now().strftime(self.time_format).ljust(19," ")}| Creating log directory... |")
                os.mkdir("logs")

            if self.log_filename == "":
                self.log_filename = f"update {datetime.datetime.now().strftime(self.time_format.replace(":","_"))}.log"
                print(f"|{datetime.datetime.now().strftime(self.time_format).ljust(19," ")}| Creating log file: {self.log_filename}... |")
                with open(os.path.join("logs",self.log_filename),"w+") as f:
                    f.write("")
                    f.close()

                if os.path.exists(os.path.join("logs",self.log_filename)):
                    print(f"|{datetime.datetime.now().strftime(self.time_format).ljust(19," ")}| Successfully created logfile: {self.log_filename}. |")

            with open(os.path.join("logs",self.log_filename),"+a") as f:
                message = f"|{datetime.datetime.now().strftime(self.time_format).ljust(19," ")}| {message} |"
                f.write(message+"\n")
                print(message)
        except Exception as e:
            print(f"Error writing to log file: {e}.")

    def get_request_data(self,url:str) -> dict:
        try:
            data = requests.request(method="get",url=url).json()
            if data == "" or data == None:
                self.log(f"No data received from: {url}.")
                return ""
            return data
        except Exception as e:
            self.log(f"Error while getting data from: {url} | {e}.")
            return ""

    def set_error(self):
        try:
            if "ERROR" not in self.log_filename:
                if os.path.exists(os.path.join("logs",self.log_filename)):
                    os.rename(os.path.join("logs",self.log_filename),os.path.join("logs",f"ERROR {self.log_filename}"))
                    self.log_filename = f"ERROR {self.log_filename}"
        except Exception as e:
            print(f"Could not rename logfile: {e}")

    def download(self,url:str,file_name:str) -> bool:
        try:
            urllib.request.urlretrieve(url,file_name)
        except Exception as e:
            self.log(f"Error while downloading newest {file_name} file: {e}.")
            self.set_error()
            return False
        else:
            return True
    
    def load_config(self,key:str):
        try:
            if not os.path.exists("config.json"):
                with open("config.json","w+") as f:
                    f.write("")
                    json.dump(
                        {
                            "lobby_server":"xxx",
                            "velocity":{
                                "version":"xxx"
                            },
                            "paper":{
                                "server_locations":[
                                    "/",
                                ]
                            }
                        },f,indent=4)
                    f.close()

            with open("config.json","r") as f:
                data = json.load(f)
                if key in data:
                    return data[key]
                else:
                    return False
        except Exception as e:
            self.log(f"Error while loading config with key: {key} | {e}.")
            self.set_error()
            return False
        
    def save_config(self,key:str,value:str):
        try:
            with open("config.json","r") as f:
                data = json.load(f)
                data[key] = value
                f.close
                with open("config.json","w+") as f:
                    json.dump(data,f,indent=4,sort_keys=True)
                    return True
        except Exception as e:
            self.log(f"Error while saving config with key: {key} and value: {value} | {e}.")
            self.set_error()
            return False
    
    def get_latest_minecraft_version(self):
        try:
            mc_request_data = self.get_request_data(self.minecraft_request_url)
            for version in mc_request_data["versions"]:
                if version["type"]=="release":
                    self.latest_minecraft_version = version["id"]
                    self.log(f"Found latest Minecraft version: {self.latest_minecraft_version}")
                    break
        except Exception as e:
            self.log(f"Error while getting latest Minecraft version: {e}")
            self.set_error()

    def get_latest_paper_version(self):
        try:
            paper_request_data = self.get_request_data(f"https://api.papermc.io/v2/projects/paper/versions/{self.latest_minecraft_version}/")
            self.latest_paper_version = paper_request_data["builds"][-1]
            self.log(f"Found latest Paper version for Minecraft {self.latest_minecraft_version}: {self.latest_paper_version}")
        except Exception as e:
            self.log(f"Error while getting latest Paper version: {e}")
            self.set_error()

    def get_latest_velocity_version(self):
        try:
            velocity_request_data = self.get_request_data(self.velocity_request_url)
            velocity_version = velocity_request_data["versions"][-1]
            velocity_build_request_data = self.get_request_data(f"https://api.papermc.io/v2/projects/velocity/versions/{velocity_version}/builds/")
            velocity_build_version = velocity_build_request_data["builds"][-1]["build"]
            self.latest_velocity_version = f"{velocity_version} {velocity_build_version}"
            self.log(f"Found latest Velocity version: {self.latest_velocity_version}")
        except Exception as e:
            self.log(f"Error while getting latest Velocity version: {e}")
            self.set_error()

    def download_paper(self):
        try:
            if not self.downloaded_paper:
                self.log(f"Downloading Paper...")
                success = self.download(f"https://api.papermc.io/v2/projects/paper/versions/{self.latest_minecraft_version}/builds/{self.latest_paper_version}/downloads/paper-{self.latest_minecraft_version}-{self.latest_paper_version}.jar", "server.jar")
                if success:
                    self.log(f"Download for Paper completed.")
                    self.downloaded_paper = True
        except Exception as e:
            self.log(f"Error while downloading Paper: {e}")
            self.set_error()

    def download_velocity(self):
        try:
            if not self.downloaded_velocity:
                self.log(f"Downloading Velocity...")
                version = self.latest_velocity_version.split(" ")[0]
                build = self.latest_velocity_version.split(" ")[1]
                success = self.download(f"https://api.papermc.io/v2/projects/velocity/versions/{version}/builds/{build}/downloads/velocity-{version}-{build}.jar", "velocity.jar")
                if success:
                    self.log(f"Download for Velocity completed.")
                    self.downloaded_velocity = True
        except Exception as e:
            self.log(f"Error while downloading Velocity: {e}")
            self.set_error()

    def scan_for_servers(self):
        for dirpath, dirnames, filenames in os.walk("."):
            for filename in filenames:
                if filename.endswith(".py"):
                    print(filename)

    def start_update(self):
        self.load_config("lobby_server")
        self.get_latest_minecraft_version()
        if self.latest_minecraft_version != "":
            self.get_latest_paper_version()
            self.download_paper()
        else:
            self.log("Skipping Paper server update")
        
        self.get_latest_velocity_version()
        if self.latest_velocity_version != "":
            self.download_velocity()
        else:
            self.log("Skipping Velocity proxy update")

if __name__ == "__main__":
    updater = Updater()
    updater.start_update()

#                         for server_type in ["World Servers","Lobby Servers"]: 
#                             for server in os.listdir(server_type):
#                                 server_config_path = os.path.join(server_type,server,"config","config.json")
#                                 if os.path.exists(server_config_path):
#                                     updating = load_config("update",server_config_path)
#                                     if not updating:
#                                         log(f"Skipping update for server {server}.")
#                                     else:
#                                         new_version = f"{mc_version}-{paper_version}"
#                                         current_version = load_config("version",server_config_path)
#                                         if current_version == new_version:
#                                             log(f"Server {server} is up to date.")
#                                         else:
#                                             log(f"Updating paper on server {server} from {current_version} to {new_version}.")
#                                             
#                                             if os.path.exists(os.path.join(server_type,server,"server.jar")):
#                                                 try:
#                                                     os.remove(os.path.join(server_type,server,"server.jar"))
#                                                 except Exception as e:
#                                                     log("Error while removing old paper server file")
#                                                     set_error()
#                                             try:
#                                                 shutil.copyfile("server.jar",os.path.join(server_type,server,"server.jar"))
#                                             except Exception as e:
#                                                 log(f"Error while copying new paper server file: {e}")
#                                                 set_error()
#                                             else:
#                                                 save_config("version",new_version,server_config_path)
#                                                 log(f"Updated server {server} to {new_version}")

#                         if downloaded_paper:
#                             try:
#                                 os.remove("server.jar")
#                             except Exception as e:
#                                 log("Error while deleting downloaded paper server file")


#                         log(f"Download of Velocity server file completed.")
#                         if os.path.exists(os.path.join("Velocity Proxy","velocity.jar")):
#                             try:
#                                 os.remove(os.path.join("Velocity Proxy","velocity.jar"))
#                             except Exception as e:
#                                 log("Could not remove old velocity server file")
#                                 set_error()
#                         if os.path.exists("Velocity Proxy"):
#                             try:
#                                 os.rename("velocity.jar","Velocity Proxy/velocity.jar")
#                             except Exception as e:
#                                 log(f"Could not move velocity server file: {e}")
#                                 set_error()
#                         save_config("current_velocity_version",velocity_server_version)