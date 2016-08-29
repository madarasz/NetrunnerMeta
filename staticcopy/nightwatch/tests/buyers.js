module.exports = {
    'Buyers-Guide corporation' : function (browser) {
        browser
            .url(browser.launchUrl)
            .waitForElementVisible('body', 3000)
            .windowMaximize('current')
            .click("a[href='/Buyers-Guide']")         // click on buyers guide
            .useXpath()
            .waitForElementVisible('//body', 3000)
            .waitForElementVisible("//ul[@id='cards']/li/ul", 3000)
            .waitForElementVisible("//span[contains(@id,'amazon')]/iframe", 10000)
            .click("//div[@id='general']/div/a")             // click on corp
            .waitForElementVisible('//body', 3000)
            .waitForElementVisible("//ul[@id='cards']/li/ul", 3000)
            .waitForElementVisible("//span[contains(@id,'amazon')]/iframe", 10000)
            .click("//div[@id='faction-list']/a")             // click on faction
            .waitForElementVisible('//body', 3000)
            .waitForElementVisible("//ul[@id='cards']/li/ul", 3000)
            .waitForElementVisible("//span[contains(@id,'amazon')]/iframe", 10000)
            .click("//div[@id='id-list']/a")             // click on id
            .waitForElementVisible('//body', 3000)
            .waitForElementVisible("//ul[@id='cards']/li/ul", 3000)
            .waitForElementVisible("//span[contains(@id,'amazon')]/iframe", 10000)
            .click("//a[contains(@href, 'faction')]")
            .waitForElementVisible('//body', 3000)
            .waitForElementVisible("//ul[@id='cards']/li/ul", 3000)
            .waitForElementVisible("//span[contains(@id,'amazon')]/iframe", 10000)
            .click("//a[contains(@href, 'side')]")
            .waitForElementVisible('//body', 3000)
            .waitForElementVisible("//ul[@id='cards']/li/ul", 3000)
            .waitForElementVisible("//span[contains(@id,'amazon')]/iframe", 10000)
            .click("//a[@href='/Buyers-Guide']")
            .waitForElementVisible('//body', 3000)
            .waitForElementVisible("//ul[@id='cards']/li/ul", 3000)
            .waitForElementVisible("//span[contains(@id,'amazon')]/iframe", 10000)
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
            .waitForElementVisible("//span[contains(@id,'amazon')]/iframe", 10000)
            .click("//div[@id='general']/div/a[2]")             // click on runner
            .waitForElementVisible('//body', 3000)
            .waitForElementVisible("//ul[@id='cards']/li/ul", 3000)
            .waitForElementVisible("//span[contains(@id,'amazon')]/iframe", 10000)
            .click("//div[@id='faction-list']/a[4]")             // click on mini faction
            .waitForElementVisible('//body', 3000)
            .waitForElementVisible("//ul[@id='cards']/li/ul", 3000)
            .waitForElementVisible("//span[contains(@id,'amazon')]/iframe", 10000)
            .click("//a[contains(@href, 'side')]")
            .waitForElementVisible('//body', 3000)
            .waitForElementVisible("//ul[@id='cards']/li/ul", 3000)
            .waitForElementVisible("//span[contains(@id,'amazon')]/iframe", 10000)
            .click("//a[@href='/Buyers-Guide']")
            .waitForElementVisible('//body', 3000)
            .waitForElementVisible("//ul[@id='cards']/li/ul", 3000)
            .waitForElementVisible("//span[contains(@id,'amazon')]/iframe", 10000)
            .end();
    }
};