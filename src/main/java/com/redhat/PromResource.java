package com.redhat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/api")
public class PromResource {

    @Inject
    @RestClient
    PromService promService;

    @GET
    @Path("/json")
    @Produces(MediaType.APPLICATION_JSON)
    public PromResponse json(@QueryParam("query") String query, @QueryParam("start") String start,
            @QueryParam("end") String end, @QueryParam("step") String step) {
        PromResponse r = promService.queryRange(query, start, end, step);
        System.out.println("Status : " + r.status);
        return r;
    }

    private void concateMetric(Appendable sb, Result res) throws IOException {
        if (res.metric.node != null && res.metric.node.trim().length() > 0) {
            sb.append(res.metric.node);
            sb.append(",");
        }
        if (res.metric.namespace != null && res.metric.namespace.trim().length() > 0) {
            sb.append(res.metric.namespace);
            sb.append(",");
        }
        if (res.metric.pod != null && res.metric.pod.trim().length() > 0) {
            sb.append(res.metric.pod);
            sb.append(",");
        }
    }

    private void parser(Appendable sb, PromResponse r) {
        try {

            if (r != null && r.data != null && r.data.result != null) {
                int count = r.data.result.size();
                System.out.println("Found " + count + " keys!");
                if (r.data.result.size() > 0) {
                    for (int i = 0; i < count; i++) {
                        Result res = r.data.result.get(i);
                        StringBuffer metricSb = new StringBuffer();
                        concateMetric(metricSb, res);
                        sb.append(metricSb);

                        if (res.value != null && !res.value.isEmpty()) {
                            sb.append(res.value.get(0).toString());
                            sb.append(",");
                            sb.append(res.value.get(1).toString());
                            sb.append("\n");
                        } else if (res.values != null && !res.values.isEmpty()) {
                            int matrixCount = res.values.size();
                            for (int j = 0; j < matrixCount; j++) {
                                if (j > 0) {
                                    sb.append(metricSb);
                                }
                                List matrix = (List) res.values.get(j);
                                BigDecimal b = (BigDecimal) matrix.get(0);
                                Long l = Long.parseLong(b.multiply(new BigDecimal(1000)).setScale(0).toString());
                                Timestamp t = new Timestamp(l);
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

                                sb.append(sdf.format(t));
                                sb.append(",");
                                sb.append(matrix.get(1).toString());
                                sb.append("\n");
                            }
                        }

                    }
                }
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    @GET
    @Path("/csvString")
    @Produces(MediaType.TEXT_PLAIN)
    public String csvString(@QueryParam("query") String query, @QueryParam("start") String start,
            @QueryParam("startStr") String startStr, @QueryParam("end") String end,
            @QueryParam("step") @DefaultValue("86400") String step) {
        System.out.println("query : " + query);
        System.out.println("start : " + start);
        System.out.println("end : " + end);
        System.out.println("step : " + step);

        StringBuffer sb = new StringBuffer();
        
        PromResponse r = promService.queryRange(query, start, end, step);
        System.out.println("Status : " + r.status);
        
        parser(sb, r);

        return sb.toString();
    }

    @GET
    @Path("/csvFile")
    @Produces(MediaType.TEXT_PLAIN)
    public PromResponse csvFile(@QueryParam("query") String query, @QueryParam("start") String start,
            @QueryParam("end") String end, @QueryParam("step") @DefaultValue("86400") String step) {
        PromResponse r = promService.queryRange(query, start, end, step);
        System.out.println("Status : " + r.status);
        try {
            File f = new File("/tmp/output.csv");
            FileWriter fw = new FileWriter(f);
            BufferedWriter bw = new BufferedWriter(fw);
            parser(bw, r);
            bw.flush();
            bw.close();
        } catch (Exception err) {
            err.printStackTrace();
        }

        return r;
    }
}