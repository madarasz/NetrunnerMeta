package com.madarasz.netrunnerstats.DRs;

import com.madarasz.netrunnerstats.DOs.CardSet;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by istvan on 2015-06-08.
 */
public interface CardSetRepository extends CrudRepository<CardSet, String> {

    CardSet findByName(String name);

    CardSet findByCode(String code);
}
