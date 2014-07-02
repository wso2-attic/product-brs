/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/
package org.wso2.brs.integration.common.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.brs.integration.common.clients.RuleServiceFileUploadClient;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.admin.client.SecurityAdminServiceClient;
import org.wso2.carbon.integration.common.utils.LoginLogoutClient;

import javax.xml.xpath.XPathExpressionException;

public class BRSMasterTest {
    private static final Log log = LogFactory.getLog(BRSMasterTest.class);
    protected AutomationContext brsServer;
    protected String sessionCookie = null;
    protected String backEndUrl = null;
    protected String serviceUrl = null;
    protected SecurityAdminServiceClient securityAdminServiceClient;
    protected LoginLogoutClient loginLogoutClient;
    protected RuleServiceFileUploadClient ruleServiceFileUploadClient;

    public void init(TestUserMode testUserMode) throws Exception {
        brsServer = new AutomationContext("BRS", testUserMode);
        loginLogoutClient = new LoginLogoutClient(brsServer);
        sessionCookie = loginLogoutClient.login();
        backEndUrl = brsServer.getContextUrls().getBackEndUrl();
        serviceUrl = brsServer.getContextUrls().getServiceUrl();
        ruleServiceFileUploadClient = new RuleServiceFileUploadClient(backEndUrl, sessionCookie);
    }

    protected void init() throws Exception {
        brsServer = new AutomationContext("BRS", TestUserMode.SUPER_TENANT_ADMIN);
        loginLogoutClient = new LoginLogoutClient(brsServer);
        sessionCookie = loginLogoutClient.login();
        backEndUrl = brsServer.getContextUrls().getBackEndUrl();
        serviceUrl = brsServer.getContextUrls().getServiceUrl();
        ruleServiceFileUploadClient = new RuleServiceFileUploadClient(backEndUrl, sessionCookie);
    }

    protected void init(String domainKey, String userKey) throws Exception {
        brsServer = new AutomationContext("BRS", "brsServerInstance0001", domainKey, userKey);
        loginLogoutClient = new LoginLogoutClient(brsServer);
        sessionCookie = loginLogoutClient.login();
        backEndUrl = brsServer.getContextUrls().getBackEndUrl();
        serviceUrl = brsServer.getContextUrls().getServiceUrl();
        ruleServiceFileUploadClient = new RuleServiceFileUploadClient(backEndUrl, sessionCookie);
    }

    protected String getServiceUrl(String serviceName) throws XPathExpressionException {
        return brsServer.getContextUrls().getServiceUrl() + "/" + serviceName;
    }

    public RuleServiceFileUploadClient getRuleServiceFileUploadClient() {
        return ruleServiceFileUploadClient;
    }

    public void setRuleServiceFileUploadClient(RuleServiceFileUploadClient ruleServiceFileUploadClient) {
        this.ruleServiceFileUploadClient = ruleServiceFileUploadClient;
    }

    public AutomationContext getContext() {
        return brsServer;
    }
}
