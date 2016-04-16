module.exports = {
    'Home page' : function (browser) {
        browser
            .url(browser.launchUrl)
            .waitForElementVisible('body', 3000)
            .click('li.dropdown a.dropdown-toggle')         // click on tournament drilldown
            .waitForElementVisible('div#chart_div1 div', 3000) // runner chart loads
            .waitForElementVisible('div#chart_div2 div', 3000) // corp chart loads
            .useXpath()
            .waitForElementVisible("//ul[@class='dropdown-menu']/li[6]", 3000) // tournament drilldown menu has at least 5 elements
            .waitForElementVisible("//div[@id='drilldowns']/a[6]", 3000) // tournament drilldown list has at least 5 elements
            .waitForElementVisible("//div[@id='blogs']/div[3]", 3000) // blogs has at least 3 elements
            .waitForElementVisible("//iframe[contains(@title,'Facebook Social Plugin')]", 3000) // facebook
            .waitForElementVisible("//iframe[contains(@class,'twitter-timeline')]", 3000) // facebook
            .useCss()
            .waitForElementVisible('iframe.iubenda-ibadge', 3000) // privacy policy
            .end();
    }
};