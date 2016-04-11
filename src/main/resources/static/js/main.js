// enable popovers
$(function () {
    $('[data-toggle="popover"]').popover();
});

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

// faction over time chart on home page
function drawFactionChart(urlvalue, elementid, callback) {

    $.ajax({
        url: urlvalue,
        dataType: "json",
        async: true,
        success: function(data) {

            var lineColors = [];

            $.each(data.cols, function (index, element) {
                lineColors.push({color : element.id});
            });

            lineColors.splice(0, 1); // removing first entry

            var chartData = new google.visualization.DataTable(data);

            var options = {
                'height': 350,
                'curveType': 'function',
                'vAxis': { format:'#,#%', viewWindowMode: 'explicit', viewWindow: { min: 0 } },
                'hAxis': { showTextEvery: 1, slantedText: 'true' },
                'chartArea': { top: 10, bottom: 10, height: '70%' },
                'legend': { position: 'right', maxLines: 6 },
                'series': lineColors,
                'lineWidth': 3
            };

            var chart = new google.visualization.LineChart(document.getElementById(elementid));
            chart.draw(chartData, options);
            $('#'+elementid).removeClass('spinner');

            typeof callback === 'function' && callback.apply(this, arguments); // make callback if exists
        }
    });

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
    if (data.getNumberOfRows() > 6) {
        haxis = 'none';
    } else {
        haxis = 'out';
    }

    var options = {
        'width': 555,
        'height':300,
        'vAxis': {format:'#,#%'},
        'hAxis': { textPosition: haxis }
    };

    var chart = new google.visualization.ColumnChart(document.getElementById(elementid));
    chart.draw(data, options);
    $('#'+elementid).removeClass('spinner');
}

function listTournamentIdentities(data, elementid, linkurl) {
    data.sort(tournamentShorters.byAllDeck);
    $.each(data, function(index, element) {
        if (element.allDeckCount > 0) {
            $(elementid).append($('<a>', {
                class: 'list-group-item',
                text: element.title,
                href: linkurl + element.title
            }).append($('<span>', {
                class: 'badge',
                text: element.allDeckCount
            })));
        }
    });
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
            {v: element[allline] / baseall, f: Math.round(element[allline] / baseall * 1000) / 10 + '%'});
        datatable[datatable.length - 1].push(
            {v: element[topline] / basetop, f: Math.round(element[topline] / basetop * 1000) / 10 + '%'});
    });

    return google.visualization.arrayToDataTable(datatable);
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
            listTournamentIdentities(data.ids, "#text_div1", "/MDSIdentity/" + dpname + "/");
            populateCardTable(data.ice, "#breakertable", 5);
            loadTournamentCardPoolTable(data.mostUsedCards.sort(tournamentShorters.byInTopDeck), "#runnertable2");

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
                    listTournamentIdentities(data.ids, "#text_div2", "/MDSIdentity/" + dpname + "/");
                    populateCardTable(data.ice, "#icetable", 5);
                    loadTournamentCardPoolTable(data.mostUsedCards.sort(tournamentShorters.byInTopDeck), "#corptable2");

                    drawTournamentCharts();
                }
            });

        }
    });
    window.scrollTo(0,0);
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
