// enable popovers
$(function () {
    $('[data-toggle="popover"]').popover()
})

// convert faction_code to color code
function factionCodeToColor(faction) {
    switch (faction) {
        case "shaper":
            return "#7EAC39";
            break;
        case "anarch":
            return "#AC5439";
            break;
        case "criminal":
            return "#3962AC";
            break;
        case "haas-bioroid":
            return "#702871";
            break;
        case "weyland-consortium":
            return "#1B654F";
            break;
        case "jinteki":
            return "#8f1E0A";
            break;
        case "nbn":
            return "#CB953A";
            break;
        case "apex":
            return "darkred";
            break;
        case "sunny-lebeau":
            return "black";
            break;
        case "adam":
            return "darkgoldenrod";
            break;
        default:
            return "grey";
    }
}

// fill card pack table
function loadCardPackTable(urlvalue, elementid) {
    var jsonData = $.ajax({
        url: urlvalue,
        dataType: "json",
        async: false,
        success: function(data) {
            $.each(data, function(index, element) {
                if (element.indecks > 0) {
                    $(elementid).append($('<tr>').append($('<td>').append($('<span>', {
                        class: 'icon-' + element.faction
                    })), $('<td>').append($('<a>', {
                        text: element.cardtitle,
                        href: '/Cards/' + element.cardtitle + '/'
                    })), $('<td>', {
                        text: element.intopdecks,
                        class: 'text-right'
                    }), $('<td>', {
                        text: '(' + Math.round(element.topdeckfraction * 100) + '%)',
                        class: 'text-left'
                    }), $('<td>', {
                        text: element.indecks,
                        class: 'text-right'
                    }), $('<td>', {
                        text: '(' + Math.round(element.deckfraction * 100) + '%)',
                        class: 'text-left'
                    })));
                } else {
                    $(elementid).append($('<tr>').append($('<td>').append($('<span>', {
                        class: 'icon-' + element.faction
                    })), $('<td>').append($('<a>', {
                        text: element.cardtitle,
                        href: '/Cards/' + element.cardtitle + '/',
                        class: 'italic'
                    })), $('<td>', {
                        class: 'text-center italic',
                        text: 'unused',
                        colspan: 4
                    })));
                }
            });
            $(elementid).removeClass('spinner');
        }
    });
}

// fill card usage table (MDS table, ICE/breakers)
function fillCardTable(urlvalue, elementid, minusing) {
    var typecode = 'none';
    $.ajax({
        url: urlvalue,
        dataType: "json",
        async: false,
        success: function(data) {
            $.each(data, function(index, element) {
                if (parseInt(element.using) > minusing) {
                    if (element.typecodes.indexOf(typecode) < 0) {
                        typecode = element.typecodes;
                        $(elementid).append($('<tr>').append($('<td>', {
                            class: 'warning',
                            colspan: 6
                        }).append($('<strong>', {
                            text: typecode
                        }))))
                    }
                    $(elementid).append($('<tr>').append($('<td>').append($('<span>', {
                        class: 'icon-' + element.faction
                    })), $('<td>').append($('<a>', {
                        text: element.cardtitle,
                        href: '/Cards/' + element.cardtitle + '/'
                    })), $('<td>').append($('<em>', {
                        text: element.cardpack
                    })), $('<td>', {
                        text: element.using,
                        class: 'text-right'
                    }), $('<td>', {
                        text: element.average,
                        class: 'text-center'
                    }), $('<td>', {
                        text: element.averageifused,
                        class: 'text-center'
                    })));
                }
            });
            $(elementid).removeClass('spinner');
        }
    });
}

// shorten identity title
function shortTitle(longtitle) {
    switch (longtitle) {
        case "Haas-Bioroid: Engineering the Future":
            return "HB: EtF";
            break;
        case "Haas-Bioroid: Stronger Together":
            return "HB: ST";
            break;
        case "Jinteki: Personal Evolution":
            return "Jinteki: PE";
            break;
        case "Jinteki: Replicating Perfection":
            return "Jinteki: RP";
            break;
        case "NBN: Making News":
            return "NBN: MN";
            break;
        case "NBN: The World is Yours*":
            return "NBN: TWiY*";
            break;
        case "Weyland Consortium: Because We Built It":
            return "Weyland: BWBI";
            break;
        case "Weyland Consortium: Building a Better World":
            return "Weyland: BaBW";
            break;
        default:
            return longtitle.substring(0, longtitle.indexOf(':'));
    }
}