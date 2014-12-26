// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Proguard.java

package com.newrelic.agent.Obfuscation;

import com.newrelic.agent.compile.Log;
import com.newrelic.agent.compile.RewriterAgent;
import com.newrelic.agent.compile.visitor.NewRelicClassVisitor;
import com.newrelic.com.google.common.io.BaseEncoding;
import com.newrelic.org.apache.commons.io.FileUtils;
import com.newrelic.org.apache.commons.io.filefilter.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class Proguard
{

    public Proguard(Log log)
    {
        licenseKey = null;
        uploadingEnabled = true;
        mappingApiHost = null;
        this.log = log;
    }

    public void findAndSendMapFile()
    {
        String mappingString = "";
        if(getProjectRoot() != null)
        {
            if(!fetchConfiguration())
                return;
            File projectRoot = new File(getProjectRoot());
            IOFileFilter fileFilter = FileFilterUtils.nameFileFilter("mapping.txt");
            Collection files = FileUtils.listFiles(projectRoot, fileFilter, TrueFileFilter.INSTANCE);
            if(files.isEmpty())
            {
                log.error("While evidence of ProGuard was detected, New Relic failed to find your mapping.txt file.");
                log.error("To de-obfuscate your builds, you'll need to upload your mapping.txt manually.");
            }
            for(Iterator i$ = files.iterator(); i$.hasNext();)
            {
                File file = (File)i$.next();
                log.info((new StringBuilder()).append("Found mapping.txt: ").append(file.getPath()).toString());
                try
                {
                    FileWriter fileWriter = new FileWriter(file, true);
                    fileWriter.write((new StringBuilder()).append("# NR_BUILD_ID: ").append(NewRelicClassVisitor.getBuildId()).toString());
                    fileWriter.close();
                    mappingString = (new StringBuilder()).append(mappingString).append(FileUtils.readFileToString(file)).toString();
                }
                catch(FileNotFoundException e)
                {
                    log.error((new StringBuilder()).append("Unable to open your mapping.txt file: ").append(e.getLocalizedMessage()).toString());
                    log.error("To de-obfuscate your builds, you'll need to upload your mapping.txt manually.");
                }
                catch(IOException e)
                {
                    log.error((new StringBuilder()).append("Unable to open your mapping.txt file: ").append(e.getLocalizedMessage()).toString());
                    log.error("To de-obfuscate your builds, you'll need to upload your mapping.txt manually.");
                }
            }

            if(uploadingEnabled)
                sendMapping(mappingString);
        }
    }

    private String getProjectRoot()
    {
        if(projectRoot == null)
        {
            String encodedProjectRoot = (String)RewriterAgent.getAgentOptions().get("projectRoot");
            if(encodedProjectRoot == null)
            {
                log.info("Unable to determine project root, falling back to CWD.");
                projectRoot = System.getProperty("user.dir");
            } else
            {
                projectRoot = new String(BaseEncoding.base64().decode(encodedProjectRoot));
                log.info((new StringBuilder()).append("Project root: ").append(projectRoot).toString());
            }
        }
        return projectRoot;
    }

    private boolean fetchConfiguration()
    {
        Reader propsReader;
        propsReader = new BufferedReader(new FileReader((new StringBuilder()).append(getProjectRoot()).append(File.separator).append("newrelic.properties").toString()));
        Properties newRelicProps = new Properties();
        newRelicProps.load(propsReader);
        licenseKey = newRelicProps.getProperty("com.newrelic.application_token");
        uploadingEnabled = newRelicProps.getProperty("com.newrelic.enable_proguard_upload", "true").equals("true");
        mappingApiHost = newRelicProps.getProperty("com.newrelic.mapping_upload_host");
        if(licenseKey != null)
            break MISSING_BLOCK_LABEL_124;
        log.error("Unable to find a value for com.newrelic.application_token in your newrelic.properties");
        log.error("To de-obfuscate your builds, you'll need to upload your mapping.txt manually.");
        return false;
        try
        {
            propsReader.close();
        }
        catch(FileNotFoundException e)
        {
            log.error((new StringBuilder()).append("Unable to find your newrelic.properties in the project root (").append(getProjectRoot()).append("): ").append(e.getLocalizedMessage()).toString());
            log.error("To de-obfuscate your builds, you'll need to upload your mapping.txt manually.");
            return false;
        }
        catch(IOException e)
        {
            log.error((new StringBuilder()).append("Unable to read your newrelic.properties in the project root (").append(getProjectRoot()).append("): ").append(e.getLocalizedMessage()).toString());
            log.error("To de-obfuscate your builds, you'll need to upload your mapping.txt manually.");
            return false;
        }
        return true;
    }

    private void sendMapping(String mapping)
    {
        StringBuilder requestBody = new StringBuilder();
        requestBody.append((new StringBuilder()).append("proguard=").append(URLEncoder.encode(mapping)).toString());
        requestBody.append((new StringBuilder()).append("&buildId=").append(NewRelicClassVisitor.getBuildId()).toString());
        try
        {
            String host = "mobile-symbol-upload.newrelic.com";
            if(mappingApiHost != null)
                host = mappingApiHost;
            URL url = new URL((new StringBuilder()).append("https://").append(host).append("/symbol").toString());
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("X-APP-LICENSE-KEY", licenseKey);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", Integer.toString(requestBody.length()));
            DataOutputStream request = new DataOutputStream(connection.getOutputStream());
            request.writeBytes(requestBody.toString());
            request.close();
            int responseCode = connection.getResponseCode();
            if(responseCode == 400)
            {
                InputStream inputStream = connection.getErrorStream();
                String response = convertStreamToString(inputStream);
                log.error((new StringBuilder()).append("Unable to send your ProGuard mapping.txt to New Relic as the params are incorrect: ").append(response).toString());
                log.error("To de-obfuscate your builds, you'll need to upload your mapping.txt manually.");
            } else
            if(responseCode > 400)
            {
                InputStream inputStream = connection.getErrorStream();
                String response = convertStreamToString(inputStream);
                log.error((new StringBuilder()).append("Unable to send your ProGuard mapping.txt to New Relic - received status ").append(responseCode).append(": ").append(response).toString());
                log.error("To de-obfuscate your builds, you'll need to upload your mapping.txt manually.");
            }
            log.info("Successfully sent mapping.txt to New Relic.");
            connection.disconnect();
        }
        catch(IOException e)
        {
            log.error("Encountered an error while uploading your ProGuard mapping to New Relic", e);
            log.error("To de-obfuscate your builds, you'll need to upload your mapping.txt manually.");
        }
    }

    private static String convertStreamToString(InputStream is)
    {
        BufferedReader reader;
        StringBuilder sb;
        String line;
        reader = new BufferedReader(new InputStreamReader(is));
        sb = new StringBuilder();
        line = null;
        while((line = reader.readLine()) != null) 
            sb.append((new StringBuilder()).append(line).append("\n").toString());
        IOException e;
        try
        {
            is.close();
        }
        // Misplaced declaration of an exception variable
        catch(IOException e)
        {
            e.printStackTrace();
        }
        break MISSING_BLOCK_LABEL_123;
        e;
        e.printStackTrace();
        try
        {
            is.close();
        }
        // Misplaced declaration of an exception variable
        catch(IOException e)
        {
            e.printStackTrace();
        }
        break MISSING_BLOCK_LABEL_123;
        Exception exception;
        exception;
        try
        {
            is.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        throw exception;
        return sb.toString();
    }

    private static final String NR_PROPERTIES = "newrelic.properties";
    private static final String PROP_NR_APP_TOKEN = "com.newrelic.application_token";
    private static final String PROP_UPLOADING_ENABLED = "com.newrelic.enable_proguard_upload";
    private static final String PROP_MAPPING_API_HOST = "com.newrelic.mapping_upload_host";
    private static final String MAPPING_FILENAME = "mapping.txt";
    private static final String DEFAULT_MAPPING_API_HOST = "mobile-symbol-upload.newrelic.com";
    private static final String MAPPING_API_PATH = "/symbol";
    private static final String LICENSE_KEY_HEADER = "X-APP-LICENSE-KEY";
    private final Log log;
    private String projectRoot;
    private String licenseKey;
    private boolean uploadingEnabled;
    private String mappingApiHost;
}
