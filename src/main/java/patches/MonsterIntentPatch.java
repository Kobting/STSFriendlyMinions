package patches;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.DebuffParticleEffect;
import com.megacrit.cardcrawl.vfx.ShieldParticleEffect;
import com.megacrit.cardcrawl.vfx.combat.BuffParticleEffect;
import enums.MonsterIntentEnum;

import java.lang.reflect.Field;
import java.util.ArrayList;


public class MonsterIntentPatch {


    @SpirePatch(
            cls = "com.megacrit.cardcrawl.monsters.AbstractMonster",
            method = "getIntentImg"
    )
    public static class GetIntentImagePatch {

        public static SpireReturn<Texture> Prefix(AbstractMonster __instance){

            AbstractMonster.Intent intent = __instance.intent;

            if(intent == MonsterIntentEnum.ATTACK_MONSTER
                    || intent == MonsterIntentEnum.ATTACK_MONSTER_BUFF
                    || intent == MonsterIntentEnum.ATTACK_MONSTER_DEBUFF
                    || intent == MonsterIntentEnum.ATTACK_MONSTER_DEFEND) {
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
                    if(__instance.intent == MonsterIntentEnum.ATTACK_MONSTER_DEFEND){
                        intentParticleTimer.setFloat(__instance, intentParticleTimer.getFloat(__instance) - Gdx.graphics.getDeltaTime());
                        float valIntentParticleTime = intentParticleTimer.getFloat(__instance);
                        if (valIntentParticleTime < 0.0F) {
                            intentParticleTimer.setFloat(__instance, 0.5f);
                            ((ArrayList<AbstractGameEffect>)intentVfx.get(__instance)).add(new ShieldParticleEffect(__instance.intentHb.cX, __instance.intentHb.cY));
                            return SpireReturn.Return(null);
                        }
                    }
                    else if(__instance.intent == MonsterIntentEnum.ATTACK_MONSTER_BUFF) {
                        intentParticleTimer.setFloat(__instance, intentParticleTimer.getFloat(__instance) - Gdx.graphics.getDeltaTime());
                        float valIntentParticleTime = intentParticleTimer.getFloat(__instance);
                        if(valIntentParticleTime < 0.0F) {
                            ((ArrayList<AbstractGameEffect>)intentVfx.get(__instance)).add(new BuffParticleEffect(__instance.intentHb.cX, __instance.intentHb.cY));
                            return SpireReturn.Return(null);
                        }
                    }
                    else if(__instance.intent == MonsterIntentEnum.ATTACK_MONSTER_DEBUFF) {
                        intentParticleTimer.setFloat(__instance, intentParticleTimer.getFloat(__instance) - Gdx.graphics.getDeltaTime());
                        float valIntentParticleTime = intentParticleTimer.getFloat(__instance);
                        if(valIntentParticleTime < 0.0F) {
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

}
