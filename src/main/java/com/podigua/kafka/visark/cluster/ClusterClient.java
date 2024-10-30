package com.podigua.kafka.visark.cluster;

import com.podigua.kafka.core.utils.DatasourceUtils;
import com.podigua.kafka.core.utils.UUIDUtils;
import com.podigua.kafka.visark.cluster.entity.ClusterProperty;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 *
 **/
public class ClusterClient {
    private static final String INSERT = "insert into cluster (id,name, servers,security,protocal,mechanism,username,password,type,parentId) values ('%s','%s', '%s',%b,'%s','%s','%s','%s','%s','%s')";
    private static final String UPDATE = "update cluster set name = '%s', servers = '%s',security=%b,protocal='%s',mechanism='%s',username='%s',password='%s',type='%s',parentId='%s' where id = '%s'";

    private static final String DELETE = "delete from cluster where id = '%s'";

    private static final String SELECT = "select * from cluster order by name";
    private static final String SELECT_CHILDREN = "select * from cluster where parentId='%s'";


    public static List<ClusterProperty> query4List() {
        return DatasourceUtils.query4List(SELECT, ClusterProperty.class);
    }

    public static void save(ClusterProperty cluster) {
        if (StringUtils.hasText(cluster.getId())) {
            DatasourceUtils.execute(String.format(UPDATE,  cluster.getName(), cluster.getServers(),cluster.getSecurity(),cluster.getProtocal().name(),cluster.getMechanism().name(),cluster.getUsername(),cluster.getPassword(),
                    cluster.getType(),
                    cluster.getParentId(),
                    cluster.getId()));
        } else {
            cluster.setId(UUIDUtils.uuid());
            DatasourceUtils.execute(String.format(INSERT, cluster.getId(), cluster.getName(), cluster.getServers(),cluster.getSecurity(),cluster.getProtocal().name(),cluster.getMechanism().name(),cluster.getUsername(),cluster.getPassword(),cluster.getType(),
                    cluster.getParentId()));
        }
    }

    public static void deleteById(String id) {
        List<ClusterProperty> children = DatasourceUtils.query4List(String.format(SELECT_CHILDREN, id), ClusterProperty.class);
        if(!CollectionUtils.isEmpty(children)){
            for (ClusterProperty child : children) {
                deleteById(child.getId());
            }
        }
        DatasourceUtils.execute(String.format(DELETE, id));
    }
}
