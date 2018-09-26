package com.madarasz.netrunnerstats.helper;

import com.madarasz.netrunnerstats.database.DOs.CardCycle;
import org.springframework.stereotype.Component;

/**
 * Created by madarasz on 11/7/15.
 * Enums.
 */
@Component
public class Enums {

    public enum Factions {
        neutral, shaper, criminal, anarch, jinteki, haas_bioroid, weyland_consortium, nbn;
    }

    public enum CardCycles {
        Promos(0), Core_Set(1), Genesis(2), Creation_and_Control(3), Spin(4), Honor_and_Profit(5), Lunar(6),
        Order_and_Chaos(7), SanSan(8), Data_and_Destiny(9), Mumbad(10), Flashpoint(11), Red_Sand(12),
        Terminal_Directive(13), Red_Sand_Part_2(14), Revised_Core_Set(20), Kitara(21), Reign_and_Reverie(22),
        Magnum_Opus(23);

        private final int cycleNumber;

        CardCycles(int cycleNumber) {
            this.cycleNumber = cycleNumber;
        }

        public int getCycleNumber() {
            return this.cycleNumber;
        }

        public static String getName(int code) {
            for (CardCycles cycle : CardCycles.values()) {
                if (code == cycle.getCycleNumber()) {
                    return cycle.toString();
                }
            }
            return "";
        }
    }

    public enum MwlType {
        global_penalty, universal_faction_cost, is_restricted, deck_limit
    }
}
