module.exports = {
    'Tournament drilldown - last 3' : function (browser) {
        browser
            .url(browser.launchUrl)
            .waitForElementVisible('body', 3000)
            .windowMaximize('current')                          // maximize browser window
            .click('div#drilldowns a')
            .validateTournamentDrilldown()
            .end();
    },
    'Tournament drilldown - last' : function (browser) {
        browser
            .url(browser.launchUrl)
            .useCss()
            .waitForElementVisible('body', 3000)
            .windowMaximize('current')                          // maximize browser window
            .useXpath()
            .click("//div[@id='drilldowns']/a[2]")
            .validateTournamentDrilldown()
            .end();
    }
};