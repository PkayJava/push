package com.itrustcambodia.push.rest.v1;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.jdbc.core.JdbcTemplate;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.itrustcambodia.pluggable.core.AbstractWebApplication;
import com.itrustcambodia.pluggable.doc.ApiMethod;
import com.itrustcambodia.pluggable.rest.Controller;
import com.itrustcambodia.pluggable.rest.RequestMapping;
import com.itrustcambodia.pluggable.rest.RequestMethod;
import com.itrustcambodia.pluggable.rest.Result;
import com.itrustcambodia.pluggable.utilities.TableUtilities;
import com.itrustcambodia.pluggable.utilities.UserUtilities;
import com.itrustcambodia.pluggable.wicket.authroles.Role;
import com.itrustcambodia.pluggable.wicket.authroles.Secured;
import com.itrustcambodia.push.entity.Application;
import com.itrustcambodia.push.entity.City;
import com.itrustcambodia.push.entity.Country;
import com.itrustcambodia.push.entity.Manufacture;
import com.itrustcambodia.push.entity.Model;
import com.itrustcambodia.push.entity.Platform;
import com.itrustcambodia.push.entity.Version;

@Controller
public class ReferenceRestAPI {

    @ApiMethod(description = "get application", responseObject = Map[].class, responseDescription = "response content is empty")
    @Secured(roles = { @Role(name = "ROLE_REST_REFERENCE_APPLICATION", description = "Access Reference Application Rest") })
    @RequestMapping(value = "/api/v1/reference/application", method = RequestMethod.GET)
    public Result application(AbstractWebApplication application, HttpServletRequest request, HttpServletResponse response) throws JsonIOException, IOException {

        JdbcTemplate jdbcTemplate = application.getBean(JdbcTemplate.class);
        Gson gson = application.getGson();

        long userId = UserUtilities.findUserId(jdbcTemplate, request.getUserPrincipal().getName());

        List<Map<String, Object>> applications = jdbcTemplate.queryForList("select " + Application.ID + " id," + Application.NAME + " from " + TableUtilities.getTableName(Application.class) + " where " + Application.USER_ID + " = ?", userId);

        gson.toJson(applications, response.getWriter());

        return Result.ok("application/json");
    }

    @ApiMethod(description = "get country", responseObject = Map[].class, responseDescription = "response content is empty")
    @Secured(roles = { @Role(name = "ROLE_REST_REFERENCE_COUNTRY", description = "Access Reference Country Rest") })
    @RequestMapping(value = "/api/v1/reference/country", method = RequestMethod.GET)
    public Result country(AbstractWebApplication application, HttpServletRequest request, HttpServletResponse response) throws JsonIOException, IOException {

        JdbcTemplate jdbcTemplate = application.getBean(JdbcTemplate.class);
        Gson gson = application.getGson();

        List<Map<String, Object>> countries = jdbcTemplate.queryForList("select " + Country.ID + " id," + Country.NAME + " from " + TableUtilities.getTableName(Country.class));

        gson.toJson(countries, response.getWriter());

        return Result.ok("application/json");
    }

    @ApiMethod(description = "get city", responseObject = Map[].class, responseDescription = "response content is empty")
    @Secured(roles = { @Role(name = "ROLE_REST_REFERENCE_CITY", description = "Access Reference City Rest") })
    @RequestMapping(value = "/api/v1/reference/city", method = RequestMethod.GET)
    public Result city(AbstractWebApplication application, HttpServletRequest request, HttpServletResponse response) throws JsonIOException, IOException {

        JdbcTemplate jdbcTemplate = application.getBean(JdbcTemplate.class);
        Gson gson = application.getGson();

        List<Map<String, Object>> cities = jdbcTemplate.queryForList("select " + City.ID + " id," + City.NAME + " from " + TableUtilities.getTableName(City.class));

        gson.toJson(cities, response.getWriter());

        return Result.ok("application/json");
    }

    @ApiMethod(description = "get platform", responseObject = Map[].class, responseDescription = "response content is empty")
    @Secured(roles = { @Role(name = "ROLE_REST_REFERENCE_PLATFORM", description = "Access Reference Platform Rest") })
    @RequestMapping(value = "/api/v1/reference/platform", method = RequestMethod.GET)
    public Result platform(AbstractWebApplication application, HttpServletRequest request, HttpServletResponse response) throws JsonIOException, IOException {

        JdbcTemplate jdbcTemplate = application.getBean(JdbcTemplate.class);
        Gson gson = application.getGson();

        List<Map<String, Object>> platforms = jdbcTemplate.queryForList("select " + Platform.ID + " id," + Platform.NAME + " from " + TableUtilities.getTableName(Platform.class));

        gson.toJson(platforms, response.getWriter());

        return Result.ok("application/json");
    }

    @ApiMethod(description = "get manufacture", responseObject = Map[].class, responseDescription = "response content is empty")
    @Secured(roles = { @Role(name = "ROLE_REST_REFERENCE_MANUFACTURE", description = "Access Reference Manufacture Rest") })
    @RequestMapping(value = "/api/v1/reference/manufacture", method = RequestMethod.GET)
    public Result manufacture(AbstractWebApplication application, HttpServletRequest request, HttpServletResponse response) throws JsonIOException, IOException {

        JdbcTemplate jdbcTemplate = application.getBean(JdbcTemplate.class);
        Gson gson = application.getGson();

        List<Map<String, Object>> manufactures = jdbcTemplate.queryForList("select " + Manufacture.ID + " id," + Manufacture.NAME + " from " + TableUtilities.getTableName(Manufacture.class));

        gson.toJson(manufactures, response.getWriter());

        return Result.ok("application/json");
    }

    @ApiMethod(description = "get model", responseObject = Map[].class, responseDescription = "response content is empty")
    @Secured(roles = { @Role(name = "ROLE_REST_REFERENCE_MODEL", description = "Access Reference Model Rest") })
    @RequestMapping(value = "/api/v1/reference/model", method = RequestMethod.GET)
    public Result model(AbstractWebApplication application, HttpServletRequest request, HttpServletResponse response) throws JsonIOException, IOException {

        JdbcTemplate jdbcTemplate = application.getBean(JdbcTemplate.class);
        Gson gson = application.getGson();

        List<Map<String, Object>> models = jdbcTemplate.queryForList("select " + Model.ID + " id," + Model.NAME + " from " + TableUtilities.getTableName(Model.class));

        gson.toJson(models, response.getWriter());

        return Result.ok("application/json");
    }

    @ApiMethod(description = "get version", responseObject = Map[].class, responseDescription = "response content is empty")
    @Secured(roles = { @Role(name = "ROLE_REST_REFERENCE_VERSION", description = "Access Reference Version Rest") })
    @RequestMapping(value = "/api/v1/reference/version", method = RequestMethod.GET)
    public Result version(AbstractWebApplication application, HttpServletRequest request, HttpServletResponse response) throws JsonIOException, IOException {

        JdbcTemplate jdbcTemplate = application.getBean(JdbcTemplate.class);
        Gson gson = application.getGson();

        List<Map<String, Object>> versions = jdbcTemplate.queryForList("select " + Version.ID + " id," + Version.NAME + " from " + TableUtilities.getTableName(Version.class));

        gson.toJson(versions, response.getWriter());

        return Result.ok("application/json");
    }
}