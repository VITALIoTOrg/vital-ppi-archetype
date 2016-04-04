package eu.vital.ppi.services;

import eu.vital.ppi.clients.IoTSystemClient;
import eu.vital.ppi.jsonpojos.DulHasLocation;
import eu.vital.ppi.jsonpojos.EmptyRequest;
import eu.vital.ppi.jsonpojos.HasLastKnownLocation;
import eu.vital.ppi.jsonpojos.IdTypeRequest;
import eu.vital.ppi.jsonpojos.IoTSystem;
import eu.vital.ppi.jsonpojos.Measure;
import eu.vital.ppi.jsonpojos.Metric;
import eu.vital.ppi.jsonpojos.MetricRequest;
import eu.vital.ppi.jsonpojos.ObservationRequest;
import eu.vital.ppi.jsonpojos.Operation;
import eu.vital.ppi.jsonpojos.PerformanceMetric;
import eu.vital.ppi.jsonpojos.PerformanceMetricsMetadata;
import eu.vital.ppi.jsonpojos.Sensor;
import eu.vital.ppi.jsonpojos.SensorStatus;
import eu.vital.ppi.jsonpojos.Service;
import eu.vital.ppi.jsonpojos.SsnHasValue;
import eu.vital.ppi.jsonpojos.SsnHasValue_;
import eu.vital.ppi.jsonpojos.SsnHasValue__;
import eu.vital.ppi.jsonpojos.SsnObserf;
import eu.vital.ppi.jsonpojos.SsnObservationProperty;
import eu.vital.ppi.jsonpojos.SsnObservationProperty_;
import eu.vital.ppi.jsonpojos.SsnObservationProperty__;
import eu.vital.ppi.jsonpojos.SsnObservationResult;
import eu.vital.ppi.jsonpojos.SsnObservationResultTime;
import eu.vital.ppi.jsonpojos.SsnObservationResultTime_;
import eu.vital.ppi.jsonpojos.SsnObservationResultTime__;
import eu.vital.ppi.jsonpojos.SsnObservationResult_;
import eu.vital.ppi.jsonpojos.SsnObservationResult__;
import eu.vital.ppi.utils.JsonUtils;
import eu.vital.ppi.utils.StatCounter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

/*
 * PPI Class that provides all the REST API to expose for attachment to VITAL
 */

@Path("")
public class PPI {

    private Logger logger;
    private IoTSystemClient client;

    // VITAL ontology extended prefix
    private static final String ontologyPrefix = "http://vital-iot.eu/ontology/ns/";

    // To be able to return metadata even if the IoT system is unreachable
    private static HashMap<String, Object> cache;
    
    private static Date startupTime = new Date();

    public PPI() {
    	TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    	
        client = new IoTSystemClient();
        logger = LogManager.getLogger(PPI.class);

        if (cache == null) {
        	cache = new HashMap<String, Object>();
        }

        if (startupTime == null) {
        	startupTime = new Date();
        }
    }

	@Path("/metadata")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMetadata(String bodyRequest, @Context UriInfo uri) {
        int i;
        IoTSystem iotSystem;
        List<String> services;
        List<String> sensors;

        try {
            JsonUtils.deserializeJson(bodyRequest, EmptyRequest.class);
        } catch (IOException e) {
            logger.error("[/metadata] Error parsing request");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        /* TODO: Call the appropriate "client" function(s) */
        /* If you cannot get a response from the IoT system, check if you have
         * the data your need in "cache" and set status to unavailable */

        services = new ArrayList<String>();
        sensors = new ArrayList<String>();
        iotSystem = new IoTSystem();

        // TODO: fill in the iotSystem properties (with data from the "client" when needed)
        iotSystem.setContext("http://vital-iot.eu/contexts/system.jsonld");
        iotSystem.setId(uri.getBaseUri().toString().replaceAll("/$", ""));
        iotSystem.setType("vital:VitalSystem");
        iotSystem.setName("Awesome IoT system");
        iotSystem.setDescription("This is the best IoT system ever!");
        iotSystem.setOperator("http://example.com/people#john_doe");
        iotSystem.setServiceArea("http://dbpedia.org/page/Thebestcity");

        // TODO: add your IoT system sensors:
        // sensors.add(uri.getBaseUri() + "sensor/" + "asensorid");
        sensors.add(uri.getBaseUri() + "sensor/monitoring");
        iotSystem.setSensors(sensors);

        services.add(uri.getBaseUri() + "service/monitoring");
        services.add(uri.getBaseUri() + "service/observation");
        iotSystem.setServices(services);

        iotSystem.setSystems(null); // Unless this system has any subsystems

        try {
			return Response.status(Response.Status.OK)
				.entity(JsonUtils.serializeJson(iotSystem))
				.build();
		} catch (IOException e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
    }

    @Path("/system/performance")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSupportedPerformanceMetrics(@Context UriInfo uri) {
    	PerformanceMetricsMetadata performanceMetricsMetadata;
        List<Metric> list;
        Metric metric;

        performanceMetricsMetadata = new PerformanceMetricsMetadata();
        list = new ArrayList<Metric>();

        metric = new Metric();
        metric.setType(ontologyPrefix + "UsedMem");
        metric.setId(uri.getBaseUri() + "sensor/monitoring/usedMem");
        list.add(metric);

        metric = new Metric();
        metric.setType(ontologyPrefix + "AvailableMem");
        metric.setId(uri.getBaseUri() + "sensor/monitoring/availableMem");
        list.add(metric);

        metric = new Metric();
        metric.setType(ontologyPrefix + "AvailableDisk");
        metric.setId(uri.getBaseUri() + "sensor/monitoring/availableDisk");
        list.add(metric);

        metric = new Metric();
        metric.setType(ontologyPrefix + "SysLoad");
        metric.setId(uri.getBaseUri() + "sensor/monitoring/sysLoad");
        list.add(metric);

        metric = new Metric();
        metric.setType(ontologyPrefix + "ServedRequests");
        metric.setId(uri.getBaseUri() + "sensor/monitoring/servedRequests");
        list.add(metric);

        metric = new Metric();
        metric.setType(ontologyPrefix + "Errors");
        metric.setId(uri.getBaseUri() + "sensor/monitoring/errors");
        list.add(metric);

        metric = new Metric();
        metric.setType(ontologyPrefix + "SysUptime");
        metric.setId(uri.getBaseUri() + "sensor/monitoring/sysUptime");
        list.add(metric);

        metric = new Metric();
        metric.setType(ontologyPrefix + "PendingRequests");
        metric.setId(uri.getBaseUri() + "sensor/monitoring/pendingRequests");
        list.add(metric);

        performanceMetricsMetadata.setMetrics(list);

        try {
			return Response.status(Response.Status.OK)
				.entity(JsonUtils.serializeJson(performanceMetricsMetadata))
				.build();
		} catch (IOException e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
    }

    @Path("/system/performance")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPerformanceMetrics(String bodyRequest, @Context UriInfo uri) {
        List<String> requestedMetrics;
        List<PerformanceMetric> metrics;
        PerformanceMetric metric;
        Date date;
        Runtime runtime;
        SimpleDateFormat printedDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        String type, unit, value;

        try {
        	requestedMetrics = ((MetricRequest) JsonUtils.deserializeJson(bodyRequest, MetricRequest.class)).getMetric();
        } catch (IOException e) {
        	logger.error("[/system/performance] Error parsing request");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        date = new Date();
        runtime = Runtime.getRuntime();
        metrics = new ArrayList<PerformanceMetric>();
        for (String m : requestedMetrics) {
            if (m.contains("UsedMem")) {
            	type = "vital:UsedMem";
            	unit = "qudt:Byte";
            	value = Long.toString(runtime.totalMemory());
            } else if (m.contains("AvailableMem")) {
            	type = "vital:AvailableMem";
            	unit = "qudt:Byte";
            	value = Long.toString(runtime.freeMemory());
            } else if (m.contains("AvailableDisk")) {
            	type = "vital:AvailableDisk";
            	unit = "qudt:Byte";
            	value = Long.toString(new File("/").getFreeSpace());
            } else if (m.contains("SysLoad")) {
            	type = "vital:SysLoad";
            	unit = "qudt:Percentage";
            	try {
					value = Double.toString(getProcessCpuLoad());
				} catch (Exception e) {
					e.printStackTrace();
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
				}
            } else if (m.contains("ServedRequests")) {
            	type = "vital:ServedRequests";
            	unit = "qudt:Number";
            	value = Integer.toString(StatCounter.getRequestNumber().get());
            } else if (m.contains("Errors")) {
            	type = "vital:Errors";
            	unit = "qudt:Number";
            	value = Integer.toString(StatCounter.getErrorNumber().get());
            } else if (m.contains("SysUptime")) {
            	type = "vital:SysUptime";
            	unit = "qudt:MilliSecond";
            	value = Long.toString(date.getTime() - startupTime.getTime());
            } else if (m.contains("PendingRequests")) {
            	type = "vital:PendingRequests";
            	unit = "qudt:Number";
            	value = Integer.toString(StatCounter.getPendingRequest() - 1);
            } else {
            	logger.error("[/system/performance] Bad metric " + m);
                return Response.status(Response.Status.BAD_REQUEST).build();
            }

            metric = new PerformanceMetric();
            metric.setContext("http://vital-iot.eu/contexts/measurement.jsonld");
            metric.setId(uri.getBaseUri() + "sensor/monitoring/observation/" + Long.toHexString(date.getTime()));
            metric.setType("ssn:Observation");

            SsnObservationProperty_ ssnObservationProperty_ = new SsnObservationProperty_();
            ssnObservationProperty_.setType(type);
            metric.setSsnObservationProperty(ssnObservationProperty_);

            SsnObservationResultTime_ ssnObservationResultTime_ = new SsnObservationResultTime_();
            ssnObservationResultTime_.setTimeInXSDDateTime(printedDateFormat.format(date));
            metric.setSsnObservationResultTime(ssnObservationResultTime_);

            SsnObservationResult_ ssnObservationResult_ = new SsnObservationResult_();
            ssnObservationResult_.setType("ssn:SensorOutput");
            SsnHasValue_ ssnHasValue_ = new SsnHasValue_();
            ssnHasValue_.setType("ssn:ObservationValue");
            ssnHasValue_.setValue(value);
            ssnHasValue_.setQudtUnit(unit);
            ssnObservationResult_.setSsnHasValue(ssnHasValue_);
            metric.setSsnObservationResult(ssnObservationResult_);

            metric.setSsnFeatureOfInterest(uri.getBaseUri().toString().replaceAll("/$", ""));
            metrics.add(metric);
        }

        try {
			return Response.status(Response.Status.OK)
				.entity(JsonUtils.serializeJson(metrics))
				.build();
		} catch (IOException e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
    }

    @Path("/system/status")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSystemStatus(@Context UriInfo uri) {
    	Date now;
        SsnHasValue_ ssnHasValue_;
        PerformanceMetric lifecycleInformation;

        // TODO: call appropriate "client" method to check the IoT system status
        lifecycleInformation = new PerformanceMetric();
        ssnHasValue_ = new SsnHasValue_();
        ssnHasValue_.setValue("vital:Running");
        // or: ssnHasValue_.setValue("vital:Unavailable");

        now = new Date();
        SimpleDateFormat printedDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

        lifecycleInformation.setContext("http://vital-iot.eu/contexts/measurement.jsonld");
        lifecycleInformation.setId(uri.getBaseUri() + "sensor/monitoring/observation/" + Long.toHexString(now.getTime()));
        lifecycleInformation.setType("ssn:Observation");

        SsnObservationProperty_ ssnObservationProperty_ = new SsnObservationProperty_();
        ssnObservationProperty_.setType("vital:OperationalState");
        lifecycleInformation.setSsnObservationProperty(ssnObservationProperty_);

        SsnObservationResultTime_ ssnObservationResultTime_ = new SsnObservationResultTime_();

        ssnObservationResultTime_.setTimeInXSDDateTime(printedDateFormat.format(now));
        lifecycleInformation.setSsnObservationResultTime(ssnObservationResultTime_);

        SsnObservationResult_ ssnObservationResult_ = new SsnObservationResult_();
        ssnObservationResult_.setType("ssn:SensorOutput");
        
        ssnHasValue_.setType("ssn:ObservationValue");
        ssnObservationResult_.setSsnHasValue(ssnHasValue_);
        lifecycleInformation.setSsnObservationResult(ssnObservationResult_);

        lifecycleInformation.setSsnFeatureOfInterest(uri.getBaseUri().toString().replaceAll("/$", ""));

        try {
			return Response.status(Response.Status.OK)
				.entity(JsonUtils.serializeJson(lifecycleInformation))
				.build();
		} catch (IOException e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
    }

    @Path("/service/metadata")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getServiceMetadata(String bodyRequest, @Context UriInfo uri) {
        IdTypeRequest serviceRequest;
        Service tmpService;
        List<Service> services;

        try {
        	serviceRequest = (IdTypeRequest) JsonUtils.deserializeJson(bodyRequest, IdTypeRequest.class);
        } catch (IOException e) {
        	logger.error("[/service/metadata] Error parsing request");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        
        services = new ArrayList<Service>();

        if ((serviceRequest.getId().size() == 0) && (serviceRequest.getType().size() == 0)) {
            services.add(createObservationService(null, uri));
            services.add(createMonitoringService(null, uri));
        } else {
            for (String type : serviceRequest.getType()) {
                if (type.contains("ObservationService")) {
                	services.add(createObservationService(null, uri));
                }
                else if (type.contains("MonitoringService")) {
                    services.add(createMonitoringService(null, uri));
                }
            }
            for (String id : serviceRequest.getId()) {
                if (id.contains("observation")) {
                    tmpService = createObservationService(null, uri);
                    if (!services.contains(tmpService)) {
                        services.add(tmpService);
                    }
                }
                else if (id.contains("monitoring")) {
                    tmpService = createMonitoringService(null, uri);
                    if (!services.contains(tmpService)) {
                        services.add(tmpService);
                    }
                }
            }
        }

        try {
			return Response.status(Response.Status.OK)
				.entity(JsonUtils.serializeJson(services))
				.build();
		} catch (IOException e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
    }

    @Path("/sensor/metadata")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSensorMetadata(String bodyRequest, @Context UriInfo uri) {
        IdTypeRequest sensorRequest;
        Sensor tmpSensor;
        List<Sensor> sensors;

        try {
        	sensorRequest = (IdTypeRequest) JsonUtils.deserializeJson(bodyRequest, IdTypeRequest.class);
        } catch (IOException e) {
        	logger.error("[/sensor/metadata] Error parsing request");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        // TODO: call the appropriate "client" method(s) and use "cache" if needed

        sensors = new ArrayList<Sensor>();

        if ((sensorRequest.getId().size() == 0) && (sensorRequest.getType().size() == 0)) {
            // TODO: add IoT system sensors
            sensors.add(createMonitoringSensor(null, uri));
        } else {
            for (String type : sensorRequest.getType()) {
                if (type.contains("VitalSensor")) {
                	// TODO: add IoT system sensors
                }
                else if (type.contains("MonitoringSensor")) {
                	sensors.add(createMonitoringSensor(null, uri));
                }
            }
            for (String id : sensorRequest.getId()) {
            	if (id.contains("monitoring")) {
                    tmpSensor = createMonitoringSensor(null, uri);
                    if (!sensors.contains(tmpSensor)) {
                    	sensors.add(tmpSensor);
                    }
                } else {
                	/* TODO: loop over the IoT system sensors and if you find the one
                       corresponding to "id" add it if not already included */
                }
            }
        }

        try {
			return Response.status(Response.Status.OK)
				.entity(JsonUtils.serializeJson(sensors))
				.build();
		} catch (IOException e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
    }

    @Path("/sensor/status")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSensorStatus(String bodyRequest, @Context UriInfo uri) {
    	IdTypeRequest sensorRequest;
        SensorStatus tmpSensor;
        List<SensorStatus> sensorsStatus;

        try {
        	sensorRequest = (IdTypeRequest) JsonUtils.deserializeJson(bodyRequest, IdTypeRequest.class);
        } catch (IOException e) {
        	logger.error("[/sensor/status] Error parsing request");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        // TODO: call the appropriate "client" method(s) and use "cache" if needed

        sensorsStatus = new ArrayList<SensorStatus>();

        if ((sensorRequest.getId().size() == 0) && (sensorRequest.getType().size() == 0)) {
            // TODO: add IoT system sensors status objects
            sensorsStatus.add(createMonitoringStatusMeasure(null, uri));
        } else {
            for (String type : sensorRequest.getType()) {
                if (type.contains("VitalSensor")) {
                    // TODO: add IoT system sensors status objects
                }
                else if (type.contains("MonitoringSensor")) {
                	sensorsStatus.add(createMonitoringStatusMeasure(null, uri));
                }
            }
            for (String id : sensorRequest.getId()) {
            	if (id.contains("monitoring")) {
                    tmpSensor = createMonitoringStatusMeasure(null, uri);
                    if (!sensorsStatus.contains(tmpSensor)) {
                    	sensorsStatus.add(tmpSensor);
                    }
                } else {
                	/* TODO: loop over the IoT system sensors and if you find the one
                       corresponding to "id" add its status object if not already included */
                }
            }
        }

        try {
			return Response.status(Response.Status.OK)
				.entity(JsonUtils.serializeJson(sensorsStatus))
				.build();
		} catch (IOException e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
    }

    @Path("/sensor/observation")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getObservation(String bodyRequest, @Context UriInfo uri) {
        ObservationRequest observationRequest;
        ArrayList<Measure> measures = new ArrayList<Measure>();
        ArrayList<PerformanceMetric> metrics = new ArrayList<PerformanceMetric>();

        try {
        	observationRequest = (ObservationRequest) JsonUtils.deserializeJson(bodyRequest, ObservationRequest.class);
        	boolean missing = false;
        	String errmsg = "";
            if (observationRequest.getSensor().isEmpty()) {
            	missing = true;
            	errmsg = errmsg + "sensor";
            }
            if (observationRequest.getProperty() == null) {
            	missing = true;
            	errmsg = errmsg + " and property";
            }
            if (missing)
            	throw new IOException("field(s) " + errmsg + " is/are required!");
        } catch (IOException e) {
        	logger.error("[/sensor/observation] Error parsing request");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        // TODO: call the appropriate "client" method(s) and use "cache" if needed

        for (String id : observationRequest.getSensor()) {
            if (id.contains("monitoring")) {
                // Monitoring sensor
                PerformanceMetric metric;
                Date date = new Date();
                Runtime runtime = Runtime.getRuntime();
                SimpleDateFormat printedDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
                String type, unit, value;
                if (observationRequest.getProperty().contains("UsedMem")) {
                	type = "vital:UsedMem";
                	unit = "qudt:Byte";
                	value = Long.toString(runtime.totalMemory());
                } else if (observationRequest.getProperty().contains("AvailableMem")) {
                	type = "vital:AvailableMem";
                	unit = "qudt:Byte";
                	value = Long.toString(runtime.freeMemory());
                } else if (observationRequest.getProperty().contains("AvailableDisk")) {
                	type = "vital:AvailableDisk";
                	unit = "qudt:Byte";
                	value = Long.toString(new File("/").getFreeSpace());
                } else if (observationRequest.getProperty().contains("SysLoad")) {
                	type = "vital:SysLoad";
                	unit = "qudt:Percentage";
                	try {
    					value = Double.toString(getProcessCpuLoad());
    				} catch (Exception e) {
    					e.printStackTrace();
    					return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    				}
                } else if (observationRequest.getProperty().contains("ServedRequests")) {
                	type = "vital:ServedRequests";
                	unit = "qudt:Number";
                	value = Integer.toString(StatCounter.getRequestNumber().get());
                } else if (observationRequest.getProperty().contains("Errors")) {
                	type = "vital:Errors";
                	unit = "qudt:Number";
                	value = Integer.toString(StatCounter.getErrorNumber().get());
                } else if (observationRequest.getProperty().contains("SysUptime")) {
                	type = "vital:SysUptime";
                	unit = "qudt:MilliSecond";
                	value = Long.toString(date.getTime() - startupTime.getTime());
                } else if (observationRequest.getProperty().contains("PendingRequests")) {
                	type = "vital:PendingRequests";
                	unit = "qudt:Number";
                	value = Integer.toString(StatCounter.getPendingRequest() - 1);
                } else {
                	logger.error("[/sensor/observation] Bad metric " + observationRequest.getProperty());
                    return Response.status(Response.Status.BAD_REQUEST).build();
                }

                metric = new PerformanceMetric();
                metric.setContext("http://vital-iot.eu/contexts/measurement.jsonld");
                metric.setId(uri.getBaseUri() + "sensor/monitoring/observation/" + Long.toHexString(date.getTime()));
                metric.setType("ssn:Observation");

                SsnObservationProperty_ ssnObservationProperty_ = new SsnObservationProperty_();
                ssnObservationProperty_.setType(type);
                metric.setSsnObservationProperty(ssnObservationProperty_);

                SsnObservationResultTime_ ssnObservationResultTime_ = new SsnObservationResultTime_();
                ssnObservationResultTime_.setTimeInXSDDateTime(printedDateFormat.format(date));
                metric.setSsnObservationResultTime(ssnObservationResultTime_);

                SsnObservationResult_ ssnObservationResult_ = new SsnObservationResult_();
                ssnObservationResult_.setType("ssn:SensorOutput");
                SsnHasValue_ ssnHasValue_ = new SsnHasValue_();
                ssnHasValue_.setType("ssn:ObservationValue");
                ssnHasValue_.setValue(value);
                ssnHasValue_.setQudtUnit(unit);
                ssnObservationResult_.setSsnHasValue(ssnHasValue_);
                metric.setSsnObservationResult(ssnObservationResult_);

                metric.setSsnFeatureOfInterest(uri.getBaseUri().toString().replaceAll("/$", ""));
                metrics.add(metric);

                try {
        			return Response.status(Response.Status.OK)
        				.entity(JsonUtils.serializeJson(metrics))
        				.build();
        		} catch (IOException e) {
        			e.printStackTrace();
        			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        		}
            } else {
            	Measure tmpMeasure;
            	boolean found = false;
                // TODO: loop over the IoT system sensors and look for the one corresponding to id
                // If found construct the observation and add it to "measures"
                // If appropriate handle the "from" and "to" fields in the request
                if (!found)
                	return Response.status(Response.Status.BAD_REQUEST).build();
            }
        }

        try {
			return Response.status(Response.Status.OK)
				.entity(JsonUtils.serializeJson(measures))
				.build();
		} catch (IOException e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
    }

    private Sensor createMonitoringSensor(String path, UriInfo uri) {
    	String id = "monitoring";
    	List<SsnObserf> observedProperties;
        Sensor sensor = new Sensor();

        sensor.setContext("http://vital-iot.eu/contexts/sensor.jsonld");
        sensor.setName(id);
        sensor.setType("vital:MonitoringSensor");
        sensor.setDescription("Awesome monitoring sensor");
        if (path == null)
            sensor.setId(uri.getBaseUri() + "sensor/" + id);
        else
            sensor.setId(uri.getBaseUri() + path + "/sensor/" + id);

        sensor.setStatus("vital:Running");

        observedProperties = new ArrayList<SsnObserf>();

        SsnObserf observedProperty = new SsnObserf();
        observedProperty.setType("vital:MemUsed");
        if (path == null)
        	observedProperty.setId(uri.getBaseUri() + "sensor/" + id + "/" + "usedMem");
        else
        	observedProperty.setId(uri.getBaseUri() + path + "/sensor/" + id + "/" + "usedMem");
        observedProperties.add(observedProperty);

        observedProperty = new SsnObserf();
        observedProperty.setType("vital:MemAvailable");
        if (path == null)
        	observedProperty.setId(uri.getBaseUri() + "sensor/" + id + "/" + "availableMem");
        else
        	observedProperty.setId(uri.getBaseUri() + path + "/sensor/" + id + "/" + "availableMem");
        observedProperties.add(observedProperty);

        observedProperty = new SsnObserf();
        observedProperty.setType("vital:DiskAvailable");
        if (path == null)
        	observedProperty.setId(uri.getBaseUri() + "sensor/" + id + "/" + "availableDisk");
        else
        	observedProperty.setId(uri.getBaseUri() + path + "/sensor/" + id + "/" + "availableDisk");
        observedProperties.add(observedProperty);

        observedProperty = new SsnObserf();
        observedProperty.setType("vital:SysLoad");
        if (path == null)
        	observedProperty.setId(uri.getBaseUri() + "sensor/" + id + "/" + "sysLoad");
        else
        	observedProperty.setId(uri.getBaseUri() + path + "/sensor/" + id + "/" + "sysLoad");
        observedProperties.add(observedProperty);

        observedProperty = new SsnObserf();
        observedProperty.setType("vital:ServedRequest");
        if (path == null)
        	observedProperty.setId(uri.getBaseUri() + "sensor/" + id + "/" + "servedRequests");
        else
        	observedProperty.setId(uri.getBaseUri() + path + "/sensor/" + id + "/" + "servedRequests");
        observedProperties.add(observedProperty);

        observedProperty = new SsnObserf();
        observedProperty.setType("vital:Errors");
        if (path == null)
        	observedProperty.setId(uri.getBaseUri() + "sensor/" + id + "/" + "errors");
        else
        	observedProperty.setId(uri.getBaseUri() + path + "/sensor/" + id + "/" + "errors");
        observedProperties.add(observedProperty);

        observedProperty = new SsnObserf();
        observedProperty.setType("vital:SysUpTime");
        if (path == null)
        	observedProperty.setId(uri.getBaseUri() + "sensor/" + id + "/" + "sysUptime");
        else
        	observedProperty.setId(uri.getBaseUri() + path + "/sensor/" + id + "/" + "sysUptime");
        observedProperties.add(observedProperty);

        observedProperty = new SsnObserf();
        observedProperty.setType("vital:PendingRequests");
        if (path == null)
        	observedProperty.setId(uri.getBaseUri() + "sensor/" + id + "/" + "pendingRequests");
        else
        	observedProperty.setId(uri.getBaseUri() + path + "/sensor/" + id + "/" + "pendingRequests");
        observedProperties.add(observedProperty);

        sensor.setSsnObserves(observedProperties);
        
        sensor.setStatus("vital:Running");

        return sensor;
    }

    /* This is an example of function to construct a sensor metadata object (CityBikes API):
        private Sensor createSensorFromStation(String path, Station station, UriInfo uri) throws ParseException {
            SimpleDateFormat timestampDateFormat;
            Date now, timestamp = null;
            String id = station.getId();
            Sensor sensor = new Sensor();

            sensor.setContext("http://vital-iot.eu/contexts/sensor.jsonld");
            sensor.setName(station.getName());
            sensor.setType("vital:VitalSensor");
            sensor.setDescription(station.getExtra().getDescription());
            sensor.setId(uri.getBaseUri() + path + "/sensor/" + id);

            timestampDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            timestamp = timestampDateFormat.parse(station.getTimestamp());
            now = new Date();
            if (now.getTime() - timestamp.getTime() > 60 * 1000 * 60) {
                sensor.setStatus("vital:Unavailable");
            } else {
                sensor.setStatus("vital:Running");
            }

            HasLastKnownLocation location = new HasLastKnownLocation();
            location.setType("geo:Point");
            location.setGeoLat(station.getLatitude());
            location.setGeoLong(station.getLongitude());
            sensor.setHasLastKnownLocation(location);

            List<SsnObserf> observedProperties = new ArrayList<SsnObserf>();

            SsnObserf freeBikes = new SsnObserf();
            freeBikes.setType("vital:AvailableBikes");
            freeBikes.setId(uri.getBaseUri() + path + "/sensor/" + id + "/" + "AvailableBikes".toLowerCase());
            SsnObserf emptySlots = new SsnObserf();
            emptySlots.setType("vital:EmptyDocks");
            emptySlots.setId(uri.getBaseUri() + path + "/sensor/" + id + "/" + "EmptyDocks".toLowerCase());
            observedProperties.add(freeBikes);
            observedProperties.add(emptySlots);

            sensor.setSsnObserves(observedProperties);

            return sensor;
        }
    */

    private Service createObservationService(String path, UriInfo uri) {
    	Operation operation;
    	List<Operation> operations;
        Service observationService = new Service();
        
        observationService.setContext("http://vital-iot.eu/contexts/service.jsonld");
        if (path == null)
        	observationService.setId(uri.getBaseUri() + "service/observation");
        else
        	observationService.setId(uri.getBaseUri() + path + "/service/observation");
        observationService.setType("vital:ObservationService");
        operations = new ArrayList<Operation>();
        operation = new Operation();
        operation.setType("vital:GetObservations");
        operation.setHrestHasMethod("hrest:POST");
        if (path == null)
        	operation.setHrestHasAddress(uri.getBaseUri() + "sensor/observation");
        else
        	operation.setHrestHasAddress(uri.getBaseUri() + path + "/sensor/observation");
        operations.add(operation);
        observationService.setOperations(operations);

        return observationService;
    }

    private Service createMonitoringService(String path, UriInfo uri) {
    	Operation operation;
    	List<Operation> operations;
        Service monitoringService = new Service();
        
        monitoringService.setContext("http://vital-iot.eu/contexts/service.jsonld");
        if (path == null)
        	monitoringService.setId(uri.getBaseUri() + "service/monitoring");
        else
        	monitoringService.setId(uri.getBaseUri() + path + "/service/monitoring");
        monitoringService.setType("vital:MonitoringService");
        operations = new ArrayList<Operation>();
        operation = new Operation();
        operation.setType("vital:GetSystemStatus");
        operation.setHrestHasMethod("hrest:POST");
        if (path == null)
        	operation.setHrestHasAddress(uri.getBaseUri() + "system/status");
        else
        	operation.setHrestHasAddress(uri.getBaseUri() + path + "/system/status");
        operations.add(operation);
        operation = new Operation();
        operation.setType("vital:GetSensorStatus");
        operation.setHrestHasMethod("hrest:POST");
        if (path == null)
        	operation.setHrestHasAddress(uri.getBaseUri() + "sensor/status");
        else
        	operation.setHrestHasAddress(uri.getBaseUri() + path + "/sensor/status");
        operations.add(operation);
        operation = new Operation();
        operation.setType("vital:GetSupportedPerformanceMetrics");
        operation.setHrestHasMethod("hrest:GET");
        if (path == null)
        	operation.setHrestHasAddress(uri.getBaseUri() + "system/performance");
        else
        	operation.setHrestHasAddress(uri.getBaseUri() + path + "/system/performance");
        operations.add(operation);
        operation = new Operation();
        operation.setType("vital:GetPerformanceMetrics");
        operation.setHrestHasMethod("hrest:POST");
        if (path == null)
        	operation.setHrestHasAddress(uri.getBaseUri() + "system/performance");
        else
        	operation.setHrestHasAddress(uri.getBaseUri() + path + "/system/performance");
        operations.add(operation);
        monitoringService.setOperations(operations);

        return monitoringService;
    }

    private SensorStatus createMonitoringStatusMeasure(String path, UriInfo uri) {
    	SimpleDateFormat printedDateFormat;
    	Date now;
        SensorStatus m = new SensorStatus();

        printedDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        now = new Date();

        m.setContext("http://vital-iot.eu/contexts/measurement.jsonld");
        if (path == null)
        	m.setId(uri.getBaseUri() + "sensor/monitoring/observation/" + Long.toHexString(now.getTime()));
        else
        	m.setId(uri.getBaseUri() + path + "/sensor/monitoring/observation/" + Long.toHexString(now.getTime()));
        m.setType("ssn:Observation");

        SsnObservationProperty__ ssnObservationProperty = new SsnObservationProperty__();
        ssnObservationProperty.setType("vital:OperationalState");
        m.setSsnObservationProperty(ssnObservationProperty);

        SsnObservationResultTime__ ssnObservationResultTime = new SsnObservationResultTime__();
    	ssnObservationResultTime.setTimeInXSDDateTime(printedDateFormat.format(now));
    	if (path == null)
    		m.setAdditionalProperty("ssn:featureOfInterest", uri.getBaseUri() + "sensor/monitoring");
    	else
    		m.setAdditionalProperty("ssn:featureOfInterest", uri.getBaseUri() + path + "/sensor/monitoring");
        m.setSsnObservationResultTime(ssnObservationResultTime);

        SsnObservationResult__ ssnObservationResult = new SsnObservationResult__();
        ssnObservationResult.setType("ssn:SensorOutput");
        SsnHasValue__ ssnHasValue = new SsnHasValue__();
        ssnHasValue.setType("ssn:ObservationValue");

    	ssnHasValue.setValue("vital:Running");
        ssnObservationResult.setSsnHasValue(ssnHasValue);
        m.setSsnObservationResult(ssnObservationResult);

        return m;
    }

    /* This is an example of function to construct a sensor status object (it is actually quite generic, "station" is only to get the sensor id):
        private SensorStatus createStatusMeasureFromStation(String path, Station station, UriInfo uri) throws ParseException {
            SimpleDateFormat timestampDateFormat, printedDateFormat;
            Date now, timestamp = null;
            SensorStatus m = new SensorStatus();

            printedDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
            timestampDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            now = new Date();

            m.setContext("http://vital-iot.eu/contexts/measurement.jsonld");
            m.setId(uri.getBaseUri() + path + "/sensor/monitoring/observation/" + Long.toHexString(now.getTime()));
            m.setType("ssn:Observation");

            SsnObservationProperty__ ssnObservationProperty = new SsnObservationProperty__();
            ssnObservationProperty.setType("vital:OperationalState");
            m.setSsnObservationProperty(ssnObservationProperty);

            SsnObservationResultTime__ ssnObservationResultTime = new SsnObservationResultTime__();
            ssnObservationResultTime.setTimeInXSDDateTime(printedDateFormat.format(now));
            m.setAdditionalProperty("ssn:featureOfInterest", uri.getBaseUri() + path + "/sensor/" + station.getId());
            m.setSsnObservationResultTime(ssnObservationResultTime);

            SsnObservationResult__ ssnObservationResult = new SsnObservationResult__();
            ssnObservationResult.setType("ssn:SensorOutput");
            SsnHasValue__ ssnHasValue = new SsnHasValue__();
            ssnHasValue.setType("ssn:ObservationValue");

            timestamp = timestampDateFormat.parse(station.getTimestamp());
            if (now.getTime() - timestamp.getTime() > 60 * 1000 * 60) {
                ssnHasValue.setValue("vital:Unavailable");
            } else {
                ssnHasValue.setValue("vital:Running");
            }
            ssnObservationResult.setSsnHasValue(ssnHasValue);
            m.setSsnObservationResult(ssnObservationResult);

            return m;
        }
    */

    /* This is an example of function to construct a sensor observation object (CityBikes API):
        private Measure createMeasureFromStation(String path, Station station, String property, UriInfo uri) throws ParseException {
            Measure m;
            SimpleDateFormat printedDateFormat, timestampDateFormat;
            Date timestamp = null;

            printedDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
            timestampDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

            timestamp = timestampDateFormat.parse(station.getTimestamp());

            m = new Measure();
            m.setContext("http://vital-iot.eu/contexts/measurement.jsonld");
            m.setId(uri.getBaseUri() + path + "/sensor/" + station.getId() + "/observation/" + Long.toHexString(timestamp.getTime()));
            m.setType("ssn:Observation");
            m.setSsnObservedBy(uri.getBaseUri() + path + "/sensor/" + station.getId());

            SsnObservationProperty ssnObservationProperty = new SsnObservationProperty();
            ssnObservationProperty.setType("vital:" + property);
            m.setSsnObservationProperty(ssnObservationProperty);

            SsnObservationResultTime ssnObservationResultTime = new SsnObservationResultTime();
            ssnObservationResultTime.setTimeInXSDDateTime(printedDateFormat.format(timestamp));
            m.setSsnObservationResultTime(ssnObservationResultTime);

            DulHasLocation dulHasLocation = new DulHasLocation();
            dulHasLocation.setType("geo:Point");
            dulHasLocation.setGeoLat(station.getLatitude());
            dulHasLocation.setGeoLong(station.getLongitude());
            m.setDulHasLocation(dulHasLocation);

            SsnObservationResult ssnObservationResult = new SsnObservationResult();
            ssnObservationResult.setType("ssn:SensorOutput");
            SsnHasValue ssnHasValue = new SsnHasValue();
            ssnHasValue.setType("ssn:ObservationValue");

            if (property.contains("AvailableBikes")) {
                ssnHasValue.setValue(station.getFreeBikes());
                ssnHasValue.setQudtUnit("qudt:Number");
            } else if (property.contains("EmptyDocks")) {
                ssnHasValue.setValue(station.getEmptySlots());
                ssnHasValue.setQudtUnit("qudt:Number");
            } else {
                return null;
            }
            ssnObservationResult.setSsnHasValue(ssnHasValue);
            m.setSsnObservationResult(ssnObservationResult);

            return m;
        }
    */
    
    private static double getProcessCpuLoad() throws Exception {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName name = ObjectName.getInstance("java.lang:type=OperatingSystem");
        AttributeList list = mbs.getAttributes(name, new String[] { "ProcessCpuLoad" });

        if (list.isEmpty())
        	return Double.NaN;

        Attribute att = (Attribute) list.get(0);
        Double value = (Double) att.getValue();

        if (value == -1.0)
        	return Double.NaN;

        return ((int) (value * 1000) / 10.0);
    }
}

