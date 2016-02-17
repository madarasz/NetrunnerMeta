package com.madarasz.netrunnerstats.helper;

import com.madarasz.netrunnerstats.Statistics;
import com.madarasz.netrunnerstats.database.DOs.stats.DPStatistics;
import com.madarasz.netrunnerstats.database.DOs.stats.entries.CountDeckStands;
import com.madarasz.netrunnerstats.database.DRs.StandingRepository;
import com.madarasz.netrunnerstats.database.DRs.stats.CardPoolStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by madarasz on 2/9/16.
 * Aggregating statistics for the last three cardpool
 */
@Component
public class LastThree {

    @Autowired
    CardPoolStatsRepository cardPoolStatsRepository;

    @Autowired
    StandingRepository standingRepository;

    @Autowired
    Statistics statistics;

    public DPStatistics getPackStat(boolean top) {
        List<DPStatistics> dpStatisticses = new ArrayList<>();
        int decknum = 0;
        int standnum = 0;
        int runnerstatnum = 0;
        int corpstatnum = 0;
        Set<CountDeckStands> runnerFactions = new HashSet<>();
        Set<CountDeckStands> runnerIdentities = new HashSet<>();
        Set<CountDeckStands> corpFactions = new HashSet<>();
        Set<CountDeckStands> corpIdentities = new HashSet<>();

        for (String cardpoolName : getLastThreeCardpoolNames()) {
            DPStatistics dpStatistics = statistics.getPackStats(cardpoolName, top);
            decknum += dpStatistics.getDecknum();
            standnum += dpStatistics.getStatnum();
            runnerstatnum += dpStatistics.getRunnerStatnum();
            corpstatnum += dpStatistics.getCorpStatnum();
            runnerFactions = mergeCDS(runnerFactions, dpStatistics.getRunnerFactions());
            runnerIdentities = mergeCDS(runnerIdentities, dpStatistics.getRunnerIdentities());
            corpFactions = mergeCDS(corpFactions, dpStatistics.getCorpFactions());
            corpIdentities = mergeCDS(corpIdentities, dpStatistics.getCorpIdentities());
        }

        DPStatistics result = new DPStatistics(statistics.LAST_3, decknum, standnum, runnerstatnum, corpstatnum, top);
        result.setCorpFactions(recalculatePerc(corpFactions, corpstatnum));
        result.setCorpIdentities(recalculatePerc(corpIdentities, corpstatnum));
        result.setRunnerFactions(recalculatePerc(runnerFactions, runnerstatnum));
        result.setRunnerIdentities(recalculatePerc(runnerIdentities, runnerstatnum));
        return result;
    }

    private Set<CountDeckStands> mergeCDS(Set<CountDeckStands> base, Set<CountDeckStands> toAdd) {
        if (base.size() == 0) {
            return toAdd;
        } else {
            Set<CountDeckStands> result = new HashSet<>(base);
            for (CountDeckStands countDeckStands : toAdd) {
                result = addCDS(result, countDeckStands);
            }
            return result;
        }
    }

    private Set<CountDeckStands> addCDS(Set<CountDeckStands> base, CountDeckStands toAdd) {
        Set<CountDeckStands> result = new HashSet<>();
        boolean existed = false;
        for (CountDeckStands countDeckStands : base) {
            if (toAdd.getTitle().equals(countDeckStands.getTitle())) {
                existed = true;
                result.add(new CountDeckStands(toAdd.getTitle(),
                        toAdd.getDecknum() + countDeckStands.getDecknum(),
                        toAdd.getStandingnum() + countDeckStands.getStandingnum(), -1, toAdd.getColorcode()));
            } else {
                result.add(countDeckStands);
            }
        }
        if (!existed) {
            result.add(toAdd);
        }
        return result;
    }

    private Set<CountDeckStands> recalculatePerc(Set<CountDeckStands> input, int standings) {
        Set<CountDeckStands> result = new HashSet<>();
        for (CountDeckStands countDeckStands : input) {
            result.add(new CountDeckStands(countDeckStands.getTitle(), countDeckStands.getDecknum(),
                    countDeckStands.getStandingnum(), (double)countDeckStands.getStandingnum() / standings,
                    countDeckStands.getColorcode()));
        }
        return result;
    }

    public List<String> getLastThreeCardpoolNames() {
        List<String> result = cardPoolStatsRepository.getCardPoolNames();
        if (result.size() > 2) {
            return result.subList(0, 3);
        } else {
            return result;
        }
    }

}
