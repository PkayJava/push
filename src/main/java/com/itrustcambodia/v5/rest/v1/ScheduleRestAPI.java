package com.itrustcambodia.v5.rest.v1;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.ServletRequestUtils;

import com.itrustcambodia.pluggable.core.AbstractWebApplication;
import com.itrustcambodia.pluggable.database.EntityRowMapper;
import com.itrustcambodia.pluggable.doc.ApiMethod;
import com.itrustcambodia.pluggable.doc.ApiParam;
import com.itrustcambodia.pluggable.rest.Controller;
import com.itrustcambodia.pluggable.rest.RequestMapping;
import com.itrustcambodia.pluggable.rest.RequestMethod;
import com.itrustcambodia.pluggable.rest.Result;
import com.itrustcambodia.pluggable.utilities.TableUtilities;
import com.itrustcambodia.pluggable.wicket.authroles.Role;
import com.itrustcambodia.pluggable.wicket.authroles.Secured;
import com.itrustcambodia.v5.PushUtils;
import com.itrustcambodia.v5.entity.Application;
import com.itrustcambodia.v5.entity.User;

@Controller
public class ScheduleRestAPI {

    private static final String WHEN_FORMAT = "yyyy-MM-dd HH:mm:ss ZZ";

    @ApiMethod(description = "schedule a push, applications, countries, cities, platforms, manufactures, models, versions, you have to specific at least a param", requestParameters = { @ApiParam(name = "applications", type = Long[].class, description = "application id"), @ApiParam(name = "countries", type = Long[].class, description = "country id"), @ApiParam(name = "cities", type = Long[].class, description = "city id"), @ApiParam(name = "platforms", type = Long[].class, description = "platform id"), @ApiParam(name = "manufactures", type = Long[].class, description = "manufacture id"), @ApiParam(name = "models", type = Long[].class, description = "model id"), @ApiParam(name = "versions", type = Long[].class, description = "version id"),
            @ApiParam(name = "message", type = String.class, description = "message", required = true), @ApiParam(name = "when", type = String.class, description = "when, null mean now", format = WHEN_FORMAT) }, responseObject = Void.class, responseDescription = "response content is empty")
    @Secured(roles = { @Role(name = "ROLE_REST_SCHEDULE", description = "Access Schedule Rest") })
    @RequestMapping(value = "/api/v1/schedule", method = RequestMethod.POST)
    public Result schedule(AbstractWebApplication application, HttpServletRequest request, HttpServletResponse response) {

        JdbcTemplate jdbcTemplate = application.getBean(JdbcTemplate.class);

        long[] applications = ServletRequestUtils.getLongParameters(request, "applications");

        long[] countries = ServletRequestUtils.getLongParameters(request, "countries");

        long[] cities = ServletRequestUtils.getLongParameters(request, "cities");

        long[] platforms = ServletRequestUtils.getLongParameters(request, "platforms");

        long[] manufactures = ServletRequestUtils.getLongParameters(request, "manufactures");

        long[] models = ServletRequestUtils.getLongParameters(request, "models");

        long[] versions = ServletRequestUtils.getLongParameters(request, "versions");

        String message = ServletRequestUtils.getStringParameter(request, "message", "");

        String when = ServletRequestUtils.getStringParameter(request, "when", "");

        Date schedule = new Date();
        if (!"".equals(when)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(WHEN_FORMAT);
            try {
                schedule = dateFormat.parse(when);
            } catch (ParseException e) {
                return Result.badRequest("application/json");
            }
        }

        if ((applications == null || applications.length == 0) && (countries == null || countries.length == 0) && (cities == null || cities.length == 0) && (platforms == null || platforms.length == 0) && (manufactures == null || manufactures.length == 0) && (models == null || models.length == 0) && (versions == null || versions.length == 0)) {
            return Result.badRequest("application/json");
        }

        String login = request.getUserPrincipal().getName();
        User user = jdbcTemplate.queryForObject("select * from " + TableUtilities.getTableName(User.class) + " where " + User.LOGIN + " = ?", new EntityRowMapper<User>(User.class), login);

        List<Long> listApplication = null;
        if (applications == null || applications.length == 0) {
            listApplication = jdbcTemplate.queryForList("select " + Application.ID + " from " + TableUtilities.getTableName(Application.class) + " where " + Application.USER_ID + " = ?", Long.class, user.getId());
        } else {
            listApplication = new ArrayList<Long>();
            for (long app : applications) {
                Long count = jdbcTemplate.queryForObject("select count(*) from " + TableUtilities.getTableName(Application.class) + " where " + Application.USER_ID + " = ? and " + Application.ID + " = ?", Long.class, user.getId(), app);
                if (count <= 0) {
                    return Result.badRequest("application/json");
                }
                listApplication.add(app);
            }
        }

        List<Long> listCountry = null;
        if (countries != null && countries.length > 0) {
            listCountry = new ArrayList<Long>();
            for (Long country : countries) {
                listCountry.add(country);
            }
        }

        List<Long> listCity = null;
        if (cities != null && cities.length > 0) {
            listCity = new ArrayList<Long>();
            for (Long city : cities) {
                listCity.add(city);
            }
        }

        List<Long> listPlatform = null;
        if (platforms != null && platforms.length > 0) {
            listPlatform = new ArrayList<Long>();
            for (Long platform : platforms) {
                listPlatform.add(platform);
            }
        }

        List<Long> listManufacture = null;
        if (manufactures != null && manufactures.length > 0) {
            listManufacture = new ArrayList<Long>();
            for (Long manufacture : manufactures) {
                listManufacture.add(manufacture);
            }
        }

        List<Long> listModel = null;
        if (models != null && models.length > 0) {
            listModel = new ArrayList<Long>();
            for (Long model : models) {
                listModel.add(model);
            }
        }

        List<Long> listVersion = null;
        if (versions != null && versions.length > 0) {
            listVersion = new ArrayList<Long>();
            for (Long version : versions) {
                listVersion.add(version);
            }
        }

        PushUtils.schedule(jdbcTemplate, user.getId(), listCountry, listCity, listApplication, listPlatform, listManufacture, listModel, listVersion, message, schedule);

        return Result.ok("application/json");
    }

}