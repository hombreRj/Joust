package gg.scenarios.joust.managers;

import gg.scenarios.joust.Joust;

import gg.scenarios.joust.challonge.Challonge;
import gg.scenarios.joust.challonge.GameType;
import gg.scenarios.joust.managers.arena.Arena;
import gg.scenarios.joust.managers.enums.KitType;
import gg.scenarios.joust.managers.enums.PlayerState;
import gg.scenarios.joust.managers.enums.TournamentState;
import gg.scenarios.joust.utils.ItemCreator;
import gg.scenarios.joust.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Getter
@Setter
public class Tournament {

    private Joust joust = Joust.getInstance();

    public static int globalMatchNumber = 1;

    private String name, url, tournamentNum, description, apiKey, bracketURL;
    private List<TournamentPlayer> host;
    private List<TournamentPlayer> players;
    private GameType type = GameType.SINGLE;
    private TournamentState tournamentState;
    private KitType kitType = KitType.BUILD;


    @Override
    public String toString() {
        return "Tournament{" +
                "type=" + type +
                ", tournamentState=" + tournamentState +
                ", kitType=" + kitType +
                '}';
    }

    Challonge challonge = new Challonge("AeoqInZkvafvAeTuiHNat7aADcJdLdxOjmiNVLPT", "ScenariosUHC", name + System.currentTimeMillis(), name + tournamentNum + " PvP tournament", "This tournament took place on na2.scenarios.gg", GameType.SINGLE);


    public Tournament(String name, String tournamentNum, String description) {
        this.name = name;
        this.url = name + "'s #" + tournamentNum;
        this.tournamentNum = tournamentNum;
        this.description = description;
    }

    public Tournament() {
        tournamentState = TournamentState.LOBBY;
    }

    public boolean setup() throws ExecutionException, InterruptedException {
        tournamentState = TournamentState.STARTED;
        return challonge.post().get();
    }

    public String getBracketURL() {
        return challonge.getUrl();
    }

    public boolean addMembers() throws ExecutionException, InterruptedException {
        TournamentPlayer.tournamentPlayerHashMap.keySet().forEach(s -> challonge.getParticipants().add(s));
        return challonge.addParticpants().get();
    }



    private void startMatches() throws ExecutionException, InterruptedException, Exception {
        Utils.broadcast(joust.getPREFIX() + "&c&lTournament is now starting matches are now being sent.");

        for (Arena arenas : Arena.arenasList) {
            if (arenas.isAvailable()) {
                startMatch(globalMatchNumber);
            }
            globalMatchNumber++;
            System.out.println(globalMatchNumber);
        }
    }


    public void start() throws ExecutionException, InterruptedException {
        tournamentState = TournamentState.STARTED;
        challonge.start().get();
        challonge.indexMatches().get();

        Inventory rules = tournamentRules();
        for (Player player : Bukkit.getOnlinePlayers()){
            player.openInventory(rules);
        }

        Bukkit.getScheduler().runTaskLater(joust, () -> {
            try {
                startMatches();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 20 * 10);

    }

    private Inventory tournamentRules() {
        Inventory rules = Bukkit.createInventory(null, 27, ChatColor.GREEN + "Rules");
        List<String> rulesLore = new ArrayList<>();

        rulesLore.add(ChatColor.RED +" " +ChatColor.STRIKETHROUGH +"-------------------------------------------------------------");
        rulesLore.add(ChatColor.GREEN + "» Tournament Rules");
        rulesLore.add(ChatColor.GREEN + "» Any use of hacked clients or .");
        rulesLore.add(ChatColor.GREEN + "» Illegal mods will result in a punishment.");
        rulesLore.add(ChatColor.GREEN + " ");
        rulesLore.add(ChatColor.GREEN + "» All tournament matches are final.");
        rulesLore.add(ChatColor.GREEN + "» There will be no rematches.");
        rulesLore.add(ChatColor.GREEN + " ");
        rulesLore.add(ChatColor.GREEN + "» Leaving the game whilst a tournament is.");
        rulesLore.add(ChatColor.GREEN + "» Ongoing might result in a disqualification.");
        rulesLore.add(ChatColor.GREEN + " ");
        rulesLore.add(ChatColor.GREEN + "» Exploiting game-breaking plugin .");
        rulesLore.add(ChatColor.GREEN + "» issues/abusing bugs will result in a punishment.");
        rulesLore.add(ChatColor.GREEN + " ");
        rulesLore.add(ChatColor.GREEN + "» Any sort of poor sportsmanship.");
        rulesLore.add(ChatColor.GREEN + "» Harassment or bullying will result in a punishment.");
        rulesLore.add(ChatColor.GREEN + " ");
        rulesLore.add(ChatColor.GREEN + "» Purposely delaying/disrupting the match will");
        rulesLore.add(ChatColor.GREEN + "» Result in a disqualification Repeated offenders will be punished.");

        rulesLore.add(ChatColor.RED +" " +ChatColor.STRIKETHROUGH +"-------------------------------------------------------------");

        ItemStack spacer = new ItemCreator(Material.STAINED_GLASS_PANE).setName(" ").get();


        ItemStack rule = new ItemCreator(Material.PAPER).setLore(rulesLore).setName(ChatColor.RED + "Rules").get();
        rules.setItem(13, rule);

        for (int i = 0; i < rules.getContents().length; i++) {
            if (rules.getContents()[i] == null){
                rules.setItem(i, spacer);
            }
        }

        return rules;
    }


    public void randomize() throws ExecutionException, InterruptedException {
        challonge.addParticpants().get();
        challonge.randomize().get();
    }

    public void update(int matchId, String name) throws ExecutionException, InterruptedException {
        challonge.updateMatch(matchId, name).get();
    }

    public void end() throws ExecutionException, InterruptedException {
        tournamentState = TournamentState.OVER;
        challonge.end().get();
    }

    public void startNextMatch() {
        System.out.println(105);

        if (tournamentState == TournamentState.STARTED) {
            try {
                Arena arenas = joust.getArenaManager().getNextAvailableArena();
                if (arenas.isAvailable()) {
                    System.out.println(110);
                    startMatch(globalMatchNumber);
                    globalMatchNumber++;
                    System.out.println(globalMatchNumber);
                } else {
                    System.out.println(114);
                    System.out.println("Could not load arena trying again in 10 seconds");
                    Bukkit.getScheduler().runTaskLater(joust, () -> {
                        try {
                            System.out.println(117);
                            startMatch(globalMatchNumber);
                            globalMatchNumber++;
                            System.out.println(globalMatchNumber);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }, 20 * 10);
                }
            } catch (Exception p) {
                p.printStackTrace();
            }
        }
    }

    public void startMatch(int matchNumber) throws Exception {
        System.out.println(130);
        Integer[] names = challonge.getMatchParticipants(matchNumber).get();
        try {
            TournamentPlayer p2 = TournamentPlayer.getTournamentPlayer(challonge.getNameFromId(names[0]));
        }catch (NullPointerException e){
            forfeit(Bukkit.getPlayer(challonge.getNameFromId(names[1])), challonge.getNameFromId(names[0]));
        }
        try {
            TournamentPlayer p1 = TournamentPlayer.getTournamentPlayer(challonge.getNameFromId(names[1]));
        }catch (NullPointerException e){
            forfeit(Bukkit.getPlayer(challonge.getNameFromId(names[0])), challonge.getNameFromId(names[1]));
        }
        TournamentMatch match = new TournamentMatch(matchNumber, challonge.getNameFromId(names[0]), challonge.getNameFromId(names[1]));
        }


    private void forfeit(Player winner, String loser) throws ExecutionException, InterruptedException {
        System.out.println(205);

        for (Player player :Bukkit.getOnlinePlayers()){
            if (!(Joust.mods.contains(player.getUniqueId()))) {
                winner.showPlayer(player);
            }
        }
        TournamentPlayer player = TournamentPlayer.getTournamentPlayer(winner);
        TournamentPlayer losers = TournamentPlayer.getTournamentPlayer(loser);

        Arena a =player.getMatch().getArena();
        a.setMatch(null);
        a.setAvailable(true);
        a.clear();
        //TODO: set match to null;
        player.setMatch(null);
        losers.setMatch(null);
        TournamentPlayer.getTournamentPlayer(winner).setState(PlayerState.LOBBY);
        TournamentPlayer.getTournamentPlayer(loser).setState(PlayerState.LOBBY);


        player.getPlayer().teleport(Bukkit.getWorld("world").getSpawnLocation());

        joust.getTournament().getChallonge().updateMatch(player.getMatchId(), winner.getName());
        player.getPlayer().sendMessage(ChatColor.RED + "Your opponent is not online so you have won");
        Bukkit.getScheduler().runTaskLater(joust, () -> {
            try {
                joust.getTournament().startNextMatch();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 20 * 7);

        player.setMatchId(0);
        losers.setMatchId(0);


        winner.getPlayer().getInventory().clear();
        winner.getPlayer().getInventory().setBoots(null);
        winner.getPlayer().getInventory().setHelmet(null);
        winner.getPlayer().getInventory().setLeggings(null);
        winner.getPlayer().getInventory().setChestplate(null);
    }



}