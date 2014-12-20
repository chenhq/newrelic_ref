/*     */ package com.newrelic.agent.android.harvest;
/*     */ 
/*     */ import com.newrelic.agent.android.activity.config.ActivityTraceConfiguration;
/*     */ import java.util.Arrays;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ 
/*     */ public class HarvestConfiguration
/*     */ {
/*     */   private static final int DEFAULT_ACTIVITY_TRACE_LENGTH = 65534;
/*     */   private static final int DEFAULT_ACTIVITY_TRACE_MAX_REPORT_ATTEMPTS = 1;
/*     */   private static final int DEFAULT_REPORT_PERIOD = 60;
/*     */   private static final int DEFAULT_ERROR_LIMIT = 50;
/*     */   private static final int DEFAULT_RESPONSE_BODY_LIMIT = 2048;
/*     */   private static final int DEFAULT_STACK_TRACE_LIMIT = 100;
/*     */   private static final int DEFAULT_MAX_TRANSACTION_AGE = 600;
/*     */   private static final int DEFAULT_MAX_TRANSACTION_COUNT = 1000;
/*     */   private static final float DEFAULT_ACTIVITY_TRACE_MIN_UTILIZATION = 0.3F;
/*     */   private boolean collect_network_errors;
/*     */   private String cross_process_id;
/*     */   private int data_report_period;
/*     */   private int[] data_token;
/*     */   private int error_limit;
/*     */   private int report_max_transaction_age;
/*     */   private int report_max_transaction_count;
/*     */   private int response_body_limit;
/*     */   private long server_timestamp;
/*     */   private int stack_trace_limit;
/*     */   private int activity_trace_max_size;
/*     */   private int activity_trace_max_report_attempts;
/*     */   private double activity_trace_min_utilization;
/*     */   private ActivityTraceConfiguration at_capture;
/*     */   private static HarvestConfiguration defaultHarvestConfiguration;
/*     */ 
/*     */   public HarvestConfiguration()
/*     */   {
/*  40 */     setDefaultValues();
/*     */   }
/*     */ 
/*     */   public void setDefaultValues() {
/*  44 */     setData_token(new int[2]);
/*  45 */     setCollect_network_errors(true);
/*  46 */     setData_report_period(60);
/*  47 */     setError_limit(50);
/*  48 */     setResponse_body_limit(2048);
/*  49 */     setStack_trace_limit(100);
/*  50 */     setReport_max_transaction_age(600);
/*  51 */     setReport_max_transaction_count(1000);
/*  52 */     setActivity_trace_max_size(65534);
/*  53 */     setActivity_trace_max_report_attempts(1);
/*  54 */     setActivity_trace_min_utilization(0.300000011920929D);
/*  55 */     setAt_capture(ActivityTraceConfiguration.defaultActivityTraceConfiguration());
/*     */   }
/*     */ 
/*     */   public static HarvestConfiguration getDefaultHarvestConfiguration() {
/*  59 */     if (defaultHarvestConfiguration != null)
/*  60 */       return defaultHarvestConfiguration;
/*  61 */     defaultHarvestConfiguration = new HarvestConfiguration();
/*  62 */     return defaultHarvestConfiguration;
/*     */   }
/*     */ 
/*     */   public void reconfigure(HarvestConfiguration configuration) {
/*  66 */     setCollect_network_errors(configuration.isCollect_network_errors());
/*     */ 
/*  68 */     if (configuration.getCross_process_id() != null) {
/*  69 */       setCross_process_id(configuration.getCross_process_id());
/*     */     }
/*  71 */     setData_report_period(configuration.getData_report_period());
/*     */ 
/*  73 */     if (configuration.getDataToken().isValid()) {
/*  74 */       setData_token(configuration.getData_token());
/*     */     }
/*  76 */     setError_limit(configuration.getError_limit());
/*  77 */     setReport_max_transaction_age(configuration.getReport_max_transaction_age());
/*  78 */     setReport_max_transaction_count(configuration.getReport_max_transaction_count());
/*  79 */     setResponse_body_limit(configuration.getResponse_body_limit());
/*  80 */     setServer_timestamp(configuration.getServer_timestamp());
/*  81 */     setStack_trace_limit(configuration.getStack_trace_limit());
/*  82 */     setActivity_trace_min_utilization(configuration.getActivity_trace_min_utilization());
/*     */ 
/*  84 */     setActivity_trace_max_report_attempts(configuration.getActivity_trace_max_report_attempts());
/*  85 */     if (configuration.getAt_capture() != null)
/*  86 */       setAt_capture(configuration.getAt_capture());
/*     */   }
/*     */ 
/*     */   public void setCollect_network_errors(boolean collect_network_errors) {
/*  90 */     this.collect_network_errors = collect_network_errors;
/*     */   }
/*     */ 
/*     */   public void setCross_process_id(String cross_process_id) {
/*  94 */     this.cross_process_id = cross_process_id;
/*     */   }
/*     */ 
/*     */   public void setData_report_period(int data_report_period) {
/*  98 */     this.data_report_period = data_report_period;
/*     */   }
/*     */ 
/*     */   public void setData_token(int[] data_token) {
/* 102 */     this.data_token = data_token;
/*     */   }
/*     */ 
/*     */   public DataToken getDataToken() {
/* 106 */     if (this.data_token == null)
/* 107 */       return null;
/* 108 */     return new DataToken(this.data_token[0], this.data_token[1]);
/*     */   }
/*     */ 
/*     */   public void setError_limit(int error_limit) {
/* 112 */     this.error_limit = error_limit;
/*     */   }
/*     */ 
/*     */   public void setReport_max_transaction_age(int report_max_transaction_age) {
/* 116 */     this.report_max_transaction_age = report_max_transaction_age;
/*     */   }
/*     */ 
/*     */   public void setReport_max_transaction_count(int report_max_transaction_count) {
/* 120 */     this.report_max_transaction_count = report_max_transaction_count;
/*     */   }
/*     */ 
/*     */   public void setResponse_body_limit(int response_body_limit) {
/* 124 */     this.response_body_limit = response_body_limit;
/*     */   }
/*     */ 
/*     */   public void setServer_timestamp(long server_timestamp) {
/* 128 */     this.server_timestamp = server_timestamp;
/*     */   }
/*     */ 
/*     */   public void setStack_trace_limit(int stack_trace_limit) {
/* 132 */     this.stack_trace_limit = stack_trace_limit;
/*     */   }
/*     */ 
/*     */   public void setActivity_trace_max_size(int activity_trace_max_size) {
/* 136 */     this.activity_trace_max_size = activity_trace_max_size;
/*     */   }
/*     */ 
/*     */   public void setActivity_trace_max_report_attempts(int activity_trace_max_report_attempts) {
/* 140 */     this.activity_trace_max_report_attempts = activity_trace_max_report_attempts;
/*     */   }
/*     */ 
/*     */   public boolean isCollect_network_errors() {
/* 144 */     return this.collect_network_errors;
/*     */   }
/*     */ 
/*     */   public String getCross_process_id() {
/* 148 */     return this.cross_process_id;
/*     */   }
/*     */ 
/*     */   public int getData_report_period() {
/* 152 */     return this.data_report_period;
/*     */   }
/*     */ 
/*     */   public int[] getData_token() {
/* 156 */     return this.data_token;
/*     */   }
/*     */ 
/*     */   public int getError_limit() {
/* 160 */     return this.error_limit;
/*     */   }
/*     */ 
/*     */   public int getReport_max_transaction_age() {
/* 164 */     return this.report_max_transaction_age;
/*     */   }
/*     */ 
/*     */   public long getReportMaxTransactionAgeMilliseconds() {
/* 168 */     return TimeUnit.MILLISECONDS.convert(this.report_max_transaction_age, TimeUnit.SECONDS);
/*     */   }
/*     */ 
/*     */   public int getReport_max_transaction_count() {
/* 172 */     return this.report_max_transaction_count;
/*     */   }
/*     */ 
/*     */   public int getResponse_body_limit() {
/* 176 */     return this.response_body_limit;
/*     */   }
/*     */ 
/*     */   public long getServer_timestamp() {
/* 180 */     return this.server_timestamp;
/*     */   }
/*     */ 
/*     */   public int getStack_trace_limit() {
/* 184 */     return this.stack_trace_limit;
/*     */   }
/*     */ 
/*     */   public int getActivity_trace_max_size() {
/* 188 */     return this.activity_trace_max_size;
/*     */   }
/*     */ 
/*     */   public int getActivity_trace_max_report_attempts() {
/* 192 */     return this.activity_trace_max_report_attempts;
/*     */   }
/*     */ 
/*     */   public ActivityTraceConfiguration getAt_capture() {
/* 196 */     return this.at_capture;
/*     */   }
/*     */ 
/*     */   public void setAt_capture(ActivityTraceConfiguration at_capture) {
/* 200 */     this.at_capture = at_capture;
/*     */   }
/*     */ 
/*     */   public double getActivity_trace_min_utilization() {
/* 204 */     return this.activity_trace_min_utilization;
/*     */   }
/*     */ 
/*     */   public void setActivity_trace_min_utilization(double activity_trace_min_utilization) {
/* 208 */     this.activity_trace_min_utilization = activity_trace_min_utilization;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 213 */     if (this == o) return true;
/* 214 */     if ((o == null) || (getClass() != o.getClass())) return false;
/*     */ 
/* 216 */     HarvestConfiguration that = (HarvestConfiguration)o;
/*     */ 
/* 218 */     if (this.collect_network_errors != that.collect_network_errors) return false;
/* 219 */     if (this.data_report_period != that.data_report_period) return false;
/* 220 */     if (this.error_limit != that.error_limit) return false;
/* 221 */     if (this.report_max_transaction_age != that.report_max_transaction_age) return false;
/* 222 */     if (this.report_max_transaction_count != that.report_max_transaction_count) return false;
/* 223 */     if (this.response_body_limit != that.response_body_limit) return false;
/* 224 */     if (this.stack_trace_limit != that.stack_trace_limit) return false;
/* 225 */     if (this.activity_trace_max_size != that.activity_trace_max_size) return false;
/* 226 */     if (this.activity_trace_max_report_attempts != that.activity_trace_max_report_attempts) return false;
/* 227 */     if ((this.cross_process_id == null) && (that.cross_process_id != null)) return false;
/* 228 */     if ((this.cross_process_id != null) && (that.cross_process_id == null)) return false;
/* 229 */     if ((this.cross_process_id != null) && (!this.cross_process_id.equals(that.cross_process_id))) return false;
/*     */ 
/* 232 */     int thisMinUtil = (int)this.activity_trace_min_utilization * 100;
/* 233 */     int thatMinUtil = (int)that.activity_trace_min_utilization * 100;
/* 234 */     if (thisMinUtil != thatMinUtil) return false;
/*     */ 
/* 237 */     boolean dataTokenEqual = Arrays.equals(this.data_token, that.data_token);
/* 238 */     return dataTokenEqual;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 246 */     int result = this.collect_network_errors ? 1 : 0;
/* 247 */     result = 31 * result + (this.cross_process_id != null ? this.cross_process_id.hashCode() : 0);
/* 248 */     result = 31 * result + this.data_report_period;
/* 249 */     result = 31 * result + (this.data_token != null ? Arrays.hashCode(this.data_token) : 0);
/* 250 */     result = 31 * result + this.error_limit;
/* 251 */     result = 31 * result + this.report_max_transaction_age;
/* 252 */     result = 31 * result + this.report_max_transaction_count;
/* 253 */     result = 31 * result + this.response_body_limit;
/* 254 */     result = 31 * result + this.stack_trace_limit;
/* 255 */     result = 31 * result + this.activity_trace_max_size;
/* 256 */     result = 31 * result + this.activity_trace_max_report_attempts;
/* 257 */     long temp = Double.doubleToLongBits(this.activity_trace_min_utilization);
/* 258 */     result = 31 * result + (int)(temp ^ temp >>> 32);
/* 259 */     result = 31 * result + (this.at_capture != null ? this.at_capture.hashCode() : 0);
/* 260 */     return result;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 265 */     return "HarvestConfiguration{collect_network_errors=" + this.collect_network_errors + ", cross_process_id='" + this.cross_process_id + '\'' + ", data_report_period=" + this.data_report_period + ", data_token=" + Arrays.toString(this.data_token) + ", error_limit=" + this.error_limit + ", report_max_transaction_age=" + this.report_max_transaction_age + ", report_max_transaction_count=" + this.report_max_transaction_count + ", response_body_limit=" + this.response_body_limit + ", server_timestamp=" + this.server_timestamp + ", stack_trace_limit=" + this.stack_trace_limit + ", activity_trace_max_size=" + this.activity_trace_max_size + ", activity_trace_max_report_attempts=" + this.activity_trace_max_report_attempts + ", activity_trace_min_utilization=" + this.activity_trace_min_utilization + ", at_capture=" + this.at_capture + '}';
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.harvest.HarvestConfiguration
 * JD-Core Version:    0.6.2
 */