module.exports = {
    'Card via cards' : function (browser) {
        browser
            .url(browser.launchUrl)
            .waitForElementVisible('body', 3000)
            .windowMaximize('current')
            .click("a[href='/Cards']")         // click on cards section
            .waitForElementVisible('body', 3000)
            .useXpath()
            .waitForElementVisible("//a[contains(text(),'Future Proof')]", 3000)
            .click("//a[contains(text(),'Future Proof')]")
            .waitForElementVisible("//a[contains(text(),'R&D Interface')]", 3000)
            .click("//a[contains(text(),'R&D Interface')]")
            .validateCard()
            .end();
    },
    'Card via blog' : function (browser) {
        browser
            .url(browser.launchUrl)
            .waitForElementVisible('body', 3000)
            .windowMaximize('current')
            .useCss()
            .click("a[href='/Blog']")         // click on blogs section
            .waitForElementVisible('body', 3000)
            .useXpath()
            .click("//a[contains(@href,'/Blog/')]")
            .waitForElementVisible('//body', 3000)
            .click("//div[@class='blog-image']/a")
            .validateCard()
            .end();
    },
    '24/7 - card' : function (browser) {
        browser
            .url(browser.launchUrl + '/Cards/24/7%20News%20Cycle/')
            .validateCard()
            .end();
    },
    'Unregistered S&W - card' : function (browser) {
        browser
            .url(browser.launchUrl + "/Cards/Unregistered%20S&W%20'35/")
            .validateCard()
            .end();
    },
    'Kate ID - card' : function (browser) {
        browser
            .url(browser.launchUrl + "/Cards/Kate%20\"Mac\"%20McCaffrey:%20Digital%20Tinker/")
            .validateCard('id')
            .end();
    }
};