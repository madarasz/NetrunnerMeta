var runnerFactions = ['shaper', 'anarch', 'criminal',  'adam', 'apex', 'sunny-lebeau'],
    corpFactions = ['jinteki', 'nbn', 'haas-bioroid', 'weyland-consortium'],
    minifactions = ['adam', 'apex', 'sunny-lebeau'];

// enable popovers
$(function () {
    $('[data-toggle="popover"]').popover();
});

// enable ekko lightbox
$(document).on('click', '[data-toggle="lightbox"]', function(event) {
    event.preventDefault();
    $(this).ekkoLightbox({alwaysShowClose: true});
});

function miniFactionToIdentity(faction) {
    switch (faction) {
        case 'adam':
            return 'Adam: Compulsive Hacker';
            break;
        case 'apex':
            return 'Apex: Invasive Predator';
            break;
        case 'sunny-lebeau':
            return 'Sunny Lebeau: Security Specialist';
    }
}

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
        case "shaper-light":
            return "#afd279";
            break;
        case "anarch-light":
            return "#d28e79";
            break;
        case "criminal-light":
            return "#7998d2";
            break;
        case "haas-bioroid-light":
            return "#bb42bd";
            break;
        case "weyland-consortium-light":
            return "#30b58d";
            break;
        case "jinteki-light":
            return "#ee3211";
            break;
        case "nbn-light":
            return "#e0c08a";
            break;
        case "apex-light":
            return "#f00000";
            break;
        case "sunny-lebeau-light":
            return "#333333";
            break;
        case "adam-light":
            return "#f3bd35";
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
                    // cards replaced by Revised Core
                    if (element.cardtitle.indexOf('(old)') > -1) {
                        $(elementid).append($('<tr>').append($('<td>').append($('<span>', {
                            class: 'icon-' + element.faction
                        })), $('<td>').append($('<span>', {
                            text: element.cardtitle,
                            class: 'italic'
                        })), $('<td>', {
                            class: 'text-center italic',
                            colspan: 4
                        }).append($('<a>', {
                            href: '#results',
                            text: "see 'Revised Core Set'",
                            onClick: "loadTables('Revised Core Set', 48)"
                        }))));
                    } else {
                        // other cards
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
                }
            });
            $(elementid).removeClass('spinner');
        }
    });
}

// fill card usage table (MDS table, ICE/breakers) TODO: delete
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
        case "Haas-Bioroid: Architects of Tomorrow":
            return "HB: AoT";
            break;
        case "Jinteki: Personal Evolution":
            return "Jinteki: PE";
            break;
        case "Jinteki: Replicating Perfection":
            return "Jinteki: RP";
            break;
        case "Jinteki: Potential Unleashed":
            return "Jinteki: PU";
            break;
        case "NBN: Making News":
            return "NBN: MN";
            break;
        case "NBN: The World is Yours*":
            return "NBN: TWiY*";
            break;
        case "NBN: Controlling the Message":
            return "NBN: CtM";
            break;
        case "Weyland Consortium: Because We Built It":
            return "Weyland: BWBI";
            break;
        case "Weyland Consortium: Building a Better World":
            return "Weyland: BaBW";
            break;
        case "Skorpios Defense Systems: Persuasive Power":
            return "Skorpios";
            break;
        case "Liza Talking Thunder: Prominent Legislator":
            return "Liza";
            break;
        default:
            if (longtitle.indexOf('"') > -1) {
                return longtitle.substring(longtitle.indexOf('"')+1, longtitle.indexOf('"', longtitle.indexOf('"')+1));
            } else {
                return longtitle.substring(0, longtitle.indexOf(':'));
            }
    }
}

// facebook social button
function facebookButton(d, s, id, callback) {
    var js, fjs = d.getElementsByTagName(s)[0];
    if (d.getElementById(id)) return;
    js = d.createElement(s); js.id = id;
    js.src = "//connect.facebook.net/en_US/sdk.js#xfbml=1&version=v2.5&appId=119600664738915";
    fjs.parentNode.insertBefore(js, fjs);
    typeof callback === 'function' && callback.apply(this, arguments); // make callback if exists
}

// twitter timeline
function twitterTimeline(d,s,id, callback) {
    var js,
        fjs=d.getElementsByTagName(s)[0],
        p=/^http:/.test(d.location)?'http':'https';
    if (!d.getElementById(id)) {
        js=d.createElement(s);
        js.id=id;
        js.src=p+"://platform.twitter.com/widgets.js";
        fjs.parentNode.insertBefore(js,fjs);
    }
    typeof callback === 'function' && callback.apply(this, arguments); // make callback if exists
}

// display table on Info page
function fillInfotable() {

    $.ajax({
        url: "/JSON/Cardpool",
        dataType: "json",
        async: true,
        success: function (data) {
            $.each(data, function (index, element) {
                $("#cptable").append($('<tr>').append($('<td>').append($('<a>', {
                    text: element.title,
                    href: "/DPStats/" + encodeURIComponent(element.title) + "/"
                })), $('<td>', {
                    text: element.tournamentnum,
                    class: 'text-right'
                }), $('<td>', {
                    text: element.decknum,
                    class: 'text-right'
                }), $('<td>', {
                    text: element.standingsnum,
                    class: 'text-right'
                }), $('<td>', {
                    text: element.matchesnum,
                    class: 'text-right'
                })));
            });
            $('#cptable').removeClass('spinner');
        }
    });
}

function drawTournamentPieChart(data, colors, elementid) {
    var tournamentPieOptions = {
        'width': 555,
        'height': 300,
        'pieSliceText': 'percentage',
        'chartArea': { top: 10, width:'100%', height:'90%' },
        'slices': colors
    };
    var chart = new google.visualization.PieChart(document.getElementById(elementid));
    chart.draw(data, tournamentPieOptions);
}

function drawTournamentBarChart(data, elementid) {
    var haxis;

    // faction or identity
    if (data.getNumberOfRows() > 6) {
        haxis = {
            textPosition: 'none'
        };
    } else {
        haxis = { textPosition: 'out' };
    }

    var options = {
        'width': 555,
        'height': 300,
        'vAxis': { format:'percent' },
        'hAxis': haxis
    };

    var chart = new google.visualization.ColumnChart(document.getElementById(elementid));
    chart.draw(data, options);
    $('#'+elementid).removeClass('spinner');
}

String.prototype.capitalizeFirstLetter = function() {
    return this.charAt(0).toUpperCase() + this.slice(1);
};

String.prototype.capitalizeTitle = function() {
    return this.replace(/[a-zA-Z]+/g, function(txt){return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();});
};

function listTournamentIdentities(data, elementid, linkurl, linkurlFaction) {
    data.sort(tournamentShorters.byAllDeck);

    // gather factions
    if  (linkurl.indexOf('Last%203%20aggregated') < 0) {
        var factions = [];
        $.each(data, function(index, element) {
            if (element.allDeckCount > 0 && $.inArray(element.faction, minifactions) < 0) {
                addIdentityToArray(element.faction, factions, element.allDeckCount);
            }
        });
        factions.sort(BuyerShorters.byCount);
        $.each(factions, function(index, element) {
            $(elementid).append($('<a>', {
                class: 'button-' + element.identity + ' list-group-item',
                text: ' ' + element.identity.capitalizeTitle(),
                href: linkurlFaction + element.identity
            }).prepend($('<span>', {
                class: 'icon-' + element.identity
            })).append($('<span>', {
                class: 'badge',
                text: element.count
            })));
        });
    }

    $.each(data, function(index, element) {
        if (element.allDeckCount > 0) {
            $(elementid).append($('<a>', {
                class: 'list-group-item',
                text: ' ' + element.title,
                href: linkurl + element.title
            }).prepend($('<span>', {
                class: 'icon-' + element.faction
            })).append($('<span>', {
                class: 'badge',
                text: element.allDeckCount
            })));
        }
    });

    // if last 3
    if (linkurl.indexOf('Last%203%20aggregated') > -1) {
        $(elementid).append($('<a>', {
            class: 'list-group-item disabled',
            text: 'faction deck drilldown is not supported here'
        }));
    }
    $(elementid).removeClass('spinner');
}

function loadTournamentCardPoolTable(data, elementid) {
    $.each(data, function(index, element) {
        $(elementid).append($('<tr>').append($('<td>').append($('<span>', {
            class: 'icon-' + element.faction
        })),$('<td>').append($('<a>', {
            text: element.cardtitle,
            href: '/Cards/' + element.cardtitle + '/'
        })), $('<td>').append($('<em>', {
            text: element.cardpacktitle
        })), $('<td>', {
            text: element.intopdecks,
            class: 'text-right'
        }), $('<td>', {
            text: '(' + Math.round(element.topdeckfraction * 100) + '%)',
            class: 'text-left'
        })));
    });
    $(elementid).removeClass('spinner');
}

function populateCardTable(data, elementid, minusing, filter) {
    var typecode = 'none';
    filter = filter || "";
    $.each(data, function(index, element) {
        if (parseInt(element.using) > minusing &&   // greater than minusing
            (filter === "" || filter.indexOf(element.typecodes) > -1))  {  // not filtered out
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

var tournamentShorters = {
    byTopStanding : function (a,b) {
        return (b.topStandingCount - a.topStandingCount);
    },
    byAllStanding : function (a,b) {
        return (b.allStandingCount - a.allStandingCount);
    },
    byAllDeck : function (a,b) {
        return (b.allDeckCount - a.allDeckCount);
    },
    byInTopDeck: function (a,b) {
        return (b.intopdecks - a.intopdecks);
    },
    byTopAllFractionStanding: function(a,b) {
        return (b.topStandingCount / b.allStandingCount - a.topStandingCount / a.allStandingCount)
    },
    byWinRate: function(a,b) {
        return ((b.matches.winMatchCount + b.matches.timedWinMatchCount) / b.matches.allMatchCount -
            (a.matches.winMatchCount + a.matches.timedWinMatchCount) / a.matches.allMatchCount);
    }
};

function buildTournamentPieData(datasource, datatable, color, titleline, dataline, colorline, sorter) {
    datasource.sort(sorter);

    $.each(datasource, function(index, element) {
        datatable.push([]);
        datatable[datatable.length - 1].push(element[titleline]);
        datatable[datatable.length - 1].push(element[dataline]);
        color.push({color : factionCodeToColor(element[colorline])});
    });

    return google.visualization.arrayToDataTable(datatable);
}

function buildTournamentBarData(datasource, datatable, baseall, basetop, titleline, allline, topline, sorter) {
    datasource.sort(sorter);

    $.each(datasource, function(index, element) {
        datatable.push([]);
        datatable[datatable.length - 1].push(element[titleline]);
        datatable[datatable.length - 1].push(
            {v: element[allline] / baseall, f: percentageToString(element[allline] / baseall)});
        datatable[datatable.length - 1].push(
            {v: element[topline] / basetop, f: percentageToString(element[topline] / basetop)});
    });

    return google.visualization.arrayToDataTable(datatable);
}

function percentageToString(fraction) {
    return Math.round(fraction * 1000) / 10 + '%';
}

function populateTournamentCharts(dpname) {
    // runner data
    $.ajax({
        url: "/JSON/Tournament/runner/" + dpname,
        dataType: "json",
        async: true,
        success: function(data) {
            runnerTopFactionData = buildTournamentPieData(data.factions, runnerTopFactionData, runnerTopFactionColors,
                "title", "topStandingCount", "title", tournamentShorters.byTopStanding);
            runnerAllFactionData = buildTournamentPieData(data.factions, runnerAllFactionData, runnerAllFactionColors,
                "title", "allStandingCount", "title", tournamentShorters.byAllStanding);
            runnerTopIdentityData = buildTournamentPieData(data.ids, runnerTopIdentityData, runnerTopIdentityColors,
                "title", "topStandingCount", "faction", tournamentShorters.byTopStanding);
            runnerAllIdentityData = buildTournamentPieData(data.ids, runnerAllIdentityData, runnerAllIdentityColors,
                "title", "allStandingCount", "faction", tournamentShorters.byAllStanding);
            runnerCompareFactionData = buildTournamentBarData(data.factions, runnerCompareFactionData,
                data.allStandingCount, data.topStandingCount, "title", "allStandingCount", "topStandingCount", tournamentShorters.byAllStanding);
            runnerCompareIdentityData = buildTournamentBarData(data.ids, runnerCompareIdentityData,
                data.allStandingCount, data.topStandingCount, "title", "allStandingCount", "topStandingCount", tournamentShorters.byAllStanding);
            listTournamentIdentities(data.ids, "#text_div1", "/MDSIdentity/" + dpname + "/", "/MDSFaction/" + dpname + "/");
            populateCardTable(data.ice, "#breakertable", 5);
            loadTournamentCardPoolTable(data.mostUsedCards.sort(tournamentShorters.byInTopDeck), "#runnertable2");
            showSpotLight(data.ids, data.allStandingCount, data.topStandingCount, data.allDeckCount, "runner");
            runnerWinrateData = buildWinrateData(data.ids, runnerWinrateData, tournamentShorters.byWinRate, "runner");

            // corp data
            $.ajax({
                url: "/JSON/Tournament/corp/" + dpname,
                dataType: "json",
                async: true,
                success: function(data) {
                    corpTopFactionData = buildTournamentPieData(data.factions, corpTopFactionData, corpTopFactionColors,
                        "title", "topStandingCount", "title", tournamentShorters.byTopStanding);
                    corpAllFactionData = buildTournamentPieData(data.factions, corpAllFactionData, corpAllFactionColors,
                        "title", "allStandingCount", "title", tournamentShorters.byAllStanding);
                    corpTopIdentityData = buildTournamentPieData(data.ids, corpTopIdentityData, corpTopIdentityColors,
                        "title", "topStandingCount", "faction", tournamentShorters.byTopStanding);
                    corpAllIdentityData = buildTournamentPieData(data.ids, corpAllIdentityData, corpAllIdentityColors,
                        "title", "allStandingCount", "faction", tournamentShorters.byAllStanding);
                    corpCompareFactionData = buildTournamentBarData(data.factions, corpCompareFactionData,
                        data.allStandingCount, data.topStandingCount, "title", "allStandingCount", "topStandingCount", tournamentShorters.byAllStanding);
                    corpCompareIdentityData = buildTournamentBarData(data.ids, corpCompareIdentityData,
                        data.allStandingCount, data.topStandingCount, "title", "allStandingCount", "topStandingCount", tournamentShorters.byAllStanding);
                    listTournamentIdentities(data.ids, "#text_div2", "/MDSIdentity/" + dpname + "/", "/MDSFaction/" + dpname + "/");
                    populateCardTable(data.ice, "#icetable", 5);
                    loadTournamentCardPoolTable(data.mostUsedCards.sort(tournamentShorters.byInTopDeck), "#corptable2");
                    showSpotLight(data.ids, data.allStandingCount, data.topStandingCount, data.allDeckCount, "corp");
                    corpWinrateData = buildWinrateData(data.ids, corpWinrateData, tournamentShorters.byWinRate, "corp");

                    winrateData = google.visualization.arrayToDataTable(winrateData);
                    drawTournamentCharts();
                }
            });

        }
    });
    window.scrollTo(0,0);
}



function imageURL(title) {
    return "/static/img/cards/netrunner-" +
        title.toLowerCase().replace(new RegExp(" ", 'g'), "-").replace(new RegExp("[^a-z0-9.-]", 'g'), "") + ".png";
}

function drawTournamentCharts() {
    drawTournamentPieChart(runnerTopFactionData, runnerTopFactionColors, 'chart_div1');
    drawTournamentPieChart(runnerTopIdentityData, runnerTopIdentityColors, 'chart_div3');
    drawTournamentPieChart(runnerAllFactionData, runnerAllFactionColors, 'chart_div5');
    drawTournamentPieChart(runnerAllIdentityData, runnerAllIdentityColors, 'chart_div7');
    drawTournamentBarChart(runnerCompareFactionData, 'chart_div9');
    drawTournamentBarChart(runnerCompareIdentityData, 'chart_div11');

    drawTournamentPieChart(corpTopFactionData, corpTopFactionColors, 'chart_div2');
    drawTournamentPieChart(corpTopIdentityData, corpTopIdentityColors, 'chart_div4');
    drawTournamentPieChart(corpAllFactionData, corpAllFactionColors, 'chart_div6');
    drawTournamentPieChart(corpAllIdentityData, corpAllIdentityColors, 'chart_div8');
    drawTournamentBarChart(corpCompareFactionData, 'chart_div10');
    drawTournamentBarChart(corpCompareIdentityData, 'chart_div12');

    drawSideWinChart();
    drawIDWinrateCharts(runnerWinrateData, 'win-runner');
    drawIDWinrateCharts(corpWinrateData, 'win-corp');
}

function drawMDSChart(data, tooltips) {

    var options = {
        'legend': 'none',
        'height': 555,
        'chartArea': { width:'100%', height:'100%' },
        'backgroundColor': { strokeWidth: 1 },
        'hAxis' : { gridlines: { count: 0 }, baselineColor: '#fff'},
        'vAxis' : { gridlines: { count: 0 }, baselineColor: '#fff'},
        'tooltip': { trigger: 'selection' }
    };

    var chart = new google.visualization.ScatterChart(document.getElementById('chart_div1'));

    // set tooltip
    chart.setAction({
        id: 'sample',
        text: 'See decklist',
        action: function () {
            selection = chart.getSelection();
            setMDSDeckInfo(tooltips[selection[0].row].htmlDigest);
        }
    });

    chart.draw(data, options);

    $('#chart_div1').removeClass('spinner');
}

function setMDSDeckInfo(htmlvalue) {
    $('#deck_data').replaceWith("<div id=\"deck_data\">" + htmlvalue + "</div>");
}

function loadFactionData() {
    $.ajax({
        url: '/JSON/FactionsOverTime',
        dataType: "json",
        async: true,
        success: function(data) {
            $.each(data, function (index, element) {
                runnerData.push([]);
                corpData.push([]);
                runnerData[runnerData.length - 1].push(element.packTitle);
                corpData[corpData.length - 1].push(element.packTitle);
                populateFOTData(runnerFactions, element.runners,
                    element.runnerTopStandingCount, element.runnerAllStandingCount, runnerData);
                populateFOTData(corpFactions, element.corps,
                    element.corpTopStandingCount, element.corpAllStandingCount, corpData);
            });
            runnerData = new google.visualization.arrayToDataTable(runnerData);
            corpData = new google.visualization.arrayToDataTable(corpData);
            drawFactionChart(runnerData, runnerFactions, "chart_div1", runnerOption);
            drawFactionChart(corpData, corpFactions, "chart_div2", corpOption);
            $('.btn-group-xs button').removeAttr('disabled');
            // social
            //facebookButton(document, 'script', 'facebook-jssdk');
            //twitterTimeline(document,"script", "twitter-wjs");
            window.scrollTo(0,0);
        }
    });
}

function populateFOTData(factions, factionData, topStanding, allStanding, dataArray) {
    $.each(factions, function (index, faction) {
        var datarow = readFaction(factionData, faction);
        if (datarow === undefined) {
            dataArray[dataArray.length - 1].push(0, 0);
        } else {
            var top = datarow.topStandingCount / topStanding,
                all = datarow.allStandingCount / allStanding;
            dataArray[dataArray.length - 1].push({f: percentageToString(top),v: top});
            dataArray[dataArray.length - 1].push({f: percentageToString(all),v: all});
        }
    });
}

function readFaction(data, faction) {
    var result;
    $.each(data, function (index, element) {
        if (element.faction === faction) {
            result = element;
            return false;
        }
    });
    return result;
}

// faction over time chart on home page
function drawFactionChart(data, factions, elementid, options, showmore) {

    // last 10 data packs
    var showdata = jQuery.extend(true, {}, data);
    if (!showmore) {
        showdata.Nf = showdata.Nf.slice(data.Nf.length - 10);
    }

    // calculate colors
    var color = [];
    $.each(factions, function (index, faction) {
        switch (options) {
            case "top":
                color.push({color: factionCodeToColor(faction)});
                break;
            case "all":
                color.push({color: factionCodeToColor(faction), lineDashStyle: [4, 4]});
                break;
            default:
                color.push({color: factionCodeToColor(faction)},
                    {color: factionCodeToColor(faction), lineDashStyle: [4, 4]});
        }
    });

    var chartOptions = {
        'height': 350,
        'curveType': 'function',
        'vAxis': { format: 'percent', textPosition: 'out', viewWindowMode: 'pretty', viewWindow: { min: 0 } },
        'hAxis': { showTextEvery: 1, slantedText: 'true' },
        'chartArea': { top: 10, bottom: 70, height: '70%' },
        'legend': { position: 'right', maxLines: 6 },
        'series': color,
        'lineWidth': 3
    };

    var chart = new google.visualization.LineChart(document.getElementById(elementid));
    // hide lines
    var view = new google.visualization.DataView(showdata);
    switch (options) {
        case "top":
            view.hideColumns([2,4,6,8,10,12]);
            break;
        case "all":
            view.hideColumns([1,3,5,7,9,11]);
            break;
    }

    chart.draw(view, chartOptions);
    $('#'+elementid).removeClass('spinner');
}

function populateBuyersFactions(data) {
    // gather factions
    var factions = [];
    $.each(data, function(index, identity) {
        if (identity.allDeckCount > 0) {
            addIdentityToArray(identity.faction, factions, identity.allDeckCount);
        }
    });
    factions.sort(BuyerShorters.byCount);
    // display factions
    $.each(factions, function(index, faction) {
        $('#faction-list').append($('<a>', {
            class: 'list-group-item lgi-small button-'+faction.identity,
            text: $.inArray(faction.identity, minifactions) < 0 ? ' ' + faction.identity.capitalizeTitle() : ' ' + miniFactionToIdentity(faction.identity),
            href: $.inArray(faction.identity, minifactions) < 0 ? '/Buyers-Guide/faction/' + faction.identity : '/Buyers-Guide/identity/' + miniFactionToIdentity(faction.identity),
            id: 'faction-' + index
        }).prepend($('<span>', {
            class: 'icon-' + faction.identity
        })));
        if (faction.count < 6) {
            $('#faction-' + index).prepend($('<span>', {
                class: 'fa fa-exclamation-triangle warn-badge badge',
                title: 'WARNING: low data',
                text: ' low data',
                'aria-hidden': 'true',
                style: "color: darkred"
            }));
        }
    });
}

function populateBuyersPacks(result, url, type, callback, url2, faction) {
    $.ajax({
        url: url,
        dataType: "json",
        async: true,
        success: function (data) {
            if (type === 'general' || type === 'side') {
                addToBuyers(result, data.mostUsedCards);
                if (type === 'side') {
                    populateBuyersFactions(data.ids);
                }
            } else {
                addToBuyersFromMDS(result, data.cards);
                if (type === 'faction') {
                    $.ajax({
                        url: url2,
                        dataType: "json",
                        async: true,
                        success: function (data) {
                            populateBuyersIds(data.ids, faction);
                        }
                    });
                }
            }
            typeof callback === 'function' && callback.apply(this, arguments); // make callback if exists
        }
    });
}

function populateBuyersIds(data, faction) {
    data.sort(BuyerShorters.byDeckCount);
    $.each(data, function(index, identity) {
        if (identity.faction === faction && identity.allDeckCount > 0) {
            $('#id-list').append($('<a>', {
                class: 'list-group-item lgi-small',
                text: ' ' + identity.title,
                href: '/Buyers-Guide/identity/' + identity.title,
                id: 'id-' + index
            }).prepend($('<span>', {
                class: 'icon-' + identity.faction
            })));
            if (identity.allDeckCount < 6) {
                $('#id-' + index).prepend($('<span>', {
                    class: 'fa fa-exclamation-triangle warn-badge badge',
                    title: 'WARNING: low data',
                    text: ' low data',
                    'aria-hidden': 'true',
                    style: "color: darkred"
                }));
            }
        }
    });
}

function displayBuyersCards(data, callback) {
    data.sort(BuyerShorters.byScore);
    $.each(data, function(index, packElement) {
        if (index < 8) {
            $("#cards").append($("<li>", {
                text: packElement.pack
            }).append($("<ul>", {
                id: 'pack-' + index,
                class: 'card-list'
            })));

            packElement.cards.sort(BuyerShorters.byScore);
            $.each(packElement.cards, function (index2, cardElement) {
                if (index2 < 6) {
                    $("#pack-" + index).append($("<li>", {
                        class: 'icon-' + cardElement.faction + ' card-list'
                    }).append($("<a>", {
                        text: cardElement.title,
                        href: "/Cards/" + cardElement.title + "/"
                    })));
                }
            });
        }
    });
    $('#cards').removeClass('spinner');
    typeof callback === 'function' && callback.apply(this, arguments); // make callback if exists
}

var BuyerShorters = {
    byScore: function (a,b) {
        return (b.score - a.score);
    },
    byDeckCount: function (a,b) {
        return (b.allDeckCount - a.allDeckCount);
    },
    byCount: function (a,b) {
        return (b.count - a.count);
    }
};

function addToBuyers(result, data) {
    $.each(data, function(index, element) {
        var found = false;
        var score = element.topdeckfraction * element.topdeckfraction;
        $.each(result, function(index2, element2) {
            if (element2.pack === element.cardpacktitle) {
                element2.score += score;
                element2.cards.push({title: element.cardtitle, faction: element.faction, score: score});
                found = true;
                return true;
            }
        });
        if (!found) {
            result.push({pack: element.cardpacktitle, score: score, cards: [{title: element.cardtitle, faction: element.faction, score: score}]});
        }

    });
}

function addToBuyersFromMDS(result, data) {
    $.each(data, function(index, element) {
        var found = false;
        var score = element.average * element.average / 9;
        if (element.average > 0.1) {     // at least 0.1
            $.each(result, function (index2, element2) {
                if (element2.pack === element.cardpack) {
                    element2.score += score;
                    element2.cards.push({title: element.cardtitle, faction: element.faction, score: score});
                    found = true;
                    return true;
                }
            });
            if (!found) {
                result.push({
                    pack: element.cardpack,
                    score: score,
                    cards: [{title: element.cardtitle, faction: element.faction, score: score}]
                });
            }
        }
    });
}



function addIdentityToArray(identity, result, increment) {
    var found = false;
    increment = increment || 1;
    $.each(result, function(index, element) {
        if (element.identity == identity) {
            element.count += increment;
            found = true;
        }
    });
    if (found == false) {
        result.push({identity: identity, count: increment, color: ''});
    }
}

function findIdColor(data, id) {
    var result;
    $.each(data, function(index, element) {
        if (element.identity == id) {
            result = element.color;
            return true;
        }
    });
    return result;
}

function hslToRgb(h, s, l){
    var r, g, b;

    if(s == 0){
        r = g = b = l; // achromatic
    }else{
        var hue2rgb = function hue2rgb(p, q, t){
            if(t < 0) t += 1;
            if(t > 1) t -= 1;
            if(t < 1/6) return p + (q - p) * 6 * t;
            if(t < 1/2) return q;
            if(t < 2/3) return p + (q - p) * (2/3 - t) * 6;
            return p;
        };

        var q = l < 0.5 ? l * (1 + s) : l + s - l * s;
        var p = 2 * l - q;
        r = hue2rgb(p, q, h + 1/3);
        g = hue2rgb(p, q, h);
        b = hue2rgb(p, q, h - 1/3);
    }

    return '#' + ('00' + Math.round(r * 255).toString(16)).substr(-2) +
        ('00' + Math.round(g * 255).toString(16)).substr(-2) +
        ('00' + Math.round(b * 255).toString(16)).substr(-2);
}

function getIDcolor(faction, index, count) {
    switch (faction) {
        case 'shaper':
            return hslToRgb(120 / 360, 0.61, (count-index)/count);
            break;
        case 'anarch':
            return hslToRgb(16 / 360, 1, (count-index)/count);
            break;
        case 'criminal':
            return hslToRgb(225 / 360, 0.57, (count-index)/count);
            break;
        case 'haas-bioroid':
            return hslToRgb(271 / 360, 0.76, (count-index)/count);
            break;
        case 'weyland-consortium':
            return hslToRgb(120 / 360, 1, (count-index)/count);
            break;
        case 'jinteki':
            return hslToRgb(348 / 360, 0.83, (count-index)/count);
            break;
        case 'nbn':
            return hslToRgb(33 / 360, 1, (count-index)/count);
            break;
        default:
            return hslToRgb(0, 0, 0.15 + (count-index)/count);
    }
}

function displayAmazon(data, elementid) {
    data.sort(BuyerShorters.byScore);
    $.each(data, function(index, element) {
        if (index < 8) {
            $.ajax({
                url: '/JSON/Cardpack/' + element.pack,
                dataType: "json",
                async: true,
                success: function (data2) {
                    if (data2.amazonHtml !== null) {
                        $('#' + elementid + index).append(data2.amazonHtml);
                    }
                }
            });
        }
    });
}

// IDs over time
function orderIDs(ids, data, dataType) {
    for (var i = 0; i < ids.length - 1; i++) {
        for (var u = i + 1; u < ids.length; u++) {
            if (data[ids[i]][dataType] > data[ids[u]][dataType]) {
                var z = ids[i];
                ids[i] = ids[u];
                ids[u] = z;
            }
        }
    }
}

function drawIDsOverTime(ids, data, packs, dataType) {
    var i, u;
    orderIDs(ids, data, dataType + 'Sum');
    dataTable = new google.visualization.DataTable();
    dataTable.addColumn('string', 'DP');
    for (i = 0; i < ids.length; i++) {
        if (typeof stopFromPackIndex == 'undefined' || i < stopFromPackIndex) {
            dataTable.addColumn('number', shortTitle(ids[i]));
        }
    }
    for (i = 0; i < packs.length; i++) {
        if (typeof stopFromPackIndex == 'undefined' || i < stopFromPackIndex) {
            var row = [packs[i]];
            for (u = 0; u < ids.length; u++) {
                var value = data[ids[u]][dataType][packs[i]];
                if (!value) {
                    value = 0;
                }
                // if we want to crop the data
                if (typeof cropFromPackIndex == 'undefined' || i < cropFromPackIndex) {
                    row.push({f: percentageToString(value), v: value});
                } else {
                    row.push(null);
                }
            }
            dataTable.addRow(row);
        }
    }
    idsOverTimeChart = new google.visualization.AreaChart(document.getElementById(chartEl));
    $('#'+chartEl).removeClass('spinner');
    idsOverTimeChart.draw(dataTable, chartOptions);
}

function loadPacks(callback) {
    $.ajax({
        url: '/JSON/Cardpoolnames',
        dataType: "json",
        async: true,
        success: function (packs) {
            cardpacks = packs.reverse();
            typeof callback === 'function' && callback.apply(this, arguments); // make callback if exists
        }
    });
}

function loadIDs(ids, callback) {
    var idCount = 0;
    $.ajax({
        url: 'https://netrunnerdb.com/api/2.0/public/cards',
        dataType: "json",
        async: true,
        success: function (idData) {
            for (var i = 0; i < idData['data'].length; i++) {
                if (idData.data[i].type_code == 'identity' && idData.data[i].pack_code != 'draft' && ids.hasOwnProperty(idData.data[i].faction_code)) {
                    idCount++;
                    ids[idData.data[i].faction_code].push(idData.data[i].title);
                    $.ajax({
                        url: '/JSON/Cards/' + idData.data[i].title + '/card.json',
                        dataType: "json",
                        async: true,
                        success: function (cardStats) {
                            // remove if no history or not significant
                            var unsignificant = true, faction = '', u;
                            for (u = 0; u < cardStats.overTime.length; u++) {
                                if (cardStats.overTime[u].deckfraction > 0.07 || cardStats.overTime[u].topdeckfraction > 0.07) {
                                    unsignificant = false;
                                    faction = cardStats.overTime[u].faction;
                                    break;
                                }
                            }
                            if (!unsignificant) {
                                // build data
                                factionData[faction][cardStats.title] = { top: {}, all: {}, topSum: 0, allSum: 0};
                                for (u = 0; u < cardStats.overTime.length; u++) {
                                    var topValue = cardStats.overTime[u].topdeckfraction,
                                        allValue = cardStats.overTime[u].deckfraction;

                                    factionData[faction][cardStats.title].top[cardStats.overTime[u].cardpacktitle] = topValue;
                                    factionData[faction][cardStats.title].topSum += topValue;
                                    factionData[faction][cardStats.title].all[cardStats.overTime[u].cardpacktitle] = allValue;
                                    factionData[faction][cardStats.title].allSum += allValue;
                                }
                            } else {
                                // remove unsignificant
                                removeIDFromList(ids, cardStats.title);
                            }

                            idCount--;
                            // last one, make callback
                            if (idCount == 0) {
                                typeof callback === 'function' && callback.apply(this, arguments); // make callback if exists
                            }
                        }
                    });
                }
            }
        }
    });
}

function removeIDFromList(list, id) {
    for (var faction in list) {
        if (list.hasOwnProperty(faction)) {
            if (list[faction].indexOf(id) > -1) {
                list[faction].splice(list[faction].indexOf(id), 1);
            }
        }
    }
}
function addCardStat(element, card, allCount, topCount) {
    $(element).append($('<a>', {
        href: '/Cards/' + card.title + '/'
    }).append($('<img>', {
        src: imageURL(card.title)
    }), $('<div>', {
        class: 'spotlight-title',
        text: card.title
    })), $('<div>', {
        text: 'all: ' + percentageToString(card.allStandingCount / allCount) +
        ' - top: ' + percentageToString(card.topStandingCount / topCount)
    }));
    $(element).removeClass('spinner');
}

function buildWinrateData(dataSource, dataTable, sorter, side) {
    dataSource.sort(sorter);
    dataSource.sort(sorter);    // WTF is this needed twice? Once does sort right.

    for (var i = 0; i < dataSource.length; i++) {

        var element = dataSource[i];
        if (element['matches']['allMatchCount'] >= 50) {

            var winPercentage = element['matches']['winMatchCount'] / element['matches']['allMatchCount'],
                timedWinPercentage = element['matches']['timedWinMatchCount'] / element['matches']['allMatchCount'],
                standardError = Math.sqrt((winPercentage+timedWinPercentage) * (1-winPercentage-timedWinPercentage)
                    / element['matches']['allMatchCount']);

            dataTable.push([]);
            // win percentage
            dataTable[dataTable.length - 1].push(shortTitle(element['title']));
            dataTable[dataTable.length - 1].push({
                v: winPercentage,
                f: percentageToString(winPercentage) +
                ' (' + element['matches']['winMatchCount'] + '/' + element['matches']['allMatchCount'] + ' matches)'
            });
            dataTable[dataTable.length - 1].push('color: ' + factionCodeToColor(element['faction']));
            // timed win percentage
            dataTable[dataTable.length - 1].push({
                v: timedWinPercentage,
                f: percentageToString(timedWinPercentage) +
                ' (' + element['matches']['timedWinMatchCount'] + '/' + element['matches']['allMatchCount'] + ' matches)'
            });
            dataTable[dataTable.length - 1].push('color: ' + factionCodeToColor(element['faction']+'-light'));
            // standard error, starts on standard+timed win
            dataTable[dataTable.length - 1].push(timedWinPercentage - standardError);
            dataTable[dataTable.length - 1].push(timedWinPercentage + standardError);
        }

        winrateData[3][1] += element['matches']['tieMatchCount'] / 2;
        winrateData[side == "runner" ? 5 : 1][1] += element['matches']['winMatchCount'];
        winrateData[side == "runner" ? 4 : 2][1] += element['matches']['timedWinMatchCount'];
        matchCount += element['matches']['allMatchCount'] / 2;
    }

    return google.visualization.arrayToDataTable(dataTable);
}

function drawSideWinChart() {
    var winratePieOptions = {
        'width': 555,
        'height': 300,
        'chartArea': { top: 10, width:'100%', height:'90%' },
        'slices': [{color: '#337ab7'}, {color: '#78acd9'}, {color: '#cccccc'}, {color: '#eba5a3'}, {color: '#d9534f'}]
    };
    var chart = new google.visualization.PieChart(document.getElementById('win-side'));
    chart.draw(winrateData, winratePieOptions);
    $('#win-side').removeClass('spinner');
    if (matchCount < 300) {
        $('#low-matches').removeClass('hidden');
        $('#win-overall-row').addClass('hidden');
        $('#win-id-row').addClass('hidden');
    }
}

function drawIDWinrateCharts(dataTable, element) {
    var options = {
        'width': 555,
        'height': 400,
        'chartArea': {width: '60%'},
        'hAxis': { format: 'percent', minValue: 0 },
        'isStacked': true,
        'vAxis': { textPosition: 'out' },
        'legend': { position: "none" },
        'intervals': { color: '#999999' }
//                'hAxis': haxis
    };

    var chart = new google.visualization.BarChart(document.getElementById(element));
    chart.draw(dataTable, options);
    $('#'+element).removeClass('spinner');
}