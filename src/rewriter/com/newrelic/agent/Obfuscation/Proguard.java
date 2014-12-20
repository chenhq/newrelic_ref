/*     */ package com.newrelic.agent.Obfuscation;
/*     */ 
/*     */ import com.newrelic.agent.compile.Log;
/*     */ import com.newrelic.agent.compile.RewriterAgent;
/*     */ import com.newrelic.agent.compile.visitor.NewRelicClassVisitor;
/*     */ import com.newrelic.com.google.common.io.BaseEncoding;
/*     */ import com.newrelic.org.apache.commons.io.FileUtils;
/*     */ import com.newrelic.org.apache.commons.io.filefilter.FileFilterUtils;
/*     */ import com.newrelic.org.apache.commons.io.filefilter.IOFileFilter;
/*     */ import com.newrelic.org.apache.commons.io.filefilter.TrueFileFilter;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileReader;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.Reader;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.net.URL;
/*     */ import java.net.URLEncoder;
/*     */ import java.util.Collection;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ 
/*     */ public class Proguard
/*     */ {
/*     */   private static final String NR_PROPERTIES = "newrelic.properties";
/*     */   private static final String PROP_NR_APP_TOKEN = "com.newrelic.application_token";
/*     */   private static final String PROP_UPLOADING_ENABLED = "com.newrelic.enable_proguard_upload";
/*     */   private static final String PROP_MAPPING_API_HOST = "com.newrelic.mapping_upload_host";
/*     */   private static final String MAPPING_FILENAME = "mapping.txt";
/*     */   private static final String DEFAULT_MAPPING_API_HOST = "mobile-symbol-upload.newrelic.com";
/*     */   private static final String MAPPING_API_PATH = "/symbol";
/*     */   private static final String LICENSE_KEY_HEADER = "X-APP-LICENSE-KEY";
/*     */   private final Log log;
/*     */   private String projectRoot;
/*  36 */   private String licenseKey = null;
/*  37 */   private boolean uploadingEnabled = true;
/*  38 */   private String mappingApiHost = null;
/*     */ 
/*     */   public Proguard(Log log) {
/*  41 */     this.log = log;
/*     */   }
/*     */ 
/*     */   public void findAndSendMapFile() {
/*  45 */     String mappingString = "";
/*     */ 
/*  47 */     if (getProjectRoot() != null) {
/*  48 */       if (!fetchConfiguration()) {
/*  49 */         return;
/*     */       }
/*     */ 
/*  52 */       File projectRoot = new File(getProjectRoot());
/*  53 */       IOFileFilter fileFilter = FileFilterUtils.nameFileFilter("mapping.txt");
/*  54 */       Collection files = FileUtils.listFiles(projectRoot, fileFilter, TrueFileFilter.INSTANCE);
/*     */ 
/*  56 */       if (files.isEmpty()) {
/*  57 */         this.log.error("While evidence of ProGuard was detected, New Relic failed to find your mapping.txt file.");
/*  58 */         this.log.error("To de-obfuscate your builds, you'll need to upload your mapping.txt manually.");
/*     */       }
/*     */ 
/*  61 */       for (File file : files) {
/*  62 */         this.log.info("Found mapping.txt: " + file.getPath());
/*     */         try
/*     */         {
/*  66 */           FileWriter fileWriter = new FileWriter(file, true);
/*  67 */           fileWriter.write("# NR_BUILD_ID: " + NewRelicClassVisitor.getBuildId());
/*  68 */           fileWriter.close();
/*     */ 
/*  70 */           mappingString = mappingString + FileUtils.readFileToString(file);
/*     */         } catch (FileNotFoundException e) {
/*  72 */           this.log.error("Unable to open your mapping.txt file: " + e.getLocalizedMessage());
/*  73 */           this.log.error("To de-obfuscate your builds, you'll need to upload your mapping.txt manually.");
/*     */         } catch (IOException e) {
/*  75 */           this.log.error("Unable to open your mapping.txt file: " + e.getLocalizedMessage());
/*  76 */           this.log.error("To de-obfuscate your builds, you'll need to upload your mapping.txt manually.");
/*     */         }
/*     */       }
/*     */ 
/*  80 */       if (this.uploadingEnabled)
/*  81 */         sendMapping(mappingString);
/*     */     }
/*     */   }
/*     */ 
/*     */   private String getProjectRoot()
/*     */   {
/*  87 */     if (this.projectRoot == null) {
/*  88 */       String encodedProjectRoot = (String)RewriterAgent.getAgentOptions().get("projectRoot");
/*     */ 
/*  90 */       if (encodedProjectRoot == null)
/*     */       {
/*  92 */         this.log.info("Unable to determine project root, falling back to CWD.");
/*  93 */         this.projectRoot = System.getProperty("user.dir");
/*     */       } else {
/*  95 */         this.projectRoot = new String(BaseEncoding.base64().decode(encodedProjectRoot));
/*  96 */         this.log.info("Project root: " + this.projectRoot);
/*     */       }
/*     */     }
/*     */ 
/* 100 */     return this.projectRoot;
/*     */   }
/*     */ 
/*     */   private boolean fetchConfiguration() {
/*     */     try {
/* 105 */       Reader propsReader = new BufferedReader(new FileReader(getProjectRoot() + File.separator + "newrelic.properties"));
/* 106 */       Properties newRelicProps = new Properties();
/* 107 */       newRelicProps.load(propsReader);
/*     */ 
/* 109 */       this.licenseKey = newRelicProps.getProperty("com.newrelic.application_token");
/* 110 */       this.uploadingEnabled = newRelicProps.getProperty("com.newrelic.enable_proguard_upload", "true").equals("true");
/* 111 */       this.mappingApiHost = newRelicProps.getProperty("com.newrelic.mapping_upload_host");
/*     */ 
/* 113 */       if (this.licenseKey == null) {
/* 114 */         this.log.error("Unable to find a value for com.newrelic.application_token in your newrelic.properties");
/* 115 */         this.log.error("To de-obfuscate your builds, you'll need to upload your mapping.txt manually.");
/*     */ 
/* 117 */         return false;
/*     */       }
/*     */ 
/* 120 */       propsReader.close();
/*     */     } catch (FileNotFoundException e) {
/* 122 */       this.log.error("Unable to find your newrelic.properties in the project root (" + getProjectRoot() + "): " + e.getLocalizedMessage());
/* 123 */       this.log.error("To de-obfuscate your builds, you'll need to upload your mapping.txt manually.");
/*     */ 
/* 125 */       return false;
/*     */     } catch (IOException e) {
/* 127 */       this.log.error("Unable to read your newrelic.properties in the project root (" + getProjectRoot() + "): " + e.getLocalizedMessage());
/* 128 */       this.log.error("To de-obfuscate your builds, you'll need to upload your mapping.txt manually.");
/*     */ 
/* 130 */       return false;
/*     */     }
/*     */ 
/* 133 */     return true;
/*     */   }
/*     */ 
/*     */   private void sendMapping(String mapping)
/*     */   {
/* 138 */     StringBuilder requestBody = new StringBuilder();
/*     */ 
/* 140 */     requestBody.append("proguard=" + URLEncoder.encode(mapping));
/* 141 */     requestBody.append("&buildId=" + NewRelicClassVisitor.getBuildId());
/*     */     try
/*     */     {
/* 144 */       String host = "mobile-symbol-upload.newrelic.com";
/* 145 */       if (this.mappingApiHost != null) {
/* 146 */         host = this.mappingApiHost;
/*     */       }
/*     */ 
/* 149 */       URL url = new URL("https://" + host + "/symbol");
/* 150 */       HttpURLConnection connection = (HttpURLConnection)url.openConnection();
/*     */ 
/* 152 */       connection.setUseCaches(false);
/* 153 */       connection.setDoOutput(true);
/* 154 */       connection.setRequestMethod("POST");
/* 155 */       connection.setRequestProperty("X-APP-LICENSE-KEY", this.licenseKey);
/* 156 */       connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
/* 157 */       connection.setRequestProperty("Content-Length", Integer.toString(requestBody.length()));
/*     */ 
/* 159 */       DataOutputStream request = new DataOutputStream(connection.getOutputStream());
/* 160 */       request.writeBytes(requestBody.toString());
/* 161 */       request.close();
/*     */ 
/* 163 */       int responseCode = connection.getResponseCode();
/* 164 */       if (responseCode == 400) {
/* 165 */         InputStream inputStream = connection.getErrorStream();
/* 166 */         String response = convertStreamToString(inputStream);
/* 167 */         this.log.error("Unable to send your ProGuard mapping.txt to New Relic as the params are incorrect: " + response);
/* 168 */         this.log.error("To de-obfuscate your builds, you'll need to upload your mapping.txt manually.");
/* 169 */       } else if (responseCode > 400) {
/* 170 */         InputStream inputStream = connection.getErrorStream();
/* 171 */         String response = convertStreamToString(inputStream);
/* 172 */         this.log.error("Unable to send your ProGuard mapping.txt to New Relic - received status " + responseCode + ": " + response);
/* 173 */         this.log.error("To de-obfuscate your builds, you'll need to upload your mapping.txt manually.");
/*     */       }
/*     */ 
/* 176 */       this.log.info("Successfully sent mapping.txt to New Relic.");
/*     */ 
/* 178 */       connection.disconnect();
/*     */     } catch (IOException e) {
/* 180 */       this.log.error("Encountered an error while uploading your ProGuard mapping to New Relic", e);
/* 181 */       this.log.error("To de-obfuscate your builds, you'll need to upload your mapping.txt manually.");
/*     */     }
/*     */   }
/*     */ 
/*     */   private static String convertStreamToString(InputStream is) {
/* 186 */     BufferedReader reader = new BufferedReader(new InputStreamReader(is));
/* 187 */     StringBuilder sb = new StringBuilder();
/*     */ 
/* 189 */     String line = null;
/*     */     try {
/* 191 */       while ((line = reader.readLine()) != null)
/* 192 */         sb.append(line + "\n");
/*     */     }
/*     */     catch (IOException e) {
/* 195 */       e.printStackTrace();
/*     */     } finally {
/*     */       try {
/* 198 */         is.close();
/*     */       } catch (IOException e) {
/* 200 */         e.printStackTrace();
/*     */       }
/*     */     }
/* 203 */     return sb.toString();
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.agent.Obfuscation.Proguard
 * JD-Core Version:    0.6.2
 */