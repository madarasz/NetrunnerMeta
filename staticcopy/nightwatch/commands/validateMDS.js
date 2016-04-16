exports.command = function (faction, callback) {
    var client = this, section1, section2;

    if (faction === 'runner') {
        section1 = 'event';
        section2 = 'icebreaker';
    } else {
        section1 = 'agenda';
        section2 = 'barrier';
    }

    client
        .useCss()
        .waitForElementVisible('body', 3000)
        .waitForElementVisible('div#chart_div1 div', 3000)     // MDS chart loads;
        .useXpath()
        .waitForElementVisible("//tbody[@id='average-table']/tr/td[@class='warning']/strong[contains(text(),'" + section1 + "')]", 3000)
        .waitForElementVisible("//tbody[@id='average-table2']/tr/td[@class='warning']/strong[contains(text(),'" + section2 + "')]", 3000);

    if (typeof callback === "function"){
        callback.call(client);
    }

    return this;
};