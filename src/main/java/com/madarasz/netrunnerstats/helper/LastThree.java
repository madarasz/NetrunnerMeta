package com.madarasz.netrunnerstats.helper;

import com.madarasz.netrunnerstats.database.DRs.stats.CardPoolStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by madarasz on 2/9/16.
 * Aggregating statistics for the last three cardpool
 */
@Component
public class LastThree {

    @Autowired
    CardPoolStatsRepository cardPoolStatsRepository;

    public List<String> getLastThreeCardpoolNames() {
        List<String> result = cardPoolStatsRepository.getCardPoolNames();
        if (result.size() > 2) {
            return result.subList(0, 3);
        } else {
            return result;
        }
    }

    public boolean isInLastThree(String pack) {
        return getLastThreeCardpoolNames().contains(pack);
    }

}
