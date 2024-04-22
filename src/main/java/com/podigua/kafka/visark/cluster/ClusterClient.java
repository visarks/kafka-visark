package com.podigua.kafka.visark.cluster;

import com.podigua.kafka.core.utils.DatasourceUtils;
import com.podigua.kafka.core.utils.UUIDUtils;
import com.podigua.kafka.visark.cluster.entity.ClusterProperty;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 *
 **/
public class ClusterClient {
    private static final String INSERT = "insert into cluster (id,name, servers,security,protocal,mechanism,username,password) values ('%s','%s', '%s',%b,'%s','%s','%s','%s')";
    private static final String UPDATE = "update cluster set name = '%s', servers = '%s',security=%b,protocal='%s',mechanism='%s',username='%s',password='%s' where id = '%s'";

    private static final String DELETE = "delete from cluster where id = '%s'";

    private static final String SELECT = "select * from cluster order by name";


    public static List<ClusterProperty> query4List() {
        return DatasourceUtils.query4List(SELECT, ClusterProperty.class);
    }

    public static void save(ClusterProperty cluster) {
        if (StringUtils.hasText(cluster.getId())) {
            DatasourceUtils.execute(String.format(UPDATE,  cluster.getName(), cluster.getServers(),cluster.getSecurity(),cluster.getProtocal().name(),cluster.getMechanism().name(),cluster.getUsername(),cluster.getPassword(), cluster.getId()));
        } else {
            cluster.setId(UUIDUtils.uuid());
            DatasourceUtils.execute(String.format(INSERT, cluster.getId(), cluster.getName(), cluster.getServers(),cluster.getSecurity(),cluster.getProtocal().name(),cluster.getMechanism().name(),cluster.getUsername(),cluster.getPassword()));
        }
    }

    public static void deleteById(String id) {
        DatasourceUtils.execute(String.format(DELETE, id));
    }
}
