package com.itrustcambodia.v5.rest.v1;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.web.bind.ServletRequestUtils;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.itrustcambodia.pluggable.core.AbstractWebApplication;
import com.itrustcambodia.pluggable.database.EntityRowMapper;
import com.itrustcambodia.pluggable.doc.ApiMethod;
import com.itrustcambodia.pluggable.doc.ApiParam;
import com.itrustcambodia.pluggable.rest.Controller;
import com.itrustcambodia.pluggable.rest.RequestMapping;
import com.itrustcambodia.pluggable.rest.RequestMethod;
import com.itrustcambodia.pluggable.rest.Result;
import com.itrustcambodia.pluggable.utilities.FrameworkUtilities;
import com.itrustcambodia.pluggable.utilities.TableUtilities;
import com.itrustcambodia.pluggable.wicket.authroles.Role;
import com.itrustcambodia.pluggable.wicket.authroles.Secured;
import com.itrustcambodia.v5.entity.Application;
import com.itrustcambodia.v5.entity.City;
import com.itrustcambodia.v5.entity.Country;
import com.itrustcambodia.v5.entity.Device;
import com.itrustcambodia.v5.entity.Manufacture;
import com.itrustcambodia.v5.entity.Model;
import com.itrustcambodia.v5.entity.Platform;
import com.itrustcambodia.v5.entity.User;
import com.itrustcambodia.v5.entity.Version;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;

@Controller
public class DeviceRestAPIV1 {

    @Secured(roles = { @Role(name = "ROLE_REST_DELETE_ANDROID_DEVICE", description = "Access Delete Android Device Rest") })
    @RequestMapping(value = "/api/v1/device/android/delete", method = RequestMethod.POST)
    @ApiMethod(responseObject = String.class, description = "mark device as inactive", requestParameters = { @ApiParam(name = "token", required = true, type = String.class, description = "a valid registered token"), @ApiParam(name = "packageId", required = true, type = String.class, description = "packageId of the app") })
    public Result androidDelete(AbstractWebApplication application, HttpServletRequest request, HttpServletResponse response) {

        String token = ServletRequestUtils.getStringParameter(request, "token", "");
        String packageId = ServletRequestUtils.getStringParameter(request, "packageId", "");
        if ("".equals(token) || "".equals(packageId)) {
            return Result.badRequest("application/json");
        }

        JdbcTemplate jdbcTemplate = application.getBean(JdbcTemplate.class);

        Platform platform = getPlatform(jdbcTemplate, Device.Platform.ANDROID);

        String loginName = request.getUserPrincipal().getName();
        User user = jdbcTemplate.queryForObject("select * from " + TableUtilities.getTableName(User.class) + " where " + User.LOGIN + " = ?", new EntityRowMapper<User>(User.class), loginName);
        Application app = null;
        try {
            app = jdbcTemplate.queryForObject("select * from " + TableUtilities.getTableName(Application.class) + " where " + Application.USER_ID + " = ? and " + Application.PACKAGE_ID + " = ?", new EntityRowMapper<Application>(Application.class), user.getId(), packageId);
        } catch (EmptyResultDataAccessException e) {
            return Result.notFound("application/json");
        }

        jdbcTemplate.update("update " + TableUtilities.getTableName(Device.class) + "set " + Device.FLAG + " = ? " + " where " + Device.TOKEN + " = ? and " + Device.APPLICATION_ID + " = ? and " + Device.PLATFORM_ID + " = ?", Device.Flag.DELETE, token, app.getId(), platform.getId());

        return Result.ok("application/json");
    }

    @Secured(roles = { @Role(name = "ROLE_REST_DELETE_IOS_DEVICE", description = "Access Delete iOS Device Rest") })
    @RequestMapping(value = "/api/v1/device/ios/delete", method = RequestMethod.POST)
    @ApiMethod(responseObject = String.class, description = "mark device as inactive", requestParameters = { @ApiParam(name = "token", required = true, type = String.class, description = "a valid registered token"), @ApiParam(name = "bundleId", required = true, type = String.class, description = "bundleId of the app") })
    public Result iosDelete(AbstractWebApplication application, HttpServletRequest request, HttpServletResponse response) {
        String token = ServletRequestUtils.getStringParameter(request, "token", "");
        String bundleId = ServletRequestUtils.getStringParameter(request, "bundleId", "");

        if ("".equals(token) || "".equals(bundleId)) {
            return Result.badRequest("application/json");
        }

        JdbcTemplate jdbcTemplate = application.getBean(JdbcTemplate.class);

        Platform platform = getPlatform(jdbcTemplate, Device.Platform.IOS);

        String loginName = request.getUserPrincipal().getName();
        User user = jdbcTemplate.queryForObject("select * from " + TableUtilities.getTableName(User.class) + " where " + User.LOGIN + " = ?", new EntityRowMapper<User>(User.class), loginName);

        Application app = null;
        try {
            app = jdbcTemplate.queryForObject("select * from " + TableUtilities.getTableName(Application.class) + " where " + Application.USER_ID + " = ? and " + Application.BUNDLE_ID + " = ?", new EntityRowMapper<Application>(Application.class), user.getId(), bundleId);
        } catch (EmptyResultDataAccessException e) {
            return Result.notFound("application/json");
        }

        jdbcTemplate.update("update " + TableUtilities.getTableName(Device.class) + "set " + Device.FLAG + " = ? " + " where " + Device.TOKEN + " = ? and " + Device.APPLICATION_ID + " = ? and " + Device.PLATFORM_ID + " = ?", Device.Flag.DELETE, token, app.getId(), platform.getId());
        return Result.ok("application/json");
    }

    @ApiMethod(responseObject = Long.class, description = "register new android device for push", requestParameters = { @ApiParam(name = "manufacture", type = String.class, description = "mabye Samsung, LG, HTC etc.."), @ApiParam(name = "token", required = true, type = String.class, description = "push notification token"), @ApiParam(name = "version", type = String.class, description = "platform api version"), @ApiParam(name = "packageId", required = true, type = String.class, description = "packageId of your app"), @ApiParam(name = "model", type = String.class, description = "device model mabye galaxy tab, galaxy tab II etc..") }, responseDescription = "device id")
    @Secured(roles = { @Role(name = "ROLE_REST_DEVICE_ANDROID", description = "Access Device Android Rest") })
    @RequestMapping(value = "/api/v1/device/android", method = RequestMethod.POST)
    public Result android(AbstractWebApplication application, HttpServletRequest request, HttpServletResponse response) throws JsonIOException, IOException {

        String token = ServletRequestUtils.getStringParameter(request, "token", "");

        String packageId = ServletRequestUtils.getStringParameter(request, "packageId", "");

        if ("".equals(token) || "".equals(packageId)) {
            return Result.badRequest("application/json");
        }

        JdbcTemplate jdbcTemplate = application.getBean(JdbcTemplate.class);

        String loginName = request.getUserPrincipal().getName();
        User user = jdbcTemplate.queryForObject("select * from " + TableUtilities.getTableName(User.class) + " where " + User.LOGIN + " = ?", new EntityRowMapper<User>(User.class), loginName);
        Application app = null;
        try {
            app = jdbcTemplate.queryForObject("select * from " + TableUtilities.getTableName(Application.class) + " where " + Application.USER_ID + " = ? and " + Application.PACKAGE_ID + " = ?", new EntityRowMapper<Application>(Application.class), user.getId(), packageId);
        } catch (EmptyResultDataAccessException e) {
            return Result.notFound("application/json");
        }

        Manufacture manufacture = getManufacture(jdbcTemplate, ServletRequestUtils.getStringParameter(request, "manufacture", "N/A"));

        Model model = getModel(jdbcTemplate, ServletRequestUtils.getStringParameter(request, "model", "N/A"));

        Platform platform = getPlatform(jdbcTemplate, Device.Platform.ANDROID);

        Version version = getVersion(jdbcTemplate, ServletRequestUtils.getStringParameter(request, "version", "N/A"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String clientIP = FrameworkUtilities.getClientIP(request);

        City city = getCity(jdbcTemplate, request.getSession().getServletContext(), clientIP);

        Country country = getCountry(jdbcTemplate, request.getSession().getServletContext(), clientIP);

        Device device = insert(application, token, clientIP, platform, model, manufacture, app, version, country, city);

        Gson gson = application.getGson();
        gson.toJson(device.getId(), response.getWriter());

        return Result.ok("application/json");
    }

    @ApiMethod(responseObject = Long.class, description = "register new ios device for push", requestParameters = { @ApiParam(name = "token", required = true, type = String.class, description = "push notification token"), @ApiParam(name = "version", type = String.class, description = "platform api version"), @ApiParam(name = "applicationId", required = true, type = Long.class, description = "applicationId of your app"), @ApiParam(name = "model", type = String.class, description = "iPad, iPad Mini, iPad Mini Retina, iPhone 2G, iPhone 3G...") }, responseDescription = "device id")
    @Secured(roles = { @Role(name = "ROLE_REST_DEVICE_IOS", description = "Access Device iOS Rest") })
    @RequestMapping(value = "/api/v1/device/ios", method = RequestMethod.POST)
    public Result ios(AbstractWebApplication application, HttpServletRequest request, HttpServletResponse response) throws JsonIOException, IOException {

        String token = ServletRequestUtils.getStringParameter(request, "token", "");

        String applicationId = ServletRequestUtils.getStringParameter(request, "applicationId", "");

        if ("".equals(token) || "".equals(applicationId)) {
            return Result.badRequest("application/json");
        } else {
            try {
                Long.valueOf(applicationId);
            } catch (NumberFormatException e) {
                return Result.badRequest("application/json");
            }
        }

        JdbcTemplate jdbcTemplate = application.getBean(JdbcTemplate.class);

        String loginName = request.getUserPrincipal().getName();
        User user = jdbcTemplate.queryForObject("select * from " + TableUtilities.getTableName(User.class) + " where " + User.LOGIN + " = ?", new EntityRowMapper<User>(User.class), loginName);
        Application app = null;
        try {
            app = jdbcTemplate.queryForObject("select * from " + TableUtilities.getTableName(Application.class) + " where " + Application.USER_ID + " = ? and " + Application.ID + " = ?", new EntityRowMapper<Application>(Application.class), user.getId(), applicationId);
        } catch (EmptyResultDataAccessException e) {
            return Result.notFound("application/json");
        }

        Manufacture manufacture = getManufacture(jdbcTemplate, "Apple Inc.");

        Model model = getModel(jdbcTemplate, ServletRequestUtils.getStringParameter(request, "model", "N/A"));

        Platform platform = getPlatform(jdbcTemplate, Device.Platform.IOS);

        Version version = getVersion(jdbcTemplate, ServletRequestUtils.getStringParameter(request, "version", "N/A"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String clientIP = FrameworkUtilities.getClientIP(request);

        City city = getCity(jdbcTemplate, request.getSession().getServletContext(), clientIP);

        Country country = getCountry(jdbcTemplate, request.getSession().getServletContext(), clientIP);

        Device device = insert(application, token, clientIP, platform, model, manufacture, app, version, country, city);

        Gson gson = application.getGson();
        gson.toJson(device.getId(), response.getWriter());
        return Result.ok("application/json");
    }

    private Device insert(AbstractWebApplication application, String token, String clientIP, Platform platform, Model model, Manufacture manufacture, Application app, Version version, Country country, City city) {
        JdbcTemplate jdbcTemplate = application.getBean(JdbcTemplate.class);

        Device device = null;
        try {
            device = jdbcTemplate.queryForObject("select * from " + TableUtilities.getTableName(Device.class) + " where " + Device.TOKEN + " = ?" + " and " + Device.PLATFORM_ID + " = ? and " + Device.APPLICATION_ID + " = ?", new EntityRowMapper<Device>(Device.class), token, platform.getId(), app.getId());
        } catch (EmptyResultDataAccessException e) {
        }

        Number deviceId = null;
        if (device == null) {
            SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate);
            insert.withTableName(TableUtilities.getTableName(Device.class));
            insert.usingGeneratedKeyColumns(Device.ID);
            Map<String, Object> fields = new HashMap<String, Object>();

            fields.put(Device.IP, clientIP);
            fields.put(Device.APPLICATION_ID, app.getId());
            fields.put(Device.MANUFACTURE_ID, manufacture.getId());
            fields.put(Device.MODEL_ID, model.getId());
            fields.put(Device.VERSION_ID, version.getId());
            fields.put(Device.FLAG, Device.Flag.ACTIVE);
            fields.put(Device.PLATFORM_ID, platform.getId());
            fields.put(Device.TOKEN, token);
            fields.put(Device.COUNTRY_ID, country.getId());
            fields.put(Device.CITY_ID, city.getId());

            deviceId = insert.executeAndReturnKey(fields);
        } else {
            deviceId = device.getId();
            jdbcTemplate.update("update " + TableUtilities.getTableName(Device.class) + " set " + Device.FLAG + " = ?, " + Device.IP + " = ?, " + Device.COUNTRY_ID + " = ?, " + Device.CITY_ID + " = ?" + " where " + Device.ID + " = ?", Device.Flag.ACTIVE, clientIP, device.getId(), country.getId(), city.getId());
        }

        device = jdbcTemplate.queryForObject("select * from " + TableUtilities.getTableName(Device.class) + " where " + Device.ID + " = ?", new EntityRowMapper<Device>(Device.class), deviceId.longValue());

        return device;
    }

    private Country getCountry(JdbcTemplate jdbcTemplate, ServletContext context, String ip) {
        try {
            DatabaseReader reader = new DatabaseReader(new File(context.getRealPath("/WEB-INF/GeoLite2-City.mmdb")));
            Country country = getCountry(jdbcTemplate, reader.city(InetAddress.getByName(ip)).getCountry().getName());
            reader.close();
            return country;
        } catch (IOException e) {
            return getCountry(jdbcTemplate, "N/A");
        } catch (GeoIp2Exception e) {
            return getCountry(jdbcTemplate, "N/A");
        }
    }

    private Country getCountry(JdbcTemplate jdbcTemplate, String name) {

        Country country = null;
        try {
            country = jdbcTemplate.queryForObject("select * from " + TableUtilities.getTableName(Country.class) + " where " + Country.NAME + " = ?", new EntityRowMapper<Country>(Country.class), name);
        } catch (EmptyResultDataAccessException e) {
            SimpleJdbcInsert p = new SimpleJdbcInsert(jdbcTemplate);
            p.withTableName(TableUtilities.getTableName(Country.class));
            p.usingGeneratedKeyColumns(Country.ID);
            Map<String, Object> pf = new HashMap<String, Object>();
            pf.put(Country.NAME, name);
            Long countryId = p.executeAndReturnKey(pf).longValue();
            country = new Country();
            country.setId(countryId);
            country.setName(name);
        }
        return country;
    }

    private City getCity(JdbcTemplate jdbcTemplate, ServletContext context, String ip) {
        try {
            DatabaseReader reader = new DatabaseReader(new File(context.getRealPath("/WEB-INF/GeoLite2-City.mmdb")));
            City city = getCity(jdbcTemplate, reader.city(InetAddress.getByName(ip)).getCity().getName());
            reader.close();
            return city;
        } catch (IOException e) {
            return getCity(jdbcTemplate, "N/A");
        } catch (GeoIp2Exception e) {
            return getCity(jdbcTemplate, "N/A");
        }
    }

    private City getCity(JdbcTemplate jdbcTemplate, String name) {
        City city = null;
        try {
            city = jdbcTemplate.queryForObject("select * from " + TableUtilities.getTableName(City.class) + " where " + City.NAME + " = ?", new EntityRowMapper<City>(City.class), name);
        } catch (EmptyResultDataAccessException e) {
            SimpleJdbcInsert p = new SimpleJdbcInsert(jdbcTemplate);
            p.withTableName(TableUtilities.getTableName(City.class));
            p.usingGeneratedKeyColumns(City.ID);
            Map<String, Object> pf = new HashMap<String, Object>();
            pf.put(City.NAME, name);
            Long cityId = p.executeAndReturnKey(pf).longValue();
            city = new City();
            city.setId(cityId);
            city.setName(name);
        }
        return city;
    }

    private Manufacture getManufacture(JdbcTemplate jdbcTemplate, String name) {
        Manufacture manufacture = null;
        try {
            manufacture = jdbcTemplate.queryForObject("select * from " + TableUtilities.getTableName(Manufacture.class) + " where " + Manufacture.NAME + " = ?", new EntityRowMapper<Manufacture>(Manufacture.class), name);
        } catch (EmptyResultDataAccessException e) {
            SimpleJdbcInsert p = new SimpleJdbcInsert(jdbcTemplate);
            p.withTableName(TableUtilities.getTableName(Manufacture.class));
            p.usingGeneratedKeyColumns(Manufacture.ID);
            Map<String, Object> pf = new HashMap<String, Object>();
            pf.put(Manufacture.NAME, name);
            Long manufactureId = p.executeAndReturnKey(pf).longValue();
            manufacture = new Manufacture();
            manufacture.setId(manufactureId);
            manufacture.setName(name);
        }
        return manufacture;
    }

    private Platform getPlatform(JdbcTemplate jdbcTemplate, String name) {
        Platform platform = null;
        try {
            platform = jdbcTemplate.queryForObject("select * from " + TableUtilities.getTableName(Platform.class) + " where " + Platform.NAME + " = ?", new EntityRowMapper<Platform>(Platform.class), name);
        } catch (EmptyResultDataAccessException e) {
            SimpleJdbcInsert p = new SimpleJdbcInsert(jdbcTemplate);
            p.withTableName(TableUtilities.getTableName(Platform.class));
            p.usingGeneratedKeyColumns(Platform.ID);
            Map<String, Object> pf = new HashMap<String, Object>();
            pf.put(Platform.NAME, name);
            Long platformId = p.executeAndReturnKey(pf).longValue();
            platform = new Platform();
            platform.setId(platformId);
            platform.setName(name);
        }
        return platform;
    }

    private Model getModel(JdbcTemplate jdbcTemplate, String name) {
        Model model = null;
        try {
            model = jdbcTemplate.queryForObject("select * from " + TableUtilities.getTableName(Model.class) + " where " + Model.NAME + " = ?", new EntityRowMapper<Model>(Model.class), name);
        } catch (EmptyResultDataAccessException e) {
            SimpleJdbcInsert p = new SimpleJdbcInsert(jdbcTemplate);
            p.withTableName(TableUtilities.getTableName(Model.class));
            p.usingGeneratedKeyColumns(Model.ID);
            Map<String, Object> pf = new HashMap<String, Object>();
            pf.put(Model.NAME, name);
            Long modelId = p.executeAndReturnKey(pf).longValue();
            model = new Model();
            model.setId(modelId);
            model.setName(name);
        }
        return model;
    }

    private Version getVersion(JdbcTemplate jdbcTemplate, String name) {
        Version version = null;
        try {
            version = jdbcTemplate.queryForObject("select * from " + TableUtilities.getTableName(Version.class) + " where " + Version.NAME + " = ?", new EntityRowMapper<Version>(Version.class), name);
        } catch (EmptyResultDataAccessException e) {
            SimpleJdbcInsert p = new SimpleJdbcInsert(jdbcTemplate);
            p.withTableName(TableUtilities.getTableName(Version.class));
            p.usingGeneratedKeyColumns(Version.ID);
            Map<String, Object> pf = new HashMap<String, Object>();
            pf.put(Version.NAME, name);
            Long versionId = p.executeAndReturnKey(pf).longValue();
            version = new Version();
            version.setId(versionId);
            version.setName(name);
        }
        return version;
    }

}
