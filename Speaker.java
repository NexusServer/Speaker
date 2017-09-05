package nexus;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import me.onebone.economyapi.EconomyAPI;

public class Speaker extends PluginBase implements Listener{
	final Pattern chatReg=Pattern.compile("^@확성기 ");
	public Config config;
	@Override
	public void onEnable(){
		this.getServer().getPluginManager().registerEvents(this, this);
		this.getDataFolder().mkdirs();
		this.config=new Config(this.getDataFolder()+"/settings.json",Config.JSON);
		if(!this.config.exists("default-chat")){
			this.config.set("default-chat","world");
		}
		if(!this.config.exists("speaker-cost")){
			this.config.set("speaker-cost", "-10000");
		}
		if(Integer.parseInt(this.config.get("speaker-cost").toString())>0){
			this.config.set("speaker-cost", "-10000");
			this.getServer().getLogger().alert("speaker-cost이(가) 0보다 높으므로 기본값인 -10000으로 맞춥니다.");
		}
		if(!this.config.exists("speaker-pattern")){
			this.config.set("speaker-pattern", "§b[확성기] {NAME}:{MESSAGE}");
		}
	}
	@Override
	public void onDisable(){
		this.config.save();
	}
	@EventHandler
	public void onChat(PlayerChatEvent event){
		String message=event.getMessage();
		Player player=event.getPlayer();
		String name=player.getDisplayName();
		Matcher result=chatReg.matcher(message);
		Set<CommandSender> set=new HashSet<CommandSender>();
		double money=EconomyAPI.getInstance().myMoney(player);
		if(money<Integer.parseInt(this.config.get("speaker-cost").toString())){
			if(!player.isOp()){
				player.sendMessage("§e[확성기] 돈이 부족합니다.");
				return;
			}
		}
		if(result.find()){
			message=message.substring(5);
			String format=this.config.get("speaker-pattern").toString();
			this.getServer().broadcastMessage(format.replace("{NAME}",name).replace("{MESSAGE}",message));
			EconomyAPI.getInstance().reduceMoney(player,-Integer.parseInt(this.config.get("speaker-cost").toString()));
			player.sendMessage("§e[확성기] -"+this.config.get("speaker-cost")+"원");
			event.setCancelled();
			return;
		}
		else{
			switch(this.config.get("default-chat").toString()){
				case "world":
				for(Player players:player.getLevel().getPlayers().values()){
					set.add((CommandSender)players);
				}
				event.setRecipients(set);
				break;
				case "server":
				break;
				default:
				this.config.set("default-chat","world");
				for(Player players:player.getLevel().getPlayers().values()){
					set.add((CommandSender)players);
				}
				event.setRecipients(set);
				break;
			}
			return;
		}
	}
}