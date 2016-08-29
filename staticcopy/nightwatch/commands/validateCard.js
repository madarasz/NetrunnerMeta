exports.command = function (type, callback) {
    var client = this;

    client
        .useCss()
        .waitForElementVisible('body', 3000);

    if (type !== 'unused') {
        client
            .waitForElementVisible('div#overtime-chart div', 3000)     // card over time chart loads
            .waitForElementVisible('tbody#combos tr td a', 3000)        // combo list loads
            .waitForElementVisible('tbody#cards tr td a', 3000)         // identity list loads
            .waitForElementVisible('ul#decks li ul li a', 3000);         // deck links load
    }

    if (type === 'id') {
        client
            .useXpath()
            .waitForElementVisible("//ul[@id='decks']/li/a[contains(text(),'Deck Drilldown')]", 3000);
    } else {
        client.waitForElementVisible('div#idchart div', 3000);            // identity chart loads
    }

    if (typeof callback === "function"){
        callback.call(client);
    }

    return this;
};