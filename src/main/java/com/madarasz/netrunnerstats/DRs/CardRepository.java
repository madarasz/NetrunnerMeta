package com.madarasz.netrunnerstats.DRs;

import com.madarasz.netrunnerstats.DOs.Card;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by madarasz on 2015-06-08.
 */
public interface CardRepository extends CrudRepository<Card, Long> {

    Card findByCode(int code);
    Card findByTitle(String title);

    Iterable<Card> findByCardSetCode(String code);
}
