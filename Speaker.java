package nexus;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.LinkedHashMap;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import me.onebone.economyapi.EconomyAPI;

public class Speaker extends PluginBase implements Listener{
	final Pattern chatReg=Pattern.compile("^@(ㅎ|확|s|S) ");
	public Config config;
	@Override
	public void onEnable(){
		LinkedHashMap<String, Object> map=new LinkedHashMap<String, Object>();
		this.getServer().getPluginManager().registerEvents(this, this);
		this.getDataFolder().mkdirs();
		map.put("default-chat","world");
		map.put("speaker-cost","10000");
		map.put("speaker-pattern","§b[확성기] {NAME} : {MESSAGE}");
		this.config=new Config(this.getDataFolder()+"/settings.json",Config.JSON,map);
		if(Integer.parseInt(this.config.get("speaker-cost").toString())<0){
			this.config.set("speaker-cost", "10000");
			this.getServer().getLogger().alert("speaker-cost이(가) 0보다  작으므로 기본값인 10000으로 맞춥니다.");
			//settings.json : speaker-cost 를 0 으로 해놓으면 무료입니다.
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
		int money=(int)EconomyAPI.getInstance().myMoney(player);
		int cost=Integer.parseInt(this.config.get("speaker-cost").toString());
		if(result.find()){
			message=message.substring(3);
			String format=this.config.get("speaker-pattern").toString();
			if(money<cost){
				if(!player.isOp()){
					player.sendMessage("§e[확성기] 돈이 부족합니다.");
					event.setCancelled();
					return;
				}
			}
			if(!player.isOp()){
				EconomyAPI.getInstance().reduceMoney(player,cost);
				player.sendMessage("§e[확성기] -"+this.config.get("speaker-cost")+"원");
			}
			this.getServer().broadcastMessage(format.replace("{NAME}",name).replace("{MESSAGE}",message));
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