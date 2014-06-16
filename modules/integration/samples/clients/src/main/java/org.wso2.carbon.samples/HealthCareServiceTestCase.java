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
package org.wso2.carbon.samples;
import org.apache.axis2.AxisFault;
import org.wso2.carbon.samples.healthCareService.patientDetail.Dose;
import org.wso2.carbon.samples.healthCareService.patientDetail.Patient;
import org.wso2.carbon.samples.healthCareService.patientDetail.PatientDetail;
import org.wso2.carbon.samples.healthCareService.stub.HealthCareServiceStub;

import java.rmi.RemoteException;

public class HealthCareServiceTestCase {

    public static void main(String[] args) {

        try {
            HealthCareServiceStub healthCareServiceStub =
                    new HealthCareServiceStub("http://localhost:9763/services/HealthCareService");

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
            System.out.println(result);

        } catch (AxisFault axisFault) {
            axisFault.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
