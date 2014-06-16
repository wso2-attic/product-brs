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

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.samples.test.mipCalculateService.clientDetail.Client;
import org.wso2.carbon.samples.test.mipCalculateService.clientDetail.MIP;
import org.wso2.carbon.samples.test.mipCalculateService.clientDetail.PlaceClientDetail;
import org.wso2.carbon.samples.test.mipCalculateService.stub.MIPCalculateServiceStub;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.rmi.RemoteException;

import static org.testng.Assert.assertNotNull;

public class MIPCalculateServiceTestCase extends BrsMaterTestCase {

    private static final Log log = LogFactory.getLog(MIPCalculateServiceTestCase.class);


    @BeforeClass(groups = {"wso2.brs"})
    public void login() throws Exception {
        init();

    }

    @Test(groups = {"wso2.brs"})
    public void uploadMIPCalculateService() throws Exception {
        String samplesDir = System.getProperty("samples.dir");
        String MIPCalculateServiceAAR = samplesDir + File.separator + "MIPCalculate.service/service/target/MIPCalculateService.aar";
        log.info(MIPCalculateServiceAAR);
        FileDataSource fileDataSource = new FileDataSource(MIPCalculateServiceAAR);
        DataHandler dataHandler = new DataHandler(fileDataSource);
        getRuleServiceFileUploadAdminStub().uploadService("MIPCalculateService.aar", dataHandler);

    }

    @Test(groups = {"wso2.brs"}, dependsOnMethods = {"uploadMIPCalculateService"})
    public void testMIPCalculate() throws XPathExpressionException {

        waitForProcessDeployment(getContext().getContextUrls().getServiceUrl() + "/MIPCalculateService");
        try {

            MIPCalculateServiceStub mipCalculateServiceStub =
                    new MIPCalculateServiceStub(getContext().getContextUrls().getServiceUrl() + "/MIPCalculateService");

            PlaceClientDetail placeClientDetail = new PlaceClientDetail();
            Client client = new Client();
            client.setLoanType("FHA");
            client.setDownPayment(8000);
            client.setMortgageValue(90000);
            Client[] clients = new Client[1];
            clients[0] = client;

            MIP[] mips = mipCalculateServiceStub.calculate(clients);
            double result = mips[0].getAnnualMIP();
            assertNotNull(result, "Result cannot be null");
        } catch (AxisFault axisFault) {
            axisFault.printStackTrace();
            assertNotNull(null);
        } catch (RemoteException e) {
            e.printStackTrace();
            assertNotNull(null);
        }
    }
}
