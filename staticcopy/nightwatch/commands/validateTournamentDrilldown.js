exports.command = function (callback) {
    var client = this;

    client
        .useCss()
        .waitForElementVisible('body', 3000)
        .waitForElementVisible('div#spot_popular_runner a img', 3000) // most popular runner spotlight
        .waitForElementVisible('div#spot_popular_corp a img', 3000) // most popular corp spotlight
        .waitForElementVisible('div#spot_impressive_runner a img', 3000) // most impressive runner spotlight
        .waitForElementVisible('div#spot_impressive_corp a img', 3000) // most impressive corp spotlight
        .click("ul#tabs li a[href='#top']")
        .waitForElementVisible('div#chart_div1 div', 3000) // top runner factions chart loads
        .waitForElementVisible('div#chart_div2 div', 3000) // top corp factions chart loads
        .waitForElementVisible('div#chart_div3 div', 3000) // top runner ids chart loads
        .waitForElementVisible('div#chart_div4 div', 3000) // top corp ids chart loads
        .click("ul#tabs li a[href='#all']")
        .waitForElementVisible('div#chart_div5 div', 3000) // all runner factions chart loads
        .waitForElementVisible('div#chart_div6 div', 3000) // all corp factions chart loads
        .waitForElementVisible('div#chart_div7 div', 3000) // all runner ids chart loads
        .waitForElementVisible('div#chart_div8 div', 3000) // all corp ids chart loads
        .click("ul#tabs li a[href='#compare']")
        .waitForElementVisible('div#chart_div9 div', 3000) // compare runner factions chart loads
        .waitForElementVisible('div#chart_div10 div', 3000) // compare corp factions chart loads
        .waitForElementVisible('div#chart_div11 div', 3000) // compare runner ids chart loads
        .waitForElementVisible('div#chart_div12 div', 3000) // compare corp ids chart loads
        .click("ul#tabs li a[href='#decks']")
        .waitForElementVisible('div#text_div1 a', 3000) // runner deck drilldown
        .waitForElementVisible('div#text_div2 a', 3000) // corp deck drilldown
        .click("ul#tabs li a[href='#ice']")
        .useXpath()
        .waitForElementVisible("//tbody[@id='breakertable']/tr/td[@class='warning']/strong[contains(text(),'fracter')]", 3000)
        .waitForElementVisible("//tbody[@id='icetable']/tr/td[@class='warning']/strong[contains(text(),'barrier')]", 3000)
        .waitForElementVisible("//tbody[@id='breakertable']/tr[2]/td", 3000)
        .waitForElementVisible("//tbody[@id='icetable']/tr[2]/td", 3000)
        .useCss()
        .click("ul#tabs li a[href='#cards']")
        .useXpath()
        .waitForElementVisible("//tbody[@id='runnertable2']/tr[10]/td[2]/a", 3000)
        .waitForElementVisible("//tbody[@id='corptable2']/tr[10]/td[2]/a", 3000);

    if (typeof callback === "function"){
        callback.call(client);
    }

    return this;
};