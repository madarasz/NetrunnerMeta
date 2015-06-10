package com.madarasz.netrunnerstats.DRs;

import com.madarasz.netrunnerstats.DOs.CardPack;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by madarasz on 2015-06-08.
 */
public interface CardPackRepository extends CrudRepository<CardPack, String> {

    CardPack findByName(String name);

    CardPack findByCode(String code);
}
