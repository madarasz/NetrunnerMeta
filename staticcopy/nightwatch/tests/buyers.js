module.exports = {
    'Buyers-Guide' : function (browser) {
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
            .click("div#id-list a")             // click on id
            .waitForElementVisible('body', 3000)
            .waitForElementVisible('ul#cards li ul', 3000)
            .end();
    }
};