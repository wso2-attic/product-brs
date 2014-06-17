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
import org.wso2.carbon.samples.test.healthCareService.patientDetail.Dose;
import org.wso2.carbon.samples.test.healthCareService.patientDetail.Patient;
import org.wso2.carbon.samples.test.healthCareService.patientDetail.PatientDetail;
import org.wso2.carbon.samples.test.healthCareService.stub.HealthCareServiceStub;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.rmi.RemoteException;

import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;


public class HealthCareServiceTestCase extends BrsMaterTestCase {

    private static final Log log = LogFactory.getLog(HealthCareServiceTestCase.class);


    @BeforeClass(groups = {"wso2.brs"})
    public void login() throws Exception {
        init();

    }

    @Test(groups = {"wso2.brs"})
    public void uploadHealthCareService() throws Exception {
        String samplesDir = System.getProperty("samples.dir");
        String HealthCareServiceAAR = samplesDir + File.separator + "healthcare.service/service/target/HealthCareService.aar";
        log.info(HealthCareServiceAAR);
        FileDataSource fileDataSource = new FileDataSource(HealthCareServiceAAR);
        DataHandler dataHandler = new DataHandler(fileDataSource);
        getRuleServiceFileUploadAdminStub().uploadService("HealthCareService.aar", dataHandler);

    }

    @Test(groups = {"wso2.brs"}, dependsOnMethods = {"uploadHealthCareService"})
    public void testHealthCare() throws XPathExpressionException {
        try {

            waitForProcessDeployment(getContext().getContextUrls().getServiceUrl() + "/HealthCareService");
            HealthCareServiceStub healthCareServiceStub =
                    new HealthCareServiceStub(getContext().getContextUrls().getServiceUrl() + "/HealthCareService");

            PatientDetail patientDetail = new PatientDetail();
            Patient patient = new Patient();
            patient.setAge(43);
            patient.setCreatinineLevel(1.0);
            patient.setMedication("Levofloxacin");

            Patient[] patients = new Patient[1];
            patients[0] = patient;
            patientDetail.setPatientDetail(patients);

            Dose[] doses = healthCareServiceStub.recommendDose(patients);
            String result = doses[0].getMessage();
            assertNotNull(result, "Result cannot be null");
            assertNotEquals(result, "");
        } catch (AxisFault axisFault) {
            axisFault.printStackTrace();
            assertNotNull(null);
        } catch (RemoteException e) {
            e.printStackTrace();
            assertNotNull(null);
        }
    }
}
