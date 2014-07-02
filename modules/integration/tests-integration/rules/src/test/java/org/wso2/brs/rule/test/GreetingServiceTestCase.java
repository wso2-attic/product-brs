/*
*  Copyright (c) WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.brs.rule.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.brs.integration.common.utils.BRSMasterTest;
import org.wso2.brs.integration.common.utils.RequestSender;
import org.wso2.carbon.samples.test.greetingService.greeting.GreetingMessage;
import org.wso2.carbon.samples.test.greetingService.greeting.GreetingServiceStub;
import org.wso2.carbon.samples.test.greetingService.greeting.User;
import org.wso2.carbon.samples.test.greetingService.greeting.UserE;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import java.io.File;

import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;

public class GreetingServiceTestCase extends BRSMasterTest {

    private static final Log log = LogFactory.getLog(GreetingServiceTestCase.class);
    RequestSender requestSender;

    @BeforeClass(groups = {"wso2.brs"})
    public void login() throws Exception {
        init();
        requestSender = new RequestSender();
    }

    @Test(groups = {"wso2.brs"})
    public void uploadGreetingService() throws Exception {
        String samplesDir = System.getProperty("samples.dir");
        String greetingServiceAAR = samplesDir + File.separator + "greeting.service/service/target/GreetingService.aar";
        log.info(greetingServiceAAR);
        FileDataSource fileDataSource = new FileDataSource(greetingServiceAAR);
        DataHandler dataHandler = new DataHandler(fileDataSource);
        getRuleServiceFileUploadClient().uploadService("GreetingService.aar", dataHandler);
    }

    @Test(groups = {"wso2.brs"}, dependsOnMethods = {"uploadGreetingService"})
    public void callGreet() throws Exception {

        String result = "";
        requestSender.waitForProcessDeployment(getContext().getContextUrls().getServiceUrl() + "/GreetingService");

        GreetingServiceStub greetingServiceStub = new GreetingServiceStub(getContext().getContextUrls().getServiceUrl() + "/GreetingService");
        UserE userRequest = new UserE();
        User user = new User();
        user.setName("Waruna");
        User[] users = new User[1];
        users[0] = user;
        userRequest.setUser(users);

        GreetingMessage[] greetingMessages = greetingServiceStub.greetMe(users);
        result = greetingMessages[0].getMessage();
        System.out.println("Greeting service results1 : " + result);


        System.out.println("Greeting service results2 : " + result);
        assertNotNull(result, "Result cannot be null");
        assertNotEquals(result, "");


    }

    @AfterClass(groups = {"wso2.brs"})
    public void logout() throws Exception {

    }


}
