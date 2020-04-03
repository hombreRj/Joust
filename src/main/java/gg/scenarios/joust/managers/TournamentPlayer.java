package gg.scenarios.joust.managers;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

@Getter
@Setter
public class TournamentPlayer {

    public static HashMap<String, TournamentPlayer> tournamentPlayerHashMap = new HashMap<>();

    private String name;
    private UUID uuid;
    private PlayerState state;

    public TournamentPlayer(String name, UUID uuid, PlayerState state) {
        this.name = name;
        this.uuid = uuid;
        this.state = state;
        tournamentPlayerHashMap.put(name, this);
    }

    public boolean isPlaying(){
        return !state.equals(PlayerState.MOD) || !state.equals(PlayerState.SPECTATOR) || !state.equals(PlayerState.ELIMINATED);
    }

    public static TournamentPlayer getTournamentPlayer(Player player){
        return tournamentPlayerHashMap.get(player.getName());
    }
}
