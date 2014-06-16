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

package org.wso2.carbon.samples;
import java.lang.String;
import org.wso2.carbon.samples.greetingService.greeting.*;
import org.apache.axis2.AxisFault;
import java.rmi.RemoteException;


public class GreetingServiceTestCase {


    public static void main(String[] args) {


 try {
            GreetingServiceStub greetingServiceStub = new GreetingServiceStub("http://localhost:9763/services/GreetingService");

            UserE userRequest = new UserE();
            User user = new User();
            user.setName("Ishara");
            User[] users = new User[1];
            users[0] = user;
            userRequest.setUser(users);

            GreetingMessage[] greetingMessages = greetingServiceStub.greetMe(users);
            String result = greetingMessages[0].getMessage();
            System.out.println("Greeting service results1 : " + result);


        } catch (AxisFault axisFault) {
            axisFault.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }


}
