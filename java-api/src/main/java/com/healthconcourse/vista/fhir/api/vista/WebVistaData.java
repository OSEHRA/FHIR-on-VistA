/* Created by Perspecta http://www.perspecta.com */
/*
        Licensed to the Apache Software Foundation (ASF) under one
        or more contributor license agreements.  See the NOTICE file
        distributed with this work for additional information
        regarding copyright ownership.  The ASF licenses this file
        to you under the Apache License, Version 2.0 (the
        "License"); you may not use this file except in compliance
        with the License.  You may obtain a copy of the License at
        http://www.apache.org/licenses/LICENSE-2.0
        Unless required by applicable law or agreed to in writing,
        software distributed under the License is distributed on an
        "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
        KIND, either express or implied.  See the License for the
        specific language governing permissions and limitations
        under the License.
*/
package com.healthconcourse.vista.fhir.api.vista;

import okhttp3.*;
import org.hl7.fhir.dstu3.model.Condition;
import org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * Service which communicates with Vista over HTTP
 **/

public class WebVistaData implements VistaData {

    private static final Logger LOG = LoggerFactory.getLogger(WebVistaData.class);
    private SimpleDateFormat vistaDateFormat = new SimpleDateFormat("yyyyMMdd");
    private final String mVistaBaseUrl;
    private final OkHttpClient mClient;
    private static final MediaType MEDIA_TYPE_TEXTPLAIN = MediaType.parse("text/plain; charset=utf-8");
    private static final String ICN = "ICN";

    public WebVistaData(String baseUrl) {

        this.mVistaBaseUrl = baseUrl;

        this.mClient = new OkHttpClient.Builder()
                .readTimeout(45, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public String getPatientData(String icn) {
        return queryVista("DHPPATDEMICN", createMapForSingleParameter(icn, true));
    }

    @Override
    public String getConditions(String name, String ssn, Date dob, AdministrativeGender gender) {

        return queryVista("DHPPATCON", createMapForSearchParams(name, ssn, dob, gender));
    }

    @Override
    public String getConditions(String icn) {

        return queryVista("DHPPATCONICN", createMapForSingleParameter(icn, true));
    }

    @Override
    public String getPatientsByCondition(String snomedCode) {

        HashMap<String, String> map = new HashMap<>();
        map.put("SCT", snomedCode);

        return queryVista("DHPPATS4CON", map);
    }

    @Override
    public String putTheCondition(Condition theCondition) {

        LinkedHashMap<String, String> map = createMapForCondition(theCondition);

        return postVista("DHPPATPRBUPD", map);
    }

    @Override
    public String getVitalsObservationsByIcn(String icn) {

        return queryVista("DHPPATVITICN", createMapForSingleParameter(icn, true));

    }

    @Override
    public String getObservationsByCriteria(String name, String ssn, Date dob, AdministrativeGender gender) {

        return queryVista("DHPPATVIT", createMapForSearchParams(name, ssn, dob, gender));
    }

    @Override
    public String getEncountersByPatient(String icn) {

        //Start never got coded on the Vista side

        return queryVista("DHPPATENCICN", createMapForSingleParameter(icn, true));
    }

    @Override
    public String getMedicationStatement(String icn) {

        return queryVista("DHPPATMEDSICN", createMapForSingleParameter(icn, true));

    }

    @Override
    public String getMedicationDispense(String icn) {

        return queryVista("DHPPATMEDAICN", createMapForSingleParameter(icn, true));

    }

    @Override
    public String getMedicationAdministration(String icn) {

        return queryVista("DHPPATMEDAICN", createMapForSingleParameter(icn, true));

    }

    @Override
    public String getProceduresByIcn(String icn) {

        return queryVista("DHPPATPRCICN", createMapForSingleParameter(icn, true));
    }

    @Override
    public String getLabObservationsByIcn(String icn) {

        return queryVista("DHPPATLABICN", createMapForSingleParameter(icn, true));
    }

    @Override
    public String getHealthFactorObservationsByIcn(String icn) {

        return queryVista("DHPPATHLFICN", createMapForSingleParameter(icn, true));
    }

    @Override
    public String getMentalHealthObservationsByIcn(String icn) {

        return queryVista("DHPPATOBSICN", createMapForSingleParameter(icn, true));
    }

    @Override
    public String getProvidersByIcn(String icn) {

        return queryVista("DHPPATPRVICN", createMapForSingleParameter(icn, true));
    }

    @Override
    public String getLocationByName(String name) {

        return queryVista("DHPHLOCINSTHLOCNAM", createMapForSingleParameter(name, "HLOC", true));
    }

    @Override
    public String getFlagByIcn(String icn) {
        return queryVista("DHPPATFLGICN", createMapForSingleParameter(icn, true));
    }

    @Override
    public String getAppointmentsByIcn(String icn) {
        return queryVista("DHPPATAPTICN", createMapForSingleParameter(icn, true));
    }

    @Override
    public String getAllergiesByIcn(String icn) {
        return queryVista("DHPPATALLICN", createMapForSingleParameter(icn, true));
    }

    @Override
    public String getImmunizationsByIcn(String icn) {
        return queryVista("DHPPATIMMICN", createMapForSingleParameter(icn, true));
    }

    @Override
    public String getGoal(String icn) {
        return queryVista("DHPPATGOLICN", createMapForSingleParameter(icn, true));
    }

    @Override
    public String getDiagnosticReport(String icn) {
        return queryVista("DHPPATDXRICN", createMapForSingleParameter(icn, true));
    }

    @Override
    public String getCarePlan(String icn) {
        return queryVista("DHPPATCPALLI", createMapForSingleParameter(icn, true));
    }

    @Override
    public String getAllPatients() {

        return queryVista("DHPPATDEMALL", createMapForSingleParameter("ALL"));
    }

    @Override
    public String getTiuNotes(String icn) {

        return queryVista("DHPPATTIUICN", createMapForSingleParameter(icn, true));
    }

    @Override
    public String getAllCareTeams() {
        return queryVista("DHPCARETEAMS", createMapForSingleParameter("J", "JSON"));
    }

    @Override
    public String getCareTeamByHame(String name) {
        return queryVista("DHPCARETEAM", createMapForSingleParameter(name, "TEAM", true));
    }

    @Override
    public String getPractitionerById(String id) {
        return queryVista("DHPGETRESID", createMapForSingleParameter(id, "RESID", false));
    }

    private String queryVista(String path, Map<String, String> parameters) {
        try {
            Request request = new Request.Builder()
                    .url(createUrl(path, parameters))
                    .build();
            okhttp3.Response response = mClient.newCall(request).execute();

            return processResponse(response);

        } catch (IOException e) {
            LOG.error("Error calling Vista", e);
            return "";
        }
    }


    private String postVista(String path, Map<String, String> parameters) {
        String resp = "???^Unknown error";
        try {
            String parms = createStringFromMap(parameters);
            RequestBody body = RequestBody.create(MEDIA_TYPE_TEXTPLAIN, parms);

            Request request = new Request.Builder()
                    .url(createUrl(path, null))
                    .post(body)
                    .build();

            Response response = mClient.newCall(request).execute();
            if (response != null) {
                resp = Integer.toString(response.code()) + "^" + response.message();
                if (response.code() == 200 || response.code() == 201) {
                    resp = response.body().string();
                    resp = Integer.toString(response.code()) + "^" + "Success";
                }
            }
        } catch (IOException e) {
            LOG.error("Error calling Vista", e);
        }
        return resp;
    }

    private String createStringFromMap(Map<String, String> parameters) {
        StringBuilder stringBuilder = new StringBuilder();

        if (parameters != null && !parameters.isEmpty()) {

            int length = parameters.size();
            int count = 0;

            for (Map.Entry<String, String> item : parameters.entrySet()) {
                count++;
                stringBuilder.append(item.getKey());
                stringBuilder.append(":");
                stringBuilder.append(item.getValue());
                if (count < length) {
                    stringBuilder.append(",");
                }
            }
        }

        return stringBuilder.toString();
    }

    private String createUrl(String path, Map<String, String> parameters) {

        HttpUrl.Builder urlBuilder = HttpUrl.parse(String.format("%s%s", this.mVistaBaseUrl, path)).newBuilder();

        if (parameters != null && !parameters.isEmpty()) {

            for (Map.Entry<String, String> item : parameters.entrySet()) {
                urlBuilder.addQueryParameter(item.getKey(), item.getValue());
            }
        }

        return urlBuilder.build().toString();
    }

    private HashMap<String, String> createMapForSearchParams(String name, String ssn, Date dob, AdministrativeGender gender) {

        HashMap<String, String> map = new HashMap<>();
        map.put("NAME", name);
        map.put("SSN", ssn);
        map.put("DOB", vistaDateFormat.format(dob));
        map.put("GENDER", VistaHelper.convertAdministrativeGender(gender));

        return map;
    }

    private static HashMap<String, String> createMapForSingleParameter(String value, boolean requestJson) {
        return createMapForSingleParameter(value, ICN, requestJson);
    }

    private static HashMap<String, String> createMapForSingleParameter(String value, String name) {
        return createMapForSingleParameter(value, name, false);
    }

    private static HashMap<String, String> createMapForSingleParameter(String value, String name, boolean requestJson) {

        HashMap<String, String> map = new HashMap<>();
        map.put(name, value);

        if (requestJson) {
            map.put("JSON", "J");
        }

        return map;
    }

    private static HashMap<String, String> createMapForSingleParameter(String icn) {

        return createMapForSingleParameter(icn, ICN);
    }

    private static LinkedHashMap<String, String> createMapForCondition(Condition theCondition) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();

        //mec... yoyo... TODO... breakdown theCondition
        //"ICN":"5482156687V807096","SCT":"95668009","DES":"158482014","ROV":"9990006675","DTM":"20171002-0700","RID":"500_EXT"
        String ICN = theCondition.getSubject().getReference().replace("Patient/", ""); // mec... bag the "Patient/"
        String SCT = theCondition.getCode().getCoding().get(0).getCode(); // "95668009";
        String DES = ""; // theCondition.getCode().getCoding().get(0).getDisplay(); // "158482014";
        String ROV = ""; // "9990006675";
        String DTM = getVistaFormattedDate(theCondition.getOnset().primitiveValue()); // "20171002-0700";
        String RID = ""; //"422"; //mec... "500_EXT";

        //mec... yoyo... TODO... BAG the hardcode 
        //"ICN":"5482156687V807096","SCT":"95668009","DES":"158482014","ROV":"9990006675","DTM":"20171002-0700","RID":"500_EXT"
        ICN = "5482156687V807096"; //mec... yoyo... fix errored hardcode
        //ROV = "9990006675"; //mec... BAG HARDCODE
        //SCT = "95668009";

        map.put("ICN", ICN);
        map.put("SCT", SCT);
        map.put("DES", DES);
        map.put("ROV", ROV);
        map.put("DTM", DTM);
        map.put("RID", RID);

        return map;
    }

    private static String getVistaFormattedDate(String dateString) {
        String formattedDate = "";
        try {
            formattedDate = dateString.substring(0, 4) +
                    dateString.substring(5, 7) +
                    dateString.substring(8, 10) +
                    "-" +
                    dateString.substring(dateString.length() - 5, dateString.length() - 3) +
                    dateString.substring(dateString.length() - 2);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return formattedDate;
    }

    private static String processResponse(okhttp3.Response response) {
        int status = response.code();
        String body = "";

        try {
            if (response.body() != null) {
                body = response.body().string();
            }
        } catch (IOException e) {
            LOG.error("Unable to fetch body", e);
            return body;
        }

        if (status != 200) {
            String err = "";
            try {
                err = (response.request().url().toString());
            } catch (Exception ex) {
                err = ex.getMessage();
            }
            LOG.error("Bad Response from VistA request (" + err + ")");
            LOG.error("HTTP Status: " + status);
            LOG.error(body);
            body = "";
        }

        return body;
    }
}
