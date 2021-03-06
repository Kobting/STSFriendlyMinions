package kobting.friendlyminions.helpers;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import kobting.friendlyminions.monsters.AbstractFriendlyMonster;
import kobting.friendlyminions.patches.PlayerAddFieldsPatch;

import java.util.Objects;

public class BasePlayerMinionHelper {

    public static MonsterGroup getMinions(AbstractPlayer player) {
        return PlayerAddFieldsPatch.f_minions.get(player);
    }

    public static void changeMaxMinionAmount(AbstractPlayer player, int newMax) {
        PlayerAddFieldsPatch.f_maxMinions.set(player, newMax);
    }

    public static boolean addMinion(AbstractPlayer player, AbstractFriendlyMonster minionToAdd) {
        MonsterGroup minions = PlayerAddFieldsPatch.f_minions.get(player);
        int maxMinions = PlayerAddFieldsPatch.f_maxMinions.get(player);

        if(minions.monsters.size() == maxMinions){
            return false;
        } else {
            minionToAdd.init();
            minionToAdd.usePreBattleAction();
            //minionToAdd.useUniversalPreBattleAction();
            minionToAdd.showHealthBar();
            minions.add(minionToAdd);
            return true;
        }
    }

    public static boolean removeMinion(AbstractPlayer player, AbstractFriendlyMonster minionToRemove) {
        return PlayerAddFieldsPatch.f_minions.get(player).monsters.remove(minionToRemove);
    }


    public static boolean hasMinions(AbstractPlayer player) {
        return PlayerAddFieldsPatch.f_minions.get(player).monsters.size() > 0;
    }

    public static int getMaxMinions(AbstractPlayer player){
        return PlayerAddFieldsPatch.f_maxMinions.get(player);
    }

    public static void clearMinions(AbstractPlayer player){
        MonsterGroup minions;
        AbstractFriendlyMonster[] p_minions;
        p_minions = new AbstractFriendlyMonster[PlayerAddFieldsPatch.f_maxMinions.get(player)];
        minions = new MonsterGroup(p_minions);
        minions.monsters.removeIf(Objects::isNull);
        PlayerAddFieldsPatch.f_minions.set(player,minions);
    }


}
