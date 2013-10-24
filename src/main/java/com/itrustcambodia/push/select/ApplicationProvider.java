package com.itrustcambodia.push.select;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.Application;
import org.apache.wicket.Session;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.itrustcambodia.pluggable.core.AbstractWebApplication;
import com.itrustcambodia.pluggable.core.WebSession;
import com.itrustcambodia.pluggable.database.EntityRowMapper;
import com.itrustcambodia.pluggable.utilities.TableUtilities;
import com.itrustcambodia.push.entity.User;
import com.vaynberg.wicket.select2.Response;
import com.vaynberg.wicket.select2.TextChoiceProvider;

public class ApplicationProvider extends TextChoiceProvider<com.itrustcambodia.push.entity.Application> {

    /**
     * 
     */
    private static final long serialVersionUID = 1193280767531341031L;

    public ApplicationProvider() {
    }

    @Override
    protected String getDisplayText(com.itrustcambodia.push.entity.Application choice) {
        return choice.getName();
    }

    @Override
    protected Object getId(com.itrustcambodia.push.entity.Application choice) {
        return choice.getId();
    }

    @Override
    public void query(String term, int page, Response<com.itrustcambodia.push.entity.Application> response) {
        JdbcTemplate jdbcTemplate = ((AbstractWebApplication) Application.get()).getJdbcTemplate();
        WebSession session = (WebSession) Session.get();
        User user = jdbcTemplate.queryForObject("select * from " + TableUtilities.getTableName(User.class) + " where " + User.LOGIN + " = ?", new EntityRowMapper<User>(User.class), session.getUsername());
        List<com.itrustcambodia.push.entity.Application> choices = jdbcTemplate.query("select * from " + TableUtilities.getTableName(com.itrustcambodia.push.entity.Application.class) + " where " + com.itrustcambodia.push.entity.Application.NAME + " like ? and " + com.itrustcambodia.push.entity.Application.USER_ID + " = ?", new EntityRowMapper<com.itrustcambodia.push.entity.Application>(com.itrustcambodia.push.entity.Application.class), term + "%", user.getId());
        response.addAll(choices);
    }

    @Override
    public Collection<com.itrustcambodia.push.entity.Application> toChoices(Collection<String> ids) {
        JdbcTemplate jdbcTemplate = ((AbstractWebApplication) Application.get()).getJdbcTemplate();

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(com.itrustcambodia.push.entity.Application.ID, ids);

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        List<com.itrustcambodia.push.entity.Application> choices = namedParameterJdbcTemplate.query("select * from " + TableUtilities.getTableName(com.itrustcambodia.push.entity.Application.class) + " where " + com.itrustcambodia.push.entity.Application.ID + " in (:" + com.itrustcambodia.push.entity.Application.ID + ")", params, new EntityRowMapper<com.itrustcambodia.push.entity.Application>(com.itrustcambodia.push.entity.Application.class));

        return choices;
    }
}