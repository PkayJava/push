package com.itrustcambodia.push.page;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import com.googlecode.wickedcharts.highcharts.options.Axis;
import com.googlecode.wickedcharts.highcharts.options.ChartOptions;
import com.googlecode.wickedcharts.highcharts.options.Cursor;
import com.googlecode.wickedcharts.highcharts.options.DataLabels;
import com.googlecode.wickedcharts.highcharts.options.HorizontalAlignment;
import com.googlecode.wickedcharts.highcharts.options.Legend;
import com.googlecode.wickedcharts.highcharts.options.LegendLayout;
import com.googlecode.wickedcharts.highcharts.options.Options;
import com.googlecode.wickedcharts.highcharts.options.PlotLine;
import com.googlecode.wickedcharts.highcharts.options.PlotOptions;
import com.googlecode.wickedcharts.highcharts.options.PlotOptionsChoice;
import com.googlecode.wickedcharts.highcharts.options.SeriesType;
import com.googlecode.wickedcharts.highcharts.options.Title;
import com.googlecode.wickedcharts.highcharts.options.Tooltip;
import com.googlecode.wickedcharts.highcharts.options.VerticalAlignment;
import com.googlecode.wickedcharts.highcharts.options.color.HexColor;
import com.googlecode.wickedcharts.highcharts.options.color.NullColor;
import com.googlecode.wickedcharts.highcharts.options.functions.PercentageFormatter;
import com.googlecode.wickedcharts.highcharts.options.series.Point;
import com.googlecode.wickedcharts.highcharts.options.series.PointSeries;
import com.googlecode.wickedcharts.highcharts.options.series.Series;
import com.googlecode.wickedcharts.highcharts.options.series.SimpleSeries;
import com.googlecode.wickedcharts.wicket6.highcharts.Chart;
import com.itrustcambodia.pluggable.core.AbstractWebApplication;
import com.itrustcambodia.pluggable.core.Menu;
import com.itrustcambodia.pluggable.core.Mount;
import com.itrustcambodia.pluggable.core.WebSession;
import com.itrustcambodia.pluggable.database.EntityRowMapper;
import com.itrustcambodia.pluggable.layout.AbstractLayout;
import com.itrustcambodia.pluggable.page.WebPage;
import com.itrustcambodia.pluggable.utilities.TableUtilities;
import com.itrustcambodia.pluggable.wicket.authroles.Role;
import com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.Roles;
import com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import com.itrustcambodia.push.entity.Application;
import com.itrustcambodia.push.entity.Country;
import com.itrustcambodia.push.entity.Device;
import com.itrustcambodia.push.entity.Manufacture;
import com.itrustcambodia.push.entity.User;
import com.itrustcambodia.push.entity.Version;

@Mount("/d")
@AuthorizeInstantiation(roles = { @Role(name = "ROLE_PAGE_DASHBOARD", description = "Access Dashboard Page") })
public class DashboardPage extends WebPage {

    /**
     * 
     */
    private static final long serialVersionUID = -4201966046136244462L;

    public DashboardPage() {
        initializeInterceptor();
    }

    private void initializeInterceptor() {
        AbstractLayout layout = requestLayout("layout");
        add(layout);
        reportDeviceByVersion(layout);
        reportDeviceByManufacture(layout);
        reportDeviceByAppByCountry(layout);
    }

    private void reportDeviceByAppByCountry(AbstractLayout layout) {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();

        Options options = new Options();

        ChartOptions chartOptions = new ChartOptions();
        chartOptions.setType(SeriesType.LINE);
        chartOptions.setMarginRight(130);
        chartOptions.setMarginBottom(25);
        chartOptions.setBorderWidth(1);
        options.setChartOptions(chartOptions);

        Title title = new Title("Usage by Country");

        options.setTitle(title);

        PlotLine plotLines = new PlotLine();
        plotLines.setValue(0f);
        plotLines.setWidth(1);
        plotLines.setColor(new HexColor("#999999"));

        Axis yAxis = new Axis();
        yAxis.setTitle(new Title("Devices"));
        yAxis.setPlotLines(Collections.singletonList(plotLines));
        options.setyAxis(yAxis);

        Legend legend = new Legend();
        legend.setLayout(LegendLayout.VERTICAL);
        legend.setAlign(HorizontalAlignment.RIGHT);
        legend.setVerticalAlign(VerticalAlignment.TOP);
        legend.setX(-10);
        legend.setY(100);
        legend.setBorderWidth(0);
        options.setLegend(legend);

        List<String> countries = jdbcTemplate.queryForList("select " + Country.NAME + " from " + TableUtilities.getTableName(Country.class), String.class);
        Axis xAxis = new Axis();
        xAxis.setCategories(countries);
        options.setxAxis(xAxis);

        WebSession session = (WebSession) getSession();

        User user = jdbcTemplate.queryForObject("select * from " + TableUtilities.getTableName(User.class) + " where " + User.LOGIN + " = ?", new EntityRowMapper<User>(User.class), session.getUsername());

        List<Application> applications = jdbcTemplate.query("select * from " + TableUtilities.getTableName(Application.class) + " where " + Application.USER_ID + " = ?", new EntityRowMapper<Application>(Application.class), user.getId());
        Map<String, List<Number>> pipi = new HashMap<String, List<Number>>();
        for (Application app : applications) {
            long count = jdbcTemplate.queryForObject("select count(*) from " + TableUtilities.getTableName(Device.class) + " where " + Device.APPLICATION_ID + " = ? and " + Device.FLAG + " = ?", Long.class, app.getId(), Device.Flag.ACTIVE);
            if (count > 0) {
                if (pipi.get(app.getName()) == null) {
                    pipi.put(app.getName(), new ArrayList<Number>());
                }
                List<Number> pi = pipi.get(app.getName());
                for (String country : countries) {
                    Long countryId = jdbcTemplate.queryForObject("select " + Country.ID + " from " + TableUtilities.getTableName(Country.class) + " where " + Country.NAME + " = ?", Long.class, country);
                    Long countryCount = jdbcTemplate.queryForObject("select count(*) from " + TableUtilities.getTableName(Device.class) + " where " + Device.APPLICATION_ID + " = ? and " + Device.FLAG + " = ? and " + Country.ID + " = ?", Long.class, app.getId(), Device.Flag.ACTIVE, countryId);
                    pi.add(countryCount);
                }
            }
        }

        for (Entry<String, List<Number>> pi : pipi.entrySet()) {
            Series<Number> series = new SimpleSeries();
            series.setName(pi.getKey());
            series.setData(pi.getValue());
            options.addSeries(series);
        }

        Chart chart = new Chart("reportDeviceByAppByCountry", options);
        layout.add(chart);
    }

    private void reportDeviceByManufacture(AbstractLayout layout) {

        Options options = new Options();

        ChartOptions chartOptions = new ChartOptions();
        chartOptions.setPlotBackgroundColor(new NullColor());
        chartOptions.setPlotShadow(Boolean.FALSE);
        chartOptions.setBorderWidth(1);

        options.setChartOptions(chartOptions);

        AbstractWebApplication application = (AbstractWebApplication) getApplication();

        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();

        WebSession session = (WebSession) getSession();
        User user = jdbcTemplate.queryForObject("select * from " + TableUtilities.getTableName(User.class) + " where " + User.LOGIN + " = ?", new EntityRowMapper<User>(User.class), session.getUsername());

        options.setTitle(new Title("Device (" + jdbcTemplate.queryForObject("select count(*) from " + TableUtilities.getTableName(Device.class) + " device inner join " + TableUtilities.getTableName(Application.class) + " application on device." + Device.APPLICATION_ID + " = application." + Application.ID + " where device." + Device.FLAG + " = ? and application." + Application.USER_ID + " = ?", Long.class, Device.Flag.ACTIVE, user.getId()) + ")"));

        options.setTooltip(new Tooltip().setFormatter(new PercentageFormatter()).setPercentageDecimals(1));

        options.setPlotOptions(new PlotOptionsChoice().setPie(new PlotOptions().setAllowPointSelect(Boolean.TRUE).setCursor(Cursor.POINTER).setDataLabels(new DataLabels().setEnabled(Boolean.TRUE).setColor(new HexColor("#000000")).setConnectorColor(new HexColor("#000000")).setFormatter(new PercentageFormatter()))));
        PointSeries pointSeries = new PointSeries();
        pointSeries.setType(SeriesType.PIE);
        pointSeries.setName("Mobile Device");

        List<Manufacture> manufactures = jdbcTemplate.query("select * from " + TableUtilities.getTableName(Manufacture.class), new EntityRowMapper<Manufacture>(Manufacture.class));
        for (Manufacture manufacture : manufactures) {
            Long count = jdbcTemplate.queryForObject("select count(*) from " + TableUtilities.getTableName(Device.class) + " device inner join " + TableUtilities.getTableName(Application.class) + " application on device." + Device.APPLICATION_ID + " = application." + Application.ID + " where device." + Device.FLAG + " = ? and device." + Device.MANUFACTURE_ID + " = ? and application." + Application.USER_ID + " = ?", Long.class, Device.Flag.ACTIVE, manufacture.getId(), user.getId());
            if (count > 0) {
                Point point = new Point(manufacture.getName() + " (" + count + ")", count);
                pointSeries.addPoint(point);
            }
        }
        options.addSeries(pointSeries);

        Chart chart = new Chart("reportDeviceByManufacture", options);
        layout.add(chart);
    }

    private void reportDeviceByVersion(AbstractLayout layout) {
        Options options = new Options();

        ChartOptions chartOptions = new ChartOptions();
        chartOptions.setPlotBackgroundColor(new NullColor());
        chartOptions.setPlotShadow(Boolean.FALSE);
        chartOptions.setBorderWidth(1);

        options.setChartOptions(chartOptions);

        AbstractWebApplication application = (AbstractWebApplication) getApplication();

        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();

        WebSession session = (WebSession) getSession();
        User user = jdbcTemplate.queryForObject("select * from " + TableUtilities.getTableName(User.class) + " where " + User.LOGIN + " = ?", new EntityRowMapper<User>(User.class), session.getUsername());

        options.setTitle(new Title("Device (" + jdbcTemplate.queryForObject("select count(*) from " + TableUtilities.getTableName(Device.class) + " device inner join " + TableUtilities.getTableName(Application.class) + " application on device." + Device.APPLICATION_ID + " = application." + Application.ID + " where device." + Device.FLAG + " = ? and application." + Application.USER_ID + " = ?", Long.class, Device.Flag.ACTIVE, user.getId()) + ")"));

        options.setTooltip(new Tooltip().setFormatter(new PercentageFormatter()).setPercentageDecimals(1));

        options.setPlotOptions(new PlotOptionsChoice().setPie(new PlotOptions().setAllowPointSelect(Boolean.TRUE).setCursor(Cursor.POINTER).setDataLabels(new DataLabels().setEnabled(Boolean.TRUE).setColor(new HexColor("#000000")).setConnectorColor(new HexColor("#000000")).setFormatter(new PercentageFormatter()))));
        PointSeries pointSeries = new PointSeries();
        pointSeries.setType(SeriesType.PIE);
        pointSeries.setName("Mobile Device");

        List<Version> versions = jdbcTemplate.query("select * from " + TableUtilities.getTableName(Version.class), new EntityRowMapper<Version>(Version.class));
        Map<String, List<String>> mappers = new HashMap<String, List<String>>();
        for (Version version : versions) {
            String key = version.getName();
            if (Version.VERSION.containsKey(version.getName())) {
                key = Version.VERSION.get(version.getName());
            }
            if (!mappers.containsKey(key)) {
                mappers.put(key, new ArrayList<String>());
            }
            List<String> mapper = mappers.get(key);
            mapper.add(String.valueOf(version.getId()));
        }

        for (Entry<String, List<String>> mapper : mappers.entrySet()) {
            Long count = jdbcTemplate.queryForObject("select count(*) from " + TableUtilities.getTableName(Device.class) + " device inner join " + TableUtilities.getTableName(Application.class) + " application on device." + Device.APPLICATION_ID + " = application." + Application.ID + " where device." + Device.FLAG + " = ? and device." + Device.VERSION_ID + " in (" + StringUtils.join(mapper.getValue(), ",") + ") and application." + Application.USER_ID + " = ?", Long.class, Device.Flag.ACTIVE, user.getId());
            if (count > 0) {
                Point point = new Point(mapper.getKey() + " (" + count + ")", count);
                pointSeries.addPoint(point);
            }
        }
        options.addSeries(pointSeries);

        Chart chart = new Chart("reportDeviceByVersion", options);
        layout.add(chart);
    }

    @Override
    public String getPageTitle() {
        return "Dashboard";
    }

    @Override
    public List<Menu> getPageMenus(Roles roles) {
        return null;
    }

}
