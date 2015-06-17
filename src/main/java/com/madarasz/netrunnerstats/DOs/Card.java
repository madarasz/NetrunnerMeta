package com.madarasz.netrunnerstats.DOs;

import org.springframework.data.neo4j.annotation.*;

/**
 * Node for Netrunner cards
 * Created by madarasz on 2015-06-08.
 */
@NodeEntity
public class Card {
    @GraphId private Long id;
    private String code;
    @Indexed(unique=true) private String title;
    private String type_code;  // identity, event, hardware, program, resource, agenda, asset, operation, ice, upgrade
    private String subtype_code;
    private String text;
    private String faction_code; // neutral, shaper, criminal, anarch, jinteki, haas-bioroid, weyland-consortium, nbn
    private String side_code; // runner, corp
    private boolean uniqueness;
    private boolean limited;
    @RelatedTo(type = "IN_SET") private @Fetch
    CardPack cardPack;

    // for identity
    private int baselink;
    private int influencelimit;
    private int minimumdecksize;

    // for event, hardware, program, resource, agenda, asset, operation, ice, upgrade
    private int cost;
    private int factioncost;

    // for program
    private int memoryunits;

    // for ice, some programs
    private int strength;

    // for agenda
    private int advancementcost;
    private int agendapoints;

    // for asset, upgrade
    private int trash;

    public Card() {
    }

    public Card(String code, String title, String type_code, String subtype_code, String text, String faction_code, String side_code, boolean uniqueness, boolean limited,
                CardPack cardPack, int baselink, int influencelimit, int minimumdecksize, int cost, int factioncost, int memoryunits, int strength, int advancementcost,
                int agendapoints, int trash) {
        this.code = code;
        this.title = title;
        this.type_code = type_code;
        this.subtype_code = subtype_code;
        this.text = text;
        this.faction_code = faction_code;
        this.side_code = side_code;
        this.uniqueness = uniqueness;
        this.limited = limited;
        this.cardPack = cardPack;
        this.baselink = baselink;
        this.influencelimit = influencelimit;
        this.minimumdecksize = minimumdecksize;
        this.cost = cost;
        this.factioncost = factioncost;
        this.memoryunits = memoryunits;
        this.strength = strength;
        this.advancementcost = advancementcost;
        this.agendapoints = agendapoints;
        this.trash = trash;
    }

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public CardPack getCardPack() {
        return cardPack;
    }

    public String getFaction_code() {
        return faction_code;
    }

    public String getSide_code() {
        return side_code;
    }

    public String getType_code() {
        return type_code;
    }

    public boolean isLimited() {
        return limited;
    }

    public int getInfluencelimit() {
        return influencelimit;
    }

    public int getMinimumdecksize() {
        return minimumdecksize;
    }

    public int getFactioncost() {
        return factioncost;
    }

    public int getAgendapoints() {
        return agendapoints;
    }

    public String getSubtype_code() {
        return subtype_code;
    }

    public boolean isIdentity() {
        return type_code.equals("identity");
    }

    @Override
    public String toString() {
//        return String.format("%s (%s) - %s", title, code, cardPack.toString());
        return title;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Card card = (Card) obj;
        if (code == null) return super.equals(obj);
        return code.equals(card.code);
    }

    @Override
    public int hashCode() {
        return Integer.valueOf(code);
    }
}
