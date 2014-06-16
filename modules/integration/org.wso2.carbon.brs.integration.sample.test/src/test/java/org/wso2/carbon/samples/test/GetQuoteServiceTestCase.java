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
import org.wso2.carbon.samples.test.quoteService.customerDetail.Customer;
import org.wso2.carbon.samples.test.quoteService.customerDetail.PlaceCustomerDetail;
import org.wso2.carbon.samples.test.quoteService.customerDetail.Quotation;
import org.wso2.carbon.samples.test.quoteService.stub.GetQuoteServiceStub;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.rmi.RemoteException;

import static org.testng.Assert.assertNotNull;

public class GetQuoteServiceTestCase extends BrsMaterTestCase {

    private static final Log log = LogFactory.getLog(GetQuoteServiceTestCase.class);

    @BeforeClass(groups = {"wso2.brs"})
    public void login() throws Exception {
        init();

    }

    @Test(groups = {"wso2.brs"})
    public void uploadGetQuoteService() throws Exception {
        String samplesDir = System.getProperty("samples.dir");
        String GetQuoteServiceAAR = samplesDir + File.separator + "quotation.service/service/target/GetQuoteService.aar";
        log.info(GetQuoteServiceAAR);
        FileDataSource fileDataSource = new FileDataSource(GetQuoteServiceAAR);
        DataHandler dataHandler = new DataHandler(fileDataSource);
        getRuleServiceFileUploadAdminStub().uploadService("GetQuoteService.aar", dataHandler);

    }

    @Test(groups = {"wso2.brs"}, dependsOnMethods = {"uploadGetQuoteService"})
    public void testGetQuote() throws XPathExpressionException {

        waitForProcessDeployment(getContext().getContextUrls().getServiceUrl() + "/GetQuoteService");
        try {
            GetQuoteServiceStub getQuoteServiceStub = new GetQuoteServiceStub(getContext().getContextUrls().getServiceUrl() + "/GetQuoteService");

            PlaceCustomerDetail placeCustomerDetail = new PlaceCustomerDetail();
            Customer customer = new Customer();
            customer.setStatus("gold");
            Customer[] customers = new Customer[1];
            customers[0] = customer;
            placeCustomerDetail.setCustomerDetail(customers);

            Quotation[] quotations = getQuoteServiceStub.getQuote(customers);
            int result = quotations[0].getPrice();
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
