package kobting.friendlyminions.patches;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.DebuffParticleEffect;
import com.megacrit.cardcrawl.vfx.ShieldParticleEffect;
import com.megacrit.cardcrawl.vfx.combat.BuffParticleEffect;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import kobting.friendlyminions.cards.MonsterCard;
import kobting.friendlyminions.characters.AbstractPlayerWithMinions;
import kobting.friendlyminions.enums.MonsterIntentEnum;
import kobting.friendlyminions.helpers.BasePlayerMinionHelper;
import kobting.friendlyminions.helpers.MonsterHelper;
import kobting.friendlyminions.monsters.AbstractFriendlyMonster;

import javax.swing.text.AbstractDocument;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;


public class MonsterIntentPatch {

    @SpirePatch(
            cls = "com.megacrit.cardcrawl.monsters.AbstractMonster",
            method = "createIntent"
    )
    public static class CreateIntentPatch{

        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void Insert(AbstractMonster __instance) {
            AbstractMonster.Intent _intent = __instance.intent;
            if(((AbstractPlayerWithMinions)AbstractDungeon.player).hasMinions() || BasePlayerMinionHelper.hasMinions(AbstractDungeon.player)){
                if((_intent == MonsterIntentEnum.ATTACK_MINION
                        || _intent == MonsterIntentEnum.ATTACK_MINION_BUFF
                        || _intent == MonsterIntentEnum.ATTACK_MINION_DEBUFF
                        || _intent == MonsterIntentEnum.ATTACK_MINION_DEFEND)
                        && MonsterHelper.getTarget(__instance) == null) {
                    if(AbstractDungeon.player instanceof AbstractPlayerWithMinions) {
                        AbstractPlayerWithMinions player = (AbstractPlayerWithMinions) AbstractDungeon.player;
                        AbstractFriendlyMonster target = (AbstractFriendlyMonster) player.minions.getRandomMonster();
                        MonsterHelper.setTarget(__instance, target);
                    } else {
                        AbstractFriendlyMonster target = (AbstractFriendlyMonster) BasePlayerMinionHelper.getMinions(AbstractDungeon.player).getRandomMonster();
                        MonsterHelper.setTarget(__instance, target);
                    }
                }
            }
        }

        public static void Prefix(AbstractMonster __instance) {
            MonsterHelper.setTarget(__instance, null);
        }

        public static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {

                Matcher finalMatcher = new Matcher.MethodCallMatcher("com.megacrit.cardcrawl.monsters.AbstractMonster", "updateIntentTip");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);

            }
        }
    }



    @SpirePatch(
            cls = "com.megacrit.cardcrawl.monsters.AbstractMonster",
            method = "getIntentImg"
    )
    public static class GetIntentImagePatch {

        public static SpireReturn<Texture> Prefix(AbstractMonster __instance){

            AbstractMonster.Intent intent = __instance.intent;

            if(intent == MonsterIntentEnum.ATTACK_MINION
                    || intent == MonsterIntentEnum.ATTACK_MINION_BUFF
                    || intent == MonsterIntentEnum.ATTACK_MINION_DEBUFF
                    || intent == MonsterIntentEnum.ATTACK_MINION_DEFEND) {
                return SpireReturn.Return(getAttackIntent(__instance));
            }

            return SpireReturn.Continue();
        }

        private static Texture getAttackIntent(AbstractMonster monster) {

            try {
                Field isMultiDmg = AbstractMonster.class.getDeclaredField("isMultiDmg");
                Field intentDmg = AbstractMonster.class.getDeclaredField("intentDmg");
                Field intentMultiAmt = AbstractMonster.class.getDeclaredField("intentMultiAmt");
                isMultiDmg.setAccessible(true);
                intentDmg.setAccessible(true);
                intentMultiAmt.setAccessible(true);

                int tmp;
                if (isMultiDmg.getBoolean(monster)) {
                    tmp = intentDmg.getInt(monster) * intentMultiAmt.getInt(monster);
                } else {
                    tmp = intentDmg.getInt(monster);
                }

                if (tmp < 5) {
                    return new Texture("images/intents/attack_monster_intent_1.png");
                } else if (tmp < 10) {
                    return new Texture("images/intents/attack_monster_intent_2.png");
                } else if (tmp < 15) {
                    return new Texture("images/intents/attack_monster_intent_3.png");
                } else if (tmp < 20) {
                    return new Texture("images/intents/attack_monster_intent_4.png");
                } else if (tmp < 25) {
                    return new Texture("images/intents/attack_monster_intent_5.png");
                } else {
                    return tmp < 30 ? new Texture("images/intents/attack_monster_intent_6.png") : new Texture("images/intents/attack_monster_intent_7.png");
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            return null;

        }

    }

    @SpirePatch(
            cls = "com.megacrit.cardcrawl.monsters.AbstractMonster",
            method = "updateIntentVFX"
    )
    public static class UpdateIntentVFXPatch {

        public static SpireReturn Prefix(AbstractMonster __instance) {

            try {
                Field intentParticleTimer = AbstractMonster.class.getDeclaredField("intentParticleTimer");
                intentParticleTimer.setAccessible(true);

                Field intentVfx = AbstractMonster.class.getDeclaredField("intentVfx");
                intentVfx.setAccessible(true);

                if(__instance.intentAlpha > 0.0f) {
                    if(__instance.intent == MonsterIntentEnum.ATTACK_MINION_DEFEND){
                        intentParticleTimer.setFloat(__instance, intentParticleTimer.getFloat(__instance) - Gdx.graphics.getDeltaTime());
                        float valIntentParticleTime = intentParticleTimer.getFloat(__instance);
                        if (valIntentParticleTime < 0.0F) {
                            intentParticleTimer.setFloat(__instance, 0.5f);
                            ((ArrayList<AbstractGameEffect>)intentVfx.get(__instance)).add(new ShieldParticleEffect(__instance.intentHb.cX, __instance.intentHb.cY));
                            return SpireReturn.Return(null);
                        }
                    }
                    else if(__instance.intent == MonsterIntentEnum.ATTACK_MINION_BUFF) {
                        intentParticleTimer.setFloat(__instance, intentParticleTimer.getFloat(__instance) - Gdx.graphics.getDeltaTime());
                        float valIntentParticleTime = intentParticleTimer.getFloat(__instance);
                        if(valIntentParticleTime < 0.0F) {
                            intentParticleTimer.setFloat(__instance, 0.1f);
                            ((ArrayList<AbstractGameEffect>)intentVfx.get(__instance)).add(new BuffParticleEffect(__instance.intentHb.cX, __instance.intentHb.cY));
                            return SpireReturn.Return(null);
                        }
                    }
                    else if(__instance.intent == MonsterIntentEnum.ATTACK_MINION_DEBUFF) {
                        intentParticleTimer.setFloat(__instance, intentParticleTimer.getFloat(__instance) - Gdx.graphics.getDeltaTime());
                        float valIntentParticleTime = intentParticleTimer.getFloat(__instance);
                        if(valIntentParticleTime < 0.0F) {
                            intentParticleTimer.setFloat(__instance, 1.0f);
                            ((ArrayList<AbstractGameEffect>)intentVfx.get(__instance)).add(new DebuffParticleEffect(__instance.intentHb.cX, __instance.intentHb.cY));
                            return SpireReturn.Return(null);
                        }
                    }
                }

                return SpireReturn.Continue();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            return SpireReturn.Continue();
        }

    }

    @SpirePatch(
            cls = "com.megacrit.cardcrawl.monsters.AbstractMonster",
            method = "updateIntentTip"
    )
    public static class UpdateIntentTipPatch {


        public static SpireReturn Prefix(AbstractMonster __instance) {

            try {

                PowerTip intentTip;
                boolean isMultiDamage;
                int intentDmg;
                int intentMultiAmt;

                Field f_intentTip = AbstractMonster.class.getDeclaredField("intentTip");
                f_intentTip.setAccessible(true);
                Field f_isMultiDamage = AbstractMonster.class.getDeclaredField("isMultiDmg");
                f_isMultiDamage.setAccessible(true);
                Field f_intentDmg = AbstractMonster.class.getDeclaredField("intentDmg");
                f_intentDmg.setAccessible(true);
                Field f_intentMultiAmt = AbstractMonster.class.getDeclaredField("intentMultiAmt");
                f_intentMultiAmt.setAccessible(true);


                intentTip = (PowerTip) f_intentTip.get(__instance);
                isMultiDamage = f_isMultiDamage.getBoolean(__instance);
                intentDmg = f_intentDmg.getInt(__instance);
                intentMultiAmt = f_intentMultiAmt.getInt(__instance);

                AbstractMonster.Intent intent = __instance.intent;
                AbstractPlayerWithMinions player = (AbstractPlayerWithMinions) AbstractDungeon.player;
                MonsterGroup playerMinions = player.minions;
                MonsterGroup playerMinion2 = BasePlayerMinionHelper.getMinions(AbstractDungeon.player);
                AbstractFriendlyMonster target = MonsterHelper.getTarget(__instance);

                if(MonsterHelper.getTarget(__instance) != null) {

                    if (intent == MonsterIntentEnum.ATTACK_MINION) {
                        String targetName = MonsterHelper.getTarget(__instance).name;
                        intentTip.header = "Aggressive";
                        if(isMultiDamage) {
                            intentTip.body = "This enemy intends to NL #yAttack a #y" + targetName + " for #b" +  intentDmg + " damage #b" + intentMultiAmt + " times.";
                        } else {
                            intentTip.body = "This enemy intends to NL #yAttack a #y" + targetName + " for #b" + intentDmg + " damage";
                        }
                        Method method = AbstractMonster.class.getDeclaredMethod("getAttackIntentTip");
                        method.setAccessible(true);
                        intentTip.img = (Texture) method.invoke(__instance);
                    }
                    else if (intent == MonsterIntentEnum.ATTACK_MINION_BUFF) {
                        String targetName = MonsterHelper.getTarget(__instance).name;
                        intentTip.header = "Aggressive";

                        if(isMultiDamage) {
                            intentTip.body = "This enemy intends to use a #yBuff and #yAttack a #y " + targetName + " for #b" + intentDmg + " damage #b" + intentMultiAmt + " times.";
                        } else {
                            intentTip.body = "This enemy intends to use a #yBuff and #yAttack a #y" + targetName + " for #b" + intentDmg + " damage.";
                        }

                        intentTip.img = ImageMaster.INTENT_ATTACK_BUFF;

                    }
                    else if (intent == MonsterIntentEnum.ATTACK_MINION_DEBUFF) {
                        String targetName = MonsterHelper.getTarget(__instance).name;
                        intentTip.header = "Strategic";
                        intentTip.body = "This enemy intends to inflict a #yNegative #yEffect on you and #yAttack a #y" + targetName + " for #b" + intentDmg + " damage.";
                        intentTip.img = ImageMaster.INTENT_ATTACK_DEBUFF;

                    }
                    else if (intent == MonsterIntentEnum.ATTACK_MINION_DEFEND) {
                        String targetName = MonsterHelper.getTarget(__instance).name;
                        intentTip.header = "Aggressive";

                        if(isMultiDamage) {
                            intentTip.body = "This enemy intends to #yBlock and #yAttack a #y" + targetName + " for #b" + intentDmg + " damage #b" + intentMultiAmt + " times.";
                        } else {
                            intentTip.body = "This enemy intends to #yBlock and #yAttack a #y" + targetName + " for #b" + intentDmg + " damage.";
                        }
                        intentTip.img = ImageMaster.INTENT_ATTACK_DEFEND;

                    } else {
                        return SpireReturn.Continue();
                    }
                } else if(
                        (MonsterHelper.getTarget(__instance) == null
                         || !playerMinions.monsters.contains(target)
                         || !playerMinion2.monsters.contains(target))
                        &&
                        (intent == MonsterIntentEnum.ATTACK_MINION
                        || intent == MonsterIntentEnum.ATTACK_MINION_DEFEND
                        || intent == MonsterIntentEnum.ATTACK_MINION_DEBUFF
                        || intent == MonsterIntentEnum.ATTACK_MINION_BUFF
                        )){
                    MonsterHelper.switchTarget(__instance, null);
                    return SpireReturn.Return(null);
                } else {
                    return SpireReturn.Continue();
                }


                f_intentTip.set(__instance, intentTip);
                return SpireReturn.Return(null);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

            return SpireReturn.Continue();
        }
    }

    @SpirePatch(
            cls = "com.megacrit.cardcrawl.monsters.AbstractMonster",
            method = "calculateDamage",
            paramtypes = {"int"}
    )
    public static class CalculateDamagePatch{

        public static SpireReturn Prefix(AbstractMonster __instance, int dmg) {

            try {
                Field f_intentDmg = AbstractMonster.class.getDeclaredField("intentDmg");
                f_intentDmg.setAccessible(true);

                AbstractMonster.Intent intent = __instance.intent;

                if(intent == MonsterIntentEnum.ATTACK_MINION
                        || intent == MonsterIntentEnum.ATTACK_MINION_BUFF
                        || intent == MonsterIntentEnum.ATTACK_MINION_DEBUFF
                        || intent == MonsterIntentEnum.ATTACK_MINION_DEFEND) {

                    AbstractFriendlyMonster target = MonsterHelper.getTarget(__instance);

                    if(target == null) {
                        return SpireReturn.Continue();
                    } else {
                        float tmp = dmg;
                        for(final AbstractPower p : __instance.powers) {
                            tmp = p.atDamageGive(tmp, DamageInfo.DamageType.NORMAL);
                        }
                        for(final AbstractPower p: target.powers) {
                            tmp = p.atDamageReceive(tmp, DamageInfo.DamageType.NORMAL);
                        }
                        for(final AbstractPower p: __instance.powers) {
                            tmp = p.atDamageFinalGive(tmp, DamageInfo.DamageType.NORMAL);
                        }
                        for(final AbstractPower p: target.powers) {
                            tmp = p.atDamageFinalReceive(tmp, DamageInfo.DamageType.NORMAL);
                        }
                        dmg = MathUtils.floor(tmp);
                        if(dmg < 0) dmg = 0;
                        f_intentDmg.set(__instance, dmg);
                    }
                    return SpireReturn.Return(null);
                } else {
                    return SpireReturn.Continue();
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
                return SpireReturn.Continue();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return SpireReturn.Continue();
            }
        }

    }

    @SpirePatch(
            cls = "com.megacrit.cardcrawl.monsters.AbstractMonster",
            method = "applyPowers"
    )
    public static class ApplyPowersPatch{

        public static SpireReturn Prefix(AbstractMonster __instance) {

            try {
                Field f_move = AbstractMonster.class.getDeclaredField("move");
                f_move.setAccessible(true);
                Field f_intentImg = AbstractMonster.class.getDeclaredField("intentImg");
                f_intentImg.setAccessible(true);
                Method m_calcDmg = AbstractMonster.class.getDeclaredMethod("calculateDamage", int.class);
                m_calcDmg.setAccessible(true);
                Method m_getIntentImg = AbstractMonster.class.getDeclaredMethod("getIntentImg");
                m_getIntentImg.setAccessible(true);
                Method m_updateIntentTip = AbstractMonster.class.getDeclaredMethod("updateIntentTip");
                m_updateIntentTip.setAccessible(true);

                EnemyMoveInfo move = (EnemyMoveInfo) f_move.get(__instance);

                AbstractFriendlyMonster target = MonsterHelper.getTarget(__instance);

                if(target != null) {
                    for(final DamageInfo  dmg: __instance.damage) {
                        dmg.applyPowers(__instance, target);
                    }
                    if(move.baseDamage > -1) {
                        m_calcDmg.invoke(__instance, move.baseDamage);
                    }

                    f_intentImg.set(__instance, m_getIntentImg.invoke(__instance));
                    m_updateIntentTip.invoke(__instance);
                    return SpireReturn.Return(null);
                } else {
                    return SpireReturn.Continue();
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

            return SpireReturn.Continue();
        }

    }

}
