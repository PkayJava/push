//package com.itrustcambodia.v5.task;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.apache.commons.io.FileUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.dao.EmptyResultDataAccessException;
//import org.springframework.http.ResponseEntity;
//import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.client.RestTemplate;
//
//import com.amazonaws.auth.BasicAWSCredentials;
//import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.AmazonS3Client;
//import com.itrustcambodia.pluggable.core.IApplication;
//import com.itrustcambodia.restclient.RestTemplateFactory;
//import com.itrustcambodia.v5.Constants;
//import com.itrustcambodia.v5.Table;
//import com.itrustcambodia.v5.VINUtils;
//import com.itrustcambodia.v5.model.Queue;
//import com.itrustcambodia.v5.model.Vehicle;
//
//@Service
//public class ImportQueue {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(ImportQueue.class);
//
//    @Autowired
//    private JdbcTemplate jdbcTemplate;
//
//    @Autowired
//    private IApplication application;
//
//    @Autowired
//    @Qualifier("server")
//    private String server;
//
//    @Transactional
//    @Scheduled(fixedRate = 1000 * 60 * 1, initialDelay = 1000 * 60 * 5)
//    public void process() {
//
//        if ("appfog".equals(server)) {
//            String accessKey = application.select(Constants.AWS_S3_ACCESS_KEY, String.class);
//            String secretKey = application.select(Constants.AWS_S3_SECRET_KEY, String.class);
//            String bucketName = application.select(Constants.AWS_S3_BUCKET_NAME, String.class);
//            if (accessKey == null || "".equals(accessKey) || secretKey == null || "".equals(secretKey) || bucketName == null || "".equals(bucketName)) {
//                return;
//            }
//        } else if ("local".equals(server)) {
//            String repository = application.select(Constants.REPOSITORY, String.class);
//            if (repository == null || "".equals(repository)) {
//                return;
//            }
//        }
//
//        AmazonS3 client = null;
//        String bucketName = null;
//        if ("appfog".equals(server)) {
//            String accessKey = application.select(Constants.AWS_S3_ACCESS_KEY, String.class);
//            String secretKey = application.select(Constants.AWS_S3_SECRET_KEY, String.class);
//            bucketName = application.select(Constants.AWS_S3_BUCKET_NAME, String.class);
//            BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
//            client = new AmazonS3Client(credentials);
//        }
//
//        try {
//            Queue queue = null;
//            try {
//                queue = jdbcTemplate.queryForObject("select * from " + Table.QUEUE + " order by " + Table.Queue.ID + " asc limit 1", Queue.MAPPER);
//            } catch (EmptyResultDataAccessException e) {
//            }
//            if (queue != null) {
//                try {
//                    Vehicle vehicle = jdbcTemplate.queryForObject("select * from " + Table.VEHICLE + " where " + Table.Vehicle.MEDIA_ID + " = ?", Vehicle.MAPPER, queue.getMediaId());
//                    List<String> pictures = jdbcTemplate.queryForList("select " + Table.Picture.HREF + " from " + Table.PICTURE + " where " + Table.Picture.VEHICLE_ID + " = ?", String.class, vehicle.getVehicleId());
//                    if ("appfog".equals(server)) {
//                        for (String href : pictures) {
//                            try {
//                                String key = href.substring("aws_s3://".length());
//                                client.deleteObject(bucketName, key);
//                            } catch (Throwable e) {
//                            }
//                        }
//                    }
//                    jdbcTemplate.update("delete from " + Table.PICTURE + " where " + Table.Picture.VEHICLE_ID + " = ?", vehicle.getVehicleId());
//                    jdbcTemplate.update("delete from " + Table.VEHICLE + " where " + Table.Vehicle.ID + " = ?", vehicle.getVehicleId());
//                } catch (EmptyResultDataAccessException e) {
//                }
//
//                String userAgent = application.select(Constants.USER_AGENT, String.class);
//                if (userAgent == null || "".equals(userAgent)) {
//                    return;
//                }
//                RestTemplateFactory factory = new RestTemplateFactory();
//                factory.setUserAgent(userAgent);
//                try {
//                    factory.afterPropertiesSet();
//                } catch (Exception e) {
//                }
//                RestTemplate restTemplate = null;
//                if (factory != null) {
//                    try {
//                        restTemplate = factory.getObject();
//                    } catch (Exception e) {
//                    }
//                }
//                if (restTemplate != null) {
//                    try {
//                        if (Queue.Source.AUCTION_EXPORT.equals(queue.getSource())) {
//                            dumpAuctionExportDetail(restTemplate, queue);
//                        }
//                    } catch (Throwable e) {
//                        LOGGER.info("error {}", e.getMessage());
//                        jdbcTemplate.update("delete from " + Table.QUEUE + " where " + Table.Queue.ID + " = ?", queue.getQueueId());
//                    } finally {
//                        if (HttpComponentsClientHttpRequestFactory.class.isAssignableFrom(restTemplate.getRequestFactory().getClass())) {
//                            HttpComponentsClientHttpRequestFactory requestFactory = (HttpComponentsClientHttpRequestFactory) restTemplate.getRequestFactory();
//                            requestFactory.destroy();
//                        }
//                    }
//                }
//            }
//        } catch (org.springframework.jdbc.BadSqlGrammarException e) {
//        }
//    }
//
//    protected void dumpAuctionExportDetail(RestTemplate restTemplate, Queue queue) {
//        LOGGER.info("importing {} - {}", queue.getMediaId(), queue.getMedia());
//        String html = restTemplate.getForObject(queue.getMedia(), String.class);
//        Document document = Jsoup.parse(html);
//
//        String vin = null;
//        String mileage = null;
//        String exteriorColor = null;
//        String interiorColor = null;
//        String engine = null;
//        String driveTrain = null;
//        String transmission = null;
//        String location = null;
//
//        String description = document.getElementById("vehicle-title").text();
//        String year = description.split(" ")[0];
//
//        Elements tables = document.getElementsByTag("table");
//
//        Element table = null;
//        for (Element tmp : tables) {
//            if (tmp.attr("style") != null && tmp.attr("style").equals("border-collapse: collapse; width: 100%; border-color: #D6DAE1;")) {
//                table = tmp;
//            }
//        }
//        Elements trs = table.getElementsByTag("tr");
//        for (Element tr : trs) {
//            Elements tds = tr.getElementsByTag("td");
//            if (tds.size() < 2) {
//                continue;
//            }
//            Element name = tds.get(0);
//            Element value = tds.get(1);
//            if ("VIN #".equals(name.text())) {
//                vin = VINUtils.decode(value.getElementsByTag("input").get(0).attr("value"));
//            } else if ("MILEAGE:".equals(name.text())) {
//                mileage = value.text();
//            } else if ("Exterior Color:".equals(name.text())) {
//                exteriorColor = value.text();
//            } else if ("Interior Color:".equals(name.text())) {
//                interiorColor = value.text();
//            } else if ("Engine:".equals(name.text())) {
//                engine = value.text();
//            } else if ("Drivetrain:".equals(name.text())) {
//                driveTrain = value.text();
//            } else if ("Transmission:".equals(name.text())) {
//                transmission = value.text();
//            } else if ("LOCATION:".equals(name.text())) {
//                location = value.text();
//            }
//        }
//
//        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate);
//        insert.withTableName(Table.VEHICLE);
//        insert.usingGeneratedKeyColumns(Table.Vehicle.ID);
//
//        Map<String, Object> field = new HashMap<String, Object>();
//        field.put(Table.Vehicle.DESCRIPTION, description);
//        field.put(Table.Vehicle.DRIVE_TRAIN, driveTrain);
//        field.put(Table.Vehicle.ENGINE, engine);
//        field.put(Table.Vehicle.EXTERIOR_COLOR, exteriorColor);
//        field.put(Table.Vehicle.INTERIOR_COLOR, interiorColor);
//        field.put(Table.Vehicle.LOCATION, location);
//        field.put(Table.Vehicle.MEDIA, queue.getMedia());
//        field.put(Table.Vehicle.MEDIA_ID, queue.getMediaId());
//        field.put(Table.Vehicle.MILEAGE, mileage);
//        field.put(Table.Vehicle.SOURCE, queue.getSource());
//        field.put(Table.Vehicle.VIN, vin);
//        field.put(Table.Vehicle.YEAR, year);
//        field.put(Table.Vehicle.TRANSMISSION, transmission);
//
//        long vehicleId = insert.executeAndReturnKey(field).longValue();
//
//        String repository = application.select(Constants.REPOSITORY, String.class);
//        File vehicleRepository = new File(repository, String.valueOf(vehicleId));
//        vehicleRepository.mkdirs();
//
//        Elements pictures = document.getElementsByClass("c_MakePrettyPhoto");
//        SimpleJdbcInsert pictureInsert = new SimpleJdbcInsert(jdbcTemplate);
//        pictureInsert.withTableName(Table.PICTURE);
//
//        AmazonS3 client = null;
//        String bucketName = null;
//        if ("appfog".equals(server)) {
//            String accessKey = application.select(Constants.AWS_S3_ACCESS_KEY, String.class);
//            String secretKey = application.select(Constants.AWS_S3_SECRET_KEY, String.class);
//            bucketName = application.select(Constants.AWS_S3_BUCKET_NAME, String.class);
//            BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
//            client = new AmazonS3Client(credentials);
//        }
//
//        for (Element picture : pictures) {
//            try {
//                String href = picture.attr("href");
//                int index = href.indexOf("picID=") + "picID=".length();
//                String picId = href.substring(index, href.indexOf("&", index));
//                List<String> params = new ArrayList<String>();
//                params.add("picID=" + picId);
//                params.add("width=640");
//                if (href.contains("d=True") || href.contains("d=true")) {
//                    params.add("d=True");
//                }
//                if (href.contains("aspect=False") || href.contains("aspect=false")) {
//                    params.add("aspect=False");
//                }
//                if (href.contains("aspect=true") || href.contains("aspect=True")) {
//                    params.add("aspect=True");
//                }
//
//                String url = "https://www.auctionexport.com/en/IMG/GetImage?" + StringUtils.join(params, "&");
//
//                LOGGER.info("pic {} - {}", vehicleId, url);
//                Thread.sleep(1000 * 5);
//                ResponseEntity<File> responseEntity = restTemplate.getForEntity(url, File.class);
//                try {
//                    if ("local".equals(server)) {
//                        File tmp = new File(vehicleRepository, picId + ".jpg");
//                        FileUtils.moveFile(responseEntity.getBody(), tmp);
//                        Map<String, Object> f = new HashMap<String, Object>();
//                        f.put(Table.Picture.VEHICLE_ID, vehicleId);
//                        f.put(Table.Picture.HREF, "file://" + tmp.getAbsolutePath());
//                        pictureInsert.execute(f);
//                    } else if ("appfog".equals(server)) {
//                        try {
//                            client.deleteObject(bucketName, picId + ".jpg");
//                        } catch (Throwable e) {
//                        }
//                        client.putObject(bucketName, picId + ".jpg", responseEntity.getBody());
//                        FileUtils.deleteQuietly(responseEntity.getBody());
//                        Map<String, Object> f = new HashMap<String, Object>();
//                        f.put(Table.Picture.VEHICLE_ID, vehicleId);
//                        f.put(Table.Picture.HREF, "aws_s3://" + picId + ".jpg");
//                        pictureInsert.execute(f);
//                    }
//                } catch (IOException e) {
//                }
//            } catch (Throwable e) {
//                LOGGER.info("error {}", e.getMessage());
//                e.printStackTrace();
//            }
//        }
//
//        LOGGER.info("imported {} - {} - {}", vin, queue.getMediaId(), queue.getMedia());
//
//        jdbcTemplate.update("delete from " + Table.QUEUE + " where " + Table.Queue.ID + " = ?", queue.getQueueId());
//    }
//}
