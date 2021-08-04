package me.bristermitten.warzone;

import com.google.inject.Guice;
import io.vavr.collection.List;
import io.vavr.concurrent.Future;
import me.bristermitten.warzone.aspect.Aspect;
import me.bristermitten.warzone.chat.ChatAspect;
import me.bristermitten.warzone.chat.ChatConfig;
import me.bristermitten.warzone.commands.CommandsAspect;
import me.bristermitten.warzone.config.ConfigurationAspect;
import me.bristermitten.warzone.database.DatabaseAspect;
import me.bristermitten.warzone.database.DatabaseConfig;
import me.bristermitten.warzone.database.Persistence;
import me.bristermitten.warzone.database.StorageException;
import me.bristermitten.warzone.file.FileWatcherAspect;
import me.bristermitten.warzone.lang.LangConfig;
import me.bristermitten.warzone.leaderboard.LeaderboardAspect;
import me.bristermitten.warzone.leaderboard.PlayerLeaderboard;
import me.bristermitten.warzone.leaderboard.menu.LeaderboardMenu;
import me.bristermitten.warzone.papi.PAPIAspect;
import me.bristermitten.warzone.papi.WarzoneExpansion;
import me.bristermitten.warzone.player.PlayerAspect;
import me.bristermitten.warzone.player.storage.PlayerPersistence;
import me.bristermitten.warzone.player.storage.PlayerStorage;
import me.bristermitten.warzone.player.xp.XPConfig;
import me.bristermitten.warzone.scoreboard.ScoreboardAspect;
import me.bristermitten.warzone.scoreboard.ScoreboardConfig;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

public class Warzone extends JavaPlugin {

    private List<Persistence> persistences = List.of();

    @Override
    public void onEnable() {
        try {
            var configAspect = new ConfigurationAspect(Set.of(
                    ScoreboardConfig.CONFIG
                    , DatabaseConfig.CONFIG
                    , XPConfig.CONFIG
                    , ChatConfig.CONFIG
                    , LangConfig.CONFIG
                    , LeaderboardMenu.CONFIG
            ));

            var aspects = List.of(
                    configAspect,
                    new ScoreboardAspect()
                    , new PluginAspect(this)
                    , new FileWatcherAspect()
                    , new DatabaseAspect()
                    , new PlayerAspect()
                    , new ChatAspect()
                    , new CommandsAspect()
                    , new PAPIAspect()
                    , new LeaderboardAspect()
            );

            var modules = aspects.map(Aspect::generateModule);

            var injector = Guice.createInjector(modules);

            aspects.forEach(it -> it.finalizeInjections(injector));

            persistences = List.of(
                    injector.getInstance(PlayerPersistence.class)
            );

            Future.sequence(persistences
                    .map(Persistence::initialise))
                    .onFailure(t -> {
                        throw new StorageException("Could not load data", t);
                    })
                    .onSuccess(v -> {
                        getSLF4JLogger().info("Successfully loaded all data!");

                        // TODO remove
                        PlayerStorage instance = injector.getInstance(PlayerStorage.class);
                        var names = "klayman55 geoule Heyokha esmerosabella slackenstein johnlee87 SurealOne fuman117 red_moon_vixen coinmac armyofzin missing_nin89 b89kev Shichimenchou Zorjeff mdow31 Derendzeit TalarisShuran lvmom4 17jschaelling pranadude shawnkhall signalmax WraithTown Agthangelos sloams kitlaan SynapticNoise draimus riona1999 abcdeath22 retrohardware whiteeaglensc Smituga BernieBomb danguy226 Bittersweetjj badjinga Breteck DaSandbox linkthetoaoftime Goldfishn Flobeee beth1929 Chibi_Meister winterNebs pizzapierules99 jojogelato DrNachoPantsss ManOnTheHill judder24 Chill_Bofana UKguy113 hellow360 atleeh dchaley RandomlyBlue crackerz246 17zwilliams Knight_Slayer3 ztmorgan SeniorDracu1a sushifishpirate Mhoram73 MeiSooHaityu Damiencowman 0fficem0nkey innate_ideas SaitoYui thing36 ykamf TGcrazylightning Chaotichymn rsrobn spook_boy rayleen32 AddiCat19 markmandue wolfordriver 15nschaelling pl_michaels mbielski Taco_Warlord cardboardmark rogemaster24 Hinthial AZMindbender makertron 56Minfei78 asgarddad64 killsamore RivenAco MobileJae Avelicity creeperpainter Threnodi HunsValen josephm102 Zaglotus228 _segfault imakalkami robumewomu Emmaesme QuinnIsLose SomeCuteLlama TheDawi93 EddNation NeroWeNeed lindolai RealMasterLuke giantsupergirl Coinkid10 Aberdeen32 Brandenman13 Georr Archangeline LiefLieferson kgallspark joransaywhat xzGmanzx mindpalace221 bloxtonium Mrcytrus GoldLibra viciogamer_xd Cunicularia slipperyp MongooseDoom Ken_Raves smallrobin14 xlson BasedChe huhna Hakashino lynldalen Dantheawesome liquiql DictatorReptile Foxcubed thehawkeye131 awesomelozinc The_Mayzer WesWilson _Dutchm4n_ crayolaman_ itechify BumboBingo Spinoravenger the_pedobear69 zombud clarapakman Templar_Anthony KingOfOrder Mysticalmario gelicia Billybob1591 onlyhearsay lxpower cndc24 Madudu MultiOrchasm CaptXochimilco Flopert Jestservers rtendy craftress22 66Unseen66 UberKritsCupcake rchase Earth";
                        for (String name : names.split(" ")) {
                            OfflinePlayer p = Bukkit.getOfflinePlayer(name);
                            var player = instance.load(p.getUniqueId()).get();
                            player.setKills((int) (Math.random() * 50));
                            player.setDeaths((int) (Math.random() * 100));
                            injector.getInstance(PlayerLeaderboard.class).add(player);
                        }
                    });

            injector.getInstance(WarzoneExpansion.class)
                    .register();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDisable() {
        Future.sequence(persistences
                .map(Persistence::initialise))
                .get(); // This needs to block!
    }
}
