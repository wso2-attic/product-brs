<!--
 ~ Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 ~
 ~ WSO2 Inc. licenses this file to you under the Apache License,
 ~ Version 2.0 (the "License"); you may not use this file except
 ~ in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~    http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing,
 ~ software distributed under the License is distributed on an
 ~ "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 ~ KIND, either express or implied.  See the License for the
 ~ specific language governing permissions and limitations
 ~ under the License.
 -->
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="org.wso2.carbon.utils.ServerConstants" %>
<%@ page import="org.wso2.carbon.ui.CarbonUIUtil"%>
<jsp:include page="../dialog/display_messages.jsp"/>
<link href="../tenant-dashboard/css/dashboard-common.css" rel="stylesheet" type="text/css" media="all"/>
<%
        Object param = session.getAttribute("authenticated");
        String passwordExpires = (String) session.getAttribute(ServerConstants.PASSWORD_EXPIRATION);
        boolean hasPermission = CarbonUIUtil.isUserAuthorized(request,
            "/permission/admin/manage/add/service");
        boolean loggedIn = false;
        if (param != null) {
            loggedIn = (Boolean) param;             
        } 
%>
  
<div id="passwordExpire">
         <%
         if (loggedIn && passwordExpires != null) {
         %>
              <div class="info-box"><p>Your password expires at <%=passwordExpires%>. Please change by visiting <a href="../user/change-passwd.jsp?isUserChange=true&returnPath=../admin/index.jsp">here</a></p></div>
         <%
             }
         %>
</div>
<div id="middle">
<div id="workArea">

    <style type="text/css">
        .tip-table td.service-hosting {
            background-image: url(../../carbon/tenant-dashboard/images/service-hosting.png);
        }
        .tip-table td.registry-rule {
            background-image: url(../../carbon/tenant-dashboard/images/registry-rule.png);
        }
        .tip-table td.rule-service {
            background-image: url(../../carbon/tenant-dashboard/images/rule-service.png);
        }
        .tip-table td.service-testing {
            background-image: url(../../carbon/tenant-dashboard/images/service-testing.png);
        }



        .tip-table td.message-tracing {
            background-image: url(../../carbon/tenant-dashboard/images/message-tracing.png);
        }
        .tip-table td.wsdl2java {
            background-image: url(../../carbon/tenant-dashboard/images/wsdl2java.png);
        }
        .tip-table td.java2wsdl {
            background-image: url(../../carbon/tenant-dashboard/images/java2wsdl.png);
        }
        .tip-table td.wsdl-validator {
            background-image: url(../../carbon/tenant-dashboard/images/wsdl-validator.png);
        }
    </style>
    <h2 class="dashboard-title">WSO2 BRS quick start dashboard</h2>
    <table class="tip-table">
        <tr>
            <td class="tip-top service-hosting"></td>
            <td class="tip-empty"></td>
            <td class="tip-top registry-rule"></td>
            <td class="tip-empty "></td>
            <td class="tip-top rule-service"></td>
            <td class="tip-empty "></td>
            <td class="tip-top service-testing"></td>
        </tr>
        <tr>
            <td class="tip-content">
                <div class="tip-content-lifter">
                    <%
                        if (hasPermission) {
                    %>
                    <a class="tip-title" href="../service-mgt/index.jsp?region=region1&item=services_list_menu">Service
                        Hosting Support</a> <br/>
                    <%
                    } else {
                    %>
                    <h3 class="tip-title">Service Hosting Support</h3><br/>
                    <%
                        }
                    %>

                    <p>Service Hosting feature in Business Rules Server enables deployment of Rule Services.</p>

                </div>
            </td>
            <td class="tip-empty"></td>
            <td class="tip-content">
                <div class="tip-content-lifter">

                    <%
                        if (hasPermission) {
                    %>
                    <a class="tip-title"
                       href="../ruleservices/rule_service_wizard_step1.jsp?region=region1&item=rule_services_create_menu">Rule
                        service creation support</a><br/>
                    <%
                    } else {
                    %>
                    <h3 class="tip-title">Rule service creation support</h3><br/>
                    <%
                        }
                    %>

                    <p>Support for creating rule services with drools or using rule services created with other IDEs</p>

                </div>
            </td>
            <td class="tip-empty"></td>
            <td class="tip-content">
                <div class="tip-content-lifter">
                    <%
                        if (hasPermission) {
                    %>
                    <a class="tip-title" href="../resources/resource.jsp?region=region3&item=resource_browser_menu">Registry
                        as a Rule Repository</a> <br/>
                    <%
                    } else {
                    %>
                    <h3 class="tip-title">Registry as a Rule Repository</h3><br/>
                    <%
                        }
                    %>

                    <p>Resource ownerships and ability to sharing, tagging, commenting and rating.</p>

                </div>
            </td>
            <td class="tip-empty"></td>
            <td class="tip-content">
                <div class="tip-content-lifter">
                    <%
                        if (hasPermission) {
                    %>
                    <a class="tip-title" href="../tryit/index.jsp?region=region5&item=tryit">Service Testing</a><br/>

                    <%
                    } else {
                    %>
                    <h3 class="tip-title">Service Testing</h3><br/>
                    <%
                        }
                    %>


                    <p>Try it tool can be used as a simple Web Service client which can be used to try your
                        services within BRS itself.</p>

                </div>
            </td>
        </tr>
        <tr>
            <td class="tip-bottom"></td>
            <td class="tip-empty"></td>
            <td class="tip-bottom"></td>
            <td class="tip-empty"></td>
            <td class="tip-bottom"></td>
        </tr>
    </table>
    <div class="tip-table-div"></div>
<p>
    <br/>
</p> </div>
</div>
