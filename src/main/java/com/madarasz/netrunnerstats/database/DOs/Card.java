package com.madarasz.netrunnerstats.database.DOs;

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
    private int decklimit;
    @RelatedTo(type = "IN_SET") private @Fetch
    @Indexed(unique=false) CardPack cardPack;

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

    public Card(String code, String title, String type_code, String subtype_code, String text, String faction_code, String side_code, boolean uniqueness, int decklimit,
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
        this.decklimit = decklimit;
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
        // fixing neutral-corp, neutral-runner
        if (this.faction_code.contains("neutral")) {
            this.faction_code = "neutral";
        }
    }

    public boolean isUniquene() {
        return uniqueness;
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

    public void setCardPack(CardPack cardPack) {
        this.cardPack = cardPack;
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

    public boolean isRunner() {
        return side_code.equals("runner");
    }

    public int getCost() {
        return cost;
    }

    public int getStrength() {
        return strength;
    }

    public int getTrash() {
        return trash;
    }

    public String getText() {
        return text;
    }

    public int getBaselink() {
        return baselink;
    }

    public int getMemoryunits() {
        return memoryunits;
    }

    public int getAdvancementcost() {
        return advancementcost;
    }

    public int getDecklimit() {
        return decklimit;
    }

    public void resetValues(Card card) {
        this.code = card.getCode();
        this.title = card.getTitle();
        this.type_code = card.getType_code();
        this.subtype_code = card.getSubtype_code();
        this.text = card.getText();
        this.faction_code = card.getFaction_code();
        this.side_code = card.getSide_code();
        this.uniqueness = card.isUniquene();
        this.decklimit = card.getDecklimit();
        this.cardPack = card.getCardPack();
        this.baselink = card.getBaselink();
        this.influencelimit = card.getInfluencelimit();
        this.minimumdecksize = card.getMinimumdecksize();
        this.cost = card.getCost();
        this.factioncost = card.getFactioncost();
        this.memoryunits = card.getMemoryunits();
        this.strength = card.getStrength();
        this.advancementcost = card.getAdvancementcost();
        this.agendapoints = card.getAgendapoints();
        this.trash = card.getTrash();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Compares all values with other card.
     * @param card
     * @return
     */
    public boolean isSame(Card card) {
        return (this.title.equals(card.getTitle()) && this.type_code.equals(card.getType_code()) &&
            this.subtype_code.equals(card.getSubtype_code()) && this.text.equals(card.getText()) &&
            this.faction_code.equals(card.getFaction_code()) && this.side_code.equals(card.getSide_code()) &&
            this.uniqueness == card.isUniquene() && this.decklimit == card.getDecklimit() &&
            this.baselink == card.getBaselink() &&
            this.influencelimit == card.getInfluencelimit() && this.minimumdecksize == card.getMinimumdecksize() &&
            this.cost == card.getCost() && this.factioncost == card.getFactioncost() &&
            this.memoryunits == card.getMemoryunits() && this.strength == card.getStrength() &&
            this.advancementcost == card.getAdvancementcost() && this.agendapoints == card.getAgendapoints() &&
            this.trash == card.getTrash() && this.getCode().equals(card.getCode()));
    }

    /**
     * get the image src for the card
     * @return URL
     */
    public String getImageSrc() {
        return "/static/img/cards/netrunner-" +
                title.toLowerCase().replace(" ", "-").replaceAll("[^a-z0-9.-]", "") + ".png";
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
