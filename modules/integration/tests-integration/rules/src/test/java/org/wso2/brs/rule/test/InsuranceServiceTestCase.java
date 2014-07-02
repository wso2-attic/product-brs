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

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.brs.integration.common.utils.BRSMasterTest;
import org.wso2.brs.integration.common.utils.RequestSender;
import org.wso2.carbon.samples.test.insuranceService.insurance.ApplyForInsurance;
import org.wso2.carbon.samples.test.insuranceService.insurance.Car;
import org.wso2.carbon.samples.test.insuranceService.insurance.Driver;
import org.wso2.carbon.samples.test.insuranceService.insurance.Policy;
import org.wso2.carbon.samples.test.insuranceService.stub.InsuranceServiceStub;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayInputStream;
import java.io.File;

import static org.testng.Assert.assertNotNull;

public class InsuranceServiceTestCase extends BRSMasterTest {

    private static final Log log = LogFactory.getLog(InsuranceServiceTestCase.class);
    ServiceClient serviceClient;
    RequestSender requestSender;

    @BeforeClass(groups = {"wso2.brs"})
    public void login() throws Exception {
        init();
        requestSender = new RequestSender();
    }

    @Test(groups = {"wso2.brs"})
    public void uploadInsuranceService() throws Exception {
        String samplesDir = System.getProperty("samples.dir");
        String InsuranceServiceAAR = samplesDir + File.separator + "insurance.service/service/target/InsuranceService.aar";
        log.info(InsuranceServiceAAR);
        FileDataSource fileDataSource = new FileDataSource(InsuranceServiceAAR);
        DataHandler dataHandler = new DataHandler(fileDataSource);
        getRuleServiceFileUploadClient().uploadService("InsuranceService.aar", dataHandler);

    }

    @Test(groups = {"wso2.brs"}, dependsOnMethods = {"uploadInsuranceService"})
    public void testApplyForInsurance() throws Exception {

        requestSender.waitForProcessDeployment(getContext().getContextUrls().getServiceUrl() + "/InsuranceService");
        serviceClient = new ServiceClient();
        Options options = new Options();
        options.setTo(new EndpointReference(getContext().getContextUrls().getServiceUrl() + "/InsuranceService"));
        options.setAction("urn:applyForInsurance");
        serviceClient.setOptions(options);

        OMElement result = serviceClient.sendReceive(createPayload());
        assertNotNull(result, "Result cannot be null");
    }

    private OMElement createPayload() throws XMLStreamException {
        String request = "<p:applyForInsuranceRequest xmlns:p=\"http://brs.carbon.wso2.org\">\n" +
                "   <!--Zero or more repetitions:-->\n" +
                "   <p:Car>\n" +
                "      <!--Zero or 1 repetitions:-->\n" +
                "      <xs:color xmlns:xs=\"http://insurance.brs/xsd\">red</xs:color>\n" +
                "      <!--Zero or 1 repetitions:-->\n" +
                "      <xs:style xmlns:xs=\"http://insurance.brs/xsd\">sport</xs:style>\n" +
                "   </p:Car>\n" +
                "   <!--Zero or more repetitions:-->\n" +
                "   <p:Driver>\n" +
                "      <!--Zero or 1 repetitions:-->\n" +
                "      <xs:age xmlns:xs=\"http://insurance.brs/xsd\">20</xs:age>\n" +
                "      <!--Zero or 1 repetitions:-->\n" +
                "      <xs:male xmlns:xs=\"http://insurance.brs/xsd\">true</xs:male>\n" +
                "      <!--Zero or 1 repetitions:-->\n" +
                "      <xs:name xmlns:xs=\"http://insurance.brs/xsd\">shammi</xs:name>\n" +
                "   </p:Driver>\n" +
                "</p:applyForInsuranceRequest>";
        return new StAXOMBuilder(new ByteArrayInputStream(request.getBytes())).getDocumentElement();
    }

    @Test(groups = {"wso2.brs"}, dependsOnMethods = {"uploadInsuranceService"})
    public void testInsuranceStub() throws Exception {

        requestSender.waitForProcessDeployment(getContext().getContextUrls().getServiceUrl() + "/InsuranceService");
        InsuranceServiceStub insuranceServiceStub =
                new InsuranceServiceStub(getContext().getContextUrls().getServiceUrl() + "/InsuranceService");

        ApplyForInsurance applyForInsurance = new ApplyForInsurance();

        Car car = new Car();
        car.setColor("red");
        car.setStyle("sport");
        Car[] cars = new Car[1];
        cars[0] = car;

        Driver driver = new Driver();
        driver.setName("Waruna");
        driver.setAge(24);
        driver.setMale(true);
        Driver[] drivers = new Driver[1];
        drivers[0] = driver;

        applyForInsurance.setCarInsurance(cars);
        applyForInsurance.setDriverInsurance(drivers);


        Policy[] policies = insuranceServiceStub.applyForInsurance(cars, drivers);
        double result = policies[0].getPremium();
        assertNotNull(result, "Result cannot be null");
        System.out.println("Premium:" + result);


    }
}
