/*
*  Licensed to the Apache Software Foundation (ASF) under one
*  or more contributor license agreements.  See the NOTICE file
*  distributed with this work for additional information
*  regarding copyright ownership.  The ASF licenses this file
*  to you under the Apache License, Version 2.0 (the
*  "License"); you may not use this file except in compliance
*  with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*   * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*/
package org.wso2.carbon.samples.test;

import junit.framework.Assert;
import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.FrameworkConstants.*;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.test.api.clients.rule.RuleServiceFileUploadAdminClient;
import org.wso2.carbon.rule.ws.stub.fileupload.RuleServiceFileUploadAdminStub;
import org.wso2.carbon.utils.FileManipulator;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Scanner;

/**
 * Created by wso2 on 4/2/14.
 */
public class BrsMaterTestCase {

    private AutomationContext context;
    private RuleServiceFileUploadAdminStub ruleServiceFileUploadAdminStub;
    private ServiceClient client;
    public static final Log log = LogFactory.getLog(BrsMaterTestCase.class);

    public void init() throws XPathExpressionException, RemoteException, LoginAuthenticationExceptionException {
        context = new AutomationContext("BRS", TestUserMode.SUPER_TENANT_ADMIN);
        String loggedInSessionCookie = context.login();
        ruleServiceFileUploadAdminStub =
                new RuleServiceFileUploadAdminStub(context.getContextUrls().getSecureServiceUrl() +"/RuleServiceFileUploadAdmin");
        client = ruleServiceFileUploadAdminStub._getServiceClient();
        Options options = client.getOptions();
        options.setManageSession(true);
        options.setProperty(org.apache.axis2.transport.http.HTTPConstants.COOKIE_STRING,
                loggedInSessionCookie);
    }


    public static void waitForProcessDeployment(String serviceUrl) {
        int serviceTimeOut = 0;
        while (!isServiceAvailable(serviceUrl)) {
            //TODO - this looping is only happening for 14 times. Need to find the exact cause for this.
            if (serviceTimeOut == 0) {
                log.info("Waiting for the " + serviceUrl + ".");
            } else if (serviceTimeOut > 10) {
                log.error("Time out");
                Assert.fail(serviceUrl + " service is not found");
                break;
            }
            try {
                Thread.sleep(5000);
                serviceTimeOut++;
            } catch (InterruptedException e) {
                Assert.fail(e.getMessage());
            }
        }
    }


    public static boolean isServiceAvailable(String serviceUrl) {
        URL wsdlURL;
        InputStream wsdlIS = null;
        try {
            wsdlURL = new URL(serviceUrl + "?wsdl");

            try {
                wsdlIS = wsdlURL.openStream();
            } catch (IOException e) {
                return false;// do nothing, wait for the service
            }

            if (wsdlIS != null) {

                String wsdlContent = convertStreamToString(wsdlIS);
                if (wsdlContent.indexOf("definitions") > 0) {
                    return true;
                }
                return false;
            }
            return false;


        } catch (MalformedURLException e) {
            return false;
        } finally {
            if (wsdlIS != null) {
                try {
                    wsdlIS.close();
                } catch (IOException e) {
                    log.error("Error occurred when closing the wsdl input stream");
                }
            }
        }

    }

    public static String convertStreamToString(InputStream is) {
        return new Scanner(is).useDelimiter("\\A").next();
    }

    public AutomationContext getContext() {
        return context;
    }

    public ServiceClient getClient() {
        return client;
    }

    public RuleServiceFileUploadAdminStub getRuleServiceFileUploadAdminStub() {
        return ruleServiceFileUploadAdminStub;
    }
}
