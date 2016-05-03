module.exports = {
    'Buyers-Guide corporation' : function (browser) {
        browser
            .url(browser.launchUrl)
            .waitForElementVisible('body', 3000)
            .windowMaximize('current')
            .click("a[href='/Buyers-Guide']")         // click on buyers guide
            .waitForElementVisible('body', 3000)
            .waitForElementVisible('ul#cards li ul', 3000)
            .click("div#general div a")             // click on corp
            .waitForElementVisible('body', 3000)
            .waitForElementVisible('ul#cards li ul', 3000)
            .click("div#faction-list a")             // click on faction
            .waitForElementVisible('body', 3000)
            .waitForElementVisible('ul#cards li ul', 3000)
            .click("div#id-list a")             // click on id
            .waitForElementVisible('body', 3000)
            .waitForElementVisible('ul#cards li ul', 3000)
            .useXpath()         // go back
            .click("//a[contains(@href, 'faction')]")
            .waitForElementVisible('//body', 3000)
            .waitForElementVisible("//ul[@id='cards']/li/ul", 3000)
            .click("//a[contains(@href, 'side')]")
            .waitForElementVisible('//body', 3000)
            .waitForElementVisible("//ul[@id='cards']/li/ul", 3000)
            .click("//a[@href='/Buyers-Guide']")
            .waitForElementVisible('//body', 3000)
            .waitForElementVisible("//ul[@id='cards']/li/ul", 3000)
            .end();
    },
    'Buyers-Guide runner minifaction' : function (browser) {
        browser
            .url(browser.launchUrl)
            .useCss()
            .waitForElementVisible('body', 3000)
            .windowMaximize('current')
            .click("a[href='/Buyers-Guide']")         // click on buyers guide
            .waitForElementVisible('body', 3000)
            .waitForElementVisible('ul#cards li ul', 3000)
            .useXpath()
            .click("//div[@id='general']/div/a[2]")             // click on runner
            .waitForElementVisible('//body', 3000)
            .waitForElementVisible("//ul[@id='cards']/li/ul", 3000)
            .click("//div[@id='faction-list']/a[4]")             // click on mini faction
            .waitForElementVisible('//body', 3000)
            .waitForElementVisible("//ul[@id='cards']/li/ul", 3000)
            .click("//a[contains(@href, 'side')]")
            .waitForElementVisible('//body', 3000)
            .waitForElementVisible("//ul[@id='cards']/li/ul", 3000)
            .click("//a[@href='/Buyers-Guide']")
            .waitForElementVisible('//body', 3000)
            .waitForElementVisible("//ul[@id='cards']/li/ul", 3000)
            .end();
    }
};