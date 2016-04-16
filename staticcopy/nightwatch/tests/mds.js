module.exports = {
    'MDS - last 3 runner' : function (browser) {
        browser
            .url(browser.launchUrl)
            .waitForElementVisible('body', 3000)
            .windowMaximize('current')                          // maximize browser window
            .click('div#drilldowns a')
            .click("ul#tabs li a[href='#decks']")
            .useXpath()
            .click("//div[@id='text_div1']/a[contains(@href,'Digital Tinker')]")
            .validateMDS('runner')
            .end();
    },
    'MDS - last corp' : function (browser) {
        browser
            .useCss()
            .url(browser.launchUrl)
            .waitForElementVisible('body', 3000)
            .windowMaximize('current')                          // maximize browser window
            .useXpath()
            .click("//div[@id='drilldowns']/a[2]")
            .useCss()
            .click("ul#tabs li a[href='#decks']")
            .useXpath()
            .click("//div[@id='text_div2']/a[contains(@href,'Near-Earth')]")
            .validateMDS('corp')
            .end();
    }
};