package gg.scenarios.joust.managers;

import gg.scenarios.joust.Joust;

import gg.scenarios.joust.challonge.Challonge;
import gg.scenarios.joust.challonge.GameType;
import gg.scenarios.joust.managers.arena.Arena;
import gg.scenarios.joust.managers.arena.ArenaManager;
import gg.scenarios.joust.managers.enums.KitType;
import gg.scenarios.joust.managers.enums.PlayerState;
import gg.scenarios.joust.managers.enums.TournamentState;
import gg.scenarios.joust.utils.ItemCreator;
import gg.scenarios.joust.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.ObjectUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter
@Setter
public class Tournament {

    private Joust joust = Joust.getInstance();

    public static int globalMatchNumber = 1, round = 1, lastRound = 1;

    private String name, url, tournamentNum, description, apiKey, bracketURL;
    private List<TournamentPlayer> host;
    private List<TournamentPlayer> players;
    private GameType type = GameType.SINGLE;
    private TournamentState tournamentState;
    private KitType kitType = KitType.BUILD;
    private Queue<Integer> matchQueue;
    private boolean membersAdded = false;

    @Override
    public String toString() {
        return "Tournament{" +
                "type=" + type +
                ", tournamentState=" + tournamentState +
                ", kitType=" + kitType +
                '}';
    }

    Challonge challonge = new Challonge(Joust.getInstance().getAPI_KEY(), "ScenariosUHC", "Scenarios" + System.currentTimeMillis(), "Scenarios 1v1 PvP tournament", "This tournament took place on na2.scenarios.gg", type);


    public Tournament(String name, String tournamentNum, String description) {
        this.name = name;
        this.url = name + "'s #" + tournamentNum;
        this.tournamentNum = tournamentNum;
        this.description = description;
    }

    public Tournament() {
        tournamentState = TournamentState.LOBBY;
        matchQueue = new LinkedList<>();
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
//START DOUBLE ROUDNS at -1
    private void startMatches() throws ExecutionException, InterruptedException, Exception {
        if (matchQueue.isEmpty()){
            end();
            return;
        }
        Utils.broadcast(joust.getPREFIX() + "&c&lRound &a" + (round-1)+ " &c&l has started!");
        for (Arena arenas : Arena.arenasList) {
            if (arenas.isAvailable()) {
                try {
                    startMatch(matchQueue.remove());
                } catch (NullPointerException e) {
                    System.out.println("Cannot start match - Too many arenas available or Not enough available players");

                } catch (NoSuchElementException e) {
                    for (Arena arena : Arena.arenasList) {
                        if (!arena.isAvailable()) {
                            throw new IllegalStateException("There is still a match going on");
                        }
                    }
                    System.out.println("Could");
//                    startNextRound();
                }
            }
        }
    }


    public void start() throws ExecutionException, InterruptedException {

        if (!membersAdded) return;

        tournamentState = TournamentState.STARTED;
        challonge.start().get();
        challonge.indexMatches().get();
        List<Integer> matches = new ArrayList<>(challonge.getMatchIds().keySet());

        matchQueue.addAll(matches);

        Inventory rules = tournamentRules();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.openInventory(rules);
        }

        Bukkit.getScheduler().runTaskLater(joust, () -> {
            try {
                start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 20 * 10);

    }




    public void randomize() throws ExecutionException, InterruptedException {
        membersAdded = true;
        challonge.addParticpants().get();
        challonge.randomize().get();
    }

    public void update(int matchId, String name) throws ExecutionException, InterruptedException {
        challonge.updateMatch(matchId, name).get();
    }

    public void end() throws ExecutionException, InterruptedException {
        tournamentState = TournamentState.OVER;
        challonge.end().get();
        Utils.broadcast(joust.getPREFIX() + "&c&LThe tournament is now over view the bracket at");
        Utils.broadcast("&c&o" + joust.getTournament().getBracketURL());
    }

    public void startNextMatch() throws Exception {
        if (round == lastRound){
            end();
            return;
        }

        System.out.println("Starting next match");
        if (tournamentState == TournamentState.STARTED) {
            try {
                int id = matchQueue.remove();
                try {
                    Arena arenas = joust.getArenaManager().getNextAvailableArena();
                    if (arenas.isAvailable()) {
                        System.out.println("Found Available Arena");
                        startMatch(id);
                    } else {
                        System.out.println("Could not load arena trying again in 10 seconds");
                        Bukkit.getScheduler().runTaskLater(joust, () -> {
                            try {
                                System.out.println("Starting match after 10 seconds");
                                startMatch(id);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }, 20 * 10);
                    }
                } catch (Exception p) {
                    p.printStackTrace();
                }
            } catch (NoSuchElementException e) {
                System.out.println("line 187, ending game");
                end();
            }
        }
    }
    int taskId;

    public void startMatch(int matchNumber) throws Exception {
        System.out.println("Start match: " + matchNumber);
        Integer[] names = null;
        try {
            names = challonge.getMatchParticipants(matchNumber).get();
        }catch (Exception e){
            System.out.println("Trying again in 15 seconds");
            ;
            taskId = Bukkit.getScheduler().runTaskTimer(joust, () -> {
                try {
                    startMatch(matchNumber);
                    Bukkit.getScheduler().cancelTask(taskId);
                } catch (Exception ignored) {
                }
            }, 15*20L, 0).getTaskId();
            throw new NullPointerException("");
        }

        assert names != null;
        new TournamentMatch(matchNumber, challonge.getNameFromId(names[0]), challonge.getNameFromId(names[1]));
    }


    private void forfeit(Player winner, String loser) throws ExecutionException, InterruptedException {
        System.out.println(205);

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!(Joust.mods.contains(player.getUniqueId()))) {
                winner.showPlayer(player);
            }
        }
        TournamentPlayer player = TournamentPlayer.getTournamentPlayer(winner);
        TournamentPlayer losers = TournamentPlayer.getTournamentPlayer(loser);

        Arena a = player.getMatch().getArena();
        a.setMatch(null);
        a.setAvailable(true);
        a.clear();
        player.setMatch(null);
        losers.setMatch(null);
        TournamentPlayer.getTournamentPlayer(winner).setState(PlayerState.LOBBY);
        TournamentPlayer.getTournamentPlayer(loser).setState(PlayerState.LOBBY);


        player.getPlayer().teleport(Bukkit.getWorld("world").getSpawnLocation());

        joust.getTournament().getChallonge().updateMatch(player.getMatchId(), winner.getName());
        player.getPlayer().sendMessage(ChatColor.RED + "Your opponent is not online so you have won");
        Bukkit.getScheduler().runTaskLater(joust, () -> {
            try {
                startNextMatch();
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


//    public void startNextRound() throws ExecutionException, InterruptedException, Exception {
//        List<Integer> matches = new ArrayList<>(challonge.getMatchIds().keySet());
//        if (matchQueue.isEmpty()) {
//            for (Arena arena : Arena.arenasList) {
//                if (!arena.isAvailable()) {
//                    throw new IllegalStateException("There is still a match going on");
//                }
//            }
//            try {
//                matchQueue.addAll(matches);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            System.out.println("starting new matches round: " + round);
//            round++;
//            startMatches();
//        } else {
//            throw new Exception("Cannot start next round as there are still matches in this round");
//        }
//    }

    private Inventory tournamentRules() {
        Inventory rules = Bukkit.createInventory(null, 27, ChatColor.GREEN + "Rules");
        List<String> rulesLore = new ArrayList<>();

        rulesLore.add(ChatColor.RED + " " + ChatColor.STRIKETHROUGH + "-------------------------------------------------------------");
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

        rulesLore.add(ChatColor.RED + " " + ChatColor.STRIKETHROUGH + "-------------------------------------------------------------");

        ItemStack spacer = new ItemCreator(Material.STAINED_GLASS_PANE).setName(" ").get();


        ItemStack rule = new ItemCreator(Material.PAPER).setLore(rulesLore).setName(ChatColor.RED + "Rules").get();
        rules.setItem(13, rule);

        for (int i = 0; i < rules.getContents().length; i++) {
            if (rules.getContents()[i] == null) {
                rules.setItem(i, spacer);
            }
        }

        return rules;
    }
}